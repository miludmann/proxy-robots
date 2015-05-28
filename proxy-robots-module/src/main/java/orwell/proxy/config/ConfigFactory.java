package orwell.proxy.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Michaël Ludmann on 03/05/15.
 */
public class ConfigFactory implements IConfigFactory {
    private final static Logger logback = LoggerFactory.getLogger(ConfigFactory.class);

    private IConfigProxy configProxy;
    private IConfigRobots configRobots;
    private IConfigServerGame configServerGame;
    private IConfigUdpBroadcast configUdpBroadcast;

    private ConfigFactory(final ConfigFactoryParameters configFactoryParameters) {
        logback.debug("IN");
        final Configuration configuration = new Configuration(configFactoryParameters);

        if (!configuration.isPopulated()) {
            logback.error("Configuration loading error");
        } else {
            final ConfigModel configModel = configuration.getConfigModel();
            configProxy = configModel.getConfigProxy();
            configRobots = configuration.getConfigModel().getConfigRobots();
            configServerGame = configProxy.getMaxPriorityConfigServerGame();
            configUdpBroadcast = configProxy.getConfigUdpBroadcast();
        }
        logback.debug("OUT");
    }

    public static ConfigFactory createConfigFactory(final ConfigFactoryParameters configFactoryParameters) {
        return new ConfigFactory(configFactoryParameters);
    }

    @Override
    public IConfigProxy getConfigProxy() {
        return configProxy;
    }

    @Override
    public IConfigRobots getConfigRobots() {
        return configRobots;
    }

    @Override
    public IConfigServerGame getMaxPriorityConfigServerGame() {
        return configServerGame;
    }
}
