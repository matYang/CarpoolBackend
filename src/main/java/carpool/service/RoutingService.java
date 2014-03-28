package carpool.service;


import org.restlet.Application;
import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.routing.Router;

import carpool.common.DebugLog;
import carpool.configurations.ServerConfig;
import carpool.resources.adminResource.AdminRoutineResource;
import carpool.resources.adminResource.AdminStateChangeResource;
import carpool.resources.adminResource.AdminStatResource;
import carpool.resources.adminResource.AdminVerificationResource;
import carpool.resources.dianmingResource.*;
import carpool.resources.generalResource.*;
import carpool.resources.letterResource.LetterResource;
import carpool.resources.letterResource.LetterResourceId;
import carpool.resources.letterResource.LetterUserResource;
import carpool.resources.locationResource.*;
import carpool.resources.notificationResource.*;
import carpool.resources.transactionResource.*;
import carpool.resources.userResource.*;
import carpool.resources.userResource.userAuthResource.ChangePasswordResource;
import carpool.resources.userResource.userAuthResource.ForgetPasswordResource;
import carpool.resources.userResource.userAuthResource.LogInResource;
import carpool.resources.userResource.userAuthResource.LogOutResource;
import carpool.resources.userResource.userAuthResource.SessionRedirect;
import carpool.resources.userResource.userEmailResource.ResendActivationEmailResource;
import carpool.resources.userResource.userEmailResource.UserEmailActivationResource;
import carpool.resources.userResource.userEmailResource.UserEmailResource;


/**
 * This class is the collection of our routes, it is the only Application attached to the default host
 * **/
public class RoutingService extends Application {

	public RoutingService() {
		super();
	}

	public RoutingService(Context context) {
		super(context);
	}

