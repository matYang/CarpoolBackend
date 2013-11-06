package carpool.dbservice;

import java.io.IOException;
import java.util.ArrayList;
import carpool.model.representation.SearchRepresentation;
import carpool.aws.*;


public class FileService {

	public static void storeSearchRepresentation(int userId, SearchRepresentation sr) throws IOException{
		awsMain.storeSearchHistory(sr, userId);
	}

	public static ArrayList<SearchRepresentation> getUserSearchHistoryFromS3(int userId) throws IOException{
		return awsMain.getUserSearchHistory(userId);
	}

	public static String uploadUserProfileImg(int userId) throws IOException{
		return awsMain.uploadProfileImg(userId);
	}
}
