
package com.linecorp.example.botphonebook;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.core.env.Environment;

@Configuration
public class PhonebookConfiguration
{
    @Autowired
    Environment mEnv;
    
	@Bean
    public DataSource getDataSource()
    {
        String dbUrl=System.getenv("com.psql.db_url");
        String username=System.getenv("com.psql.username");
        String password=System.getenv("com.psql.password");
        
        DriverManagerDataSource ds=new DriverManagerDataSource();
        ds.setDriverClassName("org.postgresql.Driver");
        ds.setUrl(dbUrl);
        ds.setUsername(username);
        ds.setPassword(password);
        
        return ds;
    }
    
    @Bean(name="com.linecorp.channel_secret")
    public String getChannelSecret()
    {
        return mEnv.getProperty("com.linecorp.channel_secret");
    }
    
    @Bean(name="com.linecorp.channel_access_token")
    public String getChannelAccessToken()
    {
        return mEnv.getProperty("com.linecorp.channel_access_token");
    }
    
    @Bean
    public PersonDao getPersonDao()
    {
        return new PersonDaoImpl(getDataSource());
    }
};
