package poker;

public class Main {

	// Just for testing
	public static void main(String[] args) {
		DeckOfCards deck = new DeckOfCards();
		
		AIPokerPlayer player1 = new AIPokerPlayer(deck, "John");
		AIPokerPlayer player2 = new AIPokerPlayer(deck, "Peter");
//		AIPokerPlayer player3 = new AIPokerPlayer(deck, "Mantas");
//		AIPokerPlayer player4 = new AIPokerPlayer(deck, "Tom");
		
		GameOfPoker game = new GameOfPoker();
		game.addPlayer(player1);
		game.addPlayer(player2);
//		game.addPlayer(player3);
//		game.addPlayer(player4);
		
		game.playRound();
	}

}
