# Cognixus Assessment
## 1.1 Run the app
```bash
docker compose up
```
## 1.2 Test the app
Update ``spring.datasource.url`` to ``jdbc:postgresql://localhost:5432/postgres`` to point to PostgreSQL server
```bash
mvn test
```
## 1.3 Build the app
```bash
mvn clean package
docker build -t assessment .
```
## 1.4 Documentation
- URL `http://localhost:1234`
- All API with path name other than `/login` will go through token verification to ensure user is logged in.
- Login expiry time can be configured in application.yml `config.login-expire`

### App APIs
| Path | Method | API Name  | Usage |
|------|--------|----------| -------|
| /login | GET | Login | `http:localhost:1234/login` |
| /login/redirect | GET | Google Auth Redirect | `http://localhost:1234/login/redirect?code=example_code` |
| /main/get-todo-list | GET | Get todo list | `http://localhost:1234/main/get-todo-list` |
| /main/add | POST | Get todo list | `http://localhost:1234/main/add/get-todo-list`
| /main/set/{action} | POST | Update todo list status | `http://localhost:1234/main/set/DONE` `http://localhost:1234/main/set/UNDONE` `http://localhost:1234/main/set/DELETE` |

### 1. /login
```bash
GET http://localhost:1234/login
```
##### Headers
N/A

##### Parameters
N/A

### 2. /login/redirect
```bash
GET http://localhost:1234/login/redirect?code=sample_token
```
##### Headers
N/A

##### Parameters
| Type | Name | Value | Description | Required |
|------|------|-------|-------------|----------|
| QueryString | code | sample_token | Token from GoogleAuth login redirect | <b>Yes</b> |

### 3. /main/get-todo-list
```bash
GET http://localhost:1234/main/get-todo-list
```

#### Header
| Name | Value | Description | Required |
|------|-------|-------------|----------|
| token | ya29.a0AfB_byDh6o5T0GuElK9lSI6k-E... | Authentication token, get from `/login` | <b>Yes</b> |

#### Parameters
N/A

### 4. /main/add
```bash
POST http://localhost:1234/main/add
```

#### Header
| Name | Value | Description | Required |
|------|-------|-------------|----------|
| token | ya29.a0AfB_byDh6o5T0GuElK9lSI6k-E... | Authentication token, get from `/login` | <b>Yes</b> |

#### Parameters
| Type | Name | Value | Description | Required |
|------|------|-------|-------------|----------|
| RequestBody | | sample_title | Title of the todo item | <b>Yes</b> |

### 5. /main/set/&#123;action&#125;
```bash
POST http://localhost:1234/main/set/DONE
```

#### Header
| Name | Value | Description | Required |
|------|-------|-------------|----------|
| token | ya29.a0AfB_byDh6o5T0GuElK9lSI6k-E... | Authentication token, get from `/login` | <b>Yes</b> |

#### Parameters
| Type | Name | Value | Description | Required |
|------|------|-------|-------------|----------|
| PathVariable | action | DONE / UNDONE / DELETE | Action wanted to be done for the todo item | <b>Yes</b> |
| RequestBody | |sample_title | Title of the todo item to be updated | <b>Yes</b> |
