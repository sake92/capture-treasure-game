package ba.sake.capture_treasure.model;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;
import ba.sake.capture_treasure.model.Board;
import ba.sake.capture_treasure.model.Field;
import ba.sake.capture_treasure.model.Row;
import ba.sake.capture_treasure.model.Field.Type;

public class BoardTests {

	@Test(expected = IllegalArgumentException.class)
	public void testThrowWhenSizetooSmall() {
		new Board(3, 1, 1, 1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testThrowWhenSizeOdd() {
		new Board(7, 1, 1, 1);
	}

	@Test
	public void testNumRowsInitialized() {
		Board board = new Board(8, 5, 3, 4);
		board.init();
		assertThat(board.getRows().size(), is(8));
	}

	@Test
	public void testNoWaterBorder() {
		Board board = new Board(8, 5, 3, 4);
		board.init();
		List<Row> rows = board.getRows();
		for (int i = 0; i < rows.size(); i++) {
			Row row = rows.get(i);
			List<Field> fields = row.getFields();
			for (int j = 0; j < fields.size(); j++) {
				Field f = fields.get(j);
				if (i == rows.size() / 2 || i == (rows.size() / 2) - 1) {
					f.setType(Type.WATER);
				} else {
					f.setType(Type.GRASS);
				}
			}
		}
		boolean ok = board.isOk(new Field(0, 0));
		assertFalse(ok);
	}

	@Test
	public void testNoIslands() {
		Board board = new Board(8, 5, 3, 4);
		board.init();
		List<Row> rows = board.getRows();
		for (int i = 0; i < rows.size(); i++) {
			Row row = rows.get(i);
			List<Field> fields = row.getFields();
			for (int j = 0; j < fields.size(); j++) {
				Field f = fields.get(j);
				f.setType(Type.GRASS);
			}
		}
		rows.get(0).getFields().get(1).setType(Type.WATER);
		rows.get(1).getFields().get(0).setType(Type.WATER);

		boolean ok = board.isOk(rows.get(3).getFields().get(3));
		assertFalse(ok);
	}

}
