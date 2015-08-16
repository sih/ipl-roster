package eu.waldonia.ipl.events;

import java.lang.reflect.Field;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.neo4j.ogm.mapper.MappingContext;
import org.neo4j.ogm.session.*;
import org.slf4j.*;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.stereotype.Component;

/**
 * Not thread-safe - perthis?
 * @author sid
 */

@Aspect
public class TransactionWatcher {

	Logger logger = LoggerFactory.getLogger("TransactionWatcher");

	
	@After("execution(* org.neo4j.ogm.session.delegates.SaveDelegate.save(..))")
	public void inSaveDelegate() throws Throwable {
		logger.info("In delegate ... just saved");
	}
	
	@After("execution(* org.neo4j.ogm.mapper.EntityGraphMapper.map(..))")
	public void inMap() throws Throwable {
		logger.info("In map ... just mapped");
	}
	
	
	Object itemToBeSaved;
	
	@After("execution(* org.springframework.data.neo4j.transaction.Neo4jTransactionManager.commit(..))")
	public void springCommit() throws Throwable {
		if (itemToBeSaved != null) {
			logger.info("COMMIT: Just committed item of class "+itemToBeSaved.getClass()+" and id ");			
		}
		itemToBeSaved = null;
	}
	
	@Around("execution(* org.neo4j.ogm.session.Neo4jSession.save(..))")
	public void neoSave(ProceedingJoinPoint pjp) throws Throwable {

		Session s = (Session)pjp.getThis();
		Neo4jSession n4js = getTargetObject(s, Neo4jSession.class);

		Object[] args = pjp.getArgs();
		Object itemToBeSaved = args[0];

		// run this here so we have an id
		pjp.proceed(args);

		// grab the id of the object to be saved
		Long id = null;
		Field idField = itemToBeSaved.getClass().getDeclaredField("id");
		if (idField != null) {
			idField.setAccessible(true);
			id = (Long)(idField.get(itemToBeSaved));
		}
		
		// grab the mapping context
		Field mappingContextField = Neo4jSession.class.getDeclaredField("mappingContext");
		mappingContextField.setAccessible(true);
		MappingContext mc = (MappingContext) mappingContextField.get(n4js);

		if (id != null) {
			logger.info(mc.getNodeEntity(id).toString());
		}

	}

	/*
	 * @see http://www.techper.net/2009/06/05/how-to-acess-target-object-behind-a-spring-proxy/
	 */
	@SuppressWarnings({"unchecked"})
	private <T> T getTargetObject(Object proxy, Class<T> targetClass) throws Exception {
	  if (AopUtils.isJdkDynamicProxy(proxy)) {
	    return (T) ((Advised)proxy).getTargetSource().getTarget();
	  } 
	  else {
	    return (T) proxy; // expected to be cglib proxy then, which is simply a specialized class
	  }
	}
	
}
