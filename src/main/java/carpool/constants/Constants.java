package carpool.constants;

import carpool.mappings.AllProvinceMappings;
import carpool.mappings.MappingBase;


public class Constants {

    public static final int max_recents = 3;
    public static final int max_displayRecents = 4;
    public static final long max_userLength = 4194304l;
    public static final long max_feedBackLength = 200000l;
	public static final long max_DMMessageLength = 819200l;
	public static final long max_TransactionLength = 400000l;
	public static final long max_NotificationLength = 300000l;
	public static final long max_notificationLifeInMili = 604800000l;
    public static final String key_message_prefix = "message-";
    public static final String key_idGenerator = "idGenerator";
    public static final String key_province = "province";
    public static final String key_recents = "recentMessages";
    public static final String key_infoChanged = "infoChanged";
    public static final String key_emailActivationAuth = "ea";
    public static final String key_forgetPasswordAuth = "fp";
    //public static final String domainName = "www.huaixuesheng.com";
    public static final String domainName = "localhost:8015";
    
    public static final String cookie_userSession = "userSessionCookie";
    public static final int cookie_maxAge = 5184000; //2 month
    
    public static final MappingBase ALL_PROVINCE = new AllProvinceMappings();


    public static final int min_DMMessageHourPrice = 1;
    public static final int max_DMMessageHourPrice = 999;

    //time stamp on the session will be updated if it is 3 days old
    public static final long session_updateThreshould = 259200000l;
    //time stamp 7 days old would result in failure of login
    public static final long session_expireThreshould = 604800000l;

    public static final long A_WEEK = 604800000l; //miliSec in a week

    //the temporary admin access code, admin access will be checked against this code instead of user cookies
    public static final String access_admin = "4rkozalh48z1";

    public static enum messageType{
    	invalid(-1),ask(0),help(1);
        public int code;
        messageType(int code){
            this.code = code;
        }
        private final static messageType[] map = messageType.values();
        public static messageType fromInt(int n){
            return map[n+1];
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
    //below are used to specify events under state sections, below are the event states that will trigger notifications and be used in Transaction states
    //note these event states are extremely important as they trigger notification types. Strict use-style should be enforced
    public static enum transactionState{
        init(0), confirm(1), refused(2), aboutToStart(3), cancelled(4), finishedToEvaluate(5), underInvestigation(6), success_noEvaluation(7), success_initUserEvaluated(8), success_targetUserEvaluated(9), success(10);
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
    	confirm(0), refuse(1), cancel(2), report(3), evaluate(4);
        public int code;
        transactionStateChangeAction(int code){
            this.code = code;
        }
        private final static transactionStateChangeAction[] map = transactionStateChangeAction.values();
        public static transactionStateChangeAction fromInt(int n){
            return map[n];
        }
    }

    public static enum transactionStateChangeAdminAction{
    	investigation_cancel(0), investigation_release(1);
    	public int code;
    	transactionStateChangeAdminAction(int code){
            this.code = code;
        }
        private final static transactionStateChangeAdminAction[] map = transactionStateChangeAdminAction.values();
        public static transactionStateChangeAdminAction fromInt(int n){
            return map[n];
        }
    }


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
    	invalid(-1),normal(0);
        public int code;
        userState(int code){
            this.code = code;
        }
        private final static userState[] map = userState.values();
        public static userState fromInt(int n){
            return map[n+1];
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
    	deleted(-2),expired(-1),normal(0);
        public int code;
        messageState(int code){
            this.code = code;
        }
        private final static messageState[] map = messageState.values();
        public static messageState fromInt(int n){
            return map[n+2];
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


}
