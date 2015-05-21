package orwell.proxy.robot;

import org.easymock.TestSubject;
import org.junit.Test;
import orwell.proxy.config.ConfigCamera;
import orwell.proxy.config.ConfigTank;
import orwell.proxy.config.IConfigCamera;
import orwell.proxy.mock.MockedConfigCamera;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
/**
 * Created by Michaël Ludmann on 5/21/15.
 */
public class RobotFactoryTest {

    private static final String BT_NAME_TEST = "BtNameTest";
    private static final String BT_ID_TEST = "BtIdTest";
    private static final String IMAGE_TEST = "ImageTest";
    private static final String TEMP_ROUTING_ID_TEST = "TempRoutingIdTest";
    private static final String IP_TEST = "IpTest";
    private static final int PORT_TEST = 777;

    @Test
    public void testGetLegoTank_NullConfigCamera() throws Exception {
        final LegoTank legoTank = RobotFactory.getLegoTank(new ConfigTank());
        assertEquals(IPWebcam.getDummy().getUrl(), legoTank.getCameraUrl());
    }

    @Test
    public void testGetLegoTank_NullParameter() throws Exception {
        final LegoTank legoTank = RobotFactory.getLegoTank(null);
        assertNull(legoTank);
    }

    @Test
    public void testGetLegoTank_normalConfig() throws Exception {
        // Manually setup the configuration of tank
        final ConfigTank configTank = new ConfigTank();
        configTank.setBluetoothID(BT_ID_TEST);
        configTank.setBluetoothName(BT_NAME_TEST);
        configTank.setImage(IMAGE_TEST);
        configTank.setShouldRegister(true);
        configTank.setTempRoutingID(TEMP_ROUTING_ID_TEST);

        // idem for camera of tank
        final ConfigCamera configCamera = new ConfigCamera();
        configCamera.setIp(IP_TEST);
        configCamera.setPort(PORT_TEST);
        configTank.setCamera(configCamera);

        // Build a tank from the config
        final LegoTank legoTank = RobotFactory.getLegoTank(configTank);
        assertEquals(IMAGE_TEST, legoTank.getImage());
        assertEquals("http://" + IP_TEST + ":" + PORT_TEST, legoTank.getCameraUrl());
    }
}
