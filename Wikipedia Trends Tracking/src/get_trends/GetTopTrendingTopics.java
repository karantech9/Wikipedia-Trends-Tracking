package get_trends;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.transfer.TransferManager;


public class GetTopTrendingTopics {

	private static AWSCredentials credentials = null;
	private static TransferManager tx;
	private static String bucketName;

	public static void getTopTrends() {

		try {
			credentials = new ProfileCredentialsProvider("default").getCredentials();
		} catch (Exception e) {
			throw new AmazonClientException(
					"Cannot load the credentials from the credential profiles file. " +
							"Please make sure that your credentials file is at the correct " +
							"location (C:\\Users\\Saurabh Gupta\\.aws\\credentials), and is in valid format.",
							e);
		}

		AmazonS3 s3 = new AmazonS3Client(credentials);

		bucketName  = "wikitrendstracking" ;//+ credentials.getAWSAccessKeyId().toLowerCase();
		tx = new TransferManager(s3);

		ListObjectsRequest fileListRequest = new ListObjectsRequest().withBucketName(bucketName).
				withPrefix("output1/");
		ObjectListing objectListing = s3.listObjects(fileListRequest);
		String line = null;
		Map<String, Long> recordMap = new HashMap<>();
		try {
			PrintWriter writer = new PrintWriter("TopTrends.txt", "UTF-8");
			for(S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
				if(objectSummary.getKey().contains("part")) {


					//System.out.println(objectSummary.getKey());
					S3Object object = s3.getObject(new GetObjectRequest(bucketName, objectSummary.getKey()));
					BufferedReader reader = new BufferedReader(new InputStreamReader(object.getObjectContent()));
					
					int i =0;
					while((line = reader.readLine()) != null) {
						String recordArray[] = line.split("\\s+");
						if(recordArray.length == 2 && !recordArray[1].contains(":") && !(recordArray[1].equals("en"))
								&&!(recordArray[1].equals("Main_Page"))) {
							
							recordMap.put(recordArray[1], Long.parseLong(recordArray[0]));
							
							i++;
						}

						if(i==10)
							break;
					}
				}
				
			}
			recordMap = sortByValue(recordMap);
			int count =0;
			for(Map.Entry<String, Long> record : recordMap.entrySet()) {
				
				System.out.println(record.getKey() + " " + record.getValue());
				writer.println("{ \"index\" : { \"_index\" : \"twittertrends\", \"_type\" : \"twitterdata\" } }");
				writer.println("{ \"Topic\":\""+ record.getKey()+"\",\"Count\":"+ record.getValue()+ "}");
				count++;
				if(count==10)
					break;
			}
			writer.println();
			writer.close();
			s3.putObject(new PutObjectRequest(bucketName, "output1/TopTrends.txt", new File("TopTrends.txt")).
					withCannedAcl(CannedAccessControlList.PublicRead));
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
	}
	public static <K, V extends Comparable<? super V>> Map<K, V> 
	sortByValue( Map<K, V> map )
	{
		List<Map.Entry<K, V>> list =
				new LinkedList<>( map.entrySet() );
		Collections.sort( list, new Comparator<Map.Entry<K, V>>()
				{
			@Override
			public int compare( Map.Entry<K, V> o1, Map.Entry<K, V> o2 )
			{
				return (o2.getValue()).compareTo( o1.getValue() );
			}
				} );

		Map<K, V> result = new LinkedHashMap<>();
		for (Map.Entry<K, V> entry : list)
		{
			result.put( entry.getKey(), entry.getValue() );
		}
		return result;
	}
}
