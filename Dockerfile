FROM frekele/gradle:3.4-jdk8u91

WORKDIR /project

ADD . /project/
CMD ["gradle", "jettyRun"]

EXPOSE 8000
