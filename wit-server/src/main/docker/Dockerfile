# FROM指定使用哪个镜像作为基准
FROM java:8
# VOLUME为挂载路径  -v
VOLUME /tmp
# ADD为复制文件到镜像中
ADD witServer.jar app.jar
# RUN为初始化时运行的命令  touch更新app.jar  容器使用设置的时区
RUN sh -c 'touch /app.jar' \
     ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
# ENV为设置环境变量 时区：上海
ENV TZ=Asia/Shanghai \
    JAVA_OPTS=""
# ENTRYPOINT为启动时运行的命令
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar" ]