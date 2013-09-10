package carpool.exception.auth;

public class UnacceptableSearchStateException extends Exception {

	private static final long serialVersionUID = 1L;


	public UnacceptableSearchStateException(){
        super();
    }


    public int getCode() {
        return -1;
    }
}