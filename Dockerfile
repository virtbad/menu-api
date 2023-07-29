FROM gradle:7.6.2-jdk17 as builder
WORKDIR /app

COPY . .

# RUN chmod +x gradlew
# RUN ./gradlew build
RUN gradle build

FROM openjdk:latest as runner
WORKDIR /app

# TODO: Find a way to copy the jar file without knowing the version but excluding the -plain.jar
COPY --from=builder /app/build/libs/menu-api-0.0.1-SNAPSHOT.jar ./menu-api.jar

# SEARCH ENGINE CONFIG
ENV SPRING_JPA_PROPERTIES_HIBERNATE_SEARCH_DEFAULT_DIRECTORY_PROVIDER "filesystem"
ENV SPRING_JPA_PROPERTIES_HIBERNATE_SEARCH_DEFAULT_INDEXBASE "indexes"

# SPRING CONFIG
ENV SERVER_ERROR_INCLUDE_MESSAGE "always"
ENV SPRING_JACKSON_DEFAULT_PROPERTY_INCLUSION "non_null"

# SET YOUR PORT THE API SHOULD RUN ON. IF DIRECTLY EXPOSED, THIS SHOULD BE SOMETHING LIKE PORT 80.
ENV SERVER_PORT "80"

# MICROSOFT OAUTH CONFIG
# PROVIDE THE TENANT ID OF YOUR ORGANISATION
ENV CUSTOM_MICROSOFT_TENANT ""
# PROVIDE THE CLIENT ID OF YOUR CREATED APPLICATION
ENV CUSTOM_MICROSOFT_CLIENT ""
# PROVIDE THE DOMAIN ON WHICH THE MAIL ADDRESSES OF YOUR ORGANISATION ENDS
ENV CUSTOM_MICROSOFT_MAIL_SUFFIX ""

# DATABASE ACCESS AND CONFIG
ENV SPRING_JPA_HIBERNATE_DDL_AUTO "update"
# PROVIDE A CONNECTION URL THAT JDBC UNDERSTANDS
ENV SPRING_DATASOURCE_URL ""
# PROVIDE THE USER DETAILS FOR A USER WHICH HAS ACCESS TO THE DATABASE REFERENCED IN THE CONNECTION URL
ENV SPRING_DATASOURCE_USERNAME ""
ENV SPRING_DATASOURCE_PASSWORD ""
# LEAVE THIS IF YOU ARE USING MARIADB
ENV SPRING_DATASOURCE_DRIVER_CLASS_NAME "org.mariadb.jdbc.Driver"
ENV SPRING_JPA_PROPERTIES_HIBERNATE_DIALECT "org.hibernate.dialect.MariaDBDialect"

CMD ["java", "-jar", "menu-api.jar"]