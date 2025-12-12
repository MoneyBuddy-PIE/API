# moneybuddy

Welcome to moneybuddy's backend.

### First step

```bash
docker build -t moneybuddy-app .
```

```bash
docker run --env-file .env -p 8080:8080 moneybuddy-app
```

### Third step

Go to [Localhost](http://localhost:8080)

You can know dev, enjoy !

### Install dependencies

Skip test for now

```bash
mvn clean install -DskipTests
```

### Run localy

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=local -DskipTests
```

```bash
mvn clean install -DskipTests
```

## API Reference

Go to [Swagger] (http://localhost:8080/swagger-ui/index.html#/) for the documentation

## Lint

TO check

```bash
mvn spotless:check
```

TO apply

```bash
mvn spotless:apply
```
