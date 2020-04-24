package io.appgal.cloud.customer;

import io.appgal.cloud.model.Profile;
import io.appgal.cloud.model.SourceOrg;
import io.appgal.cloud.persistence.MongoDBJsonStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class CustomerService {
    private static Logger logger = LoggerFactory.getLogger(CustomerService.class);

    @Inject
    private MongoDBJsonStore mongoDBJsonStore;

    public SourceOrg getSourceOrg(String sourceOrgId)
    {
        //SourceOrg sourceOrg = this.mongoDBJsonStore.getSourceOrg();
        //return sourceOrg;
        return null;
    }

    public void storeSourceOrg(SourceOrg sourceOrg)
    {
        this.mongoDBJsonStore.storeSourceOrg(sourceOrg);
    }
}
