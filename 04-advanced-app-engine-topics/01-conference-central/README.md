# Google App Engine Application

Advanced [Google App Engine topics][1].

## Products - Language - APIs

- [App Engine][2]
- [Java][3]
- [Google Cloud Endpoints][4]
- [Google App Engine Maven plugin][5]

## Setup instructions

- Update the value of `application` in `appengine-web.xml` to the app ID you have registered in the App Engine admin console and would like to use to host your instance of this sample.
  Note that application ID and version are not necessary when using the Google Cloud SDK to deploy your application. They are generated automatically.

- Update the values in `src/main/java/com/google/devrel/training/conference/Constants.java` to reflect the respective client IDs you have registered in the [Developer Console][6].

- *(Optional)* Mark this file as unchanged as follows: `$ git update-index --assume-unchanged src/main/java/com/google/devrel/training/conference/Constants.java`.

- Run `$ mvn clean install` in the command line.

- Run the application with `$ mvn appengine:devserver`, and ensure it's running by visiting your local server's address (by default [localhost:8080][7].)

- Get the client library with `$ mvn appengine:endpoints_get_client_lib`.

- Deploy your application to Google App Engine with `$ mvn appengine:update`.

## Authorizing Web clients

In order for our backend to be accessed from other applications (web clients, mobile clients, etc), it is necessary to provide an authorized way for them to do so.
In this case, we'll create an OAuth 2.0 Web Client ID.

On the [Google APIs][8] page, select your project, then under the **Credentials** tab, create a new Web Client Credential. 
Under the `OAuth consent screen`, fill in your **email address**, and your **product name**, and any other information you'd like to provide.
Then, once a `Client ID` is generated (with a `Client Secret` and a `Creation Date`), you'll need to add **Authorized JavaScript origins** as well as **Authorized redirect URIs**.

In our case, these are the configuration details we set:

- Authorized JavaScript origins
    - `https://our-app-id.appspot.com` where `our-app-id` refers to our project ID on the Google Cloud platform
    - `http://localhost:8080` when running the application on our local development server
- Authorized redirect URIs
    - `https://our-app-id.appspot.com/callback` in production
    - `http://localhost:8080/callback` when running the application locally
    
The `callback` redirect URI is specified in our webapp `app.js` module as follows:

````JavaScript
/**
 * Calls the OAuth2 authentication method.
 */
oauth2Provider.signIn = function (callback) {
    gapi.auth.signIn({
        'clientid': oauth2Provider.CLIENT_ID,
        'cookiepolicy': 'single_host_origin',
        'accesstype': 'online',
        'approveprompt': 'auto',
        'scope': oauth2Provider.SCOPES,
        'callback': callback
    });
};

````


[1]: https://cloud.google.com/appengine/docs/standard/python/memcache/
[2]: https://developers.google.com/appengine
[3]: http://java.com/en/
[4]: https://developers.google.com/appengine/docs/java/endpoints/
[5]: https://developers.google.com/appengine/docs/java/tools/maven
[6]: https://console.developers.google.com/
[7]: https://localhost:8080/
[8]: https://console.developers.google.com/apis/