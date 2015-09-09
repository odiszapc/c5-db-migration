## v0.9.9-m5 (Milestone) on 2010-06-28 ##
  * Bind 'check' goal to 'process-test-resources' phase by default.  Thanks alon!
  * Fixed a bug with over-eager detection of postgresql procs/funcs.
  * Throw a migration exception when duplicate migration versions are found.
  * Make convertMigrationsLocation() protected and added DatabaseType as a parameter
for better subclassing potential.
  * Support native syntax for postgresql funcs/procs.
  * [#35](http://code.google.com/p/c5-db-migration/issues/detail?id=35) Switched to isReadable(), which works for migrations on the filesystem or within jars.  Thanks matthew.mceachen!
  * Miscellaneous code cleanup.

## v0.9.9-m2 (Milestone) on 2010-01-18 ##
  * [#30](http://code.google.com/p/c5-db-migration/issues/detail?id=30) Added new goal "check" which can be plugged into the lifecycle and fail a build if there are pending migrations (i.e. don't run the tests unless the database is up to date).

## v0.9.9-m1 (Milestone) on 2009-12-30 ##
  * [#28](http://code.google.com/p/c5-db-migration/issues/detail?id=28) Execute last statement even if it's missing its delimiter.  Thanks nik9000!
  * Updated many dependencies and cleaned up deprecations.

## v0.9.8 on 2009-09-02 ##
  * IMPORTANT: Maven groupId is now com.carbonfive.db-support.
  * [#22](http://code.google.com/p/c5-db-migration/issues/detail?id=22) Allow loading migrations from classpath (thanks kristof.jozsa!).
  * [#23](http://code.google.com/p/c5-db-migration/issues/detail?id=23) Add the trailing slash to a migration path that's missing it (thanks jeff.maxwell!).
  * Ignore directories when resolving migrations (thanks Darren Brown!).
  * Slightly improved output now includes the full name of migrations instead of just the version (migrate and validate mojos).

## v0.9.7 on 2009-02-26 ##
  * [#16](http://code.google.com/p/c5-db-migration/issues/detail?id=16) Don't reset custom delimiters (thanks tumakha!).

## v0.9.6 on 2009-01-19 ##
  * Fixed missing slf4j-log4j12 jar which was breaking the db-migration-maven-plugin.

## v0.9.5 on 2009-01-19 ##
  * Maven repository url has changed to: http://mvn.carbonfive.com/public
  * Migrated source from Carbon Five SVN to Google Code SVN.
  * db-migration:new now generates timestamps in UTC time (override using versionTimeZone configuration option)
  * Use SLF4J instead of commons-logging
  * Plugin artifact id renamed to db-migration-maven-plugin as per the recommended naming convention
  * [#6](http://code.google.com/p/c5-db-migration/issues/detail?id=6) ScriptRunner improvements for MySQL (delimiter support: functions and stored procs) (thanks tumakha!)
  * Properly quote database names for MySQL, PostgreSQL, and MS SQL Server

Thanks for your patience while we continue to tighten things up for the impending 1.0 release.  I recognize that changing the artifact id, the repository, etc may be a little frustrating.  And we're undergoing some maven infrastructure changes at Carbon Five (We've moved to Nexus if you're curious... so far so good).  The good news is the dust is settling and it should be smoother sailing moving forward.

## v0.9.4 on 2008-11-05 ##
  * [#1](http://code.google.com/p/c5-db-migration/issues/detail?id=1): Allow for single line comment character '#' (thanks joe.trewin)
  * [#8](http://code.google.com/p/c5-db-migration/issues/detail?id=8): Added support for oracle database type and driver class determination (thanks zregvart!)
  * Added (optional) maven plugin configuration option "databaseType"

## v0.9.3 on 2008-10-20 ##
  * Added db-migration:new goal for creating new migrations scripts (-Dname=... to specify a script name)
  * Added support for HSQL database
  * "migrationsPath" no longer needs a filename wildcard at the end (it's added automatically)
  * Bugs fixed: [5](http://code.google.com/p/c5-db-migration/issues/detail?id=5)
  * Introduced database type for handling specific features (nuances) of different databases

## v0.9.2 on 2008-09-25 ##

  * MS SQL Server is now supported (tested against SQL Server 2000)
  * Migrations can be written in [Groovy](http://groovy.codehaus.org/) (use the same naming with the extension .groovy) and use the jdbc connection that's made available to the script by the name `connection`
  * Added more robust testing for MySQL, PostgreSQL, and MS SQL Server 2000
  * Maven plugin behaves better with multi-module projects

## v0.9.1 on 2008-08-29 ##

A number of changes were made that break backwards compatibility.  We try to keep things stable but had to make a few fundamental improvements before deeming this framework worthy of a 1.0 status.  The framework is now stabilizing and we don't expect as many sweeping changes before 1.0.

### New Features and Improvements (_Italicized_ changes are not backwards compatible) ###

  * _Removed explicit support for "environments" as maven profiles provide a more flexible and robust solution._
  * _Version history table schema has changed and now all applied migrations are captured (not just the last version)._
  * Timestamps are now the recommended version string (instead of 001, 002, etc).
  * Migrations don't necessarily have to run in strict order; all pending migrations are run in order however.  This in conjunction with timestamp versions prevent developers from stepping on each others migrations.
  * _Renamed artifact ids:_
    * maven-migration-plugin -> maven-db-migration-plugin
    * migration -> db-migration
    * db-support -> db-support-parent
  * _Lots of shifting code around between modules and packages._
  * Added `db-migration:create`, `drop`, and `reset` for MySQL and PostgreSQL.
  * Enhanced `db-migration:validate` to display pending migrations.

### Bugfixes ###

  * ScriptRunner failed when displaying update results (thanks Doug!)

### Upgrading ###

Here is a high-level strategy for upgrading from previous versions.

  1. _**(maven plugin)**_ Update the plugin artifactId to maven-db-migration-plugin.
  1. _**(maven plugin)**_ Update the maven plugin configuration: version and replace environments with [build profiles](MavenPlugin#Handling_Multiple_Environments.md).
  1. _**(app-embedded)**_ Update the framework artifactId to db-migration (if you reference it).
  1. The default migration location has changed, **either**:
    * Move your migrations to the new default location (src/main/db/migrations).  If you embedded the migration manager, you must locate your migrations on the classpath.
    * Configure their location using `<migrationsPath>file:src/main/resources/db/migrations/*.sql</migrationsPath>`.
  1. _**(optional)**_ Rename your migrations to use the new timestamp format.
  1. Update your version history, by **either**:
    * Drop your database, create a new one and then migrate.  Of course this is difficult in production.
    * Manually change the schema\_version table to match a table created by `create table version_schema (version varchar(32) not null unique, applied_on timestamp not null, duration int not null)` and add a row for each already applied migration.

## v0.9.0 and older ##

First "releases" of the Carbon Five Database Migration Tools.