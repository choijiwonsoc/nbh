/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package session;

import entity.Customer;
import entity.ServiceItem;
import entity.ServiceProviderListing;
import java.util.List;
import javax.ejb.Local;
import javax.persistence.NoResultException;

/**
 *
 * @author weiku
 */
@Local
public interface BusinessSessionLocal {
    
    public void updateProgress(Long serviceProviderListingId);
    
    public void deleteServiceProviderListing(Long serviceProviderListingId) throws NoResultException;
    
    public List<ServiceItem> getServiceItemsByServiceProviderListingId(Long serviceProviderListingId);
    
    public List<ServiceProviderListing> getOtherServiceProviderListingByCustomerId(Long cId);
    
    public List<ServiceProviderListing> getServiceProviderListingByCustomerId(Long cId);
    
    public void createItem(Long serviceProviderListingId,ServiceItem serviceItem);
    
    public void createBusiness(Customer c, ServiceProviderListing s);
    
    public List<ServiceProviderListing> getBusinessListings(Long cId);
    
    public ServiceProviderListing getSpecificBusinessListing(Long businessListingId);
}