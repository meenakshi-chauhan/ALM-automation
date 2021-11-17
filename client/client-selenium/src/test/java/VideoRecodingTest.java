import com.nagarro.driven.client.selenium.util.VideoRecordingUtil;
import com.nagarro.driven.core.util.ReportUtil;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;


public class VideoRecodingTest extends SeleniumTestBase{

    @Test
    public void verifyVideoRecording(){
        VideoRecordingUtil.startRecorder(false);
        seleniumAbstractDriverProvider.get().go(TestProperties.TEST_URL);
        String PAGE_NAME = "VideoRecordingPage";
        seleniumAbstractDriverProvider.get().element(PAGE_NAME,"SearchField").sendKeys("BreizhCamp 2018");
        seleniumAbstractDriverProvider.get().element(PAGE_NAME,"SearchButton").click();
        VideoRecordingUtil.stopRecorder(false);
        String videoPath=TestProperties.VIDEO_DIR + ReportUtil.getCurrentDateTimeAsString();

        File f = new File(videoPath);
        Assert.assertTrue(f.exists());
    }
}
