package orwell.proxy.robot.messages;

import com.google.protobuf.InvalidProtocolBufferException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import orwell.messages.Controller;
import orwell.proxy.robot.IRobot;
import orwell.proxy.robot.RobotState;

/**
 * Created by Michaël Ludmann on 23/08/15.
 */
public class MessageInput implements IRobotMessageVisitor {
    private final static Logger logback = LoggerFactory.getLogger(MessageInput.class);
    private Controller.Input input;
    private InputMove inputMove;
    private InputFire inputFire;

    public MessageInput(final byte[] inputMessage) {

        try {
            input = Controller.Input.parseFrom(inputMessage);
        } catch (final InvalidProtocolBufferException e) {
            logback.info("RobotActionSet protobuf exception: " + e.getMessage());
        }
        setMove();
        setFire();
    }

    public MessageInput(final InputMove inputMove) {

        this.inputMove = inputMove;
    }

    public MessageInput(final InputFire inputFire) {

        this.inputFire = inputFire;
    }

    public void setMove() {

        if (null != input && input.hasMove()) {
            this.inputMove = new InputMove(input.getMove());
        }
    }

    public void setFire() {

        if (!isFireEmpty(input)) {
            this.inputFire = new InputFire(input.getFire());
        }
    }

    public String toString(final IRobot robot) {
        final String string;
        if (null != input) {
            string = "Controller INPUT of Robot [" + robot.getRoutingId() + "]:"
                    + "\n\t|___Move order: [LEFT] "
                    + input.getMove().getLeft()
                    + " \t\t[RIGHT] "
                    + input.getMove().getRight()
                    + "\n\t|___Fire order: [WEAPON1] "
                    + input.getFire().getWeapon1()
                    + " \t[WEAPON2] "
                    + input.getFire().getWeapon2();
        } else {
            string = "Controller INPUT of Robot [" + robot.getRoutingId()
                    + "] NOT initialized!";
        }
        return string;
    }

    /**
     * @param input
     * @return true is there is no relevant data inside input Fire
     * (meaning it is not worth sending it to the robot)
     */
    private boolean isFireEmpty(final Controller.Input input) {
        return (null == input) ||
                !(input.hasFire() &&
                        (input.getFire().getWeapon1() || input.getFire().getWeapon2()));
    }

    @Override
    public void visit(final IRobot robot) {
        RobotState robotState = new RobotState();

        if (null != this.inputMove && this.inputMove.hasMove()) {
            inputMove.sendUnitMessageTo(robot);
            robotState.setMove(inputMove);
        }

        if (null != this.inputFire && this.inputFire.hasFire()) {
            inputFire.sendUnitMessageTo(robot);
            robotState.setFire(inputFire);
        }

        robot.setRobotState(robotState);
    }
}
