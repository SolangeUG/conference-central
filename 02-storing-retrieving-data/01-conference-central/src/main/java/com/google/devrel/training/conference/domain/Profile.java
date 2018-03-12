package com.google.devrel.training.conference.domain;

import com.google.devrel.training.conference.form.ProfileForm.TeeShirtSize;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

/**
 * This class represents a Profile entity.
 * @author Solange U. Gasengayire
 */
@Entity
public class Profile {
	// COMPLETED indicate that this class is an Entity

	private String displayName;
	private String mainEmail;
	private TeeShirtSize teeShirtSize;

	// COMPLETED indicate that the userId is to be used in the Entity's key
	@Id
	private String userId;
    
    /**
     * Public constructor for Profile.
     * @param userId The user id, obtained from the email
     * @param displayName Any string user wants us to display him/her on this system.
     * @param mainEmail User's main e-mail address.
     * @param teeShirtSize The User's tee shirt size
     */
    public Profile (String userId, String displayName, String mainEmail, TeeShirtSize teeShirtSize) {
    	this.userId = userId;
    	this.displayName = displayName;
    	this.mainEmail = mainEmail;
    	this.teeShirtSize = teeShirtSize;
    }

	/**
	 * Return the display name associated with this profile
	 * @return display name
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * Return the main email address associated with this profile
	 * @return the main email address
	 */
	public String getMainEmail() {
		return mainEmail;
	}

	/**
	 * Return the tee-shirt size associated with this profile
	 * @return the shirt size
	 */
	public TeeShirtSize getTeeShirtSize() {
		return teeShirtSize;
	}

	/**
	 * Return the user ID associated with this profile
	 * @return the userID
	 */
	public String getUserId() {
		return userId;
	}

	/**
     * A private default constructor.
     */
    private Profile() {}

}
