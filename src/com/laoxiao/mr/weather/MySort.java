package com.laoxiao.mr.weather;

import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

public class MySort extends WritableComparator{

	public MySort(){
		super(MyKey.class,true); //第一个参数声明比较的类型是Mykey,第二个是声明创建MySort对象. 不写这个构造方法会报null指针异常.
	}
	
	//这是partition后的sort,也是reduce阶段的sort
	public int compare(WritableComparable a, WritableComparable b) {
		MyKey k1 =(MyKey) a;
		MyKey k2 =(MyKey) b;
		int r1 =Integer.compare(k1.getYear(), k2.getYear());
		if(r1==0){
			int r2 =Integer.compare(k1.getMonth(), k2.getMonth());
			if(r2==0){
				return -Double.compare(k1.getHot(), k2.getHot());
			}else{
				return r2;
			}
		}else{
			return r1;
		}
		
	}
}
