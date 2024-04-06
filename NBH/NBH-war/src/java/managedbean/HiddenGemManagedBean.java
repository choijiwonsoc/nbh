/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package managedbean;

import entity.Customer;
import entity.HiddenGem;
import entity.HiddenGemReview;
import entity.Post;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import java.io.Serializable;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.NoResultException;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.primefaces.event.UnselectEvent;
import session.HiddenGemSessionLocal;

/**
 *
 * @author macbook
 */
@Named(value = "hiddenGemManagedBean")
@ViewScoped
public class HiddenGemManagedBean implements Serializable {

    @EJB
    private HiddenGemSessionLocal hiddenGemSessionLocal;

    private String placeName;
    private String postalCode;
    private String district;
    private String review;
    private Date created;

    private Customer selectedCustomer;
    private List<HiddenGem> hiddenGems;

    private String searchType = "PLACE";
    private String searchString;

    private List<String> districts;
    private String[] selectedDistricts;

    private Long hgId;
    private HiddenGem selectedHiddenGem;
    private List<HiddenGemReview> hiddenGemReviews;

    private List<String> postalCodes;

    public HiddenGemManagedBean() {
    }

    @PostConstruct
    public void init() {
        districts = new ArrayList<String>();
        districts.add("North");
        districts.add("South");
        districts.add("West");
        districts.add("East");
        districts.add("Central");

        if (searchString == null || searchString.equals("")) {
            hiddenGems = hiddenGemSessionLocal.searchHiddenGemsByPostalCode(null);
        } else {
            switch (searchType) {
                case "PLACE": {
                    hiddenGems = hiddenGemSessionLocal.searchHiddenGemsByPlace(searchString);
                    break;
                }
                case "POSTAL CODE":
                    hiddenGems = hiddenGemSessionLocal.searchHiddenGemsByPostalCode(searchString);
                    break;
            }
        }
    }

    public List<HiddenGem> handleSearch() {
        init();
        return this.hiddenGems;
    } //end handleSearch

    public String addHiddenGem(Customer c) throws NoResultException {
        FacesContext context = FacesContext.getCurrentInstance();
        HiddenGem hg = new HiddenGem();
        hg.setCreated(new Date());
        hg.setPlaceName(placeName);
        hg.setPostalCode(postalCode);
        hg.setDistrict(district);

        HiddenGemReview hgr = new HiddenGemReview();
        hgr.setCreated(new Date());
        hgr.setHiddenGem(hg);
        hgr.setReview(review);

        hiddenGemSessionLocal.addHiddenGem(hg, hgr, c.getId());
        FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Hidden Gem successfully added", null));
        return "/hiddenGems.xhtml?faces-redirect=true";
    }

    public void onItemUnselect(UnselectEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();

        FacesMessage msg = new FacesMessage();
        msg.setSummary("Item unselected: " + event.getObject().toString());
        msg.setSeverity(FacesMessage.SEVERITY_INFO);

        context.addMessage(null, msg);
    }

    public List<HiddenGem> filter() {
        init();
        hiddenGems = hiddenGemSessionLocal.filterHiddenGemsByDistricts(hiddenGems, selectedDistricts);
        return hiddenGems;
    }

    public void loadSelectedHiddenGem() {
        FacesContext context = FacesContext.getCurrentInstance();

        Map<String, String> params = context.getExternalContext()
                .getRequestParameterMap();
        String hgIdStr = params.get("hgId");
        this.hgId = Long.parseLong(hgIdStr);
        this.selectedHiddenGem = hiddenGemSessionLocal.getHiddenGem(hgId);
    }

    public void retrieveAllHiddenGemReviews() {
        this.hiddenGemReviews = hiddenGemSessionLocal.getAllReviews(hgId);
    }

    public void getAllPostalCodes() {
        this.postalCodes = hiddenGemSessionLocal.getAllPostalCodes();
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Customer getSelectedCustomer() {
        return selectedCustomer;
    }

    public void setSelectedCustomer(Customer selectedCustomer) {
        this.selectedCustomer = selectedCustomer;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public List<HiddenGem> getHiddenGems() {
        return hiddenGems;
    }

    public void setHiddenGems(List<HiddenGem> hiddenGems) {
        this.hiddenGems = hiddenGems;
    }

    public String getSearchType() {
        return searchType;
    }

    public void setSearchType(String searchType) {
        this.searchType = searchType;
    }

    public String getSearchString() {
        return searchString;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    public List<String> getDistricts() {
        return districts;
    }

    public void setDistricts(List<String> districts) {
        this.districts = districts;
    }

    public String[] getSelectedDistricts() {
        return selectedDistricts;
    }

    public void setSelectedDistricts(String[] selectedDistricts) {
        this.selectedDistricts = selectedDistricts;
    }

    public HiddenGem getSelectedHiddenGem() {
        return selectedHiddenGem;
    }

    public void setSelectedHiddenGem(HiddenGem selectedHiddenGem) {
        this.selectedHiddenGem = selectedHiddenGem;
    }

    public Long getHgId() {
        return hgId;
    }

    public void setHgId(Long hgId) {
        this.hgId = hgId;
    }

    public List<HiddenGemReview> getHiddenGemReviews() {
        return hiddenGemReviews;
    }

    public void setHiddenGemReviews(List<HiddenGemReview> hiddenGemReviews) {
        this.hiddenGemReviews = hiddenGemReviews;
    }

    public List<String> getPostalCodes() {
        return postalCodes;
    }

    public void setPostalCodes(List<String> postalCodes) {
        this.postalCodes = postalCodes;
    }

}
