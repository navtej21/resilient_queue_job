import redis
import time
import os


STREAM = "jobs"
GROUP = "job-workers"
CONSUMER = "worker-1"


# --------------------------------------------------
# 1. Redis connection
# --------------------------------------------------

def create_redis_connection():
    return redis.Redis(
        host="localhost",
        port=6379,
        decode_responses=True
    )


# --------------------------------------------------
# 2. Create consumer group
# --------------------------------------------------

def create_consumer_group(r):
    try:
        r.xgroup_create(
            name=STREAM,
            groupname=GROUP,
            id="0",
            mkstream=True
        )

        print(f"Created consumer group '{GROUP}'")

    except redis.exceptions.ResponseError as e:

        if "BUSYGROUP" in str(e):
            print(f"Consumer group '{GROUP}' already exists")
        else:
            raise


# --------------------------------------------------
# 3. Check whether job was already processed
# --------------------------------------------------

def is_job_processed(r, job_id):
    return r.sismember("processed_ids", job_id)


# --------------------------------------------------
# 4. Process one message
# --------------------------------------------------

def process_message(r, message_id, fields):

    job_id = fields["jobId"]

    print(f"Received {message_id}: {fields}")

    # Check idempotency
    if is_job_processed(r, job_id):

        print(f"Skipping duplicate job {job_id}")

    else:

        print("I am here now")

        # Mark as processed
        r.sadd("processed_ids", job_id)

        # Simulate actual work
        time.sleep(1)


# --------------------------------------------------
# 5. Acknowledge message
# --------------------------------------------------

def acknowledge_message(r, message_id):

    r.xack(
        STREAM,
        GROUP,
        message_id
    )

    print(f"Acked {message_id}")


# --------------------------------------------------
# 6. Process messages returned by XREADGROUP
# --------------------------------------------------

def handle_messages(r, response):

    for stream_name, messages in response:

        for message_id, fields in messages:

            process_message(
                r,
                message_id,
                fields
            )

            acknowledge_message(
                r,
                message_id
            )


# --------------------------------------------------
# 7. Drain PEL
# --------------------------------------------------

def drain_pel(r):

    print("Draining pending messages...")

    try:

        response = r.xreadgroup(
            groupname=GROUP,
            consumername=CONSUMER,
            streams={
                STREAM: "0"
            },
            count=1,
            block=5000
        )

        if response:

            handle_messages(
                r,
                response
            )

    except redis.exceptions.ResponseError as e:

        print(f"PEL error: {e}")


# --------------------------------------------------
# 8. Write worker PID
# --------------------------------------------------

def write_worker_pid():

    pid = os.getpid()

    with open("worker.pid", "w") as file:
        file.write(str(pid))

    print("Worker PID:", pid)


# --------------------------------------------------
# 9. Continuously consume NEW messages
# --------------------------------------------------

def consume_new_messages(r):

    print(
        f"{CONSUMER} listening on '{STREAM}'..."
    )

    while True:

        try:

            response = r.xreadgroup(
                groupname=GROUP,
                consumername=CONSUMER,
                streams={
                    STREAM: ">"
                },
                count=1,
                block=5000
            )

            if not response:
                continue

            handle_messages(
                r,
                response
            )

        except redis.exceptions.ResponseError as e:

            print(f"Consumer error: {e}")


# --------------------------------------------------
# 10. Main
# --------------------------------------------------

def main():

    # Create Redis connection
    r = create_redis_connection()

    # Create consumer group
    create_consumer_group(r)

    # First recover/drain pending messages
    drain_pel(r)

    # Write worker PID
    write_worker_pid()

    # Start consuming new messages
    consume_new_messages(r)


# --------------------------------------------------
# Start program
# --------------------------------------------------

if __name__ == "__main__":
    main()