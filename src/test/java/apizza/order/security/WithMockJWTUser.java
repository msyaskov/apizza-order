package apizza.order.security;


import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockJWTUserSecurityContextFactory.class)
public @interface WithMockJWTUser {

    static final String DEFAULT_USER_ID = "e75a27e2-908a-4104-b9f0-e8a220b03ef8";

    String userId() default DEFAULT_USER_ID;

}
