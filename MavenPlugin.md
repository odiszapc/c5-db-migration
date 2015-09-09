## Getting Started ##

This functionality is easy to enable in a mavenized project. First you add the Carbon Five public plugin repository:

```
<pluginRepositories>
  <pluginRepository>
    <id>c5-public-repository</id>
    <url>http://mvn.carbonfive.com/public</url>
  </pluginRepository>
</pluginRepositories>
```

And then you configure the migration plugin:

```
...
<build>
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
</build>
...
```

We've hard-coded values for url, username, and password for the sake of example.  These would most likely be properties that can be overridden for numerous environments (dev, staging, production) via maven profiles (see below).

The plugin doesn't have dependencies on any specific JDBC drivers, so you'll have to include the dependencies which include your specific driver.

Lastly, you drop in your migration scripts into the src/main/db/migrations directory, naming them using the pattern YYYYMMDDHHMMSS\_name.sql, where YYYYMMDDHHMMSS is a fourteen digit timestamp. Some examples:

  * 20080115200312\_create\_users\_table.sql
  * 20080518092345\_add\_default\_users.sql
  * 20080718214051\_add\_lastvisit\_column.sql
  * 20080802134142.sql

The name is optional and isn’t used for anything, it’s just there so that other developers can get an idea of what a script does without having to open it.

From the command line, you can run the migration plugin like this:

```
$ mvn db-migration:migrate
```

If you want the plugin to also create the database, you can invoke the plugin like this:

```
$ mvn db-migration:create db-migration:migrate
```

Sometimes you want to blow away the database and start fresh:

```
$ mvn db-migration:reset
```

You can also just check to see if the database is up-to-date:

```
$ mvn db-migration:validate
```

When it's time to create a new migration script, you can do so by:
```
$ mvn db-migration:new -Dname=users_and_roles
```
Which results in a new, empty file called: src/main/db/migrations/yyyyMMddHHmmss\_users\_and\_roles.sql (assuming default settings).

