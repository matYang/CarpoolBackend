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
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;
import carpool.carpoolDAO.CarpoolDaoBasic;
import carpool.common.DateUtility;
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


public class AwsMain {

	private static final String myAccessKeyID = CarpoolConfig.AccessKeyID;
	private static final String mySecretKey = CarpoolConfig.SecretKey;
	private static final String bucketName = "Badstudent";
	private static final AWSCredentials myCredentials = new BasicAWSCredentials(myAccessKeyID, mySecretKey);

	static Logger logger = Logger.getLogger(AwsMain.class);

	public static void createUserFile(int userId){

		AmazonS3 s3Client = new AmazonS3Client(myCredentials);
		File file = new File(getUserSHLocalFileName(userId));
		
		try{
			file.createNewFile();
			s3Client.putObject(new PutObjectRequest(bucketName,getUserSHFileName(userId),file));
		} catch(AmazonS3Exception e){
			e.printStackTrace();  
			DebugLog.d(e);
		} catch(AmazonClientException | IOException e){
			e.printStackTrace();
			DebugLog.d(e);
		} finally{
			file.delete();
			IdleConnectionReaper.shutdown();
		}

	}



	public static boolean migrateUserSearchHistory(int userId){
		AmazonS3 s3Client = new AmazonS3Client(myCredentials);
		Jedis redis = CarpoolDaoBasic.getJedis();

		//Clean all the usrSRH
		S3Object object = null; 
		File file = new File(getUserSHLocalFileName(userId));

		try{
			//initialize the new empty file
			file.createNewFile();
			object = s3Client.getObject(new GetObjectRequest(bucketName,getUserSHFileName(userId)));
			writeS3ToFile(object, file);			

			//Get redis SRH of each user
			String rediskey = getUserSHRedisKey(userId);
			long upper = redis.llen(rediskey);
			List<String> appendString = redis.lrange(rediskey, 0, upper-1);

			//Write to file			
			writeSHToFile(appendString, file);		
			s3Client.putObject(new PutObjectRequest(bucketName,getUserSHFileName(userId),file)); 

			//clean redis
			redis.del(rediskey);
		} catch(AmazonS3Exception e){
			e.printStackTrace();
			DebugLog.d(e);
			return false;
		} catch(AmazonClientException | IOException e){
			e.printStackTrace();
			DebugLog.d(e);
			return false;
		} finally{
			if (object != null){
				try {
					object.close();
				} catch (IOException e1) {
					DebugLog.d(e1);
				}
			}
			CarpoolDaoBasic.returnJedis(redis);
			file.delete();
			IdleConnectionReaper.shutdown();
		}
		
		return true;
	}

	public static boolean migrateAlltheUsersSearchHistory(){
		Jedis redis = CarpoolDaoBasic.getJedis();
		Set<String> keyset = redis.keys(CarpoolConfig.redisSearchHistoryPrefix + "*");
		CarpoolDaoBasic.returnJedis(redis);
		
		for (String key : keyset){
			//key.replaceAll(CarpoolConfig.redisSearchHistoryPrefix, "");
			int userId = Integer.parseInt(key.substring(CarpoolConfig.redisSearchHistoryPrefix.length()));
			migrateUserSearchHistory(userId);
		}

		return true;
	} 
	
	
	public static  ArrayList<SearchRepresentation> getUserSearchHistory(int userId){
		ArrayList<SearchRepresentation> list = new ArrayList<SearchRepresentation>();
		File file = new File(getUserSHLocalFileName(userId));
		
		S3Object object = null;
		AmazonS3 s3Client = new AmazonS3Client(myCredentials);
		GetObjectRequest req = new GetObjectRequest(bucketName, getUserSHFileName(userId));

		try{
			file.createNewFile();
			
			object = s3Client.getObject(req);
			writeS3ToFile(object, file);
			
			//read
			list.addAll(readSHFromFile(file));
			list.addAll(getUserSearchHistoryFromRedis(userId));
					
		} catch(AmazonServiceException e){			
			if(e.getErrorCode().equals("NoSuchKey")){
				list.addAll(getUserSearchHistoryFromRedis(userId));
			}
			else{
				DebugLog.d(e);
			}
		} catch(IOException e){
			DebugLog.d(e);
		} finally{
			if (object != null){
				try {
					object.close();
				} catch (IOException e1) {
					DebugLog.d(e1);
				}
			}
			//Make sure deleting the temp file
			file.delete();	
			IdleConnectionReaper.shutdown();
		}
		return list;
	}


