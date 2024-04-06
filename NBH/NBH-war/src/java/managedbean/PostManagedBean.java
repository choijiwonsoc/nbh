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
    private String newsTitle;
    private String newsDescription;

    private String storyTitle;
    private String storyDescription;

    private String interestGrpTitle;
    private String interestGrpDescription;

    /**
     * Creates a new instance of PostManagedBean
     */
    public PostManagedBean() {
    }

    public String addNews(Customer c) throws NoResultException, ParseException {
        LocalDate currentDate = LocalDate.now();
        FacesContext context = FacesContext.getCurrentInstance();

        if (newsTitle.length() == 0) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Please enter a title.", null);
            context.addMessage(null, message);
            return null;
        } else if (newsDescription.length() == 0) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Please enter a description.", null);
            context.addMessage(null, message);
            return null;
        } else {
            // Convert LocalDate to Date
            Date nowdate = java.sql.Date.valueOf(currentDate);
            Post p = new Post();
            p.setCategory(category);
            p.setTitle(newsTitle);
            p.setDateCreated(nowdate);
            p.setDescription(newsDescription);
            p.setLikes(0);
            postSessionLocal.createPost(p, c.getId());
            FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Post successfully uploaded", null));
            return "/forum.xhtml?faces-redirect=true";
        }

    }
    public List<Post> getAllPosts(String category) {
        return postSessionLocal.getAllPostsOrderedByDate(category);
    }
    
    public void addLike(Long pId, Long cId){
        postSessionLocal.addLike(pId, cId);
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getNewsTitle() {
        return newsTitle;
    }

    public void setNewsTitle(String newsTitle) {
        this.newsTitle = newsTitle;
    }

    public String getNewsDescription() {
        return newsDescription;
    }

    public void setNewsDescription(String newsDescription) {
        this.newsDescription = newsDescription;
    }

    public String getStoryTitle() {
        return storyTitle;
    }

    public void setStoryTitle(String storyTitle) {
        this.storyTitle = storyTitle;
    }

    public String getStoryDescription() {
        return storyDescription;
    }

    public void setStoryDescription(String storyDescription) {
        this.storyDescription = storyDescription;
    }

    public String getInterestGrpTitle() {
        return interestGrpTitle;
    }

    public void setInterestGrpTitle(String interestGrpTitle) {
        this.interestGrpTitle = interestGrpTitle;
    }

    public String getInterestGrpDescription() {
        return interestGrpDescription;
    }

    public void setInterestGrpDescription(String interestGrpDescription) {
        this.interestGrpDescription = interestGrpDescription;
    }

}
