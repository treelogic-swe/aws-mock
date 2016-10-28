FROM frekele/gradle

WORKDIR /project

ADD . /project/
CMD ["gradle", "jettyRun"]

EXPOSE 8000
