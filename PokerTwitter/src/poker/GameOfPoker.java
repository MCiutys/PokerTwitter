
package poker;

import java.util.ArrayList;

public class GameOfPoker {
	private ArrayList<PokerPlayer> players;
	private RoundOfPoker round;

	public GameOfPoker() {
		players = new ArrayList<PokerPlayer>();
		round = createNewRound();
	}

	public void addPlayer(PokerPlayer player) {
		players.add(player);
	}

	public PokerPlayer removePlayer(PokerPlayer player) {
		return players.remove(players.indexOf(player));
	}

	public ArrayList<PokerPlayer> getPlayers() {
		return players;
	}

	public RoundOfPoker createNewRound() {
		round = new RoundOfPoker(players);
		return round;
	}
}
