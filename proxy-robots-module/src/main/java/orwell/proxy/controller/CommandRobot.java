package orwell.proxy.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import orwell.messages.Controller;
import orwell.proxy.EnumGameState;
import orwell.proxy.robot.EnumConnectionState;
import orwell.proxy.robot.IRobot;
import orwell.proxy.robot.RobotGameStateVisitor;
import orwell.proxy.robot.messages.IRobotMessageVisitor;
import orwell.proxy.robot.messages.MessageInput;
import orwell.proxy.robot.messages.MessageStopTank;

import java.awt.event.KeyEvent;

/**
 * Created by Michaël Ludmann on 21/06/15.
 */
public class CommandRobot {
    public final static double DEFAULT_SPEED = 0.75;
    private final static Logger logback = LoggerFactory.getLogger(CommandRobot.class);
    private static final double INCREMENTAL_SPEED_CHANGE = 0.01;
    private final IRobot robot;
    private double speed;

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

    public void commandTyped(final int keyCode) {

    }

    public void commandPressed(final int keyCode) {
        if (EnumConnectionState.CONNECTED != robot.getConnectionState())
            return;

        switch (keyCode) {
            case KeyEvent.VK_W:
            case KeyEvent.VK_UP:
                goForward();
                break;
            case KeyEvent.VK_S:
            case KeyEvent.VK_DOWN:
                goBackward();
                break;
            case KeyEvent.VK_A:
            case KeyEvent.VK_LEFT:
                goLeft();
                break;
            case KeyEvent.VK_D:
            case KeyEvent.VK_RIGHT:
                goRight();
                break;
            case KeyEvent.VK_SUBTRACT:
            case KeyEvent.VK_MINUS:
                decreaseSpeed();
                break;
            case KeyEvent.VK_ADD:
            case KeyEvent.VK_PLUS:
                increaseSpeed();
                break;
            case KeyEvent.VK_NUMPAD1:
            case KeyEvent.VK_1:
                fireWeapon1();
                break;
            case KeyEvent.VK_NUMPAD2:
            case KeyEvent.VK_2:
                fireWeapon2();
                break;
            case KeyEvent.VK_SPACE:
                stopTank();
                break;
            case KeyEvent.VK_7:
                victory();
                break;
            case KeyEvent.VK_8:
                defeat();
                break;
            case KeyEvent.VK_9:
                draw();
                break;
            default:
                notHandled(keyCode);
                break;
        }
    }

    private void draw() {
        final RobotGameStateVisitor robotGameStateVisitor = new RobotGameStateVisitor(EnumGameState.FINISHED, null);
        robot.accept(robotGameStateVisitor);
        logback.debug("Draw");
    }

    private void defeat() {
        final RobotGameStateVisitor robotGameStateVisitor = new RobotGameStateVisitor(EnumGameState.FINISHED, "otherTeam");
        robot.accept(robotGameStateVisitor);
        logback.debug("Defeat");
    }

    private void victory() {
        final RobotGameStateVisitor robotGameStateVisitor = new RobotGameStateVisitor(EnumGameState.FINISHED, robot.getTeamName());
        robot.accept(robotGameStateVisitor);
        logback.debug("Victory");
    }

    public void commandReleased(final int keyCode) {
        if (EnumConnectionState.CONNECTED != robot.getConnectionState())
            return;

        switch (keyCode) {
            case KeyEvent.VK_W:
            case KeyEvent.VK_UP:
                stopTank();
                break;
            case KeyEvent.VK_S:
            case KeyEvent.VK_DOWN:
                stopTank();
                break;
            case KeyEvent.VK_A:
            case KeyEvent.VK_LEFT:
                stopTank();
                break;
            case KeyEvent.VK_D:
            case KeyEvent.VK_RIGHT:
                stopTank();
                break;
            case KeyEvent.VK_SUBTRACT:
            case KeyEvent.VK_MINUS:
                stopTank();
                break;
            case KeyEvent.VK_ADD:
            case KeyEvent.VK_PLUS:
                stopTank();
                break;
            case KeyEvent.VK_NUMPAD1:
            case KeyEvent.VK_1:
                break;
            case KeyEvent.VK_NUMPAD2:
            case KeyEvent.VK_2:
                break;
            case KeyEvent.VK_SPACE:
                stopTank();
                break;
            case KeyEvent.VK_7:
                stopTank();
                break;
            case KeyEvent.VK_8:
                stopTank();
                break;
            case KeyEvent.VK_9:
                stopTank();
                break;
            default:
                notHandled(keyCode);
                break;
        }
    }

    private void notHandled(final int keyCode) {
        logback.debug("Key not handled: " + keyCode);
    }

    private void fireWeapon2() {
        final Controller.Input input = getInput(0, 0, false, true);
        final IRobotMessageVisitor robotMessage = new MessageInput(input.toByteArray());
        robot.accept(robotMessage);
        logback.debug("Firing weapon 2");
    }

    private void fireWeapon1() {
        final Controller.Input input = getInput(0, 0, true, false);
        final IRobotMessageVisitor robotMessage = new MessageInput(input.toByteArray());
        robot.accept(robotMessage);
        logback.debug("Firing weapon 1");
    }

    private void increaseSpeed() {
        if (1 > speed) {
            speed += INCREMENTAL_SPEED_CHANGE;
            logback.debug("Increased Speed to: " + speed);
        }
    }

    private void decreaseSpeed() {
        if (0 < speed) {
            speed -= INCREMENTAL_SPEED_CHANGE;
            logback.debug("Decreased Speed to: " + speed);
        }
    }

    private void goRight() {
        final Controller.Input input = getInput(speed, 0, false, false);
        final IRobotMessageVisitor robotMessage = new MessageInput(input.toByteArray());
        robot.accept(robotMessage);
        logback.debug("Going right");
    }

    private void goLeft() {
        final Controller.Input input = getInput(0, speed, false, false);
        final IRobotMessageVisitor robotMessage = new MessageInput(input.toByteArray());
        robot.accept(robotMessage);
        logback.debug("Going left");
    }

    private void goBackward() {
        final Controller.Input input = getInput(-speed, -speed, false, false);
        final IRobotMessageVisitor robotMessage = new MessageInput(input.toByteArray());
        robot.accept(robotMessage);
        logback.debug("Going backward");
    }

    private void goForward() {
        final Controller.Input input = getInput(speed, speed, false, false);
        final IRobotMessageVisitor robotMessage = new MessageInput(input.toByteArray());
        robot.accept(robotMessage);
        logback.debug("Going forward");
    }

    private void stopTank() {
        final IRobotMessageVisitor robotMessage = new MessageStopTank();
        robot.accept(robotMessage);
        logback.debug("Stopping tank");
    }

    public double getSpeed() {
        return speed;
    }
}