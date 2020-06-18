package clubHub;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;

public @interface Pass {

@Documented
@Constraint(validatedBy = PassValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@ReportAsSingleViolation

public @interface Phone {

	String message() default "パスワードを設定してください";
	
	Class<?>[] groups() default {};
	
	Class<? extends Payload>[] payload() default{};
	
	boolean onlyNumber() default false;
}

}