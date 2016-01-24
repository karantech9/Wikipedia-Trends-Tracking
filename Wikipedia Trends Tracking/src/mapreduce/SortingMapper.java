package mapreduce;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class SortingMapper extends Mapper<LongWritable, Text, LongWritable, Text> {

	@Override
	protected void map(LongWritable key, Text value,
			Mapper<LongWritable, Text, LongWritable, Text>.Context context)
			throws IOException, InterruptedException {
		try {
			System.out.println(value);
			String inputArray[] = value.toString().split("\\s+");
			if(inputArray.length == 2) {
				context.write(new LongWritable(Long.parseLong(inputArray[1])), new Text(inputArray[0]));
			}
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		 
	}
	
}
