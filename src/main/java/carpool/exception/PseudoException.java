package carpool.exception;

public class PseudoException extends Exception{

	private static final long serialVersionUID = 1L;
	private String exceptionText;
	
	protected String exceptionType = "Pesudo";

	public PseudoException(){
        super();
        this.exceptionText = "不明错误，请稍后再试";
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
