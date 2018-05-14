package com.laoxiao.mr.tuijian2;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
 * 
 * 这一步做的事情是 在给step5输出结果的推荐度排序前,将用户买过的商品的推荐度行删去
 * 
 * @author 26062
 *
 */
public class Step7 {
	public static boolean run(Configuration config, Map<String, String> paths) {
		try {
			FileSystem fs = FileSystem.get(config);
			Job job = Job.getInstance(config);
			job.setJobName("step7");
			job.setJarByClass(StartRun.class);
			job.setMapperClass(Step7_Mapper.class);
			job.setReducerClass(Step7_Reducer.class);
			job.setMapOutputKeyClass(Text.class);
			job.setMapOutputValueClass(Text.class);

			// FileInputFormat.addInputPath(job, new
			// Path(paths.get("Step4Input")));
			FileInputFormat.setInputPaths(job,
					new Path[] { new Path(paths.get("Step7Input1")),
							new Path(paths.get("Step7Input2")) });
			Path outpath = new Path(paths.get("Step7Output"));
			if (fs.exists(outpath)) {
				fs.delete(outpath, true);
			}
			FileOutputFormat.setOutputPath(job, outpath);

			boolean f = job.waitForCompletion(true);
			return f;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	static class Step7_Mapper extends Mapper<LongWritable, Text, Text, Text> {
		private String flag;// A同现矩阵 or B得分矩阵

		//每个maptask，初始化时调用一次
		protected void setup(Context context) throws IOException,
				InterruptedException {
			FileSplit split = (FileSplit) context.getInputSplit();
			flag = split.getPath().getParent().getName();// 判断读的数据集

			System.out.println(flag + "**********************");
		}

		protected void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {
			String[] tokens = Pattern.compile("[\t,]").split(value.toString());
            //无论是step5还是step6的输出结果作为输入,都是以user+","+item作为mapOutputKey,之后在reduce时如果一组中有2个数据行的就可以不用输出
			if (flag.equals("step5")) {
				String user = tokens[0];
				String item = tokens[1];
				String recommendSco = tokens[2];
				Text k = new Text(user+","+item);
				Text v = new Text(recommendSco);
				context.write(k, v);

			} else if (flag.equals("step6")) {
				String user = tokens[0];
				String hasBuyItemsStr = tokens[1];
				String[] hasBuyitems = hasBuyItemsStr.split(",");
				for (String hasBuyItem : hasBuyitems) {
					Text k = new Text(user+","+hasBuyItem);
					Text v = new Text("hasbuy");
					context.write(k, v);
				}
			}
		}
	}

	static class Step7_Reducer extends Reducer<Text, Text, Text, Text> {
		protected void reduce(Text key, Iterable<Text> values, Context context)
				throws IOException, InterruptedException {
			Iterator<Text> iterator = values.iterator();
			String recommendSco = iterator.next().toString();
			
			if (! iterator.hasNext()) { //一组中只有1个数据行的才输出,说明那行数据对应的item用户没有买过
				String[] userAndItem = key.toString().split(",");
				String user = userAndItem[0];
				String item = userAndItem[1];
				Text k = new Text(user);
				Text v = new Text(item+","+recommendSco);
				context.write(k, v);
			}
		}
	}

}
