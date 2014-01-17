package carpool.dbservice;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import carpool.model.representation.SearchRepresentation;
import carpool.aws.*;


public class FileService {
	
	public static void initializeFileForUser(int userId){
		AwsMain.createUserFile(userId);
	}

	public static void storeSearchRepresentation(SearchRepresentation sr, int userId) throws IOException{
		AwsMain.storeSearchHistory(sr, userId);
	}

	public static ArrayList<SearchRepresentation> getUserSearchHistoryFromS3(int userId) throws IOException{
		return AwsMain.getUserSearchHistory(userId);
	}

	public static String uploadUserProfileImg(int userId, File file, String baseFileName){
		return AwsMain.uploadProfileImg(userId, file, baseFileName);
	}
}
