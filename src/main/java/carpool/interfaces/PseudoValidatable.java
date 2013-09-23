package carpool.interfaces;

import carpool.exception.ValidationException;

public interface PseudoValidatable {
	
	public boolean validate() throws ValidationException;

}
