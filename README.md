# Java client for the EBI AAP services

This is a library with common functionality to interact with the EBI's Authentication, Authorization
and Profile service. 

## Getting Started

Choose which aspect of the client you'd like to use:
- `security` helps protect your API's endpoints via a token produced by the AAP ([README](security/README.md)).  
- `service` helps making calls to the AAP API ([README](service/README.md)).

Include the jar as dependency to your project (for example `service` with gradle):

```groovy
repositories {
	maven { url "https://oss.sonatype.org/content/repositories/snapshots" }
}

dependencies {
	compile( group: 'uk.ac.ebi.tsc.aap.client', name: 'service', version: '1.0.2-SNAPSHOT')
}
```

### Configure which AAP environment to talk to

By default, the client uses our 'explore' environment, which we use as a sandbox for other parties integrating with us.
To switch to another environment (for example, production https://api.aai.ebi.ac.uk), add the following properties 
(for ex, in your main `application.properties`):

If you use the service module:
```properties
aap.url=https://api.aai.ebi.ac.uk
```
If you use the security module:
```properties
jwt.certificate=https://api.aai.ebi.ac.uk/meta/public.der
```

If you happen to use both, you can re-use the URL property in the definition of the certificate property so they're 
always in sync:
```properties
aap.url=https://api.aai.ebi.ac.uk
jwt.certificate=${aap.url}/meta/public.der
```

If you would rather not read the public key dynamically on startup, you can instead download it 
(https://api.aai.ebi.ac.uk/meta/public.der for production), bundle it with your resources and update `jwt.certificate` 
to `path/to/the/public/certificate.der`.

### Prerequisites

We are using this library with a few spring-boot applications, and anything with spring-security/spring-web
should be easy to infer.

For building the components, you'll need to have setup a GPG signing key (for example by following the [instructions of
the good folks of github](https://help.github.com/articles/generating-a-new-gpg-key/#generating-a-gpg-key)), and define
a signatory in gradle (typically in `~/.gradle/gradle.properties`):
```properties
signing.keyId=1A2B3C4D
signing.password=changeme
signing.secretKeyRingFile=path/to/secring.gpg
```
*Note* the long SHA does not seem to work (at least on windows), so use  `$ gpg --list-secret-keys` instead, and what you need in `keyId` is what's in `sec` after the `/`.

It is also necessary to have defined the following variables (even if you are not using the uploadArchive task):
```properties
ossrhUsername=someone
ossrhPassword=secret
```

### Installing

* Download [Gradle](https://gradle.org/gradle-download/) (if you haven't already got it...)
* Checkout the [source code](https://github.com/EMBL-EBI-TSI/aap-client-java.git)

## Running the tests

```bash
gradle test
```

## Deployment

Package this library along with your application, as best relevant to your chosen dependency management system.

## Built With

* [Spring Boot](https://projects.spring.io/spring-boot/) - The framework used
* [Gradle](https://gradle.org) - Dependency Management
* [Jenkins](https://jenkins.io/) - Continuous Delivery

## Versioning

We use [SemVer](http://semver.org/) for versioning.

