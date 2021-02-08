/*
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.tsc.aap.client.test.security.context.support;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.security.test.context.support.WithSecurityContext;

import uk.ac.ebi.tsc.aap.client.test.util.ClientTestUtil;

/**
 * Adapted from original Spring work in {@code spring-security-test-4.2.1.RELEASE}.
 * <p>
 * Behind the scenes the created security context UserDetails will have {@code null}
 * values assigned for email, user name and full name.
 * 
 * @author geoff
 * @see ClientTestUtil
 * @see WithMockAapUserSecurityContextFactory
 * @see https://github.com/spring-projects/spring-security/blob/4.2.1.RELEASE/test/src/main/java/org/springframework/security/test/context/support/WithMockUser.java
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@WithSecurityContext(factory = WithMockAapUserSecurityContextFactory.class)
public @interface WithMockAapUser {
    /**
     * The username to be used - which <b>MUST</b> be supplied (as per the contract of 
     * {@code UserDetails#getUsername()}).
     * <p>
     * Note that for consistency the AAP user reference value should be used, as {@code User#getUsername()}
     * (and therefore {@code Authentication#getPrincipal()} returns the user reference.
     *  
     * @return
     */
    String username() default "defaultUsername";

    /**
     * <p>
     * The authorities to use. A {@link Domain} will be created for each value.
     * </p>
     *
     * @return
     */
    String[] authorities() default {};
}