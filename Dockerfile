FROM java:8-alpine
MAINTAINER Your Name <you@example.com>

ADD target/uberjar/clj-text-input.jar /clj-text-input/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/clj-text-input/app.jar"]
