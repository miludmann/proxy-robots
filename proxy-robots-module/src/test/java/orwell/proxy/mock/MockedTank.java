package orwell.proxy.mock;

import lejos.mf.common.UnitMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import orwell.proxy.robot.*;
import orwell.proxy.robot.messages.IRobotMessageVisitor;

/**
 * Created by Michaël Ludmann on 03/05/15.
 */
public class MockedTank extends IRobot {
    private final static Logger logback = LoggerFactory.getLogger(MockedTank.class);

    private final IRobotElement[] robotElements;

    public MockedTank() {
        this.setRoutingId("tempRoutingId");
        this.setCameraUrl("http://fake.url");
        this.setImage("noImage");
        this.setRegistrationState(EnumRegistrationState.NOT_REGISTERED);
        this.setConnectionState(EnumConnectionState.NOT_CONNECTED);
        this.setTeamName("BLUE");
        this.robotElements = new IRobotElement[]{new RfidSensor(), new ColourSensor()};
        setRobotState(new RobotState());
    }

    public void setRfidValue(final String rfidValue) {
        ((RfidSensor) robotElements[0]).setValue(rfidValue);
    }

    @Override
    public void sendUnitMessage(final UnitMessage unitMessage) {

    }

    @Override
    public EnumConnectionState connect() {
        logback.info("Connecting to physical device");
        setConnectionState(EnumConnectionState.CONNECTED);
        return getConnectionState();
    }

    @Override
    public void closeConnection() {
        logback.info("Closing connection to physical device");
        setConnectionState(EnumConnectionState.NOT_CONNECTED);
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
    public String toString() {
        return "MockedTank { [RoutingID] " + getRoutingId() +
                " [TeamName] " + getTeamName() + " }";
    }
}
