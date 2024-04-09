/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean;

import entity.Customer;
import entity.Request;
import entity.ServiceItem;
import entity.ServiceProviderListing;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpSession;
import session.BusinessSessionLocal;
import session.CustomerSessionLocal;

/**
 *
 * @author weiku
 */
@Named(value = "businessManagedBean")
@ViewScoped
public class BusinessManagedBean implements Serializable {

    @EJB
    private BusinessSessionLocal businessSession;

    @EJB
    private CustomerSessionLocal customerSessionBean;

    private String bname;
    private String location;
    private String bio;
    private String category;
    private String uen;
    private Long userId;

    private ServiceProviderListing selectedServiceProviderListing;
    private List<ServiceProviderListing> businessListingCreatedByUser;

    private String itemName;
    private double price;
    private String description;

    private List<ServiceProviderListing> businessListings;
    private String searchType = "NAME";
    private String searchString;
    private List<ServiceProviderListing> searchServiceProviderListing;

    private List<ServiceItem> serviceItems;
  
    private String requestDescription;
    
    private List<Request> receievedRequests;
    
    private Request selectedRequest;
    
    public BusinessManagedBean() {
    }

    @PostConstruct
    public void init() {
        FacesContext context = FacesContext.getCurrentInstance();
        ExternalContext externalContext = context.getExternalContext();
        HttpSession session = (HttpSession) externalContext.getSession(false);
        if (session != null) {
            Long customerId = (Long) session.getAttribute("userId");

            if (customerId != null) {

                this.userId = customerId;
            }
        }

        String currentPage = context.getViewRoot().getViewId();
        if (currentPage.equals("/viewBusinessListing.xhtml")) {
            businessListings = businessSession.getServiceProviderListingByCustomerId(userId);
            System.out.println(businessListings + "1");
        } else {
            businessListings = businessSession.getOtherServiceProviderListingByCustomerId(userId);
            System.out.println(businessListings + "2");
        }

        if (searchString == null || searchString.equals("")) {
            searchServiceProviderListing = businessListings; 
        } else {
            switch (searchType) {
                case "NAME":
                    //searchServiceProviderListing = eventSessionBean.searchEvents(searchString);
                    break;

                case "LOCATION":
                    //searchServiceProviderListing = eventSessionBean.searchEvents(searchString);

                default:
                    //searchServiceProviderListing = eventSessionBean.searchEvents(searchString);
                    break;
            }
        }

    }
    
    public void acknowledgeRequest() {
    FacesContext context = FacesContext.getCurrentInstance();

        Map<String, String> params = context.getExternalContext()
                .getRequestParameterMap();
        String requestIdStr = params.get("requestId");
        Long requestId = Long.parseLong(requestIdStr);
        Request request = businessSession.getRequest(requestId);
        System.out.println(request + "acknowledged");
        try {

            businessSession.acknowledgeRequest(request.getId());

        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Unable to acknowledge this request"));
        }
    }
    
