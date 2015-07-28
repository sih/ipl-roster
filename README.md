# ipl-roster

Overview
--------
Spring Data Neo4J project to populate roster details (players, franchises, contract values) from a sanitised copy of the Wikipedia dataset.

The Files
---------
The roster is sourced from a file per franchise. The file represents the players on the franchise's roster in a particular year (should really be a season, but the IPL currently doesn't cross a calendar boundary so this isn't at problem at the moment). The files are organised in a structure that can be used to infer other relationships (i.e. the year which this roster is for). This pretty much follows the convention: /<yyyy>/<domain>/<team>, for example /2015/roster/csk.txt would have the roster for the Chennai Super Kings for 2015.

The idea of this is so that additional files can be added to the directory structure and additional projects run against the data. An example would be /2015/stats/csk.txt or /2015/stats/dwaynebravo.txt, or even /2015/results/april/08/csk_vs_dd.txt. Ultimately the data itself should be made a project in its own right.

Implementation
--------------
This uses the neo4j ogm (Object Graph Mapping) libraries and annotations (see https://github.com/neo4j/neo4j-ogm) rather than the Spring Data annotations to bind the domain objects to the underlying database. Spring Data is still used for some of the DDD concepts like repositories, and Spring is used in general for dependency injection.

