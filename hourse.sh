Hid=$(jps | grep hourse | awk '{print $1}')
echo $Hid
  kill -9 $Hid
rm ./nohup.out;
mvn clean install
nohup java -jar -Dfile.encoding=UTF-8 -Xms512m -Xmx1024m -Xmn512m -XX:+PrintGCDateStamps -XX:+PrintGCDetails -Xloggc:/home/ishangjie/gclog/hourse/gc.log ./target/hourse-1.0-SNAPSHOT.jar  --spring.profiles.active=local &> ./nohup.out &