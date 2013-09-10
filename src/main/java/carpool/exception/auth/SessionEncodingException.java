package carpool.exception.auth;

public class SessionEncodingException extends Exception {

	private static final long serialVersionUID = 1L;

	public	SessionEncodingException(){
        super();
    }

    public int getCode() {
        return -1;
    }
}
