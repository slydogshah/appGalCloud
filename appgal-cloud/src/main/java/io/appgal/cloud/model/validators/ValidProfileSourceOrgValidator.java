package io.appgal.cloud.model.validators;

import io.appgal.cloud.model.Profile;
import io.appgal.cloud.model.ProfileType;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidProfileSourceOrgValidator implements ConstraintValidator<ValidProfileSourceOrg, Profile>
{
    @Override
    public void initialize(ValidProfileSourceOrg constraintAnnotation) {

    }

    @Override
    public boolean isValid(Profile profile, ConstraintValidatorContext constraintValidatorContext) {
        if(profile.getProfileType() == ProfileType.ORG)
        {
            if(profile.getSourceOrgId()==null || profile.getSourceOrgId().length()==0)
            {
                return false;
            }
        }
        return true;
    }
}
