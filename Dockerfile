
FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY TaskManager.java .
COPY StudentPortalServer.java .
COPY tasks.txt .

RUN javac --add-modules jdk.httpserver TaskManager.java StudentPortalServer.java

EXPOSE 8080

CMD ["java", "--add-modules", "jdk.httpserver", "StudentPortalServer"]
