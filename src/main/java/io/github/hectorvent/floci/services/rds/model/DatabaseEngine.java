package io.github.hectorvent.floci.services.rds.model;

import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.Locale;

@RegisterForReflection
public enum DatabaseEngine {
    POSTGRES, MYSQL, MARIADB,
    // SQL Server editions are METADATA-ONLY engines: floci cannot run a SQL
    // Server container, so instances/clusters with these engines can only be
    // created when FLOCI_SERVICES_RDS_MOCK=true. They exist so control planes
    // that classify RDS estates by engine string (e.g. "sqlserver-se") can be
    // exercised against floci without a real database.
    SQLSERVER_EE, SQLSERVER_SE, SQLSERVER_EX, SQLSERVER_WEB;

    public int defaultPort() {
        return switch (this) {
            case POSTGRES -> 5432;
            case MYSQL, MARIADB -> 3306;
            case SQLSERVER_EE, SQLSERVER_SE, SQLSERVER_EX, SQLSERVER_WEB -> 1433;
        };
    }

    /**
     * The AWS engine string for API responses ("postgres", "sqlserver-se", ...).
     * Identical to {@code name().toLowerCase()} for single-word engines; SQL
     * Server editions map their underscore to the AWS hyphen so the engine a
     * caller created with round-trips verbatim through Describe*.
     */
    public String awsName() {
        return name().toLowerCase(Locale.ROOT).replace('_', '-');
    }

    /** True for the metadata-only SQL Server editions (no runnable container). */
    public boolean isSqlServer() {
        return this == SQLSERVER_EE || this == SQLSERVER_SE
                || this == SQLSERVER_EX || this == SQLSERVER_WEB;
    }
}
