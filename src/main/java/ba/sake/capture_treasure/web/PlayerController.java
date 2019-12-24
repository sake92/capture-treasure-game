package ba.sake.capture_treasure.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ba.sake.capture_treasure.model.Player;
import ba.sake.capture_treasure.repository.PlayerRepository;

@RestController
@RequestMapping("/api/players")
public class PlayerController {

	@Autowired
	PlayerRepository playerRepository;

	@GetMapping("")
	public List<Player> findAll() {
		return playerRepository.findAll();
	}

	@GetMapping("/{id}")
	public Player findOne(@PathVariable Long id) {
		return playerRepository.findOne(id);
	}

}
