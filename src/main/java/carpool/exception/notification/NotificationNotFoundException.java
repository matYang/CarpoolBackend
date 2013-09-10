package carpool.exception.notification;

import carpool.exception.PesudoException;

public class NotificationNotFoundException extends PesudoException {

	private static final long serialVersionUID = 1L;
	
	protected String exceptionType = "NotificationNotFound";
	
	public NotificationNotFoundException(){
        super();
    }
	
	public NotificationNotFoundException(String exceptionText){
		super(exceptionText);
	}
	
	@Override
    public int getCode() {
        return 8;
    }

}