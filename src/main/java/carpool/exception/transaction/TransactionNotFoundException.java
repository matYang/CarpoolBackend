package carpool.exception.transaction;

import carpool.exception.PseudoException;

public class TransactionNotFoundException extends PseudoException {

	private static final long serialVersionUID = 1L;
	
	protected String exceptionType = "TransactionNotFound";
	
	public TransactionNotFoundException(){
        super("对不起，您要找的交易不存在");
    }
	
	public TransactionNotFoundException(String exceptionText){
		super(exceptionText);
	}
	
	@Override
    public int getCode() {
        return 4;
    }

}