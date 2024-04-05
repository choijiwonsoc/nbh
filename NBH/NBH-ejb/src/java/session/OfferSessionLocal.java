/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionLocal.java to edit this template
 */
package session;

import entity.Offer;
import java.util.List;
import javax.ejb.Local;
import javax.persistence.NoResultException;

/**
 *
 * @author ninja
 */
@Local
public interface OfferSessionLocal {

    public void createOffer(Offer o, Long cId, Long elId, List<Long> skillIds);

    public void updateOffer(Offer o) throws NoResultException;

    //public void cancelOffer(Long oId) throws NoResultException;
    public Offer getOffer(Long oId) throws NoResultException;

    // type: by Customomer or by exchangeListing
    public List<Offer> getAllOffers(Long id, String type);

    //public void cancelOffer(Long oId, Long cId, Long elId) throws NoResultException;
}
