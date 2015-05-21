package orwell.proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import orwell.proxy.config.*;
import orwell.proxy.robot.*;
import orwell.proxy.zmq.IZmqMessageBroker;
import orwell.proxy.zmq.IZmqMessageListener;
import orwell.proxy.zmq.ZmqMessageBOM;

public class ProxyRobots implements IZmqMessageListener {
    private final static Logger logback = LoggerFactory.getLogger(ProxyRobots.class);
    private static final long THREAD_SLEEP_MS = 10;
    private final IConfigServerGame configServerGame;
    private final IConfigRobots configRobots;
    private final IZmqMessageBroker mfProxy;
    private final CommunicationService communicationService = new CommunicationService();
    private final Thread communicationThread = new Thread(communicationService);
    private final long outgoingMessagePeriod;
    protected IRobotsMap robotsMap;
    protected int outgoingMessageFiltered;

    public ProxyRobots(final IZmqMessageBroker mfProxy,
                       final IConfigFactory configFactory,
                       final IRobotsMap robotsMap) {
        logback.info("Constructor -- IN");
        assert (null != mfProxy);
        assert (null != configFactory);
        assert (null != configFactory.getConfigProxy());
        assert (null != robotsMap);

        this.mfProxy = mfProxy;
        this.configServerGame = configFactory.getConfigServerGame();
        this.configRobots = configFactory.getConfigRobots();
        this.robotsMap = robotsMap;
        this.outgoingMessagePeriod = configFactory.getConfigProxy().getOutgoingMsgPeriod();

        mfProxy.addZmqMessageListener(this);
        logback.info("Constructor -- OUT");
    }

    public static void main(final String[] args) throws Exception {
        final ConfigFactoryParameters configPathType = new Cli(args).parse();

        final ProxyRobots proxyRobots = new ProxyRobotsFactory(configPathType).getProxyRobots();
        if (null == proxyRobots) {
            logback.error("Error when creating ProxyRobots");
        } else {
            proxyRobots.start();
        }
    }

    private void connectToServer() {
        mfProxy.connectToServer(
                configServerGame.getIp(),
                configServerGame.getPushPort(),
                configServerGame.getSubPort());
    }

    /**
     * This instantiates Tanks objects from a configuration It only set up the
     * tanksInitializedMap
     */
    protected void initializeTanksFromConfig() {
        for (final ConfigTank configTank : configRobots.getConfigRobotsToRegister()) {
            final LegoTank tank = RobotFactory.getLegoTank(configTank);
            if (null == tank) {
                logback.error("Lego tank not initialized. Skipping it for now.");
            } else {
                logback.info("Temporary routing ID: " + tank.getRoutingId());
                tank.setRoutingId(configTank.getTempRoutingID());
                this.robotsMap.add(tank);
            }
        }

        logback.info("All " + this.robotsMap.getRobotsArray().size()
                + " tank(s) initialized");
    }

    protected void connectToRobots() {
        for (final IRobot robot : robotsMap.getNotConnectedRobots()) {
            robot.connect();
        }
    }

    protected void sendRegister() {
        for (final IRobot robot : robotsMap.getConnectedRobots()) {
            final ZmqMessageBOM zmqMessageBOM = new ZmqMessageBOM(robot.getRoutingId(), EnumMessageType.REGISTER,
                    RegisterBytes.fromRobotFactory(robot));
            mfProxy.sendZmqMessage(zmqMessageBOM);
            logback.info("Robot [" + robot.getRoutingId()
                    + "] is trying to register itself to the server!");
        }
    }

    /**
     * Sends a delta of each robot state since last call
     */
    protected void sendServerRobotStates() {
        for (final IRobot robot : robotsMap.getRegisteredRobots()) {
            final RobotElementStateVisitor stateVisitor = new RobotElementStateVisitor();
            robot.accept(stateVisitor);

            final byte[] serverRobotStateBytes = stateVisitor.getServerRobotStateBytes();
            if (null != serverRobotStateBytes) {
                logback.debug("Sending a ServerRobotState message");
                final ZmqMessageBOM zmqMessageBOM =
                        new ZmqMessageBOM(robot.getRoutingId(), EnumMessageType.SERVER_ROBOT_STATE,
                                serverRobotStateBytes);
                mfProxy.sendZmqMessage(zmqMessageBOM);
            }
        }
    }

