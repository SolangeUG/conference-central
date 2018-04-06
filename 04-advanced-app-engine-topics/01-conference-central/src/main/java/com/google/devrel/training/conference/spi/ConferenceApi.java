package com.google.devrel.training.conference.spi;

import static com.google.devrel.training.conference.service.OfyService.ofy;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;
import com.google.api.server.spi.response.ConflictException;
import com.google.api.server.spi.response.ForbiddenException;
import com.google.api.server.spi.response.NotFoundException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.users.User;
import com.google.devrel.training.conference.Constants;
import com.google.devrel.training.conference.domain.Announcement;
import com.google.devrel.training.conference.domain.Conference;
import com.google.devrel.training.conference.domain.Profile;
import com.google.devrel.training.conference.form.ConferenceForm;
import com.google.devrel.training.conference.form.ConferenceQueryForm;
import com.google.devrel.training.conference.form.ProfileForm;
import com.google.devrel.training.conference.form.ProfileForm.TeeShirtSize;
import com.google.devrel.training.conference.service.OfyService;
import com.googlecode.objectify.Key;

import com.googlecode.objectify.Work;
import com.googlecode.objectify.cmd.Query;

import javax.inject.Named;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
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
     * @param queryForm the actual query form, where users specify the query criteria
     * @return a list of conferences that match the query.
     */
    @ApiMethod(name = "queryConferences", path = "queryConferences", httpMethod = HttpMethod.POST)
    public List<Conference> queryConferences(ConferenceQueryForm queryForm) {
        // find all entities of type/kind Conference
        // Query<Conference> query = ofy().load().type(Conference.class).order("name");

        // return all entities matching the user criteria in the query form
        // Query<Conference> query = queryForm.getQuery();

        /*
         * When the Web UI displays conferences, it shows the conference organizer's display name.
         * However, for each conference, the organizer's display name is calculated on the fly when needed,
         * in case the organizer changes their display name.
         * So to decrease the hits to the datastore, we can preload all the Profile entities for the users
         * who have organized conferences.
         */

        Iterable<Conference> conferenceIterable = queryForm.getQuery();
        List<Conference> result = new ArrayList<>(0);
        List<Key<Profile>> organizersKeyList = new ArrayList<>(0);

        for (Conference conference: conferenceIterable) {
            Key<Profile> key = Key.create(Profile.class, conference.getOrganizerUserId());
            organizersKeyList.add(key);
            result.add(conference);
        }

        // To avoid separate datastore gets for each Conference, pre-fetch the profiles
        ofy().load().keys(organizersKeyList);
        return result;
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

    /**
     * A method to try out different filters for queries.
     * @return a filtered list of conferences
     */
    //@ApiMethod(name = "filterPlayground", path = "filterPlayground", httpMethod = HttpMethod.GET)
    public List<Conference> filterPlayground() {

        // Making sure the results are sorted by name
        Query<Conference> query = ofy().load().type(Conference.class);

        // Filter on city
        query = query.filter("city =", "London");

        // Add a filter for topic = "Medical Innovations"
        query = query.filter("topics =", "Medical Innovations");

        // Add a filter for month {unindexed composite query}
        // Find conferences in June
        query = query.filter("month =", 6);

        // Add a filter for maxAttendees

        // Note : the first sort property must be the same as the property to which the inequality filter is applied.
        // So, if in the query the first sort property is the name but the inequality filter is on maxAttendees
        // we'll get an error! Therefore, we remove the order by name on the first line, and sort on maxAttendees first.
        query = query.filter("maxAttendees >", 10)
                .order("maxAttendees")
                .order("name");

        /* Activate each filter one at a time!

        // Add a filter for maxAttendees
        query = query.filter("maxAttendees >", 8);
        query = query.filter("maxAttendees <", 10)
                        .order("maxAttendees")
                        .order("name");

        // multiple sort orders
        query = query.filter("city =", "Tokyo")
                        .filter("seatsAvailable <", 10)
                        .filter("seatsAvailable >" , 0)
                            .order("seatsAvailable")
                            .order("name")
                            .order("month");
        */
        return query.list();
    }

    /**
     * Returns a Conference object with the given conference key.
     * @param websafeConferenceKey The String representation of the Conference Key.
     * @return a Conference object with the given conferenceId.
     * @throws NotFoundException when there is no Conference with the given conferenceId.
     */
    @ApiMethod(name = "getConference", path = "conference/{websafeConferenceKey}", httpMethod = HttpMethod.GET)
    public Conference getConference(@Named("websafeConferenceKey") final String websafeConferenceKey)
            throws NotFoundException {
        Key<Conference> conferenceKey = Key.create(websafeConferenceKey);
        Conference conference = ofy().load().key(conferenceKey).now();
        if (conference == null) {
            throw new NotFoundException("No Conference found with key: " + websafeConferenceKey);
        }
        return conference;
    }

    /**
     * A method to allow users to register for conferences
     * @param user the user registering for the conference
     * @param websafeConferenceKey the conference key to register for
     * @return a value/flag that indicates whether the operation was successful or not
     * @throws UnauthorizedException in case the user is unidentified
     * @throws NotFoundException in case the specified conference key is not found
     * @throws ForbiddenException in case of any other unexpected error
     * @throws ConflictException in case there are no seats available for the specified conference
     */
    public WrappedBoolean registerForConference(final User user,
                                                @Named("websafeConferenceKey") final String websafeConferenceKey)
            throws UnauthorizedException, NotFoundException,
            ForbiddenException, ConflictException {

        // If not signed in, throw a 401 error.
        if (user == null) {
            throw new UnauthorizedException("Authorization required");
        }

        // Get the userId
        final String userId = user.getUserId();

        /*
         * Start a transaction with Objectify
         * Google App Engine transactions use snapshot isolation and optimistic concurrency.
         * If multiple transactions update an entity, the first transaction to commit succeeds, the others fail.
         *
         * Differences between Objectify and native Datastore Transactions:
         *   In the native Datastore API, any changes to an entity inside a transaction are not reflected to any
         *   queries or gets made later in the transaction.
         *
         *   However, queries and gets inside transactions in Objectify will reflect any changes to entities made
         *   previously in the transaction (be aware though that those changes will be lost if the transaction does not
         *   commit successfully).
         *
         * Best practice:
         *   limit the use of transactions as much as possible to entity updates rather than for queries and gets.
         */
        WrappedBoolean result = ofy().transact(new Work<WrappedBoolean>() {

            @Override
            public WrappedBoolean run() {
                try {

                    // Get the conference key
                    Key<Conference> conferenceKey = Key.create(websafeConferenceKey);

                    // Get the Conference entity from the datastore
                    Conference conference = ofy().load().key(conferenceKey).now();

                    // 404 when there is no Conference with the given conferenceId.
                    if (conference == null) {
                        return new WrappedBoolean (false,
                                "No Conference found with key: "
                                        + websafeConferenceKey);
                    }

                    // Get the user's Profile entity
                    Profile profile = getProfileFromUser(user);

                    // Has the user already registered to attend this conference?
                    if (profile.getConferenceKeysToAttend().contains(
                            websafeConferenceKey)) {
                        return new WrappedBoolean (false, "Already registered");
                    } else if (conference.getSeatsAvailable() <= 0) {
                        return new WrappedBoolean (false, "No seats available");
                    } else {
                        // All looks good, go ahead and book the seat
                        profile.addToConferenceKeysToAttend(websafeConferenceKey);
                        conference.bookSeats(1);

                        // Save the Conference and Profile entities
                        ofy().save().entities(profile, conference).now();
                        // We are booked!
                        return new WrappedBoolean(true);
                    }

                } catch (Exception e) {
                    return new WrappedBoolean(false, "Unknown exception");
                }
            }
        });

        // if result is false
        if (! result.getResult()) {

            String reason = result.getReason();

            if ("Already registered".equals(reason)) {
                throw new ConflictException("You have already registered");
            } else if ("No seats available".equals(reason)) {
                throw new ConflictException("There are no seats available");
            } else if (reason.contains("No Conference found with key")) {
                throw new NotFoundException("No Conference found with key: " + websafeConferenceKey);
            } else {
                throw new ForbiddenException("Unknown exception");
            }
        }
        return result;
    }

    /**
     * Utility class for wrapping boolean values.
     * This is because endpoint functions must return objects,
     * they can't return simple data type objects such as String, Long or Boolean.
     */
    public static class WrappedBoolean {

        private final Boolean result;
        private final String reason;

        public WrappedBoolean(Boolean result) {
            this.result = result;
            this.reason = "";
        }

        public WrappedBoolean(Boolean result, String reason) {
            this.result = result;
            this.reason = reason;
        }

        public Boolean getResult() {
            return result;
        }

        public String getReason() {
            return reason;
        }
    }

    /**
     * Returns a collection of Conference Object that the user is going to attend.
     * @param user An user who invokes this method, null when the user is not signed in.
     * @return a Collection of Conferences that the user is going to attend.
     * @throws UnauthorizedException when the User object is null.
     */
    @ApiMethod(name = "getConferencesToAttend", path = "getConferencesToAttend", httpMethod = HttpMethod.GET)
    public Collection<Conference> getConferencesToAttend(final User user)
            throws UnauthorizedException, NotFoundException {
        // If not signed in, throw a 401 error.
        if (user == null) {
            throw new UnauthorizedException("Authorization required");
        }

        // COMPLETED: Get the Profile entity for the user
        Profile profile = getProfileFromUser(user);
        if (profile == null) {
            throw new NotFoundException("Profile doesn't exist.");
        }

        // COMPLETED: Get the value of the profile's conferenceKeysToAttend property
        List<String> keyStringsToAttend = profile.getConferenceKeysToAttend();

        // COMPLETED: Iterate over keyStringsToAttend, and return a Collection of the
        // Conference entities that the user has registered to atend
        List<Conference> conferences = new LinkedList<>();
        for (String key: keyStringsToAttend) {
            Conference conference = getConference(key);
            conferences.add(conference);
        }

        return conferences;
    }

    /**
     * Unregister from the specified Conference.
     * @param user An user who invokes this method, null when the user is not signed in.
     * @param websafeConferenceKey The String representation of the Conference Key
     * to unregister from.
     * @return Boolean true when success, otherwise false.
     * @throws UnauthorizedException when the user is not signed in.
     * @throws NotFoundException when there is no Conference with the given conferenceId.
     */
    @ApiMethod(name = "unregisterFromConference", path = "conference/{websafeConferenceKey}/registration",
            httpMethod = HttpMethod.DELETE)
    public WrappedBoolean unregisterFromConference(final User user,
                                                   @Named("websafeConferenceKey") final String websafeConferenceKey)
            throws UnauthorizedException, NotFoundException, ForbiddenException, ConflictException {

        // Error 401 if the user is not signed in
        if (user == null) {
            throw new UnauthorizedException("Authorization required.");
        }

        // Get the user ID
        final String userId = user.getUserId();

        // Start a transaction with Objectify to unregister from conference
        WrappedBoolean result = ofy().transact(new Work<WrappedBoolean>() {

            @Override
            public WrappedBoolean run() {

                try {

                    // Get the conference key
                    Key<Conference> conferenceKey = Key.create(websafeConferenceKey);

                    // Get the Conference entity from the datastore
                    Conference conference = ofy().load().key(conferenceKey).now();

                    // When there is no Conference with the given conference key
                    if (conference == null) {
                        return new WrappedBoolean (
                                false,
                                "No Conference found with key: " + websafeConferenceKey);
                    }

                    // Get the user's Profile entity
                    Profile profile = getProfileFromUser(user);

                    // Has the user registered to attend this conference?
                    if (! profile.getConferenceKeysToAttend().contains(websafeConferenceKey)) {
                        return new WrappedBoolean (
                                false,
                                "Not registered for this conference");
                    } else {
                        // All looks good, go ahead and unregister
                        profile.unregisterFromConference(websafeConferenceKey);
                        conference.giveBackSeats(1);

                        // Save the Conference and Profile entities
                        ofy().save().entities(profile, conference).now();
                        // We have successfully unregistered!
                        return new WrappedBoolean(true);
                    }

                } catch (Exception exception) {
                    return new WrappedBoolean(false, exception.getMessage());
                }
            }
        });

        // in case the operation failed
        if (! result.getResult()) {
            String reason = result.getReason();
            if ("Not registered for this conference".equals(reason)) {
                throw new ConflictException(reason);
            } else if (reason.contains("No Conference found with key")) {
                throw new NotFoundException("No Conference found with key: " + websafeConferenceKey);
            } else {
                throw new ForbiddenException(reason);
            }
        }
        // success
        return result;
    }

    /**
     * A method to retrieve the latest announcement out of memcache.
     * @return an announcement if available
     */
    @ApiMethod(name = "getAnnouncement", path = "announcement", httpMethod = HttpMethod.GET)
    public Announcement getAnnouncement() {
        MemcacheService service = MemcacheServiceFactory.getMemcacheService();
        String announcementKey = Constants.MEMCACHE_ANNOUNCEMENTS_KEY;
        Object message = service.get(announcementKey);

        // an annoucement has been found
        if (message != null) {
            return new Announcement(message.toString());
        }

        // there's no announcement
        return null;
    }
}
