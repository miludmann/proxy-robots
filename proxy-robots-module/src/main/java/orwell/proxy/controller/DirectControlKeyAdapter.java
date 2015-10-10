package orwell.proxy.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;

/**
 * Created by Michaël Ludmann on 6/22/15.
 */
public class DirectControlKeyAdapter extends KeyAdapter {
    private final static Logger logback = LoggerFactory.getLogger(DirectControlKeyAdapter.class);
    private static final long KEY_RELEASED_TIME_DELAY = 50;

    private final CommandRobot commandRobot;
    private final HashMap<Integer, Long> keyDownMap;

    public DirectControlKeyAdapter(final CommandRobot commandRobot) {
        this.commandRobot = commandRobot;
        this.keyDownMap = new HashMap<>();
    }

    @Override
    public void keyTyped(final KeyEvent e) {
        logback.debug("++++++ Key typed");
        keyDownMap.put(e.getKeyCode(), e.getWhen());
        commandRobot.commandTyped(e.getKeyChar());
    }

    @Override
    public void keyPressed(final KeyEvent e) {
        logback.debug("++++++ Key pressed");
        keyDownMap.put(e.getKeyCode(), e.getWhen());
        commandRobot.commandPressed(e.getKeyCode());
    }

    @Override
    public void keyReleased(final KeyEvent e) {
        // We filter keyReleased event happening at the exact
        // same time of a keyPressed/Typed event (since linux
        // fires a keyReleased event with a keyDown event...)
        logback.debug("++++++ Key released");

        if (!keyDownMap.containsKey(e.getKeyCode())) {
            commandRobot.commandReleased(e.getKeyCode());
            logback.debug("++++++ Key released 1");
        } else if ((keyDownMap.get(e.getKeyCode()) + KEY_RELEASED_TIME_DELAY) > e.getWhen()) {
            keyDownMap.remove(e.getKeyCode()); // Filter the event
            logback.debug("++++++ Key released 2");
        } else {
            keyDownMap.remove(e.getKeyCode());
            commandRobot.commandReleased(e.getKeyCode());
            logback.debug("++++++ Key released 3");
        }
    }
}