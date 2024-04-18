/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionLocal.java to edit this template
 */
package session;

import entity.HiddenGem;
import entity.HiddenGemReview;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author macbook
 */
@Local
public interface HiddenGemSessionLocal {

    public void addHiddenGem(HiddenGem hg, HiddenGemReview hgr, Long cId);

    public List<HiddenGem> searchHiddenGemsByPlace(String placeName);

    public List<HiddenGem> searchHiddenGemsByPostalCode(String postalCode);

    public List<HiddenGem> filterHiddenGemsByDistricts(List<HiddenGem> searchResults, String[] districts);

    public HiddenGem getHiddenGem(Long hgId);

    public List<HiddenGemReview> getAllReviews(Long hgId);

    public List<String> getAllPostalCodes();

    public void addHiddenGemReview(Long hgId, Long cId, HiddenGemReview hgr);

}
