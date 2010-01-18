package com.carbonfive.db.migration;

import static org.hamcrest.core.Is.*;
import static org.junit.Assert.*;
import org.junit.*;
import org.springframework.jdbc.core.simple.*;

import javax.sql.*;

public class DataSourceMigrationManagerTest
{
    static final String SINGLE = "classpath:/test_migrations/valid_1/";
    static final String MULTIPLE = "classpath:/test_migrations/valid_2/";
    static final String DUPLICATE = "classpath:/test_migrations/duplicate_1/";
    static final String BAD_SCRIPT = "classpath:/test_migrations/bad_script_1/";

    private DataSourceMigrationManager migrationManager;
    private SimpleJdbcTemplate jdbcTemplate;

    @Before
    public void setup()
    {
        DataSource dataSource = DatabaseTestUtils.createUniqueDataSource();
        migrationManager = new DataSourceMigrationManager(dataSource);

        jdbcTemplate = new SimpleJdbcTemplate(dataSource);
    }

    @Test
    public void enableMigrationsShouldCreateSchemaTrackingTable()
    {
        migrationManager.enableMigrations();
        jdbcTemplate.queryForInt("select count(*) from schema_version"); // Throws exception if table doesn't exist.
    }

    @Test
    public void migrateShouldApplyOneMigration()
    {
        migrationManager.setMigrationResolver(new ResourceMigrationResolver(ResourceMigrationResolverTest.SINGLE));

        migrationManager.migrate();
        assertThat(migrationManager.validate(), is(true));

        assertThat(jdbcTemplate.queryForInt("select count(*) from users"), is(3));
    }

    @Test
    public void pendingMigrationsShouldReturnOneMigration()
    {
        migrationManager.setMigrationResolver(new ResourceMigrationResolver(ResourceMigrationResolverTest.SINGLE));
        assertThat(migrationManager.pendingMigrations().size(), is(1));
    }

    @Test
    public void migrateShouldApplyMultipleMigrations()
    {
        migrationManager.setMigrationResolver(new ResourceMigrationResolver(ResourceMigrationResolverTest.MULTIPLE));

        migrationManager.migrate();
        assertThat(migrationManager.validate(), is(true));

        assertThat(jdbcTemplate.queryForInt("select count(*) from users"), is(3));
        assertThat(jdbcTemplate.queryForInt("select count(*) from trips"), is(3));
    }

    @Test
    public void pendingMigrationsShouldReturnMultipleMigration()
    {
        migrationManager.setMigrationResolver(new ResourceMigrationResolver(ResourceMigrationResolverTest.MULTIPLE));
        assertThat(migrationManager.pendingMigrations().size(), is(3));
    }

    @Test
    public void migrateShouldCatchDuplicateMigrationVersions()
    {
        migrationManager.setMigrationResolver(new ResourceMigrationResolver(DUPLICATE));

        try
        {
            migrationManager.migrate();
            fail("Should have thrown an exception reporting an incorrect number of migrations.");
        }
        catch (Exception e)
        {
        }
    }

    @Test
    public void migrateShouldHandleInvalidSql()
    {
        migrationManager.setMigrationResolver(new ResourceMigrationResolver(BAD_SCRIPT));

        try
        {
            migrationManager.migrate();
            fail("MigrateException should have been thrown as migration 3 fails.");
        }
        catch (MigrationException e)
        {
        }

        assertThat(jdbcTemplate.queryForInt("select count(*) from trips"), is(2));
        assertThat(migrationManager.validate(), is(false));
    }
}