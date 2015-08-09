package eu.waldonia.ipl.events;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.neo4j.ogm.session.transaction.*;
import org.springframework.data.neo4j.transaction.Neo4jTransactionStatus;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class TransactionWatcher {
		
	@Around("execution(* org.springframework.data.neo4j.transaction.Neo4jTransactionManager.commit(..))")
	public void springCommit(ProceedingJoinPoint pjp) throws Throwable {
		
		Object[] args = pjp.getArgs();
		Neo4jTransactionStatus txStatus = (Neo4jTransactionStatus)args[0];
		Transaction tx = txStatus.getTransaction();
		LongTransaction lt = (LongTransaction)tx;
		System.out.println("Coooooo "+tx.getClass());
		pjp.proceed(args);
	}
	
}
