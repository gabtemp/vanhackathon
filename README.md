# No Leftovers

This project aims to connect restaurants that have leftover food with places that need that food.

## Stack
This application is built using Java 8 + Spring Boot with the support of the following tools:

- JPA/Hibernate
- H2 (Development/Local testing environment)
- MySQL **(Not implemented yet)**
- Liquibase **(Not implemented yet)**
- Jersey
- Docker

## Features

- Register restaurants that have leftover food and when they are available for pick ups.

## Planned Features

- Register shelter of other places that may need the leftover food. **(Not implemented yet)**
- Register courriers that will take the leftover food from the restaurants to those shelters. **(Not implemented yet)**
- Provide the courriers with the best route of the available restaurants. **(Not implemented yet)**
## Build

To build and run the application just run the following commands on the root folder of the project

```
mvn clean package
docker-compose up --build
```

And the application will be available on `http://localhost:8080/`

## Usage

This section describes all available HTTP methods provided by the API.

All methods returns a response body content in JSON format (or an error message). A specific restaurant can only be accessed
directly via ID (`/restaurant/{id}`).

### GET Methods ###

#### Find all ####
- Path: `/`

  Returns all registered restaurants.
  
#### Find by id ####  
- Path: `/{id}/`

  Returns the specific restaurant with the id `{id}`. Or 404 status if it doesn't exists.

### POST Methods ###

#### Create new restaurant ####
- Path: `/`
- Form parameters:
  - name: The name of the restaurant,
  - address: The address of the restaurant,
  - pickUpDays: A comma separated list of the week days that the restaurant is available for pick-ups. E.g.: "FRIDAY,SATURDAY,SUNDAY".
  - pickUpTime: The time that the restaurant is available in the format `hh:mm`.

  Registers a new restaurant, if successful the newly created restaurant will be displayed with it's generated id with a 201 HTT status. If unsuccessful a 400 status will be displayed.
  All fields are required, except `pickUpDays` that has a default value of `MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY`.

### PUT Methods ###

#### Edit existing restaurant ####
- Path: `/{id}/`
- Body: ```{
  "name": "Restaurant name",
  "address": "Restaurant St.",
  "pickUpDays": "FRIDAY, SATURDAY",
  "pickUpTime": "22:45",
}```

  Updates an existing restaurant with the new data, if successful the new data will be displayed with a HTTP status 200. If unsucessful a 400 status will be displayed.
  
### DELETE Methods ###

#### Delete existing restaurant ####
- Path: `/{id}/`

  Deletes an existing restaurant with the provided id, if successful the deleted restaurant will be displayed with a HTTP status 200. Returns the HTTP status 404 if the id is not found.
