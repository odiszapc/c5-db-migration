## Migrating from your Application ##

Another usage scenario is to auto-migrate during application startup. At the core of the framework, there’s an interface called MigrationManager which has two implementations: DataSourceMigrationManager and DriverManagerMigrationManager. Migration happens right after a datasource (of the javax.sql variety) is created.

Migrating from your application is as easy as instantiating one of these early in the startup cycle and invoking the migrate() method, something like this:

```
MigrationManager migrationManager = new
    DriverManagerMigrationManager(“com.mysql.jdbc.Driver”, “jdbc:mysql://localhost/myapp_test”, “dev”, “dev”);
migrationManager.migrate();
```

Of course this needs to happen before anything else in the application uses the database; we want to database to be updated completely before it’s used.

Spring is part of our standard development stack on our Java projects, and it’s easy to enforce these dependencies in Spring configuration. First we define a data source for the application:

```
<bean id="dataSource" class="org.springframework.jdbc.datasource.DriverManagerDataSource">
  <property name="driverClassName" value="org.h2.Driver"/>
  <property name="url" value="jdbc:h2:file:~/.h2/migration_sample2_test"/>
  <property name="username" value="dev"/>
  <property name="password" value="dev"/>
</bean>
```

And now we declare our MigrationManager instance. Note the ‘init-method’ attribute:

```
<bean id="migrationManager"
    class="com.carbonfive.db.migration.DataSourceMigrationManager"
    init-method="migrate">
  <constructor-arg ref="dataSource"/>
</bean>
```

And then you define something that’s going to use the defined DataSource. Note the ‘depends-on’ attribute:

```
<bean id="userService"
    class="com.carbonfive.migration.sample2.UserService"
    depends-on="migrationManager">
  <constructor-arg ref="dataSource"/>
</bean>
```

This is obviously a little contrived for the sake of example, but you get the point. In a typical application the thing that would depend on the datasource is a Hibernate SessionFactory.

You can check out the source code for this example on the Carbon Five public subversion repository [here](http://svn.carbonfive.com/public/christian/migration-sample2/trunk).