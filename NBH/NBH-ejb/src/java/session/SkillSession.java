/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package session;

import entity.Customer;
import entity.Skill;
import error.NoResultException;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author ninja
 */
@Stateless
public class SkillSession implements SkillSessionLocal {

    @EJB(name = "CustomerSessionLocal")
    private CustomerSessionLocal customerSessionLocal;

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @PersistenceContext(unitName = "NBH-ejbPU")
    private EntityManager em;

    @Override
    public void createSkill(Skill s, Long cId) {
        Customer customer = customerSessionLocal.getCustomer(cId);
        customer.getSkills().add(s);
        s.getCustomers().add(customer);
        em.persist(s);
    }

    @Override
    public void equipSkillCustomer(Long sId, Long cId) throws NoResultException {
        Skill sToEquip = em.find(Skill.class, sId);
        Customer customer = customerSessionLocal.getCustomer(cId);
        if (!customer.getSkills().contains(sToEquip)) {
            customer.getSkills().add(sToEquip);
            sToEquip.getCustomers().add(customer);
        }
    }

    @Override
    public void unequipAllSkillsCustomer(Long cId) throws NoResultException {
        Customer customer = customerSessionLocal.getCustomer(cId);
        List<Skill> skillsToUnequip = customer.getSkills();
        for (Skill s : skillsToUnequip) {
            customer.getSkills().remove(s);
            s.getCustomers().remove(customer);
        }
    }

    @Override
    public void createSkill(Skill s) {
        em.persist(s);
    }

    @Override
    public void editSkill(Skill s) throws NoResultException {
        Skill oldS = getSkill(s.getId());
        oldS.setSkillName(s.getSkillName());
    }

    @Override
    public void deleteSkill(Long sId) throws NoResultException {
        Skill s = em.find(Skill.class, sId);
        em.remove(s);
    }

    @Override
    public Skill getSkill(Long sId) throws NoResultException {
        Query query = em.createQuery("SELECT s FROM Skill s WHERE s.id = :sId");
        query.setParameter("sId", sId);
        return (Skill) query.getSingleResult();
    }

    @Override
    public List<Skill> getAllSkillsByCustomer(Long cId) {
        Query query;
        if (cId != null) {
            /*
            // for a to 1 relationship
            query = em.createQuery("SELECT s FROM Skill s WHERE s.customer.id = :cId");
             */
            query = em.createQuery("SELECT s FROM Skill s JOIN s.customers c WHERE c.id = :cId");
            query.setParameter("cId", cId);
            return query.getResultList();
        } else {
            query = em.createQuery("SELECT s FROM Skill s");
            return query.getResultList();
        }
    }

    @Override
    public List<Skill> getAllSkillsByListing(Long elId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Skill> getAllSkillsByOffer(Long oId) {
        Query query;
        if (oId != null) {
            /*
            // for a to 1 relationship
            query = em.createQuery("SELECT s FROM Skill s WHERE s.customer.id = :cId");
             */
            query = em.createQuery("SELECT s FROM Skill s JOIN s.offers o WHERE o.id = :oId");
            query.setParameter("oId", oId);
            return query.getResultList();
        } else {
            query = em.createQuery("SELECT s FROM Skill s");
            return query.getResultList();
        }
    }

}
