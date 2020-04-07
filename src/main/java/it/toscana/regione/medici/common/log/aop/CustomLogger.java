package it.toscana.regione.medici.common.log.aop;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@Component
@Aspect
@Order
public class CustomLogger {

	private static final String OBSCURED_ARGUMENT = "xxxx";

	@Pointcut("execution(* it.toscana.regione.medici.controllers.*.*(..))")
	public void otherControllerExecution() {}

	@Pointcut("@annotation(it.toscana.regione.medici.common.log.aop.NoLogging)")
	public void noLogging() {}

	/**
	 * log exception during a normal flow operation, no matter where an exception is raised
	 *
	 */
	@AfterThrowing(pointcut = "execution(* it.toscana..*.*(..))", throwing = "ex")
	public final void processException(JoinPoint joinPoint, Throwable ex) throws Throwable {
		Logger logger = getLog(joinPoint);
		String methodName = joinPoint.getSignature().getName();
		String stuff = joinPoint.getSignature().toString();
		String arguments = isCrypted(joinPoint) ? "xxxxx" : Arrays.toString(getLoggableArguments(joinPoint));

		logger.error("Exception in method: {} with arguments {}. Full toString: {}. The exception is: {}", methodName,
				arguments, stuff, ex.getMessage());
/*		if (!ex.getClass().getSuperclass().equals(CustomAbstractException.class)) {
			logger.error("Exception: ", ex);
		}
		*/
	}

	/**
	 *
	 * log inbounds calls.
	 *
	 */
	@Before("otherControllerExecution() && !noLogging()")
	public final void logRestCall(JoinPoint joinPoint) throws Throwable {
		if (isBaseController(joinPoint) || isProxyClass(joinPoint)) {
			return;
		}
		Logger log = getLog(joinPoint);
		log.info(getLogMessage(joinPoint).toString());
	}

	@AfterReturning(pointcut = "otherControllerExecution() && !noLogging()", returning = "result")
	public final void logRestCallAfter(JoinPoint joinPoint, Object result) throws Throwable {
		if (isBaseController(joinPoint)) {
			return;
		}
		logResponseInJson(joinPoint, result);
		//logRestCallMethodReturn(joinPoint, result);
	}

	private void logResponseInJson(JoinPoint joinPoint, Object result)
	{
		Logger logger = getLog(joinPoint);
		ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        try {
        	logger.debug("\nResponse object: \n" + mapper.writeValueAsString(result));
        } catch (JsonProcessingException e) {
            System.out.println(e.getMessage());
        }
	}

	/**
	 *
	 * normal application log
	 *
	 */
	@Before("execution(* it..*.*(..)) && !otherControllerExecution() && !noLogging()")
	public final void logAllMethodsInputs(JoinPoint joinPoint) throws Throwable {
		if (isBaseController(joinPoint) || isProxyClass(joinPoint)) {
			return;
		}
		Logger log = getLog(joinPoint);
		if (log.isDebugEnabled()) {
			log.debug(getLogMessage(joinPoint).toString());
		}
	}

	@AfterReturning(pointcut = "execution(* it..*.*(..)) && !otherControllerExecution() && !noLogging()", returning = "result")
	public final void logAllMethodsReturnValues(JoinPoint joinPoint, Object result) throws Throwable {
		if (isBaseController(joinPoint)) {
			return;
		}
		logMethodReturn(joinPoint, result);
	}

	private static Logger getLog(JoinPoint jp) {
		return LoggerFactory.getLogger(jp.getTarget().getClass());
	}

	private StringBuilder getLogMessage(JoinPoint joinPoint) {
		StringBuilder logMessage = new StringBuilder();
		logMessage.append(joinPoint.getSignature().getName() + " ");
		Object[] args = getLoggableArguments(joinPoint);
		for (int i = 0; i < args.length; i++) {
			logMessage.append(args[i]).append(",");
		}
		if (args.length > 0) {
			logMessage.deleteCharAt(logMessage.length() - 1);
		}
		return logMessage;
	}

	private void logMethodReturn(JoinPoint joinPoint, Object result) {
		if (isProxyClass(joinPoint)) {
			return;
		}
		Logger log = getLog(joinPoint);
		Object output = isCrypted(joinPoint) ? OBSCURED_ARGUMENT : result;
		if (log.isDebugEnabled()) {
			log.debug("Method {} returned: {}", joinPoint.getSignature().getName(),
					result != null ? output.toString() : "void|null");
		}
	}
/*
	private void logRestCallMethodReturn(JoinPoint joinPoint, Object result) {
		if (isProxyClass(joinPoint)) {
			return;
		}
		Logger log = getLog(joinPoint);
		Object output = isCrypted(joinPoint) ? OBSCURED_ARGUMENT : result;
		log.info("Method {} returned: {}", joinPoint.getSignature().getName(),
				result != null ? output.toString() : "void|null");
	}
*/
	private boolean isProxyClass(JoinPoint joinPoint) {
		return StringUtils.contains(joinPoint.getTarget().getClass().toString(), "Proxy");
	}

	private boolean isBaseController(JoinPoint joinPoint) {
		return joinPoint.getArgs() != null && joinPoint.getArgs().length == 1
				&& joinPoint.getArgs()[0] instanceof Exception;
	}

	private boolean isCrypted(JoinPoint joinPoint) {
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		return signature.getMethod().getAnnotation(Obscure.class) != null;
	}

	private boolean isCrypted(Annotation[] annotations) {
		if (annotations != null) {
			for (Annotation annotation : annotations) {
				if (annotation instanceof Obscure) {
					return true;
				}
			}
		}
		return false;
	}

	private Object[] getLoggableArguments(JoinPoint joinPoint) throws SecurityException {
		if (isCrypted(joinPoint)) {
			return new Object[] { OBSCURED_ARGUMENT };
		}
		List<Object> loggableArgs = new ArrayList<>();

		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		Annotation[][] parameterAnnotations = signature.getMethod().getParameterAnnotations();
		for (int i = 0; i < joinPoint.getArgs().length; i++) {
			Annotation[] annotations = parameterAnnotations != null ? parameterAnnotations[i] : null;
			if (isCrypted(annotations)) {
				loggableArgs.add(OBSCURED_ARGUMENT);
			} else {
				loggableArgs.add(joinPoint.getArgs()[i]);
			}
		}

		return loggableArgs.toArray();
	}

}
