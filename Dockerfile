FROM gradle:8.8-jdk17
WORKDIR /app
COPY --chown=gradle:gradle . .
RUN gradle clean build -x test
CMD ["gradle", "run", "--no-daemon"]
