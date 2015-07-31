package eu.waldonia.ipl.repository;

import java.util.List;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;

import eu.waldonia.ipl.domain.DOB;

public interface DOBRepository extends GraphRepository<DOB> {

	@Query("MATCH dob WHERE dob.day = {0} AND dob.month = {1} AND dob.year = {2} RETURN DISTINCT dob")
	DOB getBirthday(int day, int month, int year);
	
}
