package carpool.exception.transaction;

public class TransactionOwnerNotMatchException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public TransactionOwnerNotMatchException(){
        super();
    }

    public int getCode() {
        return -1;
    }

}
