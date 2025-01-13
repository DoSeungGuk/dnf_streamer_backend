# 1. Base image 설정
FROM openjdk:17

# 2. 애플리케이션 JAR 파일 복사
ARG JAR_FILE=out/artifacts/df_jar/df.jar
COPY ${JAR_FILE} df.jar

# 3. 컨테이너가 사용하는 포트
EXPOSE 8080

# 4. 애플리케이션 실행 명령어
ENTRYPOINT ["java", "-jar", "/df.jar"]
