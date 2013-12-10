package carpool.exception.transaction;

import carpool.exception.PseudoException;

public class TransactionOwnerNotMatchException extends PseudoException {

	private static final long serialVersionUID = 1L;
	
	protected String exceptionType = "TransactionOwnerNotMatchFound";
	
	public TransactionOwnerNotMatchException(){
        super("对不起，您没有对该交易执行该操作的权限");
    }
	
	public TransactionOwnerNotMatchException(String exceptionText){
		super(exceptionText);
	}
	
	@Override
    public int getCode() {
        return 5;
    }

}