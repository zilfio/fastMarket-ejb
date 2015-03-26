package it.univaq.mwt.fastmarket.business.impl.hibernate;

import java.util.List;

import javax.annotation.Resource;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import it.univaq.mwt.fastmarket.business.BookingService;
import it.univaq.mwt.fastmarket.business.BusinessException;
import it.univaq.mwt.fastmarket.business.RequestGrid;
import it.univaq.mwt.fastmarket.business.ResponseGrid;
import it.univaq.mwt.fastmarket.business.model.Booking;
import it.univaq.mwt.fastmarket.common.utility.ConversionUtility;

@Stateless
@Remote(BookingService.class)
@TransactionManagement(TransactionManagementType.CONTAINER)
public class BookingServiceHibernate implements BookingService {

	@Resource(name="hib") 
	SessionFactory sessionFactory;
	
	public BookingServiceHibernate() {
		new Configuration().configure().buildSessionFactory();
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public ResponseGrid<Booking> findAllBookingsPaginated(RequestGrid requestGrid) throws BusinessException {
		
		Session session = sessionFactory.openSession();
		
		Criteria criteria = session.createCriteria(Booking.class,"b")
				.setFetchMode("b.cart", FetchMode.JOIN)
				.createAlias("b.cart", "cart"); // inner join by default
		
		if ("id".equals(requestGrid.getSortCol())) {
			requestGrid.setSortCol("b.id");
		} else if ("cart.name".equals(requestGrid.getSortCol())) {
			requestGrid.setSortCol("cart.name");
		} else {
			requestGrid.setSortCol("b." + requestGrid.getSortCol());
		}
	
		if(!"".equals(requestGrid.getSortCol()) && !"".equals(requestGrid.getSortDir())) {
			if("asc".equals(requestGrid.getSortDir())){
				criteria.addOrder(Order.asc(requestGrid.getSortCol()));	
			}else{
				criteria.addOrder(Order.desc(requestGrid.getSortCol()));	
			}
		}
		
		if(!"".equals(requestGrid.getsSearch())){
			criteria.add(Restrictions.like("cart.name", ConversionUtility.addPercentSuffix(requestGrid.getsSearch())).ignoreCase());
		}
		
		criteria.setFirstResult((int) requestGrid.getiDisplayStart())
			.setMaxResults((int) requestGrid.getiDisplayLength());
		
		Long records = (Long) session.createCriteria(Booking.class)
				.setProjection(Projections.rowCount()).uniqueResult();
		
		List<Booking> bookings = criteria.list();
		
		return new ResponseGrid<Booking>(requestGrid.getsEcho(), records, records, bookings);
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void create(Booking booking) throws BusinessException {
		Session session = sessionFactory.openSession();
		session.save(booking);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Booking findBookingByID(Long id) throws BusinessException {
		Session session = sessionFactory.openSession();
		Query query = session.createQuery("from Booking where id=:id");
		query.setParameter("id", id);
		Booking booking = (Booking) query.uniqueResult();
		if(booking == null) {
			throw new BusinessException("Booking not found");
		} else {
			return booking;
		}
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void update(Booking booking) throws BusinessException {
		Session session = sessionFactory.openSession();
		session.merge(booking);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void delete(Booking booking) throws BusinessException {
		Session session = sessionFactory.openSession();
		Booking booking2 = (Booking) session.load(Booking.class, booking.getId());
		session.delete(booking2);
	}

}
