# Hosting

This guide is for people, which would like to host this api for themselves, targeting their own sv-group restaurant.

## Using Docker

The easiest way to host this api is to use docker. For that, you need to have docker installed on your machine.

This repository contains an automatic workflow which builds a docker image and pushes it to the github container registry. From there you can pull it and run it on your machine:

```bash
docker pull ghcr.io/virtbad/menu-api:latest
```
 
> Every image has its own tag, which is the same as the version of the api. You can find all available tags [here](https://github.com/virtbad/menu-api/tags). 
> To get the latest version use the `latest` tag.

### Configuration

Once pulled you need to run the container with the following environment variables to configure it properly.
All environment variables which aren't empty can be omitted. When omitted the default value will be used. 
The empty ones are specific for every deployment and have therefore to be set manually.

```bash
# Search Engine Config
SPRING_JPA_PROPERTIES_HIBERNATE_SEARCH_DEFAULT_DIRECTORY_PROVIDER="filesystem"
SPRING_JPA_PROPERTIES_HIBERNATE_SEARCH_DEFAULT_INDEXBASE="indexes"

# Spring Config
SERVER_ERROR_INCLUDE_MESSAGE="always"
SPRING_JACKSON_DEFAULT_PROPERTY_INCLUSION="non_null"

# Set your port the api should run on. If directly exposed, this should be something like port 80.
SERVER_PORT="80"

# Ip from which new menus can be uploaded. This is used to limit the access to the upload endpoint.
MENU_UPLOAD_IP="127.0.0.1"

# Microsoft OAuth Config
# Provide the tenant id of your organisation
CUSTOM_MICROSOFT_TENANT=""
# Provide the client id of your created application
CUSTOM_MICROSOFT_CLIENT=""
# Provide the domain on which the mail addresses of your organisation ends
CUSTOM_MICROSOFT_MAIL_SUFFIX=""

# Database Access and Config
SPRING_JPA_HIBERNATE_DDL_AUTO="update"
# Provide a connection url that jdbc understands
SPRING_DATASOURCE_URL=""
# Provide the user details for a user which has access to the database referenced in the connection url
SPRING_DATASOURCE_USERNAME=""
SPRING_DATASOURCE_PASSWORD=""
# Leave this if you are using MariaDB
SPRING_DATASOURCE_DRIVER_CLASS_NAME="org.mariadb.jdbc.Driver"
SPRING_JPA_PROPERTIES_HIBERNATE_DIALECT="org.hibernate.dialect.MariaDBDialect"
```

## Obtaining an Executable

The first step would be to obtain an executable of the api. This is relatively easy. A quick tutorial for Unix-based
systems is listed here:

1. Make sure that you have a JDK installed.
2. Clone the repository and open it in a terminal.
3. Make the gradlew script executable: ```chmod +x gradlew```
4. Run the build using ```./gradlew bootJar```
5. Locate the built jar in the build/libs folder.

These simple steps provide you with an executable jar, which can be run with an installed JRE.

## Installation Configuring

The next step would be to configure the api to your needs. For that, you should create an application.properties file in
the same folder you have put the jar.

As a next step, you should get some things ready:

* Install a sql-based database server somewhere (preferably MariaDB).
* Find out your tenant id for your target organisation.
* Create your own microsoft application in your Azure Active Directory.

In your application.properties file, you should provide the api with the following entries:

```properties
! This part of the config can be left as is, yet can be customized to meet special needs.

! Search Engine Config
spring.jpa.properties.hibernate.search.default.directory_provider=filesystem
spring.jpa.properties.hibernate.search.default.indexBase=indexes

! Spring Config
server.error.include-message=always
spring.jackson.default-property-inclusion=non_null


! These config entries are specific to your particular install.

! Set your port the api should run on. If directly exposed, this should be something like port 80.
server.port=[your-port]

! Ip from which new menus can be uploaded. This is used to limit the access to the upload endpoint.
menu.upload.ip=127.0.0.1

! Microsoft OAuth Config
! Provide the tenant id of your organisation
custom.microsoft.tenant=[tenant-id]
! Provide the client id of your created application 
custom.microsoft.client=[client-id]
! Provide the domain on which the mail addresses of your organisation ends
custom.microsoft.mailsuffix=[organisation-mail.domain]

! Database Access and Config
spring.jpa.hibernate.ddl-auto=update
! Provide a connection url that jdbc understands
spring.datasource.url=[jdbc-connection-url]
! Provide the user details for a user which has access to the database referenced in the connection url
spring.datasource.username=[username]
spring.datasource.password=[password]
! Leave this if you are using MariaDB
spring.datasource.driver-class-name=org.mariadb.jdbc.Driver
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MariaDBDialect
```

## Hosting

Now you have configured your instance of the api successfully. The next step is now to host it somewhere. For that we
recommend using a VPS, on which you then must only run your built jar.

It is also recommended using a reverse proxy to do the ssl stuff, like NGINX.

## Obtaining Menus

Your last step will be to add some menus to your database. This api does not provide functionality to do that directly.

Therefore, you could use a tool like our [menu-updater](https://github.com/VirtBad/menu-updater), which is written to
scrape and insert menus from a restaurant's menuplan. For more information, refer to its repo. Obviously, you could also
use one of your own scripts or tools to do that. For that, the api provides a special [endpoint](menu.md#Submit Menu).