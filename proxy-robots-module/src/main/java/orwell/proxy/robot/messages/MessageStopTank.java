package orwell.proxy.robot.messages;

import lejos.mf.common.UnitMessage;
import lejos.mf.common.UnitMessageType;
import orwell.proxy.robot.IRobot;
import orwell.proxy.robot.RobotState;

/**
 * Created by Michaël Ludmann on 23/08/15.
 */
public class MessageStopTank implements IRobotMessageVisitor {
    private final static String STOP_PAYLOAD_HEADER = "stop";

    @Override
    public void visit(IRobot robot) {
        robot.sendUnitMessage(new UnitMessage(UnitMessageType.Command,
                STOP_PAYLOAD_HEADER));
        RobotState robotState = new RobotState();
        robotState.setHasStop(true);
        robot.setRobotState(robotState);
    }
}
