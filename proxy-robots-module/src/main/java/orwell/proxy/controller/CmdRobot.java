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
public class CmdRobot {
    public final static Logger logback = LoggerFactory.getLogger(CmdRobot.class);

    private final IRobot robot;
    private int speed = 75;

    public CmdRobot(IRobot robot) {
        this.robot = robot;
    }

    public void commandTyped(char keyChar) {
        if(EnumConnectionState.CONNECTED != robot.getConnectionState())
            return;

        switch(keyChar) {
            case 'w':
                goForward();
                break;
            case 's':
                goBackward();
                break;
            case 'a':
                goLeft();
                break;
            case 'd':
                goRight();
                break;
            case '-':
                decreaseSpeed();
                break;
            case '+':
                increaseSpeed();
                break;
            case '1':
                fireWeapon1();
                break;
            case '2':
                fireWeapon2();
                break;
        }
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
        if(speed < 100) {
            speed++;
            logback.debug("Increased Speed to: " + speed);
        }
    }

    private void decreaseSpeed() {
        if(speed > -100) {
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

    public void commandPressed(char keyChar) {
    }

    public void commandReleased(char keyChar) {
        if (EnumConnectionState.CONNECTED != robot.getConnectionState())
            return;

        switch (keyChar) {
            case 'w':
                stop();
                break;
            case 's':
                stop();
                break;
            case 'a':
                stop();
                break;
            case 'd':
                stop();
                break;
            case '-':
                stop();
                break;
            case '+':
                stop();
                break;
            case '1':
                break;
            case '2':
                break;
        }
    }


    private void stop() {
        final Controller.Input input = getInput(0, 0, false, false);
        final RobotInputSetVisitor robotInputSetVisitor = new RobotInputSetVisitor(input.toByteArray());
        robot.accept(robotInputSetVisitor);
        logback.debug("Stopping");

    }

    public static Controller.Input getInput(double left, double right,
                                            boolean weapon1, boolean weapon2) {
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
}