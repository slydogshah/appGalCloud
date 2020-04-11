package io.appgal.cloud.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class SourceOrgTests {
    private static Logger logger = LoggerFactory.getLogger(SourceOrgTests.class);

    @Test
    public void testEquals()
    {
        SourceOrg sourceOrg1 = new SourceOrg("microsoft", "Microsoft", "melinda_gates@microsoft.com");
        SourceOrg sourceOrg2 = new SourceOrg("apple", "Apple", "tim_cook@apple.com");

        assertFalse(sourceOrg1.equals(sourceOrg2));
    }
}
