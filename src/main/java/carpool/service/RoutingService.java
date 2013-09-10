package carpool.service;


import org.restlet.Application;
import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.routing.Router;

import carpool.common.Common;
import carpool.common.Constants;
import carpool.resources.dianmingResource.*;
import carpool.resources.generalResource.*;
import carpool.resources.locationResource.*;
import carpool.resources.notificationResource.*;
import carpool.resources.transactionResource.*;
import carpool.resources.userResource.*;


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
		Common.d("initiaing router::RoutingService");
		Router router = new Router(getContext());
		
		/** -------------------- APIs for dianming module -------------- **/
		String dMServicePrefix = "/dianming";
		//  API for dianming messages:  /api/v1.0/dianming/*
		
		String DMResourcePrefix = "/dianming";
		//	API for Get/Post dianming messages: /api/v1.0/dianming/dianming
		router.attach(Constants.applicationPrefix + Constants.versionPrefix + dMServicePrefix + DMResourcePrefix, DMResource.class);
		//	API for Get/Put/Delete dianming messages: /api/v1.0/dianming/dianming/:id
		router.attach(Constants.applicationPrefix + Constants.versionPrefix + dMServicePrefix + DMResourcePrefix + "/{id}", DMResourceId.class);
		String DMGenderResourcePrefix = "/gender";
		//	API for DM message change gender: /api/v1.0/dianming/gender/:id
		router.attach(Constants.applicationPrefix + Constants.versionPrefix + dMServicePrefix + DMGenderResourcePrefix + "/{id}", DMGenderResource.class);
		String DMLocationResourcePrefix = "/location";
		//	API for DM message change gender: /api/v1.0/dianming/location/:id
		router.attach(Constants.applicationPrefix + Constants.versionPrefix + dMServicePrefix + DMLocationResourcePrefix + "/{id}", DMLocationResource.class);
		String DMNoteResourcePrefix = "/note";
		//	API for DM message change gender: /api/v1.0/dianming/note/:id
		router.attach(Constants.applicationPrefix + Constants.versionPrefix + dMServicePrefix + DMNoteResourcePrefix + "/{id}", DMNoteResource.class);
		String DMPaymentMethodResourcePrefix = "/paymentMethod";
		//	API for DM message change gender: /api/v1.0/dianming/paymentMethod/:id
		router.attach(Constants.applicationPrefix + Constants.versionPrefix + dMServicePrefix + DMPaymentMethodResourcePrefix + "/{id}", DMPaymentMethodResource.class);
		String DMPriceResourcePrefix = "/price";
		//	API for DM message change gender: /api/v1.0/dianming/price/:id
		router.attach(Constants.applicationPrefix + Constants.versionPrefix + dMServicePrefix + DMPriceResourcePrefix + "/{id}", DMPriceResource.class);
		String DMTimingResourcePrefix = "/timing";
		//	API for DM message change gender: /api/v1.0/dianming/timing/:id
		router.attach(Constants.applicationPrefix + Constants.versionPrefix + dMServicePrefix + DMTimingResourcePrefix + "/{id}", DMTimingResource.class);
		String DMRecentResourcePrefix = "/recent";
		//	API for DM message change gender: /api/v1.0/dianming/recent
		router.attach(Constants.applicationPrefix + Constants.versionPrefix + dMServicePrefix + DMRecentResourcePrefix, RecentMessageResource.class);
		String DMSearchResourcePrefix = "/search";
		//	API for DM message change gender: /api/v1.0/dianming/search
		router.attach(Constants.applicationPrefix + Constants.versionPrefix + dMServicePrefix + DMSearchResourcePrefix, DMSearchResource.class);
		
		
		/** -------------------- APIs for transaction module -------------- **/
		String transactionServicePrefix = "/transaction";
		//  API for transaction:  /api/v1.0/transaction/*
		
		String TransactionResourcePrefix = "/transaction";
		//	API for Get/Post transactions: /api/v1.0/transaction/transaction
		router.attach(Constants.applicationPrefix + Constants.versionPrefix + transactionServicePrefix + TransactionResourcePrefix, TransactionResource.class);
		//	API for Get/Put/Delete transactions: /api/v1.0/transaction/transaction/:id
		router.attach(Constants.applicationPrefix + Constants.versionPrefix + transactionServicePrefix + TransactionResourcePrefix + "/{id}", TransactionResourceId.class);
		String TransactionAdminResourcePrefix = "/admin";
		//	API for admin state changes actions on transactions: /api/v1.0/transaction/admin/:id
		router.attach(Constants.applicationPrefix + Constants.versionPrefix + transactionServicePrefix + TransactionAdminResourcePrefix, TransactionAdminResource.class);

		
		
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
		String CircleLocationResourcePrefix = "/circleLocation";
		//	API for user changing the university circle:  /api/v1.0/users/circleLocation/:id
		router.attach(Constants.applicationPrefix + Constants.versionPrefix + userServicePrefix + CircleLocationResourcePrefix + "/{id}", UserCircleLocationResource.class);
		String EmailActivationResourcePrefix = "/emailActivation";
		//	API for user email activation: /api/v1.0/users/emailActivation
		router.attach(Constants.applicationPrefix + Constants.versionPrefix + userServicePrefix + EmailActivationResourcePrefix, UserEmailActivationResource.class);
		String ResendActivationEmailResourcePrefix = "/resendActivationEmail";
		//	API for sending activation email to target userId: /api/v1.0/users/resendActivationEmail/:id
		router.attach(Constants.applicationPrefix + Constants.versionPrefix + userServicePrefix + ResendActivationEmailResourcePrefix + "/{id}", ResendActivationEmailResource.class);
		String UserTopBarResourcePrefix = "/topBar";
		//	API to return the user object designed for topBar: /api/v1.0/users/topBar/:id
		router.attach(Constants.applicationPrefix + Constants.versionPrefix + userServicePrefix + UserTopBarResourcePrefix + "/{id}", UserTopBarResource.class);
		String ForgetPasswordResourcePrefix = "/forgetPassword";
		//	API for user forgetting the password: /api/v1.0/users/forgetPassword
		router.attach(Constants.applicationPrefix + Constants.versionPrefix + userServicePrefix + ForgetPasswordResourcePrefix, ForgetPasswordResource.class);
		String UserToggleEmailNoticePrefix = "/toggleEmailNotice";
		String UserTogglePhoneNoticePrefix = "/togglePhoneNotice";
		//	API for user to toggle email notice state: /api/v1.0/users/toggleEmailNotice/:id
		//	API for user to toggle phone notice state: /api/v1.0/users/togglePhoneNotice/:id
		router.attach(Constants.applicationPrefix + Constants.versionPrefix + userServicePrefix + UserToggleEmailNoticePrefix + "/{id}", UserToggleEmailNoticeResource.class);
		router.attach(Constants.applicationPrefix + Constants.versionPrefix + userServicePrefix + UserTogglePhoneNoticePrefix + "/{id}", UserTogglePhoneNoticeResource.class);
		String UserWatchUserResourcePrefix = "/watchUser";
		//	API for user watch/de-watch other users: /api/v1.0/users/watchUser/:id
		router.attach(Constants.applicationPrefix + Constants.versionPrefix + userServicePrefix + UserWatchUserResourcePrefix + "/{id}", UserWatchUserResource.class);
		String UserWatchDMMessageResourcePrefix = "/watchDMMessage";
		//	API for user watch/de-watch other messages: /api/v1.0/users/watchMessage/:id
		router.attach(Constants.applicationPrefix + Constants.versionPrefix + userServicePrefix + UserWatchDMMessageResourcePrefix + "/{id}", UserWatchDMMessageResource.class);
		String UserMessageHistoryResourcePrefix = "/messageHistory";
		//	API for user getting history messages: /api/v1.0/users/messageHistory/:id
		router.attach(Constants.applicationPrefix + Constants.versionPrefix + userServicePrefix + UserMessageHistoryResourcePrefix + "/{id}", UserMessageHistoryResource.class);
		String UserTransactionResourcePrefix = "/transaction";
		//	API for user getting transactions: /api/v1.0/users/transaction/:id
		router.attach(Constants.applicationPrefix + Constants.versionPrefix + userServicePrefix + UserTransactionResourcePrefix + "/{id}", UserTransactionResource.class);
		String UserNotificationResourcePrefix = "/notification";
		//	API for user getting history messages: /api/v1.0/users/notification/:id
		router.attach(Constants.applicationPrefix + Constants.versionPrefix + userServicePrefix + UserNotificationResourcePrefix + "/{id}", UserNotificationResource.class);
		
		
		
		
		/** --------------------- APIs for general module (legacy from v0.9) ------------------ **/
		String generalServicePrefix = "/general";
		//   API for single messages:  /api/v1.0/general/*
		
		String feedBackResourcePrefix = "/feedBack";
		//	 API for feed back: /api/v1.0/general/feedBack
		router.attach(Constants.applicationPrefix + Constants.versionPrefix + generalServicePrefix + feedBackResourcePrefix, FeedBackResource.class);
		
		
		/** APIs for location module**/
		String locationServicePrefix = "/location";
		//   API for single messages:  /api/v1.0/location/*
				
		String locationResourcePrefix = "/location";
		//   API for location resources:  /api/v1.0/location/location
		router.attach(Constants.applicationPrefix + Constants.versionPrefix + locationServicePrefix + locationResourcePrefix, LocationResource.class);
		
		
		
		return router;
	}
	
	
}