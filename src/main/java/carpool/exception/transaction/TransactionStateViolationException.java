package carpool.exception.transaction;

import carpool.constants.Constants.transactionState;
import carpool.exception.PseudoException;

public class TransactionStateViolationException extends PseudoException {

	private static final long serialVersionUID = 1L;
	
	private transactionState curState;
	private transactionState expectedState;
	
	protected String exceptionType = "TransactionStateViolation";
	
	public TransactionStateViolationException(transactionState curState, transactionState expectedState){
        super();
        this.curState = curState;
        this.expectedState = expectedState;
    }
	
	public TransactionStateViolationException(transactionState curState, transactionState expectedState, String exceptionText){
        super(exceptionText);
        this.curState = curState;
        this.expectedState = expectedState;
    }
	
	@Override
    public int getCode() {
        return 7;
    }
    
    public transactionState getCurState(){
    	return curState;
    }
    
    public transactionState getExpectedState(){
    	return expectedState;
    }

}
