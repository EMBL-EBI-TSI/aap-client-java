# Java client for the EBI AAP services

This is a library with common functionality to interact with the EBI's Authentication, Authorization
and Profile service. It currently (Jan 2017) covers the authentication classes to secure rest API
using spring.

## Getting Started

Choose which aspect of the client you'd like to use:
- `security` helps protect your API's endpoint via a token produced by the AAP
- `service` helps making calls to the AAP API


Include the jar as dependency to your project (for example `service` with gradle):

```groovy
repositories {
	maven { url "https://oss.sonatype.org/content/repositories/snapshots" }
}

dependencies {
	compile( group: 'uk.ac.ebi.tsc.aap.client', name: 'service', version: '0.1-SNAPSHOT')
}
```

Use the provided classes to secure your API: more to come on this section soon!

### Prerequisites

We are using this library with a few spring-boot applications, and anything with spring-security/spring-web
should be easy to infer.


### Installing

* Download [Gradle](https://gradle.org/gradle-download/) (if you haven't already got it...)
* Checkout the [source code](https://github.com/EMBL-EBI-TSI/aap-client-java.git)
*

To come next: getting a JWT from the AAP, and using it to check which domain a given user is part of.

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