    protected void startCommunicationService() {
        communicationThread.start();
    }

    protected boolean isCommunicationServiceAlive() {
        return communicationThread.isAlive();
    }

    public void stop() {
        disconnectAllTanks();
    }

    private void disconnectAllTanks() {
        for (final IRobot tank : robotsMap.getConnectedRobots()) {
            tank.closeConnection();
        }
    }

    private void onRegistered(final ZmqMessageBOM zmqMessageBOM) {
        logback.info("Setting ServerGame Registered to tank");
        final String routingId = zmqMessageBOM.getRoutingId();
        if (robotsMap.isRobotConnected(routingId)) {
            final IRobot registeredRobot = robotsMap.get(routingId);
            final Registered registered = new Registered(zmqMessageBOM.getMessageBodyBytes());
            registered.setToRobot(registeredRobot);
            final RobotElementPrintVisitor printVisitor = new RobotElementPrintVisitor();
            registeredRobot.accept(printVisitor);
        } else {
            logback.info("RoutingID " + routingId
                    + " is not an ID of a tank to register");
        }
    }

    private void onInput(final ZmqMessageBOM zmqMessageBOM) {
        logback.info("Setting controller Input to tank");
        final String routingId = zmqMessageBOM.getRoutingId();
        if (robotsMap.isRobotRegistered(routingId)) {
            final IRobot targetedRobot = robotsMap.get(routingId);
            final RobotInputSetVisitor inputSetVisitor = new RobotInputSetVisitor(zmqMessageBOM.getMessageBodyBytes());
            targetedRobot.accept(inputSetVisitor);
            logback.info("tankTargeted input : " + inputSetVisitor.inputToString(targetedRobot));
        } else {
            logback.info("RoutingID " + routingId
                    + " is not an ID of a registered tank");
        }
    }

    private void onGameState(final ZmqMessageBOM zmqMessageBOM) {
        logback.warn("Received GameState - not handled (robot " + zmqMessageBOM.getRoutingId() + ")");
    }

    private void onDefault() {
        logback.warn("Unknown message type");
    }

    /**
     * Starts the proxy :
     * -connect itself to the server
     * -initialize robots from a config if the provided map is empty
     * -connect to those robots
     * -start communication service with the server
     * -send register to the server
     */
    public void start() {
        this.connectToServer();
        if (robotsMap.getRobotsArray().isEmpty())
            this.initializeTanksFromConfig();
        this.connectToRobots();
        //We have to start the communication service before sending Register
        //Otherwise we risk not being ready to read Registered in time
        this.startCommunicationService();
        this.sendRegister();
    }

    @Override
    public void receivedNewZmq(final ZmqMessageBOM zmqMessageBOM) {
        switch (zmqMessageBOM.getMessageType()) {
            case REGISTERED:
                onRegistered(zmqMessageBOM);
                break;
            case INPUT:
                onInput(zmqMessageBOM);
                break;
            case GAME_STATE:
                onGameState(zmqMessageBOM);
                break;
            default:
                onDefault();
        }
    }


    private class CommunicationService implements Runnable {
        public void run() {
            logback.info("Start of communication service");
            long lastSendTime = System.currentTimeMillis();

            // We stop the service once there are no more robots
            // connected to the proxy
            while (!Thread.currentThread().isInterrupted() &&
                    !robotsMap.getConnectedRobots().isEmpty()) {

                // We avoid flooding the server
                if (outgoingMessagePeriod < System.currentTimeMillis() - lastSendTime) {
                    sendServerRobotStates();
                    lastSendTime = System.currentTimeMillis();
                } else {
                    outgoingMessageFiltered++;
                }
                try {
                    // This is performed to avoid high CPU consumption
                    //noinspection BusyWait
                    Thread.sleep(THREAD_SLEEP_MS);
                } catch (final InterruptedException e) {
                    logback.error("CommunicationService thread sleep exception: " + e.getMessage());
                }
            }
            logback.info("End of communication service");
            mfProxy.close();
            Thread.yield();
        }
    }
}
