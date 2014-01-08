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
import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;
import carpool.common.DebugLog;
import carpool.constants.CarpoolConfig;
import carpool.model.representation.SearchRepresentation;
import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.http.IdleConnectionReaper;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;


public class awsMain {

	private static String myAccessKeyID= CarpoolConfig.AccessKeyID;
	private static String mySecretKey=CarpoolConfig.SecretKey;
	private static String bucketName="BadStudentTest";
	private static String filekey ="";
	private static String imgkey="";

	static Logger logger = Logger.getLogger(awsMain.class);

	public static void createUserFile(int userId){

		AWSCredentials myCredentials = new BasicAWSCredentials(myAccessKeyID, mySecretKey);		
		AmazonS3 s3Client = new AmazonS3Client(myCredentials);
		String fileName = userId+"/"+userId+"_sr.txt";
		String localfileName = CarpoolConfig.pathToSearchHistoryFolder + userId + CarpoolConfig.searchHistoryFileSufix;
		File file = new File(localfileName);
		try{
			s3Client.putObject(new PutObjectRequest(bucketName,fileName,file));
			IdleConnectionReaper.shutdown();
		}//catch no file exception
		catch(AmazonS3Exception e1){
			e1.printStackTrace();
			DebugLog.d(e1);
		}
		catch(AmazonClientException e2){
			e2.printStackTrace();
			DebugLog.d(e2);
		}

	}

	public static void getImgObject(int userId){
		String userProfile = carpool.constants.CarpoolConfig.profileImgPrefix;
		String imgSize = carpool.constants.CarpoolConfig.imgSize_m;
		String imgName = userProfile+imgSize+userId;

		java.util.Date expiration = new java.util.Date();
		long msec = expiration.getTime();
		imgkey = userId+"/"+imgName+"-"+msec+".png";

		AWSCredentials myCredentials = new BasicAWSCredentials(myAccessKeyID, mySecretKey);
		AmazonS3 s3Client = new AmazonS3Client(myCredentials);

		try{

			S3Object object = s3Client.getObject(new GetObjectRequest(bucketName, imgkey));
			InputStream objectData = object.getObjectContent();	
			IOUtils.copy(objectData, new FileOutputStream(CarpoolConfig.pathToSearchHistoryFolder+imgName+".png"));
			objectData.close();
			IdleConnectionReaper.shutdown();
		}catch(AmazonS3Exception e){
			e.printStackTrace();
			DebugLog.d(e);
		}catch(AmazonServiceException e){
			e.printStackTrace();
			DebugLog.d(e);
		}catch (IOException e) {			
			e.printStackTrace();
			DebugLog.d(e);
		}

	}

	public static void getFileObject(int userId){
		String localfileName = CarpoolConfig.pathToSearchHistoryFolder + userId + CarpoolConfig.searchHistoryFileSufix;
		AWSCredentials myCredentials = new BasicAWSCredentials(myAccessKeyID, mySecretKey);
		AmazonS3 s3Client = new AmazonS3Client(myCredentials);
		File file = new File(localfileName);
		filekey = userId+"/"+userId+"_sr.txt";
		try{

			S3Object object = s3Client.getObject(new GetObjectRequest(bucketName, filekey));
			InputStream objectData = object.getObjectContent(); 
			InputStream reader = new BufferedInputStream(objectData);			 

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
			IdleConnectionReaper.shutdown();
		}catch(AmazonServiceException e){
			e.printStackTrace();
			DebugLog.d(e);
		}catch(IOException e2){
			e2.printStackTrace();
			DebugLog.d(e2);
		}
		file.delete();		
	}

