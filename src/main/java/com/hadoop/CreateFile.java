package com.hadoop;
import java.net.URI;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

/**
 * 创建文件 hdfs file
 *
 * @author zhangbocheng
 * @version v1.0
 * @date 2019/11/19 11:08
 */
public class CreateFile {
    public static void main(String[] args) throws Exception{
        Configuration conf = new Configuration();
        conf.set("fs.hdfs.impl", "org.apache.hadoop.hdfs.DistributedFileSystem");
        URI uri = new URI("hdfs://192.168.192.3");
        FileSystem fs = FileSystem.get(uri, conf);
        // 定义HDFS存储文件位置
        Path dfs = new Path("/user/zhangbocheng/test_new.txt");
        // 创建新文件，如果有覆盖(true)
        FSDataOutputStream createFile = fs.create(dfs, true);
        createFile.writeBytes("Hello,HDFS!");
        createFile.close();
        fs.close();
        System.out.println("Create new file success！");
    }
}
