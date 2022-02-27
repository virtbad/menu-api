# Authorization

As you might have noticed, some endpoints or requests require the requester to be authorized with some sort of account.

This API uses microsoft oauth for this. With that, a user can log in using their microsoft account that they already
have. Because this website is aimed for organisations or universities, which have their own sv-group restaurant, the
users should log in with their microsoft account which is provided by the organisation.

In order to ensure, that only people with a microsoft account from that specific tenant can log in, this api is provided
a tenant id, to which users have to belong in order to log in.

## Use the Authorization

So in order to perform requests, which seem to be exclusive to logged-in members, you should follow the standard
microsoft auth flow. For that, you'll need to provide a client id, which is used to authenticate the user with an "
application", which represents this api / website. This client id is different for each hosted instance, so contact the
hoster of your target api to find their client id. With this client id, and of course the tenant id of your
organisation, you should now simply be able to follow the microsoft auth flow.

There are some tools to assist you during this process like the library msal.

### Authorize Requests

When the login procedure is finished, your application should be provided with a token which can be used to authorize
the requests to the api.

To do this, just treat the token as any other JWT. So you should put it in the authorization header like follows:

```
Authorization: Bearer [your-token]
```

The api will then pick up the token and validate it on its own side. If it is invalid, a corresponding http error code
is sent.

### Check logged-in User
In order to check which user the token is for, you can send a request to the following endpoint of this api.

> ```GET /user```

* **Request**
    * Authorization: Yes
    * Body: No
* **Response**
    * Body: User object of the logged-in user
    * Common Errors:
        * Common Authorization Errors
