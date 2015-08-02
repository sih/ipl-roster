package eu.waldonia.ipl.repository;

import org.springframework.data.neo4j.repository.GraphRepository;

import eu.waldonia.ipl.domain.Player;

public interface PlayerRepository extends GraphRepository<Player> {

	Player findPlayerByName(String name);
}
