/*
 * TwitterListener.java
 * 
 */

package poker;

import twitter4j.FilterQuery;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;

public class TwitterListener {

	private static TwitterListener twitterListener;
	private static TwitterStream twitterStream;

	private TwitterListener() {}

	private static void initialize() {
		TwitterStreamFactory twitterStreamFactory = new TwitterStreamFactory(TwitterBot.getAPI().getConfiguration());
		twitterStream = twitterStreamFactory.getInstance();
		twitterStream.addListener(new CustomStatusListener());
		twitterStream.filter(new FilterQuery(TwitterBot.HASH_TAG));
	}

	public static boolean startListening() {
		if (twitterListener == null) {
			twitterListener = new TwitterListener();
			initialize();
			return true;
		}
		return false;
	}

	public static boolean stopListening() {
		if (twitterStream != null) {
			twitterStream.shutdown();
			twitterStream = null;
			return true;
		}
		return false;
	}

}