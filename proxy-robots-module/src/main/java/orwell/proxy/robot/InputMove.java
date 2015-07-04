package orwell.proxy.robot;

import lejos.mf.common.UnitMessage;
import lejos.mf.common.UnitMessageType;
import orwell.messages.Controller.Input;

import static orwell.proxy.Utils.round;

/**
 * Created by Michaël Ludmann on 5/18/15.
 */
public class InputMove implements IRobotInput {

    private final static String MOVE_PAYLOAD_HEADER = "move ";
    private final static int MAX_DECIMAL_LENGTH = 4;
    private Input.Move move;
    private boolean hasMove = false;


    public void setMove(final Input.Move move) {
        this.move = move;
        this.hasMove = true;
    }


    public boolean hasMove() {
        return hasMove;
    }

    @Override
    public void accept(final IRobotInputVisitor visitor) {
        visitor.visit(this);
    }

    public void sendUnitMessageTo(final IRobot robot) {
        // "input move leftMove rightMove"
        final String payload = MOVE_PAYLOAD_HEADER + getTrimmedMove(move.getLeft()) + " " + getTrimmedMove(move.getRight());
        robot.sendUnitMessage(new UnitMessage(UnitMessageType.Command, payload));
    }

    /**
     *
     * @param value
     * @return String of the input value no bigger than MAX_DECIMAL_LENGTH
     * example : MAX_DECIMAL_LENGTH = 4 && input = 0.8999999 -> return = "0.90"
     */
    private String getTrimmedMove(final double value) {
        return Double.toString(round(value, MAX_DECIMAL_LENGTH));
    }

    public Input.Move getMove() {
        return move;
    }
}
