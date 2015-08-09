package eu.waldonia.ipl;

import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import org.springframework.context.annotation.*;
import org.springframework.data.neo4j.config.Neo4jConfiguration;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.data.neo4j.server.Neo4jServer;
import org.springframework.data.neo4j.server.RemoteServer;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableNeo4jRepositories("eu.waldonia.ipl.repository")
@EnableTransactionManagement
@ComponentScan("eu.waldonia.ipl")
@EnableAspectJAutoProxy
public class PersistenceContext extends Neo4jConfiguration {

    public static final int NEO4J_PORT = 7479;

    @Override
    public SessionFactory getSessionFactory() {
        return new SessionFactory("eu.waldonia.ipl.domain");
    }

    @Bean
    public Neo4jServer neo4jServer() {
        return new RemoteServer("http://localhost:" + NEO4J_PORT);
    }

    @Override
    @Bean
    public Session getSession() throws Exception {
        return super.getSession();
    }
}
