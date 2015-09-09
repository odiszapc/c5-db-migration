Latest Version: 0.9.9-m5 (check out the new check goal!)

## Overview ##

The [Carbon Five](http://www.carbonfive.com) Database Migration framework and Maven plugin for Java provides a simple solution to the problem of managing discrete, incremental changes to databases over time across multiple environments.  Each migration is versioned and tracked when applied to the database.  Sensible defaults reduce the amount of necessary configuration, though the framework can be configured and extended.  Before this google code page, this framework was announced on our [Community Blog](http://blog.carbonfive.com/2008/02/java/introducing-java-db-migrations).

## Key Maven Plugin Goals ##

| **Goal** | **Description** |
|:---------|:----------------|
|db-migration:create|Create a new, empty database|
|db-migration:migrate|Apply all pending migrations|
|db-migration:reset|Drop the existing database, create a new one, and apply all pending migrations|
|db-migration:new|Create a new, empty migration script|
|db-migration:check|Check for pending migrations, fail the build if the db is not up to date|

## Sample Maven Plugin Configuration ##

```
...
<plugin>
    <groupId>com.carbonfive.db-support</groupId>
    <artifactId>db-migration-maven-plugin</artifactId>
    <version>RELEASE</version>
    <configuration>
        <url>jdbc:mysql://localhost/myapp_test</url>
        <username>dev</username>
        <password>dev</password>
    </configuration>
    <dependencies>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>5.1.6</version>
        </dependency>
    </dependencies>
</plugin>

<pluginRepositories>
    <pluginRepository>
        <id>c5-public-repository</id>
        <url>http://mvn.carbonfive.com/public</url>
    </pluginRepository>
</pluginRepositories>
...
```

## Other Details ##

By default, migrations are loaded from the directory src/main/db/migrations and follow the naming convention yyyymmddhhmmss\_some\_description.sql.  Version history is stored in the database in the schema\_version table, which captures the version, date-applied, and how long it took for the migration to execute for each applied migration.

## Supported Databases ##

All functionality is supported for MySQL, PostgreSQL, MS SQL Server, HSQL, and H2Database, though the migrate functionality is likely to work with many other database systems.  Create/drop/reset are less likely to work out of the box.  Our goal is to support all of the major database systems and we're eager to hear how it works with other systems.

## Documentation ##

Check out the [Maven Plugin Guide](MavenPlugin.md) if you're interested in manually triggering database updates.  If you want to embed the migration framework in your application, check out the [Application Embedding Guide](ApplicationEmbedding.md).