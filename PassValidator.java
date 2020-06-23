package clubHub;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PassValidator implements ConstraintValidator<Pass, String> {

	@Override
	public void initialize(Pass pass) {
	}
	
	@Override
		public boolean isValid(String input, ConstraintValidatorContext cxt) {
			if(input == null) {
				return false;
			}
			return input.matches("[0-9]*");
		}
	
}
