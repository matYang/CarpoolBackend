package carpool.exception.letter;

import carpool.exception.PseudoException;

public class LetterOwnerNotMatchException extends PseudoException {

	private static final long serialVersionUID = 1L;
	
	protected String exceptionType = "LetterOwnerNotMatchFound";

	public LetterOwnerNotMatchException(){
        super("对不起，您没有对该站内信执行该操作的权限");
    }
	
	public LetterOwnerNotMatchException(String exceptionText){
		super(exceptionText);
	}
	
	@Override
    public int getCode() {
        return 20;
    }
    
}