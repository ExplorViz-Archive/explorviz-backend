package net.explorviz.broadcast.security.filters;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.ws.rs.NameBinding;

/**
 * You can annotate a resource with {@code @Secure} to instruct that inbound requests are
 * first passed to the {@link AuthenticationFilter} and {@link AuthorizationFilter}. Thus, you
 * should specify one of {@link javax.annotation.security.RolesAllowed},
 * {@link javax.annotation.security.DenyAll}, and {@link javax.annotation.security.PermitAll} at
 * the same method. If none is given, the request is denied by default. The latter annotations have
 * no effects if {@code @Secure} is not used.
 *
 * <p>If a resource class is annotated, the above explained applies to all contained resource
 * methods.
 *
 * <p>You need to make sure that {@link AuthenticationFilter} and {@link AuthorizationFilter} are
 * registered in this order at the application.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@NameBinding
public @interface Secure {
}
