/*
 * TwitterListener.java
 * 
 */

package poker;

public class TwitterListener implements Runnable {

	private static TwitterListener twitterLister;
//	private static int lastProcessedTweetId;

	private TwitterListener() {}

	@Override
	public void run() {

		while (true) {
			// Listen...

			//Query query = new Query(TwitterBot.HASH_TAG);

			// Store last processed tweet, next time query for tweets
			// that have been tweeted after that particular tweet was posted.

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