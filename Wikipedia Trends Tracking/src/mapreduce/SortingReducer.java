package mapreduce;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class SortingReducer extends Reducer<LongWritable, Text, LongWritable, Text> {
	@Override
	protected void reduce(LongWritable key, Iterable<Text> values,
			Reducer<LongWritable, Text, LongWritable, Text>.Context context)
			throws IOException, InterruptedException {
		for(Text value: values) {
			context.write(key, value);
		}
	}
}
