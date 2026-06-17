# =============================================
# 博客后端 Dockerfile — 单阶段构建 (Docker Hub 不可用时)
# 使用本地已有的 maven 镜像构建并运行
# =============================================

FROM maven:3.9-eclipse-temurin-17
WORKDIR /app

# 使用国内 Maven 镜像加速
RUN mkdir -p /root/.m2 && echo '<?xml version="1.0" encoding="UTF-8"?><settings><mirrors><mirror><id>aliyun</id><mirrorOf>central</mirrorOf><url>https://maven.aliyun.com/repository/public</url></mirror></mirrors></settings>' > /root/.m2/settings.xml

COPY pom.xml .
RUN mvn dependency:go-offline -B -q
COPY src ./src
RUN mvn package -Dmaven.test.skip=true -B -q

# 创建上传目录
RUN mkdir -p /data/blog/img

# JVM 参数
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+ExitOnOutOfMemoryError"

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar target/*.jar"]
