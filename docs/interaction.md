# Menu Interactions

This section covers how a user can interact with a menu, by rating commenting or doing other things with it.

## Votes

### Get Vote on Menu

This request is to fetch whether a logged-in user has already voted on a menu and in which direction.
> ```GET /menu/{id}/vote```

* **Request**
    * Authorization: Yes
    * Body: No
    * URL Parameters
        * ```id```: uuid - This parameter specifies the uuid of the target menu.
* **Response**
    * Body: Object containing voter direction
    * Common Errors:
        * Common Authorization Errors
        * 404 Not Found - Menu not present in database

#### Response Body

```typescript
{
  direction: int    // Positive = Upvoted, Negative = Downvoted, Zero = Not Voted
}
```

### Vote on Menu

This request is to cast a vote on a menu.
> ```PUT /menu/{id}/vote```
* **Request**
  * Authorization: Yes
  * Body: Yes
  * URL Parameters
    * ```id```: uuid - This parameter specifies the uuid of the target menu.
* **Response**
  * Body: Object containing created comment id
  * Common Errors:
    * Common Authorization Errors
    * 404 Not Found - Menu not present in database

#### Request Body

```typescript
{
  direction: int    // Positive = Upvoted, Negative = Downvoted, Zero = Not Voted
}
```

## Comments

### Get Existing Comments

The following request is to fetch the comment which are written for a menu.
> ```GET /menu/{id}/comment```

* **Request**
    * Authorization: Yes
    * Body: No
    * URL Parameters
        * ```id```: uuid - This parameter specifies the uuid of the target menu.
* **Response**
    * Body: List of Comment Objects
        * Sorted: By Date Created
    * Common Errors:
        * Common Authorization Errors
        * 404 Not Found - Menu not present in database

### Post Comment

With the following request one can post a comment to a menu
> ```POST /menu/{id}/comment```

* **Request**
    * Authorization: Yes
    * Body: Specific
    * URL Parameters
        * ```id```: uuid - This parameter specifies the uuid of the target menu.
* **Response**
    * Body: No
    * Common Errors:
        * Common Authorization Errors
        * 404 Not Found - Menu not present in database

#### Request Body

```typescript
{
  rating: float,    // Star rating of the comment (1 - 5)
  title: string,    // String of the title (64 char)
  content: string   // String of the content (256 char)
}
```

### Edit Comment

Edits a comment that one has written. This request can be made by the commenter or an admin.
> ```PUT /menu/{id}/comment/{cid}```

* **Request**
    * Authorization: Yes
    * Body: Same as on "Post Comment" request
    * URL Parameters
        * ```id```: uuid - This parameter specifies the uuid of the target menu.
        * ```cid```: uuid - This parameter specifies the uuid of the target comment.
* **Response**
    * Body: No
    * Common Errors:
        * Common Authorization Errors
        * 403 Forbidden - No right to edit comment
        * 404 Not Found - Comment or Menu not present in database

### Delete Comment

Deletes a written comment This request can be made by the commenter or an admin.
> ```DELETE /menu/{id}/comment/{cid}```

* **Request**
    * Authorization: Yes
    * Body: No
    * URL Parameters
        * ```id```: uuid - This parameter specifies the uuid of the target menu.
        * ```cid```: uuid - This parameter specifies the uuid of the target comment.
* **Response**
    * Body: No
    * Common Errors:
        * Common Authorization Errors
        * 403 Forbidden - No right to delete comment
        * 404 Not Found - Menu not present in database