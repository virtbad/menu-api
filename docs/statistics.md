# Statistics
There are also a few endpoints which are used to display general statistics about the data this api guards. Currently, there is only one endpoint.

## Get amount of menus
This request lets you fetch the amount of menus which are persisted in the database. This is useful for displaying some useless information.
> ```GET /stats/menu```

* **Request**
    * Authorization: None
    * Body: No
* **Response**
    * Body: Object containing amount
    * Common Errors: None

#### Response Body
```typescript
{
  amount: int    // How many menus are stored in the database
}
```