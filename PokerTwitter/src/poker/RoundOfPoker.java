package poker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import poker.player.HumanPokerPlayer;
import poker.player.PokerPlayer;
import poker.twitter.TwitterBot;
import twitter4j.TwitterException;

public class RoundOfPoker {

	private static final int STARTING_BET = 1;

	private int pot;
	private ArrayList<PokerPlayer> players;
	private DeckOfCards deck;
	private HashMap<PokerPlayer, Integer> lastBets; // storing last bets of every player
	private HashMap<PokerPlayer, Integer> splitPots;

	public RoundOfPoker(ArrayList<PokerPlayer> players, DeckOfCards deck) {
		createNewRound(players, deck);
	}

	private void createNewRound(ArrayList<PokerPlayer> players, DeckOfCards deck) {
		pot = 0;
		this.players = players;
		lastBets = new HashMap<PokerPlayer, Integer>();
		splitPots = new HashMap<PokerPlayer, Integer>();
		this.deck = deck;
	}

	private void createHashmapForBets(ArrayList<PokerPlayer> players) {
		for (int i = 0; i < players.size(); i++) {
			lastBets.put(players.get(i), 0);
		}
	}

	private void createHashmapForPot(ArrayList<PokerPlayer> players) {
		for (int i = 0; i < players.size(); i++) {
			splitPots.put(players.get(i), 0);
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
		boolean takeFromOthers = false;
		int amountToTake = 0;
		for (int i = 0; i < winners.size(); i++) {
			PokerPlayer winner = winners.get(i);
			int requiredPart = pot / winners.size();
			System.out.println("Required part: " + requiredPart);
			System.out.println("Winners part: " + splitPots.get(winner));
			System.out.println("Winners size: " + winners.size());
			if (splitPots.get(winner) >= requiredPart || winners.size() == 1) {
				winner.addToFunds(pot);
				System.out.println("Equal parts");
			} else {
				winner.addToFunds(splitPots.get(winner) * winners.size());
				pot -= splitPots.get(winner) * winners.size();
				takeFromOthers = true;
				amountToTake = splitPots.get(winner);
				System.out.println("Unequal parts");
			}
		}
		if (takeFromOthers) {
			for (int i = 0; i < winners.size(); i++) {
				PokerPlayer winner = winners.get(i);
				winner.addToFunds(splitPots.get(winner) - amountToTake);
			}
			System.exit(0);
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
		String chipsUpdate = "";
		for (int i = 0; i < players.size(); i++) {
			chipsUpdate += players.get(i).getName() + " has " + players.get(i).getFunds() + " chips.\n";
		}
		tweetAllHumanPlayers(chipsUpdate);
	}

	private List<HumanPokerPlayer> getHumanPlayers() {
		List<HumanPokerPlayer> humanPokerPlayers = new ArrayList<HumanPokerPlayer>();
		for (PokerPlayer pokerPlayer : players) {
			if (pokerPlayer instanceof HumanPokerPlayer) {
				humanPokerPlayers.add((HumanPokerPlayer) pokerPlayer);
			}
		}
		return humanPokerPlayers;
	}

	private void tweetAllHumanPlayers(String message) {
		String tagHumanPlayers = "";
		for (HumanPokerPlayer humanPokerPlayer : getHumanPlayers()) {
			tagHumanPlayers += "@" + humanPokerPlayer.getName() + " ";
		}

		try {
			TwitterBot.getAPI().updateStatus(Constants.HASH_TAG + Constants.NEW_LINE + message + Constants.NEW_LINE + tagHumanPlayers);
		} catch (TwitterException e) {
			e.printStackTrace();
		}
	}

	// Dealing cards for players
	private void dealCards() {
		System.out.println("------------------");
		for (int i = 0; i < players.size() * HandOfCards.HAND_SIZE; i++) {
			players.get(i % players.size()).addCard(deck.dealNext());
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

	private void removePlayers() {
		for (int i = 0; i < players.size(); i++) {
			PokerPlayer player = players.get(i);
			if (player.getFunds() == 0) {
				players.remove(player);
			}
		}
	}

	// Betting, folding, raising
	private void betting(int startingPlayer) {
		int bet = STARTING_BET;
		int lastBet = bet;
		int counter = players.size(); // when 0, betting is finished
		for (int i = startingPlayer; counter != 0; i++) {
			int size = players.size();
			PokerPlayer player = players.get(i % size);
			if (player.getFolded()) {
				System.out.println("SKIP PLAYER");
				continue;
			}
			System.out.println("---------------------------------");
			System.out.println("Last bet by any player was " + lastBet);
			System.out.println("Last bet for " + player.getName() + " was " + lastBets.get(player));
			// Get the bet from a player
			System.out.println(player.getName() + " has to add " + (lastBet - lastBets.get(player)) + " to the pot");
			bet = player.bet((lastBet - lastBets.get(player)));

			// if bet is -1, player folded
			if (bet == PokerPlayer.BET_FOLD) {
				System.out.println(player.getName() + " has folded");
				//i = folding(i, size);
				players.get(i % size).setFolded(true);
				counter--;
				// otherwise, add bet to the pot
			} else {
				pot += bet;
				splitPots.put(player, splitPots.get(player) + bet);
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
				System.out.println("Counter " + counter);
				System.out.println("Show pot: " + pot);
			}
		}
	}

	// Play the round
	public void playRound() {
		createHashmapForBets(players);
		createHashmapForPot(players);
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
			removePlayers();
			// no one can open, so start new round
		} 
	}
}