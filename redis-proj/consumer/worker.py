import redis
import time

r = redis.Redis(host="localhost", port=6379, decode_responses=True)

STREAM = "jobs"
GROUP = "job-workers"
CONSUMER = "worker-1"

try:
    r.xgroup_create(name=STREAM, groupname=GROUP, id="0", mkstream=True)
    print(f"Created consumer group '{GROUP}'")
except redis.exceptions.ResponseError as e:
    if "BUSYGROUP" in str(e):
        print(f"Consumer group '{GROUP}' already exists")
    else:
        raise

print(f"{CONSUMER} listening on '{STREAM}'...")


while True:
    try:
        response = r.xreadgroup(
        groupname=GROUP,
        consumername=CONSUMER,
        streams={STREAM: ">"},
        count=1,
        block=5000
    )
        if not response:
            print("skipped",f"{fields["jobId"]}")
            
        for stream_name, messages in response:
            for message_id, fields in messages:
                print(f"Received {message_id}: {fields}")
                
                if (r.sismember("processed_ids",fields["jobId"])):
                     print(f"Skipping duplicate job {fields['jobId']}")
                     continue
                else:
                    r.sadd("processed_ids",fields["jobId"])
                    time.sleep(1)  # simulate work
                    r.xack(STREAM, GROUP, message_id)
                print(f"Acked {message_id}")
    except redis.exceptions.ResponseError as e:
        print(f"error {e}")
