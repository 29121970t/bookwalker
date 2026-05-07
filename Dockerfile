FROM eclipse-temurin:21-jdk as build
WORKDIR /build
COPY . . 

RUN ./mvnw clean package


FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=build /build/target .
CMD java -jar /app/bookwalker-1.0.0.jar

