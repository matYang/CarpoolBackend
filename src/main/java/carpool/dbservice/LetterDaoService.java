package carpool.dbservice;

import java.util.ArrayList;

import carpool.asyncRelayExecutor.ExecutorProvider;
import carpool.asyncTask.relayTask.LetterRelayTask;
import carpool.asyncTask.relayTask.NotificationRelayTask;
import carpool.constants.Constants.LetterDirection;
import carpool.constants.Constants.LetterType;
import carpool.exception.letter.LetterNotFoundException;
import carpool.exception.letter.LetterOwnerNotMatchException;
import carpool.model.Letter;



public class LetterDaoService{

	
	public static ArrayList<Letter> getAllLetters(){
		
		return null;
	}
	
	
	public static ArrayList<Letter> getUserLetter(int curUserId, int targetUserId, LetterType type, LetterDirection direction){
		
		return null;
	}
	
	public static ArrayList<User> getLetterUsers(int userId) throws UserNotFoundException{
		
		return null;
	}
	
	
	public static Letter getLetterById(int letterId) throws LetterNotFoundException{
		
		return null;
	}
	
	
	public static Letter sendLetter(Letter letter){
		LetterRelayTask lTask = new LetterRelayTask(letter);
        ExecutorProvider.executeRelay(lTask);
        
        
        //TODO dao
		return null;
	}
	
	
	public static Letter checkLetter(int userId, int originLetterId) throws LetterNotFoundException, LetterOwnerNotMatchException{
		
		return null;
	};
	
	
	
	public static void deleteLetter(int letterId) throws LetterNotFoundException, LetterOwnerNotMatchException{
		
	}
	
	
}

