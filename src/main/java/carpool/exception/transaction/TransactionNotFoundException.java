package carpool.exception.transaction;

public class TransactionNotFoundException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public TransactionNotFoundException(){
        super();
    }

    public int getCode() {
        return -1;
    }

}
