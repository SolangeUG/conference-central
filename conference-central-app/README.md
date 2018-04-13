# Google App Engine Application

[Conference Central Application][1].

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

## Authorizing Mobile clients

As stated above, for a mobile app to be able to access our backend API, it needs to be authorized first.
Here's how to create an OAuth 2.0 Android Client ID.

First, before an Android app can be authorized to access our API, we'll need to create a **SHA-1 fingerprint** of that 
mobile app. To do so, follow these steps:

- in a terminal window, run the follow command to generate a **key** and sign the mobile application
    - `keytool -genkey -v -keystore keystore-name.keystore -alias keystore-alias -keyalg RSA -keysize 2048 -validity 1000` 
- enter a **password** for your key (and make sure to remember it)
- answer the various questions that follow (such as your first name, and your last name...)
- say **yes** to the last question
- then enter a password for the keystore alias. It is OK for the password for the _keystore alias_ to be the same as the _keystore_ password.

Getting the **SHA-1 fingerprint** for the mobile app:

- in terminal window, run the following command:
    - `keytool -exportcert -alias keystore-alias -keystore keystore-name.keystore -list -v`
- enter the password you chose in the previous steps
- copy and save the generated **SHA-1** value, for example: `04:E7:79:66:CD...C7:41`


On the [Google APIs][8] page, select your project, then under the **Credentials** tab, click the **Create credentials** 
drop-down button, the choose, the `OAuth client ID` option. You'll be prompted to choose an application type, select 
**Android**. Then, follow these instructions: 
- enter a **name** for your Android Client ID
- enter the **SHA-1** fingerprint of your mobile application
- from your `AndroidManifest.xml` file, get the package name of your mobile app
- to finish, click the `Create` button.

Once the `Client ID` is generated, we can add it to the list of authorized client IDS in the `@Api` annotation of our 
Java backend endpoints class.

## Generating The Client Libraries

The next step is to generate the client library that contains our endpoints API, to include it in our mobile app.

For Andoid apps:

- in a terminal window, navigate to the root folder of our Google App Engine application (where `pom.xml` is located)
- run the `mvn appengine:endpoints_get_client_lib` command
- then, navigate to the `target/endpoints-client-libs/conference` folder, and run the `mvn install` command
- navigate to the `target` folder (inside the `conference` folder) where the client library jar file will be located
- copy the generated client library (`conference-v1-1.2.0.jar` for example) to the project folder of your mobile application.

**NB: should you make any changes to your backend API, you will need to update the client library.**

Once you've generated the client library, read through the Google Cloud documentation 
on [Calling Backend APIs from an Android Client][1].

## Changes to the Conference Central Code

In general, when you create an application that uses **Endpoints**, the code to define the endpoints is the same regardless 
of whether the endpoints are used by a Web app or an Android app. 
However, when the Android app injects the user into an endpoints API call, the `User` object does not include the `userId`. 

In our backend application, we have used the `userId` to uniquely identify the logged-in user, and obviously this 
won't work for Android users. 
Therefore, in our Conference Central application, we have to define a class `AppEngineUser` that compensates for the 
lack of the `userId` in Android Users.



[1]: https://sug-apt-octagon.appspot.com/
[2]: https://developers.google.com/appengine
[3]: http://java.com/en/
[4]: https://developers.google.com/appengine/docs/java/endpoints/
[5]: https://developers.google.com/appengine/docs/java/tools/maven
[6]: https://console.developers.google.com/
[7]: https://localhost:8080/
[8]: https://console.developers.google.com/apis/