# 本项目为 Hadoop课程作业

- 作业一：编写`Java`程序，实现以程序的方式，把循环产生的1000个字符串写到`hdfs://192.168.192.3/user/yourname/test.txt`。

> 扩展：创建、下载、查看文件信息等功能实现。

# 遇到的问题及其解决方案

- 问题1，如何将`Maven`项目打包成可执行的`jar`包？

解决方案：https://www.cnblogs.com/linjiqin/p/10091113.html

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
☁  hadoop-demo [master] ⚡  mvn clean
☁  hadoop-demo [master] ⚡  mvn assembly:assembly
```

- 问题2，如何将`Maven`项目打包成可执行的`jar`包并通过传参数执行不同的文件？

解决方案：https://blog.csdn.net/daerzei/article/details/82883472#commentBox

1）在`src/main/resources/assembly`创建文件`package.xml`；

2）将以下代码复制到`pom.xml`的对应位置中；

```xml
<build>
  <finalName>hadoop-demo</finalName> <!--配置全局jar包名称-->
  <pluginManagement><!-- lock down plugins versions to avoid using Maven defaults (may be moved to parent pom) -->
      
.....
    
<plugin>
  <artifactId>maven-jar-plugin</artifactId>
  <version>3.0.2</version>
  <!--对要打的jar包进行配置-->
  <configuration>
    <!-- Configuration of the archive -->
    <archive>
      <!--生成的jar中，不要包含pom.xml和pom.properties这两个文件-->
      <addMavenDescriptor>false</addMavenDescriptor>

      <!-- Manifest specific configuration -->
      <manifest>
        <!--是否要把第三方jar放到manifest的classpath中-->
        <addClasspath>true</addClasspath>

        <!--生成的manifest中classpath的前缀，因为要把第三方jar放到lib目录下，
                  所以classpath的前缀是lib/-->
        <classpathPrefix>lib/</classpathPrefix>
      </manifest>
    </archive>
    <!--过滤掉不希望包含在jar中的文件-->
    <excludes>
      <!-- 排除不需要的文件夹(路径是jar包内部的路径) -->
      <exclude>**/assembly/</exclude>
    </excludes>
  </configuration>
</plugin>

...

<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-assembly-plugin</artifactId>
  <configuration>
    <!-- 指定assembly插件的配置文件所在位置 -->
    <descriptors>
      <descriptor>src/main/resources/assembly/package.xml</descriptor>
    </descriptors>
    <descriptorRefs>
      <descriptorRef>jar-with-dependencies</descriptorRef>
    </descriptorRefs>
  </configuration>
  <executions>
    <execution>
      <id>make-assembly</id>
      <!-- 将组装绑定到maven生命周期的哪一阶段 -->
      <phase>package</phase>
      <goals>
        <!-- 指定assembly插件的打包方式-->
        <goal>single</goal>
      </goals>
    </execution>
  </executions>
</plugin>
```

3）将以下代码复制到`package.xml`文件；

```xml
<?xml version="1.0" encoding="UTF-8"?>
<assembly>
    <id>bin</id>
    <!-- 最终打包成一个用于发布的zip文件 -->
    <formats>
        <format>zip</format>
    </formats>

    <!-- 把依赖jar包打包进Zip压缩文件的lib目录下 -->
    <dependencySets>
        <dependencySet>
            <!--不使用项目的artifact，第三方jar不要解压，打包进zip文件的lib目录-->
            <useProjectArtifact>false</useProjectArtifact>

            <!-- 第三方jar打包进Zip文件的lib目录下， -->
            <!-- 注意此目录要与maven-jar-plugin中classpathPrefix指定的目录相同, -->
            <!-- 不然这些依赖的jar包加载到ClassPath的时候会找不到-->
            <outputDirectory>lib</outputDirectory>

            <!-- 第三方jar不要解压-->
            <unpack>false</unpack>
        </dependencySet>
    </dependencySets>

    <!-- 文件设置，你想把哪些文件包含进去，或者把某些文件排除掉，都是在这里配置-->
    <fileSets>
        <!-- 把项目自己编译出来的可执行jar，打包进zip文件的根目录 -->
        <fileSet>
            <directory>${project.build.directory}</directory>
            <outputDirectory></outputDirectory>
            <includes>
                <include>*.jar</include>
            </includes>
        </fileSet>

        <!-- 把项目相关的说明文件，打包进zip文件的根目录 -->
        <fileSet>
            <directory>${project.basedir}</directory>
            <outputDirectory>/</outputDirectory>
            <includes>
                <include>README*</include>
                <include>LICENSE*</include>
                <include>NOTICE*</include>
            </includes>
        </fileSet>

        <!-- 把项目的配置文件，打包进zip文件的config目录 -->
        <fileSet>
            <directory>${project.basedir}</directory>
            <outputDirectory>conf</outputDirectory>
            <includes>
                <include>*.xml</include>
                <include>*.properties</include>
                <include>*.key</include>
            </includes>
        </fileSet>

        <!-- 把项目的脚本文件目录（ src/main/scripts ）中的启动脚本文件，打包进zip文件的跟目录 -->
        <fileSet>
            <directory>${project.build.scriptSourceDirectory}</directory>
            <outputDirectory></outputDirectory>
            <includes>
                <include>*</include>
            </includes>
        </fileSet>
    </fileSets>
