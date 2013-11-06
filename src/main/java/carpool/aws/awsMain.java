package carpool.aws;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Appender;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;
import org.apache.log4j.varia.NullAppender;
import org.apache.log4j.Layout;
import redis.clients.jedis.Jedis;
import carpool.common.DebugLog;
import carpool.constants.CarpoolConfig;
import carpool.model.representation.SearchRepresentation;
import com.amazonaws.AmazonClientException;
import com.amazonaws.HttpMethod;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;


public class awsMain {

	private static String myAccessKeyID="AKIAJAU3ADUWK7CKFPZQ";
	private static String mySecretKey="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";
	private static String bucketName="BadStudentTest";
	private static String filekey ="";
	private static String imgkey="";

	public static void getImgObject(int userId) throws IOException{
		String userProfile = carpool.constants.CarpoolConfig.profileImgPrefix;
		String imgSize = carpool.constants.CarpoolConfig.imgSize_m;
		String imgName = userProfile+imgSize+userId;
		imgkey = userId+"/"+imgName +".png";
		AWSCredentials myCredentials = new BasicAWSCredentials(myAccessKeyID, mySecretKey);
		AmazonS3 s3Client = new AmazonS3Client(myCredentials);
		try{
			BasicConfigurator.configure();

			S3Object object = s3Client.getObject(new GetObjectRequest(bucketName, imgkey));
			InputStream objectData = object.getObjectContent();	
			IOUtils.copy(objectData, new FileOutputStream(CarpoolConfig.pathToSearchHistoryFolder+imgName+".png"));
			objectData.close();

		}catch(AmazonClientException e){
			e.printStackTrace();
			DebugLog.d(e.getMessage());
		}
	}

	public static void getFileObject(int userId) throws IOException{
		String localfileName = CarpoolConfig.pathToSearchHistoryFolder + userId + CarpoolConfig.searchHistoryFileSufix;
		AWSCredentials myCredentials = new BasicAWSCredentials(myAccessKeyID, mySecretKey);
		AmazonS3 s3Client = new AmazonS3Client(myCredentials);
		filekey = userId+"/"+userId+"_sr.txt";
		try{
			BasicConfigurator.configure();

			S3Object object = s3Client.getObject(new GetObjectRequest(bucketName, filekey));
			InputStream objectData = object.getObjectContent(); 

			InputStream reader = new BufferedInputStream(objectData);
			File file = new File(localfileName); 

			//Make sure the file is "empty" before we write to it;
			PrintWriter pwriter = new PrintWriter(localfileName);
			pwriter.write("");
			pwriter.close();

			OutputStream writer = new BufferedOutputStream(new FileOutputStream(file));
			int read = -1;
			while ( ( read = reader.read() ) != -1 ) {
				writer.write(read);
			}

			writer.flush();
			writer.close();
			reader.close();

			objectData.close();			
		}catch(AmazonClientException e){
			e.printStackTrace();
			DebugLog.d(e.getMessage());
		}

	}

	public static  ArrayList<SearchRepresentation> getUserSearchHistory(int userId) throws IOException{
		ArrayList<SearchRepresentation> list = new ArrayList<SearchRepresentation>();
		AWSCredentials myCredentials = new BasicAWSCredentials(myAccessKeyID, mySecretKey);
		AmazonS3 s3Client = new AmazonS3Client(myCredentials);
		String fileName = userId+"/"+userId+"_sr.txt";
		String localfileName = CarpoolConfig.pathToSearchHistoryFolder + userId + CarpoolConfig.searchHistoryFileSufix;
		File file = new File(localfileName);  

		try{
			BasicConfigurator.configure();

			S3Object object = s3Client.getObject(new GetObjectRequest(bucketName, fileName));
			InputStream objectData = object.getObjectContent(); 

			InputStream reader = new BufferedInputStream(objectData);

			//Make sure the file is "empty" before we write to it;
			PrintWriter pwriter = new PrintWriter(localfileName);
			pwriter.write("");
			pwriter.close();
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

			String rediskey = carpool.constants.CarpoolConfig.redisSearchHistoryPrefix+userId;
			int upper = carpool.constants.CarpoolConfig.redisSearchHistoryUpbound;
			Jedis redis = carpool.carpoolDAO.CarpoolDaoBasic.getJedis();
			List<String> appendString = redis.lrange(rediskey, 0, upper-1);

			for(int i=0; i<appendString.size(); i++){
				list.add(new SearchRepresentation(appendString.get(i)));
			}

		}catch(AmazonClientException e){

			String rediskey = carpool.constants.CarpoolConfig.redisSearchHistoryPrefix+userId;
			int upper = carpool.constants.CarpoolConfig.redisSearchHistoryUpbound;
			Jedis redis = carpool.carpoolDAO.CarpoolDaoBasic.getJedis();
			List<String> appendString = redis.lrange(rediskey, 0, upper-1);

			for(int i=0; i<appendString.size(); i++){
				list.add(new SearchRepresentation(appendString.get(i)));
			}
			return list;
		}
		return list;
	}

	public static String uploadProfileImg(int userId) throws IOException{
		String userProfile = carpool.constants.CarpoolConfig.profileImgPrefix;
		String imgSize = carpool.constants.CarpoolConfig.imgSize_m;
		String imgName = userProfile+imgSize+userId;
		AWSCredentials myCredentials = new BasicAWSCredentials(myAccessKeyID, mySecretKey);
		AmazonS3 s3Client = new AmazonS3Client(myCredentials);

		BasicConfigurator.configure();

		s3Client.putObject(bucketName,userId+"/"+imgName+".png",new File(CarpoolConfig.pathToSearchHistoryFolder+imgName+".png"));
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

		return s.toString();

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
			File file = new File(localfileName);

			//Make sure the file is "empty" before we write to it;
			PrintWriter pwriter = new PrintWriter(localfileName);
			pwriter.write("");
			pwriter.close();

			BasicConfigurator.configure();
			try{

				S3Object object = s3Client.getObject(new GetObjectRequest(bucketName,fileName));    
				InputStream objectData = object.getObjectContent(); 
				InputStream reader = new BufferedInputStream(objectData);      
				OutputStream writer = new BufferedOutputStream(new FileOutputStream(file));
				int read = -1;
				while ( ( read = reader.read() ) != -1 ) {       
					writer.write(read);
				}
				writer.flush();
				writer.close();
				reader.close();
				objectData.close();   

				//Write to file
				BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
				for(int i=upper-1; i>=0; i--){
					bw.write(appendString.get(i));   
					bw.newLine();
				}    
				bw.flush();
				bw.close();


				s3Client.putObject(new PutObjectRequest(bucketName,fileName,file)); 
				//clean redis
				redis.del(rediskey);
			}catch(AmazonClientException e){	   
				//Write to file
				BufferedWriter	bw = new BufferedWriter(new FileWriter(file, true));
				for(int i=upper-1; i>=0; i--){
					bw.write(appendString.get(i));   
					bw.newLine();
				}    
				bw.flush();
				bw.close();

				s3Client.putObject(new PutObjectRequest(bucketName,fileName,file)); 
				//clean redis
				redis.del(rediskey);
			}

		}
	}		



}

