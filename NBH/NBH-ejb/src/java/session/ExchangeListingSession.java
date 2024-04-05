/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package session;

import entity.Customer;
import entity.ExchangeListing;
import entity.Offer;
import entity.Skill;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author ninja
 */
@Stateless
public class ExchangeListingSession implements ExchangeListingSessionLocal {

    @EJB(name = "CustomerSessionLocal")
    private CustomerSessionLocal customerSessionLocal;

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @PersistenceContext(unitName = "NBH-ejbPU")
    private EntityManager em;

    //SCHEDULE WORKS - UNCOMMENT LATER
    //@Schedule(hour = "*", minute = "*/10")
    /*
    public void checkForExpiredListings() {
        List<ExchangeListing> listings = getAllListing(null);
        Date now = new Date();
        for (ExchangeListing listing : listings) {
            if (listing.getEndDateTime().before(now)) {
                listing.setStatus("expired");
                updateListing(listing);
            }
        }
    }
     */
    @Override
    public void createListing(ExchangeListing el, Long cId, List<Long> skillIds) {
        Customer creator = customerSessionLocal.getCustomer(cId);
        // associate with customer
        creator.getExchangeListings().add(el);
        el.setCreator(creator);
        // associate with skills
        Skill skill;
        for (Long skillId : skillIds) {
            skill = em.find(Skill.class, skillId);
            if (el.getSkills() == null) {
                el.setSkills(new ArrayList<>());
            }
            el.getSkills().add(skill);
            if (skill.getExchangeListings() == null) {
                skill.setExchangeListings(new ArrayList<>());
            }
            skill.getExchangeListings().add(el);
        }
        em.persist(el);
    }

    @Override
    public void updateListing(ExchangeListing el) throws NoResultException {
        ExchangeListing oldEl = getListing(el.getId());
        oldEl.setDescription(el.getDescription());
        oldEl.setStartDateTime(el.getStartDateTime());
        oldEl.setEndDateTime(el.getEndDateTime());
        oldEl.setPostedDateTime(el.getPostedDateTime());
        oldEl.setListingType(el.getListingType());
        oldEl.setStatus(el.getStatus());
        oldEl.setVisibility(el.getVisibility());
    }

    @Override
    public void deleteListing(Long elId) throws NoResultException {
        ExchangeListing elToDelete = em.find(ExchangeListing.class, elId);
        // dissociate from customer
        Customer creator = elToDelete.getCreator();
        creator.getExchangeListings().remove(elToDelete);

        // dissociate from skill
        List<Skill> skills = elToDelete.getSkills();
        for (Skill s : skills) {
            s.getExchangeListings().remove(elToDelete);
            elToDelete.getSkills().remove(s);
        }

        // dissociate from offer
        List<Offer> offers = elToDelete.getOffers();
        for (Offer o : offers) {
            em.remove(o);
        }

        em.remove(elToDelete);
    }

    @Override
    public List<ExchangeListing> getAllListing(Long cId) {
        if (cId != null) {
            Query query = em.createQuery("SELECT el FROM ExchangeListing el WHERE el.creator.id = :cId");
            query.setParameter("cId", cId);
            return query.getResultList();
        } else {
            Query query = em.createQuery("SELECT el FROM ExchangeListing el WHERE el.status LIKE :status");
            query.setParameter("status", "ACTIVE");
            return query.getResultList();
        }
    }

    @Override
    public List<ExchangeListing> searchListing(String title) throws NoResultException {
        Query query;
        if (title != null) {
            query = em.createQuery("SELECT el FROM ExchangeListing el WHERE LOWER(el.description) LIKE :title");
            query.setParameter("title", "%" + title.toLowerCase() + "%");
        } else {
            query = em.createQuery("SELECT el FROM ExchangeListing el");
        }
        return query.getResultList();
    }

    @Override
    public ExchangeListing getListing(Long elId) throws NoResultException {
        ExchangeListing el = em.find(ExchangeListing.class, elId);
        if (el != null) {
            /*
            // recursive call btwn update and getListing so cnnt put here
            if (el.getEndDateTime().before(new Date())) {
                el.setStatus("EXPIRED");
                updateListing(el);
            }*/
            return el;
        } else {
            throw new NoResultException("Not found");
        }
    }

}
