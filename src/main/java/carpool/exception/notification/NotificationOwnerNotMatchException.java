package carpool.exception.notification;

public class NotificationOwnerNotMatchException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public NotificationOwnerNotMatchException(){
        super();
    }

    public int getCode() {
        return -1;
    }

}
