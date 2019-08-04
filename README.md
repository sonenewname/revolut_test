To start and test this demo project you can simply run `start.sh -p <port>` script


## Examples

Creating new account

Request:
```
POST http://localhost:8080/api/accounts
Content-Type: application/json
 
{
  "name": "account_name",
}
```

Response:
```json
{
    "id": 1,
    "balance": 0,
    "name": "account_name",
}
```

Get account by id

Request:
```
GET http://localhost:8080/api/accounts/:id
```

Response:
```json
{
    "id": 1,
    "balance": 30,
    "name": "account_name",
}
```

Get all operation by account

Request:
```
GET http://localhost:8080/api/accounts/:id/operations
```

Response: 
```json
[
    {
        "from": 1,
        "to": 2,
        "amount": 13.54
    },
    {
        "from": 1,
        "to": 2,
        "amount": 13.54
    },
    {
        "to": 1,
        "amount": 100.00
    }
]
```

Make deposit to account

Request:
```
POST http://localhost:8080/api/operations/deposit
Content-Type: application/json

{
  "to": "1",
  "amount": 100
}
```

Response:
```json
{
    "to": 1,
    "amount": 100
}
```

Make transfer from A to B account

Request:
```
POST http://localhost:8080/api/operations
Content-Type: application/json

{
  "from": "1",
  "to": "2",
  "amount": 13.54
}
```

Response:
```json
{
    "from": 1,
    "to": 2,
    "amount": 13.54
}
```



