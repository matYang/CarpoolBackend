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

    //public static enum transactionStateChangeAdminAction{
    //	investigation_cancel(0), investigation_release(1);
    //	public int code;
    //	transactionStateChangeAdminAction(int code){
    //        this.code = code;
    //    }
    //    private final static transactionStateChangeAdminAction[] map = transactionStateChangeAdminAction.values();
    //    public static transactionStateChangeAdminAction fromInt(int n){
    //        return map[n];
    //    }
    //}


    public static enum  notificationType{
    	on_user(0), on_message(1), on_transaction(2);
    	public int code;
    	notificationType(int code){
    		this.code = code;
    	}
    	private final static notificationType[] map = notificationType.values();
        public static notificationType fromInt(int n){
            return map[n];
        }
    }


    //below are used in Notification only, not in Transaction
    //note: followerNewPost should be categorized into on_message, and thus a messageId should be passed into Notificaiton constructor
    public static enum notificationEvent{
        messageWatched(0),watchingMessageModified(1),watchingMessageDeleted(2),watchingMessageAboutToExpire(3),
        followerNewPost(4),followed(5),transactionPending(6), transactionConfrimed(7), transactionRefused(8),
        transactionAboutToStart(9), transactionCancelled(10), transactionFinishedToEvaluate(11), tranasctionUnderInvestigation(12),
        transactionEvaluated(13), transactionAboutToAutoMark(14), transactionAutoMarked(15), transactionReleased(16);
        public int code;
        notificationEvent(int code){
            this.code = code;
        }
        private final static notificationEvent[] map = notificationEvent.values();
        public static notificationEvent fromInt(int n){
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

    //TODO
    public static long max_imageSize = 83886080l;
	public static String imagePathPrefix = "../../var/www/userImages/profile-";
	public static String imagePathSufix = ".png";
	
	
	
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
