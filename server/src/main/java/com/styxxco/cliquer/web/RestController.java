package com.styxxco.cliquer.web;

import com.styxxco.cliquer.database.*;
import com.styxxco.cliquer.domain.Account;
import com.styxxco.cliquer.domain.Message;
import com.styxxco.cliquer.domain.Skill;
import com.styxxco.cliquer.domain.Group;
import org.bson.types.ObjectId;
import com.styxxco.cliquer.service.AccountService;
import com.styxxco.cliquer.service.FirebaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import java.lang.reflect.Array;
import java.util.ArrayList;

@Controller
public class RestController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private FirebaseService firebaseService;


    @RequestMapping(value = "/open/index", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public String index() {
        return "index";
    }

    @RequestMapping(value = "getProfile", method = RequestMethod.GET)
    public @ResponseBody Object getUserProfile(@RequestParam(value="identifier") String identifier,
                                               @RequestParam(value="type") String type,
                                               @RequestHeader HttpHeaders headers) {
        Account user;
        switch (type) {
            case "user":
                user = accountService.getUserProfile(identifier);
                break;
            case "member":
                ObjectId memberID = new ObjectId(identifier);
                user = accountService.getMemberProfile(memberID);
                break;
            case "public":
                ObjectId publicID = new ObjectId(identifier);
                user = accountService.getPublicProfile(publicID);
                break;
            default:
                return HttpStatus.BAD_REQUEST;
        }
        if (user == null) {
            return HttpStatus.BAD_REQUEST;
        }
        return user;
    }

    @RequestMapping(value = "/open/signup", method = RequestMethod.POST)
    public String signUp(@RequestHeader(value = "X-Authorization-Firebase") String firebaseToken) {
        firebaseService.registerUser(firebaseToken);
        return "greeting";
    }

    @RequestMapping(value = "createProfile", method = RequestMethod.POST)
    public @ResponseBody Object createProfile(@RequestParam(value="username") String username,
                                              @RequestParam(value="first") String firstName,
                                              @RequestParam(value="last") String lastName,
                                              @RequestHeader HttpHeaders headers)
    {
        Account user = accountService.createAccount(username, firstName, lastName);
        if(user == null)
        {
            return HttpStatus.BAD_REQUEST;
        }
        return user;
    }

    @RequestMapping(value = "updateProfile", method = RequestMethod.POST)
    public @ResponseBody Object updateProfile(@RequestParam(value="username") String username,
                                              @RequestParam(value="key") String key,
                                              @RequestParam(value="value") String value,
                                              @RequestHeader HttpHeaders headers)
    {
        Account user = accountService.updateUserProfile(username, key, value);
        if(user == null)
        {
            return HttpStatus.BAD_REQUEST;
        }
        return user;
    }

    @RequestMapping(value = "getSkillList", method = RequestMethod.GET)
    public @ResponseBody Object getSkillList(@RequestHeader HttpHeaders headers)
    {
        ArrayList<Skill> skills = accountService.getAllValidSkills();
        if(skills == null)
        {
            return HttpStatus.BAD_REQUEST;
        }
        return skills;
    }

    @RequestMapping(value = "getSkills", method = RequestMethod.GET)
    public @ResponseBody Object getSkills(@RequestParam(value="username") String username,
                                          @RequestHeader HttpHeaders headers)
    {
        ArrayList<Skill> skills = accountService.getAllUserSkills(username);
        if(skills == null)
        {
            return HttpStatus.BAD_REQUEST;
        }
        return skills;
    }

    @RequestMapping(value = "addSkill", method = RequestMethod.POST)
    public @ResponseBody Object addSkill(@RequestParam(value="username") String username,
                                         @RequestParam(value="name") String skillName,
                                         @RequestParam(value="level") String skillLevel,
                                         @RequestHeader HttpHeaders headers)
    {
        Account user = accountService.addSkill(username, skillName, Integer.parseInt(skillLevel));
        if(user == null)
        {
            return HttpStatus.BAD_REQUEST;
        }
        return user;
    }

    @RequestMapping(value = "removeSkill", method = RequestMethod.POST)
    public @ResponseBody Object removeSkill(@RequestParam(value="username") String username,
                                            @RequestParam(value="name") String skillName,
                                            @RequestHeader HttpHeaders headers)
    {
        Account user = accountService.removeSkill(username, skillName);
        if(user == null)
        {
            return HttpStatus.BAD_REQUEST;
        }
        return user;
    }

    @RequestMapping(value = "getMessages", method = RequestMethod.GET)
    public @ResponseBody Object getMessages(@RequestParam(value="username") String username,
                                            @RequestHeader HttpHeaders headers)
    {
        ArrayList<Message> messages = accountService.getNewMessages(username);
        if(messages == null)
        {
            return HttpStatus.BAD_REQUEST;
        }
        return messages;
    }

    /* TODO Group Endpoints */
}
