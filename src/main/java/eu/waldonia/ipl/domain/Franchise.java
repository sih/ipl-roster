package eu.waldonia.ipl.domain;

import java.util.Set;

import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

@NodeEntity
public class Franchise {

	@GraphId Long id;
	public String name;
	public String code;
	
	@RelatedTo(type="HOLDS")
	private Set<Contract> contracts;
	
}
