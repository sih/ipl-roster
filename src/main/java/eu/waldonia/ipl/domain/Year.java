package eu.waldonia.ipl.domain;

import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;

@NodeEntity
public class Year {

	@GraphId Long id;
}
