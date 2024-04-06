/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionLocal.java to edit this template
 */
package session;

import entity.Post;
import java.util.List;
import javax.ejb.Local;
import javax.persistence.NoResultException;

/**
 *
 * @author choijiwon
 */
@Local
public interface PostSessionLocal {

    public void createPost(Post p, Long cId);

    public List<Post> getAllPostsOrderedByDate(String category);

    public void addLike(Long pId, Long cId);

    public Post getPost(Long pId) throws NoResultException;
    
}
