# 本项目为 Hadoop课程作业

- 作业一：编写`Java`程序，实现以程序的方式，把循环产生的1000个字符串写到`hdfs://192.168.192.3/user/yourname/test.txt`。

# 遇到的问题及其解决方案

- 问题1，如何将`Maven`项目打包成可执行的`jar`包？

解决方案：

1）将以下代码复制到`pom.xml`的对应位置中；

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-assembly-plugin</artifactId>
            <configuration>
                <archive>
                    <manifest>
                        <mainClass>com.mimaxueyuan.cloud.eureka.EurekaHAApplication</mainClass>
                    </manifest>
                </archive>
                <descriptorRefs>
                    <descriptorRef>
                        jar-with-dependencies
                    </descriptorRef>
                </descriptorRefs>
            </configuration>
        </plugin>
    </plugins>
</build>
```

2）在项目文件夹下执行如下命令即可。

```bash
☁  hadoop-demo [master] ⚡  mvn assembly:assembly
```

