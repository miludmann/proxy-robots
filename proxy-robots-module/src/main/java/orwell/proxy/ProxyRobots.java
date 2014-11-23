package orwell.proxy;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZMQ;

import orwell.proxy.IRobot.EnumConnectionState;
import orwell.proxy.config.ConfigModel;
import orwell.proxy.config.ConfigProxy;
import orwell.proxy.config.ConfigRobots;
import orwell.proxy.config.ConfigServerGame;
import orwell.proxy.config.ConfigTank;
import orwell.proxy.config.Configuration;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.util.StatusPrinter;

public class ProxyRobots {
	final static Logger logback = LoggerFactory.getLogger(ProxyRobots.class); 

	private ConfigServerGame configServerGame;
	private ConfigRobots configRobots;
	private ZMQ.Context context;
	private ZMQ.Socket sender;
	private ZMQ.Socket receiver;
	private HashMap<String, IRobot> tanksInitializedMap = new HashMap<String, IRobot>();
	private HashMap<String, IRobot> tanksConnectedMap = new HashMap<String, IRobot>();
	private HashMap<String, IRobot> tanksRegisteredMap = new HashMap<String, IRobot>();

	public ProxyRobots(String ConfigFileAddress, String serverGame,
			ZMQ.Context zmqContext) {
		logback.info("Constructor -- IN");
		Configuration configuration = new Configuration(ConfigFileAddress);
		try {
			// TODO Include populate into default constructor
			configuration.populate();
		} catch (FileNotFoundException | JAXBException e1) {
			e1.toString();
		}
		ConfigModel configProxyModel = configuration.getConfigModel();
		ConfigProxy configProxy = configProxyModel.getConfigProxy();
		configRobots = configuration.getConfigModel().getConfigRobots();
		try {
			configServerGame = configProxy.getConfigServerGame(serverGame);
		} catch (Exception e) {
			e.printStackTrace();
		}

		context = zmqContext;
		sender = context.socket(ZMQ.PUSH);
		receiver = context.socket(ZMQ.SUB);
		sender.setLinger(configProxy.getSenderLinger());
		receiver.setLinger(configProxy.getReceiverLinger());
		logback.info("Constructor -- OUT");
	}

	public ProxyRobots(String ConfigFileAddress, String serverGame) {
		this(ConfigFileAddress, serverGame, ZMQ.context(1));
	}

	public HashMap<String, IRobot> getTanksInitializedMap() {
		return tanksInitializedMap;
	}

	public HashMap<String, IRobot> getTanksConnectedMap() {
		return tanksConnectedMap;
	}

	public HashMap<String, IRobot> getTanksRegisteredMap() {
		return tanksRegisteredMap;
	}

	public void connectToServer() {
		sender.connect("tcp://" + configServerGame.getIp() + ":"
				+ configServerGame.getPushPort());
		logback.info("ProxyRobots Sender created");
		receiver.connect("tcp://" + configServerGame.getIp() + ":"
				+ configServerGame.getSubPort());
		logback.info("ProxyRobots Receiver created");
		receiver.subscribe(new String("").getBytes());
	}

	/*
	 * This instantiate Tanks objects from a configuration It only set up the
	 * tanksInitializedMap
	 */
	public void initialiseTanks() {
		for (ConfigTank configTank : configRobots.getConfigRobotsToRegister()) {
			Camera camera = new Camera(configTank.getConfigCamera().getIp(),
					configTank.getConfigCamera().getPort());
			//TODO Improve initialization of setImage to get something meaningful
			//from the string (like an actual picture)
			Tank tank = new Tank(configTank.getBluetoothName(),
					configTank.getBluetoothID(), camera, configTank.getImage());
			logback.info(" NININININ " + configTank.getTempRoutingID());
			tank.setRoutingID(configTank.getTempRoutingID());
			this.tanksInitializedMap.put(tank.getRoutingID(), tank);
		}

		logback.info("All " + this.tanksInitializedMap.size()
				+ " tank(s) initialised");
	}

	/*
	 * This instantiate Tanks objects It only set up the tanksInitializedMap
	 * from another map
	 * 
	 * @param map of tanks to setup
	 */
	public void initialiseTanks(HashMap<String, Tank> tanksToInitializeMap) {
		for (Map.Entry<String, Tank> entry : tanksToInitializeMap.entrySet()) {
			String routingID = entry.getKey();
			Tank tank = entry.getValue();
			tank.setRoutingID(routingID);
			this.tanksInitializedMap.put(routingID, tank);
		}
	}

