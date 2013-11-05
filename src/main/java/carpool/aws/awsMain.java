package carpool.aws;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import redis.clients.jedis.Jedis;

import carpool.constants.CarpoolConfig;
import carpool.model.representation.SearchRepresentation;

import com.amazonaws.AmazonClientException;
import com.amazonaws.HttpMethod;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.http.AmazonHttpClient;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.CreateBucketRequest;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.RestoreObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;

public class awsMain {

	private static String myAccessKeyID="AKIAJAU3ADUWK7CKFPZQ";
	private static String mySecretKey="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";
	private static String bucketName="BadStudentTest";
	private static String filekey ="Test.doc";
	private static String imgkey="Test.png";

	public static void getImgObject() throws IOException{
		AWSCredentials myCredentials = new BasicAWSCredentials(myAccessKeyID, mySecretKey);
		AmazonS3 s3Client = new AmazonS3Client(myCredentials);
		S3Object object = s3Client.getObject(new GetObjectRequest(bucketName, imgkey));
		InputStream objectData = object.getObjectContent();	
		IOUtils.copy(objectData, new FileOutputStream("/Users/harryxiong/Desktop/img.png"));

		objectData.close();
	}

	public static void getFileObject() throws IOException{
		AWSCredentials myCredentials = new BasicAWSCredentials(myAccessKeyID, mySecretKey);
		AmazonS3 s3Client = new AmazonS3Client(myCredentials);
		S3Object object = s3Client.getObject(new GetObjectRequest(bucketName, filekey));
		InputStream objectData = object.getObjectContent(); 

		InputStream reader = new BufferedInputStream(objectData);
		File file = new File("/Users/harryxiong/Desktop/localFilename");  
		OutputStream writer = new BufferedOutputStream(new FileOutputStream(file));
		int read = -1;
		while ( ( read = reader.read() ) != -1 ) {
			writer.write(read);
		}

		writer.flush();
		writer.close();
		reader.close();

		objectData.close();
	}

	public static  ArrayList<SearchRepresentation> getUserSearchHistory(int userId) throws IOException{
		ArrayList<SearchRepresentation> list = new ArrayList<SearchRepresentation>();
		AWSCredentials myCredentials = new BasicAWSCredentials(myAccessKeyID, mySecretKey);
		AmazonS3 s3Client = new AmazonS3Client(myCredentials);
		String fileName = userId+"/"+userId+"_sr.txt";
		String localfileName = CarpoolConfig.pathToSearchHistoryFolder + userId + CarpoolConfig.searchHistoryFileSufix;
		try{
			S3Object object = s3Client.getObject(new GetObjectRequest(bucketName, fileName));
			InputStream objectData = object.getObjectContent(); 

			InputStream reader = new BufferedInputStream(objectData);
			File file = new File(localfileName);  
			//write
			OutputStream writer = new BufferedOutputStream(new FileOutputStream(file));
			int read = -1;
			while ( ( read = reader.read() ) != -1 ) {
				writer.write(read);
			}

			writer.flush();
			writer.close();
			reader.close();

			objectData.close();
			//read

			BufferedReader bfreader = new BufferedReader(new FileReader(file));
			String line = bfreader.readLine();
			while(line!=null){
				list.add(new SearchRepresentation(line));
				line = bfreader.readLine();
			}
			bfreader.close();
		}catch(AmazonClientException e){
			//There is no such object, just return the list
			return list;
		}

		return list;
	}

	public static void uploadImg(int userId){
		String userProfile = carpool.constants.CarpoolConfig.profileImgPrefix;
		String imgSize = carpool.constants.CarpoolConfig.imgSize_m;
		String imgName = userProfile+imgSize+userId;
		AWSCredentials myCredentials = new BasicAWSCredentials(myAccessKeyID, mySecretKey);
		AmazonS3 s3Client = new AmazonS3Client(myCredentials);
		s3Client.putObject(bucketName,userId+"/"+imgName+".png",new File("/Users/harryxiong/Desktop/Test.png"));

		imgkey = userId+"/"+imgName +".png";

		java.util.Date expiration = new java.util.Date();
		long msec = expiration.getTime();
		msec += 1000 * 60 * 60; // 1 hour.
		expiration.setTime(msec);

		GeneratePresignedUrlRequest generatePresignedUrlRequest = 
				new GeneratePresignedUrlRequest(bucketName, imgkey);
		generatePresignedUrlRequest.setMethod(HttpMethod.GET); 
		generatePresignedUrlRequest.setExpiration(expiration);

		URL s = s3Client.generatePresignedUrl(generatePresignedUrlRequest); 
		//Print out the URL
		//System.out.println(s);

	}
	public static void storeSearchHistory(SearchRepresentation sr,int userId) throws IOException{

		String rediskey = carpool.constants.CarpoolConfig.redisSearchHistoryPrefix+userId;
		int upper = carpool.constants.CarpoolConfig.redisSearchHistoryUpbound;
		String srString = sr.toSerializedString();
		Jedis redis = carpool.carpoolDAO.CarpoolDaoBasic.getJedis();
		redis.lpush(rediskey, srString);
		//check
		if(redis.llen(rediskey)>=upper){
			AWSCredentials myCredentials = new BasicAWSCredentials(myAccessKeyID, mySecretKey);		
			AmazonS3 s3Client = new AmazonS3Client(myCredentials);
			List<String> appendString = redis.lrange(rediskey, 0, upper-1);
			String fileName = userId+"/"+userId+"_sr.txt";
			String localfileName = CarpoolConfig.pathToSearchHistoryFolder + userId + CarpoolConfig.searchHistoryFileSufix;


			try{
				S3Object object = s3Client.getObject(new GetObjectRequest(bucketName,fileName));    
				InputStream objectData = object.getObjectContent(); 
				InputStream reader = new BufferedInputStream(objectData);      
				OutputStream writer = new BufferedOutputStream(new FileOutputStream(new File(localfileName)));
				int read = -1;
				while ( ( read = reader.read() ) != -1 ) {       
					writer.write(read);
				}
				writer.flush();
				writer.close();
				reader.close();
				objectData.close();   

				//Write to file
				BufferedWriter bw = new BufferedWriter(new FileWriter(new File(localfileName), true));
				for(int i=upper-1; i>=0; i--){
					bw.write(appendString.get(i));   
					bw.newLine();
				}    
				bw.flush();
				bw.close();


				s3Client.putObject(new PutObjectRequest(bucketName,fileName,new File(localfileName))); 
				//clean local file
				PrintWriter pw = new PrintWriter(localfileName);
				pw.write("");
				pw.close();
				//clean redis
				redis.del(rediskey);
			}catch(AmazonClientException e){	   
				//Write to file
				BufferedWriter	bw = new BufferedWriter(new FileWriter(new File(localfileName), true));
				for(int i=upper-1; i>=0; i--){
					bw.write(appendString.get(i));   
					bw.newLine();
				}    
				bw.flush();
				bw.close();

				s3Client.putObject(new PutObjectRequest(bucketName,fileName,new File(localfileName))); 
				//clean local file
				PrintWriter pw = new PrintWriter(localfileName);
				pw.write("");
				pw.close();
				//clean redis
				redis.del(rediskey);
			}

		}
	}		



}

