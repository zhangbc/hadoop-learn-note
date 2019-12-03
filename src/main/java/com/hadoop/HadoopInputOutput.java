package com.hadoop;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.util.ReflectionUtils;

/**
 * HadoopInputOutput类
 *
 * @author zhangbocheng
 * @version v1.0
 * @date 2019/12/3 14:56
 */
public class HadoopInputOutput {
    public static void main(String[] args) throws Exception {
        System.out.println("Sequence File Writing...");
        SequenceFileWrite sequenceFileWrite = new SequenceFileWrite();
        sequenceFileWrite.write();
        System.out.println("Sequence File Reading...");
        SequenceFileRead sequenceFileRead = new SequenceFileRead();
        sequenceFileRead.reader();
    }
}


/**
 * SequenceFileRead类
 */
class SequenceFileRead {

     void reader() throws Exception {

        // 设置Hadoop Home路径
        System.setProperty("hadoop.home.dir", "/home/tools/hadoop-2.2.0");

        Configuration conf = new Configuration();
        String uri = "data/squence_file.txt";
        Path path = new Path(uri);
        SequenceFile.Reader reader = null;

        SequenceFile.Reader.Option opp = SequenceFile.Reader.file(path);
        SequenceFile.Reader.Option opl = SequenceFile.Reader.length(174);
        try {
            reader = new SequenceFile.Reader(conf, opp, opl);
            Writable key = (Writable) ReflectionUtils.newInstance(reader.getKeyClass(), conf);
            Writable value = (Writable) ReflectionUtils.newInstance(reader.getValueClass(), conf);
            long pos = reader.getPosition();
            while (reader.next(key, value)) {
                String syncSeen = reader.syncSeen() ? "*": "";
                System.out.printf("[%s%s]\t%s\t%s\n", pos, syncSeen, key, value);
            }
        }
        finally {
            IOUtils.closeStream(reader);
        }
    }
}


/**
 * SequenceFileWrite类
 */
class SequenceFileWrite {
    private static final String[] DATA = {
            "One, two, buckle my shoe",
            "Three, four, shut the door",
            "Five six, pick up sticks",
            "Seven, eight, lay them straight",
            "Nine, ten, a big fat hen."
    };

    void write() throws Exception {
        Configuration conf = new Configuration();
        String uri = "data/squence_file.txt";
        IntWritable key = new IntWritable();
        Path path = new Path(uri);
        Text value = new Text();
        SequenceFile.Writer writer = null;

        SequenceFile.Writer.Option opp = SequenceFile.Writer.file(path);
        SequenceFile.Writer.Option opk = SequenceFile.Writer.keyClass(key.getClass());
        SequenceFile.Writer.Option opv = SequenceFile.Writer.valueClass(value.getClass());
        try {
            writer = SequenceFile.createWriter(conf, opp, opk, opv);
            int count = 100;
            for (int i = 0; i < count; i++) {
                key.set(100 - i);
                value.set(DATA[i % DATA.length]);
                System.out.printf("[%s]\t%s\t%s\n", writer.getLength(), key, value);
                writer.append(key, value);
            }
        }
        finally {
            IOUtils.closeStream(writer);
        }
    }
}
