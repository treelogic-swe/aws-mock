FROM java:8

RUN apt-get update
RUN apt-get install -y gradle

WORKDIR /project

ADD . /project/
CMD ["gradle", "jettyRun"]

<<<<<<< HEAD
EXPOSE 8000
=======
EXPOSE 8000
>>>>>>> b9d99f368634a8de4e643529fba309845537a13a
