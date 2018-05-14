package com.laoxiao.mr.weather;

import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

public class MyGroup extends WritableComparator{ //分组其实就是比较(默认比较MapOutputKey是否一样),所以是继承WritableComparator

	public MyGroup(){
		super(MyKey.class,true);
	}
	//这里是自定义成比较年月是否一样,年月一样的为一组
	public int compare(WritableComparable a, WritableComparable b) {
		MyKey k1 =(MyKey) a;
		MyKey k2 =(MyKey) b;
		int r1 =Integer.compare(k1.getYear(), k2.getYear());
		if(r1==0){
			return Integer.compare(k1.getMonth(), k2.getMonth());
		}else{
			return r1;
		}
		
	}
}
