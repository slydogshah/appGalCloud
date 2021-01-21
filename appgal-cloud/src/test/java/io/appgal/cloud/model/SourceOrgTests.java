package io.appgal.cloud.model;

import io.bugsbunny.test.components.BaseTest;
import io.bugsbunny.test.components.MockData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class SourceOrgTests  {
    private static Logger logger = LoggerFactory.getLogger(SourceOrgTests.class);

    @Test
    public void testEquals()
    {
        SourceOrg sourceOrg1 = MockData.mockProducerOrg();
        SourceOrg sourceOrg2 = MockData.mockReceiverOrg();

        assertFalse(sourceOrg1.equals(sourceOrg2));
    }
}
