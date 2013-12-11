package carpool.exception.notification;

import carpool.exception.PseudoException;

public class NotificationNotFoundException extends PseudoException {

	private static final long serialVersionUID = 1L;
	
	protected String exceptionType = "NotificationNotFound";
	
	public NotificationNotFoundException(){
        super("对不起，您要找的提醒不存在");
    }
	
	public NotificationNotFoundException(String exceptionText){
		super(exceptionText);
	}
	
	@Override
    public int getCode() {
        return 8;
    }

}