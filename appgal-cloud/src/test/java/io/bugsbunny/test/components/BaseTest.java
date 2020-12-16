package io.bugsbunny.test.components;

import io.appgal.cloud.infrastructure.MongoDBJsonStore;
import org.junit.jupiter.api.BeforeEach;

import javax.inject.Inject;

public abstract class BaseTest
{
    @Inject
    private SecurityTokenMockComponent securityTokenMockComponent;

    @Inject
    private MongoDBJsonStore mongoDBJsonStore;

    @BeforeEach
    public void setUp() throws Exception
    {
        this.securityTokenMockComponent.start();
        //this.mongoDBJsonStore.cleanup();
    }
}
