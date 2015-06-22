package orwell.proxy.controller;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Created by Michaël Ludmann on 6/22/15.
 */
public class DirectControlKeyAdapter extends KeyAdapter {
    private final CmdRobot cmdRobot;

    public DirectControlKeyAdapter(final CmdRobot cmdRobot) {
        this.cmdRobot = cmdRobot;
    }

    @Override
    public void keyTyped(final KeyEvent e) {
        cmdRobot.commandTyped(e.getKeyChar());
    }

    @Override
    public void keyPressed(final KeyEvent e) {
        cmdRobot.commandPressed(e.getKeyChar());
    }

    @Override
    public void keyReleased(final KeyEvent e) {
        cmdRobot.commandReleased(e.getKeyChar());
    }
}