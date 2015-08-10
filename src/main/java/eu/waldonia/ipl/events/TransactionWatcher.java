package eu.waldonia.ipl.events;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.transaction.*;
import org.slf4j.*;
import org.springframework.data.neo4j.transaction.Neo4jTransactionStatus;
import org.springframework.stereotype.Component;

import eu.waldonia.ipl.populator.RosterFileProcessor;

@Aspect
@Component
public class TransactionWatcher {

	Logger logger = LoggerFactory.getLogger(TransactionWatcher.class);
	
	@Around("execution(* org.springframework.data.neo4j.transaction.Neo4jTransactionManager.commit(..))")
	public void springCommit(ProceedingJoinPoint pjp) throws Throwable {
		
		Object[] args = pjp.getArgs();
		Neo4jTransactionStatus txStatus = (Neo4jTransactionStatus)args[0];
		Transaction tx = txStatus.getTransaction();
		LongTransaction lt = (LongTransaction)tx;
		System.out.println("Coooooo "+tx.getClass());
		pjp.proceed(args);
	}
	
	@Around("execution(* org.neo4j.ogm.session.Session.save(..))")
	public void neoSave(ProceedingJoinPoint pjp) throws Throwable {
		
		Object[] args = pjp.getArgs();
		Session s = (Session)pjp.getThis();
		logger.info("Saving "+args[0].getClass()+" with attrs ");
		pjp.proceed(args);
	}
	
}
