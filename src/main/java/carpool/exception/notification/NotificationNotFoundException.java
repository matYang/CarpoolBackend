package carpool.exception.notification;

public class NotificationNotFoundException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public NotificationNotFoundException(){
        super();
    }

    public int getCode() {
        return -1;
    }

}
