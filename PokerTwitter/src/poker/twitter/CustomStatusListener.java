/*
 * CustomStatusListener.java
 * 
 */

package poker.twitter;

import poker.PokerManager;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;

public class CustomStatusListener implements StatusListener{

	@Override
	public void onException(Exception arg0) {

	}

	@Override
	public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {

	}

	@Override
	public void onScrubGeo(long userId, long upToStatusId) {

	}

	@Override
	public void onStallWarning(StallWarning warning) {

	}

	@Override
	public void onStatus(Status status) {
		PokerManager.getInstance().processInput(status.getUser(), status.getText());
	}

	@Override
	public void onTrackLimitationNotice(int numberOfLimitedStatuses) {

	}

}
