/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionLocal.java to edit this template
 */
package session;

import entity.Customer;
import javax.ejb.Local;

/**
 *
 * @author choijiwon
 */
@Local
public interface CustomerSessionLocal {

    public void persist(Customer c);
    
}
