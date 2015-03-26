package it.univaq.mwt.fastmarket.business;

import it.univaq.mwt.fastmarket.business.model.SystemInformation;

import javax.ejb.Remote;

@Remote
public interface SystemInformationService {

	ResponseGrid<SystemInformation> findAllSystemInformationsPaginated(RequestGrid requestGrid) throws BusinessException;
	
	void create(SystemInformation systemInformation) throws BusinessException;
	
	SystemInformation findSystemInformationByID(Long id) throws BusinessException;
	
	void update(SystemInformation systemInformation) throws BusinessException;
	
	void delete(SystemInformation systemInformation) throws BusinessException;
	
}
