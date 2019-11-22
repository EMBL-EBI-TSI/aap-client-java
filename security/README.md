# AAP Client - Security

This component helps protect your API's endpoints via a token produced by the AAP.

Use the provided classes to secure your API.

In simple use cases you can rely on the [Auto-configuration](src/main/java/uk/ac/ebi/tsc/aap/client/security/AAPWebSecurityAutoConfiguration.java), which should automatically secure all your endpoints with AAP token when you add AAP Client security as a dependency to your Spring Boot project.
In more complicated cases you may need to provide your own Security Config (such as [WebSecurityConfigurer](https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/config/annotation/web/WebSecurityConfigurer.html)) placing [StatelessAuthenticationFilter](src/main/java/uk/ac/ebi/tsc/aap/client/security/StatelessAuthenticationFilter.java) in the Filter Chain.
If you still want to use Auto-configuration and send CORS requests to your application, you can set the property

```
aap-client.cors.enabled=true
```     
This will add [CorsFilter](https://docs.spring.io/spring-security/site/docs/5.2.1.RELEASE/reference/htmlsingle/#cors) and use your Spring MVC CORS configuration (such as `@CrossOrigin` annotations), which you need to provide.