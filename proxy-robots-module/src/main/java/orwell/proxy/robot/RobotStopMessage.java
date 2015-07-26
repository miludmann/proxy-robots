package orwell.proxy.robot;

import lejos.mf.common.UnitMessage;
import lejos.mf.common.UnitMessageType;

/**
 * Created by Michaël Ludmann on 7/26/15.
 */
public class RobotStopMessage {
    private final static String STOP_PAYLOAD_HEADER = "stop";

    public void sendUnitMessageTo(final IRobot robot) {
        robot.sendUnitMessage(new UnitMessage(UnitMessageType.Command,
                STOP_PAYLOAD_HEADER));
    }
}
