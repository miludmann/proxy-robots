package orwell.proxy.controller;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import orwell.proxy.mock.MockedTank;

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
    public void testCommandTyped_Forward() throws Exception {
        assertFalse(mockedTank.getInputMove().hasMove());
        commandRobot.commandTyped(CommandRobot.CHAR_FORWARD);
        assertTrue(mockedTank.getInputMove().hasMove());
        assertTrue(0 < mockedTank.getInputMove().getMove().getLeft());
        assertTrue(0 < mockedTank.getInputMove().getMove().getRight());
    }

    @Test
    public void testCommandTyped_Backward() throws Exception {
        assertFalse(mockedTank.getInputMove().hasMove());
        commandRobot.commandTyped(CommandRobot.CHAR_BACKWARD);
        assertTrue(mockedTank.getInputMove().hasMove());
        assertTrue(0 > mockedTank.getInputMove().getMove().getLeft());
        assertTrue(0 > mockedTank.getInputMove().getMove().getRight());
    }

    @Test
    public void testCommandTyped_Left() throws Exception {
        assertFalse(mockedTank.getInputMove().hasMove());
        commandRobot.commandTyped(CommandRobot.CHAR_LEFT);
        assertTrue(mockedTank.getInputMove().hasMove());
        assertEquals(0, mockedTank.getInputMove().getMove().getLeft(), 0);
        assertTrue(0 < mockedTank.getInputMove().getMove().getRight());
    }

    @Test
    public void testCommandTyped_Right() throws Exception {
        assertFalse(mockedTank.getInputMove().hasMove());
        commandRobot.commandTyped(CommandRobot.CHAR_RIGHT);
        assertTrue(mockedTank.getInputMove().hasMove());
        assertTrue(0 < mockedTank.getInputMove().getMove().getLeft());
        assertEquals(0, mockedTank.getInputMove().getMove().getRight(), 0);
    }

    @Test
    public void testCommandTyped_DecreaseSpeed() throws Exception {
        assertEquals(CommandRobot.DEFAULT_SPEED, commandRobot.getSpeed(), 1);
        commandRobot.commandTyped(CommandRobot.CHAR_DECREASE_SPEED);
        assertEquals(CommandRobot.DEFAULT_SPEED - 1, commandRobot.getSpeed(), 1);
    }

    @Test
    public void testCommandTyped_IncreaseSpeed() throws Exception {
        assertEquals(CommandRobot.DEFAULT_SPEED, commandRobot.getSpeed(), 1);
        commandRobot.commandTyped(CommandRobot.CHAR_INCREASE_SPEED);
        assertEquals(CommandRobot.DEFAULT_SPEED + 1, commandRobot.getSpeed(), 1);
    }

    @Test
    public void testCommandReleased_Stop() throws Exception {
        assertFalse(mockedTank.getInputMove().hasMove());
        commandRobot.commandReleased(CommandRobot.CHAR_FORWARD);
        assertTrue(mockedTank.getInputMove().hasMove());
        assertEquals(0, mockedTank.getInputMove().getMove().getLeft(), 0);
        assertEquals(0, mockedTank.getInputMove().getMove().getRight(), 0);
    }

    @After
    public void tearDown() throws Exception {
        logback.debug("<<<< OUT");
    }
}

