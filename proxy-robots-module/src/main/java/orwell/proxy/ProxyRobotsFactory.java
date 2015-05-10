package orwell.proxy;

import orwell.proxy.config.ConfigCli;
import orwell.proxy.config.ConfigFactory;
import orwell.proxy.robot.RobotsMap;
import orwell.proxy.zmq.FrequencyFilter;
import orwell.proxy.zmq.IFilter;
import orwell.proxy.zmq.ZmqMessageFramework;

import java.util.ArrayList;

/**
 * Created by parapampa on 03/05/15.
 */
public class ProxyRobotsFactory {
    private final ConfigFactory configFactory;
    private final ZmqMessageFramework zmqMessageFramework;

    public ProxyRobotsFactory(final ConfigCli configPathType) {
        configFactory = new ConfigFactory(configPathType);

        zmqMessageFramework = new ZmqMessageFramework(configFactory.getConfigProxy().getSenderLinger(),
                configFactory.getConfigProxy().getReceiverLinger(),
                null);
    }

    public ProxyRobots getProxyRobots() {
        return new ProxyRobots(zmqMessageFramework,
                configFactory.getConfigServerGame(),
                configFactory.getConfigRobots(),
                new RobotsMap());
    }

    // TODO Frequency filter should be revised ; we do not want to filter
    // necessary messages for the server
    private ArrayList<IFilter> getFilterList(){
        final ArrayList<IFilter> filterList = new ArrayList<>();
        filterList.add(new FrequencyFilter(configFactory.getConfigProxy().getOutgoingMsgFrequency()));

        return filterList;
    }
}
