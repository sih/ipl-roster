package eu.waldonia.ipl.domain;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class Handedness {

	@GraphId Long id;
}
