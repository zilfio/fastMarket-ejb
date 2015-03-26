package it.univaq.mwt.fastmarket.business;

import it.univaq.mwt.fastmarket.business.model.NonGrocery;

import javax.ejb.Remote;

@Remote
public interface NonGroceriesService {

	ResponseGrid<NonGrocery> findAllNonGroceriesPaginated(RequestGrid requestGrid) throws BusinessException;
	
	void create(NonGrocery nonGrocery) throws BusinessException;
	
	NonGrocery findNonGroceryByID(Long id) throws BusinessException;
	
	void update(NonGrocery nonGrocery) throws BusinessException;
	
	void delete(NonGrocery nonGrocery) throws BusinessException;
	
}
