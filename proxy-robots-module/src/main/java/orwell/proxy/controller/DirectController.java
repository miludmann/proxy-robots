package orwell.proxy.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import orwell.proxy.Cli;
import orwell.proxy.config.ConfigModel;
import orwell.proxy.config.Configuration;
import orwell.proxy.config.elements.ConfigRobots;
import orwell.proxy.config.elements.IConfigRobot;
import orwell.proxy.robot.IRobot;
import orwell.proxy.robot.LegoTank;
import orwell.proxy.robot.RobotFactory;

import java.util.ArrayList;

/**
 * Created by Michaël Ludmann on 6/18/15.
 */
public class DirectController {
    private final static Logger logback = LoggerFactory.getLogger(DirectController.class);
    private final IRobot robot;
    private final GameGUI gameGUI;
    private final CmdRobot cmdRobot;

    public DirectController(final IRobot robot) {
        this.robot = robot;
        this.gameGUI = new GameGUI();
        this.cmdRobot = new CmdRobot(robot);
    }

    public static void main(final String[] args) throws Exception {
        final Configuration configuration = new Cli(args).parse();
        if (null == configuration) {
            logback.warn("Command Line Interface did not manage to extract a configuration. Exiting now.");
            System.exit(0);
        }

        final DirectController controller =
                new DirectController(RobotFactory.getRobot(configuration, "BananaOne"));
        controller.start();
    }

    private void start() {
        robot.connect();
        gameGUI.addKeyListener(new DirectControlKeyAdapter(getCmdRobot()));
    }

    private CmdRobot getCmdRobot() {
        return cmdRobot;
    }
}
