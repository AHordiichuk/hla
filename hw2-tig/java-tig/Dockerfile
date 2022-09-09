FROM arm64v8/amazoncorretto:17-al2-jdk

ADD ./ ./

EXPOSE 8081
EXPOSE 8082

ENTRYPOINT [ "./mvnw", "spring-boot:run" ]