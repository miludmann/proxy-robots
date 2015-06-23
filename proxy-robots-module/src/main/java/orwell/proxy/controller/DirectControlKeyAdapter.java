package orwell.proxy.controller;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Created by Michaël Ludmann on 6/22/15.
 */
public class DirectControlKeyAdapter extends KeyAdapter {
    private final CommandRobot commandRobot;

    public DirectControlKeyAdapter(final CommandRobot commandRobot) {
        this.commandRobot = commandRobot;
    }

    @Override
    public void keyTyped(final KeyEvent e) {
        commandRobot.commandTyped(e.getKeyChar());
    }

    @Override
    public void keyPressed(final KeyEvent e) {
        commandRobot.commandPressed(e.getKeyChar());
    }

    @Override
    public void keyReleased(final KeyEvent e) {
        commandRobot.commandReleased(e.getKeyChar());
    }
}