	public void connectToRobots() {
		Iterator<Map.Entry<String, IRobot>> iterator = tanksInitializedMap
				.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<String, IRobot> entry = iterator.next();
			String routingID = entry.getKey();
			IRobot tank = entry.getValue();
			logback.info("Connecting to robot: \n" + tank.toString());
			tank.connectToRobot();

			if (tank.getConnectionState() == EnumConnectionState.CONNECTION_FAILED) {
				logback.info("Robot [" + tank.getRoutingID()
						+ "] failed to connect to the proxy!");
			} else {
				logback.info("Robot [" + tank.getRoutingID()
						+ "] is connected to the proxy!");
				this.tanksConnectedMap.put(routingID, tank);
				iterator.remove();
			}
		}
	}

	public void registerRobots() {
		for (IRobot tank : tanksConnectedMap.values()) {
			tank.buildRegister();
			this.sender.send(tank.getZMQRegister(), 0);
			logback.info("TEST RegisterHeader: " + tank.getZMQRegister().toString());
			logback.info("Robot [" + tank.getRoutingID()
					+ "] is trying to register itself to the server!");
		}
	}

	public void startCommunication(ZmqMessageWrapper interruptMessage) {
		String zmq_previousMessage = new String();
		String previousInput = new String();
		ZmqMessageWrapper zmqMessage;

		while (!Thread.currentThread().isInterrupted()) {
			byte[] raw_zmq_message = this.receiver.recv();
			zmqMessage = new ZmqMessageWrapper(raw_zmq_message);

			// We do not want to uselessly flood the robot
			if (zmqMessage.zmqMessageString.compareTo(zmq_previousMessage) == 0) {
				logback.debug("Current zmq message identical to previous zmq message");
				continue;
			}

			switch (zmqMessage.type) {
			case "Registered":
				logback.info("Setting ServerGame Registered to tank");
				if (this.tanksConnectedMap.containsKey(zmqMessage.routingId)) {
					IRobot registeredRobot = this.tanksConnectedMap
							.get(zmqMessage.routingId);
					this.tanksRegisteredMap.put(zmqMessage.routingId,
							registeredRobot);
					this.tanksConnectedMap.remove(zmqMessage.routingId);
					registeredRobot.setRegistered(zmqMessage.message);
					logback.info("Registered robot : " + registeredRobot
							.serverGameRegisteredToString());
				} else {
					logback.info("RoutingID " + zmqMessage.routingId
							+ " is not an ID of a tank to register");
				}
				break;
			case "Input":
				logback.info("Setting controller Input to tank");
				if (previousInput.compareTo(zmqMessage.zmqMessageString) == 0) {
					logback.debug("Current input identical to previous input");
					continue;
				}
				previousInput = zmqMessage.zmqMessageString;
				if (this.tanksRegisteredMap.containsKey(zmqMessage.routingId)) {
					IRobot tankTargeted = this.tanksRegisteredMap
							.get(zmqMessage.routingId);
					tankTargeted.setControllerInput(zmqMessage.message);
					logback.info("tankTargeted input : " + tankTargeted.controllerInputToString());
				} else {
					logback.info("RoutingID " + zmqMessage.routingId
							+ " is not an ID of a tank to register");
				}
				break;
			case "GameState":
				break;
			default:
				logback.info("[WARNING] Invalid Message type");
			}

			zmq_previousMessage = zmqMessage.zmqMessageString;
			
			logback.debug("zmqMessage.type = " + zmqMessage.type);
			logback.debug("interruptMessage.type = " + interruptMessage.type);

			if(null != interruptMessage && zmqMessage.type.equals(interruptMessage.type))
			{
				logback.info("Communication interrupted with interrupt message : " + interruptMessage.type);
				break;
			}
		}
	}

	public void stopCommunication() {
		this.sender.close();
		this.receiver.close();
		this.context.term();
	}
	
	public void printLoggerState() {
		// print internal state
		LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
		StatusPrinter.print(lc);
	}

	public static void main(String[] args) throws Exception {
		ProxyRobots proxyRobots = new ProxyRobots(
				"/configuration.xml", "irondamien");
		proxyRobots.connectToServer();
		proxyRobots.initialiseTanks();
		proxyRobots.connectToRobots();
		proxyRobots.registerRobots();
		proxyRobots.startCommunication(null);

		// proxyRobots.sender.send(proxyRobots.tank.getZMQRobotState(), 0);
		// logback.info("Message sent");
		//
		// String request = "Banana";
		// proxyRobots.sender.send(request, 0);
		// logback.info("Message sent: " + request);
		proxyRobots.stopCommunication();
	}
}
