package orwell.proxy.config;

import javax.xml.bind.annotation.XmlElement;
import java.util.List;

public class ConfigProxy implements IConfigProxy {

    private List<ConfigServerGame> configServerGames;
    private int senderLinger;
    private int receiverLinger;
    private int outgoingMsgFrequency;

    @XmlElement(name = "server-game")
    public List<ConfigServerGame> getConfigServerGames() {
        return configServerGames;
    }

    public void setConfigServerGames(final List<ConfigServerGame> configServerGames) {
        this.configServerGames = configServerGames;
    }

    /**
     * @return the server game configuration of the highest priority
     * If no priority defined, it is the first one found by default.
     */
    @Override
    public ConfigServerGame getConfigServerGame() {
        ConfigServerGame maxPriorityConfig = null;
        for (final ConfigServerGame config : this.configServerGames) {
            if (null == maxPriorityConfig || config.getPriority() > maxPriorityConfig.getPriority()) {
                maxPriorityConfig = config;
            }
        }
        return maxPriorityConfig;
    }

    @Override
    public int getSenderLinger() {
        return senderLinger;
    }

    @XmlElement(name = "senderLinger")
    public void setSenderLinger(final int senderLinger) {
        this.senderLinger = senderLinger;
    }

    @Override
    public int getReceiverLinger() {
        return receiverLinger;
    }

    @XmlElement(name = "receiverLinger")
    public void setReceiverLinger(final int receiverLinger) {
        this.receiverLinger = receiverLinger;
    }

    @Override
    public int getOutgoingMsgFrequency() {
        return outgoingMsgFrequency;
    }

    @XmlElement(name = "outgoingMsgFrequency")
    public void setOutgoingMsgFrequency(final int outgoingMsgFrequency) {
        this.outgoingMsgFrequency = outgoingMsgFrequency;
    }
}
