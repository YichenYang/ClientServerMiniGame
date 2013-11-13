import java.io.Serializable;


public class GameStates implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String player1;
	String player2;
	String turn;
	String[] state;

	GameStates(String player1, String player2, String[] state) {
		this.player1 = player1;
		this.player2 = player2;
		this.state = state;
		this.turn = player1;
	}
}