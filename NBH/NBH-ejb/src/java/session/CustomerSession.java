/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package session;

import entity.Customer;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author choijiwon
 */
@Stateless
public class CustomerSession implements CustomerSessionLocal {

    @PersistenceContext
    private EntityManager em;

    @Override
    public void persist(Customer c) {
        em.persist(c);
    }
    
    @Override
    public List<String> getAllEmails() {
        List<String> customerEmailList = new ArrayList<>();
        try {
            Query query = em.createQuery("SELECT c.email FROM Customer c");
            List<String> customersEmail = query.getResultList();
            for (String email : customersEmail) {
                customerEmailList.add(email);
            }
        } catch (Exception e) {
            // Handle exception (e.g., log error, throw custom exception)
            e.printStackTrace();
        }
        return customerEmailList;
    }
    
    @Override
    public List<String> getAllUsername() {
        List<String> customerUsernameList = new ArrayList<>();
        try {
            Query query = em.createQuery("SELECT c.username FROM Customer c");
            List<String> customersUsername = query.getResultList();
            for (String username : customersUsername) {
                customerUsernameList.add(username);
            }
        } catch (Exception e) {
            // Handle exception (e.g., log error, throw custom exception)
            e.printStackTrace();
        }
        return customerUsernameList;
    }
    
    @Override
    public Customer getCustByUsername(String username) {
        
        Query query = em.createQuery("SELECT c FROM Customer c WHERE c.username = :username");
        query.setParameter("username", username);
        return (Customer) query.getSingleResult();
        
    }
    
    @Override
    public Customer getCustomer(Long cId) throws NoResultException {
        Customer cust = em.find(Customer.class, cId);
        if (cust != null) {
            return cust;
        } else {
            throw new NoResultException("Customer not found");
        }
    }
    
    @Override
    public List<Customer> getAllCustomers() {
        Query query = em.createQuery("SELECT c FROM Customer c");
        return query.getResultList();
    }
    
    @Override
    public void setProfilePicFile(Long cId, String fileName) {
        Customer cust = em.find(Customer.class, cId);
        cust.setFileName(fileName);
    }
    
    @Override
    public void editCustomer(Customer c){
        Customer oldC = em.find(Customer.class, c.getId());
        oldC.setContact(c.getContact());
        oldC.setEmail(c.getEmail());
        oldC.setUsername(c.getUsername());
        oldC.setPassword(c.getPassword());
    }
    
    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
}
