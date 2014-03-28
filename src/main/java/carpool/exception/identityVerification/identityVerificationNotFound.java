package carpool.exception.identityVerification;

import carpool.exception.PseudoException;

public class identityVerificationNotFound extends PseudoException {

	protected String exceptionType = "IdentityVerificationNotFound";

	public identityVerificationNotFound(){
        super("对不起，您要找的用户认证不存在");
    }
	
	public identityVerificationNotFound(String exceptionText){
        super(exceptionText);
    }

	@Override
    public int getCode() {
        return 21;
    }
}
