FROM eclipse-temurin:21-jdk as build
WORKDIR /build
COPY . . 

RUN ./mvnw clean package


FROM eclipse-temurin:21-jdk
ARG PORT
WORKDIR /app
COPY --from=build /build/target .
CMD java -jar /app/bookwalker-1.0.0.jar
HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=3 \
  CMD curl -f http://localhost:${PORT}/tags || exit 1


