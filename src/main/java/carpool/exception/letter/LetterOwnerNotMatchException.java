package carpool.exception.letter;

import carpool.exception.PseudoException;

public class LetterOwnerNotMatchException extends PseudoException {

	private static final long serialVersionUID = 1L;
	
	protected String exceptionType = "LetterOwnerNotMatchFound";

	public LetterOwnerNotMatchException(){
        super();
    }
	
	public LetterOwnerNotMatchException(String exceptionText){
		super(exceptionText);
	}
	
	@Override
    public int getCode() {
        return 20;
    }
    
}