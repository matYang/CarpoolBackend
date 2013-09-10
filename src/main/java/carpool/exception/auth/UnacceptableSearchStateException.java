package carpool.exception.auth;

import carpool.exception.PesudoException;

public class UnacceptableSearchStateException extends PesudoException {

	private static final long serialVersionUID = 1L;

	protected String exceptionType = "UnacceptableSearchState";

	public UnacceptableSearchStateException(){
        super();
    }
	
	public UnacceptableSearchStateException(String exceptionText){
		super(exceptionText);
	}
	
	@Override
    public int getCode() {
        return 12;
    }
	
}