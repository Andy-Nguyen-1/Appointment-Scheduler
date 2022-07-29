package model;

import java.time.LocalDateTime;

/**
 * Appointment model
 */
public class Appointment {
    private int apptId;
    private String title;
    private String description;
    private String location;
    private String contact;
    private String type;
    private String startDateTime;
    private String endDateTime;
    private int custId;
    private int userID;

    /**
     * Appointment Constructor
     */

    public Appointment(int apptId, String title, String description, String location, String contact, String type, String startDateTime, String endDateTime, int custId, int userID) {
        this.apptId = apptId;
        this.title = title;
        this.description = description;
        this.location = location;
        this.contact = contact;
        this.type = type;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.custId = custId;
        this.userID = userID;
    }

    /**
     * Getter to return appointment ID.
     * @return apptID
     */
    public int getApptId () {return apptId;}

    /**
     * Getter to return appointment title.
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Getter to return description.
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Getter to return appointment location.
     * @return location
     */
    public String getLocation() {
        return location;
    }

    /**
     * Getter to return appointment contact.
     * @return contact
     */
    public String getContact() {
        return contact;
    }

    /**
     * Getter to return appointment type.
     * @return type
     */
    public String getType() {
        return type;
    }

    /**
     * Getter to return appointment start timestamp.
     * @return startDateTime
     */
    public String getStartDateTime() {
        return startDateTime;
    }

    /**
     * Getter to return appointment end timestamp.
     * @return endDateTime
     */
    public String getEndDateTime() {
        return endDateTime;
    }

    /**
     * Getter to return appointment customer ID.
     * @return custID
     */
    public int getCustId() {
        return custId;
    }

    /**
     * Getter to return appointment customer ID.
     * @return userID
     */
    public int getUserID() {
        return userID;
    }

    /**
     * Function to convert a timestamp to a zoned time that calls a lambda.
     */
    public interface dateTimeLambda {
        LocalDateTime localDateTimeConverter(String dateTime);
    }

}


