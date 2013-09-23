package carpool.common;

import java.util.ArrayList;

import carpool.model.Message;
import carpool.model.User;

public class HelperOperator {

	public static void removeFromSocialList(ArrayList<User> list,int id){
		for(User user : list){
			if(user.getUserId()==id){
				list.remove(user);
				break;
			}
		}
	}

	public static void removeFromWatchList(ArrayList<Message> list,int id){
		for(Message msg : list){
			if(msg.getMessageId()==id){
				list.remove(msg);
				break;
			}
		}
	}

	/**
	 * removes the image at the image path
	 * @param imgPath
	 * @return	true if removed, false if not
	 */
	public static boolean removePreviousImg(String imgPath) {
		// TODO
		return false;
	}

}
