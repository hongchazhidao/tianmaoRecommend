package com.laoxiao.mr.tuijian2;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.Validate;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
 * 把每个用户购买过的商品提取出来
 * @author 26062
 *
 */
public class Step6 {
	
	public static boolean run(Configuration config,Map<String, String> paths){
		try {
			FileSystem fs =FileSystem.get(config);
			Job job =Job.getInstance(config);
			job.setJobName("step6");
			job.setJarByClass(StartRun.class);
			job.setMapperClass(Step6_Mapper.class);
			job.setReducerClass(Step6_Reducer.class);
//			
			job.setMapOutputKeyClass(Text.class);
			job.setMapOutputValueClass(Text.class);
			
			
			FileInputFormat.addInputPath(job, new Path(paths.get("Step6Input")));
			Path outpath=new Path(paths.get("Step6Output"));
			if(fs.exists(outpath)){
				fs.delete(outpath,true);
			}
			FileOutputFormat.setOutputPath(job, outpath);
			
			boolean f= job.waitForCompletion(true);
			return f;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	 static class Step6_Mapper extends Mapper<LongWritable, Text, Text, Text>{

		
		protected void map(LongWritable key, Text value,
				Context context)
				throws IOException, InterruptedException {
			String[]  tokens=value.toString().split(",");
			if (tokens[2].equals("alipay")) { //把用户购买过的商品挑出来
				String item=tokens[0];
				String user=tokens[1];
				Text k= new Text(user);
				Text v =new Text(item);
				context.write(k, v);
			}
		}
	}
	
	 
	 static class Step6_Reducer extends Reducer<Text, Text, Text, Text>{

			protected void reduce(Text key, Iterable<Text> i,
					Context context)
					throws IOException, InterruptedException {
				StringBuffer sb =new StringBuffer();
				for(Text value :i){
					sb.append(value.toString()+",");
				}
				context.write(key,new Text(sb.toString()));
			}
		}
	
}
