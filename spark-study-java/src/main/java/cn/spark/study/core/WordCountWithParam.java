package cn.spark.study.core;

import java.util.Arrays;
import java.util.List;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;

import scala.Tuple2;

public class WordCountWithParam {
	
	public static void main(String[] args) {
		SparkConf conf = new SparkConf()
				.setAppName("WordCount");
//				.setMaster("spark://192.168.0.101:7077");     
		JavaSparkContext sc = new JavaSparkContext(conf);
//		String file = "C://Users//Administrator//Desktop//hello.txt";
		String file = null;
		if(null != args && args.length>0) {
			System.out.println("接收到的参数:"+args[0]);
			file = args[0];
		} else {
			file = "hdfs://192.168.0.101:8020/test/hello.txt";
		}
		
		JavaRDD<String> lines = sc.textFile(file);
		
		JavaRDD<String> words = lines.flatMap(new FlatMapFunction<String, String>() {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public Iterable<String> call(String line) throws Exception {
				return Arrays.asList(line.split(" "));  
			}
			
		});
		
		JavaPairRDD<String, Integer> pairs = words.mapToPair(
				
				new PairFunction<String, String, Integer>() {

					private static final long serialVersionUID = 1L;
		
					@Override
					public Tuple2<String, Integer> call(String word) throws Exception {
						return new Tuple2<String, Integer>(word, 1);
					}
					
				});
		
		JavaPairRDD<String, Integer> wordCounts = pairs.reduceByKey(
				
				new Function2<Integer, Integer, Integer>() {
					
					private static final long serialVersionUID = 1L;
		
					@Override
					public Integer call(Integer v1, Integer v2) throws Exception {
						return v1 + v2;
					}
					
				});
		
		List<Tuple2<String, Integer>> wordCountList = wordCounts.collect();
		for(Tuple2<String, Integer> wordCount : wordCountList) {
			System.out.println(wordCount);  
		}
		
		sc.close();
	}
	
}
