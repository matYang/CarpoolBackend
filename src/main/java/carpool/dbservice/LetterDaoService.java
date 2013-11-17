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

	
	public ArrayList<Letter> getAllLetters(){
		
		return null;
	}
	
	
	public ArrayList<Letter> getUserLetter(int curUserId, int targetUserId, LetterType type, LetterDirection direction){
		
		return null;
	}
	
	
	public Letter getLetterById(int letterId) throws LetterNotFoundException{
		
		return null;
	}
	
	
	public Letter sendLetter(Letter letter){
		LetterRelayTask lTask = new LetterRelayTask(letter);
        ExecutorProvider.executeRelay(lTask);
        
        
        //TODO dao
		return null;
	}
	
	
	public Letter checkLetter(int userId, int originLetterId) throws LetterNotFoundException, LetterOwnerNotMatchException{
		
		return null;
	};
	
	
	
	public void deleteLetter(int letterId) throws LetterNotFoundException, LetterOwnerNotMatchException{
		
	}
	
	
}

