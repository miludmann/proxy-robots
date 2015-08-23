package orwell.proxy.robot;

import junit.framework.AssertionFailedError;
import lejos.mf.common.UnitMessage;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import orwell.proxy.ProtobufTest;
import orwell.proxy.mock.MockedTank;
import orwell.proxy.robot.messages.MessageInput;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;


/**
 * Created by Michaël Ludmann on 11/04/15.
 */
@RunWith(JUnit4.class)
public class MessageInputTest {
    private final static Logger logback = LoggerFactory.getLogger(MessageInputTest.class);

    @TestSubject
    private MessageInput messageInput;

    @Mock
    private LegoTank tank;


    @Before
    public void setUp() {
        logback.debug(">>>>>>>>> IN");
        messageInput = new MessageInput(ProtobufTest.getTestInput().toByteArray());
    }


    @Test
    public void testToString() {
        MockedTank mockedTank = new MockedTank();
        assertEquals("Controller INPUT of Robot [tempRoutingId]:\n" +
                        "\t|___Move order: [LEFT] 50.5 \t\t[RIGHT] 10.0\n" +
                        "\t|___Fire order: [WEAPON1] false \t[WEAPON2] true",
                messageInput.toString(mockedTank));
    }


    @Test
    public void testVisit_Robot_Empty() {
        messageInput = new MessageInput(ProtobufTest.getEmptyTestInput().toByteArray());

        // Mock the tank
        tank = createMock(LegoTank.class);
        tank.sendUnitMessage(anyObject(UnitMessage.class));
        // We should not send any unitMessage (or we throw an exception)
        expectLastCall().andThrow(new AssertionFailedError("Tank should not send an unitMessage")).anyTimes();
        tank.setRobotState(anyObject(RobotState.class));
        expectLastCall().once();
        replay(tank);

        // Perform the actual visit
        messageInput.visit(tank);

        verify(tank);
    }


    @Test
    public void testVisit_Robot_Full() {
        // Mock the tank
        tank = createMock(LegoTank.class);
        tank.sendUnitMessage(anyObject(UnitMessage.class));
        expectLastCall().times(2); // we should send two unitMessages
        tank.setRobotState(anyObject(RobotState.class));
        expectLastCall().once();
        replay(tank);

        // Perform the actual visit
        messageInput.visit(tank);

        verify(tank);
    }

    @After
    public void tearDown() throws Exception {
        logback.debug("<<<< OUT");
    }
}
