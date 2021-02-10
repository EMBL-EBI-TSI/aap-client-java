/*
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.tsc.aap.client.test.security.context.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Trimmed-down version of {@code WithMockUserSecurityContextFactoryTests.java} 
 * 
 * @author geoff
 * @see https://github.com/spring-projects/spring-security/blob/4.2.1.RELEASE/test/src/test/java/org/springframework/security/test/context/support/WithMockUserSecurityContextFactoryTests.java
 */
@RunWith(MockitoJUnitRunner.class)
public class WithMockAapUserSecurityContextFactoryTests {

     @Mock
     private WithMockAapUser withAapUser;

     private WithMockAapUserSecurityContextFactory factory;

     @Before
     public void setup() {
          factory = new WithMockAapUserSecurityContextFactory();
     }

     @Test(expected = IllegalArgumentException.class)
     public void usernameNull() {
          factory.createSecurityContext(withAapUser);
     }

     @Test
     public void usernameWorks() {
          when(withAapUser.username()).thenReturn("customUser");
          when(withAapUser.authorities()).thenReturn(new String[] {});

          assertThat(factory.createSecurityContext(withAapUser)
                            .getAuthentication()
                            .getName())
                    .isEqualTo(withAapUser.username());
     }

     @Test
     public void authoritiesWorks() {
          when(withAapUser.username()).thenReturn("customUser");
          when(withAapUser.authorities()).thenReturn(new String[] { "USER", "CUSTOM" });

          assertThat(factory.createSecurityContext(withAapUser)
                            .getAuthentication()
                            .getAuthorities())
                    .extracting("domainName")
                    .containsOnly("USER", "CUSTOM");
     }
}