</assembly>
```

4）在项目文件夹下执行如下命令即可。

```bash
☁  hadoop-demo [master] ⚡  mvn clean
☁  hadoop-demo [master] ⚡  mvn assembly:assembly
```

5）运行代码

```bash
☁  target [master] ⚡  java -classpath hadoop-demo-jar-with-dependencies.jar com.hadoop.PutFile
☁  target [master] ⚡  java -classpath hadoop-demo-jar-with-dependencies.jar com.hadoop.CreateFile
☁  target [master] ⚡  java -classpath hadoop-demo-jar-with-dependencies.jar com.hadoop.GetFile
☁  target [master] ⚡  java -classpath hadoop-demo-jar-with-dependencies.jar com.hadoop.GetFileStatus
```

- 问题3：在`windows`下之下执行`getFile.java`(下载文件)是报错如下：

```bash
F:\hadoop-demo-bin\hadoop-demo>java -classpath hadoop-demo.jar com.hadoop.GetFil
e
log4j:WARN No appenders could be found for logger (org.apache.hadoop.metrics2.li
b.MutableMetricsFactory).
log4j:WARN Please initialize the log4j system properly.
log4j:WARN See http://logging.apache.org/log4j/1.2/faq.html#noconfig for more in
fo.
SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
SLF4J: Defaulting to no-operation (NOP) logger implementation
SLF4J: See http://www.slf4j.org/codes.html#StaticLoggerBinder for further detail
s.
Exception in thread "main" java.lang.NullPointerException
        at java.lang.ProcessBuilder.start(Unknown Source)
        at org.apache.hadoop.util.Shell.runCommand(Shell.java:482)
        at org.apache.hadoop.util.Shell.run(Shell.java:455)
        at org.apache.hadoop.util.Shell$ShellCommandExecutor.execute(Shell.java:
715)
        at org.apache.hadoop.util.Shell.execCommand(Shell.java:808)
        at org.apache.hadoop.util.Shell.execCommand(Shell.java:791)
        at org.apache.hadoop.fs.RawLocalFileSystem.setPermission(RawLocalFileSys
tem.java:656)
        at org.apache.hadoop.fs.FilterFileSystem.setPermission(FilterFileSystem.
java:490)
        at org.apache.hadoop.fs.ChecksumFileSystem.create(ChecksumFileSystem.jav
a:462)
        at org.apache.hadoop.fs.ChecksumFileSystem.create(ChecksumFileSystem.jav
a:428)
        at org.apache.hadoop.fs.FileSystem.create(FileSystem.java:908)
        at org.apache.hadoop.fs.FileSystem.create(FileSystem.java:889)
        at org.apache.hadoop.fs.FileSystem.create(FileSystem.java:786)
        at org.apache.hadoop.fs.FileUtil.copy(FileUtil.java:365)
        at org.apache.hadoop.fs.FileUtil.copy(FileUtil.java:338)
        at org.apache.hadoop.fs.FileUtil.copy(FileUtil.java:289)
        at org.apache.hadoop.fs.FileSystem.copyToLocalFile(FileSystem.java:1970)

        at org.apache.hadoop.fs.FileSystem.copyToLocalFile(FileSystem.java:1939)

        at org.apache.hadoop.fs.FileSystem.copyToLocalFile(FileSystem.java:1915)

        at com.hadoop.GetFile.main(GetFile.java:26)
```

解决方案：https://www.cnblogs.com/biehongli/p/7895857.html 

1）在`GitHub`上[`winutils`](https://github.com/4ttty/winutils)找对应于要操作的`Hadoop`集群上的`Hadoop`版本，并下载`hadoop.dll`，如本实验需要下载的版本是[`hadoop2.6.0`](https://github.com/4ttty/winutils/blob/master/hadoop-2.6.0/bin/hadoop.dll)；

2）将已下载的`hadoop.dll`版本放入文件目录下`C:\Windows\System32`，即可。