package badstudent.exception.message;

public class MessageNotFoundException extends Exception {

	private static final long serialVersionUID = 1L;

	public MessageNotFoundException(){
        super();
    }

    public int getCode() {
        return -1;
    }
    
}