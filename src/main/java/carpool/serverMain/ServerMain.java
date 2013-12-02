package carpool.serverMain;

import java.util.ArrayList;

import org.json.JSONObject;
import org.restlet.Component;
import org.restlet.Server;
import org.restlet.data.Protocol;

import carpool.common.DebugLog;
import carpool.constants.CarpoolConfig;
import carpool.constants.Constants.gender;
import carpool.factory.JSONFactory;
import carpool.locationService.LocationService;
import carpool.service.*;



public class ServerMain {

	//private static Log log = LogFactory.getLog(ServiceMain.class);

	private static ServerMain me;

	private Component component;

	public void init(String[] arguments) {

	}

	/**
	 * Start the Thread, accept incoming connections
	 * 
	 * Use this entry point to start with embedded HTTP Server
	 * 
	 * @throws Exception
	 */
	public void start() throws Exception {
		LocationService.init();
		
		component = new Component();

		// Add a new HTTP server listening on port

		Server server = component.getServers().add(Protocol.HTTP, 8015);
		server.getContext().getParameters().add("maxThreads", "1024");

		// Attach the sample application
		RoutingService routingService = new RoutingService();

		component.getDefaultHost().attach(routingService);

		// Start the component.
		//log.info("ready to start");
		DebugLog.d("ready to start");
		component.start();

	}

	/**
	 * Stops RESTlet application
	 */
	public void stop() {
		component.getDefaultHost().detach(component.getApplication());
	}

	public static ServerMain getInstance() {
		if (me == null) {
			me = new ServerMain();
		}
		
		return me;
	}
	


	public static void main(String... args) throws Exception {
		CarpoolConfig.initConfig();
		DebugLog.initializeLogger();
		
		DebugLog.d("Excuting");
		// Load server logic
		try {
			ServerMain.getInstance().init(args);
			ServerMain.getInstance().start();
		} catch (Exception e) {
			//log.error("Failed to start server", e);
		}
		Thread thread = new CleanThreadService();
		thread.start();
	}

}
