package poker;

import java.util.ArrayList;

public class RoundOfPoker {

	private static final int STARTING_BET = 1;

	private int pot;
	private ArrayList<PokerPlayer> players;
	private DeckOfCards deck;
	private ArrayList<PokerPlayer> originalPlayers; // just in case round has to
	// be restarted

	public RoundOfPoker(ArrayList<PokerPlayer> players) {
		createNewRound(players);
	}

	private void createNewRound(ArrayList<PokerPlayer> players) {
		pot = 0;
		this.players = players;
		copyOfPlayers(players);
		deck = new DeckOfCards();
	}

	private void copyOfPlayers(ArrayList<PokerPlayer> players) {
		for (int i = 0; i < players.size(); i++) {
			originalPlayers.add(players.get(i));
		}
	}

	// Finding out who has won the round
	private void findWinner() {
		ArrayList<Integer> winners = new ArrayList<Integer>(); // indexes of
		// players who
		// won
		winners.add(0);
		for (int i = 1; i < players.size(); i++) {
			int currentWinnersGameValue = players.get(winners.get(0)).getHand().getGameValue();
			int playersGameValue = players.get(i).getHand().getGameValue();

			// Comparing game values to find out who has won
			// If players game value larger than current leading player's one,
			// replace it
			if (currentWinnersGameValue < playersGameValue) {
				winners.clear();
				winners.add(i);
				// if equal, add it to the winners list
			} else if (currentWinnersGameValue == playersGameValue) {
				winners.add(i);
			}
		}
		splitPot(winners);
	}

	// Split the pot
	private void splitPot(ArrayList<Integer> winners) {
		for (int i = 0; i < winners.size(); i++) {
			int winningPot = pot / winners.size();
			players.get(i).addToFunds(winningPot);
		}
	}

	// Displaying cards of every player
	private void displayCards() {
		for (int i = 0; i < players.size(); i++) {
			System.out.println(players.get(i).getName() + " had the following hand: ");
			System.out.println(players.get(i).getHand());
		}
	}

	// Showing amount of chips each player has
	private void showChips() {
		for (int i = 0; i < players.size(); i++) {
			System.out.println(players.get(i).getFunds());
		}
	}

	// Dealing cards for players
	private void dealCards() {
		for (int i = 0; i < players.size() * HandOfCards.HAND_SIZE; i++) {
			players.get(i % players.size()).addCard(deck.dealNext());
		}
	}

	// Looking for someone who can open
	private int openingPlayer() {
		int openingPlayer = -1;
		for (int i = players.size() - 1; i >= 0; i--) {
			if (players.get(i).canStartBetting()) {
				openingPlayer = i;
				System.out.println(players.get(i).getName() + " can open");
			} else {
				System.out.println(players.get(i).getName() + " cannot open");
			}
		}
		return openingPlayer;
	}

	// Discarding cards
	private void discardCards() {
		String discarded = "Discarded cards: ";
		for (int i = 0; i < players.size(); i++) {
			discarded += players.get(i).discard();
			System.out.println(discarded);
		}
	}

	// Betting, folding, raising
	private void betting(int startingPlayer) {
		int bet = STARTING_BET;
		int lastBet = bet;
		int counter = players.size(); // when 0, betting is finished
		for (int i = startingPlayer; counter != 0; i++) {
			// Get the bet from a player
			bet = players.get(i % players.size()).bet(bet);

			// if bet is -1, player folded
			if (bet == PokerPlayer.BET_FOLD) {
				System.out.println(players.get(i).getName() + " has folded");
				players.remove(i % players.size());
				counter--;
				// otherwise, add bet to the pot
			} else {
				pot += bet;
				counter--;
				// if new bet higher than last one, player has raised
				// have to go full circle again
				if (bet > lastBet) {
					counter = players.size();
					lastBet = bet;
				}
			}
		}
	}

	// Play the round
	public void playRound() {
		showChips();
		dealCards();
		int openingPlayer = openingPlayer();
		discardCards();

		// If at least one player can open, start the round
		if (openingPlayer != -1) {
			betting(openingPlayer);
			findWinner();
			displayCards();
			// no one can open, so start new round
		} else {
			createNewRound(originalPlayers);
		}
	}
}
