/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.db.vendor.oracle.result.statement;

import org.mule.module.db.domain.autogeneratedkey.AutoGeneratedKeyStrategy;
import org.mule.module.db.domain.query.QueryTemplate;
import org.mule.module.db.domain.connection.DbConnection;
import org.mule.module.db.result.statement.GenericStatementResultIteratorFactory;
import org.mule.module.db.result.resultset.ResultSetHandler;
import org.mule.module.db.result.statement.StatementResultIterator;

import java.sql.Statement;

/**
 * Creates {@link OracleStatementResultIterator} instances
 */
public class OracleStatementResultIteratorFactory extends GenericStatementResultIteratorFactory
{

    public OracleStatementResultIteratorFactory(ResultSetHandler resultSetHandler)
    {
        super(resultSetHandler);
    }

    @Override
    protected StatementResultIterator doCreateStatementResultIterator(DbConnection connection, Statement statement, QueryTemplate queryTemplate, AutoGeneratedKeyStrategy autoGeneratedKeyStrategy, ResultSetHandler resultSetHandler)
    {
        return new OracleStatementResultIterator(connection, statement, queryTemplate, autoGeneratedKeyStrategy, resultSetHandler);
    }

}