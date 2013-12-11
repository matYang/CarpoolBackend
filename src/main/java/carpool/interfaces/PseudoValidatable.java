package carpool.interfaces;

import carpool.exception.validation.ValidationException;

public interface PseudoValidatable {
	
	public boolean validate() throws ValidationException;

}
