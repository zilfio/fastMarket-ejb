package it.univaq.mwt.fastmarket.business;

import it.univaq.mwt.fastmarket.business.model.Booking;

import javax.ejb.Remote;

@Remote
public interface BookingService {

	ResponseGrid<Booking> findAllBookingsPaginated(RequestGrid requestGrid) throws BusinessException;
	
	void create(Booking booking) throws BusinessException;
	
	Booking findBookingByID(Long id) throws BusinessException;
	
	void update(Booking booking) throws BusinessException;
	
	void delete(Booking booking) throws BusinessException;
	
}
