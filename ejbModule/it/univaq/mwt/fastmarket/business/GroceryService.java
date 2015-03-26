package it.univaq.mwt.fastmarket.business;

import it.univaq.mwt.fastmarket.business.model.Grocery;

import javax.ejb.Remote;

@Remote
public interface GroceryService {

	ResponseGrid<Grocery> findAllGroceriesPaginated(RequestGrid requestGrid) throws BusinessException;
	
	void create(Grocery grocery) throws BusinessException;
	
	Grocery findGroceryByID(Long id) throws BusinessException;
	
	void update(Grocery grocery) throws BusinessException;
	
	void delete(Grocery grocery) throws BusinessException;
	
}
