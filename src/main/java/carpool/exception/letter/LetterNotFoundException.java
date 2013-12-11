package carpool.exception.letter;

import carpool.exception.PseudoException;

public class LetterNotFoundException extends PseudoException {

	private static final long serialVersionUID = 1L;
	
	protected String exceptionType = "LetterNotFound";

	public LetterNotFoundException(){
        super("不好意思，您要找的站内信不存在");
    }
	
	public LetterNotFoundException(String exceptionText){
		super(exceptionText);
	}
	
	@Override
    public int getCode() {
        return 19;
    }
    
}