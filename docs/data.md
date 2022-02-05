# Data

This page documents which datatypes are commonly used with this api and how certain common responses look. If something
behaves unexpectedly, please look whether the datatypes are used correctly.

## General

This uses the format JSON for every request and / or response. This is because JSON seems to be the standart for REST
APIs. The JSON is being serialized and deserialized using the Jackson Library, because of which certain things may be
specific to that.

## Primitive Datatypes

This documentation also uses a few seemingly "primitive" datatypes, which are explained in this section.

* ```timestamp```: The timestamp datatype is used to indicate that a date is transmitted, using the unix timestamp
  format. Please make sure that you use the **MILLISECOND** resolution version of the timestamp, because else, certain
  dates will not get deserialized correctly.
* ```uuid```: For some objects, this API uses uuids (version 4). These are transmitted as strings as you might expect.

## Common Objects

The api also uses a few common objects, which are used quite often all over the api. These are documented here.

### Menu Object
Pretty much every menu is transmitted in the same format. This format is specified here.

```typescript 
{
  id: uuid,             // Unique UUID of the menu
  title: string,        // Title of the menu
  description: string,  // Description of the menu
  date: timestamp,      // Timestamp of the date when the menu was served
  channel: int,         // Channel on which the menu occured (like different menu types)
  label: int,           // Label which the menu has (like vegetarian etc.).
  prices: Price[],      // Array of prices (see below) for different groups of customers.
  voteBalance: int      // Up- and Downvote balance, aka the score shown between the buttons.
}
```

### Price Object
Prices are also transmitted in a special format.

```typescript 
{
  tag: string,    // 3 or 4 Letter tag that specifies who the price is for.
  price: float,   // The actual price, in the corresponding currency.
}
```

## Constants
The api also uses different constant numbers which can mean different things.

### Labels
As mentioned above, a menu can have a few different labels.
* ```0``` - No special label at all.
* ```1``` - This menu is Vegetarian.
* ```2``` - This menu is Vegan.
* ```3``` - This menu belongs to the "One Climate" trend.