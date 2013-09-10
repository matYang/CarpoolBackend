package carpool.exception.transaction;

import carpool.common.Constants.transactionState;

public class TransactionStateViolationException extends Exception {

	private static final long serialVersionUID = 1L;
	
	private transactionState curState;
	private transactionState expectedState;
	
	public TransactionStateViolationException(transactionState curState, transactionState expectedState){
        super();
        this.curState = curState;
        this.expectedState = expectedState;
    }

    public int getCode() {
        return -1;
    }
    
    public transactionState getCurState(){
    	return curState;
    }
    
    public transactionState getExpectedState(){
    	return expectedState;
    }

}
