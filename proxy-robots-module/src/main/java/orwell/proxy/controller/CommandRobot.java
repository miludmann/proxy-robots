package orwell.proxy.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import orwell.messages.Controller;
import orwell.proxy.robot.EnumConnectionState;
import orwell.proxy.robot.IRobot;
import orwell.proxy.robot.RobotInputSetVisitor;

/**
 * Created by Michaël Ludmann on 21/06/15.
 */
public class CommandRobot {
    public final static int DEFAULT_SPEED = 75;
    public static final char CHAR_FORWARD = 'w';
    public static final char CHAR_BACKWARD = 's';
    public static final char CHAR_LEFT = 'a';
    public static final char CHAR_RIGHT = 'd';
    public static final char CHAR_DECREASE_SPEED = '-';
    public static final char CHAR_INCREASE_SPEED = '+';
    public static final char CHAR_FIRE_WEAPON1 = '1';
    public static final char CHAR_FIRE_WEAPON2 = '2';
    private final static Logger logback = LoggerFactory.getLogger(CommandRobot.class);
    private final IRobot robot;
    private int speed;

    public CommandRobot(final IRobot robot) {
        this.robot = robot;
        this.speed = DEFAULT_SPEED;
    }

    private static Controller.Input getInput(final double left, final double right,
                                             final boolean weapon1, final boolean weapon2) {
        final Controller.Input.Builder inputBuilder = Controller.Input.newBuilder();

        final Controller.Input.Fire.Builder fireBuilder = Controller.Input.Fire.newBuilder();
        fireBuilder.setWeapon1(weapon1);
        fireBuilder.setWeapon2(weapon2);
        inputBuilder.setFire(fireBuilder.build());


        final Controller.Input.Move.Builder moveBuilder = Controller.Input.Move.newBuilder();
        moveBuilder.setLeft(left);
        moveBuilder.setRight(right);
        inputBuilder.setMove(moveBuilder.build());

        return inputBuilder.build();
    }

    public void commandTyped(final char keyChar) {
        if (EnumConnectionState.CONNECTED != robot.getConnectionState())
            return;

        switch (keyChar) {
            case CHAR_FORWARD:
                goForward();
                break;
            case CHAR_BACKWARD:
                goBackward();
                break;
            case CHAR_LEFT:
                goLeft();
                break;
            case CHAR_RIGHT:
                goRight();
                break;
            case CHAR_DECREASE_SPEED:
                decreaseSpeed();
                break;
            case CHAR_INCREASE_SPEED:
                increaseSpeed();
                break;
            case CHAR_FIRE_WEAPON1:
                fireWeapon1();
                break;
            case CHAR_FIRE_WEAPON2:
                fireWeapon2();
                break;
            default:
                notHandled(keyChar);
                break;
        }
    }

    public void commandPressed(final char keyChar) {
        logback.debug("Command pressed not handled for key: " + keyChar);
    }

    public void commandReleased(final char keyChar) {
        if (EnumConnectionState.CONNECTED != robot.getConnectionState())
            return;

        switch (keyChar) {
            case CHAR_FORWARD:
                stop();
                break;
            case CHAR_BACKWARD:
                stop();
                break;
            case CHAR_LEFT:
                stop();
                break;
            case CHAR_RIGHT:
                stop();
                break;
            case CHAR_DECREASE_SPEED:
                stop();
                break;
            case CHAR_INCREASE_SPEED:
                stop();
                break;
            case CHAR_FIRE_WEAPON1:
                break;
            case CHAR_FIRE_WEAPON2:
                break;
            default:
                notHandled(keyChar);
                break;
        }
    }

    private void notHandled(final char keyChar) {
        logback.debug("Key not handled: " + keyChar);
    }

    private void fireWeapon2() {
        final Controller.Input input = getInput(0, 0, false, true);
        final RobotInputSetVisitor robotInputSetVisitor = new RobotInputSetVisitor(input.toByteArray());
        robot.accept(robotInputSetVisitor);
        logback.debug("Firing weapon 2");
    }

    private void fireWeapon1() {
        final Controller.Input input = getInput(0, 0, true, false);
        final RobotInputSetVisitor robotInputSetVisitor = new RobotInputSetVisitor(input.toByteArray());
        robot.accept(robotInputSetVisitor);
        logback.debug("Firing weapon 1");
    }

    private void increaseSpeed() {
        if (100 > speed) {
            speed++;
            logback.debug("Increased Speed to: " + speed);
        }
    }

    private void decreaseSpeed() {
        if (-100 < speed) {
            speed--;
            logback.debug("Decreased Speed to: " + speed);
        }
    }

    private void goRight() {
        final Controller.Input input = getInput(speed, 0, false, false);
        final RobotInputSetVisitor robotInputSetVisitor = new RobotInputSetVisitor(input.toByteArray());
        robot.accept(robotInputSetVisitor);
        logback.debug("Going right");
    }

    private void goLeft() {
        final Controller.Input input = getInput(0, speed, false, false);
        final RobotInputSetVisitor robotInputSetVisitor = new RobotInputSetVisitor(input.toByteArray());
        robot.accept(robotInputSetVisitor);
        logback.debug("Going left");
    }

    private void goBackward() {
        final Controller.Input input = getInput(-speed, -speed, false, false);
        final RobotInputSetVisitor robotInputSetVisitor = new RobotInputSetVisitor(input.toByteArray());
        robot.accept(robotInputSetVisitor);
        logback.debug("Going backward");
    }

    private void goForward() {
        final Controller.Input input = getInput(speed, speed, false, false);
        final RobotInputSetVisitor robotInputSetVisitor = new RobotInputSetVisitor(input.toByteArray());
        robot.accept(robotInputSetVisitor);
        logback.debug("Going forward");
    }

    private void stop() {
        final Controller.Input input = getInput(0, 0, false, false);
        final RobotInputSetVisitor robotInputSetVisitor = new RobotInputSetVisitor(input.toByteArray());
        robot.accept(robotInputSetVisitor);
        logback.debug("Stopping");

    }

    public double getSpeed() {
        return speed;
    }
}