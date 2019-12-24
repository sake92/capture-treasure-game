package ba.sake.capture_treasure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ba.sake.capture_treasure.model.Game;
import ba.sake.capture_treasure.model.Game.Status;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {

	Game findOneByStatus(Status status);

}
