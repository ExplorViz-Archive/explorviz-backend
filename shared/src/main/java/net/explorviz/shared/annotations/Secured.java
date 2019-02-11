package net.explorviz.shared.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.ws.rs.NameBinding;

/**
 * This annotation is used to enable custom authentication / validation aspects for web services. It
 * is nothing more but an annotation that can be found by using Java reflection. For an example,
 * check the authentication filter inside of the authentication web service.
 */
@NameBinding
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Secured {
}
