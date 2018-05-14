package com.laoxiao.mr.wc;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class RunJob {

	public static void main(String[] args) {
		Configuration config =new Configuration();
		//config.set("fs.defaultFS", "hdfs://node1:8020");
		//config.set("yarn.resourcemanager.hostname", "node1");
		config.set("mapred.jar", "C:\\Users\\26062\\Desktop\\wc.jar");
		try {
			FileSystem fs =FileSystem.get(config);
			
			Job job =Job.getInstance(config);
			job.setJarByClass(RunJob.class); //设置MR的执行类
			
			job.setJobName("wc");
			
			job.setMapperClass(WordCountMapper.class);
			job.setReducerClass(WordCountReducer.class);
			
			job.setMapOutputKeyClass(Text.class);
			job.setMapOutputValueClass(IntWritable.class);
			
			FileInputFormat.addInputPath(job, new Path("/usr/input/")); //input下所有文件都是输入数据
			
			Path outpath =new Path("/usr/output/wc"); //这个目录要保证原先不存在,hadoop会自动帮我们创建
			if(fs.exists(outpath)){
				fs.delete(outpath, true); //第二个参数是递归删除
			}
			FileOutputFormat.setOutputPath(job, outpath);
			
			boolean f= job.waitForCompletion(true);
			if(f){
				System.out.println("job任务执行成功");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
