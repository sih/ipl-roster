# Indian Premier League (IPL) Roster

Overview
--------
Spring Data Neo4J project to populate roster details (players, franchises, contract values) from a sanitised copy of the Wikipedia dataset.

The Files
---------
The roster is sourced from a file per franchise. The file represents the players on the franchise's roster in a particular year (should really be a season, but the IPL currently doesn't cross a calendar boundary so this isn't at problem at the moment). The files are organised in a structure that can be used to infer other relationships (i.e. the year which this roster is for). This pretty much follows the convention: /<yyyy>/<domain>/<team>, for example /2015/roster/csk.txt would have the roster for the Chennai Super Kings for 2015.

The idea of this is so that additional files can be added to the directory structure and additional projects run against the data. An example would be /2015/stats/csk.txt or /2015/stats/dwaynebravo.txt, or even /2015/results/april/08/csk_vs_dd.txt. Ultimately the data itself should be made a project in its own right.

Model
-----

![Graph data model of roster](https://s3-eu-west-1.amazonaws.com/github-sih/ipl-roster.jpg "Roster Data Model")

Implementation
--------------
This uses the neo4j ogm (Object Graph Mapping) libraries and annotations (see https://github.com/neo4j/neo4j-ogm) rather than the Spring Data annotations to bind the domain objects to the underlying database. Spring Data is still used for some of the DDD concepts like repositories, and Spring is used in general for dependency injection.
Wherever possible, queries (mostly finders) have been automagically generated by Spring Data's Repositories. For more complex queries, e.g. finding a node by more that one of its properties, the finder has been annotated with a @Query, as the neo4j-ogm doesn't seem to support the FindBy<X>And<Y>And<Z> pattern.

Hooks have been added in order to capture save events on elements in the graph. There aren't any (useful) built in hooks so an Aspect has been added that first captures .save calls from the Neo4j Session, puts the object in to a local variable, and then emits it when a commit is called. This has the limitation that it assumes a single-threaded processor. At some point this needs to be refactored and solved, but for the purposes of loading up the roster, this is fine.

Events
------
The project uses AspectJ to watch for save events on the Spring Data POJOs. As well as instrumenting the beans, this needs to intercept calls to underlying neo4j-ogm objects, and hence uses compile-time weaving / load-time weaving.
