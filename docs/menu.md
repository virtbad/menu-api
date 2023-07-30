# Menus

This section covers the endpoints on which you can fetch menus on the website, to display and do things with.

## Menu Information

The following request is to fetch the information about a specific menu found in the database. <br>
For more information or modifications about an individual menu, there is a whole page about that [here](interaction.md).
> ```GET /menu/{id}```

* **Request**
    * Authorization: None
    * Body: No
    * URL Parameters
        * ```id```: uuid - This parameter specifies the uuid of the menu to get.
* **Response**
    * Body: List of Menu Objects
        * Sorted: By relevance
    * Common Errors:
        * 404 Not Found - Menu not present in database

## Discover Menus

The following set of requests are designed to discover menus in the database also to get their id.

### Search Menus

On this request, you can search for menus in the database with a search query and a few other filter options.
> ```GET /menu/search?query=burger```

* **Request**
    * Authorization: None
    * Body: No
    * Request Parameters
        * ```query```: string - This parameter contains the query the user searches for and is required.
        * ```page```: int - This parameter specifies which page of menus are requested, by default the 0th.
        * ```channel```: int - This parameter is a filter parameter to filter by channel of the menu and is optional.
        * ```label```: int - This parameter is a filter parameter to filter by menu label and is optional.
        * ```start```: timestamp - This parameter is a filter parameter to filter by date and is optional. With this
          parameter, all before the provided date are then ignored.
        * ```end```: timestamp - This parameter is a filter parameter to filter by date and is optional. All menus with
          a date after this parameter will get ignored.
* **Response**
    * Body: List of Menu Objects
        * Sorted: By relevance
    * Common Errors: None

### All Menus

On this request, you can get all menus that are in the database.
> ```GET /menu/all```

* **Request**
    * Authorization: None
    * Body: No
    * Request Parameters
        * ```page```: int - This parameter specifies which page of menus are requested, by default the 0th.
* **Response**
    * Body: List of Menu Objects
        * Sorted: By date of the Menu. The newest first.
    * Common Errors: None

### Dated Menus

On this request, you can get all menus on a specific date.
> ```GET /menu/date```

* **Request**
    * Authorization: None
    * Body: No
    * Request Parameters
        * ```date```: timestamp - This parameter specifies the date of which the menus should be fetched, defaulting to
          today.
* **Response**
    * Body: List of Menu Objects
    * Common Errors: None

### Upcoming Menus

On this request, you can get all menus that are yet to come in a mensa.
> ```GET /menu/upcoming```

* **Request**
    * Authorization: None
    * Body: No

* **Response**
    * Body: List of Menu Objects
        * Sorted: by Date
    * Common Errors: None

## Submit Menu

The following request is to submit a new menu into the database. Please be aware, that due to abuse concerns, this
endpoint can only be called from **remote addresses which are included in the trust regex**. Refer to
the [Hosting](hosting.md) documentation for more information.
> ```POST /menu```

* **Request**
    * Authorization: Request must be from Local Host
    * Body: Complete Menu Object
* **Response**
    * Body: No
    * Common Errors:
        * 403 Forbidden - Request was not made from local host
        * 400 Bad Request - Body Menu Object is not complete
