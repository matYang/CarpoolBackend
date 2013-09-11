package carpool.exception;

public class PseudoException extends Exception{

	private static final long serialVersionUID = 1L;
	private String exceptionText;
	
	protected String exceptionType = "Pesudo";

	public PseudoException(){
        super();
        this.exceptionText = "Unspecified";
    }
	
	public PseudoException(String exceptionText){
		super();
		this.exceptionText = exceptionText;
	}
	
	public String getExeceptionType(){
		return this.exceptionType;
	}
	
	public String getExceptionText(){
		return this.exceptionText;
	}
	
	public int getCode() {
        return 0;
    }

}
