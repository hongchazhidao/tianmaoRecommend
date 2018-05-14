package com.laoxiao.mr.pagerank;


import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.KeyValueTextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class RunJob {
	
	public static enum Mycounter{
		my
	}

	public static void main(String[] args) {
		Configuration config =new Configuration();
		config.set("fs.defaultFS", "hdfs://node1:8020");
		config.set("yarn.resourcemanager.hostname", "node1");
		double d =0.001;
		int i=0;
		while(true){
			i++;
			try {
				config.setInt("runCount", i); //配置一个变量runCount,记录MR任务运行的轮数
				FileSystem fs =FileSystem.get(config);
				Job job =Job.getInstance(config);
				job.setJarByClass(RunJob.class);
				job.setJobName("pr"+i);
				job.setMapperClass(PageRankMapper.class);
				job.setReducerClass(PageRankReducer.class);
				job.setMapOutputKeyClass(Text.class);
				job.setMapOutputValueClass(Text.class);
				job.setInputFormatClass(KeyValueTextInputFormat.class);
				Path inputPath =new Path("/usr/input/pagerank.txt");
				if(i>1){
					inputPath =new Path("/usr/output/pr"+(i-1));
				}
				FileInputFormat.addInputPath(job, inputPath);
				
				Path outpath =new Path("/usr/output/pr"+i);
				if(fs.exists(outpath)){
					fs.delete(outpath, true);
				}
				FileOutputFormat.setOutputPath(job, outpath);
				
				boolean f= job.waitForCompletion(true);
				if(f){
					System.out.println("pr "+ i +" success.");
					long sum= job.getCounters().findCounter(Mycounter.my).getValue();
					System.out.println(sum);
					double avgd= sum/4000.0; //除以4000.0是因为之前放大了1000倍,现在要缩小回来,再除以4是页面个数
					if(avgd<d){
						break;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	static class PageRankMapper extends Mapper<Text, Text, Text, Text>{
		protected void map(Text key, Text value,
				Context context)
				throws IOException, InterruptedException {
			int runCount= context.getConfiguration().getInt("runCount", 1);
			String page =key.toString();
			Node node =null;
			if(runCount==1){
				node =Node.fromMR("1.0"+"\t"+value.toString()); //每个页面的初始pr值都定义为1
			}else{
				node =Node.fromMR(value.toString());
			}
			context.write(new Text(page), new Text(node.toString()));//A:1.0	B	D
			if(node.containsAdjacentNodes()){
				double outValue =node.getPageRank()/node.getAdjacentNodeNames().length; //一行数据中第一个页面 对它有链接的页面的PR贡献值
				for (int i = 0; i < node.getAdjacentNodeNames().length; i++) {
					String  outPage = node.getAdjacentNodeNames()[i];
					context.write(new Text(outPage), new Text(outValue+""));//B:0.5  D:0.5
				}
			}
		}
	}
	
	static class PageRankReducer extends Reducer<Text, Text, Text, Text>{
		protected void reduce(Text arg0, Iterable<Text> arg1,
				Context arg2)
				throws IOException, InterruptedException {
			double sum =0.0;
			Node sourceNode =null;
			for(Text i:arg1){
				Node node =Node.fromMR(i.toString());
				if(node.containsAdjacentNodes()){
					sourceNode =node;
				}else{
					sum=sum+node.getPageRank();
				}
			}
			
			double newPR=(0.15/4)+(0.85*sum); //这里页面个数写死为4,但是实际中应该再用一个MR来计算原始数据中页面的个数
			System.out.println("*********** new pageRank value is "+newPR);
			
			//把新的pr值和计算之前的pr比较
			double d= newPR -sourceNode.getPageRank();
			
			int j=(int)( d*1000.0);//先把差值放大1000倍转成int,之后传给increment(),因为double无法直接转成long
			j=Math.abs(j);
			System.out.println(j+"___________");
			arg2.getCounter(Mycounter.my).increment(j);; //用hadoop内置的计数器进行累加
			
			//写出新的数据
			sourceNode.setPageRank(newPR);
			arg2.write(arg0, new Text(sourceNode.toString()));
		}
	}
}
