package carpool.configurations;

public class ValidationConfig {

	public static final int qqMaxLength = 10;
	public static final int qqMinLength = 5;
	public static final String RegexPwPattern = "[A-Za-z0-9!@#$%^&*?-_+=]*";
	public static final String RegexNameWhiteSpacePattern = "\\S*\\s\\S*";
	public static final String RegexNamePattern = "[\u4e00-\u9fa5|A-za-z]*";
	public static final String RegexEmailPattern = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
	public static final int maxEmailLength = 50;
	public static final int maxUserNameLength = 18;
	public static final int minPasswordLength = 6;
	public static final int maxPasswordLength = 30;
	public static final long max_feedBackLength = 200000l;
	public static final long max_PostLength = 819200l;
	public static final long max_FileLength = 81920000l;

}
