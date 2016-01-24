package mapreduce;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class WikiTrendsMapper extends Mapper<LongWritable, Text, Text, LongWritable>{
	
	@Override
	protected void map(LongWritable key, Text value,
			Mapper<LongWritable, Text, Text, LongWritable>.Context context)
			throws IOException, InterruptedException {

		String recordArray[] = new String [] {};
		try {
			if(value.toString().startsWith("en")){
				recordArray = value.toString().split(" ");
				
				if(recordArray.length == 4) {
					//String pageName = java.net.URLDecoder.decode(recordArray[1].replaceAll("%", "%25" ), "UTF-8");
					long pageHits = Long.parseLong(recordArray[2]);
				context.write(new Text(recordArray[1]),	new LongWritable(pageHits));//Long.getLong(recordArray[2])));
				}
					
			}
			
		}
		catch(Exception e) {
			System.out.println(value + "at record" + key.toString());
			System.out.println(recordArray[1] + "" + recordArray[1] + "" + recordArray[2] + ""  + recordArray[2]);
			throw e;
		}
		
	}
	

}
