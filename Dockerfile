FROM azul/zulu-openjdk:11-latest
COPY build/libs/*-shadow.jar /app/main.jar
ENTRYPOINT /usr/bin/java -jar /app/main.jar
