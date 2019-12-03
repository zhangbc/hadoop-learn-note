package com.hadoop;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.dictionary.stopword.CoreStopWordDictionary;
import com.hankcs.hanlp.seg.common.Term;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 统计文章中的词频
 *
 * java -classpath target/hadoop-demo-jar-with-dependencies.jar com.hadoop.WordsCount data/AC data/wordout
 * @author zhangbocheng
 * @version v1.0
 * @date 2019/11/26 15:38
 */
public class WordsCount {
    public static void main(String[] args) throws Exception {

        //设置日期格式
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        System.out.println("WordsCount beginning: " + df.format(new Date()));
        long start = System.currentTimeMillis();

        // 设置Hadoop Home路径
        System.setProperty("hadoop.home.dir", "/home/tools/hadoop-2.2.0");

        Configuration conf = new Configuration();
        String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
        int argsNumber = 2;
        if (otherArgs.length != argsNumber) {
            System.err.println("Usage: wordcount <in> <out>");
            System.exit(1);
        }

        Job job = Job.getInstance(conf);
        job.setJobName("WordsCount");
        job.setMapperClass(WordsMapper.class);
        // 设置作业合成类
        job.setCombinerClass(WordsReducer.class);
        job.setReducerClass(WordsReducer.class);
        // 设置输出数据的关键类和值类
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        // 设置输入和输出文件
        FileInputFormat.setInputPaths(job, new Path(otherArgs[0]));
        FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
        // 提交
        int isOk = job.waitForCompletion(true) ? 0: 1;
        System.exit(isOk);

        long end = System.currentTimeMillis();
        System.out.println("WordsCount end: " + df.format(new Date()));
        System.out.println("WordsCount MapReduce go " + (end - start));
    }
}


/**
 * WordMapper类：
 * LongWritable的key是线的偏移量，表示该行在文件中的位置
 * IntWritable的key是行号
 * Text一行文本信息 字符串类型String
 */
class WordsMapper extends Mapper<LongWritable, Text, Text, IntWritable> {
    private IntWritable ONCE = new IntWritable(1);
    private Text word = new Text();
    private Passages passages = new Passages();
    private String spilt = " ";

    @Override
    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        StringTokenizer it = new StringTokenizer(value.toString());
        while (it.hasMoreTokens()) {
            for (String w: passages.splitWords(it.nextToken()).split(spilt)) {
                word.set(w);
                context.write(word, ONCE);
            }
        }
    }
}


class WordsReducer extends Reducer<Text, IntWritable, Text, IntWritable> {

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


class Passages {
    private String pathDir = "data/AC";
    private List<String> totalFiles = new LinkedList<>();

    /**
     * 读取单个文件
     * @param fileName 文件名
     * @return 文件内容
     * @throws IOException 文件操作异常
     */
    String readFile(String fileName) throws IOException {
        StringBuilder strText = new StringBuilder();
        FileInputStream fop = new FileInputStream(fileName);
        InputStreamReader reader = new InputStreamReader(fop, "UTF-8");
        while (reader.ready()) {
            strText.append((char)reader.read());
        }
        return strText.toString();
    }

    /**
     * 将字符串转为json
     * @param text 文本字符串
     * @return json文本
     * @throws JSONException JSON转换异常
     */
    JSONObject strToJson(String text) throws JSONException {
        return new JSONObject(text);
    }

    /**
     * 获取文件夹下所有文件(包含路径)
     * @param dir 文件夹路径
     */
    void getFileNames(String dir) {
        if (dir == null || "".equals(dir)) {
            System.out.println(dir + "请初始化文件目录！");
            System.exit(1);
        }

        File fp = new File(dir);
        if (!fp.isDirectory()) {
            System.out.println(dir + " 不是一个目录！");
            System.exit(2);
        }

        // 提取包含的文件和文件夹
        String[] dirFiles = fp.list();
        if (dirFiles == null || dirFiles.length == 0)
        {
            System.out.println(dir + " 是一个空目录！");
            System.exit(2);
        }

        for (String dirFile: dirFiles) {
            File tmp = new File(dir + "/" + dirFile);
            if (tmp.isDirectory()) {
                getFileNames(tmp.toString());
            } else {
                totalFiles.add(tmp.toString());
            }
        }
    }

    /**
     *  将文件提取为JSON数组
     * @param strText 文本
     * @return JSON数组
     */
    Map<Integer, JSONObject> getPassageJSONArray(String strText) {
        String[] array = strText.split("\n");
        Map<Integer, JSONObject> map = new HashMap<>(16);
        Integer count = 1;
        for (String arr: array) {
            map.put(count, strToJson(arr));
            count++;
        }
        return map;
    }

    String splitWords(String strText) {
        StringBuilder wordsText = new StringBuilder();
        List<Term> words = HanLP.segment(strText);
        for (Term term: words) {
            // 过滤标点符号
            if (term.nature.startsWith('w')) {
                continue;
            }
            // 过滤停用词
            if (CoreStopWordDictionary.contains(term.word)) {
                continue;
            }

            if (!" ".equals(term.word)) {
                wordsText.append(term.word);
                wordsText.append(" ");
            }
        }
        return wordsText.toString();
    }

    public static void main(String[] args) throws IOException {
        Passages passages = new Passages();
        String strText;
        passages.getFileNames(passages.pathDir);
        for (String dir: passages.totalFiles) {
            String passage = passages.readFile(dir);
            Map<Integer, JSONObject> jsonObjectMap = passages.getPassageJSONArray(passage);
            for (int i = 1; i <= jsonObjectMap.size(); i++) {
                strText = (String) jsonObjectMap.get(i).get("text");
                String strList = passages.splitWords(strText);
                System.out.println(strList);
            }
        }
    }
}