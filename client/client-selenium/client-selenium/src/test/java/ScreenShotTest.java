import org.testng.Assert;
import org.testng.annotations.Test;
import java.io.File;


public class ScreenShotTest extends SeleniumTestBase {

    @Test
    public void verifyScreenshotFunction() {
        String APP_URL = TestProperties.SCREENSHOT_TEST_URL;
        seleniumAbstractDriverProvider.get().go(APP_URL);
        String PAGE_NAME = "TextElementOfWebPage";
        File screenshot=seleniumAbstractDriverProvider.get().element(PAGE_NAME,"ClickHere").takeScreenshot();
       Assert.assertTrue(screenshot.exists());
    }
}
