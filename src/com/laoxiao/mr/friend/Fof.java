package com.laoxiao.mr.friend;

import org.apache.hadoop.io.Text;

//朋友的朋友（friend of a friend）
//这个类用来保证如 凤姐 老王 和 老王 凤姐会被统一变成内容一样的字符串,然后被分到一组
public class Fof extends Text{

	public Fof(){
		super();
	}
	
	public Fof(String a,String b){
		super(getFof(a, b));
	}

	public static String getFof(String a,String b){
		int r =a.compareTo(b);
		if(r<0){
			return a+"\t"+b;
		}else{
			return b+"\t"+a;
		}
	}
}
