# bot-phonebook #

This repository demonstrates how to create a bot with **LINE Messaging API** using **Line Bot SDK**, **Spring Framework**, and connected with **PostgreSQL** deployed in **Heroku**.

### How do I get set up? ###
* Make LINE@ Account with Messaging API enabled
> [LINE Business Center](https://business.line.me/en/)

* Register your Webhook URL
	1. Open [LINE Developer](https://developers.line.me/)
	2. Choose your channel
	3. Edit "Basic Information"

* Add `application.properties` file in *src/main/resources* directory, and fill it with your channel secret and channel access token, like the following:

	```ini
com.linecorp.channel_secret=<your_channel_secret>
com.linecorp.channel_access_token=<your_channel_access_token>
	```
	
* Open Heroku Postgres

	```bash
	$ heroku psql
	```

* Prepare `phonebook` table

	```sql
	CREATE TABLE IF NOT EXISTS phonebook
	(
		id BIGSERIAL PRIMARY KEY,
		name TEXT,
		phone_number TEXT
	);
	```	

* Compile
 
    ```bash
    $ gradle clean build
    ```
* Deploy
 	
 	```bash
	$ git push heroku master
	```  

* Run Server

    ```bash
    $ heroku ps:scale web=1
    ```

* Use
    
    There are two intent that you can use with this bot, which are **find** and **reg**

Message template for find:
> find "Your_Name"<br><br>
> **INFO** Double quotation mark (") is essential. If you do not use it, then your intent won't be recognized by bot.
    
Message template for reg:
> reg "Your_Name" #Your_Phone_Number<br><br>
> **INFO** Double quotation mark (") and Number sign (#) are essential. If you do not use it, then your intent won't be recognized by bot.

### How do I contribute? ###

* Add your name and e-mail address into CONTRIBUTORS.txt
