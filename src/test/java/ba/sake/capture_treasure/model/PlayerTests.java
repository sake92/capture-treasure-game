package ba.sake.capture_treasure.model;

import static org.junit.Assert.assertFalse;

import org.junit.Test;
import ba.sake.capture_treasure.model.Board;
import ba.sake.capture_treasure.model.Player;

public class PlayerTests {

	@Test
	public void testFoundGoldFalse() {
		Board board = new Board();
		Player player1 = new Player(true, board);
		assertFalse(player1.isFoundGold());
	}

}
