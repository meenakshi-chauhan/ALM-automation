import com.nagarro.driven.core.webdriver.Browser;
import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;


@Slf4j
public class BrowserOptionsTest extends SeleniumTestBase {

    @DataProvider(name = "browser-provider")
    public Object[][] browserProvider(){
        return new Object[][] {{Browser.INTERNET_EXPLORER},{Browser.FIREFOX},{Browser.CHROME},{Browser.SAFARI}};
    }

    @Test(dataProvider = "browser-provider")
    public void verifyBrowserOptions(Browser browser){
        BROWSER=browser;
        log.info("The Url is opening in: {}",browser.name());
        seleniumAbstractDriverProvider.get().go(TestProperties.BROWSER_OPTION_TEST_URL);
        String expectedResult="Guru99 Bank Home Page";
        String actualResult=seleniumAbstractDriverProvider.get().getWebDriver().getTitle();
        Assert.assertEquals(actualResult,expectedResult);
    }

}
