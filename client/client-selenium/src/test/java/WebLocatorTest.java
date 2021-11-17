import static org.assertj.core.api.Assertions.assertThat;

import com.nagarro.driven.client.selenium.WebLocatorLoader;
import java.util.function.BiFunction;
import org.testng.annotations.Test;

public class WebLocatorTest extends SeleniumTestBase {

  private final String APP_URL = TestProperties.TEST_URL;
  private final String PAGE_NAME = "GooglePage";

  @Test
  public void verifyFindWebLocatorForElement() {
    seleniumAbstractDriverProvider.get().go(APP_URL);
    BiFunction<String, String, String> locator = String::concat;
    String expectedResult =
        locator.apply("By.xpath: ", "//*[@id=\"tsf\"]/div[2]/div[1]/div[1]/div/div[2]/input");
    String actualResult =
        WebLocatorLoader.findWebLocatorForElement(
                PAGE_NAME,
                "SearchField",
                seleniumAbstractDriverProvider.get().getWebDriver(),
                reportLogger)
            .toString();
    assertThat(actualResult).isEqualTo(expectedResult);
  }

  @Test
  public void verifyFindWebLocatorForElementDynamicLocator() {
    seleniumAbstractDriverProvider.get().go(APP_URL);
    BiFunction<String, String, String> locator = String::concat;
    String expectedResult =
        locator.apply("By.xpath: ", "//*[@id=\"tsf\"]/div[2]/div[1]/div[1]/div/div[2]/input");
    String actualResult =
        WebLocatorLoader.findWebLocatorForElement(
                PAGE_NAME,
                "SearchField",
                seleniumAbstractDriverProvider.get().getWebDriver(),
                reportLogger,
                "//input[@aria-label='Search']")
            .toString();
    assertThat(actualResult).isEqualTo(expectedResult);
  }
}
