package carpool.dbservice;

import java.util.ArrayList;

import carpool.asyncRelayExecutor.ExecutorProvider;
import carpool.asyncTask.relayTask.LetterRelayTask;
import carpool.asyncTask.relayTask.NotificationRelayTask;
import carpool.carpoolDAO.CarpoolDaoLetter;
import carpool.constants.Constants.LetterDirection;
import carpool.constants.Constants.LetterType;
import carpool.exception.letter.LetterNotFoundException;
import carpool.exception.letter.LetterOwnerNotMatchException;
import carpool.exception.user.UserNotFoundException;
import carpool.model.Letter;
import carpool.model.User;



public class LetterDaoService{


	public static ArrayList<Letter> getAllLetters() throws UserNotFoundException{

		return CarpoolDaoLetter.getAllLetters();
	}


	public static ArrayList<Letter> getUserLetters(int curUserId, int targetUserId, LetterType type, LetterDirection direction) throws UserNotFoundException{
		ArrayList<Letter> letters = CarpoolDaoLetter.getUserLetters(curUserId, targetUserId, type, direction);
		checkLetter(curUserId, targetUserId);
		return letters;
	}

	public static ArrayList<User> getLetterUsers(int userId) throws UserNotFoundException{

		return CarpoolDaoLetter.getLetterUsers(userId);
	}


	public static Letter getLetterById(int letterId) throws LetterNotFoundException, UserNotFoundException{

		return CarpoolDaoLetter.getLetterById(letterId);
	}


	public static Letter sendLetter(Letter letter) throws UserNotFoundException{
		LetterRelayTask lTask = new LetterRelayTask(letter);
		ExecutorProvider.executeRelay(lTask);
		return CarpoolDaoLetter.addLetterToDatabases(letter);
	}


	public static void checkLetter(int userId, int targetUserId){
		CarpoolDaoLetter.checkLetter(userId, targetUserId);
	}



	public static void deleteLetter(int letterId) throws LetterNotFoundException, LetterOwnerNotMatchException{
		CarpoolDaoLetter.deleteLetter(letterId);
	}


}

