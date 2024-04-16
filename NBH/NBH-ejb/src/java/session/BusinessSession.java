/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package session;

import entity.Customer;
import entity.Request;
import entity.Review;
import entity.ServiceItem;
import entity.ServiceProviderListing;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author weiku
 */
@Stateless
public class BusinessSession implements BusinessSessionLocal {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Request> receieveRequest(Long id) {
        Query query = em.createQuery("SELECT e FROM Request e  WHERE e.receiver.id = :id");
        query.setParameter("id", id);
        return query.getResultList();
    }

    @Override
    public void insertRating(Long sId, Integer rating, Long userId) {
        ServiceProviderListing s = getSpecificBusinessListing(sId);
        //Integer currentRating = s.getRating();
        Customer customer = em.find(Customer.class, userId);
        Review review = new Review();
        review.setStars(rating); // Assuming you have a setRating method in Review entity
        review.setCustomer(customer);
        review.setServiceProviderListing(s);

        // Add the review to the collections
        s.getReviews().add(review);
        customer.getReview().add(review);
        em.persist(review);
    }

    
    @Override
    public Double getRatingForServiceProviderListing(Long sId) {
     
        Query query = em.createQuery("SELECT AVG(e.stars) FROM Review e WHERE e.serviceProviderListing.id = :sId");
        query.setParameter("sId", sId);
        
        return (Double)query.getSingleResult();
    }
    
    @Override
    public void insertReview(Long sId, Integer rating) {
        ServiceProviderListing s = getSpecificBusinessListing(sId);
        Integer currentRating = s.getRating();
        if (currentRating == 0) {
            Integer averageRating = currentRating + rating;
            s.setRating(averageRating);
        } else {
            Integer averageRating = (currentRating + rating) / 2;
            s.setRating(averageRating);
        }

        em.persist(s);

    }

    @Override
    public Request getRequest(Long requestId) {
        Request r = em.find(Request.class, requestId);
        return r;
    }

    @Override
    public void acknowledgeRequest(Long rId) {
        Query query = em.createQuery("UPDATE Request r SET r.status = :progress WHERE r.id = :rId");
        query.setParameter("progress", "Acknowledged");
        query.setParameter("rId", rId);

        int updatedCount = query.executeUpdate();

    }

    @Override
    public void makeRequest(Long serviceProviderListingId, Long sendRequestPersonId, Request request) {
        ServiceProviderListing s = getSpecificBusinessListing(serviceProviderListingId);
        Customer receiver = s.getCustomer();
        Customer sender = em.find(Customer.class, sendRequestPersonId);

        request.setStatus("Pending"); // Set the initial status
        request.setRequester(sender); // Set the sender
        request.setReceiver(receiver);

        em.persist(request);
    }

    @Override
    public void updateProgress(Long serviceProviderListingId) {
        //Query query = em.createQuery("UPDATE Attendance r SET r.status = :status WHERE r.event.id = :eventId AND r.customer.id = :customerId");
        Query query = em.createQuery("UPDATE ServiceProviderListing e SET e.progress = :progress WHERE e.id = :serviceProviderListingId");
        query.setParameter("progress", "Cancelled");
        query.setParameter("serviceProviderListingId", serviceProviderListingId);

        int updatedCount = query.executeUpdate();

    }

    @Override
    public void deleteServiceProviderListing(Long serviceProviderListingId) throws NoResultException {
        ServiceProviderListing serviceProviderListing = getSpecificBusinessListing(serviceProviderListingId);

        if (serviceProviderListing != null) {
            Customer customer = serviceProviderListing.getCustomer(); // Get the associated customer
            if (customer != null) {
                customer.setServiceProviderListing(null); // Remove the association from the customer side
            }

            em.remove(serviceProviderListing);
        } else {
            throw new NoResultException("Service Provider Listing with ID " + serviceProviderListingId + " not found");
        }
    }

    @Override
    public List<ServiceItem> getServiceItemsByServiceProviderListingId(Long serviceProviderListingId) {
        Query query = em.createQuery("SELECT s FROM ServiceItem s JOIN s.serviceProviderListing spl WHERE spl.id = :serviceProviderListingId");
        query.setParameter("serviceProviderListingId", serviceProviderListingId);
        return query.getResultList();
    }

    @Override
    public List<ServiceProviderListing> getServiceProviderListingByCustomerId(Long cId) {
        Query query = em.createQuery("SELECT e FROM ServiceProviderListing e JOIN e.customer c WHERE c.id = :cId");
        query.setParameter("cId", cId);
        return query.getResultList();
    }

    @Override
    public List<ServiceProviderListing> getOtherServiceProviderListingByCustomerId(Long cId) {
        Query query = em.createQuery("SELECT e FROM ServiceProviderListing e JOIN e.customer c WHERE c.id != :cId AND e.progress = :progress");
        query.setParameter("cId", cId);
        query.setParameter("progress", "Ongoing");
        return query.getResultList();
    }

    @Override
    public void createItem(Long serviceProviderListingId, ServiceItem serviceItem) {
        ServiceProviderListing s = getSpecificBusinessListing(serviceProviderListingId);
        serviceItem.setServiceProviderListing(s);
        em.persist(serviceItem);
    }

    @Override
    public void createBusiness(Customer c, ServiceProviderListing s) {
        if (c != null && s != null) {
            // Set the customer in the service provider listing
            s.setCustomer(c);

            // If necessary, add the service provider listing to the customer
            if (c.getServiceProviderListing() == null) {
                c.setServiceProviderListing(new ArrayList<>());
            }
            c.getServiceProviderListing().add(s);

            // Persist the service provider listing
            em.persist(s);
        }
    }

    @Override
    public List<ServiceProviderListing> getBusinessListings(Long cId) {

        Query query = em.createQuery("SELECT e FROM ServiceProviderListing e JOIN e.customer c WHERE c.id = :cId");
        query.setParameter("cId", cId);
        return query.getResultList();
    }

    @Override
    public ServiceProviderListing getSpecificBusinessListing(Long businessListingId) {

        ServiceProviderListing s = em.find(ServiceProviderListing.class, businessListingId);
        return s;
    }

    public void persist(Object object) {
        em.persist(object);
    }
}
