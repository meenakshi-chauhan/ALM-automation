package com.nagarro.driven.client.selenium.config;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.Config.LoadPolicy;
import org.aeonbits.owner.Config.LoadType;
import org.aeonbits.owner.Config.Sources;
import org.aeonbits.owner.ConfigFactory;

/**
 * This class is used to create the instance of the selenium config and contains interface of
 * SeleniumConfigSpec.
 *
 * @author nagarro
 */
public class SeleniumConfig {

    /* The instance of SeleniumConfigSpec */
    private static final SeleniumConfigSpec INSTANCE =
            ConfigFactory.create(SeleniumConfigSpec.class, System.getProperties(), System.getenv());

    /**
     * Gets the instance of SeleniumConfigSpec.
     *
     * @return the instance of SeleniumConfigSpec.
     */
    public static SeleniumConfigSpec getInstance() {
        return INSTANCE;
    }

    @LoadPolicy(LoadType.MERGE)
    @Sources({"classpath:com/nagarro/driven/client/selenium/config/SeleniumConfig.properties"})
    public interface SeleniumConfigSpec extends Config {

        @Key("selenium.report.video.recording")
        @DefaultValue("false")
        boolean videoRecording();

        @Key("selenium.report.video.folder.size.gb")
        @DefaultValue("2")
        int maxVideoFolderSizeGB();

        @Key("selenium.util.video.testVideoPath")
        @DefaultValue("C:/driven/Videos/TestVideo")
        String testVideoPath();

        @Key("selenium.util.video.driverPath")
        @DefaultValue("E:/GAF/root/resources")
        String driverPath();

        @Key("selenium.util.video.chromeDriverPath")
        @DefaultValue("C:/driven/chromedriver.exe")
        String chromeDriverPath();

        @Key("selenium.util.video.hubIp")
        @DefaultValue("10.175.29.97:4444")
        String hubIp();

        @Key("selenium.implicit.wait.timeout.seconds")
        @DefaultValue("5")
        int seleniumImplicitWaitTimeoutSeconds();
    }
}
