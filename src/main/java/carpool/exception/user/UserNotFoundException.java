package carpool.exception.user;

public class UserNotFoundException extends Exception {

	private static final long serialVersionUID = 1L;

	public UserNotFoundException(){
        super();
    }

    public int getCode() {
        return -1;
    }
    
}