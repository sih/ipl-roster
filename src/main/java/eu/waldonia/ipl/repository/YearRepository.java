package eu.waldonia.ipl.repository;

import org.springframework.data.neo4j.repository.GraphRepository;

import eu.waldonia.ipl.domain.Year;

public interface YearRepository extends GraphRepository<Year> {

	public Year findYearByYear(int year);
	
}
