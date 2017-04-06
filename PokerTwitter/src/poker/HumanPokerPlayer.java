package poker;

public class HumanPokerPlayer extends PokerPlayer {
	private String twitterHandle;
	private TwitterBot twitterBot;

	public HumanPokerPlayer(DeckOfCards deck, String mName, String mTwitterHandle) {
		super(deck, mName);
		twitterHandle = mTwitterHandle;
	}

	@Override
	public int discard() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int bet() {
		// TODO Auto-generated method stub
		return 0;
	}

}
