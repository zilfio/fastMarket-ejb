package it.univaq.mwt.fastmarket.business;

import it.univaq.mwt.fastmarket.business.model.Address;

import javax.ejb.Remote;

@Remote
public interface AddressService {
	
	ResponseGrid<Address> findAllAddressesPaginated(RequestGrid requestGrid) throws BusinessException;
	
	void create(Address address) throws BusinessException;
	
	Address findAddressByID(Long id) throws BusinessException;
	
	void update(Address address) throws BusinessException;
	
	void delete(Address address) throws BusinessException;
}
