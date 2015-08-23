package orwell.proxy.robot;

import orwell.proxy.robot.messages.InputFire;
import orwell.proxy.robot.messages.InputMove;

/**
 * Created by Michaël Ludmann on 23/08/15.
 */
public class RobotState {
    private InputMove move;
    private InputFire fire;
    private boolean hasStop;

    public InputMove getMove() {
        return move;
    }

    public void setMove(InputMove move) {
        this.move = move;
    }

    public InputFire getFire() {
        return fire;
    }

    public void setFire(InputFire fire) {
        this.fire = fire;
    }

    public boolean hasFire() {
        return null != fire && fire.hasFire();
    }

    public boolean hasMove() {
        return null != move && move.hasMove();
    }

    public boolean hasStop() {
        return hasStop;
    }

    public void setHasStop(boolean hasStop) {
        this.hasStop = hasStop;
    }
}
