package it.univaq.mwt.fastmarket.business.impl.hibernate;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import it.univaq.mwt.fastmarket.business.BusinessException;
import it.univaq.mwt.fastmarket.business.SecurityService;
import it.univaq.mwt.fastmarket.business.model.Role;
import it.univaq.mwt.fastmarket.business.model.User;

@Stateless
@Remote(SecurityService.class)
@TransactionManagement(TransactionManagementType.CONTAINER)
public class SecurityServiceHibernate implements SecurityService {

	@Resource(name="hib") 
	SessionFactory sessionFactory;
	
	public SecurityServiceHibernate() {
		new Configuration().configure().buildSessionFactory();
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public User authenticate(String username) throws BusinessException {
		Session session = sessionFactory.openSession();
		Query query = session.createQuery("from User where username = :username");
		query.setParameter("username", username);
		User user = (User) query.uniqueResult();
		user.setRoles(findRoles(username));
		return user;
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	private Set<Role> findRoles(String username) throws BusinessException {
		Session session = sessionFactory.openSession();
		Query query = session.createQuery("select roles from User u where u.username = :username");
		query.setParameter("username", username);
		List<Role> roles = query.list();
		Set<Role> result = new HashSet<Role>(roles);
		
		return result;
	}

}
