/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package managedbean;

import entity.Customer;
import entity.ExchangeListing;
import entity.Skill;
import error.NoResultException;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import session.ExchangeListingSessionLocal;
import session.SkillSessionLocal;

/**
 *
 * @author ninja
 */
@Named(value = "exchangeListingManagedBean")
@ViewScoped
public class ExchangeListingManagedBean implements Serializable {

    @EJB(name = "SkillSessionLocal")
    private SkillSessionLocal skillSessionLocal;

    @EJB(name = "ExchangeListingSessionLocal")
    private ExchangeListingSessionLocal exchangeListingSessionLocal;

    /**
     * Creates a new instance of ExchangeListingManagedBean
     */
    public ExchangeListingManagedBean() {
    }

    private List<ExchangeListing> allListings;
    private List<Skill> skills; // All skills
    private List<Skill> neededSkills; //skills customer need in listing
    private List<Long> neededSkillIds;

    private String title;
    private String description;
    private Date startDateTime;
    private Date endDateTime;
    private Date postedDateTime;
    private String listingType;
    private String status;
    private Boolean visibility;

    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;

    private ExchangeListing currentExchangeListing;

    @PostConstruct
    public void init() {
        allListings = exchangeListingSessionLocal.getAllListing(null); //all listings available

        skills = skillSessionLocal.getAllSkillsByCustomer(null); //Display all the skills
        neededSkills = new ArrayList<Skill>(); // Initialize the list
        neededSkillIds = new ArrayList<Long>();

        String viewId = FacesContext.getCurrentInstance().getViewRoot().getViewId();
        System.out.println("viewId is: " + viewId);
        if ("/secret/addExchangeListing.xhtml".equals(viewId)) {
            // use substring or .contains in string url attached at back
            //Long listingId = (Long) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("listingId");
            String listingId = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("listingId");
            if (listingId != null) {
                Long id = Long.parseLong(listingId);
                currentExchangeListing = exchangeListingSessionLocal.getListing(id); // Load the listing based on ID
            }
            // Check if in edit mode, otherwise stay in add mode with a new instance
            if (this.currentExchangeListing == null) {
                // add mode
                System.out.println("INIT: no current exchange Listing");
                this.currentExchangeListing = new ExchangeListing();
            } else {
                // edit mode
                System.out.println("INIT: currentListing exists, title" + currentExchangeListing.getTitle());
                for (Skill skill : currentExchangeListing.getSkills()) {
                    this.neededSkillIds.add(skill.getId());
                    System.out.println("Skills added" + skill.getSkillName());
                }
            }
        } else if ("/secret/viewExchangeListing.xhtml".equals(viewId)) {
            // viewExchangeListing
            FacesContext context = FacesContext.getCurrentInstance();
            Map<String, Object> flash = context.getExternalContext().getFlash();
            Long listingId = (Long) flash.get("listingId");
            System.out.println("Listing id for view is: " + listingId);
            if (listingId != null) {
                currentExchangeListing = exchangeListingSessionLocal.getListing(listingId); // Load the listing based on ID
                System.out.println("currentExchangeListing is :" + currentExchangeListing.getTitle());
                for (Skill skill : currentExchangeListing.getSkills()) {
                    this.neededSkills.add(skill);
                    System.out.println("Skills added" + skill.getSkillName());
                }
            } else {
                System.out.println("Listing ID is null viewExchangeListing");
            }
        }

    }

    public String navigateToViewExchangeListing(ExchangeListing listing) {
        // timeExchange.xhtml to viewExchangeListing.xhtml
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("listingId", listing.getId());
        System.out.println("changing to view Listing: " + listing.getId());
        return "viewExchangeListing.xhtml?faces-redirect=true";
    }

    // delete - either set to inactive or delete method in sessionbean
    public void cancelListing(ExchangeListing listing) {
        listing.setStatus("CANCELLED");
        listing.setVisibility(false);
        exchangeListingSessionLocal.updateListing(listing, neededSkillIds);
    }

    public void convertStringsToDate(String dateString, String startTimeString, String endTimeString) {
        // Define the formatters if your date and time are in specific formats
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        // Parse the date and time strings
        LocalDate date = LocalDate.parse(dateString, dateFormatter);
        LocalTime startTime = LocalTime.parse(startTimeString, timeFormatter);
        LocalTime endTime = LocalTime.parse(endTimeString, timeFormatter);

        // Combine date and time to form LocalDateTime
        LocalDateTime startLdt = LocalDateTime.of(date, startTime);
        LocalDateTime endLdt = LocalDateTime.of(date, endTime);

        // Convert LocalDateTime to Date
        this.startDateTime = Date.from(startLdt.atZone(ZoneId.systemDefault()).toInstant());
        this.endDateTime = Date.from(endLdt.atZone(ZoneId.systemDefault()).toInstant());
    }

