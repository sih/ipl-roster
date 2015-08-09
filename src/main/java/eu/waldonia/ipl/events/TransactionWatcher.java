package eu.waldonia.ipl.events;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.neo4j.ogm.session.transaction.*;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class TransactionWatcher {

	@Around("execution(* org.neo4j.ogm.session.Neo4jSession.doInTransaction(..))")
	public void sayHelloCommit(ProceedingJoinPoint pjp) throws Throwable {
		Object[] objects = pjp.getArgs();
		// TODO
		pjp.proceed(objects);
	}
	
}
