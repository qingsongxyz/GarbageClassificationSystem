# 基础镜像，使用alpine操作系统，openjkd使用8u201
FROM openjdk:8u201-jdk-alpine3.9

#作者
MAINTAINER qingsongxyz <1351117125@qq.com>

#系统编码
ENV LANG=C.UTF-8 LC_ALL=C.UTF-8

#声明工作目录
WORKDIR /tmp

#应用构建成功后的jar文件被复制到镜像内，名字也改成了app.jar
ADD target/GarbageClassificationSystem-user-1.0-SNAPSHOT.jar GarbageClassificationSystem-user.jar

#启动容器时的进程
CMD ["java","-jar", "GarbageClassificationSystem-user.jar"]

#暴露端口
EXPOSE 8081