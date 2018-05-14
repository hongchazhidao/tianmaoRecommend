package com.laoxiao.mr.wc;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.util.StringUtils;
//LongWritable是hadoop的Long类型,这4个参数是map task输入输出键值对的类型,hadoop中String要用Text
public class WordCountMapper extends Mapper<LongWritable, Text, Text, IntWritable>{

	//该方法循环调用，从文件的split中每读取一行调用一次，默认把该行所在的下标为key(一行的下标是这行首字符的下标)，该行的内容为value
	protected void map(LongWritable key, Text value,
			Context context)
			throws IOException, InterruptedException {
		String[] words = StringUtils.split(value.toString(), ' ');
		for(String w :words){
			context.write(new Text(w), new IntWritable(1));
		}
	}
}
