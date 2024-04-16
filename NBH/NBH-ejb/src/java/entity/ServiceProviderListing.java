/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 *
 * @author weiku
 */
@Entity
public class ServiceProviderListing implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String businessName;
    
    private String location;
    
    private String bio;
    
    private Date datePosted;
    
    private String category;
    
    private String businessUEN;
    
    private String progress;
    
    private Integer rating;
    
    @OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    private ArrayList<ServiceItem> serviceItems;

    @OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    private ArrayList<Review> reviews;

    // Review
    @ManyToOne(fetch = FetchType.EAGER)
    private Customer customer;

    public String getProgress() {
        return progress;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }

    public Long getId() {
        return id;
    }

    public ServiceProviderListing() {
        this.serviceItems = new ArrayList<>();
        this.reviews = new ArrayList<>();;
        this.progress = "Ongoing";
        this.rating = 0;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getBio() {
        return bio;
    }

    public String getBusinessUEN() {
        return businessUEN;
    }

    public void setBusinessUEN(String businessUEN) {
        this.businessUEN = businessUEN;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public ArrayList<ServiceItem> getServiceItems() {
        return serviceItems;
    }

    public void setServiceItems(ArrayList<ServiceItem> serviceItems) {
        this.serviceItems = serviceItems;
    }

    public ArrayList<Review> getReviews() {
        return reviews;
    }

    public void setReviews(ArrayList<Review> reviews) {
        this.reviews = reviews;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String address) {
        this.location = address;
    }

    public Date getDatePosted() {
        return datePosted;
    }

    public void setDatePosted(Date datePosted) {
        this.datePosted = datePosted;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
    
    
    
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ServiceProviderListing)) {
            return false;
        }
        ServiceProviderListing other = (ServiceProviderListing) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.ServiceProviderListing[ id=" + id + " ]";
    }
    
}
