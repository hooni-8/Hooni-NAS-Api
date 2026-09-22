FROM eclipse-temurin:17

# ffmpeg 설치
RUN apt-get update \
    && apt-get install -y ffmpeg \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

COPY build/libs/*.jar /home/app.jar
ENTRYPOINT ["java","-jar","/home/app.jar"]