    public String navigateToEditExchangeListing(ExchangeListing listing) {
        FacesContext context = FacesContext.getCurrentInstance();
        this.currentExchangeListing = listing;
        System.out.println("Navigate method: currentExchangeListing: " + currentExchangeListing.getTitle());
        if (currentExchangeListing.getOffers().isEmpty()) {
            // pass the id as a parameter
            return "addExchangeListing.xhtml?faces-redirect=true&listingId=" + currentExchangeListing.getId();
            /*
            FacesContext.getCurrentInstance().getExternalContext().getFlash().put("listingId", listing.getId());
            System.out.println("changing to add Listing: " + listing.getId());
            return "addExchangeListing.xhtml?faces-redirect=true";
             */
        } else {

            //context.getExternalContext().getFlash().setKeepMessages(true);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "There are offers existing!"));
            return null;  // Redirect or navigation logic...

        }

    }

    /*
    public void addExchangeListing() {
        FacesContext context = FacesContext.getCurrentInstance();

        Map<String, String> params = context.getExternalContext().getRequestParameterMap();

        String date = params.get("date");
        String startTime = params.get("startTime");
        String endTime = params.get("endTime");
        this.description = params.get("message");

        System.out.println("date is " + date);
        System.out.println("StartTime: " + startTime);
        System.out.println("EndTime: " + endTime);
        System.out.println("description is " + description);

        convertStringsToDate(date, startTime, endTime);

        System.out.println("Title is " + title);
        System.out.println("Start DateTime: " + startDateTime);
        System.out.println("End DateTime: " + endDateTime);
        System.out.println("description is " + description);

        ExchangeListing el = new ExchangeListing();
        el.setTitle(title);
        el.setStartDateTime(startDateTime);
        el.setEndDateTime(endDateTime);
        el.setDescription(description);
        el.setPostedDateTime(new Date());
        el.setListingType("SINGLE");
        el.setStatus("ACTIVE");
        el.setVisibility(true);


        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        HttpSession session = request.getSession();
        long userId = (Long) session.getAttribute("userId");
        for (Long s : neededSkillIds) {
            System.out.print("Id of skill is " + s);
            try {
                Skill skill = skillSessionLocal.getSkill(s);
                System.out.println("skill name is " + skill.getSkillName());
            } catch (NoResultException ex) {
                Logger.getLogger(ExchangeListingManagedBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        try {
            exchangeListingSessionLocal.createListing(el, userId, neededSkillIds);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Your submission was successful."));

        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Unable to submit"));

        }
    }
     */
    public void addExchangeListing() {
        FacesContext context = FacesContext.getCurrentInstance();

        try {
            ExchangeListing el = new ExchangeListing();
            el.setTitle(currentExchangeListing.getTitle());
            el.setStartDateTime(currentExchangeListing.getStartDateTime());
            el.setEndDateTime(currentExchangeListing.getEndDateTime());
            el.setDescription(currentExchangeListing.getDescription());
            el.setPostedDateTime(new Date()); // Or use your business logic to set this
            el.setListingType("SINGLE"); // Use actual values from your form if necessary
            el.setStatus("ACTIVE"); // Use actual values from your form if necessary
            el.setVisibility(true); // Use actual values from your form if necessary

            for (Long s : neededSkillIds) {
                System.out.print("Id of skill is " + s);
                try {
                    Skill skill = skillSessionLocal.getSkill(s);
                    System.out.println("skill name is " + skill.getSkillName());
                } catch (NoResultException ex) {
                    Logger.getLogger(ExchangeListingManagedBean.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
            HttpSession session = request.getSession();
            long userId = (Long) session.getAttribute("userId");

            exchangeListingSessionLocal.createListing(el, userId, neededSkillIds);

            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Listing added successfully."));
            // Redirect to another page or update the view as necessary
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Unable to add the listing."));
            e.printStackTrace(); // Log the exception for debugging
        }
    }

    public void editExchangeListing() {
        FacesContext context = FacesContext.getCurrentInstance();

        try {
            ExchangeListing el = currentExchangeListing;
            el.setTitle(currentExchangeListing.getTitle());
            System.out.println("after edit: title is " + el.getTitle());
            el.setStartDateTime(currentExchangeListing.getStartDateTime());
            System.out.println("IN EDIT METHOD: CEL SDT is : " + currentExchangeListing.getStartDateTime());
            System.out.println("IN EDIT METHOD: CEL EDT is : " + currentExchangeListing.getEndDateTime());
            el.setEndDateTime(currentExchangeListing.getEndDateTime());
            el.setDescription(currentExchangeListing.getDescription());
            el.setPostedDateTime(new Date()); // Or use your business logic to set this
            el.setListingType("SINGLE"); // Use actual values from your form if necessary
            el.setStatus("ACTIVE"); // Use actual values from your form if necessary
            el.setVisibility(true); // Use actual values from your form if necessary

            for (Long s : neededSkillIds) {
                System.out.print("Id of skill is " + s);
                try {
                    Skill skill = skillSessionLocal.getSkill(s);
                    System.out.println("skill name is " + skill.getSkillName());
                } catch (NoResultException ex) {
                    Logger.getLogger(ExchangeListingManagedBean.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            System.out.println("Updating listing " + el.getTitle());
            exchangeListingSessionLocal.updateListing(el, neededSkillIds);

            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Listing updated."));
            // Redirect to another page or update the view as necessary
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Unable to update listing."));
            e.printStackTrace(); // Log the exception for debugging
        }
    }

    public String submit() {
        if (currentExchangeListing.getId() == null) {
            // add
            addExchangeListing();
        } else {
            // handle update
            editExchangeListing();

        }
        // Redirect or refresh as needed
        return "myExchangeListing.xhtml?faces-redirect=true";
    }

    // Getter and Setter
    public String getFormattedEndDateTime() {
        /*
        * Note :  input still bound to this value="#{exchangeListingManagedBean.currentExchangeListing.endDateTime}"
            just that it needs to format to allow the datetime to be properly displayed.
         */
        if (currentExchangeListing.getEndDateTime() != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
            return dateFormat.format(currentExchangeListing.getEndDateTime());
        }
        return null; // or a default value
    }

    public void setFormattedEndDateTime(String dateString) {
        if (dateString != null && !dateString.isEmpty()) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
            try {
                Date parsedDate = dateFormat.parse(dateString);
                this.endDateTime = parsedDate;
                System.out.println("Changed EndDT: " + endDateTime);
                currentExchangeListing.setEndDateTime(endDateTime);
                System.out.println("CurrentEL after changing EDT is: " + currentExchangeListing.getEndDateTime());
            } catch (ParseException e) {
                // Handle the exception (e.g., log it and set startDateTime to a default value or null)
            }
        } else {
            this.startDateTime = null; // or a default value
        }
    }

    public String getFormattedStartDateTime() {
        if (currentExchangeListing.getStartDateTime() != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
            String startDateTime = dateFormat.format(currentExchangeListing.getStartDateTime());
            System.out.println("StartDateTime is " + startDateTime);
            return startDateTime;
        }
        return null; // or a default value
    }

    public void setFormattedStartDateTime(String dateString) {
        if (dateString != null && !dateString.isEmpty()) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
            try {
                Date parsedDate = dateFormat.parse(dateString);
                this.startDateTime = parsedDate;
                System.out.println("Changed StartDT: " + startDateTime);
                currentExchangeListing.setStartDateTime(startDateTime);
                System.out.println("CurrentEL after changing SDT is: " + currentExchangeListing.getStartDateTime());
            } catch (ParseException e) {
                // Handle the exception (e.g., log it and set startDateTime to a default value or null)
            }
        } else {
            this.startDateTime = null; // or a default value
        }
    }

    /*
    // NOTE: Not used
    // Ensure you have a setter that can handle the date-time string and parse it back to a Date object
    public void setEndDateTime(String dateTimeStr) {
        FacesContext context = FacesContext.getCurrentInstance();

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
            currentExchangeListing.setEndDateTime(dateFormat.parse(dateTimeStr));
        } catch (Exception e) {
            // Handle parsing error
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Unable to set."));

        }
    }

    public void setStartDateTime(String dateTimeStr) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
            currentExchangeListing.setStartDateTime(dateFormat.parse(dateTimeStr));
        } catch (Exception e) {
            // Handle parsing error
        }
    }
     */
    public List<ExchangeListing> getAllListings() {
        return allListings;
    }

    public void setAllListings(List<ExchangeListing> allListings) {
        this.allListings = allListings;
    }

    public ExchangeListingSessionLocal getExchangeListingSessionLocal() {
        return exchangeListingSessionLocal;
    }

    public void setExchangeListingSessionLocal(ExchangeListingSessionLocal exchangeListingSessionLocal) {
        this.exchangeListingSessionLocal = exchangeListingSessionLocal;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(Date startDateTime) {
        this.startDateTime = startDateTime;
    }

    public Date getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(Date endDateTime) {
        this.endDateTime = endDateTime;
    }

    public Date getPostedDateTime() {
        return postedDateTime;
    }

    public void setPostedDateTime(Date postedDateTime) {
        this.postedDateTime = postedDateTime;
    }

    public String getListingType() {
        return listingType;
    }

    public void setListingType(String listingType) {
        this.listingType = listingType;
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

    public SkillSessionLocal getSkillSessionLocal() {
        return skillSessionLocal;
    }

    public void setSkillSessionLocal(SkillSessionLocal skillSessionLocal) {
        this.skillSessionLocal = skillSessionLocal;
    }

    public List<Skill> getSkills() {
        return skills;
    }

    public void setSkills(List<Skill> skills) {
        this.skills = skills;
    }

    public List<Skill> getNeededSkills() {
        return neededSkills;
    }

    public void setNeededSkills(List<Skill> neededSkills) {
        this.neededSkills = neededSkills;
    }

    public List<Long> getNeededSkillIds() {
        return neededSkillIds;
    }

    public void setNeededSkillIds(List<Long> neededSkillIds) {
        this.neededSkillIds = neededSkillIds;
    }

    public ExchangeListing getCurrentExchangeListing() {
        return currentExchangeListing;
    }

    public void setCurrentExchangeListing(ExchangeListing currentExchangeListing) {
        this.currentExchangeListing = currentExchangeListing;
    }

}