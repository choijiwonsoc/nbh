/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionLocal.java to edit this template
 */
package session;

import entity.ExchangeListing;
import java.util.List;
import javax.ejb.Local;
import javax.persistence.NoResultException;

/**
 *
 * @author ninja
 */
@Local
public interface ExchangeListingSessionLocal {

    public void createListing(ExchangeListing el, Long cId, List<Long> skillIds);

    public void updateListing(ExchangeListing el, List<Long> newSkillIds) throws NoResultException;

    public void deleteListing(Long elId) throws NoResultException;

    // if null : all events. If pId provided, listing by person
    public List<ExchangeListing> getAllListing(Long cId);

    // if null : all events. If pId provided, listing by person
    public List<ExchangeListing> getAllActiveListing(Long cId);

    public List<ExchangeListing> searchListing(String title) throws NoResultException;

    public ExchangeListing getListing(Long elId) throws NoResultException;

    public ExchangeListing getListingFromOffer(Long oId) throws NoResultException;
}
