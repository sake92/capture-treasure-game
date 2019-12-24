package ba.sake.capture_treasure.model;

import java.util.Random;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Player {

	private static final Logger logger = LoggerFactory.getLogger(Player.class);

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@OneToOne(cascade = CascadeType.ALL)
	private Field position;

	@JsonIgnore
	@OneToOne
	private Board board;

	private boolean isPlayer1;

	@OneToOne(cascade = CascadeType.ALL)
	private Field castlePosition;
	private boolean foundGold;

	public Player() {
	}

	public Player(boolean isPlayer1, Board board) {
		this.isPlayer1 = isPlayer1;
		this.board = board;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Field getPosition() {
		return position;
	}

	public void setPosition(Field position) {
		this.position = position;
	}

	public boolean isPlayer1() {
		return isPlayer1;
	}

	public void setPlayer1(boolean isPlayer1) {
		this.isPlayer1 = isPlayer1;
	}

	public Board getBoard() {
		return board;
	}

	public void setBoard(Board board) {
		this.board = board;
	}

	public Field getCastlePosition() {
		return castlePosition;
	}

	public void setCastlePosition(Field castlePosition) {
		this.castlePosition = castlePosition;
	}

	public boolean isFoundGold() {
		return foundGold;
	}

	public void setFoundGold(boolean foundGold) {
		this.foundGold = foundGold;
	}

	/* METHODS */
	public void populateBoardHalf() {
		logger.info("Populating player" + (isPlayer1 ? 1 : 2) + " board half...");
		int boardSizeHalf = board.getSize() / 2;
		// if player1 start from 0 else from size/2
		int startIndex = isPlayer1 ? 0 : boardSizeHalf;
		int endIndex = isPlayer1 ? boardSizeHalf : board.getSize();
		populate(startIndex, endIndex);
	}

	/* HELPERS */
	private void populate(int startIndex, int endIndex) {
		int fieldsTotal = board.getSize() * (board.getSize() / 2);

		int grassFieldsMin = board.getMinGrass();
		int mountainFieldsMin = board.getMinMountain();
		int waterFieldsMin = board.getMinWater();

		// generating random (total) numbers of grass/mountain/water fields
		Random r = new Random();
		int grassFieldsMax = fieldsTotal - grassFieldsMin - mountainFieldsMin - waterFieldsMin;
		int grassFieldsTotal = (grassFieldsMin + r.nextInt(grassFieldsMax - 1));
		if (grassFieldsTotal > 7) {
			grassFieldsTotal = 7;
		}
		int mountainFieldsMax = fieldsTotal - grassFieldsTotal - mountainFieldsMin - waterFieldsMin;
		int mountainFieldsTotal = grassFieldsMin + r.nextInt(mountainFieldsMax);
		int waterFieldsTotal = fieldsTotal - grassFieldsTotal - mountainFieldsTotal;

		// populate random grass
		int rowBound = endIndex - startIndex;
		placeRandomFields(startIndex, rowBound, r, grassFieldsTotal, Field.Type.GRASS);
		// populate random grass
		placeRandomFields(startIndex, rowBound, r, mountainFieldsTotal, Field.Type.MOUNTAIN);
		// populate random water
		placeRandomFields(startIndex, rowBound, r, waterFieldsTotal, Field.Type.WATER);

		// place castle
		placeCastle(startIndex, rowBound, r);
	}

	private void placeRandomFields(int startIndex, int rowBound, Random r, int fieldsTypeTotal, Field.Type type) {
		int i = 0;
		while (i < fieldsTypeTotal) {
			int randomRow = r.nextInt(rowBound) + (isPlayer1 ? 0 : startIndex);
			int randomCol = r.nextInt(board.getSize());
			Field field = board.getRows().get(randomRow).getFields().get(randomCol);
			if (field.getType() == null) {
				field.setType(type);
				i++;
			}
		}
	}

	private void placeCastle(int startIndex, int rowBound, Random r) {
		int randomRow = r.nextInt(rowBound) + (isPlayer1 ? 0 : startIndex);
		int randomCol = r.nextInt(board.getSize());
		Field field = board.getRows().get(randomRow).getFields().get(randomCol);
		if (field.getType().isGrass()) {
			this.castlePosition = field;
		} else {
			placeCastle(startIndex, rowBound, r);
		}
	}

	@Override
	public String toString() {
		return "Player [id=" + id + ", position=" + position + ", isPlayer1=" + isPlayer1 + ", castlePosition="
				+ castlePosition + ", foundGold=" + foundGold + "]";
	}

}
