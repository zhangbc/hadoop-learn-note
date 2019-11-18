package com.hadoop;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.io.IOException;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;


/**
 * 上传文件至HDFS
 *
 * @author zhangbocheng
 * @version v1.0
 * @date 2019/11/15 11:11
 */
public class PutFile {
    public static void main(String[] args) throws IOException, URISyntaxException {
        createFile();
        Configuration conf = new Configuration();
        conf.set("fs.hdfs.impl", "org.apache.hadoop.hdfs.DistributedFileSystem");
        URI uri = new URI("hdfs://192.168.192.3");
        FileSystem fs = FileSystem.get(uri, conf);
        // 本地文件
        Path src = new Path("D:\\data\\test.txt");
        // HDFS存储文件位置
        Path dst = new Path("/user/zhangbocheng/test.txt");
        fs.copyFromLocalFile(src, dst);
        System.out.println("Upload to " + conf.get("fs.defaultFS"));
        // 执行hdfs dfs -ls
        FileStatus[] files = fs.listStatus(dst);
        for (FileStatus file:files) {
            System.out.println(file.getPath());
        }
    }

    /**
     * 创建文件并写入数据
     * @throws IOException 创建文件及其写数据异常
     */
    private static void createFile() throws IOException {
        // 创建文件夹
        File file = new File("D:\\data\\test.txt");
        boolean fs = file.createNewFile();
        if (fs)
        {
            System.out.println("文件已经创建成功！");
        }
        else
        {
            System.out.println("文件已经存在！");
        }

        // 写入数据，循环产生1000个字符串
        int count = 1000;
        String str;
        FileOutputStream fos = new FileOutputStream(file);
        for (int i = 0; i < count; i++) {
            str = (i + 1) + "\t" + generateString() + "\n";
            fos.write(str.getBytes());
        }
        fos.close();
    }


    /**
     * 生成随机字符串
     * @return 返回一个任意的随机字符串
     */
    private static String generateString() {
        // 产生一个50～100之间的随机整数
        int count = (int)(Math.random() * (100 - 50 + 1) + 50);
        return RandomStringUtils.randomAlphanumeric(count) + "(length:" + count + ")";
    }
}