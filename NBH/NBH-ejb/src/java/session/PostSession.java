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
import java.util.Date;
import java.util.List;
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
    public List<Post> getAllPostsOrderedByDate(String category) {
        return em.createQuery("SELECT p FROM Post p WHERE p.category = :category ORDER BY p.dateCreated DESC", Post.class)
                .setParameter("category", category)
                .getResultList();

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
    public Post getPost(Long pId) throws NoResultException {
        Post post = em.find(Post.class, pId);
        if (post != null) {
            return post;
        } else {
            throw new NoResultException("Customer not found");
        } //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public void addComment(String text, Long pId, Long cId){
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
    
}
