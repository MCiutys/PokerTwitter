package poker;

import java.util.ArrayList;
import java.util.HashMap;

public class RoundOfPoker {

	private static final int STARTING_BET = 1;

	private int pot;
	private ArrayList<PokerPlayer> players;
	private DeckOfCards deck;
	private ArrayList<PokerPlayer> originalPlayers; // just in case round has to
	// be restarted
	private HashMap<PokerPlayer, Integer> lastBets; // storing last bets of
	// every player

	public RoundOfPoker(ArrayList<PokerPlayer> players) {
		createNewRound(players);
	}

	private void createNewRound(ArrayList<PokerPlayer> players) {
		pot = 0;
		this.players = players;
		// copyOfPlayers(players);
		originalPlayers = players;
		deck = new DeckOfCards();
		lastBets = new HashMap<PokerPlayer, Integer>();
	}

	private void createHashmap(ArrayList<PokerPlayer> players) {
		for (int i = 0; i < players.size(); i++) {
			lastBets.put(players.get(i), 0);
		}
	}

	private void copyOfPlayers(ArrayList<PokerPlayer> players) {
		for (int i = 0; i < players.size(); i++) {
			originalPlayers.add(players.get(i));
		}
	}

	// Finding out who has won the round
	private void findWinner() {
		System.out.println("------------------");
		ArrayList<PokerPlayer> winners = new ArrayList<PokerPlayer>();

		winners.add(players.get(0));
		int currentWinnersGameValue = winners.get(0).getHand().getGameValue();
		for (int i = 1; i < players.size(); i++) {
			PokerPlayer player = players.get(i);
			int playersGameValue = player.getHand().getGameValue();

			// Comparing game values to find out who has won
			// If players game value larger than current leading player's one,
			// replace it
			if (currentWinnersGameValue < playersGameValue) {
				winners.clear();
				winners.add(player);
				currentWinnersGameValue = playersGameValue;
				// if equal, add it to the winners list
			} else if (currentWinnersGameValue == playersGameValue) {
				winners.add(player);
			}
		}

		// For testing
		System.out.println("---------------------");
		System.out.println("Winners: ");
		for (int i = 0; i < winners.size(); i++) {
			System.out.println(winners.get(i).getName() + " ");
		}
		splitPot(winners);
	}

	// Split the pot
	private void splitPot(ArrayList<PokerPlayer> winners) {
		System.out.println("------------------");
		for (int i = 0; i < winners.size(); i++) {
			int winningPot = pot / winners.size();
			winners.get(i).addToFunds(winningPot);
			System.out.println(winners.get(i).getName() + " wins " + winningPot);
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
		System.out.println("------------------");
		for (int i = 0; i < players.size(); i++) {
			System.out.println(players.get(i).getName() + " has " + players.get(i).getFunds());
		}
	}

	// Dealing cards for players
	private void dealCards() {
		System.out.println("------------------");
		for (int i = 0; i < players.size() * HandOfCards.HAND_SIZE; i++) {
			players.get(i % players.size()).addCard();
		}

		// For testing
		for (int i = 0; i < players.size(); i++) {
			System.out.println(players.get(i).getName() + " has: ");
			System.out.println(players.get(i).getHand());
		}
	}

	// Looking for someone who can open
	private int openingPlayer() {
		System.out.println("------------------");
		int openingPlayer = -1;
		int min = players.size();

		for (int i = 0; i < players.size(); i++) {
			if (players.get(i).canStartBetting()) {
				if (i <= min) {
					min = i;
					openingPlayer = i;
				}
				System.out.println(players.get(i).getName() + " can open");
			} else {
				System.out.println(players.get(i).getName() + " cannot open");
			}
		}

		return openingPlayer;
	}

	// Discarding cards
	private void discardCards() {
		System.out.println("------------------");
		for (int i = 0; i < players.size(); i++) {
			System.out.println(players.get(i).getName() + " discarded " + players.get(i).discard());
		}
	}

	// Folding process
	private int folding(int index, int size) {
		int indexToReturn = 0;
		players.remove(index % size);
		if (index % size == size) {
			indexToReturn = -1;
		} else {
			indexToReturn = (index % size) - 1;
		}
		return indexToReturn;
	}

	// Betting, folding, raising
	private void betting(int startingPlayer) {
		int bet = STARTING_BET;
		int lastBet = bet;
		int counter = players.size(); // when 0, betting is finished
		for (int i = startingPlayer; counter != 0; i++) {
			int size = players.size();
			PokerPlayer player = players.get(i % size);
			System.out.println("---------------------------------");
			System.out.println("Last bet by any player was " + lastBet);
			System.out.println("Last bet for " + player.getName() + " was " + lastBets.get(player));
			// Get the bet from a player
			System.out.println(player.getName() + " has to add " + (lastBet - lastBets.get(player)) + " to the pot");
			bet = player.bet((lastBet - lastBets.get(player)));

			// if bet is -1, player folded
			if (bet == PokerPlayer.BET_FOLD) {
				System.out.println(player.getName() + " has folded");
				i = folding(i, size);
				counter--;
				// otherwise, add bet to the pot
			} else {
				pot += bet;
				System.out.println(player.getName() + " has bet " + bet);
				counter--;

				// if new bet higher than last one, player has raised
				if (bet > (lastBet - lastBets.get(player))) {
					System.out.println(player.getName() + " has raised by " + (bet - (lastBet - lastBets.get(player))));
					counter = size - 1;
				}
				bet += lastBets.get(player);
				lastBet = bet;
				lastBets.put(player, bet);
				System.out.println("Next has to bet in total: " + bet);
			}
		}
	}

	// Play the round
	public void playRound() {
		createHashmap(players);
		showChips();
		dealCards();
		int openingPlayer = openingPlayer();
		discardCards();

		// If at least one player can open, start the round
		if (openingPlayer != -1) {
			betting(openingPlayer);
			findWinner();
			displayCards();
			showChips();
			// no one can open, so start new round
		} else {
			playRound();
		}
	}
}
