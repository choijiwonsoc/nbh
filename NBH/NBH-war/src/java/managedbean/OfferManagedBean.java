/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package managedbean;

import entity.ExchangeListing;
import entity.Offer;
import entity.Skill;
import error.NoResultException;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import session.ExchangeListingSessionLocal;
import session.OfferSessionLocal;
import session.SkillSessionLocal;

/**
 *
 * @author ninja
 */
@Named(value = "offerManagedBean")
@ViewScoped
public class OfferManagedBean implements Serializable {

    @EJB(name = "OfferSessionLocal")
    private OfferSessionLocal offerSessionLocal;

    @EJB(name = "SkillSessionLocal")
    private SkillSessionLocal skillSessionLocal;

    @EJB(name = "ExchangeListingSessionLocal")
    private ExchangeListingSessionLocal exchangeListingSessionLocal;

    /**
     * Creates a new instance of OfferManagedBean
     */
    public OfferManagedBean() {
    }

    private List<Skill> skillsAvailableToChoose; //select skills from customer who made listing
    private List<Long> toExchangeSkills; //skills that current user choose, intended for lister to do

    private String description;
    private List<Date> dateOptions;
    private Date postedDateTime;
    private String status;
    private Boolean visibility;

    private Long exchangeListingId; // Id passed from viewExchangeListing.xhtml to addOffer
    private ExchangeListing currentExchangeListing;

    private List<Offer> offersMade;

    private List<Offer> availOffers;

    @PostConstruct
    public void init() {

        ///AddOffer.xhtml
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        HttpSession session = request.getSession();
        long userId = (Long) session.getAttribute("userId");
        skillsAvailableToChoose = skillSessionLocal.getAllSkillsByCustomer(userId);
        for (Skill s : skillsAvailableToChoose) {
            System.out.println("in INIT addOffer: Skills available " + s.getSkillName());
        }
        toExchangeSkills = new ArrayList<Long>();

        exchangeListingId = (Long) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("listingId");
        if (exchangeListingId == null) {
            // Handle the case where the listing is not passed correctly, maybe redirect back or show an error message
            System.out.println("exchangeListingID not passed from viewExchangeListing.xhtml");
        }
        if (exchangeListingId != null) {
            currentExchangeListing = exchangeListingSessionLocal.getListing(exchangeListingId);
            System.out.println("OFFERMB exchangeListing is " + currentExchangeListing.getTitle());
        } else {
            System.out.println("exchangeListingID not passed from viewExchangeListing.xhtml");
        }
        /// END AddOffer.xhtml

        /// myOffers.xhtml
        offersMade = offerSessionLocal.getAllOffers(userId, "C");

        /// availableOffers.xhtml
        //added in navigateToAvailableOffers in ExchangeListingManagedBean
        Long listingId = (Long) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("listingId");
        //String listingId = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("listingId"); // for url parameter
        if (listingId != null) {

            currentExchangeListing = exchangeListingSessionLocal.getListing(listingId); // Load the listing based on ID

            availOffers = offerSessionLocal.getAllOffers(listingId, "EL");
            for (Offer o : availOffers) {
                System.out.println("in INIT OFFERMB: o is" + o.getDescription());
            }
        }
    }

    public String getSkillsForOffer(Long oId) {
        List<Skill> skills = skillSessionLocal.getAllSkillsByOffer(oId);
        if (skills.isEmpty()) {
            return "No skills specified";
        } else {
            return skills.stream().map(Skill::getSkillName).collect(Collectors.joining(", "));
        }
    }

    public String getTitleOfListing(Long oId) {
        // myOffers.xhtml
        if (oId != null) {
            ExchangeListing listing = exchangeListingSessionLocal.getListingFromOffer(oId);
            return (listing != null) ? listing.getTitle() : "Listing not found";
        } else {
            return null;
        }
    }

    public String navigateToViewListing(Long oId) {
        // from myOffers.xhtml to viewExchangeListing.xhtml
        System.out.println("oId is:" + oId); //offerId
        ExchangeListing listing = exchangeListingSessionLocal.getListingFromOffer(oId);
        System.out.println("listing from myOffers.xhtml to viewExchangeListing.xhtml :" + listing.getId());
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("listingId", listing.getId());
        return "viewExchangeListing.xhtml?faces-redirect=true";
    }

    public void acceptOffer(Long oId) {
        // change status of this offer and update
        Offer offer = offerSessionLocal.getOffer(oId);
        offer.setStatus("ACCEPTED");
        offerSessionLocal.updateOffer(offer);

        // get rest of the offers for this exchange listing and change status of them and update
        for (Offer o : availOffers) {
            if (o.getId() != oId) {
                o.setStatus("ON-HOLD");
                offerSessionLocal.updateOffer(o);
            }
        }
        // set Listing to taken
        ExchangeListing listing = exchangeListingSessionLocal.getListingFromOffer(oId);
        listing.setStatus("TAKEN");
        List<Skill> currentSkills = listing.getSkills();
        List<Long> currentSId = new ArrayList<>();
        for (Skill s : currentSkills) {
            currentSId.add(s.getId());
        }
        exchangeListingSessionLocal.updateListing(listing, currentSId);

        //notify the user whos offer is accepted?
        //reflect the changes, update the availOffers list
        availOffers = offerSessionLocal.getAllOffers(listing.getId(), "EL");
    }

