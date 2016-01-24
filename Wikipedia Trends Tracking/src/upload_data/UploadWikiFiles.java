package upload_data;

import java.io.File;
import java.nio.file.Paths;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.transfer.MultipleFileUpload;
import com.amazonaws.services.s3.transfer.TransferManager;

public class UploadWikiFiles {

	private static String bucketName = "wikitrendstracking/input";

	public static void UploadFiles() {
		try {
			TransferManager manager = new TransferManager(
					new ProfileCredentialsProvider());
			String dir = new File(Paths.get(".").toAbsolutePath().normalize()
					.toString()).getParent();
			//String inputDir = new File(dir).getParent();
			File folder = new File(dir + "/input");

			MultipleFileUpload upload = manager.uploadDirectory(bucketName,
					null, folder, false);
			while (upload.isDone() == false) {

				System.out.println(Math.round(new Double(upload.getProgress().getPercentTransferred())) + "%");
				Thread.sleep(5000);
			}

			upload.waitForCompletion();
			manager.shutdownNow();

		} catch (AmazonServiceException e) {
			e.getMessage();
			e.printStackTrace();
		} catch (AmazonClientException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

}

