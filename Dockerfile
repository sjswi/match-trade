# 使用官方Maven镜像作为构建环境
FROM maven:3.6.3-jdk-8 AS build
WORKDIR /app

# 复制pom.xml和源码
COPY . ./

# 打包应用程序
RUN mvn package -Pmysql -DskipTests

# 使用官方Java运行时环境作为基础镜像
FROM openjdk:8-jre-slim
WORKDIR /app

# 从构建阶段复制构建的jar包
COPY --from=build /app/target/*.jar app.jar

# 暴露端口
EXPOSE 8888

# 设置容器启动后执行的命令
CMD ["java", "-jar", "app.jar"]
