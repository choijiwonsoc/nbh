/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package session;

import entity.Comment;
import entity.Customer;
import entity.Post;
import java.awt.Event;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

/**
 *
 * @author choijiwon
 */
@Stateless
public class PostSession implements PostSessionLocal {

    @EJB(name = "CustomerSessionLocal")
    private CustomerSessionLocal customerSessionLocal;

    @PersistenceContext
    private EntityManager em;

    @Override
    public void createPost(Post p, Long cId) {
        Customer c = em.find(Customer.class, cId);
        p.setCustomer(c);
        c.getPosts().add(p); // Add the event to the list of organised events for the customer
        em.persist(p); //To change body of generated methods, choose Tools | Templates.
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @Override
    public List<Post> getAllPostsOrderedByDate() {
        List<Post> postList = em.createQuery("SELECT p FROM Post p", Post.class)
                .getResultList();
        Collections.reverse(postList);
        return postList;

    }

    @Override
    public void addLike(Long pId, Long cId) {
        Customer c = em.find(Customer.class, cId);
        Post p = em.find(Post.class, pId);
        if (!c.getLikedPosts().contains(p)) {
            p.setLikes(p.getLikes() + 1);
            c.getLikedPosts().add(p);
        }
    }

    @Override
    public boolean isLiked(Long pId, Long cId) {
        Customer c = em.find(Customer.class, cId);
        Post p = em.find(Post.class, pId);
        if (!c.getLikedPosts().contains(p)) {
            return false;
        } else {
            return true;
        }

    }

    @Override
    public Post getPost(Long pId) throws NoResultException {
        Post post = em.find(Post.class, pId);
        if (post != null) {
            return post;
        } else {
            throw new NoResultException("Customer not found");
        } //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addComment(String text, Long pId, Long cId) {
        Post post = em.find(Post.class, pId);
        Customer cust = em.find(Customer.class, cId);

        LocalDate currentDate = LocalDate.now();
        Date nowdate = java.sql.Date.valueOf(currentDate);
        Comment comment = new Comment();
        comment.setCommentDate(nowdate);
        comment.setCustomer(cust);
        comment.setText(text);
        em.persist(comment);
        post.getComments().add(comment);
    }

    @Override
    public void deletePost(Long pId) {
        Post post = em.find(Post.class, pId);
        Customer c = post.getCustomer();
        Customer realCust = em.find(Customer.class, c.getId());

        realCust.getPosts().remove(post);
        List<Customer> allCustomers = customerSessionLocal.getAllCustomers();
        for (Customer likedCust : allCustomers) {
            if (likedCust.getLikedPosts().contains(post)) {
                Customer realLikedCust = em.find(Customer.class, likedCust.getId());
                realLikedCust.getLikedPosts().remove(post);
            }
        }

        for (Comment comment : post.getComments()) {
            Comment realComment = em.find(Comment.class, comment.getId());
            em.remove(realComment);
        }
        post.getComments().clear();

        em.remove(post);

    }

    @Override
    public void editPost(Post p) {
        Post oldPost = em.find(Post.class, p.getId());

        oldPost.setTitle(p.getTitle());
        oldPost.setDescription(p.getDescription());

    }

    @Override
    public void unlikePost(Long pId, Long cId) {
        Post post = em.find(Post.class, pId);
        Customer cust = em.find(Customer.class, cId);

        cust.getLikedPosts().remove(post);
        post.setLikes(post.getLikes() - 1);

    }

    @Override
    public List<Post> getPostsByCat(String filterCategory) {
        String jpql = "SELECT p FROM Post p WHERE p.category = :filterCategory ORDER BY p.dateCreated DESC";
        return em.createQuery(jpql, Post.class)
                .setParameter("filterCategory", filterCategory)
                .getResultList();

    }
    
    @Override
    public void setProfilePicFile(Long pId, String fileName) {
        Post post = em.find(Post.class, pId);
        post.setFileName(fileName);
    }

}
