package carpool.exception.validation;

import carpool.exception.PseudoException;

public class UnacceptableSearchStateException extends PseudoException {

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