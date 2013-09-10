package badstudent.exception.auth;

public class DuplicateSessionCookieException extends Exception {

	private static final long serialVersionUID = 1L;


	public DuplicateSessionCookieException(){
        super();
    }

    public int getCode() {
        return -1;
    }
}