    public void rejectOffer(Long oId) {
        // Exchange Lister rejects the offer
        Offer offer = offerSessionLocal.getOffer(oId);
        if (offer.getStatus().equalsIgnoreCase("ACCEPTED")) {
            // set rest of the offers back to pending
            for (Offer o : availOffers) {
                if (o.getId() != oId) {
                    o.setStatus("PENDING");
                    offerSessionLocal.updateOffer(o);
                }
            }
        }

        offer.setStatus("DECLINED");
        offerSessionLocal.updateOffer(offer);

        //reflect the changes, update the availOffers list
        ExchangeListing listing = exchangeListingSessionLocal.getListingFromOffer(oId);
        listing.setStatus("ACTIVE");
        // retrive currentskills to continue for updateListing.
        List<Skill> currentSkills = listing.getSkills();
        List<Long> currentSId = new ArrayList<>();
        for (Skill s : currentSkills) {
            currentSId.add(s.getId());
        }
        exchangeListingSessionLocal.updateListing(listing, currentSId);
        availOffers = offerSessionLocal.getAllOffers(listing.getId(), "EL");
    }

    public void cancelOffer(Long oId) {
        // Person that made the offer deletes it on his own
        Offer offer = offerSessionLocal.getOffer(oId);
        if (offer.getStatus().equalsIgnoreCase("ACCEPTED")) {
            if (availOffers != null) {
                // set the rest of the offers back to on-hold
                for (Offer o : availOffers) {
                    if (o.getId() != oId) {
                        o.setStatus("ON-HOLD");
                        offerSessionLocal.updateOffer(o);
                    }
                }
            }

            // set exchangeListing status back to active
            ExchangeListing listing = exchangeListingSessionLocal.getListingFromOffer(oId);
            listing.setStatus("ACTIVE");
            // retrive currentskills to continue for updateListing.
            List<Skill> currentSkills = listing.getSkills();
            List<Long> currentSId = new ArrayList<>();
            for (Skill s : currentSkills) {
                currentSId.add(s.getId());
            }
            exchangeListingSessionLocal.updateListing(listing, currentSId);
        }
        offer.setStatus("CANCELLED");
        offerSessionLocal.updateOffer(offer);

        //reflect the changes, update the offersMade list
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        HttpSession session = request.getSession();
        long userId = (Long) session.getAttribute("userId");
        offersMade = offerSessionLocal.getAllOffers(userId, "C");
    }

    public String submit() {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        HttpSession session = request.getSession();
        long userId = (Long) session.getAttribute("userId");
        try {
            Offer o = new Offer();
            o.setDescription(description);
            o.setDateOptions(new ArrayList());
            o.setPostedDateTime(new Date());
            o.setStatus("PENDING");
            o.setVisibility(true);
            System.out.println("o.description " + o.getDescription());
            // just for debug,
            for (Long id : toExchangeSkills) {
                Skill s;
                try {
                    s = skillSessionLocal.getSkill(id);
                    System.out.println("add offer, skill exchange: " + s.getSkillName());
                } catch (NoResultException ex) {
                    Logger.getLogger(OfferManagedBean.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            System.out.println("submit offer: " + o);
            System.out.println("submit userId: " + userId);
            System.out.println("submit exchangeListingId: " + exchangeListingId);
            System.out.println("submit toExchangeSkills: " + toExchangeSkills);
            offerSessionLocal.createOffer(o, userId, exchangeListingId, toExchangeSkills);
            FacesMessage msg = new FacesMessage("Successful", "Offer created!");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (Exception e) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Unable to make this offer");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }

        return "myOffers.xhtml?faces-redirect=true";
    }

    // Getter and Setter
    public SkillSessionLocal getSkillSessionLocal() {
        return skillSessionLocal;
    }

    public void setSkillSessionLocal(SkillSessionLocal skillSessionLocal) {
        this.skillSessionLocal = skillSessionLocal;
    }

    public ExchangeListingSessionLocal getExchangeListingSessionLocal() {
        return exchangeListingSessionLocal;
    }

    public void setExchangeListingSessionLocal(ExchangeListingSessionLocal exchangeListingSessionLocal) {
        this.exchangeListingSessionLocal = exchangeListingSessionLocal;
    }

    public OfferSessionLocal getOfferSessionLocal() {
        return offerSessionLocal;
    }

    public void setOfferSessionLocal(OfferSessionLocal offerSessionLocal) {
        this.offerSessionLocal = offerSessionLocal;
    }

    public List<Skill> getSkillsAvailableToChoose() {
        return skillsAvailableToChoose;
    }

    public void setSkillsAvailableToChoose(List<Skill> skillsAvailableToChoose) {
        this.skillsAvailableToChoose = skillsAvailableToChoose;
    }

    public List<Long> getToExchangeSkills() {
        return toExchangeSkills;
    }

    public void setToExchangeSkills(List<Long> toExchangeSkills) {
        this.toExchangeSkills = toExchangeSkills;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Date> getDateOptions() {
        return dateOptions;
    }

    public void setDateOptions(List<Date> dateOptions) {
        this.dateOptions = dateOptions;
    }

    public Date getPostedDateTime() {
        return postedDateTime;
    }

    public void setPostedDateTime(Date postedDateTime) {
        this.postedDateTime = postedDateTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getVisibility() {
        return visibility;
    }

    public void setVisibility(Boolean visibility) {
        this.visibility = visibility;
    }

    public Long getExchangeListingId() {
        return exchangeListingId;
    }

    public void setExchangeListingId(Long exchangeListingId) {
        this.exchangeListingId = exchangeListingId;
    }

    public ExchangeListing getCurrentExchangeListing() {
        return currentExchangeListing;
    }

    public void setCurrentExchangeListing(ExchangeListing currentExchangeListing) {
        this.currentExchangeListing = currentExchangeListing;
    }

    public List<Offer> getOffersMade() {
        return offersMade;
    }

    public void setOffersMade(List<Offer> offersMade) {
        this.offersMade = offersMade;
    }

    public List<Offer> getAvailOffers() {
        return availOffers;
    }

    public void setAvailOffers(List<Offer> availOffers) {
        this.availOffers = availOffers;
    }

}
