package it.univaq.mwt.fastmarket.business;

import it.univaq.mwt.fastmarket.business.model.Cart;

import java.util.Set;

import javax.ejb.Remote;

@Remote
public interface CartService {

	Set<Cart> getAllCarts() throws BusinessException;
	
	ResponseGrid<Cart> findAllCartsPaginated(RequestGrid requestGrid) throws BusinessException;
	
	void create(Cart cart) throws BusinessException;
	
	Cart findCartById(Long id) throws BusinessException;
	
	void update(Cart cart) throws BusinessException;
	
	void delete(Cart cart) throws BusinessException;
	
}
