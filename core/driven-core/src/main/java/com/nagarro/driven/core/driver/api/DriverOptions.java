package com.nagarro.driven.core.driver.api;

import com.nagarro.driven.core.webdriver.Browser;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.URL;

/**
 * This class is for Driver Options
 *
 * @author nagarro
 */
public class DriverOptions {

  // TODO Implementation of Capabilities
  // TODO Find a solution to remove the selenium dependency from framework core
  // TODO Implementation of Desired Capabilities - API/Mapper
  private DesiredCapabilities capabilities;
  private String driverName;
  private URL gridUrl;
  private Browser browser;
  private String applicationName;
  private String deviceName = "";
  private String platformName;
  private String platformVersion;
  private String appPackage = "";
  private String appActivity = "";
  private boolean noResetApp = true;
  private boolean runOnEmulator = false;
  private String apkPath="";
  private String app;
  private String automationName;
  private String udid;
  private int emulatorWaitTime;
  private String applicationType;
  private String appiumIpAddress;
  private String host;
  private String port;
  private int appiumPortNumber;
  private String nodeJsPath;
  private int androidEmulatorLaunchWaitTimeInSec;
  private String appiumLibPathForIos;
  private String canvasLibrary;

  public String getAppiumLibPathForIos() {
	return appiumLibPathForIos;
}

public void setAppiumLibPathForIos(String appiumLibPathForIos) {
	this.appiumLibPathForIos = appiumLibPathForIos;
}

public int getAndroidEmulatorLaunchWaitTimeInSec() {
	return androidEmulatorLaunchWaitTimeInSec;
}

public void setAndroidEmulatorLaunchWaitTimeInSec(int androidEmulatorLaunchWaitTimeInSec) {
	this.androidEmulatorLaunchWaitTimeInSec = androidEmulatorLaunchWaitTimeInSec;
}

public String getNodeJsPath() {
	return nodeJsPath;
}

public void setNodeJsPath(String nodeJsPath) {
	this.nodeJsPath = nodeJsPath;
}

public String getAppiumIpAddress() {
	return appiumIpAddress;
}

public void setAppiumIpAddress(String appiumIpAddress) {
	this.appiumIpAddress = appiumIpAddress;
}

public int getAppiumPortNumber() {
	return appiumPortNumber;
}

public void setAppiumPortNumber(int appiumPortNumber) {
	this.appiumPortNumber = appiumPortNumber;
}

public String getApplicationType() {
	return applicationType;
}

public void setApplicationType(String applicationType) {
	this.applicationType = applicationType;
}

	public String getAutomationName() {
    return automationName;
  }

  public void setAutomationName(String automationName) {
    this.automationName = automationName;
  }

  public String getUdid() {
    return udid;
  }

  public void setUdid(String udid) {
    this.udid = udid;
  }

  public String getApp() {
    return app;
  }

  public void setApp(String app) {
    this.app = app;
  }

  public String getApkPath() {
    return apkPath;
  }

  public void setApkPath(String apkPath) {
    this.apkPath = apkPath;
  }

  public int getEmulatorWaitTime() {
    return emulatorWaitTime;
  }

  public void setEmulatorWaitTime(int emulatorWaitTime) {
    this.emulatorWaitTime = emulatorWaitTime;
  }

  public String getApplicationName() {
    return applicationName;
  }

  public void setApplicationName(String applicationName) {
    this.applicationName = applicationName;
  }

  public Browser getBrowser() {
    return browser;
  }

  public void setBrowser(Browser browser) {
    this.browser = browser;
  }

  public DesiredCapabilities getCapabilities() {
    return capabilities;
  }

  public void setCapabilities(DesiredCapabilities capabilities) {
    this.capabilities = capabilities;
  }

  public String getDriverName() {
    return driverName;
  }

  public void setDriverName(String driverName) {
    this.driverName = driverName;
  }

  public URL getGridUrl() {
    return gridUrl;
  }

  public void setGridUrl(URL gridUrl) {
    this.gridUrl = gridUrl;
  }

  public String getDeviceName() {
    return deviceName;
  }

  public void setDeviceName(String deviceName) {
    this.deviceName = deviceName;
  }

  public String getAppPackage() {
    return appPackage;
  }

  public void setAppPackage(String appPackage) {
    this.appPackage = appPackage;
  }

  public String getAppActivity() {
    return appActivity;
  }

  public void setAppActivity(String appActivity) {
    this.appActivity = appActivity;
  }

  public boolean isNoResetApp() {
    return noResetApp;
  }

  public void setNoResetApp(boolean noResetApp) {
    this.noResetApp = noResetApp;
  }

  public String getPlatformName() {
    return platformName;
  }

  public void setPlatformName(String platformName) {
    this.platformName = platformName;
  }

  public String getPlatformVersion() {
    return platformVersion;
  }

  public void setPlatformVersion(String platformVersion) {
    this.platformVersion = platformVersion;
  }

  public boolean isRunOnEmulator() {
    return runOnEmulator;
  }

  public void setRunOnEmulator(boolean runOnEmulator) {
    this.runOnEmulator = runOnEmulator;
  }

	public String getHost() {
		return host;
}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

  public String getCanvasLibrary() {
    return canvasLibrary;
  }

  public void setCanvasLibrary(String canvasLibrary) {
    this.canvasLibrary = canvasLibrary;
  }
}
