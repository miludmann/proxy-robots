package orwell.proxy.robot.messages;

/**
 * Created by Michaël Ludmann on 23/08/15.
 */
public interface IRobotMessage {
    void accept(final IRobotMessageVisitor visitor);
}
