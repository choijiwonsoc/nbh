/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package managedbean;

import entity.Customer;
import entity.Post;
import entity.ServiceProviderListing;
import java.io.IOException;
import java.io.InputStream;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.persistence.NoResultException;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import session.CustomerSessionLocal;
import session.PostSessionLocal;

/**
 *
 * @author choijiwon
 */
@Named(value = "customerManagedBean")
@ViewScoped
public class CustomerManagedBean implements Serializable {

    @EJB(name = "PostSessionLocal")
    private PostSessionLocal postSessionLocal;

    @EJB(name = "CustomerSessionLocal")
    private CustomerSessionLocal customerSessionLocal;

    private String username;
    private String password;

    private Long id;
    private String name;
    private String contact;
    private String email;
    private String district;
    private String region;

    Customer selectedCustomer;

    private Part uploadedfile;
    private String filename = "";

    /**
     * Creates a new instance of CustomerManagedBean
     */
    public CustomerManagedBean() {
    }

    public String login() {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        HttpSession session = request.getSession();
        FacesContext context = FacesContext.getCurrentInstance();
        if (customerSessionLocal.getAllUsername().contains(username)) {
            //login successful
            Customer cust = customerSessionLocal.getCustByUsername(username);
            if (password.equals(cust.getPassword())) {
                Long userId = cust.getId();
                session.setAttribute("userId", userId);
                FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Successfully login", null));
                return "/s/customerView_1.xhtml?faces-redirect=true";
            } else {
                //Long userId = new Long(-1);
                //session.setAttribute("userId", userId);
                username = null;
                password = null;
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Wrong username/password.", null);
                context.addMessage(null, message);
                return null;
            }

        } else {
            //Long userId = new Long(-1);
            //session.setAttribute("userId", userId);
            username = null;
            password = null;
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Wrong username/password.", null);
            context.addMessage(null, message);
            return null;
        }
    }

    public String logout() {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        HttpSession session = request.getSession();
        Long userId = new Long(-1);
        session.setAttribute("userId", userId);
        return "/index.xhtml?faces-redirect=true";
    }

    public String addCustomer(ActionEvent evt) throws IOException {
        Customer c = new Customer();
        if (name.length() == 0 || contact.length() == 0 || email.length() == 0 || password.length() == 0 || username.length() == 0) {

            FacesContext context = FacesContext.getCurrentInstance();
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "One or more fields not filled.", null);
            context.addMessage(null, message);
            return null;

        } else if (!customerSessionLocal.getAllUsername().contains(username)) {
            c.setName(name);
            c.setContact(contact);
            c.setDistrict(district);
            if ("Ang Mo Kio".equals(district) || "Yishun".equals(district) || "Sembawang".equals(district)) {
                c.setRegion("North");
            } else if ("Bedok".equals(district) || "Pasir Ris".equals(district) || "Tampines".equals(district)) {
                c.setRegion("East");
            } else if ("Bukit Batok".equals(district) || "Choa Chu Kang".equals(district) || "Jurong East".equals(district)) {
                c.setRegion("West");
            } else if ("Bishan".equals(district) || "Bukit Timah".equals(district) || "Toa Payoh".equals(district)) {
                c.setRegion("Central");
            } else {
                c.setRegion("South");
            }
            c.setEmail(email);
            c.setPassword(password);
            c.setUsername(username);
            customerSessionLocal.persist(c);
            HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
            HttpSession session = request.getSession();
            Long userId = c.getId();
            session.setAttribute("userId", userId);
        } else {
            FacesContext context = FacesContext.getCurrentInstance();
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Username already exists", null);
            context.addMessage(null, message);
            return null;
        }
        return "/s/addCustomerPhoto.xhtml?faces-redirect=true";

    }

    public String editCustomer(Long cId) {
        if (username.length() == 0 || contact.length() == 0 || email.length() == 0) {
            FacesContext context = FacesContext.getCurrentInstance();
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "One or more fields not filled", null);
            context.addMessage(null, message);
            return null;

        } else {
            Customer c = new Customer();
            String pw = selectedCustomer.getPassword();
            c.setId(cId);
            c.setUsername(username);
            if (password.length() > 0) {
                c.setPassword(password);
            } else {
                c.setPassword(pw);
            }

            c.setContact(contact);
            c.setEmail(email);
            customerSessionLocal.editCustomer(c);
            return "/viewProfile.xhtml?faces-redirect=true";
        }

    }

    public void loadSelectedCustomer() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        HttpSession session = request.getSession();
        try {
            long userId = (Long) session.getAttribute("userId");
            this.selectedCustomer
                    = customerSessionLocal.getCustomer(userId);
            id = this.selectedCustomer.getId();
            name = this.selectedCustomer.getName();
            contact = this.selectedCustomer.getContact();
            district = this.selectedCustomer.getDistrict();
            region = this.selectedCustomer.getPassword();
            email = this.selectedCustomer.getEmail();
            username = this.selectedCustomer.getUsername();
            password = this.selectedCustomer.getPassword();
            //List<ServiceProviderListing> businessListings = this.selectedCustomer.getServiceProviderListing();

        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Unable to load customer"));
        }
    }

    public String upload(Long cId) throws IOException, error.NoResultException {
        ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        FacesContext context = FacesContext.getCurrentInstance();

        //get the deployment path
        String UPLOAD_DIRECTORY = ctx.getRealPath("/") + "upload/";
        System.out.println("#UPLOAD_DIRECTORY : " + UPLOAD_DIRECTORY);

        if (uploadedfile != null) {
            setFilename(Paths.get(uploadedfile.getSubmittedFileName()).getFileName().toString());
            System.out.println("filename: " + getFilename());
            //---------------------
            customerSessionLocal.setProfilePicFile(cId, getFilename());
            //replace existing file
            Path path = Paths.get(UPLOAD_DIRECTORY + getFilename());
            InputStream bytes = uploadedfile.getInputStream();
            Files.copy(bytes, path, StandardCopyOption.REPLACE_EXISTING);
        } else {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Please upload a valid file", null);
            context.addMessage(null, message);
        }
        return null;
        //debug purposes

    }

    public List<Post> orderByDate(List<Post> postList) {
        Collections.reverse(postList);
        return postList;

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public Customer getSelectedCustomer() {
        return selectedCustomer;
    }

    public void setSelectedCustomer(Customer selectedCustomer) {
        this.selectedCustomer = selectedCustomer;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Part getUploadedfile() {
        return uploadedfile;
    }

    public void setUploadedfile(Part uploadedfile) {
        this.uploadedfile = uploadedfile;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

}
