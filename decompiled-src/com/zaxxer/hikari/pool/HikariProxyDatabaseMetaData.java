package com.zaxxer.hikari.pool;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.RowIdLifetime;
import java.sql.SQLException;
import java.sql.Wrapper;

public final class HikariProxyDatabaseMetaData extends ProxyDatabaseMetaData implements Wrapper, DatabaseMetaData {

    public boolean isWrapperFor(Class oclass) throws SQLException {
        try {
            return super.delegate.isWrapperFor(oclass);
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public boolean allProceduresAreCallable() throws SQLException {
        try {
            return super.delegate.allProceduresAreCallable();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public boolean allTablesAreSelectable() throws SQLException {
        try {
            return super.delegate.allTablesAreSelectable();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public String getURL() throws SQLException {
        try {
            return super.delegate.getURL();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public String getUserName() throws SQLException {
        try {
            return super.delegate.getUserName();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public boolean isReadOnly() throws SQLException {
        try {
            return super.delegate.isReadOnly();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public boolean nullsAreSortedHigh() throws SQLException {
        try {
            return super.delegate.nullsAreSortedHigh();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public boolean nullsAreSortedLow() throws SQLException {
        try {
            return super.delegate.nullsAreSortedLow();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public boolean nullsAreSortedAtStart() throws SQLException {
        try {
            return super.delegate.nullsAreSortedAtStart();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public boolean nullsAreSortedAtEnd() throws SQLException {
        try {
            return super.delegate.nullsAreSortedAtEnd();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public String getDatabaseProductName() throws SQLException {
        try {
            return super.delegate.getDatabaseProductName();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public String getDatabaseProductVersion() throws SQLException {
        try {
            return super.delegate.getDatabaseProductVersion();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public String getDriverName() throws SQLException {
        try {
            return super.delegate.getDriverName();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public String getDriverVersion() throws SQLException {
        try {
            return super.delegate.getDriverVersion();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public int getDriverMajorVersion() {
        return ((DatabaseMetaData) super.delegate).getDriverMajorVersion();
    }

    public int getDriverMinorVersion() {
        return ((DatabaseMetaData) super.delegate).getDriverMinorVersion();
    }

    public boolean usesLocalFiles() throws SQLException {
        try {
            return super.delegate.usesLocalFiles();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public boolean usesLocalFilePerTable() throws SQLException {
        try {
            return super.delegate.usesLocalFilePerTable();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public boolean supportsMixedCaseIdentifiers() throws SQLException {
        try {
            return super.delegate.supportsMixedCaseIdentifiers();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public boolean storesUpperCaseIdentifiers() throws SQLException {
        try {
            return super.delegate.storesUpperCaseIdentifiers();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public boolean storesLowerCaseIdentifiers() throws SQLException {
        try {
            return super.delegate.storesLowerCaseIdentifiers();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public boolean storesMixedCaseIdentifiers() throws SQLException {
        try {
            return super.delegate.storesMixedCaseIdentifiers();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public boolean supportsMixedCaseQuotedIdentifiers() throws SQLException {
        try {
            return super.delegate.supportsMixedCaseQuotedIdentifiers();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public boolean storesUpperCaseQuotedIdentifiers() throws SQLException {
        try {
            return super.delegate.storesUpperCaseQuotedIdentifiers();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public boolean storesLowerCaseQuotedIdentifiers() throws SQLException {
        try {
            return super.delegate.storesLowerCaseQuotedIdentifiers();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public boolean storesMixedCaseQuotedIdentifiers() throws SQLException {
        try {
            return super.delegate.storesMixedCaseQuotedIdentifiers();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public String getIdentifierQuoteString() throws SQLException {
        try {
            return super.delegate.getIdentifierQuoteString();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public String getSQLKeywords() throws SQLException {
        try {
            return super.delegate.getSQLKeywords();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public String getNumericFunctions() throws SQLException {
        try {
            return super.delegate.getNumericFunctions();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public String getStringFunctions() throws SQLException {
        try {
            return super.delegate.getStringFunctions();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public String getSystemFunctions() throws SQLException {
        try {
            return super.delegate.getSystemFunctions();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public String getTimeDateFunctions() throws SQLException {
        try {
            return super.delegate.getTimeDateFunctions();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public String getSearchStringEscape() throws SQLException {
        try {
            return super.delegate.getSearchStringEscape();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public String getExtraNameCharacters() throws SQLException {
        try {
            return super.delegate.getExtraNameCharacters();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public boolean supportsAlterTableWithAddColumn() throws SQLException {
        try {
            return super.delegate.supportsAlterTableWithAddColumn();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public boolean supportsAlterTableWithDropColumn() throws SQLException {
        try {
            return super.delegate.supportsAlterTableWithDropColumn();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public boolean supportsColumnAliasing() throws SQLException {
        try {
            return super.delegate.supportsColumnAliasing();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public boolean nullPlusNonNullIsNull() throws SQLException {
        try {
            return super.delegate.nullPlusNonNullIsNull();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public boolean supportsConvert() throws SQLException {
        try {
            return super.delegate.supportsConvert();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public boolean supportsConvert(int i, int j) throws SQLException {
        try {
            return super.delegate.supportsConvert(i, j);
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public boolean supportsTableCorrelationNames() throws SQLException {
        try {
            return super.delegate.supportsTableCorrelationNames();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public boolean supportsDifferentTableCorrelationNames() throws SQLException {
        try {
            return super.delegate.supportsDifferentTableCorrelationNames();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public boolean supportsExpressionsInOrderBy() throws SQLException {
        try {
            return super.delegate.supportsExpressionsInOrderBy();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public boolean supportsOrderByUnrelated() throws SQLException {
        try {
            return super.delegate.supportsOrderByUnrelated();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public boolean supportsGroupBy() throws SQLException {
        try {
            return super.delegate.supportsGroupBy();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public boolean supportsGroupByUnrelated() throws SQLException {
        try {
            return super.delegate.supportsGroupByUnrelated();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public boolean supportsGroupByBeyondSelect() throws SQLException {
        try {
            return super.delegate.supportsGroupByBeyondSelect();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public boolean supportsLikeEscapeClause() throws SQLException {
        try {
            return super.delegate.supportsLikeEscapeClause();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public boolean supportsMultipleResultSets() throws SQLException {
        try {
            return super.delegate.supportsMultipleResultSets();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public boolean supportsMultipleTransactions() throws SQLException {
        try {
            return super.delegate.supportsMultipleTransactions();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public boolean supportsNonNullableColumns() throws SQLException {
        try {
            return super.delegate.supportsNonNullableColumns();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public boolean supportsMinimumSQLGrammar() throws SQLException {
        try {
            return super.delegate.supportsMinimumSQLGrammar();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public boolean supportsCoreSQLGrammar() throws SQLException {
        try {
            return super.delegate.supportsCoreSQLGrammar();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public boolean supportsExtendedSQLGrammar() throws SQLException {
        try {
            return super.delegate.supportsExtendedSQLGrammar();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public boolean supportsANSI92EntryLevelSQL() throws SQLException {
        try {
            return super.delegate.supportsANSI92EntryLevelSQL();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public boolean supportsANSI92IntermediateSQL() throws SQLException {
        try {
            return super.delegate.supportsANSI92IntermediateSQL();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public boolean supportsANSI92FullSQL() throws SQLException {
        try {
            return super.delegate.supportsANSI92FullSQL();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public boolean supportsIntegrityEnhancementFacility() throws SQLException {
        try {
            return super.delegate.supportsIntegrityEnhancementFacility();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public boolean supportsOuterJoins() throws SQLException {
        try {
            return super.delegate.supportsOuterJoins();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public boolean supportsFullOuterJoins() throws SQLException {
        try {
            return super.delegate.supportsFullOuterJoins();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public boolean supportsLimitedOuterJoins() throws SQLException {
        try {
            return super.delegate.supportsLimitedOuterJoins();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public String getSchemaTerm() throws SQLException {
        try {
            return super.delegate.getSchemaTerm();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public String getProcedureTerm() throws SQLException {
        try {
            return super.delegate.getProcedureTerm();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public String getCatalogTerm() throws SQLException {
        try {
            return super.delegate.getCatalogTerm();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public boolean isCatalogAtStart() throws SQLException {
        try {
            return super.delegate.isCatalogAtStart();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public String getCatalogSeparator() throws SQLException {
        try {
            return super.delegate.getCatalogSeparator();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public boolean supportsSchemasInDataManipulation() throws SQLException {
        try {
            return super.delegate.supportsSchemasInDataManipulation();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public boolean supportsSchemasInProcedureCalls() throws SQLException {
        try {
            return super.delegate.supportsSchemasInProcedureCalls();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public boolean supportsSchemasInTableDefinitions() throws SQLException {
        try {
            return super.delegate.supportsSchemasInTableDefinitions();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public boolean supportsSchemasInIndexDefinitions() throws SQLException {
        try {
            return super.delegate.supportsSchemasInIndexDefinitions();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public boolean supportsSchemasInPrivilegeDefinitions() throws SQLException {
        try {
            return super.delegate.supportsSchemasInPrivilegeDefinitions();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public boolean supportsCatalogsInDataManipulation() throws SQLException {
        try {
            return super.delegate.supportsCatalogsInDataManipulation();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public boolean supportsCatalogsInProcedureCalls() throws SQLException {
        try {
            return super.delegate.supportsCatalogsInProcedureCalls();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public boolean supportsCatalogsInTableDefinitions() throws SQLException {
        try {
            return super.delegate.supportsCatalogsInTableDefinitions();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public boolean supportsCatalogsInIndexDefinitions() throws SQLException {
        try {
            return super.delegate.supportsCatalogsInIndexDefinitions();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public boolean supportsCatalogsInPrivilegeDefinitions() throws SQLException {
        try {
            return super.delegate.supportsCatalogsInPrivilegeDefinitions();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public boolean supportsPositionedDelete() throws SQLException {
        try {
            return super.delegate.supportsPositionedDelete();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public boolean supportsPositionedUpdate() throws SQLException {
        try {
            return super.delegate.supportsPositionedUpdate();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public boolean supportsSelectForUpdate() throws SQLException {
        try {
            return super.delegate.supportsSelectForUpdate();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public boolean supportsStoredProcedures() throws SQLException {
        try {
            return super.delegate.supportsStoredProcedures();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public boolean supportsSubqueriesInComparisons() throws SQLException {
        try {
            return super.delegate.supportsSubqueriesInComparisons();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public boolean supportsSubqueriesInExists() throws SQLException {
        try {
            return super.delegate.supportsSubqueriesInExists();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public boolean supportsSubqueriesInIns() throws SQLException {
        try {
            return super.delegate.supportsSubqueriesInIns();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public boolean supportsSubqueriesInQuantifieds() throws SQLException {
        try {
            return super.delegate.supportsSubqueriesInQuantifieds();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public boolean supportsCorrelatedSubqueries() throws SQLException {
        try {
            return super.delegate.supportsCorrelatedSubqueries();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public boolean supportsUnion() throws SQLException {
        try {
            return super.delegate.supportsUnion();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public boolean supportsUnionAll() throws SQLException {
        try {
            return super.delegate.supportsUnionAll();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public boolean supportsOpenCursorsAcrossCommit() throws SQLException {
        try {
            return super.delegate.supportsOpenCursorsAcrossCommit();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public boolean supportsOpenCursorsAcrossRollback() throws SQLException {
        try {
            return super.delegate.supportsOpenCursorsAcrossRollback();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public boolean supportsOpenStatementsAcrossCommit() throws SQLException {
        try {
            return super.delegate.supportsOpenStatementsAcrossCommit();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public boolean supportsOpenStatementsAcrossRollback() throws SQLException {
        try {
            return super.delegate.supportsOpenStatementsAcrossRollback();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public int getMaxBinaryLiteralLength() throws SQLException {
        try {
            return super.delegate.getMaxBinaryLiteralLength();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public int getMaxCharLiteralLength() throws SQLException {
        try {
            return super.delegate.getMaxCharLiteralLength();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public int getMaxColumnNameLength() throws SQLException {
        try {
            return super.delegate.getMaxColumnNameLength();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public int getMaxColumnsInGroupBy() throws SQLException {
        try {
            return super.delegate.getMaxColumnsInGroupBy();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public int getMaxColumnsInIndex() throws SQLException {
        try {
            return super.delegate.getMaxColumnsInIndex();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public int getMaxColumnsInOrderBy() throws SQLException {
        try {
            return super.delegate.getMaxColumnsInOrderBy();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public int getMaxColumnsInSelect() throws SQLException {
        try {
            return super.delegate.getMaxColumnsInSelect();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public int getMaxColumnsInTable() throws SQLException {
        try {
            return super.delegate.getMaxColumnsInTable();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public int getMaxConnections() throws SQLException {
        try {
            return super.delegate.getMaxConnections();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public int getMaxCursorNameLength() throws SQLException {
        try {
            return super.delegate.getMaxCursorNameLength();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public int getMaxIndexLength() throws SQLException {
        try {
            return super.delegate.getMaxIndexLength();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public int getMaxSchemaNameLength() throws SQLException {
        try {
            return super.delegate.getMaxSchemaNameLength();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public int getMaxProcedureNameLength() throws SQLException {
        try {
            return super.delegate.getMaxProcedureNameLength();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public int getMaxCatalogNameLength() throws SQLException {
        try {
            return super.delegate.getMaxCatalogNameLength();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public int getMaxRowSize() throws SQLException {
        try {
            return super.delegate.getMaxRowSize();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public boolean doesMaxRowSizeIncludeBlobs() throws SQLException {
        try {
            return super.delegate.doesMaxRowSizeIncludeBlobs();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public int getMaxStatementLength() throws SQLException {
        try {
            return super.delegate.getMaxStatementLength();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public int getMaxStatements() throws SQLException {
        try {
            return super.delegate.getMaxStatements();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public int getMaxTableNameLength() throws SQLException {
        try {
            return super.delegate.getMaxTableNameLength();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public int getMaxTablesInSelect() throws SQLException {
        try {
            return super.delegate.getMaxTablesInSelect();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public int getMaxUserNameLength() throws SQLException {
        try {
            return super.delegate.getMaxUserNameLength();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public int getDefaultTransactionIsolation() throws SQLException {
        try {
            return super.delegate.getDefaultTransactionIsolation();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public boolean supportsTransactions() throws SQLException {
        try {
            return super.delegate.supportsTransactions();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public boolean supportsTransactionIsolationLevel(int i) throws SQLException {
        try {
            return super.delegate.supportsTransactionIsolationLevel(i);
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public boolean supportsDataDefinitionAndDataManipulationTransactions() throws SQLException {
        try {
            return super.delegate.supportsDataDefinitionAndDataManipulationTransactions();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public boolean supportsDataManipulationTransactionsOnly() throws SQLException {
        try {
            return super.delegate.supportsDataManipulationTransactionsOnly();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public boolean dataDefinitionCausesTransactionCommit() throws SQLException {
        try {
            return super.delegate.dataDefinitionCausesTransactionCommit();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public boolean dataDefinitionIgnoredInTransactions() throws SQLException {
        try {
            return super.delegate.dataDefinitionIgnoredInTransactions();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public ResultSet getProcedures(String s, String s1, String s2) throws SQLException {
        try {
            return super.getProcedures(s, s1, s2);
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public ResultSet getProcedureColumns(String s, String s1, String s2, String s3) throws SQLException {
        try {
            return super.getProcedureColumns(s, s1, s2, s3);
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public ResultSet getTables(String s, String s1, String s2, String[] astring) throws SQLException {
        try {
            return super.getTables(s, s1, s2, astring);
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public ResultSet getSchemas() throws SQLException {
        try {
            return super.getSchemas();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public ResultSet getCatalogs() throws SQLException {
        try {
            return super.getCatalogs();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public ResultSet getTableTypes() throws SQLException {
        try {
            return super.getTableTypes();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public ResultSet getColumns(String s, String s1, String s2, String s3) throws SQLException {
        try {
            return super.getColumns(s, s1, s2, s3);
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public ResultSet getColumnPrivileges(String s, String s1, String s2, String s3) throws SQLException {
        try {
            return super.getColumnPrivileges(s, s1, s2, s3);
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public ResultSet getTablePrivileges(String s, String s1, String s2) throws SQLException {
        try {
            return super.getTablePrivileges(s, s1, s2);
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public ResultSet getBestRowIdentifier(String s, String s1, String s2, int i, boolean flag) throws SQLException {
        try {
            return super.getBestRowIdentifier(s, s1, s2, i, flag);
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public ResultSet getVersionColumns(String s, String s1, String s2) throws SQLException {
        try {
            return super.getVersionColumns(s, s1, s2);
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public ResultSet getPrimaryKeys(String s, String s1, String s2) throws SQLException {
        try {
            return super.getPrimaryKeys(s, s1, s2);
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public ResultSet getImportedKeys(String s, String s1, String s2) throws SQLException {
        try {
            return super.getImportedKeys(s, s1, s2);
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public ResultSet getExportedKeys(String s, String s1, String s2) throws SQLException {
        try {
            return super.getExportedKeys(s, s1, s2);
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public ResultSet getCrossReference(String s, String s1, String s2, String s3, String s4, String s5) throws SQLException {
        try {
            return super.getCrossReference(s, s1, s2, s3, s4, s5);
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public ResultSet getTypeInfo() throws SQLException {
        try {
            return super.getTypeInfo();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public ResultSet getIndexInfo(String s, String s1, String s2, boolean flag, boolean flag1) throws SQLException {
        try {
            return super.getIndexInfo(s, s1, s2, flag, flag1);
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public boolean supportsResultSetType(int i) throws SQLException {
        try {
            return super.delegate.supportsResultSetType(i);
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public boolean supportsResultSetConcurrency(int i, int j) throws SQLException {
        try {
            return super.delegate.supportsResultSetConcurrency(i, j);
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public boolean ownUpdatesAreVisible(int i) throws SQLException {
        try {
            return super.delegate.ownUpdatesAreVisible(i);
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public boolean ownDeletesAreVisible(int i) throws SQLException {
        try {
            return super.delegate.ownDeletesAreVisible(i);
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public boolean ownInsertsAreVisible(int i) throws SQLException {
        try {
            return super.delegate.ownInsertsAreVisible(i);
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public boolean othersUpdatesAreVisible(int i) throws SQLException {
        try {
            return super.delegate.othersUpdatesAreVisible(i);
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public boolean othersDeletesAreVisible(int i) throws SQLException {
        try {
            return super.delegate.othersDeletesAreVisible(i);
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public boolean othersInsertsAreVisible(int i) throws SQLException {
        try {
            return super.delegate.othersInsertsAreVisible(i);
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public boolean updatesAreDetected(int i) throws SQLException {
        try {
            return super.delegate.updatesAreDetected(i);
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public boolean deletesAreDetected(int i) throws SQLException {
        try {
            return super.delegate.deletesAreDetected(i);
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public boolean insertsAreDetected(int i) throws SQLException {
        try {
            return super.delegate.insertsAreDetected(i);
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public boolean supportsBatchUpdates() throws SQLException {
        try {
            return super.delegate.supportsBatchUpdates();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public ResultSet getUDTs(String s, String s1, String s2, int[] aint) throws SQLException {
        try {
            return super.getUDTs(s, s1, s2, aint);
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public boolean supportsSavepoints() throws SQLException {
        try {
            return super.delegate.supportsSavepoints();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public boolean supportsNamedParameters() throws SQLException {
        try {
            return super.delegate.supportsNamedParameters();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public boolean supportsMultipleOpenResults() throws SQLException {
        try {
            return super.delegate.supportsMultipleOpenResults();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public boolean supportsGetGeneratedKeys() throws SQLException {
        try {
            return super.delegate.supportsGetGeneratedKeys();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public ResultSet getSuperTypes(String s, String s1, String s2) throws SQLException {
        try {
            return super.getSuperTypes(s, s1, s2);
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public ResultSet getSuperTables(String s, String s1, String s2) throws SQLException {
        try {
            return super.getSuperTables(s, s1, s2);
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public ResultSet getAttributes(String s, String s1, String s2, String s3) throws SQLException {
        try {
            return super.getAttributes(s, s1, s2, s3);
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public boolean supportsResultSetHoldability(int i) throws SQLException {
        try {
            return super.delegate.supportsResultSetHoldability(i);
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public int getResultSetHoldability() throws SQLException {
        try {
            return super.delegate.getResultSetHoldability();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public int getDatabaseMajorVersion() throws SQLException {
        try {
            return super.delegate.getDatabaseMajorVersion();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public int getDatabaseMinorVersion() throws SQLException {
        try {
            return super.delegate.getDatabaseMinorVersion();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public int getJDBCMajorVersion() throws SQLException {
        try {
            return super.delegate.getJDBCMajorVersion();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public int getJDBCMinorVersion() throws SQLException {
        try {
            return super.delegate.getJDBCMinorVersion();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public int getSQLStateType() throws SQLException {
        try {
            return super.delegate.getSQLStateType();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public boolean locatorsUpdateCopy() throws SQLException {
        try {
            return super.delegate.locatorsUpdateCopy();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public boolean supportsStatementPooling() throws SQLException {
        try {
            return super.delegate.supportsStatementPooling();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public RowIdLifetime getRowIdLifetime() throws SQLException {
        try {
            return super.delegate.getRowIdLifetime();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public ResultSet getSchemas(String s, String s1) throws SQLException {
        try {
            return super.getSchemas(s, s1);
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public boolean supportsStoredFunctionsUsingCallSyntax() throws SQLException {
        try {
            return super.delegate.supportsStoredFunctionsUsingCallSyntax();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public boolean autoCommitFailureClosesAllResultSets() throws SQLException {
        try {
            return super.delegate.autoCommitFailureClosesAllResultSets();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public ResultSet getClientInfoProperties() throws SQLException {
        try {
            return super.getClientInfoProperties();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public ResultSet getFunctions(String s, String s1, String s2) throws SQLException {
        try {
            return super.getFunctions(s, s1, s2);
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public ResultSet getFunctionColumns(String s, String s1, String s2, String s3) throws SQLException {
        try {
            return super.getFunctionColumns(s, s1, s2, s3);
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public ResultSet getPseudoColumns(String s, String s1, String s2, String s3) throws SQLException {
        try {
            return super.getPseudoColumns(s, s1, s2, s3);
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public boolean generatedKeyAlwaysReturned() throws SQLException {
        try {
            return super.delegate.generatedKeyAlwaysReturned();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public long getMaxLogicalLobSize() throws SQLException {
        try {
            return super.delegate.getMaxLogicalLobSize();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public boolean supportsRefCursors() throws SQLException {
        try {
            return super.delegate.supportsRefCursors();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    public boolean supportsSharding() throws SQLException {
        try {
            return super.delegate.supportsSharding();
        } catch (SQLException sqlexception) {
            throw ((ProxyDatabaseMetaData) this).checkException(sqlexception);
        }
    }

    HikariProxyDatabaseMetaData(ProxyConnection proxyconnection, DatabaseMetaData databasemetadata) {
        super(proxyconnection, databasemetadata);
    }
}
