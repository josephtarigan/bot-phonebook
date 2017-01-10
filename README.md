# spring-phonebook #

This repository demonstrates how to create a basic web application using Spring Framework, connected with PostgreSQL deployed in Heroku.

### How do I get set up? ###

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
    
    You can get the list of the people in the phonebook by calling:
    > https://\<your\_heroku\_endpoint\>/phonebook/
    
    You can search a name from the phonebook by calling:
    > https://\<your\_heroku\_endpoint\>/phonebook?name=\<name\_to\_search\>
    
    You can add new record to the phonebook by sending POST request like the following:
    
    ```bash
	$ curl -X POST -d'{ "name":"Sally", "phoneNumber":"909090" }' -H "Content-Type: application/json;charset=UTF-8" https://<your_heroku_endpoint>/phonebook/
	```


### How do I contribute? ###

* Add your name and e-mail address into CONTRIBUTORS.txt
