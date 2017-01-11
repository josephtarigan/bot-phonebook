package com.linecorp.example.botphonebook;

import java.util.List;
import com.linecorp.example.botphonebook.model.Person;

public interface PersonDao
{
    public Long post(Person aPerson);
    public List<Person> get();
    public List<Person> getByName(String aName);
    public int registerPerson(String aName, String aPhoneNumber);
};
