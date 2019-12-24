package ba.sake.capture_treasure.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ba.sake.capture_treasure.model.Board;
import ba.sake.capture_treasure.model.Game;
import ba.sake.capture_treasure.service.GameService;

@RestController
@RequestMapping("/api/games")
public class GameController {

	@Autowired
	GameService gameService;

	@GetMapping
	public List<Game> findAll() {
		return gameService.findAll();
	}

	@GetMapping("/{id}")
	public Game findOne(@PathVariable Long id) {
		return gameService.findOne(id);
	}

	@PostMapping("")
	public Game create() {
		return gameService.create();
	}

	@GetMapping("/{id}/board")
	public Board currentBoard(@PathVariable Long id) {
		Game currentGame = gameService.findOne(id);
		if (currentGame == null)
			return null;
		return currentGame.getBoard();
	}

}