	public static  ArrayList<SearchRepresentation> getUserSearchHistory(int userId){
		ArrayList<SearchRepresentation> list = new ArrayList<SearchRepresentation>();
		AWSCredentials myCredentials = new BasicAWSCredentials(myAccessKeyID, mySecretKey);
		AmazonS3 s3Client = new AmazonS3Client(myCredentials);
		String fileName = userId+"/"+userId+"_sr.txt";
		String localfileName = CarpoolConfig.pathToSearchHistoryFolder + userId + CarpoolConfig.searchHistoryFileSufix;
		File file = new File(localfileName);
		S3Object object = null;
		GetObjectRequest req = new GetObjectRequest(bucketName, fileName);

		try{

			object = s3Client.getObject(req);

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

			object.close();			
		} catch(AmazonServiceException e){			
			if(e.getErrorCode().equals("NoSuchKey")){
				String rediskey = carpool.constants.CarpoolConfig.redisSearchHistoryPrefix+userId;
				int upper = carpool.constants.CarpoolConfig.redisSearchHistoryUpbound;
				Jedis redis = carpool.carpoolDAO.CarpoolDaoBasic.getJedis();
				List<String> appendString = redis.lrange(rediskey, 0, upper-1);

				for(int i=0; i<appendString.size(); i++){
					list.add(new SearchRepresentation(appendString.get(i)));
				}
				if (object != null){
					try {
						object.close();
					} catch (IOException e1) {
						DebugLog.d(e1);
					}
				}
			}
			else{
				DebugLog.d(e);
			}
		} catch(IOException e){
			DebugLog.d(e);
		}
		//Make sure deleting the temp file
		file.delete();	
		IdleConnectionReaper.shutdown();
		return list;
	}

	public static String uploadProfileImg(int userId, File file, String imgName){

		AWSCredentials myCredentials = new BasicAWSCredentials(myAccessKeyID, mySecretKey);
		AmazonS3Client s3Client = new AmazonS3Client(myCredentials);		

		java.util.Date expiration = new java.util.Date();
		long msec = expiration.getTime();
		imgkey = userId+"/"+imgName+"-"+msec+".png";
		URL s = null;
		try{
			s3Client.putObject(new PutObjectRequest(bucketName,imgkey,new File(CarpoolConfig.pathToSearchHistoryFolder+imgName+".png")).withCannedAcl(CannedAccessControlList.PublicRead));
			s = s3Client.getUrl(bucketName, imgkey);			
		}catch(AmazonS3Exception e1){
			e1.printStackTrace();
			DebugLog.d(e1);
		}
		catch(AmazonClientException e2){
			e2.printStackTrace();
			DebugLog.d(e2);
		}
		IdleConnectionReaper.shutdown();
		return  (String) (s.equals(null)? s : s.toString());	

	}

	public static String uploadProfileImg(int userId){
		String userProfile = carpool.constants.CarpoolConfig.profileImgPrefix;
		String imgSize = carpool.constants.CarpoolConfig.imgSize_m;
		String imgName = userProfile+imgSize+userId;
		AWSCredentials myCredentials = new BasicAWSCredentials(myAccessKeyID, mySecretKey);
		AmazonS3Client s3Client = new AmazonS3Client(myCredentials);

		java.util.Date expiration = new java.util.Date();
		long msec = expiration.getTime();
		imgkey = userId+"/"+imgName+"-"+msec+".png";

		URL s = null;

		try{
			s3Client.putObject(new PutObjectRequest(bucketName,imgkey,new File(CarpoolConfig.pathToSearchHistoryFolder+imgName+".png")).withCannedAcl(CannedAccessControlList.PublicRead));
			s = s3Client.getUrl(bucketName, imgkey);
			//System.out.println(s.toString());
			return s.toString();
		}catch(AmazonS3Exception e1){
			e1.printStackTrace();
			DebugLog.d(e1);
		}
		catch(AmazonClientException e2){
			e2.printStackTrace();
			DebugLog.d(e2);
		}
		IdleConnectionReaper.shutdown();
		return  (String) (s.equals(null)? s : s.toString());
	}

	public static void storeSearchHistory(SearchRepresentation sr,int userId){

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


			try{
				//Make sure the file is "empty" before we write to it;
				PrintWriter pwriter = new PrintWriter(localfileName);
				pwriter.write("");
				pwriter.close();

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
			} catch(AmazonServiceException e){	
				if(e.getErrorCode().equals("NoSuchKey")){
					//Write to file
					try{
						BufferedWriter	bw = new BufferedWriter(new FileWriter(file, true));
						for(int i = upper-1; i >= 0; i--){
							bw.write(appendString.get(i));   
							bw.newLine();
						}    
						bw.flush();
						bw.close();

						s3Client.putObject(new PutObjectRequest(bucketName,fileName,file)); 
						//clean redis
						redis.del(rediskey);
					} catch (IOException e1){
						DebugLog.d(e);
					}
				}
				else{
					DebugLog.d(e);
				}
			} catch (IOException e){
				DebugLog.d(e);
			}
			//Make sure deleting the temp file
			file.delete();
			IdleConnectionReaper.shutdown();			
		}

	}		


}

