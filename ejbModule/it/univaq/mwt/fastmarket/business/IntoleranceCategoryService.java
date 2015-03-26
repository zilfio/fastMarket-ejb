package it.univaq.mwt.fastmarket.business;

import it.univaq.mwt.fastmarket.business.model.IntoleranceCategory;

import java.util.Set;

import javax.ejb.Remote;

@Remote
public interface IntoleranceCategoryService {
	
	Set<IntoleranceCategory> getAllIntoleranceCategories() throws BusinessException;
	
	ResponseGrid<IntoleranceCategory> findAllIntoleranceCategoriesPaginated(RequestGrid requestGrid) throws BusinessException;
	
	void create(IntoleranceCategory intoleranceCategory) throws BusinessException;
	
	IntoleranceCategory findIntoleranceCategoryById(Long id) throws BusinessException;
	
	void update(IntoleranceCategory intoleranceCategory) throws BusinessException;
	
	void delete(IntoleranceCategory intoleranceCategory) throws BusinessException;
	
}
