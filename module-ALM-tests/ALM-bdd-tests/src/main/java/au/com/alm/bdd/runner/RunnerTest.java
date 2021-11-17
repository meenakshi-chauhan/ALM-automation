package au.com.alm.bdd.runner;

import au.com.alm.bdd.testbase.ALMBDDTestBase;
import io.cucumber.testng.CucumberOptions;
@CucumberOptions(
        features = {"classpath:featurefile"},
        glue = {
                "classpath:au.com.alm.bdd.stepdefinitions",
                "au.com.alm.bdd.testbase"
        },
        plugin = {"pretty", "json:target/espoCRM.json", "html:target/cucumber-reporting"},
        monochrome = true,
        tags = "@Scenario")
public class RunnerTest extends ALMBDDTestBase {
}
