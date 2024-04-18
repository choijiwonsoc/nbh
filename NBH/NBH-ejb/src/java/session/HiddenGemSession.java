/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package session;

import entity.Customer;
import entity.HiddenGem;
import entity.HiddenGemReview;
import entity.Post;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author macbook
 */
@Stateless
public class HiddenGemSession implements HiddenGemSessionLocal {

    @PersistenceContext
    private EntityManager em;

    @Override
    public void addHiddenGem(HiddenGem hg, HiddenGemReview hgr, Long cId) {
        Query q1 = em.createQuery("SELECT hg FROM HiddenGem hg WHERE hg.postalCode =:hgPostalCode");
        q1.setParameter("hgPostalCode", hg.getPostalCode());

        List<HiddenGem> resultList = q1.getResultList();

        if (resultList.isEmpty()) {
            em.persist(hg);
            hgr.setHiddenGem(hg);
            hg.getReviews().add(hgr);
        } else {
            HiddenGem exisitingHg = resultList.get(0);
            hgr.setHiddenGem(exisitingHg);
            exisitingHg.getReviews().add(hgr);
        }

        Customer c = em.find(Customer.class, cId);
        hgr.setCustomer(c);
        c.getHiddenGemReviews().add(hgr);
    }

    @Override
    public void addHiddenGemReview(Long hgId, Long cId, HiddenGemReview hgr) {
        Query q1 = em.createQuery("SELECT hg FROM HiddenGem hg WHERE hg.id =:hgId");
        q1.setParameter("hgId", hgId);
        HiddenGem exisitingHg = (HiddenGem) q1.getSingleResult();
        hgr.setHiddenGem(exisitingHg);
        exisitingHg.getReviews().add(hgr);

        Customer c = em.find(Customer.class, cId);
        hgr.setCustomer(c);

        c.getHiddenGemReviews().add(hgr);
    }

    @Override
    public List<HiddenGem> searchHiddenGemsByPostalCode(String postalCode) {
        Query q;
        if (postalCode != null) {
            q = em.createQuery("SELECT hg FROM HiddenGem hg WHERE hg.postalCode LIKE :postalCode");
            q.setParameter("postalCode", "%" + postalCode + "%");
        } else {
            q = em.createQuery("SELECT hg FROM HiddenGem hg");
        }

        return q.getResultList();
    }

    @Override
    public List<HiddenGem> searchHiddenGemsByPlace(String placeName) {
        Query q;
        if (placeName != null) {
            q = em.createQuery("SELECT hg FROM HiddenGem hg WHERE LOWER(hg.placeName) LIKE :name");
            q.setParameter("name", "%" + placeName.toLowerCase() + "%");
        } else {
            q = em.createQuery("SELECT hg FROM HiddenGem hg");
        }
        return q.getResultList();
    }

    @Override
    public List<HiddenGem> filterHiddenGemsByDistricts(List<HiddenGem> searchResults, String[] districts) {
        List<HiddenGem> hiddenGems = new ArrayList<>();

        if (districts != null) {
            for (HiddenGem searchResult : searchResults) {
                if (Arrays.asList(districts).contains(searchResult.getDistrict())) {
                    hiddenGems.add(searchResult);
                }
            }
        } else {
            return searchResults;
        }

        return hiddenGems;
    }

    @Override
    public HiddenGem getHiddenGem(Long hgId) {
        HiddenGem hg = em.find(HiddenGem.class, hgId);
        return hg;
    }

    @Override
    public List<HiddenGemReview> getAllReviews(Long hgId) {
        Query q = em.createQuery("SELECT hgr FROM HiddenGemReview hgr WHERE hgr.hiddenGem.id =:hgId");
        q.setParameter("hgId", hgId);
        return q.getResultList();
    }

    @Override
    public List<String> getAllPostalCodes() {
        List<String> postalCodes = new ArrayList<>();
        Query q = em.createQuery("SELECT hg FROM HiddenGem hg");
        List<HiddenGem> hiddenGems = q.getResultList();
        for (HiddenGem hg : hiddenGems) {
            postalCodes.add(hg.getPostalCode());
        }
        return postalCodes;
    }
}

// @Override
// public List<HiddenGem> getAllHiddenGemsOrderedByPopularity() {
// List<String> postalCodes = em.createQuery(
// "SELECT hg.postalCode, COUNT(hg.postalCode) "
// + "FROM HiddenGem hg "
// + "GROUP BY hg.postalCode "
// + "ORDER BY COUNT(hg.postalCode) DESC").getResultList();
//
// List<HiddenGem> hiddenGemsOrderedByPopularity = new ArrayList<>();
// for (String postalCode : postalCodes) {
// Query q;
// q = em.createQuery("SELECT hg FROM HiddenGem hg WHERE hg.postalCode
// =:postalCode");
// q.getSingleResult()
// hiddenGemsGroupedByPostalCode.addAll(gemsForPostalCode);
// }
//
// }
