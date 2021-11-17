package com.nagarro.driven.client.selenium.util;

import com.nagarro.driven.client.selenium.config.SeleniumConfig;
import com.nagarro.driven.core.config.CoreConfig;
import com.nagarro.driven.core.constant.FrameworkCoreConstant;
import com.nagarro.driven.core.util.ReportUtil;
import com.nagarro.driven.core.util.Sleeper;
import org.apache.commons.io.FileUtils;
import org.monte.media.Format;
import org.monte.media.FormatKeys.MediaType;
import org.monte.media.Registry;
import org.monte.media.math.Rational;
import org.monte.screenrecorder.ScreenRecorder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static org.monte.media.AudioFormatKeys.EncodingKey;
import static org.monte.media.AudioFormatKeys.FrameRateKey;
import static org.monte.media.AudioFormatKeys.KeyFrameIntervalKey;
import static org.monte.media.AudioFormatKeys.MIME_AVI;
import static org.monte.media.AudioFormatKeys.MediaTypeKey;
import static org.monte.media.AudioFormatKeys.MimeTypeKey;
import static org.monte.media.VideoFormatKeys.*;

public class VideoRecordingUtil extends ScreenRecorder {
    private static final Logger log = LoggerFactory.getLogger(VideoRecordingUtil.class);
    private static final SeleniumConfig.SeleniumConfigSpec config = SeleniumConfig.getInstance();
    static Path videoDir = CoreConfig.getInstance().reportPath()
            .resolve(FrameworkCoreConstant.VIDEO_FOLDER_NAME);
    private static ScreenRecorder screenRecorder;
    private String name;

    public VideoRecordingUtil(GraphicsConfiguration cfg, Rectangle captureArea, Format fileFormat, Format screenFormat,
                              Format mouseFormat, File movieFolder, String name) throws IOException, AWTException {
        super(cfg, captureArea, fileFormat, screenFormat, mouseFormat, null, movieFolder);
        this.name = name;
    }

    public static void startRecorder(boolean initializeGrid) {
        try {
            if (config.videoRecording() && !initializeGrid) {
                ReportUtil.makeDirectory(videoDir);
                Long currentSize = getFolderSizeInBytes(videoDir.toFile());
                log.info("Folder size of video recording in bytes is: {}", currentSize);
                double billion = 1e+9;
                double maxSizeInGB = billion * config.maxVideoFolderSizeGB();

                if (currentSize >= maxSizeInGB) {
                    log.info(
                            "The video folder size is more than the allowed {} GB. Hence some video files will be " +
                                    "deleted.",
                            maxSizeInGB);
                    FileUtils.cleanDirectory(videoDir.toFile());
                }
                File file = new File(videoDir.toString());
                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                int width = screenSize.width;
                int height = screenSize.height;
                Rectangle captureSize = new Rectangle(0, 0, width, height);
                GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
                        .getDefaultConfiguration();
                String methodName = FrameworkCoreConstant.VIDEO_NAME_PREFIX + ReportUtil.getCurrentDateTimeAsString();
                screenRecorder = new VideoRecordingUtil(gc, captureSize,
                        new Format(MediaTypeKey, MediaType.FILE, MimeTypeKey, MIME_AVI),
                        new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
                                CompressorNameKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE, DepthKey, 8, FrameRateKey,
                                Rational.valueOf(15), QualityKey, 1.0f, KeyFrameIntervalKey, 15 * 60),
                        new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey, "black", FrameRateKey,
                                Rational.valueOf(15))

                        , file, methodName);

                screenRecorder.start();
            } else if (initializeGrid) {
                Parallelization.startHub();
                Parallelization.startNode();
                Sleeper.silentSleep(FrameworkCoreConstant.INT_5000);
            }
        } catch (IOException | AWTException e) {
            log.error("Exception occured while starting the video recording : {}", e.getMessage());
        }
    }

    public static void stopRecorder(boolean initializeGrid) {
        if (config.videoRecording() && !initializeGrid) {
            try {
                screenRecorder.stop();
            } catch (RuntimeException | IOException e) {
                log.error("Exception occured while starting the video recording : {}", e.getMessage());
            }
        }

    }

    /*
     * Gets folder size in bytes.
     */
    private static long getFolderSizeInBytes(File directory) {
        return FileUtils.sizeOfDirectory(directory);
    }

    @Override
    protected File createMovieFile(Format fileFormat) throws IOException {
        if (!movieFolder.exists()) {
            movieFolder.mkdirs();
        } else if (!movieFolder.isDirectory()) {
            throw new IOException("\"" + movieFolder + "\" is not a directory.");
        }
        return new File(movieFolder, name + "." + Registry.getInstance().getExtension(fileFormat));
    }
}
