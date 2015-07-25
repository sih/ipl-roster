package eu.waldonia.ipl.domain;

import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

@NodeEntity
public class DOB {

	@GraphId Long id;
	
	@RelatedTo(type="DAY")
	@Fetch private Day day;
	
	@RelatedTo(type="MONTH")
	@Fetch private Month month;
	
	@RelatedTo(type="YEAR")
	@Fetch private Year year;
}
