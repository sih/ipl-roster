package eu.waldonia.ipl.repository;

import org.springframework.data.neo4j.repository.GraphRepository;

import eu.waldonia.ipl.domain.Country;

public interface CountryRepository extends GraphRepository<Country>{

	public Country findCountryByName(String name);
	
}
