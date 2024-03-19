/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package managedbean;

import entity.Customer;
import entity.Post;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import java.io.Serializable;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.persistence.NoResultException;
import session.PostSessionLocal;

/**
 *
 * @author choijiwon
 */
@Named(value = "postManagedBean")
@ViewScoped
public class PostManagedBean implements Serializable {

    @EJB(name = "PostSessionLocal")
    private PostSessionLocal postSessionLocal;

    private String category;
    private String title;
    private String description;

    /**
     * Creates a new instance of PostManagedBean
     */
    public PostManagedBean() {
    }

    public String addPost(Customer c) throws NoResultException, ParseException {
        LocalDate currentDate = LocalDate.now();

        // Convert LocalDate to Date
        Date nowdate = java.sql.Date.valueOf(currentDate);
        Post p = new Post();
        p.setCategory(category);
        p.setTitle(title);
        p.setDateCreated(nowdate);
        p.setDescription(description);
        p.setLikes(0);
        postSessionLocal.createPost(p, c.getId());
        FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Post successfully uploaded", null));
        return "/secret/forum.xhtml?faces-redirect=true";

    }

    public List<Post> getAllPosts(String category) {
        return postSessionLocal.getAllPostsOrderedByDate(category);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

}
