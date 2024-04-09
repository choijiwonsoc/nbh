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

    public List<Post> getAllPostsOrderedByDate();

    public void addLike(Long pId, Long cId);

    public Post getPost(Long pId) throws NoResultException;

    public void addComment(String text, Long pId, Long cId);

    public boolean isLiked(Long pId, Long cId);

    public void deletePost(Long pId);

    public void unlikePost(Long pId, Long cId);

    public void editPost(Post p);

    public List<Post> getPostsByCat(String filterCategory);
    
}