	//@Override
	public synchronized Restlet createInboundRoot(){
		DebugLog.d("initiaing router::RoutingService");
		Router router = new Router(getContext());

		/** -------------------- APIs for dianming module -------------- **/
		String dMServicePrefix = "/dianming";
		//  API for dianming messages:  /api/v1.0/dianming/*

		String DMResourcePrefix = "/dianming";
		//	API for Get/Post dianming messages: /api/v1.0/dianming/dianming
		router.attach(ServerConfig.applicationPrefix + ServerConfig.versionPrefix + dMServicePrefix + DMResourcePrefix, DMResource.class);
		//	API for Get/Put/Delete dianming messages: /api/v1.0/dianming/dianming/:id
		router.attach(ServerConfig.applicationPrefix + ServerConfig.versionPrefix + dMServicePrefix + DMResourcePrefix + "/{id}", DMResourceId.class);
		String DMRecentResourcePrefix = "/recent";
		//	API for DM message getting recent messages: /api/v1.0/dianming/recent
		router.attach(ServerConfig.applicationPrefix + ServerConfig.versionPrefix + dMServicePrefix + DMRecentResourcePrefix, RecentMessageResource.class);
		String DMSearchResourcePrefix = "/search";
		//	API for DM message search: /api/v1.0/dianming/search
		router.attach(ServerConfig.applicationPrefix + ServerConfig.versionPrefix + dMServicePrefix + DMSearchResourcePrefix, DMSearchResource.class);
		String DMTransactionResourcePrefix = "/transaction";
		//	API for DM message fetchTransactionList : /api/v1.0/dianming/transaction/:id
		router.attach(ServerConfig.applicationPrefix + ServerConfig.versionPrefix + dMServicePrefix + DMTransactionResourcePrefix + "/{id}", DMTransactionResource.class);
		String DMAutoMatchResourcePrefix = "/autoMatch";
		//	API for DM message auto-matching : /api/v1.0/dianming/autoMatch/:id
		router.attach(ServerConfig.applicationPrefix + ServerConfig.versionPrefix + dMServicePrefix + DMAutoMatchResourcePrefix + "/{id}", DMAutoMatchResource.class);


		/** -------------------- APIs for transaction module -------------- **/
		String transactionServicePrefix = "/transaction";
		//  API for transaction:  /api/v1.0/transaction/*

		String TransactionResourcePrefix = "/transaction";
		//	API for Get/Post transactions: /api/v1.0/transaction/transaction
		router.attach(ServerConfig.applicationPrefix + ServerConfig.versionPrefix + transactionServicePrefix + TransactionResourcePrefix, TransactionResource.class);
		//	API for Get/Put/Delete transactions: /api/v1.0/transaction/transaction/:id
		router.attach(ServerConfig.applicationPrefix + ServerConfig.versionPrefix + transactionServicePrefix + TransactionResourcePrefix + "/{id}", TransactionResourceId.class);



		/** -------------------- APIs for notification module -------------- **/
		String notificationServicePrefix = "/notification";
		//  API for transaction:  /api/v1.0/notification/*

		String NotificationResourcePrefix = "/notification";
		//	API for Get notifications: /api/v1.0/notification/notification
		router.attach(ServerConfig.applicationPrefix + ServerConfig.versionPrefix + notificationServicePrefix + NotificationResourcePrefix, NotificationResource.class);
		//	API for Get/Put/Delete notification: /api/v1.0/notification/notification/:id
		router.attach(ServerConfig.applicationPrefix + ServerConfig.versionPrefix + notificationServicePrefix + NotificationResourcePrefix + "/{id}", NotificationResourceId.class);
		String NotificationDeleteByIdListResourcePrefix = "/notificationByIdList";
		//	API for Check/Delete notification by an id list: /api/v1.0/notification/notificationByIdList
		router.attach(ServerConfig.applicationPrefix + ServerConfig.versionPrefix + notificationServicePrefix + NotificationDeleteByIdListResourcePrefix, NotificationByIdListResource.class);

		/** -------------------- APIs for letter module -------------- **/
		String letterServicePrefix = "/letter";
		//  API for transaction:  /api/v1.0/letter/*

		String LetterResourcePrefix = "/letter";
		//	API for Get letters: /api/v1.0/letter/letter
		router.attach(ServerConfig.applicationPrefix + ServerConfig.versionPrefix + letterServicePrefix + LetterResourcePrefix, LetterResource.class);
		//	API for Get/Put/Delete letters: /api/v1.0/letter/letter/:id
		router.attach(ServerConfig.applicationPrefix + ServerConfig.versionPrefix + letterServicePrefix + LetterResourcePrefix + "/{id}", LetterResourceId.class);
		String LetterUserResourcePrefix = "/user";
		//	API for Get users in chat history: /api/v1.0/letter/user/:id
		router.attach(ServerConfig.applicationPrefix + ServerConfig.versionPrefix + letterServicePrefix + LetterUserResourcePrefix + "/{id}", LetterUserResource.class);


		/** -------------------- APIs for user module ------------------ **/
		String userServicePrefix = "/users";
		//  API for users:  /api/v1.0/users/*

		String SessionRedirectPrefix = "/findSession";
		//	API for session redirection upon non-session pages: /api/v1.0/users/findSession
		router.attach(ServerConfig.applicationPrefix + ServerConfig.versionPrefix + userServicePrefix + SessionRedirectPrefix, SessionRedirect.class);
		router.attach(ServerConfig.applicationPrefix + ServerConfig.versionPrefix + userServicePrefix + SessionRedirectPrefix + "/{id}", SessionRedirect.class);

		String UserResourcePrefix = "/user";
		//  API for Get/Post user:  /api/v1.0/users/user
		router.attach(ServerConfig.applicationPrefix + ServerConfig.versionPrefix + userServicePrefix + UserResourcePrefix, UserResource.class);
		//  API for Get/Post/Delete user:  /api/v1.0/users/user/:id
		router.attach(ServerConfig.applicationPrefix + ServerConfig.versionPrefix + userServicePrefix + UserResourcePrefix + "/{id}", UserResourceId.class);
		String LogInResourcePrefix = "/login";
		//  API for user Login:  /api/v1.0/users/login
		router.attach(ServerConfig.applicationPrefix + ServerConfig.versionPrefix + userServicePrefix + LogInResourcePrefix, LogInResource.class);
		String LogOutResourcePrefix = "/logout";
		//  API for user Logout:  /api/v1.0/users/logout/:id
		router.attach(ServerConfig.applicationPrefix + ServerConfig.versionPrefix + userServicePrefix + LogOutResourcePrefix + "/{id}", LogOutResource.class);
		String ImgResourcePrefix = "/img";
		//  API for user Image resources:  /api/v1.0/users/img/:id
		router.attach(ServerConfig.applicationPrefix + ServerConfig.versionPrefix + userServicePrefix + ImgResourcePrefix + "/{id}", UserImgResource.class);
		String EmailResourcePrefix = "/email";
		//  API for check email availability:  /api/v1.0/users/email
		//  API for user update email:  /api/v1.0/users/email/:id
		router.attach(ServerConfig.applicationPrefix + ServerConfig.versionPrefix + userServicePrefix + EmailResourcePrefix, UserEmailResource.class);
		//router.attach(CarpoolConfig.applicationPrefix + CarpoolConfig.versionPrefix + userServicePrefix + EmailResourcePrefix + "/{id}", UserEmailResourceId.class);
		String ChangePasswordResourcePrefix = "/changePassword";
		//	API for user changing the password:  /api/v1.0/users/changePassword/:id
		router.attach(ServerConfig.applicationPrefix + ServerConfig.versionPrefix + userServicePrefix + ChangePasswordResourcePrefix + "/{id}", ChangePasswordResource.class);
		String ContactInfoResourcePrefix = "/contactInfo";
		//	API for user changing the contact information:  /api/v1.0/users/contactInfo/:id
		router.attach(ServerConfig.applicationPrefix + ServerConfig.versionPrefix + userServicePrefix + ContactInfoResourcePrefix + "/{id}", UserChangeContactInfoResource.class);
		String SingleLocationResourcePrefix = "/singleLocation";
		//	API for user changing the single location:  /api/v1.0/users/singleLocation/:id
		router.attach(ServerConfig.applicationPrefix + ServerConfig.versionPrefix + userServicePrefix + SingleLocationResourcePrefix + "/{id}", UserSingleLocationResource.class);
		String EmailActivationResourcePrefix = "/emailActivation";
		//	API for user email activation: /api/v1.0/users/emailActivation
		router.attach(ServerConfig.applicationPrefix + ServerConfig.versionPrefix + userServicePrefix + EmailActivationResourcePrefix, UserEmailActivationResource.class);
		String ResendActivationEmailResourcePrefix = "/resendActivationEmail";
		//	API for sending activation email to target userId: /api/v1.0/users/resendActivationEmail/:id
		router.attach(ServerConfig.applicationPrefix + ServerConfig.versionPrefix + userServicePrefix + ResendActivationEmailResourcePrefix + "/{id}", ResendActivationEmailResource.class);
		String ForgetPasswordResourcePrefix = "/forgetPassword";
		//	API for user forgetting the password: /api/v1.0/users/forgetPassword
		router.attach(ServerConfig.applicationPrefix + ServerConfig.versionPrefix + userServicePrefix + ForgetPasswordResourcePrefix, ForgetPasswordResource.class);
		String UserToggleEmailNoticePrefix = "/toggleNotices";
		//	API for user to toggle email notice state: /api/v1.0/users/toggleNotices/:id
		router.attach(ServerConfig.applicationPrefix + ServerConfig.versionPrefix + userServicePrefix + UserToggleEmailNoticePrefix + "/{id}", UserToggleNoticesResource.class);
		String UserWatchUserResourcePrefix = "/watchUser";
		//	API for user watch/de-watch other users: /api/v1.0/users/watchUser/:id
		router.attach(ServerConfig.applicationPrefix + ServerConfig.versionPrefix + userServicePrefix + UserWatchUserResourcePrefix + "/{id}", UserWatchUserResource.class);
		String UserIsWatchedResourcePrefix = "/isWatched";
		//	API for user watch/de-watch other users: /api/v1.0/users/isWatched/:id
		router.attach(ServerConfig.applicationPrefix + ServerConfig.versionPrefix + userServicePrefix + UserIsWatchedResourcePrefix + "/{id}", UserIsWatchedResource.class);
		String UserSearchUserResourcePrefix = "/searchUser";
		//	API for user watch/de-watch other users: /api/v1.0/users/searchUser
		router.attach(ServerConfig.applicationPrefix + ServerConfig.versionPrefix + userServicePrefix + UserSearchUserResourcePrefix, UserSearchUserResource.class);
		String UserMessageHistoryResourcePrefix = "/messageHistory";
		//	API for user getting history messages: /api/v1.0/users/messageHistory/:id
		router.attach(ServerConfig.applicationPrefix + ServerConfig.versionPrefix + userServicePrefix + UserMessageHistoryResourcePrefix + "/{id}", UserMessageHistoryResource.class);
		String UserTransactionResourcePrefix = "/transaction";
		//	API for user getting transactions: /api/v1.0/users/transaction/:id
		router.attach(ServerConfig.applicationPrefix + ServerConfig.versionPrefix + userServicePrefix + UserTransactionResourcePrefix + "/{id}", UserTransactionResource.class);
		String UserNotificationResourcePrefix = "/notification";
		//	API for user getting history notification: /api/v1.0/users/notification/:id
		router.attach(ServerConfig.applicationPrefix + ServerConfig.versionPrefix + userServicePrefix + UserNotificationResourcePrefix + "/{id}", UserNotificationResource.class);
		String UserUncheckedNotificationResourcePrefix = "/uncheckedNotification";
		//	API for user getting unread notificaitons: /api/v1.0/users/uncheckedNotification/:id
		router.attach(ServerConfig.applicationPrefix + ServerConfig.versionPrefix + userServicePrefix + UserUncheckedNotificationResourcePrefix + "/{id}", UserUncheckedNotificationResource.class);
		String UserUncheckedLetterResourcePefix = "/uncheckedLetter";
		//	API for user getting unread letters: /api/v1.0/users/uncheckedLetter/:id
		router.attach(ServerConfig.applicationPrefix + ServerConfig.versionPrefix + userServicePrefix + UserUncheckedLetterResourcePefix + "/{id}", UserUncheckedLetterResource.class);
		String UserSearchHistoryResourcePrefix = "/searchHistory";
		// API for user getting history messages: /api/v1.0/users/searchHistory/:id
		router.attach(ServerConfig.applicationPrefix + ServerConfig.versionPrefix + userServicePrefix + UserSearchHistoryResourcePrefix + "/{id}", UserSearchHistoryResource.class);
		String UserDriverVerificationResourcePrefix = "/driverVerification";
		// API for user getting starting driver verification: /api/v1.0/users/driverVerification/:id
		router.attach(ServerConfig.applicationPrefix + ServerConfig.versionPrefix + userServicePrefix + UserDriverVerificationResourcePrefix  + "/{id}", UserDriverVerificationResource.class);
		String UserPassengerVerificationResourcePrefix = "/passengerVerification";
		// API for user getting starting passenger verification: /api/v1.0/users/passengerVerification/:id
		router.attach(ServerConfig.applicationPrefix + ServerConfig.versionPrefix + userServicePrefix + UserPassengerVerificationResourcePrefix  + "/{id}", UserPassengerVerificationResource.class);

		/** --------------------- APIs for general module (legacy from v0.9) ------------------ **/
		String generalServicePrefix = "/general";
		//   API for feedback module:  /api/v1.0/general/*

		String feedBackResourcePrefix = "/feedBack";
		//	 API for feed back: /api/v1.0/general/feedBack
		router.attach(ServerConfig.applicationPrefix + ServerConfig.versionPrefix + generalServicePrefix + feedBackResourcePrefix, FeedBackResource.class);


		/** APIs for location module**/
		String locationServicePrefix = "/location";
		//   API for location module:  /api/v1.0/location/*

		String locationDefaultResourcePrefix = "/default";
		//	 API for location default resources: /api/v1.0/location/default
		router.attach(ServerConfig.applicationPrefix + ServerConfig.versionPrefix + locationServicePrefix + locationDefaultResourcePrefix, LocationDefaultResource.class);



		/** --------------------- APIs for Administrator ------------------ **/
		String adminServicePrefix = "/admin";
		//   API for single messages:  /api/v1.0/admin/*

		String StateChangeResourcePrefix = "/stateChange";
		//	API for admin state changes actions on user/message/transaction: /api/v1.0/admin/stateChange
		router.attach(ServerConfig.applicationPrefix + ServerConfig.versionPrefix + adminServicePrefix + StateChangeResourcePrefix, AdminStateChangeResource.class);
		String RoutineResourcePrefix = "/routine";
		//	API for admin to force routine tasks to take place: /api/v1.0/admin/routine
		router.attach(ServerConfig.applicationPrefix + ServerConfig.versionPrefix + adminServicePrefix + RoutineResourcePrefix, AdminRoutineResource.class);
		String StatAnalysisPrefix = "/stat";
		//	API for admin to analyze statistic of data service: /api/v1.0/admin/stat		
		router.attach(ServerConfig.applicationPrefix + ServerConfig.versionPrefix + adminServicePrefix + StatAnalysisPrefix + "/{type}", AdminStatResource.class);
		String VerificationPrefix = "/verification";
		//	API for admin to verification management: /api/v1.0/admin/verification		
		router.attach(ServerConfig.applicationPrefix + ServerConfig.versionPrefix + adminServicePrefix + VerificationPrefix, AdminVerificationResource.class);

		return router;
	}


}
