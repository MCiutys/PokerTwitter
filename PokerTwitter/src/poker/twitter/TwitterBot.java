/*
 * TwitterBot.java
 * 
 */

package poker.twitter;

import poker.Constants;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterBot {

	private static TwitterBot twitterBot;
	private static Twitter api;

	// Authentication constants
	private static final String CONSUMER_KEY = "5q3jlhwnsmWwP3rctM0tIxgjk";
	private static final String CONSUMER_SECRET = "luZ51FkM6GerCGrwEAFwE7pm44PwM1vxApf1zSyh2aSGg1VkjX";
	private static final String ACCESS_TOKEN = "849543733473406978-mmNt3AumvZ0m5mbnoTcJ7X7YqvaPQRE";
	private static final String ACCESS_TOKEN_SECRET = "GMYuAHiEoFeDP9BWeoqiMbDH8zEummDD0JYKu58jCj2x1";

	private TwitterBot() {}

	public static Twitter getAPI() {
		if (twitterBot == null) {
			twitterBot = new TwitterBot();
			initialize();
		}
		return api;
	}

	// This method generates four digit random number hence the name.
	// That number is then appended to the message to avoid getting the "similar message" error.
	private static int randomFourDigitNumber() {
		String currentTime = String.valueOf(System.currentTimeMillis());
		return Integer.parseInt(currentTime.substring(currentTime.length() - 4));
	}

	private static void sleep() {
		try {
			Thread.sleep(20000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	// These two methods were created in order to limit requests so we don't get the rate limit error.
	// Also to catch TwitterException.
	public static void directMessage(long userId, String text) {
		sleep();
		try {
			TwitterBot.getAPI().sendDirectMessage(userId, text + Constants.NEW_LINE + "[" + randomFourDigitNumber() + "]");
		} catch (TwitterException e) {
			e.printStackTrace();
		}
	}

	public static void updateStatus(String status) {
		sleep();
		try {
			TwitterBot.getAPI().updateStatus(status + Constants.NEW_LINE + "[" + randomFourDigitNumber() + "]");
		} catch (TwitterException e) {
			e.printStackTrace();
		}
	}

	private static void initialize() {
		ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
		configurationBuilder.setDebugEnabled(true)
		.setOAuthConsumerKey(CONSUMER_KEY)
		.setOAuthConsumerSecret(CONSUMER_SECRET)
		.setOAuthAccessToken(ACCESS_TOKEN)
		.setOAuthAccessTokenSecret(ACCESS_TOKEN_SECRET);
		api = new TwitterFactory(configurationBuilder.build()).getInstance();
		TwitterListener.startListening();
	}

	public static void main(String[] args) {
		TwitterBot.getAPI();
	}

}