/*
 * TwitterListener.java
 * 
 */

package poker;

import java.util.List;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.TwitterException;

public class TwitterListener implements Runnable {

	private static TwitterListener twitterLister;
	private static long lastProcessedTweetId;

	private TwitterListener() {}

	@Override
	public void run() {

		// FIXME Switch to streaming instead of querying. (Rate limit gets exceeded too fast)
		while (true) {
			Query query = new Query(TwitterBot.HASH_TAG);
			query.count(1);
			if (lastProcessedTweetId > 0) {
				query.setSinceId(lastProcessedTweetId);
			}
			QueryResult queryResult = null;
			
			// Store last processed tweet, next time query for tweets
			// that have been tweeted after that particular tweet was posted.

			try {
				queryResult = TwitterBot.getAPI().search(query);
			} catch (TwitterException e) {
				e.printStackTrace();
			} finally {
				List<Status> tweets = queryResult.getTweets();
				if (!tweets.isEmpty()) {
					Status tweet = tweets.get(0);
					lastProcessedTweetId = tweet.getId();
					System.out.println(tweet.getText());
				}
			}

			// Sleep for 10ms to reduce the CPU usage,
			// otherwise CPU usage will be at 100%.
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
	}

	public static TwitterListener getInstance() {
		if (twitterLister == null) {
			twitterLister = new TwitterListener();
		}
		return twitterLister;
	}

}