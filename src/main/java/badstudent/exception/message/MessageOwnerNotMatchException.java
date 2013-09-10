package badstudent.exception.message;

public class MessageOwnerNotMatchException extends Exception {

	private static final long serialVersionUID = 1L;

	public MessageOwnerNotMatchException(){
        super();
    }

    public int getCode() {
        return -1;
    }
    
}