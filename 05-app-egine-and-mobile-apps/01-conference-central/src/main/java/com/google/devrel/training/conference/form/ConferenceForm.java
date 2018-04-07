package com.google.devrel.training.conference.form;

import com.google.common.collect.ImmutableList;

import java.util.Date;
import java.util.List;

/**
 * A simple Java object (POJO) representing a Conference form sent from the client.
 */
public class ConferenceForm {
    /**
     * The name of the conference.
     */
    private String name;

    /**
     * The description of the conference.
     */
    private String description;

    /**
     * Topics that are discussed in this conference.
     */
    private List<String> topics;

    /**
     * The city where the conference will take place.
     */
    private String city;

    /**
     * The start date of the conference.
     */
    private Date startDate;

    /**
     * The end date of the conference.
     */
    private Date endDate;

    /**
     * The capacity of the conference.
     */
    private int maxAttendees;

    private ConferenceForm() {}

    /**
     * Public constructor is solely for Unit Test.
     * @param name conference name
     * @param description conference description
     * @param topics conference topics
     * @param city conference city
     * @param startDate conference start date
     * @param endDate conference end date
     * @param maxAttendees maximum number of attendees
     */
    public ConferenceForm(String name, String description, List<String> topics, String city,
                          Date startDate, Date endDate, int maxAttendees) {
        this.name = name;
        this.description = description;
        this.topics = topics == null ? null : ImmutableList.copyOf(topics);
        this.city = city;
        this.startDate = startDate == null ? null : new Date(startDate.getTime());
        this.endDate = endDate == null ? null : new Date(endDate.getTime());
        this.maxAttendees = maxAttendees;
    }

    /**
     * Return the name on the conference form
     * @return conference name
     */
    public String getName() {
        return name;
    }

    /**
     * Return the description on the conference form
     * @return conference description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Return the topics on the conference form
     * @return conference topics
     */
    public List<String> getTopics() {
        return topics;
    }

    /**
     * Return the city on the conference form
     * @return conference city
     */
    public String getCity() {
        return city;
    }

    /**
     * Return the start date on the conference form
     * @return conference start date
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * Return the end date on the conference form
     * @return conference end date
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * Return the maximum number of attendees
     * @return conference capacity
     */
    public int getMaxAttendees() {
        return maxAttendees;
    }
}
