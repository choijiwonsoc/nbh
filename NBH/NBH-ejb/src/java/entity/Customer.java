/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import java.io.Serializable;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;

/**
 *
 * @author choijiwon
 */
@Entity
public class Customer implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String contact;
    private String email;
    private String district;
    private String region;

    private String password;
    private String username;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Post> posts;
    
    
    @OneToMany(mappedBy = "customer", fetch = FetchType.EAGER)
    private List<ServiceProviderListing> serviceProviderListing;

    @OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    @JoinTable(name = "LIKED_POSTS")
    private List<Post> likedPosts;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<HiddenGemReview> hiddenGemReviews;

     @OneToMany(mappedBy = "requester")
    private List<Request> sentRequests;

    @OneToMany(mappedBy = "receiver")
    private List<Request> receivedRequests;
    
    public Long getId() {
        return id;
    }

    public List<Request> getSentRequests() {
        return sentRequests;
    }

    public void setSentRequests(List<Request> sentRequests) {
        this.sentRequests = sentRequests;
    }

    public List<Request> getReceivedRequests() {
        return receivedRequests;
    }

    public void setReceivedRequests(List<Request> receivedRequests) {
        this.receivedRequests = receivedRequests;
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

    public List<ServiceProviderListing> getServiceProviderListing() {
        return serviceProviderListing;
    }

    public void setServiceProviderListing(List<ServiceProviderListing> serviceProviderListing) {
        this.serviceProviderListing = serviceProviderListing;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Customer)) {
            return false;
        }
        Customer other = (Customer) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Customer[ id=" + id + " ]";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<Post> getLikedPosts() {
        return likedPosts;
    }

    public void setLikedPosts(List<Post> likedPosts) {
        this.likedPosts = likedPosts;
    }

    public List<HiddenGemReview> getHiddenGemReviews() {
        return hiddenGemReviews;
    }

    public void setHiddenGemReviews(List<HiddenGemReview> hiddenGemReviews) {
        this.hiddenGemReviews = hiddenGemReviews;
    }

}
