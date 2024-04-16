/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package managedbean;

import entity.Comment;
import entity.Customer;
import entity.Post;
import java.io.IOException;
import java.io.InputStream;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
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
    
    private String filterCategory;
    private List<Post> searchResults;
    
    private Part uploadedfile;
    private String filename = "";

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
            p.setRegion(c.getRegion());
            postSessionLocal.createPost(p, c.getId());
            FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Post successfully uploaded", null));
            HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
            HttpSession session = request.getSession();
            Long postId = p.getId();
            session.setAttribute("postId", postId);
            return "/forum.xhtml?faces-redirect=true";
        }

    }
    
    public List<Post> filterRegion(List<Post> postList, String region){
        List<Post> results = new ArrayList<>();
        for(Post p : postList){
            if(p.getRegion().equals(region)){
                results.add(p);
            }
        }
        return results;
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
    
    public void searchPosts() throws ParseException {
        init();
    }

    @PostConstruct
    public void init() {
        List<Post> results = new ArrayList<>();
        if(filterCategory!=null){
            results = postSessionLocal.getPostsByCat(filterCategory);
        }else{
            results = getAllPosts();
        }
       
        searchResults = results;
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
        loadSelectedPost();
    }
    
    public void addComment(Long pId, Long cId){
        postSessionLocal.addComment(commentText, pId, cId);
        loadSelectedPost();
    }
    
    public List<Comment> getReversedComments(Post p) {
        List<Comment> reversedComments = p.getComments(); // Make a copy of the original list
        Collections.reverse(reversedComments); // Reverse the order of comments
        return reversedComments;
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
    
    public String upload(Long pId) throws IOException, error.NoResultException {
        ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        FacesContext context = FacesContext.getCurrentInstance();

        //get the deployment path
        String UPLOAD_DIRECTORY = ctx.getRealPath("/") + "upload/";
        System.out.println("#UPLOAD_DIRECTORY : " + UPLOAD_DIRECTORY);

        if (uploadedfile != null) {
            setFilename(Paths.get(uploadedfile.getSubmittedFileName()).getFileName().toString());
            System.out.println("filename: " + getFilename());
            //---------------------
            postSessionLocal.setProfilePicFile(pId, getFilename());
            //replace existing file
            Path path = Paths.get(UPLOAD_DIRECTORY + getFilename());
            InputStream bytes = uploadedfile.getInputStream();
            Files.copy(bytes, path, StandardCopyOption.REPLACE_EXISTING);
        } else {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Please upload a valid file", null);
            context.addMessage(null, message);
        }
        return null;
        //debug purposes

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

    public String getFilterCategory() {
        return filterCategory;
    }

    public void setFilterCategory(String filterCategory) {
        this.filterCategory = filterCategory;
    }

    public List<Post> getSearchResults() {
        return searchResults;
    }

    public void setSearchResults(List<Post> searchResults) {
        this.searchResults = searchResults;
    }

    public Part getUploadedfile() {
        return uploadedfile;
    }

    public void setUploadedfile(Part uploadedfile) {
        this.uploadedfile = uploadedfile;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

}
