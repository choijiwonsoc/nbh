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
import java.util.List;
import javax.ejb.EJB;
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
public class OfferSession implements OfferSessionLocal {

    @EJB(name = "ExchangeListingSessionLocal")
    private ExchangeListingSessionLocal exchangeListingSessionLocal;

    @EJB(name = "CustomerSessionLocal")
    private CustomerSessionLocal customerSessionLocal;

    @PersistenceContext
    private EntityManager em;

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @Override
    public void createOffer(Offer o, Long cId, Long elId, List<Long> skillIds) {
        Customer customer = customerSessionLocal.getCustomer(cId);
        // associate with Customer
        customer.getOffers().add(o);
        o.setCustomer(customer);

        //associate with ExchangeListing
        ExchangeListing el = exchangeListingSessionLocal.getListing(elId);
        el.getOffers().add(o);
        o.setExchangeListing(el);

        // associate with skills
        if (o.getSkills() == null) {
            o.setSkills(new ArrayList<>());
        }
        Skill skill;
        for (Long skillId : skillIds) {
            skill = em.find(Skill.class, skillId);
            if (skill.getOffers() == null) {
                skill.setOffers(new ArrayList<>());
            }
            o.getSkills().add(skill);
            skill.getOffers().add(o);
        }
        em.persist(o);
    }

    @Override
    public void updateOffer(Offer o) throws NoResultException {
        Offer oldO = getOffer(o.getId());
        oldO.setDescription(o.getDescription());
        oldO.setDateOptions(o.getDateOptions());
        oldO.setPostedDateTime(o.getPostedDateTime());
        oldO.setStatus(o.getStatus());
        oldO.setVisibility(o.getVisibility());
    }

    @Override
    public Offer getOffer(Long oId) throws NoResultException {
        Offer o = em.find(Offer.class, oId);
        if (o != null) {
            return o;
        } else {
            throw new NoResultException("Not found");
        }
    }

    @Override
    public List<Offer> getAllOffers(Long id, String type) {
        Query query;
        if (id != null) {
            if (type.equals("C")) {
                query = em.createQuery("SELECT o FROM Offer o WHERE o.customer.id = :cId");
                query.setParameter("cId", id);

            } else if (type.equals("EL")) {
                query = em.createQuery("SELECT o FROM Offer o WHERE o.exchangeListing.id = :elId AND o.status <> 'CANCELLED' AND o.status <> 'DECLINED' "); //status not equal cancelled
                query.setParameter("elId", id);

            } else {
                throw new IllegalArgumentException("Invalid type given");
            }

        } else {
            query = em.createQuery("SELECT o FROM Offer o ");

        }
        return query.getResultList();
    }

    @Override
    public void deleteOffer(Long oId, Long cId, Long elId) throws NoResultException {
        Offer o = em.find(Offer.class, oId);
        // dissociate from customer
        Customer customer = em.find(Customer.class, cId);
        customer.getOffers().remove(o);
        o.setCustomer(null);

        // dissociate from exchangeListing
        ExchangeListing el = em.find(ExchangeListing.class, elId);
        el.getOffers().remove(o);
        o.setExchangeListing(null);

        // dissociate from skill
        for (Skill skill : o.getSkills()) {
            skill.getOffers().remove(o); // Remove the offer from each skill
            em.persist(skill); // Persist changes if not cascading
        }
        o.getSkills().clear(); // Clear the collection of skills from the offer

        em.remove(o);
    }

}
