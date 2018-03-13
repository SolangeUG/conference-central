package com.google.devrel.training.conference.form;

/**
 * A POJO representing a profile form on the client side.
 */
public class ProfileForm {
    /**
     * Any string user wants us to display him/her on this system.
     */
    private String displayName;

    /**
     * T shirt size.
     */
    private TeeShirtSize teeShirtSize;

    /**
     * No-argument constructor
     */
    private ProfileForm () {}

    /**
     * Constructor for ProfileForm, solely for unit test.
     * @param displayName A String for displaying the user on this system.
     * @param teeShirtSize the T shirt size
     */
    public ProfileForm(String displayName, TeeShirtSize teeShirtSize) {
        this.displayName = displayName;
        this.teeShirtSize = teeShirtSize;
    }

    /**
     * Return the display name
     * @return display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Return the T shirt size
     * @return T shirt size
     */
    public TeeShirtSize getTeeShirtSize() {
        return teeShirtSize;
    }

    /**
     * All known T shirt sizes
     */
    public static enum TeeShirtSize {
    	NOT_SPECIFIED,
        XS,
        S,
        M,
        L, 
        XL, 
        XXL,
        XXXL
    }
}
