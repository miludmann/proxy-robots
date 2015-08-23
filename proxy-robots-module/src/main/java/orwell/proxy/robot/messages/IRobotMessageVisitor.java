package orwell.proxy.robot.messages;

import orwell.proxy.robot.IRobot;

/**
 * Created by Michaël Ludmann on 5/18/15.
 */
public interface IRobotMessageVisitor {
    void visit(final IRobot robot);
}
