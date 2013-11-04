package carpool.aws;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceAsync;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceAsyncClient;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;

/**
 * @author Brian DiCasa
 */
public class AWSClientFactory {

	private static final String AWS_ACCESS_KEY = "harryharryharry";
	private static final String AWS_SECRET_KEY = "matthewmatthewmatthew";
	private static final AWSCredentials awsCredentials;
	private static final AmazonS3 s3Client;
	private static final AmazonSimpleEmailService sesClient;
	private static final AmazonSimpleEmailServiceAsync sesClientAsync;

	static {
		awsCredentials = new BasicAWSCredentials(AWS_ACCESS_KEY, AWS_SECRET_KEY);
		s3Client = new AmazonS3Client(awsCredentials);
		sesClient = new AmazonSimpleEmailServiceClient(awsCredentials);
		sesClientAsync = new AmazonSimpleEmailServiceAsyncClient(awsCredentials);
	}

	public static AmazonS3 getAmazonS3Client() {
		return s3Client;
	}

	public static AmazonSimpleEmailService getAmazonSESClient() {
		return sesClient;
	}

	public static AmazonSimpleEmailServiceAsync getAmazonSESClientAsync() {
		return sesClientAsync;
	}
}
