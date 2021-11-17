import org.testng.Assert;
import org.testng.annotations.Test;

public class SeleniumActionTest extends SeleniumTestBase{

    private final String APP_URL=TestProperties.ACTION_WINDOW_TEST_URL;

    private final String PAGE_NAME = "AlertPage";

    @Test
    public void verifyAlertAccept(){
        String expectedResult="Customer Successfully Delete!";
        seleniumAbstractDriverProvider.get().go(APP_URL);
        seleniumAbstractDriverProvider.get().element(PAGE_NAME,"CustomerIdField").sendKeys("53920");
        seleniumAbstractDriverProvider.get().element(PAGE_NAME,"SubmitButton").click();
        seleniumAbstractDriverProvider.get().prompt().acceptAlert();
        String actualResult=seleniumAbstractDriverProvider.get().prompt().getAlertText();
        Assert.assertEquals(actualResult,expectedResult);
    }

    @Test
    public void verifyAlertDismiss(){
        String expectedResult="Do you really want to delete this Customer?";
        seleniumAbstractDriverProvider.get().go(APP_URL);
        seleniumAbstractDriverProvider.get().element(PAGE_NAME,"CustomerIdField").sendKeys("53920");
        seleniumAbstractDriverProvider.get().element(PAGE_NAME,"SubmitButton").click();
        String actualResult=seleniumAbstractDriverProvider.get().prompt().getAlertText();
        seleniumAbstractDriverProvider.get().prompt().dismissAlert();
        Assert.assertEquals(actualResult,expectedResult);
    }

    @Test
    public void verifyPageUpDownFunc(){
        seleniumAbstractDriverProvider.get().go(TestProperties.PAGEUP_DOWN_TEST_URL);
        seleniumAbstractDriverProvider.get().helpers().pageDown();
        seleniumAbstractDriverProvider.get().helpers().pageUp();
        String expectedResult="Featured Article";
        String actualResult=seleniumAbstractDriverProvider.get().element(PAGE_NAME,"Article").getText();
        Assert.assertEquals(actualResult,expectedResult);
    }

    @Test
    public void verifySwitchFrame(){
        String expectedResult="Feel free to contact us for more details";
        seleniumAbstractDriverProvider.get().go(TestProperties.SWITCH_FRAME_URL);
        seleniumAbstractDriverProvider.get().helpers().switchToFrame("contact-iframe");
        String PAGE_NAME2 = "SwitchFrame";
        String actualResult=seleniumAbstractDriverProvider.get().element(PAGE_NAME2,"Frame").getText();
        Assert.assertEquals(actualResult,expectedResult);

    }

    @Test
    public void verifySwitchTab(){
        String expectedResult="This access is valid only for 20 days.";
        seleniumAbstractDriverProvider.get().go(TestProperties.SWITCH_TAB_URL);
        String PAGE_NAME3 = "SwitchTab";
        seleniumAbstractDriverProvider.get().element(PAGE_NAME3,"ClickHere").click();
        seleniumAbstractDriverProvider.get().helpers().switchToTab(1);
        seleniumAbstractDriverProvider.get().element(PAGE_NAME3,"EmailId").sendKeys("akash@gmail.com");
        seleniumAbstractDriverProvider.get().element(PAGE_NAME3,"SubmitButton").click();
        String actualResult=seleniumAbstractDriverProvider.get().element(PAGE_NAME3,"TextBox").getText();
        seleniumAbstractDriverProvider.get().helpers().switchToParentTab();
        Assert.assertEquals(actualResult,expectedResult);

    }
  
}
