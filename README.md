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
	
* Prepare DAO on the client side

	```java
	public interface PersonDao
{
    	public Long post(Person aPerson);
    	public List<Person> get();
    	public List<Person> getByName(String aName);
    	public int registerPerson(String aName, String aPhoneNumber);
};

	```

* Use Dao for Query

	```java
    private final static String SQL_REGISTER="INSERT INTO phonebook (name, phone_number) VALUES (?, ?);";
    
    public int registerPerson(String aName, String aPhoneNumber)
    {
        return mJdbc.update(SQL_REGISTER, new Object[]{aName, aPhoneNumber});
    }
	```
	
	```java
	public Long post(Person aPerson)
    {
        SimpleJdbcInsert insert=new SimpleJdbcInsert(mJdbc)
            .withTableName("phonebook")
            .usingGeneratedKeyColumns("id");
        Map<String, Object> fields=new HashMap<String, Object>();
        fields.put("name", aPerson.name);
        fields.put("phone_number", aPerson.phoneNumber);
        return insert.executeAndReturnKey(fields).longValue();
    }
	```
	
	```java
	private final static String SQL_SELECT_ALL="SELECT id, name, phone_number FROM phonebook";
    
    private final static ResultSetExtractor< List<Person> > MULTIPLE_RS_EXTRACTOR=new ResultSetExtractor< List<Person> >()
    {
        @Override
        public List<Person> extractData(ResultSet aRs)
            throws SQLException, DataAccessException
        {
            List<Person> list=new Vector<Person>();
            while(aRs.next())
            {
                Person p=new Person(
                aRs.getLong("id"),
                aRs.getString("name"),
                aRs.getString("phone_number"));
                list.add(p);
            }
            return list;
        }
    };
    
    public List<Person> get()
    {
        return mJdbc.query(SQL_SELECT_ALL, MULTIPLE_RS_EXTRACTOR);
    }
	```
	
	```java
	private final static String SQL_SELECT_ALL="SELECT id, name, phone_number FROM phonebook";
    private final static String SQL_GET_BY_NAME=SQL_SELECT_ALL + " WHERE LOWER(name) LIKE LOWER(?);";
    
    private final static ResultSetExtractor<Person> SINGLE_RS_EXTRACTOR=new ResultSetExtractor<Person>()
    {
        @Override
        public Person extractData(ResultSet aRs)
				throws SQLException, DataAccessException
        {
            while(aRs.next())
            {
                Person p=new Person(
                    aRs.getLong("id"),
                    aRs.getString("name"),
                    aRs.getString("phone_number"));
                return p;
            }
            return null;
        }
    };
    
    public List<Person> getByName(String aName)
    {
        return mJdbc.query(SQL_GET_BY_NAME, new Object[]{"%"+aName+"%"}, MULTIPLE_RS_EXTRACTOR);
    }
	```

* Connect user's command to database using DAO

	```java
	List<Person> self=mDao.getByName("%"+aName+"%");
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
