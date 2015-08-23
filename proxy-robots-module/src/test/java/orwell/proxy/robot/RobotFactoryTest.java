package orwell.proxy.robot;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import orwell.proxy.config.ConfigModel;
import orwell.proxy.config.Configuration;
import orwell.proxy.config.elements.ConfigCamera;
import orwell.proxy.config.elements.ConfigRobots;
import orwell.proxy.config.elements.ConfigTank;

import javax.xml.bind.JAXBException;
import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Created by Michaël Ludmann on 5/21/15.
 */
@RunWith(JUnit4.class)
public class RobotFactoryTest {

    private final static Logger logback = LoggerFactory.getLogger(RobotFactoryTest.class);
    private static final String BT_NAME_TEST = "BtNameTest";
    private static final String BT_ID_TEST = "BtIdTest";
    private static final String IMAGE_TEST = "ImageTest";
    private static final String TEMP_ROUTING_ID_TEST = "TempRoutingIdTest";
    private static final String IP_TEST = "IpTest";
    private static final int PORT_TEST = 777;

    @Before
    public void setUp() {
        logback.debug(">>>>>>>>> IN");
    }

    @Test
    public void testGetLegoTank_NullConfigCamera() throws Exception {
        final LegoTank legoTank = (LegoTank) RobotFactory.getRobot(new ConfigTank());
        assertNotNull(IPWebcam.getDummy());
        assertEquals(IPWebcam.getDummy().getUrl(), legoTank.getCameraUrl());
    }

    @Test
    public void testGetLegoTank_NullParameter() throws Exception {
        final LegoTank legoTank = (LegoTank) RobotFactory.getRobot(null);
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
        final LegoTank legoTank = (LegoTank) RobotFactory.getRobot(configTank);
        assertEquals(IMAGE_TEST, legoTank.getImage());
        assertEquals("http://" + IP_TEST + ":" + PORT_TEST, legoTank.getCameraUrl());
    }

    @Test
    public void testGetRobot_Configuration() throws Exception {
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

        final ConfigRobots configRobots = new ConfigRobots();
        final ArrayList<ConfigTank> configTanks = new ArrayList<>();
        configTanks.add(configTank);
        configRobots.setConfigTanks(configTanks);

        final ConfigModel configModel = new ConfigModel();
        configModel.setConfigRobots(configRobots);

        final ConfigurationTest configurationTest = new ConfigurationTest(configModel);

        // Build a tank from the config with existing ID
        final LegoTank legoTank = (LegoTank) RobotFactory.getRobot(configurationTest, TEMP_ROUTING_ID_TEST);
        assertNotNull(legoTank);
        assertEquals(IMAGE_TEST, legoTank.getImage());
        assertEquals("http://" + IP_TEST + ":" + PORT_TEST, legoTank.getCameraUrl());

        // Build a tank from the config with a bad ID
        final LegoTank legoTankNull = (LegoTank) RobotFactory.getRobot(configurationTest, "");
        assertNull(legoTankNull);
    }

    @After
    public void tearDown() throws Exception {
        logback.debug("<<<< OUT");
    }

    private class ConfigurationTest extends Configuration {
        final ConfigModel configModel;

        public ConfigurationTest(final ConfigModel configModel) {
            this.configModel = configModel;
        }

        @Override
        protected void populateFromSource() throws JAXBException {
        }

        public ConfigModel getConfigModel() {
            return configModel;
        }
    }
}
