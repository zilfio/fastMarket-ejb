package it.univaq.mwt.fastmarket.business;

import it.univaq.mwt.fastmarket.business.model.Province;

import java.util.Set;

import javax.ejb.Remote;

@Remote
public interface ProvinceService {

	Set<Province> getAllProvinces() throws BusinessException;
	
	ResponseGrid<Province> findAllProvincesPaginated(RequestGrid requestGrid) throws BusinessException;
	
	void create(Province province) throws BusinessException;
	
	Province findProvinceByID(Long id) throws BusinessException;
	
	void update(Province province) throws BusinessException;
	
	void delete(Province province) throws BusinessException;
	
}
