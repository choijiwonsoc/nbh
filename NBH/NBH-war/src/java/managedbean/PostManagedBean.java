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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
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
    
    private Post currentPost;
    
    private String commentText;

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
    
    public void loadSelectedPost() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        HttpSession session = request.getSession();
        try {
            long postId = (Long) session.getAttribute("postId");
            this.currentPost
                    = postSessionLocal.getPost(postId);
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Unable to load post"));
        }
    }
    
    public boolean isLiked(Long pId, Long cId){
        return postSessionLocal.isLiked(pId, cId);
    }

    public void viewPostDetails(Long postId) throws NoResultException {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        HttpSession session = request.getSession();
        session.setAttribute("postId", postId);
    }
    
    public List<Post> getAllPosts() {
        return postSessionLocal.getAllPostsOrderedByDate();
    }
    
    public void addLike(Long pId, Long cId){
        postSessionLocal.addLike(pId, cId);
    }
    
    public void addComment(Long pId, Long cId){
        postSessionLocal.addComment(commentText, pId, cId);
    }
    
    public void deletePost(Long pId){
        postSessionLocal.deletePost(pId);
    }
    
    public void editPost(){
        Post p = currentPost;
        p.setTitle(newsTitle);
        p.setDescription(newsDescription);
        postSessionLocal.editPost(p);
    }
    
    
    public void unlikePost(Long pId, Long cId){
        postSessionLocal.unlikePost(pId, cId);
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

    public Post getCurrentPost() {
        return currentPost;
    }

    public void setCurrentPost(Post currentPost) {
        this.currentPost = currentPost;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

}
