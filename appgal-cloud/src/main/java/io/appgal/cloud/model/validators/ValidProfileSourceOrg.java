package io.appgal.cloud.model.validators;

import javax.validation.Constraint;
import javax.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ TYPE, ANNOTATION_TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = { ValidProfileSourceOrgValidator.class })
@Documented
public @interface ValidProfileSourceOrg {

    String message() default "organization_required";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
