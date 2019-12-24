package ba.sake.capture_treasure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ba.sake.capture_treasure.model.Board;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {

}
