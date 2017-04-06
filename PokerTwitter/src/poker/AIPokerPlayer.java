package poker;

public class AIPokerPlayer extends PokerPlayer {

	public AIPokerPlayer(DeckOfCards deck, String mName) {
		super(deck, mName);
	}

	@Override
	public int discard() {
		int counter = 0;
		for (int i = 0; i < HandOfCards.HAND_SIZE; i++) {
			int discardProbability = hand.getDiscardProbability(i);
			// Generate random number between 1 and 99.
			int random = (int) (Math.random() * HandOfCards.PROBABILITY_MAXIMUM - 1) + 1;
			// If generated random number is in the range between 0 and
			// discardProbability, discard the card.
			if (random <= discardProbability && counter < MAX_CARDS_TO_DISCARD) {
				hand.replaceCard(i);
				counter++;
			}
		}

		return counter;
	}

	@Override
	public int bet() {
		// TODO Auto-generated method stub
		return 0;
	}

}
