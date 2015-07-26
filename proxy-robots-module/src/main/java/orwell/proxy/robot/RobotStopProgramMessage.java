package orwell.proxy.robot;

import lejos.mf.common.UnitMessage;
import lejos.mf.common.UnitMessageType;

/**
 * Created by Michaël Ludmann on 7/26/15.
 */
public class RobotStopProgramMessage {
    private final static String STOP_PRG_PAYLOAD_HEADER = "stopPrg";

    public void sendUnitMessageTo(final IRobot robot) {
        robot.sendUnitMessage(new UnitMessage(UnitMessageType.Command,
                STOP_PRG_PAYLOAD_HEADER));
    }
}
