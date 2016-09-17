## Clio Notes  
*Katrina Butler*

<br/>

### Features:

* View all Matters
* View all Notes for a Matter
* Create new Note
* Delete a Note
* Edit a Note
* View all Notes when offline
<br/>
<br/>

### Architecture:
<br/>

**ACTIVITY/CURSOR ADAPTER (UI Layer)**  

This layer contains view related items. This layer only sends messages to the Service Helper layer and recieved updateds from the Content Provider layer.

* MatterCursorAdapter: *recieved updated from the Matter table in the local SQLite DB*
* NoteCursorAdapter: *recieved updated from the Note table in the local SQLite DB*


**CONTENT PROVIDER**  

This layer exposes the date from the local SQLite cache database. This layer will notify the UI layer and also send messages to the Service Helper for some requests. When an insert/update/delete occurs on the Content Provider layer there will be a corrisponding request to the Service Helper to make the request to Clio's servers.

* ClioContentProvider: *provides content for cached data from Clio*
* ClioDatabaseHelper: *manages local database creation and version management*


**SERVICE HELPER**  

This layer is to help manage the background services.

* RESTServiceHelper: *singleton class that starts background services that manage REST operations*


**SERVICE**  

This layer is for the background services that make the REST request to Clio. Server requests are made on a new thread.

* RESTService: *background service to run REST request to Clio*


**REST**  

This is the client layer to access Clio's REST API

* RESTClient: *Wrapper around the Android HttpClient to make a cleaner interface when making REST requests.*


**PROCESSOR**  

This layer processes any REST responses recieved from the Clio servers. The processor will send messages to the Content Provider layer to update the local SQLite DB. This layer will also update the rest_state column on the recourses when needed.

* RESTProcessor: *processes the REST responses received from Clio*



<br/>


### Limits I saw fit to impose:


* If you create/edit/delete a note when you have no network connectivity, when connectivity is restored, it will never send to Clio (adds locally but not on the Clio server). This is a limit because in the future I expect to implement syncing between Clio and the local database. Alternatively I could have shown an error message saying the network is down so you can't Add a New Note.  
<br/>



### Future Work:   
<br/>


**OFFLINE ACCESS:**    

I decided to use a SQLite database with my app for both caching data, and keeping offline mode in mind. I created a column in the Notes table called "rest_state" which stores either NORMAL, GETTING, POSTING, PUTTING or DELETING.  Right now the user is able to create, edit, and delete notes with no internet connection, but only the local database is updated. To support full offline access, when internet connectivity is restored, I would check the "rest_state" column for any entry that is set to GETTING, POSTING, PUTTING, or DELETING. This means that the local database has been updated, but a request still needs to be sent to Clio. Once those requests are sent and I am given a successfull response, then I set all the rest_states to NORMAL on the effected resources.   

<br/>  
  


**ERROR HANDLING:**   

As well in the future, I would like to handle Clio's API error cases better. Right now, I only print a debug message when I receive a 400 or 404 error.

