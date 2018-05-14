package com.laoxiao.mr.weather;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.lib.partition.HashPartitioner;
//默认的分区 是用hashcode去%,这样不一定能把属于某一年的数据都分到一个区
public class MyPartitioner extends HashPartitioner<MyKey, DoubleWritable>{ //HashPartitioner后面跟的是MapOutput的key和value

	//maptask每输出一个数据调用一次,所以该方法执行时间越短越好
	public int getPartition(MyKey key, DoubleWritable value, int numReduceTasks) {
		return (key.getYear()-1949)%numReduceTasks;
	}
}
