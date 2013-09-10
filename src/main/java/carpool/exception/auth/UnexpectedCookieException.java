package carpool.exception.auth;

public class UnexpectedCookieException extends Exception {

	private static final long serialVersionUID = 1L;


	public UnexpectedCookieException(){
        super();
    }


    public int getCode() {
        return -1;
    }
}