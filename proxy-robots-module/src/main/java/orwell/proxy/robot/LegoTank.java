package orwell.proxy.robot;

import lejos.mf.common.MessageListenerInterface;
import lejos.mf.common.UnitMessage;
import lejos.mf.pc.MessageFramework;
import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import orwell.proxy.robot.messages.IRobotMessageVisitor;

public class LegoTank extends IRobot implements MessageListenerInterface {
    private final static Logger logback = LoggerFactory.getLogger(LegoTank.class);
    private final IRobotElement[] robotElements;
    private final NXTInfo nxtInfo;
    private final MessageFramework messageFramework;
    private final UnitMessageBroker unitMessageBroker = new UnitMessageBroker(this);


    public LegoTank(final String bluetoothName, final String bluetoothId,
                    final MessageFramework messageFramework,
                    final ICamera camera, final String image) {
        this.robotElements = new IRobotElement[]{camera, new RfidSensor(), new ColourSensor()};
        this.nxtInfo = new NXTInfo(NXTCommFactory.BLUETOOTH, bluetoothName, bluetoothId);
        this.messageFramework = messageFramework;
        messageFramework.addMessageListener(this);
        setImage(image);
        setCameraUrl(camera.getUrl());
        setRobotState(new RobotState());
    }

    public LegoTank(final String bluetoothName, final String bluetoothId,
                    final ICamera camera, final String image) {
        this(bluetoothName, bluetoothId, new MessageFramework(), camera, image);
    }


    public void setRfidValue(final String rfidValue) {
        ((RfidSensor) robotElements[1]).setValue(rfidValue);
    }

    public void setColourValue(final String colourValue) {
        ((ColourSensor) robotElements[2]).setValue(colourValue);
    }


    @Override
    public void receivedNewMessage(final UnitMessage msg) {
        unitMessageBroker.handle(msg);
    }

    @Override
    public void accept(final IRobotElementVisitor visitor) {
        for (final IRobotElement element : robotElements) {
            element.accept(visitor);
        }
        visitor.visit(this);
    }

    @Override
    public void accept(final IRobotMessageVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public void sendUnitMessage(final UnitMessage unitMessage) {

        logback.debug("Sending input to physical device");
        messageFramework.SendMessage(unitMessage);
    }

    @Override
    public EnumConnectionState connect() {
        logback.info("Connecting to robot: " + this.getRoutingId());

        final Boolean isConnected = messageFramework.ConnectToNXT(nxtInfo);
        if (isConnected) {
            this.setConnectionState(EnumConnectionState.CONNECTED);
            logback.info("Robot [" + getRoutingId() + "] is connected to the proxy!");
        } else {
            this.setConnectionState(EnumConnectionState.CONNECTION_FAILED);
            logback.warn("Robot [" + getRoutingId() + "] failed to connect to the proxy!");
        }
        return this.getConnectionState();
    }

    @Override
    public void closeConnection() {
        if (EnumConnectionState.CONNECTED == getConnectionState()) {
            messageFramework.close();
        }
    }

    @Override
    public String toString() {
        return "LegoTank { [BTName] " + nxtInfo.name + " [BT-ID] " +
                nxtInfo.deviceAddress + " [RoutingID] " + getRoutingId() +
                " [TeamName] " + getTeamName() + " }";
    }

    @Override
    public void accept(final RobotGameStateVisitor visitor) {
        visitor.visit(this);
    }
}