I’ve created a simple, complete sample that shows off this functionality, it’s located on the Carbon Five public subversion repository [here](http://svn.carbonfive.com/public/christian/migration-sample1/trunk). Check it out from subversion and then read the readme.txt at the top of the project.

## Maven Plugin Goal Reference ##

The maven plugin has several goals:

| **Goal** | **Description** |
|:---------|:----------------|
|db-migration:check|Check to see if the database is up-to-date and fail the build if there are pending migrations|
|db-migration:create|Create a new, empty database<sup>1</sup>|
|db-migration:drop|Drop the database<sup>1</sup>|
|db-migration:migrate|Apply all pending migrations<sup>2</sup>|
|db-migration:new|Create a new, empty migration file (-name=... to include a name in the filename)|
|db-migration:reset|Drop the existing database, create a new one, and apply all pending migrations<sup>1</sup>|
|db-migration:validate|Check to see if the database is up-to-date and report pending migrations|

Notes:
  1. Must have create and drop database privileges.
  1. Must have schema update privileges.

## Maven Plugin Configuration ##

### General Settings ###

Here is the exhaustive list of configuration options and the goal to which they apply.

| **Option** | **Description** | **Required** | **Default** | **Goals** |
|:-----------|:----------------|:-------------|:------------|:----------|
| driver     | JDBC driver class name (fully-qualified) | No           | Autodetected from JDBC connection URL | migrate, validate, drop, create, reset |
| url        | JDBC connection URL | Yes          | N/A         | migrate, validate, drop, create, reset |
| username   | Database username | Yes          | N/A         | migrate, validate, drop, create, reset |
| password   | Database password | No           | "" - empty string | migrate, validate, drop, create, reset |
| databaseType| MYSQL, POSTGRESQL, SQL\_SERVER, HSQL, H2, DB2, ORACLE, UNKNOWN | No           | Autodetected from JDBC connection URL | migrate, validate, drop, create, reset |
| migrationsPath | Location of migrations | No           | "src/main/db/migrations/**"**|migrate, validate, new |
| versionPattern | Date format for new migration filenames | No           | "yyyyMMddHHmmss" | new       |
| versionTimeZone | Time zone for new migration filename date format | No           | "UTC"       | new       |
| migrationExtension | Filename extension for new migrations | No           | ".sql"      | new       |
| versionTable | Name of version tracking table| No           | "schema\_version" | migrate, validate |
| versionColumn | Name of version column | No           | "version"   | migrate, validate |
| appliedDate| Name of applied-date column | No           | "applied\_on" | migrate, validate |
| durationColumn | Name of duration column | No           | "duration"  | migrate, validate |
| createSql  | SQL to execute to create the database | No           | "create database %s"<sup>1</sup> | create, drop, reset |
| dropSql    | SQL to execute to drop the database | No           | "drop database %s"<sup>1</sup> | create, drop, reset |

Notes:
  1. Where %s is the name of the database, as extracted from the JDBC connection URL.

## Fail the Build if the Database is Out of Date ##

Tests can sometime fail in weird ways if they're run against an out-of-date (i.e. there are pending migrations) database.  It's easy to configure the plugin to report pending migrations and fail the build if the database is out-of-date.  Here's an example of the configuration required to do so:

```
...
<build>
   ...
  <plugin>
    <groupId>com.carbonfive.db-support</groupId>
    <artifactId>db-migration-maven-plugin</artifactId>
    <version>RELEASE</version>
    <executions>
      <execution>
        <phase>validate</phase>
        <goals>
          <goal>check</goal>
        </goals>
      </execution>
    </executions>
    <configuration>
      <url>jdbc:mysql://localhost/myapp_test</url>
      <username>dev</username>
      <password>dev</password>
    </configuration>
    ...
  </plugin>
</build>
...
```

## Handling Multiple Environments ##

In any real world application, a number of environments must be supported.  At the very least, there are unit testing and application databases for each developer.  And then most likely, a staging and production database.  This can be handled using a native maven feature, [build profiles](http://maven.apache.org/guides/introduction/introduction-to-profiles.html).

Consider the following plugin configuration...

```
<build>
  <plugins>
    ...
    <plugin>
      <groupId>com.carbonfive.db-support</groupId>
      <artifactId>maven-db-migration-plugin</artifactId>
      <version>RELEASE</version>
      <configuration>
        <url>${jdbc.url}</url>
        <username>${jdbc.username}</username>
        <password>${jdbc.password}</password>
      </configuration>
      ...
    </plugin>
  </plugins>
  ...
</build>
```

The three placeholders can be resolved to values that come from [a number of locations](http://maven.apache.org/pom.html#Properties).  Additionally, they could be defined in one or more [maven build profiles](http://maven.apache.org/guides/introduction/introduction-to-profiles.html).  Sometimes our developers share a common database server and in other cases (when possible) we run the database on our development machines.  I'll tackle each in turn:

### Local Database (localhost) ###

```
...
<properties>
  <!-- Default to localhost but allow for simple overriding. -->
  <jdbc.host>localhost</jdbc.host>
</properties>
...
<profiles>
  <profile>
    <id>test</id>
    <activation>
      <activeByDefault>true</activeByDefault>
    </activation>
    <properties>
      <jdbc.url>jdbc:mysql://${jdbc.host}/myapp_test</jdbc.url>
      <jdbc.username>dev</jdbc.username>
      <jdbc.password>dev</jdbc.password>
    </properties>
  </profile>
  <profile>
    <id>development</id>
    <properties>
      <jdbc.url>jdbc:mysql://${jdbc.host}/myapp_development</jdbc.url>
      <jdbc.username>dev</jdbc.username>
      <jdbc.password>dev</jdbc.password>
    </properties>
  </profile>
</profiles>
...
```

### Shared Development Database Server ###

```
...
<properties>
  <!-- Default to staging but allow for simple overriding. -->
  <jdbc.host>staging-db</jdbc.host>
</properties>
...
<profiles>
  <profile>
    <id>test</id>
    <activation>
      <activeByDefault>true</activeByDefault>
    </activation>
    <properties>
      <jdbc.url>jdbc:mysql://${jdbc.host}/${env.USERNAME}_myapp_test</jdbc.url>
      <jdbc.username>dev</jdbc.username>
      <jdbc.password>dev</jdbc.password>
    </properties>
  </profile>
  <profile>
    <id>development</id>
    <properties>
      <jdbc.url>jdbc:mysql://${jdbc.host}/${env.USERNAME}_myapp_development</jdbc.url>
      <jdbc.username>dev</jdbc.username>
      <jdbc.password>dev</jdbc.password>
    </properties>
  </profile>
</profiles>
...
```

Note that `jdbc.username` and `jdbc.password` could be defined in each developer's settings.xml if greater security was desired.

### Staging and Production ###

```
...
  <profile>
    <id>staging</id>
    <properties>
      <jdbc.url>jdbc:mysql://192.168.2.122/myapp_staging</jdbc.url>
      <jdbc.username>myapp_user</jdbc.username>
      <jdbc.password>8H6%gj8n</jdbc.password>
    </properties>
  </profile>
  <profile>
    <!-- This would likely only be present in settings.xml for whoever pushed to production. --> 
    <id>production</id>
    <properties>
      <jdbc.url>jdbc:mysql://10.4.5.341/myapp_production</jdbc.url>
      <jdbc.username>myapp_user</jdbc.username>
      <jdbc.password>gH7Ga5jw</jdbc.password>
    </properties>
  </profile>
...
```

### Profile Location and Activation ###

Profiles can be defined in the pom, a parent pom, individual developer's settings.xml, and an external profiles.xml.  This gives you a few options for where best to define a specific profile.  And with resolution mechanism for properties (e.g. ${jdbc.url), you can support just about anything you might need.

Profiles can be manually activated using the `mvn -Pdevelopment <goal>` syntax.  They can also be activated automatically when certain criteria are met.