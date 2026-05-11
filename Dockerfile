FROM ubuntu:latest
LABEL authors="khadh"

ENTRYPOINT ["top", "-b"]