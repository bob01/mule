/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.runtime.module.db.integration.storedprocedure;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeThat;
import static org.mule.runtime.module.db.integration.TestRecordUtil.assertRecords;
import static org.mule.runtime.module.db.integration.TestRecordUtil.getVenusRecord;
import org.mule.runtime.core.api.MuleEvent;
import org.mule.runtime.core.api.MuleMessage;
import org.mule.runtime.module.db.integration.AbstractDbIntegrationTestCase;
import org.mule.runtime.module.db.integration.TestDbConfig;
import org.mule.runtime.module.db.integration.matcher.SupportsStoredFunctionsUsingCallSyntax;
import org.mule.runtime.module.db.integration.model.AbstractTestDatabase;
import org.mule.runtime.module.db.integration.model.MySqlTestDatabase;

import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.Parameterized;

@Ignore("DB vendors/drivers do not use maxRows as described in Statement's javadoc")
public class StoreProcedureMaxRowsTestCase extends AbstractDbIntegrationTestCase
{

    public StoreProcedureMaxRowsTestCase(String dataSourceConfigResource, AbstractTestDatabase testDatabase)
    {
        super(dataSourceConfigResource, testDatabase);
    }

    @Parameterized.Parameters
    public static List<Object[]> parameters()
    {
        return TestDbConfig.getResources();
    }

    @Override
    protected String[] getFlowConfigurationResources()
    {
        return new String[] {"integration/storedprocedure/stored-procedure-max-rows-config.xml"};
    }

    @Test
    public void testRequestResponse() throws Exception
    {
        final MuleEvent responseEvent = flowRunner("defaultQueryRequestResponse").withPayload(TEST_MESSAGE).run();

        final MuleMessage response = responseEvent.getMessage();
        Map payload = (Map) response.getPayload();
        if (testDatabase instanceof MySqlTestDatabase)
        {
            assertThat(payload.size(), equalTo(2));
            assertThat((Integer) payload.get("updateCount1"), equalTo(0));
        }
        else
        {
            assertThat(payload.size(), equalTo(1));
        }

        assertRecords(payload.get("resultSet1"), getVenusRecord());
    }

    @Before
    public void setupStoredProcedure() throws Exception
    {
        assumeThat(getDefaultDataSource(), new SupportsStoredFunctionsUsingCallSyntax());
        testDatabase.createStoredProcedureGetRecords(getDefaultDataSource());
    }
}
