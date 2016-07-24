package orwell.proxy.zmq;

import lejos.mf.common.UnitMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZMQ;

/**
 * Created by Michaël Ludmann on 27/06/16.
 */
public class RobotMessageBroker {
    private final static Logger logback = LoggerFactory.getLogger(RobotMessageBroker.class);
    private static final String BINDING_ADDRESS = "tcp://0.0.0.0";
    private final ZMQ.Context context;
    private final ZMQ.Socket sender;
    private final ZMQ.Socket receiver;
    private final int pushPort;
    private final int pullPort;

    public RobotMessageBroker(int pushPort, int pullPort) {
        this.pushPort = pushPort;
        this.pullPort = pullPort;

        context = ZMQ.context(1);
        sender = context.socket(ZMQ.PUSH);
        receiver = context.socket(ZMQ.PULL);

        sender.setLinger(1000);
        receiver.setLinger(1000);
    }

    public void bind() {
        sender.bind(BINDING_ADDRESS + ":" + pushPort);
        receiver.bind(BINDING_ADDRESS + ":" + pullPort);
        logback.info("Proxy is binding on ports " + pushPort + " (push) and " + pullPort + " (pull)");
    }

    public void close() {
        sender.close();
        receiver.close();
        context.term();
        logback.info("All sockets of are now closed");
    }

    public boolean send(UnitMessage unitMessage) {
        logback.debug("Sending message to physical device");
        return sender.send(unitMessage.toString());
    }
}
