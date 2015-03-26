package it.univaq.mwt.fastmarket.business;

import it.univaq.mwt.fastmarket.business.model.Role;

import java.util.Set;

import javax.ejb.Remote;

@Remote
public interface RoleService {
	
	Set<Role> getAllRoles() throws BusinessException;
	
	ResponseGrid<Role> findAllRolesPaginated(RequestGrid requestGrid) throws BusinessException;
	
	void create(Role role) throws BusinessException;
	
	Role findRoleByID(Long id) throws BusinessException;
	
	Role findRoleByName(String name) throws BusinessException;
	
	Set<Role> getRolesByUserID(Long id) throws BusinessException;
	
	Set<Role> getRolesUserRegistred() throws BusinessException;
	
	void update(Role role) throws BusinessException;
	
	void delete(Role role) throws BusinessException;

}
