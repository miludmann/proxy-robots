package orwell.proxy.robot.messages;

import lejos.mf.common.UnitMessage;
import lejos.mf.common.UnitMessageType;
import orwell.messages.Controller;
import orwell.proxy.robot.IRobot;

/**
 * Created by Michaël Ludmann on 5/18/15.
 */
public class InputFire {

    private final static String FIRE_PAYLOAD_HEADER = "fire ";
    private Controller.Input.Fire fire;
    private boolean hasFire = false;

    public InputFire(Controller.Input.Fire fire) {
        setFire(fire);
    }

    public void setFire(final Controller.Input.Fire fire) {
        this.fire = fire;
        this.hasFire = true;
    }

    public boolean hasFire() {
        return hasFire;
    }

    public void sendUnitMessageTo(final IRobot robot) {
        // "input fire fireWeapon1 fireWeapon2"
        if (fire.getWeapon1() || fire.getWeapon2()) // We avoid flooding the robot if there is no fire
            robot.sendUnitMessage(
                    new UnitMessage(
                            UnitMessageType.Command, FIRE_PAYLOAD_HEADER +
                            fire.getWeapon1() + " " + fire.getWeapon2())
            );
    }
}