	public static void storeSearchHistory(SearchRepresentation sr,int userId){

		String rediskey = getUserSHRedisKey(userId);
		Jedis redis = CarpoolDaoBasic.getJedis();
		redis.lpush(rediskey, sr.toSerializedString());
		
		S3Object object = null;
		//check
		if(redis.llen(rediskey) >= CarpoolConfig.redisSearchHistoryUpbound){
			AmazonS3 s3Client = new AmazonS3Client(myCredentials);
			List<String> appendString = redis.lrange(rediskey, 0, CarpoolConfig.redisSearchHistoryUpbound-1);
			String fileName = getUserSHFileName(userId);
			File file = new File(getUserSHLocalFileName(userId));

			try{
				file.createNewFile();

				object = s3Client.getObject(new GetObjectRequest(bucketName,fileName));
				writeS3ToFile(object, file);
				writeSHToFile(appendString, file);
				s3Client.putObject(new PutObjectRequest(bucketName,fileName,file)); 
				
				//clean redis
				redis.del(rediskey);
			} catch(AmazonServiceException e){	
				if(e.getErrorCode().equals("NoSuchKey")){
					try{
						writeSHToFile(appendString, file);
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
			} finally {
				if (object != null){
					try {
						object.close();
					} catch (IOException e1) {
						DebugLog.d(e1);
					}
				}
				CarpoolDaoBasic.returnJedis(redis);
				//Make sure deleting the temp file
				file.delete();
				IdleConnectionReaper.shutdown();	
			}
		} else{
			CarpoolDaoBasic.returnJedis(redis);
		}
	}		
	
	public static String uploadProfileImg(int userId, File file, String imgName){
		return uploadProfileImg(userId, file, imgName, true);
	}
	
	
	//the boolean shouldDelete is used for testing so that the sample is not deleted every time
	public static String uploadProfileImg(int userId, File file, String imgName, boolean shouldDelete){

		AmazonS3Client s3Client = new AmazonS3Client(myCredentials);		
		URL s = null;
		try{
			String tempImageKey = getUserImageKey(userId, imgName);
			s3Client.putObject(new PutObjectRequest(bucketName,tempImageKey,file).withCannedAcl(CannedAccessControlList.PublicRead));
			s = s3Client.getUrl(bucketName, tempImageKey);			
		} catch(AmazonS3Exception e){
			e.printStackTrace();
			DebugLog.d(e);
		} catch(AmazonClientException e){
			e.printStackTrace();
			DebugLog.d(e);
		} finally{
			IdleConnectionReaper.shutdown();
			if (shouldDelete){
				file.delete();
			}
		}		
		return  s == null ? null : s.toString();	

	}

	
	
	private static ArrayList<SearchRepresentation> getUserSearchHistoryFromRedis(int userId){
		Jedis redis = CarpoolDaoBasic.getJedis();
		List<String> appendString = redis.lrange(getUserSHRedisKey(userId), 0, CarpoolConfig.redisSearchHistoryUpbound-1);
		CarpoolDaoBasic.returnJedis(redis);
		
		ArrayList<SearchRepresentation> srList = new ArrayList<SearchRepresentation>();
		for (String str : appendString){
			srList.add(new SearchRepresentation(str));
		}
		
		return srList;
	}
	
	private static String getUserSHRedisKey(int userId){
		return CarpoolConfig.redisSearchHistoryPrefix+userId;
	}
	
	private static String getUserSHFileName(int userId){
		return userId + "/" + userId + "_sr.txt";
	}
	
	private static String getUserSHLocalFileName(int userId){
		return CarpoolConfig.pathToSearchHistoryFolder + userId + CarpoolConfig.searchHistoryFileSufix;	
	}
	
	private static String getUserImageKey(int userId, String imageName){
		long msec = DateUtility.getCurTime();		
		return userId + "/" + imageName + "-" + msec + ".png";
	}
	
	private static void writeSHToFile(List<String> shList, File file) throws IOException{
		BufferedWriter	bw = new BufferedWriter(new FileWriter(file, true));
		for(int i = shList.size() - 1; i >= 0; i--){
			bw.write(shList.get(i));   
			bw.newLine();
		}    
		bw.flush();
		bw.close();
	}
	
	private static void writeS3ToFile(S3Object object, File file) throws IOException{
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
	}
	
	private static List<SearchRepresentation> readSHFromFile(File file) throws IOException{
		ArrayList<SearchRepresentation> shList = new ArrayList<SearchRepresentation>();
		BufferedReader bfreader = new BufferedReader(new FileReader(file));
		String line = bfreader.readLine();
		while(line!=null){
			shList.add(new SearchRepresentation(line));
			line = bfreader.readLine();
		}
		bfreader.close();
		return shList;
	}

}

