package com.hadoop;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.common.Term;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.util.GenericOptionsParser;

import java.io.IOException;
import java.util.*;

/**
 * 统计文章中的词频
 * 运行方式：java -classpath target/hadoop-demo-jar-with-dependencies.jar com.hadoop.StatisticsWords data/wordin.txt data/wordout
 *
 * @author zhangbocheng
 * @version v1.0
 * @date 2019/11/26 15:38
 */
public class StatisticsWords {
    public static void main(String[] args) throws Exception {

        // 设置Hadoop Home路径
        System.setProperty("hadoop.home.dir", "/home/tools/hadoop-2.2.0");

        Configuration conf = new Configuration();
        String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
        int argsNumber = 2;
        if (otherArgs.length != argsNumber) {
            System.err.println("Usage: wordcount <in> <out>");
            System.exit(1);
        }

        JobConf jobConf = new JobConf(conf, StatisticsWords.class);
        Job job = new Job(jobConf);
        job.setJobName("WordCount");
        job.setMapperClass(WordMapper.class);
        // 设置作业合成类
        job.setCombinerClass(WordReducer.class);
        job.setReducerClass(WordReducer.class);
        // 设置输出数据的关键类和值类
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        // 设置输入和输出文件
        FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
        FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
        System.exit(job.waitForCompletion(true) ? 0: 1);
    }
}


/**
 * WordMapper类
 */
class WordMapper extends Mapper<Object, Text, Text, IntWritable> {
    private final static IntWritable ONCE = new IntWritable(1);
    private Text word = new Text();

    @Override
    public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
        StringTokenizer it = new StringTokenizer(value.toString());
        while (it.hasMoreTokens()) {
            word.set(it.nextToken());
            context.write(word, ONCE);
        }
    }
}


class WordReducer extends Reducer<Text, IntWritable, Text, IntWritable> {

    private IntWritable result = new IntWritable();

    @Override
    public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
        int sum = 0;
        for (IntWritable val: values) {
            sum += val.get();
        }
        result.set(sum);
        context.write(key, result);
    }
}


