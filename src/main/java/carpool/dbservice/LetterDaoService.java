package carpool.dbservice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import carpool.asyncRelayExecutor.ExecutorProvider;
import carpool.asyncTask.relayTask.LetterRelayTask;
import carpool.asyncTask.relayTask.NotificationRelayTask;
import carpool.carpoolDAO.CarpoolDaoLetter;
import carpool.common.DateUtility;
import carpool.constants.Constants.LetterDirection;
import carpool.constants.Constants.LetterType;
import carpool.exception.letter.LetterNotFoundException;
import carpool.exception.letter.LetterOwnerNotMatchException;
import carpool.exception.location.LocationNotFoundException;
import carpool.exception.user.UserNotFoundException;
import carpool.model.Letter;
import carpool.model.User;



public class LetterDaoService{


	public static ArrayList<Letter> getAllLetters() throws UserNotFoundException, LocationNotFoundException{

		return CarpoolDaoLetter.getAllLetters();
	}


	public static ArrayList<Letter> getUserLetters(int curUserId, int targetUserId, LetterType type, LetterDirection direction) throws UserNotFoundException, LocationNotFoundException{
		ArrayList<Letter> letters = CarpoolDaoLetter.getUserLetters(curUserId, targetUserId, type, direction);
		checkLetter(curUserId, targetUserId);
		return letters;
	}

	public static ArrayList<User> getLetterUsers(int userId) throws UserNotFoundException, LocationNotFoundException{

		return CarpoolDaoLetter.getLetterUsers(userId);
	}


	public static Letter getLetterById(int letterId) throws LetterNotFoundException, UserNotFoundException, LocationNotFoundException{

		return CarpoolDaoLetter.getLetterById(letterId);
	}


	public static Letter sendLetter(Letter letter) throws UserNotFoundException, LocationNotFoundException{
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

	public static ArrayList<Letter> sortLetters(ArrayList<Letter> list){
		Collections.sort(list, new Comparator<Letter>() {
			@Override public int compare(final Letter letter1, final Letter letter2) {
				return DateUtility.toSQLDateTime(letter1.getSend_time()).compareTo(DateUtility.toSQLDateTime(letter2.getSend_time()));
			}
		});
		return list;
	}

}

