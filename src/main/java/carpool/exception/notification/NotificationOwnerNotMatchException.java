package carpool.exception.notification;

import carpool.exception.PseudoException;

public class NotificationOwnerNotMatchException extends PseudoException {

	private static final long serialVersionUID = 1L;
	
	protected String exceptionType = "NotificationOwnerNotMatch";
	
	public NotificationOwnerNotMatchException(){
        super("对不起，您没有对该提醒执行该操作的权限");
    }
	
	public NotificationOwnerNotMatchException(String exceptionText){
		super(exceptionText);
	}
	
	@Override
    public int getCode() {
        return 9;
    }

}