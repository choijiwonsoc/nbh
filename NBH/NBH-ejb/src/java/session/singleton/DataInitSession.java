/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB31/SingletonEjbClass.java to edit this template
 */
package session.singleton;

import entity.Skill;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import session.SkillSessionLocal;

/**
 *
 * @author ninja
 */
@Singleton
@LocalBean
@Startup
public class DataInitSession {

    @PersistenceContext(unitName = "NBH-ejbPU")
    private EntityManager em;

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @EJB(name = "SkillSessionLocal")
    private SkillSessionLocal skillSessionLocal;

    @PostConstruct
    public void postConstruct() {
        if (em.find(Skill.class, 1L) == null) {
            skillSessionLocal.createSkill(new Skill("Caretaking"));
            skillSessionLocal.createSkill(new Skill("Gardening"));
            skillSessionLocal.createSkill(new Skill("Tutoring"));
            skillSessionLocal.createSkill(new Skill("Dog Walking"));
            skillSessionLocal.createSkill(new Skill("Tech"));
            skillSessionLocal.createSkill(new Skill("Cooking"));
            skillSessionLocal.createSkill(new Skill("Grocery Shopping"));
        }

    }
}
