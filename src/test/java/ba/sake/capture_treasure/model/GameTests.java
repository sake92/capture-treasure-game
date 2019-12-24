package ba.sake.capture_treasure.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;
import ba.sake.capture_treasure.model.Board;
import ba.sake.capture_treasure.model.Game;
import ba.sake.capture_treasure.model.Player;
import ba.sake.capture_treasure.model.Game.Status;

public class GameTests {

	@Test
	public void testGameStarted() {
		Board board = new Board();
		Player player1 = new Player(true, board);
		Player player2 = new Player(false, board);
		Game game = new Game(player1, player2, board);
		assertEquals(game.getStatus(), Status.STARTED);
	}

	@Test
	public void testPlayersGoldPlace() {
		Game game = new Game();
		Board board = new Board();
		game.setBoard(board);
		Player player1 = new Player(true, board);
		Player player2 = new Player(false, board);
		game.setPlayer1(player1);
		game.setPlayer2(player2);

		/* initialization */
		do {
			board.init();
			player1.populateBoardHalf();
			player2.populateBoardHalf();
		} while (!board.isOk(player1.getCastlePosition()));

		game.placePlayersAndGold();

		assertNotEquals(player1.getPosition(), player1.getCastlePosition());
		assertNotEquals(player1.getPosition(), board.getPlayer1GoldPosition());
		assertNotEquals(player1.getCastlePosition(), board.getPlayer1GoldPosition());

		assertNotEquals(player2.getPosition(), player2.getCastlePosition());
		assertNotEquals(player2.getPosition(), board.getPlayer2GoldPosition());
		assertNotEquals(player2.getCastlePosition(), board.getPlayer2GoldPosition());
	}

}
