package it.univaq.mwt.fastmarket.business;

import javax.ejb.Remote;

import it.univaq.mwt.fastmarket.business.model.User;

@Remote
public interface SecurityService {

	User authenticate(String username) throws BusinessException;
	
}
