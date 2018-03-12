package com.google.devrel.training.conference.spi;

import static com.google.devrel.training.conference.service.OfyService.ofy;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.users.User;
import com.google.devrel.training.conference.Constants;
import com.google.devrel.training.conference.domain.Profile;
import com.google.devrel.training.conference.form.ProfileForm;
import com.google.devrel.training.conference.form.ProfileForm.TeeShirtSize;
import com.googlecode.objectify.Key;

/**
 * This class defines the first steps of the conference app APIs.
 * @author Solange U. Gasengayire
 */
@Api(name = "conference",
		version = "v1",
		scopes = {Constants.EMAIL_SCOPE },
    	clientIds = {
			Constants.WEB_CLIENT_ID,
			Constants.API_EXPLORER_CLIENT_ID },
    	description = "API for the Conference Central Backend application.")
public class ConferenceApi {

	/**
     * Get the display name from the user's email.
	 * For example, if the email is lemoncake@example.com, then the display name becomes "lemoncake."
	 * @param email the email input
     * @return the display name
     */
	private static String extractDefaultDisplayNameFromEmail(String email) {
		return email == null ? null : email.substring(0, email.indexOf("@"));
	}

	/**
	 * Create or update a Profile object associated with the given user object.
	 * @param user a User object injected by the cloud endpoints.
	 * @param profileForm a ProfileForm object sent from the client form.
	 * @return the profile object just created.
	 * @throws UnauthorizedException when the User object is null.
	 */
	@ApiMethod(name = "saveProfile", path = "profile", httpMethod = HttpMethod.POST)
	public Profile saveProfile(final User user, ProfileForm profileForm) throws UnauthorizedException {

		// The @ApiMethod annotation declares this method as a method available externally through Endpoints.
		// The request that invokes this method should provide data that conforms to the fields defined in ProfileForm.

		// COMPLETED 1 Pass the ProfileForm parameter
		// COMPLETED 2 Pass the User parameter

		// COMPLETED 2 If the user is not logged in, throw an UnauthorizedException
		if (user == null) {
			throw new UnauthorizedException("Authorization required");
		}

		TeeShirtSize teeShirtSize = TeeShirtSize.NOT_SPECIFIED;

		// COMPLETED 1 Set the teeShirtSize to the value sent by the ProfileForm if sent, otherwise leave it as is
		if (profileForm.getTeeShirtSize() != null) {
			teeShirtSize = profileForm.getTeeShirtSize();
		}

		// COMPLETED 2 Get the userId and mainEmail
		String userId = user.getUserId();
		String mainEmail = user.getEmail();

		// COMPLETED 1 Set the displayName to the value sent by the ProfileForm if sent, otherwise set it to null
		String displayName = profileForm.getDisplayName();

		// COMPLETED 2 If the displayName is null, set it to default value based on the user's email
		if (displayName == null) {
			displayName = extractDefaultDisplayNameFromEmail(mainEmail);
		}

		// Create a new Profile entity from the userId, displayName, mainEmail and teeShirtSize
		Profile profile = new Profile(userId, displayName, mainEmail, teeShirtSize);

		// COMPLETED 3 (In Lesson 3) Save the Profile entity in the datastore
		ofy().save().entities(profile).now();

		// Return the profile
		return profile;
	}

	/**
	 * Return a Profile object associated with the given user object.
	 * The cloud endpoints system automatically inject the User object.
	 * @param user a User object injected by the cloud endpoints.
	 * @return the Profile object associated with the user
	 * @throws UnauthorizedException when the User object is null.
	 */
	@ApiMethod(name = "getProfile", path = "profile", httpMethod = HttpMethod.GET)
	public Profile getProfile(final User user) throws UnauthorizedException {
		if (user == null) {
			throw new UnauthorizedException("Authorization required");
		}

		// COMPLETED 3 (In Lesson 3) Load the Profile Entity by initializing these variables
		String userId = user.getUserId();

		// The key is directly generated from the userId
		Key key = Key.create(Profile.class, userId);

		// Retrieve the profile associated with the previously generated key
		return (Profile) ofy().load().key(key).now();
	}
}
