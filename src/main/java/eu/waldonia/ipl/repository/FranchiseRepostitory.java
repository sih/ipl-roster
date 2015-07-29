package eu.waldonia.ipl.repository;

import org.springframework.data.neo4j.repository.GraphRepository;

import eu.waldonia.ipl.domain.Franchise;

public interface FranchiseRepostitory extends GraphRepository<Franchise> {

	Franchise findByCode(String code);
	
}
