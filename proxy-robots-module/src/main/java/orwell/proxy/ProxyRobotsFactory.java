package orwell.proxy;

import orwell.proxy.config.ConfigCli;
import orwell.proxy.config.ConfigFactory;
import orwell.proxy.robot.RobotsMap;
import orwell.proxy.zmq.FrequencyFilter;
import orwell.proxy.zmq.IFilter;
import orwell.proxy.zmq.ZmqMessageBroker;

import java.util.ArrayList;

/**
 * Created by parapampa on 03/05/15.
 */
public class ProxyRobotsFactory {
    private final ConfigFactory configFactory;
    private final ZmqMessageBroker zmqMessageFramework;

    public ProxyRobotsFactory(final ConfigCli configPathType) {
        configFactory = new ConfigFactory(configPathType);

        if (null == configFactory.getConfigProxy()) {
            zmqMessageFramework = null;
        } else {
            zmqMessageFramework = new ZmqMessageBroker(configFactory.getConfigProxy().getSenderLinger(),
                    configFactory.getConfigProxy().getReceiverLinger(),
                    null);
        }
    }

    public ProxyRobots getProxyRobots() {
        if (null == zmqMessageFramework) {
            return null;
        }
        return new ProxyRobots(zmqMessageFramework,
                configFactory.getConfigServerGame(),
                configFactory.getConfigRobots(),
                new RobotsMap());

    }

    // TODO Frequency filter should be revised ; we do not want to filter
    // necessary messages for the server
    private ArrayList<IFilter> getFilterList() {
        final ArrayList<IFilter> filterList = new ArrayList<>();
        filterList.add(new FrequencyFilter(configFactory.getConfigProxy().getOutgoingMsgFrequency()));

        return filterList;
    }
}
