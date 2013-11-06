package carpool.service;


import org.restlet.Application;
import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.routing.Router;

import carpool.common.DebugLog;
import carpool.constants.Constants;
import carpool.resources.adminResource.AdminRoutineResource;
import carpool.resources.adminResource.AdminStateChangeResource;
import carpool.resources.dianmingResource.*;
import carpool.resources.generalResource.*;
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
import carpool.resources.userResource.userEmailResource.UserEmailResourceId;


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
		router.attach(Constants.applicationPrefix + Constants.versionPrefix + dMServicePrefix + DMResourcePrefix, DMResource.class);
		//	API for Get/Put/Delete dianming messages: /api/v1.0/dianming/dianming/:id
		router.attach(Constants.applicationPrefix + Constants.versionPrefix + dMServicePrefix + DMResourcePrefix + "/{id}", DMResourceId.class);
		String DMRecentResourcePrefix = "/recent";
		//	API for DM message change gender: /api/v1.0/dianming/recent
		router.attach(Constants.applicationPrefix + Constants.versionPrefix + dMServicePrefix + DMRecentResourcePrefix, RecentMessageResource.class);
		String DMSearchResourcePrefix = "/search";
		//	API for DM message change gender: /api/v1.0/dianming/search
		router.attach(Constants.applicationPrefix + Constants.versionPrefix + dMServicePrefix + DMSearchResourcePrefix, DMSearchResource.class);
		String DMTransactionResourcePrefix = "/transaction";
		//	API for DM message change gender: /api/v1.0/dianming/transacion/:id
		router.attach(Constants.applicationPrefix + Constants.versionPrefix + dMServicePrefix + DMTransactionResourcePrefix, DMTransactionResource.class);
		
		
		/** -------------------- APIs for transaction module -------------- **/
		String transactionServicePrefix = "/transaction";
		//  API for transaction:  /api/v1.0/transaction/*
		
		String TransactionResourcePrefix = "/transaction";
		//	API for Get/Post transactions: /api/v1.0/transaction/transaction
		router.attach(Constants.applicationPrefix + Constants.versionPrefix + transactionServicePrefix + TransactionResourcePrefix, TransactionResource.class);
		//	API for Get/Put/Delete transactions: /api/v1.0/transaction/transaction/:id
		router.attach(Constants.applicationPrefix + Constants.versionPrefix + transactionServicePrefix + TransactionResourcePrefix + "/{id}", TransactionResourceId.class);

		
		
		/** -------------------- APIs for notification module -------------- **/
		String notificationServicePrefix = "/notification";
		//  API for transaction:  /api/v1.0/notification/*
		
		String NotificationResourcePrefix = "/notification";
		//	API for Get transactions: /api/v1.0/notification/notification
		router.attach(Constants.applicationPrefix + Constants.versionPrefix + notificationServicePrefix + NotificationResourcePrefix, NotificationResource.class);
		//	API for Get/Put/Delete notification: /api/v1.0/notification/notification/:id
		router.attach(Constants.applicationPrefix + Constants.versionPrefix + notificationServicePrefix + NotificationResourcePrefix + "/{id}", NotificationResourceId.class);
		
		
		
		/** -------------------- APIs for user module ------------------ **/
		String userServicePrefix = "/users";
		//  API for users:  /api/v1.0/users/*
		
		String SessionRedirectPrefix = "/findSession";
		//	API for session redirection upon non-session pages: /api/v1.0/users/findSession
		router.attach(Constants.applicationPrefix + Constants.versionPrefix + userServicePrefix + SessionRedirectPrefix, SessionRedirect.class);
		
		String UserResourcePrefix = "/user";
		//  API for Get/Post user:  /api/v1.0/users/user
		router.attach(Constants.applicationPrefix + Constants.versionPrefix + userServicePrefix + UserResourcePrefix, UserResource.class);
		//  API for Get/Post/Delete user:  /api/v1.0/users/user/:id
		router.attach(Constants.applicationPrefix + Constants.versionPrefix + userServicePrefix + UserResourcePrefix + "/{id}", UserResourceId.class);
		String LogInResourcePrefix = "/login";
		//  API for user Login:  /api/v1.0/users/login
		router.attach(Constants.applicationPrefix + Constants.versionPrefix + userServicePrefix + LogInResourcePrefix, LogInResource.class);
		String LogOutResourcePrefix = "/logout";
		//  API for user Logout:  /api/v1.0/users/logout/:id
		router.attach(Constants.applicationPrefix + Constants.versionPrefix + userServicePrefix + LogOutResourcePrefix + "/{id}", LogOutResource.class);
		String ImgResourcePrefix = "/img";
		//  API for user Image resources:  /api/v1.0/users/img/:id
		router.attach(Constants.applicationPrefix + Constants.versionPrefix + userServicePrefix + ImgResourcePrefix + "/{id}", ImgResource.class);
		String EmailResourcePrefix = "/email";
		//  API for check email availability:  /api/v1.0/users/email
		//  API for user update email:  /api/v1.0/users/email/:id
		router.attach(Constants.applicationPrefix + Constants.versionPrefix + userServicePrefix + EmailResourcePrefix, UserEmailResource.class);
		router.attach(Constants.applicationPrefix + Constants.versionPrefix + userServicePrefix + EmailResourcePrefix + "/{id}", UserEmailResourceId.class);
		String ChangePasswordResourcePrefix = "/changePassword";
		//	API for user changing the password:  /api/v1.0/users/changePassword/:id
		router.attach(Constants.applicationPrefix + Constants.versionPrefix + userServicePrefix + ChangePasswordResourcePrefix + "/{id}", ChangePasswordResource.class);
		String ContactInfoResourcePrefix = "/contactInfo";
		//	API for user changing the contact information:  /api/v1.0/users/contactInfo/:id
		router.attach(Constants.applicationPrefix + Constants.versionPrefix + userServicePrefix + ContactInfoResourcePrefix + "/{id}", UserContactResource.class);
		String SingleLocationResourcePrefix = "/singleLocation";
		//	API for user changing the single location:  /api/v1.0/users/singleLocation/:id
		router.attach(Constants.applicationPrefix + Constants.versionPrefix + userServicePrefix + SingleLocationResourcePrefix + "/{id}", UserSingleLocationResource.class);
		String EmailActivationResourcePrefix = "/emailActivation";
		//	API for user email activation: /api/v1.0/users/emailActivation
		router.attach(Constants.applicationPrefix + Constants.versionPrefix + userServicePrefix + EmailActivationResourcePrefix, UserEmailActivationResource.class);
		String ResendActivationEmailResourcePrefix = "/resendActivationEmail";
		//	API for sending activation email to target userId: /api/v1.0/users/resendActivationEmail/:id
		router.attach(Constants.applicationPrefix + Constants.versionPrefix + userServicePrefix + ResendActivationEmailResourcePrefix + "/{id}", ResendActivationEmailResource.class);
		String ForgetPasswordResourcePrefix = "/forgetPassword";
		//	API for user forgetting the password: /api/v1.0/users/forgetPassword
		router.attach(Constants.applicationPrefix + Constants.versionPrefix + userServicePrefix + ForgetPasswordResourcePrefix, ForgetPasswordResource.class);
		String UserToggleEmailNoticePrefix = "/toggleNotices";
		//	API for user to toggle email notice state: /api/v1.0/users/toggleNotices/:id
		router.attach(Constants.applicationPrefix + Constants.versionPrefix + userServicePrefix + UserToggleEmailNoticePrefix + "/{id}", UserToggleNoticesResource.class);
		String UserWatchUserResourcePrefix = "/watchUser";
		//	API for user watch/de-watch other users: /api/v1.0/users/watchUser/:id
		router.attach(Constants.applicationPrefix + Constants.versionPrefix + userServicePrefix + UserWatchUserResourcePrefix + "/{id}", UserWatchUserResource.class);
		String UserIsWatchedResourcePrefix = "/isWatched";
		//	API for user watch/de-watch other users: /api/v1.0/users/isWatched/:id
		router.attach(Constants.applicationPrefix + Constants.versionPrefix + userServicePrefix + UserIsWatchedResourcePrefix + "/{id}", UserIsWatchedResource.class);
		String UserSearchUserResourcePrefix = "/searchUser";
		//	API for user watch/de-watch other users: /api/v1.0/users/searchUser
		router.attach(Constants.applicationPrefix + Constants.versionPrefix + userServicePrefix + UserSearchUserResourcePrefix, UserSearchUserResource.class);
		String UserMessageHistoryResourcePrefix = "/messageHistory";
		//	API for user getting history messages: /api/v1.0/users/messageHistory/:id
		router.attach(Constants.applicationPrefix + Constants.versionPrefix + userServicePrefix + UserMessageHistoryResourcePrefix + "/{id}", UserMessageHistoryResource.class);
		String UserTransactionResourcePrefix = "/transaction";
		//	API for user getting transactions: /api/v1.0/users/transaction/:id
		router.attach(Constants.applicationPrefix + Constants.versionPrefix + userServicePrefix + UserTransactionResourcePrefix + "/{id}", UserTransactionResource.class);
		String UserNotificationResourcePrefix = "/notification";
		//	API for user getting history messages: /api/v1.0/users/notification/:id
		router.attach(Constants.applicationPrefix + Constants.versionPrefix + userServicePrefix + UserNotificationResourcePrefix + "/{id}", UserNotificationResource.class);
		String UserSearchHistoryResourcePrefix = "/searchHistory";
		// API for user getting history messages: /api/v1.0/users/searchHistory/:id
		router.attach(Constants.applicationPrefix + Constants.versionPrefix + userServicePrefix + UserSearchHistoryResourcePrefix + "/{id}", UserSearchHistoryResource.class);
		
		
		/** --------------------- APIs for general module (legacy from v0.9) ------------------ **/
		String generalServicePrefix = "/general";
		//   API for feedback module:  /api/v1.0/general/*
		
		String feedBackResourcePrefix = "/feedBack";
		//	 API for feed back: /api/v1.0/general/feedBack
		router.attach(Constants.applicationPrefix + Constants.versionPrefix + generalServicePrefix + feedBackResourcePrefix, FeedBackResource.class);
		
		
		/** APIs for location module**/
		String locationServicePrefix = "/location";
		//   API for location module:  /api/v1.0/location/*
				
		String locationResourcePrefix = "/location";
		//   API for location resources:  /api/v1.0/location/location
		router.attach(Constants.applicationPrefix + Constants.versionPrefix + locationServicePrefix + locationResourcePrefix, LocationResource.class);
		
		
		
		/** --------------------- APIs for Administrator ------------------ **/
		String adminServicePrefix = "/admin";
		//   API for single messages:  /api/v1.0/admin/*
		
		String StateChangeResourcePrefix = "/stateChange";
		//	API for admin state changes actions on user/message/transaction: /api/v1.0/admin/stateChange
		router.attach(Constants.applicationPrefix + Constants.versionPrefix + adminServicePrefix + StateChangeResourcePrefix, AdminStateChangeResource.class);
		String RoutineResourcePrefix = "/routine";
		//	API for admin to force routine tasks to take place: /api/v1.0/admin/routine
		router.attach(Constants.applicationPrefix + Constants.versionPrefix + adminServicePrefix + RoutineResourcePrefix, AdminRoutineResource.class);
		
		return router;
	}
	
	
}