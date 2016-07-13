FROM java:8

RUN apt-get clean && apt-get update
RUN apt-get install -y gradle

WORKDIR /project

ADD . /project/
CMD ["gradle", "jettyRun"]

EXPOSE 8000
