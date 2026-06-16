# =============================================
# 博客后端 Dockerfile — 多阶段构建
# 阶段1: Maven 构建  |  阶段2: JRE 运行
# =============================================

# ---- 构建阶段 ----
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /build
COPY pom.xml .
# 先下载依赖（利用 Docker 缓存层）
RUN mvn dependency:go-offline -B -q
COPY src ./src
RUN mvn package -DskipTests -B -q

# ---- 运行阶段 ----
FROM eclipse-temurin:17-jre-alpine AS runtime
WORKDIR /app

# 创建上传目录
RUN mkdir -p /data/blog/img

COPY --from=builder /build/target/*.jar app.jar

# JVM 参数：限制内存、OOM 自动 dump
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+ExitOnOutOfMemoryError"

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
