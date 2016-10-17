package crawler.info;

import java.io.IOException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import beans.AppInfo;
import config.ConfigurationManager;
import crawler.Crawler;
import utils.WebElements;

public class PlayStoreCrawlerInfo extends Crawler {

	private ArrayList<String> appList;
	private String browserChoice;
	private String driverPath;
	private ConfigurationManager config;
	private WebDriver driver;

	public PlayStoreCrawlerInfo(ConfigurationManager config) {
		this.config = config;
		this.browserChoice = this.config.getBrowserChoice();
		this.driverPath = this.config.getWebDriver().getAbsolutePath();

		if (browserChoice.equalsIgnoreCase("Chrome")) {

			System.setProperty("webdriver.chrome.driver", driverPath);
			driver = new ChromeDriver();

		} else if (browserChoice.equalsIgnoreCase("Firefox")) {
			driver = new FirefoxDriver();
		}
	}

	public void setAppList(ArrayList<String> appList) {
		this.appList = appList;
	}

	@Override
	public void run() {

		for (String appName : appList) {

			String appLink = WebElements.PLAY_STORE_BASE_LINK + appName + WebElements.REVIEWS_LANGUAGE;
			connectTo(appLink);
			AppInfo info = this.getInfo();
			if (info != null) {
				try {
					this.writeLine(info);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		}
	}

	public AppInfo getAppInformation(String appLink) throws Exception {
		connectTo(appLink);
		AppInfo info = this.getInfo();
		return info;
	}

	public void closeDriver() {
		driver.quit();
	}

	private void connectTo(String appLink) {
		if (appLink.startsWith(WebElements.PLAY_STORE_BASE_LINK)) {
			boolean found = false;
			Pattern pattern = Pattern.compile("hl=[a-zA-Z]{2}");
			Matcher matcher = pattern.matcher(appLink);
			while (matcher.find()) {
				found = true;
				String oldLang = matcher.group();
				appLink = appLink.replace(oldLang, "hl=en");
			}
			if (!found) {
				appLink = appLink + "&hl=en";
			}
			if (driver == null) {

			}

			driver.manage().window().setPosition(new Point(-2000, 0));
			driver.navigate().to(appLink);

		}

	}

	private AppInfo getInfo() {
		Boolean errorPresent = driver.findElements(By.id("error-section")).size() > 0;
		AppInfo appInfo = null;
		if (!errorPresent) {
			try {
				appInfo = new AppInfo();
				String version = driver.findElement(By.xpath(WebElements.CURRENT_VERSION)).getText();
				String upDate = driver.findElement(By.xpath(WebElements.LAST_UPDATE)).getText();
				appInfo.setCurrentVersion(version);
				appInfo.setLastUpdate(upDate);
			} catch (NoSuchElementException e) {
				return null;
			}
		}
		return appInfo;

	}
}
