
package com.linecorp.example.botphonebook;

import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.*;
import com.linecorp.example.botphonebook.model.*;
import com.google.gson.Gson;

import retrofit2.Response;
import com.linecorp.bot.model.*;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.response.BotApiResponse;
import com.linecorp.bot.client.LineSignatureValidator;
import com.linecorp.bot.client.LineMessagingServiceBuilder;

@RestController
@RequestMapping(value="/")
public class PhonebookController
{
    @Autowired
    PersonDao mDao;
    
    @Autowired
    @Qualifier("com.linecorp.channel_access_token")
    String lChannelSecret;
    
    @Autowired
    @Qualifier("com.linecorp.channel_access_token")
    String lChannelAccessToken;
    
    @RequestMapping(value="/phonebook", method=RequestMethod.POST)
    public ResponseEntity<String> callback(@RequestHeader("X-Line-Signature") String aXLineSignature,
                                           @RequestBody String aPayload)
    {
        final String text=String.format("The Signature is: %s",
                                        (aXLineSignature!=null && aXLineSignature.length() > 0) ? aXLineSignature : "N/A");
        
        System.out.println(text);
        
        final boolean valid=new LineSignatureValidator(lChannelSecret.getBytes()).validateSignature(aPayload.getBytes(), aXLineSignature);
        
        System.out.println("The signature is: " + (valid ? "valid" : "tidak valid"));
        
        //Get events from source
        if(aPayload!=null && aPayload.length() > 0)
        {
            System.out.println("Payload: " + aPayload);
        }
        
        Gson gson = new Gson();
        Payload payload = gson.fromJson(aPayload, Payload.class);
        
        String idTarget = " ";
        if (payload.events[0].source.type.equals("group")){
            idTarget = payload.events[0].source.groupId;
        } else if (payload.events[0].source.type.equals("room")){
            idTarget = payload.events[0].source.roomId;
        } else if (payload.events[0].source.type.equals("user")){
            idTarget = payload.events[0].source.userId;
        }
        
        String msgText = " ";
        if (!payload.events[0].message.type.equals("text")){
            replyToUser(payload.events[0].replyToken, "Unknown message");
        } else {
            msgText = payload.events[0].message.text;
            processText(payload.events[0].replyToken, idTarget, msgText);
        }
        
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    private void processText(String aReplyToken, String aUserId, String aText)
    {
        System.out.println("message text: " + aText + " from: " + aUserId);
        
        if (aText.indexOf("\"") == -1){
            replyToUser(aReplyToken, "Unknown keyword");
            return;
        }
        
        String [] words=aText.trim().split("\\s+");
        String intent=words[0];
        System.out.println("intent: " + intent);
        String msg = " ";
        
        String name = " ";
        String phoneNumber = " ";
        
        if(intent.equalsIgnoreCase("reg"))
        {
            String target=words.length>1 ? words[1] : "";
            if (target.length()<=3)
            {
                msg = "Need more than 3 character to find person";
            }
            else
            {
                System.out.println("Words passed: " + target);
                name = aText.substring(aText.indexOf("\"") + 1, aText.lastIndexOf("\""));
                phoneNumber = aText.substring(aText.indexOf("#") + 1);
                String status = RegProcessor(name, phoneNumber);
                replyToUser(aReplyToken, status);
            }
        }
        else if(intent.equalsIgnoreCase("find"))
        {
            name = aText.substring(aText.indexOf("\"") + 1, aText.lastIndexOf("\""));
            String txtMessage = FindProcessor(name);
            replyToUser(aReplyToken, txtMessage);
        }
        
        // if msg is invalid
        if(msg == " ")
        {
            replyToUser(aReplyToken, "Unknown keyword");
        }
    }
    
    private String RegProcessor(String aName, String aPhoneNumber){
        String regStatus;
        int exist=mDao.dataExist(aName);
        if(exist==1)
        {
            int reg=mDao.registerPerson(aName, aPhoneNumber);
            if(reg==1)
            {
                regStatus="Successfully Registered";
            }
            else
            {
                regStatus="Registration process failed";
            }
        }
        else
        {
            regStatus="Already registered";
        }
        
        return regStatus;
    }
    
    private String FindProcessor(String aName){
        String txt="Find Result:";
        List<Person> self=mDao.getByName("%"+aName+"%");
        if(self.size() > 0)
        {
            for (int i=0; i<self.size(); i++){
                Person prs=self.get(i);
                txt=txt+"\n";
                txt=txt+getPersonString(prs);
                txt=txt+"\n";
            }
            
        }
        else
        {
            txt="Employee not found";
        }
        return txt;
    }
    
    private String getPersonString(Person aPerson)
    {
        return String.format("Name: %s\nPhone Number: %s\n", aPerson.name, aPerson.phoneNumber);
    }
    
    //Method for reply user's message
    private void replyToUser(String rToken, String messageToUser){
        TextMessage textMessage = new TextMessage(messageToUser);
        ReplyMessage replyMessage = new ReplyMessage(rToken, textMessage);
        try {
            Response<BotApiResponse> response = LineMessagingServiceBuilder
            .create(lChannelAccessToken)
            .build()
            .replyMessage(replyMessage)
            .execute();
            System.out.println("Reply Message: " + response.code() + " " + response.message());
        } catch (IOException e) {
            System.out.println("Exception is raised ");
            e.printStackTrace();
        }
    }
};
