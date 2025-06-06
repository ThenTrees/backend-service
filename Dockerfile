# Stage 1: Build JAR từ source code
FROM maven:3.9.5-eclipse-temurin-17 AS build

WORKDIR /app

#copy src code vao container
COPY . .

# Build ứng dụng (bỏ qua test nếu cần nhanh)
RUN mvn clean package -DskipTests

# Stage 2: Image chạy thực tế - nhẹ hơn, chỉ copy jar cần thiết
FROM eclipse-temurin:17-jre-alpine

# Thông tin metadata
LABEL maintainer="thientritran.dev@gmail.com"
LABEL version="1.0.0"
LABEL description="Backend Service Docker Image"

# Thiết lập thư mục làm việc
WORKDIR /app

# Copy jar từ stage build sang
COPY --from=build /app/target/*.jar backend-service.jar

# Thêm JVM option chuẩn hóa cho container
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75 -XX:+ExitOnOutOfMemoryError"

# Expose port
EXPOSE 8080

# Healthcheck (tuỳ thuộc bạn có health endpoint chưa)
HEALTHCHECK --interval=30s --timeout=5s --start-period=30s --retries=3 \
CMD curl -f http://localhost:8080/actuator/health || exit 1

# Khởi động app
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar backend-service.jar"]

