package carpool.exception.transaction;

import carpool.constants.Constants.TransactionState;
import carpool.exception.PseudoException;

public class TransactionStateViolationException extends PseudoException {

	private static final long serialVersionUID = 1L;
	
	private TransactionState curState;
	private TransactionState expectedState;
	
	protected String exceptionType = "TransactionStateViolation";
	
	public TransactionStateViolationException(TransactionState curState, TransactionState expectedState){
        super("交易状态冲突，当前交易状态： " + curState.toString() + ", 预期交易状态: " + expectedState.toString());
        this.curState = curState;
        this.expectedState = expectedState;
    }
	
	public TransactionStateViolationException(TransactionState curState, TransactionState expectedState, String exceptionText){
        super(exceptionText);
        this.curState = curState;
        this.expectedState = expectedState;
    }
	
	@Override
    public int getCode() {
        return 7;
    }
    
    public TransactionState getCurState(){
    	return curState;
    }
    
    public TransactionState getExpectedState(){
    	return expectedState;
    }

}