    public void receieveRequest() {
        receievedRequests = businessSession.receieveRequest(userId);
        System.out.println(receievedRequests + "request");
        
    }
    
    
    public void makeRequest() {
    FacesContext context = FacesContext.getCurrentInstance();
    Request r = new Request();
    System.out.println(requestDescription + " this is the description");
    r.setDescription(requestDescription);
    System.out.println(this.selectedServiceProviderListing + "this checked");
    try {
        if (selectedServiceProviderListing != null && userId != null) {
            businessSession.makeRequest(selectedServiceProviderListing.getId(), userId, r);
            System.out.println("makeRequest method called successfully."); // Add this line for logging
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Request successfully created"));
        } else {
            if (selectedServiceProviderListing == null) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No service provider listing selected."));
            }
            if (userId == null) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "User ID not found."));
            }
        }
    } catch (Exception e) {
        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Unable to create request: " + e.getMessage()));
    }
}


    
    
    public void deleteServiceProviderListing() {
        FacesContext context = FacesContext.getCurrentInstance();

        Map<String, String> params = context.getExternalContext()
                .getRequestParameterMap();
        String serviceProviderListingIdStr = params.get("serviceProviderListingId");
        Long serviceProviderListingId = Long.parseLong(serviceProviderListingIdStr);
        try {
            businessSession.deleteServiceProviderListing(serviceProviderListingId);
        } catch (Exception e) {
            //show with an error icon
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Unable to delete Event"));
            businessSession.updateProgress(serviceProviderListingId);
            return;
        }
        context.addMessage(null, new FacesMessage("Success",
                "Successfully deleted Event"));
        init();
    }
    
    
    public void retrieveServiceItems() {
        System.out.println("retrieveServiceItems method called successfully."); // Add this line for logging
        FacesContext context = FacesContext.getCurrentInstance();

        Map<String, String> params = context.getExternalContext()
                .getRequestParameterMap();
        String serviceProviderListingIdStr = params.get("serviceProviderListingId");
        Long serviceProviderListingId = Long.parseLong(serviceProviderListingIdStr);
        System.out.println(serviceProviderListingId + "retrieveServiceItems ");
        try {

            this.serviceItems = businessSession.getServiceItemsByServiceProviderListingId(serviceProviderListingId);

        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Unable to load serviceItems"));
        }
    }

    public void loadServiceProviderListing(Long serviceProviderListingId) {
    FacesContext context = FacesContext.getCurrentInstance();
    
    try {
        System.out.println(serviceProviderListingId + " this id");
        this.selectedServiceProviderListing = businessSession.getSpecificBusinessListing(serviceProviderListingId);
    } catch (Exception e) {
        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Unable to load service provider listing"));
    }
}

    public void loadBusinessListingsCreatedByUser() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {

            this.businessListingCreatedByUser
                    = businessSession.getBusinessListings(userId);

        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Unable to load"));
        }
    }

    public void addItems() {
        FacesContext context = FacesContext.getCurrentInstance();
        ServiceItem c = new ServiceItem();

        c.setItemDescription(description);
        c.setItemName(itemName);
        c.setItemPrice(price);

        try {
            businessSession.createItem(this.selectedServiceProviderListing.getId(), c);
            System.out.println("addItems() method called successfully."); // Add this line for logging
        } catch (Exception e) {
            //show with an error icon
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Unable to create Business"));
            return;
        }

        context.addMessage(null, new FacesMessage("Success", "Successfully Created Business"));
    }

    public void addBusiness(ActionEvent evt) {
        FacesContext context = FacesContext.getCurrentInstance();
        ServiceProviderListing c = new ServiceProviderListing();
        c.setBusinessName(bname);
        c.setCategory(category);
        c.setLocation(location);
        c.setBio(bio);
        c.setBusinessUEN(uen);
        Date currentDate = new Date();
        c.setDatePosted(currentDate);
        Customer customer = customerSessionBean.getCustomer(userId);
        //eventSessionBean.createEvent(customer, c);

        try {
            businessSession.createBusiness(customer, c);
        } catch (Exception e) {
            //show with an error icon
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Unable to create Business"));
            return;
        }
        context.addMessage(null, new FacesMessage("Success",
                "Successfully Created Business"));
    }

    public List<ServiceItem> getServiceItems() {
        return serviceItems;
    }

    public void setServiceItems(List<ServiceItem> serviceItems) {
        this.serviceItems = serviceItems;
    }

    public BusinessSessionLocal getBusinessSession() {
        return businessSession;
    }

    public void setBusinessSession(BusinessSessionLocal businessSession) {
        this.businessSession = businessSession;
    }

    public CustomerSessionLocal getCustomerSessionBean() {
        return customerSessionBean;
    }

    public void setCustomerSessionBean(CustomerSessionLocal customerSessionBean) {
        this.customerSessionBean = customerSessionBean;
    }

    public String getBname() {
        return bname;
    }

    public void setBname(String bname) {
        this.bname = bname;
    }

    public String getRequestDescription() {
        return requestDescription;
    }

    public void setRequestDescription(String requestDescription) {
        this.requestDescription = requestDescription;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<Request> getReceievedRequests() {
        return receievedRequests;
    }

    public void setReceievedRequests(List<Request> receievedRequests) {
        this.receievedRequests = receievedRequests;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getCategory() {
        return category;
    }

    public ServiceProviderListing getSelectedServiceProviderListing() {
        return selectedServiceProviderListing;
    }

    public void setSelectedServiceProviderListing(ServiceProviderListing selectedServiceProviderListing) {
        this.selectedServiceProviderListing = selectedServiceProviderListing;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getUen() {
        return uen;
    }

    public void setUen(String uen) {
        this.uen = uen;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<ServiceProviderListing> getBusinessListingCreatedByUser() {
        return businessListingCreatedByUser;
    }

    public void setBusinessListingCreatedByUser(List<ServiceProviderListing> businessListingCreatedByUser) {
        this.businessListingCreatedByUser = businessListingCreatedByUser;
    }

    public List<ServiceProviderListing> getBusinessListings() {
        return businessListings;
    }

    public void setBusinessListings(List<ServiceProviderListing> businessListings) {
        this.businessListings = businessListings;
    }

    public String getSearchType() {
        return searchType;
    }

    public void setSearchType(String searchType) {
        this.searchType = searchType;
    }

    public String getSearchString() {
        return searchString;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    public List<ServiceProviderListing> getSearchServiceProviderListing() {
        return searchServiceProviderListing;
    }

    public void setSearchServiceProviderListing(List<ServiceProviderListing> searchServiceProviderListing) {
        this.searchServiceProviderListing = searchServiceProviderListing;
    }

}
