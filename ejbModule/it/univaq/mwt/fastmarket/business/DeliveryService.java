package it.univaq.mwt.fastmarket.business;

import it.univaq.mwt.fastmarket.business.model.Delivery;

import javax.ejb.Remote;

@Remote
public interface DeliveryService {
	
	ResponseGrid<Delivery> findAllDeliveriesPaginated(RequestGrid requestGrid) throws BusinessException;
	
	void create(Delivery delivery) throws BusinessException;
	
	Delivery findDeliveryByID(Long id) throws BusinessException;
	
	void update(Delivery delivery) throws BusinessException;
	
	void delete(Delivery delivery) throws BusinessException;
	
}
