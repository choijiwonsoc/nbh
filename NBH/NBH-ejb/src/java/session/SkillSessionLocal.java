/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionLocal.java to edit this template
 */
package session;

import entity.Skill;
import error.NoResultException;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author ninja
 */
@Local
public interface SkillSessionLocal {

    public void createSkill(Skill s, Long cId);

    public void equipSkillCustomer(Long sId, Long cId) throws NoResultException;

    public void createSkill(Skill s);

    public void editSkill(Skill s) throws NoResultException;

    public void deleteSkill(Long sId) throws NoResultException;

    public Skill getSkill(Long sId) throws NoResultException;

    public List<Skill> getAllSkillsByCustomer(Long cId);

    public List<Skill> getAllSkillsByListing(Long elId);

    public List<Skill> getAllSkillsByOffer(Long oId);

}
