package eu.waldonia.ipl.events;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.transaction.*;
import org.slf4j.*;
import org.springframework.data.neo4j.transaction.Neo4jTransactionStatus;
import org.springframework.stereotype.Component;

import eu.waldonia.ipl.populator.RosterFileProcessor;

/**
 * Not thread-safe
 * @author sid
 */
@Aspect
@Component
public class TransactionWatcher {

	Logger logger = LoggerFactory.getLogger("TransactionWatcher");
	
	Object itemToBeSaved;
	
	@After("execution(* org.springframework.data.neo4j.transaction.Neo4jTransactionManager.commit(..))")
	public void springCommit() throws Throwable {
		if (itemToBeSaved != null) {
			logger.info("COMMIT: Just committed item of class "+itemToBeSaved.getClass()+" and id ");			
		}
		itemToBeSaved = null;
	}
	
	@Around("execution(* org.neo4j.ogm.session.Session.save(..))")
	public void neoSave(ProceedingJoinPoint pjp) throws Throwable {
		
		Object[] args = pjp.getArgs();
		Session s = (Session)pjp.getThis();
		logger.info("Saving "+args[0].getClass()+" with attrs ");
		itemToBeSaved = args[0];
		pjp.proceed(args);
	}
	
}
