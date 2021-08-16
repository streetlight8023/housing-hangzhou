#指定基础镜像，在其上进行定制
FROM java:8

#维护者信息
MAINTAINER fanzhengqi <streetlight8023@gmail.com>

#VOLUME /tmp

#复制上下文目录下的target/hourse.jar 到容器里
COPY target/hourse-1.0-SNAPSHOT.jar hourse.jar

#bash方式执行，使demo-1.0.0.jar可访问
#RUN新建立一层，在其上执行这些命令，执行结束后， commit 这一层的修改，构成新的镜像。
RUN bash -c "touch /hourse.jar"

#声明运行时容器提供服务端口，这只是一个声明，在运行时并不会因为这个声明应用就会开启这个端口的服务
EXPOSE 8080

#指定容器启动程序及参数   <ENTRYPOINT> "<CMD>"
ENTRYPOINT ["java","-jar","hourse.jar"]