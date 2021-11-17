import org.testng.Assert;
import org.testng.annotations.Test;

public class SeleniumWindowTest extends SeleniumTestBase{

    private final String APP_URL=TestProperties.ACTION_WINDOW_TEST_URL;

    @Test
    public void verifyTitleOfWindow(){
        seleniumAbstractDriverProvider.get().go(APP_URL);
        String expectedResult="Delete Customer";
        String actualResult=seleniumAbstractDriverProvider.get().getWebDriver().getTitle();
        Assert.assertEquals(actualResult,expectedResult);
    }

    @Test
    public void verifyTextOfElementOnWebPage(){
        String expectedResult="Click Here";
        seleniumAbstractDriverProvider.get().go(TestProperties.SCREENSHOT_TEST_URL);
        String PAGE_NAME = "TextElementOfWebPage";
        String actualResult=seleniumAbstractDriverProvider.get().element(PAGE_NAME,"ClickHere").getText();
        Assert.assertEquals(actualResult,expectedResult);
    }

    @Test
    public void verifyTextOnWebpage(){
        seleniumAbstractDriverProvider.get().go(APP_URL);
        Assert.assertTrue(seleniumAbstractDriverProvider.get().helpers().verifyAnyTextOnPage("Guru99 Bank"));
    }

    @Test
    public void verifyUrlOfWebsiteIsValid(){
        Assert.assertTrue(seleniumAbstractDriverProvider.get().helpers().verifyURLIsValid(APP_URL));
    }

    @Test
    public void verifyContentForPDFWindow(){
        String expectedResult="This is a small demonstration .pdf file";
        seleniumAbstractDriverProvider.get().go(TestProperties.TEST_URL);
        String PAGE_NAME2 = "ContentOfPDFPage";
        seleniumAbstractDriverProvider.get().element(PAGE_NAME2,"SearchField").sendKeys("pdf file url");
        seleniumAbstractDriverProvider.get().element(PAGE_NAME2,"SearchButton").click();
        seleniumAbstractDriverProvider.get().element(PAGE_NAME2,"ContentOfPDF").click();
        String actualResult=seleniumAbstractDriverProvider.get().helpers().getPDFContent(1,2);
        Assert.assertTrue(actualResult.contains(expectedResult));
    }
}
