package extractors;

import java.util.ArrayList;
import java.util.List;

import beans.Review;
import config.ConfigurationManager;
import io.CSVWriter;
import io.MongoDBHandler;

public class DatabaseExtractor extends Extractor {
	
	private MongoDBHandler mongo = new MongoDBHandler();
	private CSVWriter csvWriter = new CSVWriter();

	public DatabaseExtractor(ArrayList<String> inputApps, ConfigurationManager config) {
		super(inputApps);
	}

	@Override
	public void extract() {
		
		for (String app : appsToMine) {
			List<Review> reviews = mongo.getReviewsFromDB(app);
			this.writeReviewForAnApp(reviews);
			System.out.println("Extracted " + reviews.size() + " reviews for " + app);
		}
	}

	private void writeReviewForAnApp(List<Review> reviews) {
		for (Review review : reviews) {
			this.csvWriter.writeline(review);
		}
	}

	@Override
	public void printNumberOfInputApps() {
		System.out.println(super.appsToMine.size() + " apps to extract");
	}	
}