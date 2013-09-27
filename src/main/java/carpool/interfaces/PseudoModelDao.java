package carpool.interfaces;

import java.sql.SQLException;
import java.util.ArrayList;

import carpool.exception.PseudoException;

public interface PseudoModelDao {
	
	public ArrayList<PseudoModel> getAll();
	
	public PseudoModel getById() throws PseudoException, SQLException;
	
	public PseudoModel create(PseudoModel c) throws PseudoException, SQLException;
	
	public PseudoModel update(PseudoModel u) throws PseudoException, SQLException;
	
	public PseudoModel deleteById(int id) throws PseudoException, SQLException;
	
	public void deleteAll();
	

}
