# moneybuddy

Welcome to moneybuddy's backend.

### First step

```bash
cp .env.example .env
```

insert the right values to variables

### Second step

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

## API Reference

### Auth

##### Register

```http
  POST /auth/register
```

| Parameter         | Type     | Description   |
| :---------------- | :------- | :------------ |
| `email`           | `string` | **Required**. |
| `password`        | `string` | **Required**. |
| `confirmPassword` | `string` | **Required**. |
| `pin`             | `string` | **Required**. |
| `name`            | `string` | **Required**. |

##### Login

```http
  POST /auth/login
```

| Parameter  | Type     | Description   |
| :--------- | :------- | :------------ |
| `email`    | `string` | **Required**. |
| `password` | `string` | **Required**. |

##### Get Me

```http
  GET /auth/me
```

| Parameter  | Type     | Description                          |
| :--------- | :------- | :----------------------------------- |
| `JwtToken` | `string` | **Required**. Authorization : Bearer |

##### SubAccount Login

```http
  POST /auth/subAccount/login
```

| Parameter  | Type     | Description                         |
| :--------- | :------- | :---------------------------------- |
| `JwtToken` | `string` | **Required**. Authorization: Bearer |
| `id`       | `string` | **Required**.                       |
| `pin`      | `string` | **Required For Parent SubAccount**. |

##### Get SubAccount Me

```http
  GET /auth/subAccount/me
```

| Parameter  | Type     | Description                          |
| :--------- | :------- | :----------------------------------- |
| `JwtToken` | `string` | **Required**. Authorization : Bearer |

### Task

#### Create Task

Only parents can create tasks.

```http
  POST /tasks
```

| Parameter      | Type     | Description                          |
| :------------- | :------- | :----------------------------------- |
| `JwtToken`     | `string` | **Required**. Authorization : Bearer |
| `description`  | `string` | **Required**.                        |
| `category`     | `string` | **Required**.                        |
| `subAccountId` | `string` | **Required**.                        |
| `reward`       | `string` | **Required**.                        |
| `dateLimit`    | `string` | **Required**.                        |

#### Get Tasks

```http
  GET /tasks
```

| Parameter  | Type              | Description                          |
| :--------- | :---------------- | :----------------------------------- |
| `JwtToken` | `string`          | **Required**. Authorization : Bearer |
| `source`   | `PARENT or CHILD` | **Optinal**.                         |

#### Get Task

```http
  GET /tasks/id
```

| Parameter  | Type     | Description                          |
| :--------- | :------- | :----------------------------------- |
| `JwtToken` | `string` | **Required**. Authorization : Bearer |
| `id`       | `string` | **Required**.                        |

#### Delete Task

Only parents can delete tasks.

```http
  DELETE /tasks/id
```

| Parameter  | Type     | Description                          |
| :--------- | :------- | :----------------------------------- |
| `JwtToken` | `string` | **Required**. Authorization : Bearer |
| `id`       | `string` | **Required**.                        |

#### Modify Task

Only parents can modify tasks.

```http
  PUT /tasks/id
```

| Parameter      | Type     | Description                          |
| :------------- | :------- | :----------------------------------- |
| `JwtToken`     | `string` | **Required**. Authorization : Bearer |
| `id`           | `string` | **Required**.                        |
| `description`  | `string` | **Required**.                        |
| `category`     | `string` | **Required**.                        |
| `subAccountId` | `string` | **Required**.                        |
| `reward`       | `string` | **Required**.                        |
| `dateLimit`    | `string` | **Required**.                        |

#### Set Task as complete

Only parents can sent a task as complete.

```http
  PUT /tasks/complete/id
```

| Parameter  | Type     | Description                          |
| :--------- | :------- | :----------------------------------- |
| `JwtToken` | `string` | **Required**. Authorization : Bearer |
| `id`       | `string` | **Required**.                        |
