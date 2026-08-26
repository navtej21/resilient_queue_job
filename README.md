# resilient-job-queue

A minimal async job queue demonstrating **at-least-once delivery**, **idempotent processing**, and **crash recovery** using Redis Streams — with a Spring Boot producer and a Python consumer.

## Problem

When a slow or unreliable operation shouldn't block the caller, the standard fix is to publish a message and process it asynchronously. But that decoupling introduces a real problem: what happens if the consumer crashes *while* processing a message? Redis Streams' consumer groups guarantee **at-least-once delivery** — meaning a crashed, unacknowledged message *will* be redelivered — but that also means your consumer must be able to handle processing the same message twice without corrupting state or double-running side effects.

This project builds that end-to-end, then deliberately crashes the consumer mid-processing to prove the recovery actually works — not just claims to.

## Architecture

```
┌─────────────┐      XADD       ┌───────────────┐      XREADGROUP      ┌──────────────┐
│ Spring Boot │ ───────────────▶│ Redis Stream  │◀──────────────────── │ Python       │
│ POST /jobs  │   returns 202   │   "jobs"      │      XACK             │ Consumer     │
│ immediately │   before work   │ + consumer    │──────────────────────▶│ (worker-1)   │
└─────────────┘   is done       │   group PEL   │                       └──────┬───────┘
                                 └───────────────┘                              │
                                                                    SADD/SISMEMBER
                                                                    "processed_ids"
                                                                    (idempotency guard)
```

- **Producer** (Spring Boot): accepts a job request, generates a `jobId`, publishes it to the `jobs` stream, and returns immediately — the caller never waits on the actual work.
- **Consumer** (Python): reads from the `jobs` stream via a consumer group (`job-workers`), processes each message, and acknowledges it. A Redis Set (`processed_ids`) provides an idempotency guard so a redelivered message is never reprocessed.
- **Chaos script**: force-kills the consumer process on command (`SIGKILL`-equivalent), simulating a real crash mid-processing — no graceful shutdown, no cleanup.

## Running it

**1. Start Redis:**
```bash
docker-compose up -d
```

**2. Start the producer (Spring Boot):**
```bash
cd producer
./mvnw spring-boot:run
```

**3. Start the consumer (Python):**
```bash
cd consumer
python worker.py
```

**4. Publish a job:**
```bash
curl -X POST http://localhost:8080/job -H "Content-Type: application/json" -d '{"payLoad": "some work"}'
```
Response:
```json
{ "jobId": "..." }
```

**5. Simulate a crash mid-processing** (in a separate terminal, while the consumer is mid-work):
```bash
python chaos.py
```

## Proof: crash, stuck delivery, correct recovery

This is the actual sequence run against this codebase — not a hypothetical.

### Step 1 — job delivered, consumer killed mid-processing

Consumer terminal:
```
worker-1 listening on 'jobs'...
the id 24272
Received 1787727996541-0: {'jobId': 'c51764ee-e3e2-4143-be06-414c9d9c1f36', 'payLoad': 'this a new task to test now and we are going to do it now.'}
i am here now
```
*(process killed here via `chaos.py` — no further output, no ack)*

### Step 2 — confirm the message is stuck, unacknowledged

```
127.0.0.1:6379> xpending jobs job-workers - + 5
1) 1) "1787727996541-0"
   2) "worker-1"
   3) (integer) 17310
   4) (integer) 1
```
One entry pending, owned by `worker-1`, idle ~17 seconds — exactly the message it was mid-way through when it died.

### Step 3 — restart the consumer, it recovers and correctly skips reprocessing

```
Consumer group 'job-workers' already exists
Received 1787727996541-0: {'jobId': 'c51764ee-e3e2-4143-be06-414c9d9c1f36', 'payLoad': 'this a new task to test now and we are going to do it now.'}
Skipping duplicate job c51764ee-e3e2-4143-be06-414c9d9c1f36
Acked 1787727996541-0
recovered from PEL
worker-1 listening on 'jobs'...
```
The message is redelivered via `XREADGROUP ... "0"` (recover-my-own-pending-work). The idempotency guard correctly identifies this `jobId` as already processed — because `SADD` had already run *before* the crash, even though the acknowledgment never made it out — so the work is not repeated, but the message is still acknowledged, closing the loop.

### Step 4 — confirm the pending entries list is now clear

```
127.0.0.1:6379> xpending jobs job-workers - + 5
(empty array)
```

### Step 5 — confirm the consumer still processes genuinely new work

A fresh job published after recovery is picked up normally via `XREADGROUP ... ">"`, proving the recovery phase doesn't leave the consumer stuck only replaying old work.

## What this demonstrates

- **At-least-once delivery** — Redis Streams consumer groups guarantee a message is never silently dropped on consumer failure; it stays in the Pending Entries List (PEL) until explicitly acknowledged.
- **Idempotent processing with correct ordering** — the idempotency guard (`SADD` before `XACK`) ensures that if a crash happens *after* the real work completes but *before* the acknowledgment, the redelivered message is recognized and skipped rather than reprocessed — while still being cleared from the PEL.
- **Chaos-tested crash recovery** — the failure scenario wasn't reasoned about in the abstract; it was reproduced on demand with `chaos.py` (`SIGKILL`-equivalent, no graceful shutdown) and the recovery was observed directly via `XPENDING`, not assumed.
- **Delivery vs. business-state are separate concerns** — the Redis Streams PEL only tracks "was this transport-level entry acknowledged," while the `processed_ids` set tracks "did this business operation actually complete." The two must be kept in sync deliberately (via ordering), because the transport has no visibility into the application's own notion of completion.

## Notes

- The consumer performs a one-time recovery pass at startup (`XREADGROUP ... "0"`) to reclaim any of its own unacknowledged work from a previous crash, before switching to normal operation (`XREADGROUP ... ">"`) for new messages.
- Idempotency is keyed on the business `jobId` (from the message payload), not the Redis stream entry ID — the entry ID is a transport-internal detail; `jobId` is the domain identifier that remains meaningful regardless of the underlying message broker.
- This project does not yet cover horizontal scaling (multiple consumer instances) or cross-consumer takeover of abandoned work (`XCLAIM`/`XAUTOCLAIM`) — those are logical next steps, since a real production system can't assume the same consumer process will always come back.
