package carpool.exception.transaction;

import carpool.exception.PesudoException;

public class TransactionNotFoundException extends PesudoException {

	private static final long serialVersionUID = 1L;
	
	protected String exceptionType = "TransactionNotFound";
	
	public TransactionNotFoundException(){
        super();
    }
	
	public TransactionNotFoundException(String exceptionText){
		super(exceptionText);
	}
	
	@Override
    public int getCode() {
        return 4;
    }

}