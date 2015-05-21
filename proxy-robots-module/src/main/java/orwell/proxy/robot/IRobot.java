package orwell.proxy.robot;

import lejos.mf.common.UnitMessage;

/**
 * Created by Michaël Ludmann on 5/18/15.
 */
public abstract class IRobot implements IRobotElement, IRobotInput {

    private String routingId = "";
    private String cameraUrl;
    private String image;
    private String teamName = "";
    private EnumRegistrationState registrationState = EnumRegistrationState.NOT_REGISTERED;
    private EnumConnectionState connectionState = EnumConnectionState.NOT_CONNECTED;

    public abstract void sendUnitMessage(UnitMessage unitMessage);

    public abstract EnumConnectionState connect();

    public abstract void closeConnection();


    public String getRoutingId() {
        return routingId;
    }

    public void setRoutingId(final String routingId) {
        this.routingId = routingId;
    }

    public String getCameraUrl() {
        return cameraUrl;
    }

    protected void setCameraUrl(final String cameraUrl) {
        this.cameraUrl = cameraUrl;
    }

    String getImage() {
        return image;
    }

    protected void setImage(final String image) {
        this.image = image;
    }

    public String getTeamName() {
        return teamName;
    }

    void setTeamName(final String teamName) {
        this.teamName = teamName;
    }

    public EnumRegistrationState getRegistrationState() {
        return registrationState;
    }

    protected void setRegistrationState(final EnumRegistrationState registrationState) {
        this.registrationState = registrationState;
    }

    public EnumConnectionState getConnectionState() {
        return connectionState;
    }

    protected void setConnectionState(final EnumConnectionState connectionState) {
        this.connectionState = connectionState;
    }

}
