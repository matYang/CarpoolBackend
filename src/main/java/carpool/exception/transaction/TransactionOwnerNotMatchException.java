package carpool.exception.transaction;

import carpool.exception.PesudoException;

public class TransactionOwnerNotMatchException extends PesudoException {

	private static final long serialVersionUID = 1L;
	
	protected String exceptionType = "TransactionOwnerNotMatchFound";
	
	public TransactionOwnerNotMatchException(){
        super();
    }
	
	public TransactionOwnerNotMatchException(String exceptionText){
		super(exceptionText);
	}
	
	@Override
    public int getCode() {
        return 5;
    }

}