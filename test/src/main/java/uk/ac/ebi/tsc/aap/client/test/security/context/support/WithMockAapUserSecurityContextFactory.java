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

import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.StringUtils;

import uk.ac.ebi.tsc.aap.client.model.Domain;
import uk.ac.ebi.tsc.aap.client.model.User;
import uk.ac.ebi.tsc.aap.client.security.UserAuthentication;

/**
 * Adapted from original Spring work in {@code spring-security-test-4.2.1.RELEASE} to create
 * a security context populated with an AAP {@link UserAuthentication} .
 * 
 * @see https://github.com/spring-projects/spring-security/blob/4.2.1.RELEASE/test/src/main/java/org/springframework/security/test/context/support/WithMockUserSecurityContextFactory.java
 */
final class WithMockAapUserSecurityContextFactory implements
            WithSecurityContextFactory<WithMockAapUser> {

    @Override
    public SecurityContext createSecurityContext(WithMockAapUser withUser) {
        String username = withUser.username();
        if (username == null || StringUtils.isEmpty(username)) {
            throw new IllegalArgumentException(withUser + " cannot have null or empty username");
        }

        final Set<Domain> domains = new HashSet<Domain>();
        for (String authority : withUser.authorities()) {
            domains.add(new Domain(authority, null, null));
        }

        final User user = new User(null, null, username, null, domains);
        // UserAuthentication's initialising constructor is protected, so using reflection
        final UserAuthentication authentication = new UserAuthentication();
        ReflectionTestUtils.setField(authentication, "user", user);

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        return context;
    }

}