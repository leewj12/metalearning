FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY app.war app.war
EXPOSE 9091
ENTRYPOINT ["java", "-Xmx256m", "-jar", "app.war", "--server.port=9091"]
