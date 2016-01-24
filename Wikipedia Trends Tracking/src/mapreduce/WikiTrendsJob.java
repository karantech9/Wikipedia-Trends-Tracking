package mapreduce;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

public class WikiTrendsJob {
	
	public static void main(String[] args) {
		Configuration configuration =  new Configuration();
		
		try {
			Job wikiTrendsJob =  new Job(configuration, "wikitrends-pass1");
			wikiTrendsJob.setJarByClass(WikiTrendsJob.class);
			
			FileInputFormat.addInputPath(wikiTrendsJob, new Path(args[0]));
			FileOutputFormat.setOutputPath(wikiTrendsJob, new Path(args[1]));
			
			wikiTrendsJob.setInputFormatClass(TextInputFormat.class);
			
			wikiTrendsJob.setMapOutputKeyClass(Text.class);
			wikiTrendsJob.setMapOutputValueClass(LongWritable.class);
			
			wikiTrendsJob.setOutputKeyClass(Text.class);
			wikiTrendsJob.setOutputValueClass(LongWritable.class);
			
			wikiTrendsJob.setOutputFormatClass(TextOutputFormat.class);
			
			wikiTrendsJob.setMapperClass(WikiTrendsMapper.class);
			wikiTrendsJob.setReducerClass(WikiTrendsReducer.class);
			
			
			
			wikiTrendsJob.waitForCompletion(true);
			
			Job sortingJob =  new Job(configuration, "wikitrends-pass2");
			sortingJob.setJarByClass(WikiTrendsJob.class);
			
			sortingJob.setMapOutputKeyClass(LongWritable.class);
			sortingJob.setMapOutputValueClass(Text.class);
			
			sortingJob.setOutputKeyClass(LongWritable.class);
			sortingJob.setOutputValueClass(Text.class);
			
			sortingJob.setInputFormatClass(TextInputFormat.class);
			sortingJob.setOutputFormatClass(TextOutputFormat.class);
			
			sortingJob.setMapperClass(SortingMapper.class);
			sortingJob.setSortComparatorClass(LongWritable.DecreasingComparator.class);
			sortingJob.setReducerClass(SortingReducer.class);
			
			FileInputFormat.addInputPath(sortingJob, new Path(args[1]));
			FileOutputFormat.setOutputPath(sortingJob, new Path(args[2]));
			
			sortingJob.waitForCompletion(true);
			
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

