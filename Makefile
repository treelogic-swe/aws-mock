default: war

war:
	./gradlew war

start:
	./gradlew integrationTomcatRun

stop:
	./gradlew integrationTomcatStop

clean:
	./gradlew clean

eclipse-init:
	./gradlew clean Eclipse eclipse
