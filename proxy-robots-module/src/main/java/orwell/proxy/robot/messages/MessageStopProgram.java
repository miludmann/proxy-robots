package orwell.proxy.robot.messages;

import lejos.mf.common.UnitMessage;
import lejos.mf.common.UnitMessageType;
import orwell.proxy.robot.IRobot;

/**
 * Created by Michaël Ludmann on 7/26/15.
 */
public class MessageStopProgram implements IRobotMessageVisitor {
    private final static String STOP_PRG_PAYLOAD_HEADER = "stopPrg";

    @Override
    public void visit(IRobot robot) {
        robot.sendUnitMessage(new UnitMessage(UnitMessageType.Command,
                STOP_PRG_PAYLOAD_HEADER));
    }
}
