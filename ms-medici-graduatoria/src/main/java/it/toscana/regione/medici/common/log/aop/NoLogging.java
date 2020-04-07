package it.toscana.regione.medici.common.log.aop;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * annotation used to hide log parameter
 * 
 * <p>
 * Mark a method parameter with this annotation in order to hide the parameter
 * on logger
 * </p>
 * 
 * @author fgiuntoli
 *         
 */
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PARAMETER, ElementType.TYPE, ElementType.METHOD })
public @interface NoLogging {

}
