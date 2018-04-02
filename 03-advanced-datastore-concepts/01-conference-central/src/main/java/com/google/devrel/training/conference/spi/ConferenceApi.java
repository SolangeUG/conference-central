package com.google.devrel.training.conference.spi;

import static com.google.devrel.training.conference.service.OfyService.ofy;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.users.User;
import com.google.devrel.training.conference.Constants;
import com.google.devrel.training.conference.domain.Conference;
import com.google.devrel.training.conference.domain.Profile;
import com.google.devrel.training.conference.form.ConferenceForm;
import com.google.devrel.training.conference.form.ProfileForm;
import com.google.devrel.training.conference.form.ProfileForm.TeeShirtSize;
import com.google.devrel.training.conference.service.OfyService;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.Query;

import java.util.List;

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

        // Check for an existing profile
        Profile profile = getProfile(user);

        TeeShirtSize teeShirtSize = TeeShirtSize.NOT_SPECIFIED;
        // COMPLETED 1 Set the teeShirtSize to the value sent by the ProfileForm if sent, otherwise leave it as is
        if (profileForm.getTeeShirtSize() != null) {
            teeShirtSize = profileForm.getTeeShirtSize();
        }

        // COMPLETED 1 Set the displayName to the value sent by the ProfileForm if sent, otherwise set it to null
        String displayName = profileForm.getDisplayName();

        if (profile != null) {
            // if it exists, update its properties
            profile.update(displayName, teeShirtSize);
        } else {
            // COMPLETED 2 Get the userId and mainEmail
            String userId = user.getUserId();
            String mainEmail = user.getEmail();

            // COMPLETED 2 If the displayName is null, set it to default value based on the user's email
            if (displayName == null) {
                displayName = extractDefaultDisplayNameFromEmail(mainEmail);
            }
            // if not, create a new Profile entity from the userId, displayName, mainEmail and teeShirtSize
            profile = new Profile(userId, displayName, mainEmail, teeShirtSize);
        }

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

    /**
     * Gets the Profile entity for the current user or creates it if it doesn't exist
     * @param user the current user
     * @return user's Profile
     */
    private static Profile getProfileFromUser(User user) {
        // First fetch the user's Profile from the datastore.
        Profile profile = ofy().load().key(
                Key.create(Profile.class, user.getUserId())).now();
        if (profile == null) {
            // Create a new Profile if it doesn't exist.
            // Use default displayName and teeShirtSize
            String email = user.getEmail();
            profile = new Profile(user.getUserId(),
                    extractDefaultDisplayNameFromEmail(email), email, TeeShirtSize.NOT_SPECIFIED);
        }
        return profile;
    }

    /**
     * Creates a new Conference object and stores it to the datastore.
     *
     * @param user A user who invokes this method, null when the user is not signed in.
     * @param conferenceForm A ConferenceForm object representing user's inputs.
     * @return A newly created Conference Object.
     * @throws UnauthorizedException when the user is not signed in.
     */
    @ApiMethod(name = "createConference", path = "conference", httpMethod = HttpMethod.POST)
    public Conference createConference(final User user, final ConferenceForm conferenceForm)
            throws UnauthorizedException {

        if (user == null) {
            throw new UnauthorizedException("Authorization required");
        }

        // COMPLETED (Lesson 4) Get the userId of the logged in User
        String userId = user.getUserId();

        // COMPLETED (Lesson 4) Get the key for the User's Profile
        Key<Profile> profileKey = Key.create(Profile.class, userId);

        // COMPLETED (Lesson 4) Allocate a key for the conference -- let App Engine allocate the ID
        // Don't forget to include the parent Profile in the allocated ID
        final Key<Conference> conferenceKey = OfyService.ofy().factory().allocateId(profileKey, Conference.class);

        // COMPLETED (Lesson 4) Get the Conference Id from the Key
        final long conferenceId = conferenceKey.getId();

        // COMPLETED (Lesson 4) Get the existing Profile entity for the current user if there is one
        // Otherwise create a new Profile entity with default values
        Profile profile = getProfileFromUser(user);

        // COMPLETED (Lesson 4) Create a new Conference Entity,
        // specifying the user's Profile entity as the parent of the conference
        Conference conference = new Conference(conferenceId, userId, conferenceForm);

        // COMPLETED (Lesson 4) Save Conference and Profile Entities
        ofy().save().entities(profile, conference).now();

        return conference;
    }

    /**
     * Queries against the datastore with the given filters and returns the result.
     * Normally, this kind of method is supposed to get invoked by a GET HTTP method,
     * but we do it with POST, in order to receive a conferenceQueryForm object via the POST method
     *
     * @return a list of conferences that match the query.
     */
    @ApiMethod(name = "queryConferences", path = "queryConferences", httpMethod = HttpMethod.POST)
    public List<Conference> queryConferences() {
        // find all entities of type/kind Conference
        Query<Conference> query = ofy().load().type(Conference.class).order("name");
        return query.list();
    }

    /**
     * Ancestor query: restrict results to conferences that descend from the parent entity.
     * We're using a POST method here again, in order to receive a conferenceQueryForm object via the POST method
     *
     * @param user the ancestor entity - the user who created the conferences
     * @return a list of conferences created by the logged in user
     */
    @ApiMethod(name = "getConferencesCreated", path = "getConferencesCreated", httpMethod = HttpMethod.POST)
    public List<Conference> getConferencesCreated(final User user)
                            throws UnauthorizedException {

        if (user == null) {
            // only allow logged in users
            throw new UnauthorizedException("Authorization required");
        }

        String userId = user.getUserId();
        Key<Profile> profileKey = Key.create(Profile.class, userId);
        Query<Conference> query = ofy().load().type(Conference.class).ancestor(profileKey);

        return query.list();
    }
}
