package carpool.constants;



public class Constants {

    public static final long A_WEEK = 604800000l; //miliSec in a week


    public static enum messageType{
    	ask(0),help(1), both(2);
        public int code;
        messageType(int code){
            this.code = code;
        }
        private final static messageType[] map = messageType.values();
        public static messageType fromInt(int n){
            return map[n];
        }
    }
    public static enum gender{
        male(0),female(1),both(2);
        public int code;
        gender(int code){
            this.code = code;
        }
        private final static gender[] map = gender.values();
        public static gender fromInt(int n){
            return map[n];
        }
    }

    public static enum paymentMethod{
        offline(0),paypal(1),all(2);
        public int code;
        paymentMethod(int code){
            this.code = code;
        }
        private final static paymentMethod[] map = paymentMethod.values();
        public static paymentMethod fromInt(int n){
            return map[n];
        }
    }

    //notifications and transactions are more time-sensitive, their states are more related to time and user interactions, event states will be used for their states
    public static enum transactionState{
        init(0), cancelled(1), aboutToStart(2), finished(3), underInvestigation(4), invalid(5);
        public int code;
        transactionState(int code){
            this.code = code;
        }
        private final static transactionState[] map = transactionState.values();
        public static transactionState fromInt(int n){
            return map[n];
        }
    }

    public static enum transactionStateChangeAction{
    	init(0), cancel(1), report(2), evaluate(3);
        public int code;
        transactionStateChangeAction(int code){
            this.code = code;
        }
        private final static transactionStateChangeAction[] map = transactionStateChangeAction.values();
        public static transactionStateChangeAction fromInt(int n){
            return map[n];
        }
    }

    public static enum NotificationEvent{
        transactionInit(0), transactionCancelled(1), transactionAboutToStart(2),
        transactionEvaluated(3), tranasctionUnderInvestigation(4), transactionReleased(5), watched(6), newLetter(7);
        public int code;
        NotificationEvent(int code){
            this.code = code;
        }
        private final static NotificationEvent[] map = NotificationEvent.values();
        public static NotificationEvent fromInt(int n){
            return map[n];
        }
    }
    
    public static enum NotificationState{
        unread(0), read(1);
        public int code;
        NotificationState(int code){
            this.code = code;
        }
        private final static NotificationState[] map = NotificationState.values();
        public static NotificationState fromInt(int n){
            return map[n];
        }
    }


    //states of the user account, more states reserved for future uses
    public static enum userState{
    	normal(0);
        public int code;
        userState(int code){
            this.code = code;
        }
        private final static userState[] map = userState.values();
        public static userState fromInt(int n){
            return map[n];
        }
    }

    public static enum userSearchState{
        universityAsk(0), universityHelp(1), regionAsk(2), regionHelp(3), universityGroupAsk(4), universityGroupHelp(5);
        public int code;
        userSearchState(int code){
            this.code = code;
        }
        private final static userSearchState[] map = userSearchState.values();
        public static userSearchState fromInt(int n){
            return map[n];
        }
    }
    //states of the message, more states reserved for future uses
    public static enum messageState{
    	deleted(0),expired(1),normal(2);
        public int code;
        messageState(int code){
            this.code = code;
        }
        private final static messageState[] map = messageState.values();
        public static messageState fromInt(int n){
            return map[n];
        }
    }
    
    public static enum TransactionType{
    	departure(0), arrival(1);
    	public int code;
    	TransactionType(int code){
            this.code = code;
        }
        private final static TransactionType[] map = TransactionType.values();
        public static TransactionType fromInt(int n){
            return map[n];
        }
    }
    
    public static enum DayTimeSlot{
    	all(0), morning(1), afternoon(2), night(3), specific(4);
    	public int code;
    	DayTimeSlot(int code){
            this.code = code;
        }
        private final static DayTimeSlot[] map = DayTimeSlot.values();
        public static DayTimeSlot fromInt(int n){
            return map[n];
        }
    }
    
    public static enum LetterType{
    	user(0), system(1);
    	public int code;
    	LetterType(int code){
            this.code = code;
        }
        private final static LetterType[] map = LetterType.values();
        public static LetterType fromInt(int n){
            return map[n];
        }
    }
    
    public static enum LetterState{
    	unread(0), read(1), invalid(2);
    	public int code;
    	LetterState(int code){
    		this.code = code;
    	}
    	private final static LetterState[] map = LetterState.values();
    	public static LetterState fromInt(int n){
    		return map[n];
    	}
    }
    
    public static enum LetterDirection{
    	inbound(0), outbound(1), both(2);
    	public int code;
    	LetterDirection(int code){
    		this.code = code;
    	}
    	private final static LetterDirection[] map = LetterDirection.values();
    	public static LetterDirection fromInt(int n){
    		return map[n];
    	}
    }


    /*API level constants*/
    public static final int category_DM = 0;
    public static final String applicationPrefix = "/api";
    public static final String versionPrefix = "/v1.0";
    public static final int maxNameLength = 50;
    public static final int fixedPhoneLength = 11;
    public static final int maxEmailLength = 50;
    public static final int maxQqLength = 50;
    public static final int maxUserNameLength = 30;
    public static final int maxPasswordLength = 30;

    public static final String goofyPasswordTrickHackers = "o god you are so gay";

    public static long max_imageSize = 83886080l;
	
    
    public static final String log_errKeyword = "ERROR!:";
	
	/** -------------------Administrator-------------------**/
    //the temporary admin access code, admin access will be checked against this code instead of user cookies
    public static final String access_admin = "4rkozalh48z1";
    
    public static enum AdminRoutineAction{
    	clearBothDatabase(0), messageClean(1), transactionMonitor(2), cleanAndMonitor(3);
    	public int code;
    	AdminRoutineAction(int code){
            this.code = code;
        }
        private final static AdminRoutineAction[] map = AdminRoutineAction.values();
        public static AdminRoutineAction fromInt(int n){
            return map[n];
        }
    }

}
