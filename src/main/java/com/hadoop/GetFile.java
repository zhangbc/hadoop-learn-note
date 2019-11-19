package com.hadoop;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.net.URI;

/**
 * 下载hdfs文件至本地
 *
 * @author zhangbocheng
 * @version v1.0
 * @date 2019/11/19 11:43
 */
public class GetFile {
    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        conf.set("fs.hdfs.impl", "org.apache.hadoop.hdfs.DistributedFileSystem");
        URI uri = new URI("hdfs://192.168.192.3");
        FileSystem fs = FileSystem.get(uri, conf);
        // HDFS存储文件位置
        Path src = new Path("/user/zhangbocheng/test_new.txt");
        // 本地文件
        Path dst = new Path("D:\\data");
        fs.copyToLocalFile(src, dst);
        fs.close();
        System.out.println("Download file finished！");
    }
}
