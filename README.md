# Rate Limiter Spring Boot Starter

Spring Boot application integrates rate limitation quickly, to prevent too frequent accesses.

## Quickstart

- Import dependencies

```xml
    <dependency>
        <groupId>com.yookue.springstarter</groupId>
        <artifactId>rate-limiter-spring-boot-starter</artifactId>
        <version>LATEST</version>
    </dependency>
```

> By default, this starter will auto take effect, you can turn it off by `spring.rate-limiter.enabled = false`

- Configure Spring Boot `application.yml` with prefix `spring.rate-limiter` (**Optional**)

```yml
spring:
    rate-limiter:
        denied-html-url: '/error/rate-limited'
        denied-rest-url: '/error/rate-limited'
        storage-type: 'redis'
        name-prefix: "${spring.application.name}:limiter:"
        throws-exception: false
```

- Annotate your (non-static)  method with `@RateLimited` annotation, done!

> The `keyType` attribute of the annotation, is the limitation ways, supports
  - IP address
  - session
  - username

- This starter needs to save the limitation data to somewhere, currently is
  - redis

## Document

- Github: https://github.com/yookue/rate-limiter-spring-boot-starter

## Requirement

- jdk 1.8+

## License

This project is under the [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0)

See the `NOTICE.txt` file for required notices and attributions.

## Donation

You like this package? Then [donate to Yookue](https://yookue.com/public/donate) to support the development.

## Website

- Yookue: https://yookue.com
