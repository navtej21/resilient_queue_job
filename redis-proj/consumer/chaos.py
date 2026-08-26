import subprocess

with open("worker.pid","r") as file:
    pid=int(str(file.read()))


print("killing the process",pid)
subprocess.run(["taskkill","/F","/PID",str(pid)])


