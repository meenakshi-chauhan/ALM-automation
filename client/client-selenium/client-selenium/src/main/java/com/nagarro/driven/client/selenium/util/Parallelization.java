package com.nagarro.driven.client.selenium.util;

import com.nagarro.driven.client.selenium.config.SeleniumConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is used to start the hub and node in command prompt if parallel execution is enabled.
 *
 * @author nagarro
 */
public class Parallelization {

    private static final Logger log = LoggerFactory.getLogger(Parallelization.class);
    private static final SeleniumConfig.SeleniumConfigSpec config = SeleniumConfig.getInstance();

    private Parallelization() {
        // no instantiation allowed
    }

    /**
     * Start the hub and checks if video recording is enabled or not (to start the hub accordingly).
     */
    public static void startHub() {
        try {
            StringBuilder builder = new StringBuilder("java ");
            if (config.videoRecording()) {
                builder
                        .append(
                                "-Dvideo.storage=\"com.aimmac23.hub.videostorage.LocalFileVideoStore\" -Dvideo.path=")
                        .append(config.testVideoPath())
                        .append("-%DATE:/=-%_%TIME::=-%")
                        .append(" -cp \"selenium-video-node-2.8.jar;selenium-server-standalone-3.14.0.jar\" ")
                        .append(
                                "org.openqa.grid.selenium.GridLauncherV3 -servlets com.aimmac23.hub.servlet" +
                                        ".HubVideoInfoServlet,com.aimmac23.hub.servlet.HubVideoDownloadServlet ");
            } else {
                builder.append("-jar selenium-server-standalone-3.14.0.jar ");
            }

            builder.append("-role hub");
            Runtime.getRuntime()
                    .exec("cmd /c start cmd.exe /K \"cd " + config.driverPath() + " && " + builder + "\"");

        } catch (Exception exception) {
            log.error("Exception occurred while exection hub", exception);
        }
    }

    /**
     * Start the node and checks if video recording is enabled or not (to start the node accordingly).
     */
    public static void startNode() {
        try {
            StringBuilder builder = new StringBuilder("java ");
            builder
                    .append(
                            "-Djava.net.preferIPv4Stack=false -Dwebdriver.gecko.driver=drivers\\geckodriver.exe " +
                                    "-Dwebdriver.chrome.driver=")
                    .append(config.chromeDriverPath())
                    .append(" -Dwebdriver.ie.driver=drivers\\IEDriverServer.exe ");

            if (config.videoRecording()) {
                builder
                        .append("-cp \"selenium-video-node-2.8.jar;selenium-server-standalone-3.14.0.jar\" ")
                        .append(
                                "org.openqa.grid.selenium.GridLauncherV3 -servlets com.aimmac23.node.servlet" +
                                        ".VideoRecordingControlServlet -proxy com.aimmac23.hub.proxy.VideoProxy ");
            } else {
                builder.append("-jar selenium-server-standalone-3.14.0.jar ");
            }

            builder
                    .append("-role node -hub http://")
                    .append(config.hubIp())
                    .append("/grid/register -port 5555");
            Runtime.getRuntime()
                    .exec("cmd /c start cmd.exe /K \"cd " + config.driverPath() + " && " + builder + "\"");

        } catch (Exception exception) {
            log.error("Exception occurred while executing node", exception);
        }
    }
}
