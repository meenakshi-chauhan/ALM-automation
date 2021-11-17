package au.com.alm.bdd.config;

import com.google.inject.Stage;
import com.nagarro.driven.core.webdriver.Browser;
import com.nagarro.driven.core.webdriver.Driver;

import java.net.URL;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.Config.LoadPolicy;
import org.aeonbits.owner.Config.LoadType;
import org.aeonbits.owner.Config.Sources;

@LoadPolicy(LoadType.MERGE)
@Sources({"classpath:au/com/alm/bdd/config/ALMBDDConfig.properties"})
public interface ALMBDDConfig extends Config {

    @Key("application.url")
    @DefaultValue("")
    String applicationURL();

    @Key("initialize.selenium.grid")
    @DefaultValue("false")
    boolean initializeSeleniumGrid();

    @Key("browser")
    @DefaultValue("CHROME")
    Browser initiateBrowser();

    @Key("driver.name")
    @DefaultValue("SELENIUM_WEBDRIVER")
    Driver driverName();

    @Key("gridUrl")
    @DefaultValue("http://localhost:4444/wd/hub")
    URL gridUrl();

    @Key("application.name")
    @DefaultValue("")
    String applicationName();

    @Key("cucumber.report.path")
    @DefaultValue("C:/driven")
    String cucumberReportPath();

    @Key("dependency.injection.stage")
    @DefaultValue("DEVELOPMENT")
    Stage dependencyInjectionStage();
}
