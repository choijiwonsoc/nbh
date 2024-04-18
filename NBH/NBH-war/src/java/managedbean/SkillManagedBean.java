/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package managedbean;

import entity.Skill;
import error.NoResultException;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import session.SkillSessionLocal;

/**
 *
 * @author ninja
 */
@Named(value = "skillManagedBean")
@ViewScoped
public class SkillManagedBean implements Serializable {

    @EJB(name = "SkillSessionLocal")
    private SkillSessionLocal skillSessionLocal;

    /**
     * Creates a new instance of SkillManagedBean
     */
    public SkillManagedBean() {
    }

    private List<Skill> skills;
    private List<Long> selectedSkillIds;
    private List<Skill> currentCustomerSkills;

    @PostConstruct
    public void init() {
        selectedSkillIds = new ArrayList<Long>(); // Initialize the list
        skills = skillSessionLocal.getAllSkillsByCustomer(null); //Display all the skills

        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        HttpSession session = request.getSession();
        long userId = (Long) session.getAttribute("userId");
        currentCustomerSkills = skillSessionLocal.getAllSkillsByCustomer(userId);
        //highlight skills that customer already has
        for (Skill skill : currentCustomerSkills) {
            selectedSkillIds.add(skill.getId()); // Add the skill ID to the selected list
        }

    }

    public void toggleSkillSelection(Long skillId) {
        boolean found = false;
        if (selectedSkillIds.isEmpty()) {
            selectedSkillIds.add(skillId);
        } else {
            if (selectedSkillIds.contains(skillId)) {
                selectedSkillIds.remove(skillId);
            } else {
                selectedSkillIds.add(skillId);
            }
        }
    }

    public String submit() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        HttpSession session = request.getSession();
        int addSkillCounter = 0;
        try {
            long userId = (Long) session.getAttribute("userId");
            /*

            "clear skill list and add agn"
             removing all skills from customer thr SessionBean method (unequipAllSkillsCustomer)
             retrieve currentCustomerSkills agn, check if this is empty
             then
             for each skill in selectedSkillsId -> equipSkillCustomer
             */
            skillSessionLocal.unequipAllSkillsCustomer(userId);
            if (!selectedSkillIds.isEmpty()) {

                currentCustomerSkills = skillSessionLocal.getAllSkillsByCustomer(userId);
                if (currentCustomerSkills.isEmpty()) {
                    //System.out.println("Customer has no skills");
                } else {
                    for (Skill skill : currentCustomerSkills) {
                        //System.out.println("customer current skill: " + skill);
                    }
                }
                for (Long sId : selectedSkillIds) {
                    //System.out.println("selected skill: " + skillSessionLocal.getSkill(sId).getSkillName());
                    skillSessionLocal.equipSkillCustomer(sId, userId);
                }
                FacesMessage msg = new FacesMessage("Successful", "Successfully updated skills");
                FacesContext.getCurrentInstance().addMessage(null, msg);
                return "addExchangeListing.xhtml?faces-redirect=true";
            }
            /*
            // Previous code, cannot unequip skill
            if (!selectedSkillIds.isEmpty()) {
                for (Skill skill : currentCustomerSkills) {
                    System.out.println("customer current skill: " + skill);
                }

                for (Long sId : selectedSkillIds) {
                    if (currentCustomerSkills.contains(skillSessionLocal.getSkill(sId))) {
                        // skill alr equipped
                        System.out.println("Skill already equipped");
                        System.out.println("Selected Skill: " + skillSessionLocal.getSkill(sId).getSkillName());
                    } else {
                        skillSessionLocal.equipSkillCustomer(sId, userId);
                        //context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Successfully added skills"));
                    }
                }
                FacesMessage msg = new FacesMessage("Successful", "Successfully updated skills");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
             */

        } catch (NoResultException ex) {
            Logger.getLogger(SkillManagedBean.class.getName()).log(Level.SEVERE, null, ex);
            //context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Unable to submit"));
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Unable to submit");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
        return null;
    }

    // Getter and setter
    public SkillSessionLocal getSkillSessionLocal() {
        return skillSessionLocal;
    }

    public void setSkillSessionLocal(SkillSessionLocal skillSessionLocal) {
        this.skillSessionLocal = skillSessionLocal;
    }

    public List<Skill> getSkills() {
        return skills;
    }

    public void setSkills(List<Skill> skills) {
        this.skills = skills;
    }

    public List<Long> getSelectedSkillIds() {
        return selectedSkillIds;
    }

    public void setSelectedSkillIds(List<Long> selectedSkillIds) {
        this.selectedSkillIds = selectedSkillIds;
    }

    public List<Skill> getCurrentCustomerSkills() {
        return currentCustomerSkills;
    }

    public void setCurrentCustomerSkills(List<Skill> currentCustomerSkills) {
        this.currentCustomerSkills = currentCustomerSkills;
    }

}
