# Google App Engine Application

First steps in building a Conference Central App which lets users schedule and query for conferences.

# Products - Language - APIs

- [App Engine][1]
- [Java][2]
- [Google Cloud Endpoints][3]
- [Google App Engine Maven plugin][6]

# Setup instructions

- Update the value of `application` in `appengine-web.xml` to the app ID you have registered in the App Engine admin console and would like to use to host your instance of this sample.
  Note that application ID and version are not necessary when using the Google Cloud SDK to deploy your application. They are generated automatically.

- Update the values in `src/main/java/com/google/devrel/training/conference/Constants.java` to reflect the respective client IDs you have registered in the [Developer Console][4].

- *(Optional)* Mark this file as unchanged as follows: `$ git update-index --assume-unchanged src/main/java/com/google/devrel/training/conference/Constants.java`.

- Run `$ mvn clean install` in the command line.

- Run the application with `$ mvn appengine:devserver`, and ensure it's running by visiting your local server's  address (by default [localhost:8080][5].)

- Get the client library with `$ mvn appengine:endpoints_get_client_lib`.

- Deploy your application to Google App Engine with `$ mvn appengine:update`.


[1]: https://developers.google.com/appengine
[2]: http://java.com/en/
[3]: https://developers.google.com/appengine/docs/java/endpoints/
[4]: https://console.developers.google.com/
[5]: https://localhost:8080/
[6]: https://developers.google.com/appengine/docs/java/tools/maven