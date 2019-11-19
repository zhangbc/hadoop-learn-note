package com.hadoop;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.BlockLocation;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

/**
 * 查看文件详细信息
 *
 * @author zhangbocheng
 * @version v1.0
 * @date 2019/11/19 11:17
 */
public class GetFileStatus {
    public static void main(String[] args) throws Exception{
        Configuration conf = new Configuration();
        conf.set("fs.hdfs.impl", "org.apache.hadoop.hdfs.DistributedFileSystem");
        URI uri = new URI("hdfs://192.168.192.3");
        FileSystem fs = FileSystem.get(uri, conf);
        // 定义HDFS存储文件位置
        Path fPath = new Path("/user/zhangbocheng/test_new.txt");
        FileStatus fileStatus = fs.getFileStatus(fPath);

        /*获取文件在HDFS集群上的位置：
         * getFileBlockLocations(FileStatus file, long start, long len)
         * 可获取指定文件在HDFS集群上的位置，其中file为文件的完整路径，start和len标识查找文件的路径
         */
        BlockLocation[] blockLocations = fs.getFileBlockLocations(fileStatus, 0, fileStatus.getLen());
        fileStatus.getAccessTime();
        for (int i = 0; i < blockLocations.length; i++) {
            String[] hosts = blockLocations[i].getHosts();
            System.out.println("block_" + i + "_location: " + hosts[0]);
        }

        // 格式化日期输出
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 获取文件访问时间
        long accessTime = fileStatus.getAccessTime();
        System.out.println("access time: " + formatter.format(new Date(accessTime)));
        // 获取文件修改时间
        long modifyTime = fileStatus.getModificationTime();
        System.out.println("modify time: " + formatter.format(new Date(modifyTime)));
        // 获取块大小，单位为B
        long blockSize = fileStatus.getBlockSize();
        System.out.println("block size(B): " + blockSize);
        // 获取文件大小，单位为B
        long len = fileStatus.getLen();
        System.out.println("length(B): " + len);
        // 获取文件所在用户组
        String group = fileStatus.getGroup();
        System.out.println("group: " + group);
        // 获取文件的拥有者
        String owner = fileStatus.getOwner();
        System.out.println("owner: " + owner);
        // 获取文件复制数
        short replication = fileStatus.getReplication();
        System.out.println("replication: " + replication);
    }
}
