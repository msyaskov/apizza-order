package apizza.order.security;


import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockJWTAdminSecurityContextFactory.class)
public @interface WithMockJWTAdmin {

    static final String DEFAULT_USER_ID = "521e8e81-3498-443d-ac95-de5b12b2ace0";

    String userId() default DEFAULT_USER_ID;

}
