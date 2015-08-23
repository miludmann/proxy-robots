package orwell.proxy.controller;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import orwell.proxy.mock.MockedTank;

import java.awt.event.KeyEvent;

import static org.junit.Assert.*;

/**
 * Created by Michaël Ludmann on 6/23/15.
 */
@RunWith(JUnit4.class)
public class CommandRobotTest {
    private final static Logger logback = LoggerFactory.getLogger(CommandRobotTest.class);
    private MockedTank mockedTank;
    private CommandRobot commandRobot;

    @Before
    public void setUp() throws Exception {
        logback.debug(">>>>>>>>> IN");
        mockedTank = new MockedTank();
        mockedTank.connect();
        commandRobot = new CommandRobot(mockedTank);
    }

    @Test
    public void testCommandPressed_Forward() throws Exception {
        assertFalse(mockedTank.getRobotState().hasMove());
        commandRobot.commandPressed(KeyEvent.VK_UP);
        assertTrue(mockedTank.getRobotState().hasMove());
        assertTrue(0 < mockedTank.getRobotState().getMove().getMove().getLeft());
        assertTrue(0 < mockedTank.getRobotState().getMove().getMove().getRight());
    }

    @Test
    public void testCommandPressed_Backward() throws Exception {
        assertFalse(mockedTank.getRobotState().hasMove());
        commandRobot.commandPressed(KeyEvent.VK_DOWN);
        assertTrue(mockedTank.getRobotState().hasMove());
        assertTrue(0 > mockedTank.getRobotState().getMove().getMove().getLeft());
        assertTrue(0 > mockedTank.getRobotState().getMove().getMove().getRight());
    }

    @Test
    public void testCommandPressed_Left() throws Exception {
        assertFalse(mockedTank.getRobotState().hasMove());
        commandRobot.commandPressed(KeyEvent.VK_LEFT);
        assertTrue(mockedTank.getRobotState().hasMove());
        assertEquals(0, mockedTank.getRobotState().getMove().getMove().getLeft(), 0);
        assertTrue(0 < mockedTank.getRobotState().getMove().getMove().getRight());
    }

    @Test
    public void testCommandPressed_Right() throws Exception {
        assertFalse(mockedTank.getRobotState().hasMove());
        commandRobot.commandPressed(KeyEvent.VK_RIGHT);
        assertTrue(mockedTank.getRobotState().hasMove());
        assertTrue(0 < mockedTank.getRobotState().getMove().getMove().getLeft());
        assertEquals(0, mockedTank.getRobotState().getMove().getMove().getRight(), 0);
    }

    @Test
    public void testCommandPressed_DecreaseSpeed() throws Exception {
        assertEquals(CommandRobot.DEFAULT_SPEED, commandRobot.getSpeed(), 1);
        commandRobot.commandPressed(KeyEvent.VK_MINUS);
        assertEquals(CommandRobot.DEFAULT_SPEED - 1, commandRobot.getSpeed(), 1);
    }

    @Test
    public void testCommandPressed_IncreaseSpeed() throws Exception {
        assertEquals(CommandRobot.DEFAULT_SPEED, commandRobot.getSpeed(), 1);
        commandRobot.commandPressed(KeyEvent.VK_PLUS);
        assertEquals(CommandRobot.DEFAULT_SPEED + 1, commandRobot.getSpeed(), 1);
    }

    @Test
    public void testCommandReleased_Stop() throws Exception {
        assertFalse(mockedTank.getRobotState().hasStop());
        commandRobot.commandReleased(KeyEvent.VK_UP);
        assertTrue(mockedTank.getRobotState().hasStop());
    }

    @After
    public void tearDown() throws Exception {
        logback.debug("<<<< OUT");
    }
}

