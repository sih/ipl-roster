package eu.waldonia.ipl.repository;

import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.transaction.annotation.Transactional;
import eu.waldonia.ipl.domain.Player;

@Transactional
public interface PlayerRepository extends GraphRepository<Player> {

	Player findPlayerByName(String name);
}
