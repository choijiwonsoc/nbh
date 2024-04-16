/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionLocal.java to edit this template
 */
package session;

import entity.Customer;
import java.util.List;
import javax.ejb.Local;
import javax.persistence.NoResultException;

/**
 *
 * @author choijiwon
 */
@Local
public interface CustomerSessionLocal {

    public void persist(Customer c);

    public List<String> getAllEmails();

    public Customer getCustByUsername(String username);

    public Customer getCustomer(Long cId) throws NoResultException;

    public List<String> getAllUsername();

    public List<Customer> getAllCustomers();

    public void setProfilePicFile(Long cId, String fileName);

    public void editCustomer(Customer c);
    
}
