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

    @PostConstruct
    public void init() {
        allListings = exchangeListingSessionLocal.getAllListing(null); //all listings available

        skills = skillSessionLocal.getAllSkillsByCustomer(null); //Display all the skills
        neededSkills = new ArrayList<Skill>(); // Initialize the list
        neededSkillIds = new ArrayList<Long>();
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

    public void submit() {
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

        /* List<Long> skillIds = new ArrayList<Long>();
        for (Skill s : neededSkills) {
            skillIds.add(s.getId());
        }
         */
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

    // Getter and Setter
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

}
