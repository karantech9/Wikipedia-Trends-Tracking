package main;

import upload_data.UploadWikiFiles;
import emr_job.EMRJob;
import get_trends.GetTopTrendingTopics;


public class RunJob {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		//Upload wiki files to S3
		UploadWikiFiles.UploadFiles();
		
		//run emr job
		EMRJob.run();
		
		//Get Top Trending topics
		GetTopTrendingTopics.getTopTrends();

	}

}
