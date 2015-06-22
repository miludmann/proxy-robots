package orwell.proxy.robot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import orwell.proxy.config.ConfigModel;
import orwell.proxy.config.Configuration;
import orwell.proxy.config.elements.*;

import java.net.MalformedURLException;
import java.util.ArrayList;

/**
 * Created by Michaël Ludmann on 5/21/15.
 */
public final class RobotFactory {
    private final static Logger logback = LoggerFactory.getLogger(RobotFactory.class);

    public static IRobot getRobot(final IConfigRobot configRobot) {
        if (null == configRobot) {
            return null;
        }

        if (configRobot instanceof ConfigTank) {
            return getRobot((ConfigTank) configRobot);
        }
        if (configRobot instanceof ConfigScout) {
            return getRobot((ConfigScout) configRobot);
        }

        return null;
    }

    public static IRobot getRobot(final Configuration configuration, final String tempRoutingId) {
        final ConfigModel configModel = configuration.getConfigModel();
        final ConfigRobots configRobots = configModel.getConfigRobots();
        final ArrayList<IConfigRobot> robots = configRobots.getConfigRobotsToRegister();

        for (final IConfigRobot configRobot : robots) {
            if (tempRoutingId.equals(configRobot.getTempRoutingID())) {
                logback.info("Found matching robot in the config: " + tempRoutingId);
                return getRobot(configRobot);
            }
        }
        logback.info("Not matching robot found in the config: " + tempRoutingId);
        return null;
    }

    private static IRobot getRobot(final ConfigTank configTank) {
        final IConfigCamera configCamera = configTank.getConfigCamera();
        if (null == configCamera) {
            logback.warn("Config of camera is missing for LegoTank: " + configTank.getBluetoothName());
            logback.warn("Using dummy camera");
            return new LegoTank(configTank.getBluetoothName(),
                    configTank.getBluetoothID(), IPWebcam.getDummy(), configTank.getImage());
        } else {
            final IPWebcam ipWebcam;
            try {
                ipWebcam = new IPWebcam(configCamera);
            } catch (final MalformedURLException e) {
                logback.error("Url of robot " + configTank.getBluetoothName() +
                        " is malformed. Robot will not be instantiated. " +
                        e.getMessage());
                return null;
            }
            //TODO Improve initialization of setImage to get something meaningful from the string (actual image)
            return new LegoTank(configTank.getBluetoothName(),
                    configTank.getBluetoothID(), ipWebcam, configTank.getImage());
        }
    }

    private static IRobot getRobot(final ConfigScout configScout) {
        logback.warn("Scout config is not handled yet");
        return null;
    }
}
