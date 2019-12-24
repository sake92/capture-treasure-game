package ba.sake.capture_treasure.model;

import java.util.Random;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Game {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@JsonIgnore
	@OneToOne(cascade = CascadeType.ALL)
	private Board board;

	@OneToOne(cascade = CascadeType.ALL)
	private Player player1;
	@OneToOne(cascade = CascadeType.ALL)
	private Player player2;

	@Enumerated(EnumType.STRING)
	private Status status;

	private int numMoves;
	@Lob
	@Column(length = 100000)
	private String moves = "";

	public Game() {
		this.status = Status.STARTED;
	}

	public Game(Player player1, Player player2, Board board) {
		this.player1 = player1;
		this.player2 = player2;
		this.board = board;
		this.status = Status.STARTED;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Player getPlayer1() {
		return player1;
	}

	public void setPlayer1(Player player1) {
		this.player1 = player1;
	}

	public Player getPlayer2() {
		return player2;
	}

	public void setPlayer2(Player player2) {
		this.player2 = player2;
	}

	public Board getBoard() {
		return board;
	}

	public void setBoard(Board board) {
		this.board = board;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public int getNumMoves() {
		return numMoves;
	}

	public void setNumMoves(int numMoves) {
		this.numMoves = numMoves;
	}

	public String getMoves() {
		return moves;
	}

	public void setMoves(String moves) {
		this.moves = moves;
	}

	/* METHODS */
	public void placePlayersAndGold() {
		Random r = new Random();
		int boardSize = board.getSize();
		int boardSizeHalf = boardSize / 2;

		/* PLAYER POSITIONS */
		// player1 position
		while (player1.getPosition() == null) {
			int randRow = r.nextInt(boardSizeHalf);
			int randCol = r.nextInt(boardSize);
			Field f = board.getRows().get(randRow).getFields().get(randCol);
			if (f.getType().isGrass() && !player1.getCastlePosition().equals(f)) {
				player1.setPosition(f);
			}
		}
		// player2 position
		while (player2.getPosition() == null) {
			int randRow = r.nextInt(boardSizeHalf) + boardSizeHalf;
			int randCol = r.nextInt(boardSize);
			Field f = board.getRows().get(randRow).getFields().get(randCol);
			if (f.getType().isGrass() && !player2.getCastlePosition().equals(f)) {
				player2.setPosition(f);
			}
		}
		/* GOLD POSITIONS */
		// player1 gold position
		while (board.getPlayer1GoldPosition() == null) {
			int randRow = r.nextInt(boardSizeHalf);
			int randCol = r.nextInt(boardSize);
			Field f = board.getRows().get(randRow).getFields().get(randCol);
			if (f.getType().isGrass() && !player1.getCastlePosition().equals(f) && !player1.getPosition().equals(f)) {
				board.setPlayer1GoldPosition(f);
			}
		}
		// player2 gold position
		while (board.getPlayer2GoldPosition() == null) {
			int randRow = r.nextInt(boardSizeHalf) + boardSizeHalf;
			int randCol = r.nextInt(boardSize);
			Field f = board.getRows().get(randRow).getFields().get(randCol);
			if (f.getType().isGrass() && !player2.getCastlePosition().equals(f) && !player2.getPosition().equals(f)) {
				board.setPlayer2GoldPosition(f);
			}
		}
	}

	public static enum Status {
		STARTED, FINISHED_PLAYER1_WON, FINISHED_PLAYER2_WON, FINISHED_TIE
	}

}
