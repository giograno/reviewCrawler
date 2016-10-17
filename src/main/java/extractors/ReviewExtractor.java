package extractors;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import config.ConfigurationManager;
import crawler.Crawler;
import crawler.CrawlerFactory;

public class ReviewExtractor extends Extractor {

	private ConfigurationManager configurationManager;

	public ReviewExtractor(ArrayList<String> appsToMine, ConfigurationManager configurationManager) {
		super(appsToMine);
		this.configurationManager = configurationManager;
	}

	@Override
	public void extract() {

		ExecutorService executor = Executors.newFixedThreadPool(this.configurationManager.getNumberOfThreadToUse());

		try {
			ConfigurationManager.getInstance().updateDateOfLastCrawl();
		} catch (Exception e) {
			e.printStackTrace();
		}	
		
		for (String currentApp : appsToMine) {
			Crawler googlePlayStoreCrawler = CrawlerFactory.getCrawler(this.configurationManager, currentApp, "google");
			executor.execute(googlePlayStoreCrawler);
		}
		
		executor.shutdown();
		
	}

}
