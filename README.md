# Rate Limit Spring Boot Starter

Spring Boot application integrates rate limitation quickly, to prevent too frequent accesses.

## Quickstart

- Import dependencies

```xml
    <dependency>
        <groupId>com.yookue.springstarter</groupId>
        <artifactId>rate-limit-spring-boot-starter</artifactId>
        <version>LATEST</version>
    </dependency>
```

> By default, this starter will auto take effect, you can turn it off by `spring.rate-limit.enabled = false`

- Configure Spring Boot `application.yml` with prefix `spring.rate-limit` (**Optional**)

```yml
spring:
    rate-limit:
        name-prefix: "${spring.application.name}:limit:"
        denied-html-url: '/error/rate-limit'
        denied-rest-url: '/error/rate-limit'
        storage-type: 'redis'
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

- Github: https://github.com/yookue/rate-limit-spring-boot-starter

## Requirement

- jdk 17+

## License

This project is under the [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0)

See the `NOTICE.txt` file for required notices and attributions.

## Donation

You like this package? Then [donate to Yookue](https://yookue.com/public/donate) to support the development.

## Website

- Yookue: https://yookue.com
