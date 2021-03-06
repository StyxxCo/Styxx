package com.styxxco.cliquer.tests;

import com.styxxco.cliquer.database.*;
import com.styxxco.cliquer.domain.*;
import com.styxxco.cliquer.domain.Message.Types;
import com.styxxco.cliquer.service.AccountService;
import com.styxxco.cliquer.service.GroupService;
import com.styxxco.cliquer.service.impl.GroupServiceImpl;
import com.styxxco.cliquer.service.impl.AccountServiceImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations="classpath:application-test.properties")
public class SprintThreeServicesTest {

    @Autowired
    public AccountRepository accountRepository;
    @Autowired
    public SkillRepository skillRepository;
    @Autowired
    public MessageRepository messageRepository;
    @Autowired
    public GroupRepository groupRepository;
    @Autowired
    public RoleRepository roleRepository;
    @Autowired
    public AccountService accountService;
    @Autowired
    public GroupService groupService;

    /* Back end Unit Test for User Story 5 */
    @Test
    public void testAddSkillToDatabase() {
        Account jordan = accountService.createAccount("reed226", "reed226@pdue.edu", "Jordan", "Reed");
        Account kevin = accountService.createAccount("knagar", "knagar@pdue.edu", "Kevin", "Nagar");

        accountService.addToModerators(kevin.getAccountID());

        Skill result = accountService.addSkillToDatabase(kevin.getAccountID(), "Surfing");
        assertEquals(0, result.getSkillLevel());
        assertNotNull(skillRepository.findBySkillNameAndSkillLevel("Surfing", 0));
        assertNotNull(skillRepository.findBySkillNameAndSkillLevel("Surfing", 1));
        assertNotNull(skillRepository.findBySkillNameAndSkillLevel("Surfing", 2));
        assertNotNull(skillRepository.findBySkillNameAndSkillLevel("Surfing", 3));
        assertNotNull(skillRepository.findBySkillNameAndSkillLevel("Surfing", 4));
        assertNotNull(skillRepository.findBySkillNameAndSkillLevel("Surfing", 5));
        assertNotNull(skillRepository.findBySkillNameAndSkillLevel("Surfing", 6));
        assertNotNull(skillRepository.findBySkillNameAndSkillLevel("Surfing", 7));
        assertNotNull(skillRepository.findBySkillNameAndSkillLevel("Surfing", 8));
        assertNotNull(skillRepository.findBySkillNameAndSkillLevel("Surfing", 9));
        assertNotNull(skillRepository.findBySkillNameAndSkillLevel("Surfing", 10));
        assertNull(skillRepository.findBySkillNameAndSkillLevel("Surfing", 11));

        result = accountService.addSkillToDatabase(jordan.getAccountID(), "Chess");
        assertNull(result);
        Skill skill = skillRepository.findBySkillNameAndSkillLevel("Java", 1);
        result = accountService.addSkillToDatabase(kevin.getAccountID(), "Java");
        assertEquals(0, result.getSkillLevel());
        assertEquals(skill.getSkillID(), skillRepository.findBySkillNameAndSkillLevel("Java", 1).getSkillID());
}

    /* Back end Unit Test for User Story 8 */
    @Test
    public void testGetMessages() {
        Account jordan = accountService.createAccount("reed226", "reed226@pdue.edu", "Jordan", "Reed");
        Account shawn = accountService.createAccount("montgo38", "montgo38@pdue.edu", "Shawn", "Montgomery");

        Message first = accountService.sendMessage(jordan.getAccountID(), shawn.getAccountID(), "Be my friend?", Message.Types.FRIEND_INVITE);
        Message second = accountService.sendMessage(jordan.getAccountID(), shawn.getAccountID(), "Please be my friend?", Message.Types.FRIEND_INVITE);

        List<Message> messages = accountService.getMessages(shawn.getAccountID(), "false", null);
        assertEquals(2, messages.size());
        assertEquals(1, messages.get(0).getType());

        accountService.readMessage(shawn.getAccountID(), first.getMessageID());
        accountService.readMessage(shawn.getAccountID(), second.getMessageID());

        Message third = accountService.sendMessage(jordan.getAccountID(), shawn.getAccountID(), "Pretty please be my friend?", Message.Types.FRIEND_INVITE);

        messages = accountService.getMessages(shawn.getAccountID(), "false", null);
        assertEquals(1, messages.size());
        assertEquals("Pretty please be my friend?", messages.get(0).getContent());

        accountService.readMessage(shawn.getAccountID(), third.getMessageID());

        messages = accountService.getMessages(shawn.getAccountID(), "false", null);
        assertEquals(0, messages.size());

        messages = accountService.getMessages(shawn.getAccountID(), "true", null);
        assertEquals(3, messages.size());

        first.setCreationDate(LocalDate.parse("2018-04-01"));
        second.setCreationDate(LocalDate.parse("2018-03-22"));
        third.setCreationDate(LocalDate.parse("2016-04-01"));

        messageRepository.save(first);
        messageRepository.save(second);
        messageRepository.save(third);

        messages = accountService.getMessages(shawn.getAccountID(), "true", "2016-03-21");
        assertEquals(3, messages.size());
        assertEquals("Pretty please be my friend?", messages.get(0).getContent());
        assertEquals("Please be my friend?", messages.get(1).getContent());
        assertEquals("Be my friend?", messages.get(2).getContent());

        messages = accountService.getMessages(shawn.getAccountID(), "true", "2018-03-23");
        assertEquals(1, messages.size());
        assertEquals("Be my friend?", messages.get(0).getContent());

        first.setCreationDate(LocalDate.parse("2018-04-01"));
        first.setCreationTime(LocalTime.parse("14:10"));
        second.setCreationDate(LocalDate.parse("2018-04-01"));
        second.setCreationTime(LocalTime.parse("11:20"));
        third.setCreationDate(LocalDate.parse("2018-04-01"));
        third.setCreationTime(LocalTime.parse("23:40"));

        messageRepository.save(first);
        messageRepository.save(second);
        messageRepository.save(third);

        messages = accountService.getMessages(shawn.getAccountID(), "true", "2016-03-30");
        assertEquals(3, messages.size());
        assertEquals("Please be my friend?", messages.get(0).getContent());
        assertEquals("Be my friend?", messages.get(1).getContent());
        assertEquals("Pretty please be my friend?", messages.get(2).getContent());
    }

    /* Back end Unit Test for User Story 11 */
    @Test
    public void testGroupEventBroadcast() {
        Account jordan = accountService.createAccount("reed226", "reed226@pdue.edu", "Jordan", "Reed");
        Account shawn = accountService.createAccount("montgo38", "montgo38@pdue.edu", "Shawn", "Montgomery");
        Account kevin = accountService.createAccount("knagar", "knagar@pdue.edu", "Kevin", "Nagar");

        Group cliquer = groupService.createGroup(
                "Cliquer",
                "To create a web app that facilitates the teaming of people who may have never met before",
                jordan.getAccountID());
        Group hoops = groupService.createGroup(
                "Hoops",
                "To play basketball",
                shawn.getAccountID());

        Skill java = skillRepository.findBySkillNameAndSkillLevel("Java", 6);
        Skill ball = skillRepository.findBySkillNameAndSkillLevel("Basketball", 7);

        groupService.addGroupMember(cliquer.getGroupID(), jordan.getAccountID(), shawn.getAccountID());
        cliquer = groupRepository.findByGroupID(cliquer.getGroupID());
        shawn = accountRepository.findByAccountID(shawn.getAccountID());
        cliquer.addSkillReq(java);
        cliquer.setProximityReq(1);
        groupRepository.save(cliquer);

        hoops.addSkillReq(ball);
        hoops.setProximityReq(1);
        groupRepository.save(hoops);

        jordan.setLatitude(40.0);
        jordan.setLongitude(-80.0);
        jordan.addSkill(java);
        jordan.setPublic(true);
        accountRepository.save(jordan);

        shawn.setLatitude(40.2);
        shawn.setLongitude(-80.4);
        shawn.addSkill(java);
        shawn.addSkill(ball);
        shawn.setPublic(true);
        accountRepository.save(shawn);

        kevin.setLatitude(40.4);
        kevin.setLongitude(-80.8);
        kevin.addSkill(ball);
        kevin.setPublic(true);
        accountRepository.save(kevin);

        List<Account> result = groupService.broadcastEvent(cliquer.getGroupID(), jordan.getAccountID(), "Cliquer",
                "Test run for Cliquer", 200, new ArrayList<>());
        assertEquals(1, result.size());
        assertEquals("Kevin", result.get(0).getFirstName());
        kevin = accountRepository.findByAccountID(kevin.getAccountID());
        assertEquals(1, kevin.getMessageIDs().keySet().size());

        result = groupService.broadcastEvent(cliquer.getGroupID(), jordan.getAccountID(), "Cliquer",
                "Test run for Cliquer", 30, new ArrayList<>());
        assertEquals(0, result.size());
        kevin = accountRepository.findByAccountID(kevin.getAccountID());
        assertEquals(1, kevin.getMessageIDs().keySet().size());

        List<String> reqs = new ArrayList<>();
        reqs.add(ball.getSkillName());
        result = groupService.broadcastEvent(hoops.getGroupID(), shawn.getAccountID(), "Cliquer",
                "Basketball tournament", 100, reqs);
        assertEquals(1, result.size());
        assertEquals("Kevin", result.get(0).getFirstName());
        kevin = accountRepository.findByAccountID(kevin.getAccountID());
        assertEquals(2, kevin.getMessageIDs().keySet().size());
    }

    /* Back end Unit Test for User Story 19 */
    @Test
    public void testBecomingModerator() {
        Account jordan = accountService.createAccount("reed226", "reed226@pdue.edu", "Jordan", "Reed");
        Account shawn = accountService.createAccount("montgo38", "montgo38@pdue.edu", "Shawn", "Montgomery");
        Account kevin = accountService.createAccount("knagar", "knagar@pdue.edu", "Kevin", "Nagar");
        Account buckmaster = accountService.createAccount("buckmast", "buckmast@pdue.edu", "Jordan", "Buckmaster");
        Account rhys = accountService.createAccount("rbuckmas", "rbuckmas@pdue.edu", "Rhys", "Buckmaster");

        accountService.addToModerators(jordan.getAccountID());
        jordan = accountRepository.findByAccountID(jordan.getAccountID());
        jordan.setMessageIDs(new TreeMap<>());
        accountRepository.save(jordan);

        accountService.addToModerators(shawn.getAccountID());
        shawn = accountRepository.findByAccountID(shawn.getAccountID());
        shawn.setMessageIDs(new TreeMap<>());
        accountRepository.save(shawn);

        accountService.addToModerators(kevin.getAccountID());
        kevin = accountRepository.findByAccountID(kevin.getAccountID());
        kevin.setMessageIDs(new TreeMap<>());
        accountRepository.save(kevin);

        buckmaster.setReputation(55);
        buckmaster.setNewUser(false);
        buckmaster.getGroupIDs().put("group1", "group1");
        buckmaster.getGroupIDs().put("group2", "group2");
        buckmaster.getGroupIDs().put("group3", "group3");
        buckmaster.getGroupIDs().put("group4", "group4");
        accountRepository.save(buckmaster);

        Message result = accountService.checkModStatus(buckmaster.getAccountID());
        assertNull(result);

        buckmaster = accountRepository.findByAccountID(buckmaster.getAccountID());
        buckmaster.getGroupIDs().put("group5", "group5");
        accountRepository.save(buckmaster);
        result = accountService.checkModStatus(buckmaster.getAccountID());
        assertEquals("You are now able to apply to be a moderator!", result.getContent());
        buckmaster = accountRepository.findByAccountID(buckmaster.getAccountID());
        assertEquals(Types.MOD_INVITE, (int)buckmaster.getMessageIDs().get(result.getMessageID()));

        accountService.acceptModInvite(buckmaster.getAccountID(), result.getMessageID(), "No particular reason");

        String messageID = null;
        jordan = accountRepository.findByAccountID(jordan.getAccountID());
        for(String id : jordan.getMessageIDs().keySet()){
            assertEquals(Types.MOD_REQUEST, (int)jordan.getMessageIDs().get(id));
            messageID = id;
        }
        accountService.acceptModRequest(jordan.getAccountID(), messageID);
        buckmaster = accountRepository.findByAccountID(buckmaster.getAccountID());
        assertEquals(false, buckmaster.isModerator());

        shawn = accountRepository.findByAccountID(shawn.getAccountID());
        for(String id : shawn.getMessageIDs().keySet()){
            assertEquals(Types.MOD_REQUEST, (int)shawn.getMessageIDs().get(id));
            messageID = id;
        }
        accountService.rejectModRequest(shawn.getAccountID(), messageID);
        buckmaster = accountRepository.findByAccountID(buckmaster.getAccountID());
        assertEquals(false, buckmaster.isModerator());

        kevin = accountRepository.findByAccountID(kevin.getAccountID());
        for(String id : kevin.getMessageIDs().keySet()){
            assertEquals(Types.MOD_REQUEST, (int)kevin.getMessageIDs().get(id));
            messageID = id;
        }
        accountService.acceptModRequest(kevin.getAccountID(), messageID);
        buckmaster = accountRepository.findByAccountID(buckmaster.getAccountID());
        assertEquals(true, buckmaster.isModerator());

        rhys.setReputation(60);
        rhys.setNewUser(false);
        rhys.getGroupIDs().put("group1", "group1");
        rhys.getGroupIDs().put("group2", "group2");
        rhys.getGroupIDs().put("group3", "group3");
        rhys.getGroupIDs().put("group4", "group4");
        rhys.getGroupIDs().put("group5", "group5");
        accountRepository.save(rhys);

        result = accountService.checkModStatus(rhys.getAccountID());
        assertEquals("You are now able to apply to be a moderator!", result.getContent());
        rhys = accountRepository.findByAccountID(rhys.getAccountID());
        assertEquals(Types.MOD_INVITE, (int)rhys.getMessageIDs().get(result.getMessageID()));

        jordan = accountRepository.findByAccountID(jordan.getAccountID());
        jordan.setMessageIDs(new TreeMap<>());
        accountRepository.save(jordan);

        accountService.rejectModInvite(rhys.getAccountID(), result.getMessageID());
        jordan = accountRepository.findByAccountID(jordan.getAccountID());
        assertEquals(true, jordan.getMessageIDs().isEmpty());

        result = accountService.checkModStatus(rhys.getAccountID());
        assertNull(result);
    }

    /* Back end Unit Test for User Story 26 */
    @Test
    public void testGroupMemberSearch() {
        Account jordan = accountService.createAccount("reed226", "reed226@pdue.edu", "Jordan", "Reed");
        Account shawn = accountService.createAccount("montgo38", "montgo38@pdue.edu", "Shawn", "Montgomery");
        Account kevin = accountService.createAccount("knagar", "knagar@pdue.edu", "Kevin", "Nagar");
        Account buckmaster = accountService.createAccount("buckmast", "buckmast@pdue.edu", "Jordan", "Buckmaster");

        Group cliquer = groupService.createGroup(
                "Cliquer",
                "To create a web app that facilitates the teaming of people who may have never met before",
                jordan.getAccountID());

        Skill javaReq = skillRepository.findBySkillNameAndSkillLevel("Java", 7);
        Skill javaPass = skillRepository.findBySkillNameAndSkillLevel("Java", 8);
        Skill javaFail = skillRepository.findBySkillNameAndSkillLevel("Java", 6);

        groupService.addGroupMember(cliquer.getGroupID(), jordan.getAccountID(), shawn.getAccountID());
        cliquer = groupRepository.findByGroupID(cliquer.getGroupID());
        shawn = accountRepository.findByAccountID(shawn.getAccountID());

        cliquer.addSkillReq(javaReq);
        cliquer.setProximityReq(30);
        cliquer.setReputationReq(0.5);
        groupRepository.save(cliquer);

        jordan.setLatitude(40.0);
        jordan.setLongitude(-80.0);
        jordan.addSkill(javaPass);
        jordan.setReputation(40);
        jordan.setProximityReq(100);
        accountRepository.save(jordan);

        shawn.setLatitude(40.2);
        shawn.setLongitude(-80.4);
        shawn.addSkill(javaPass);
        shawn.setReputation(20);
        shawn.setReputationReq(0.0);
        shawn.setProximityReq(100);
        accountRepository.save(shawn);

        kevin.setLatitude(40.2);
        kevin.setLongitude(-80.4);
        kevin.addSkill(javaFail);
        kevin.setReputation(40);
        kevin.setReputationReq(0.0);
        kevin.setProximityReq(100);
        accountRepository.save(kevin);

        buckmaster.setLatitude(40.4);
        buckmaster.setLongitude(-80.8);
        buckmaster.addSkill(javaPass);
        buckmaster.setReputation(60);
        buckmaster.setReputationReq(0.0);
        buckmaster.setProximityReq(100);
        accountRepository.save(buckmaster);

        List<Account> result = groupService.inviteEligibleUsers(cliquer.getGroupID(), jordan.getAccountID());
        assertEquals(0, result.size());

        cliquer.setProximityReq(60);
        groupRepository.save(cliquer);

        result = groupService.inviteEligibleUsers(cliquer.getGroupID(), jordan.getAccountID());
        assertEquals(1, result.size());
        assertEquals("Jordan Buckmaster", result.get(0).getFullName());

        buckmaster = accountRepository.findByAccountID(buckmaster.getAccountID());
        assertEquals(1, buckmaster.getMessageIDs().keySet().size());
        assertEquals(true, buckmaster.getMessageIDs().values().contains(Types.SEARCH_INVITE));
        ArrayList<String> messages = new ArrayList<>(buckmaster.getMessageIDs().keySet());
        Message message = messageRepository.findByMessageID(messages.get(0));
        assertEquals("You have been matched with group Cliquer!", message.getContent());
        message = groupService.acceptSearchInvite(buckmaster.getAccountID(), messages.get(0));
        assertNotNull(message);
        assertEquals("User Jordan Buckmaster wishes to join your group Cliquer", message.getContent());
        buckmaster = accountRepository.findByAccountID(buckmaster.getAccountID());
        assertEquals(0, buckmaster.getMessageIDs().keySet().size());

        jordan = accountRepository.findByAccountID(jordan.getAccountID());
        assertEquals(1, jordan.getMessageIDs().keySet().size());
        assertEquals(true, jordan.getMessageIDs().values().contains(Types.JOIN_REQUEST));
        messages = new ArrayList<>(jordan.getMessageIDs().keySet());
        message = groupService.acceptJoinRequest(jordan.getAccountID(), messages.get(0));
        assertNotNull(message);
        //assertEquals("You have been accepted into group Cliquer", message.getContent());
        jordan = accountRepository.findByAccountID(jordan.getAccountID());
        assertEquals(0, jordan.getMessageIDs().keySet().size());

        buckmaster = accountRepository.findByAccountID(buckmaster.getAccountID());
        assertEquals(true, buckmaster.getGroupIDs().containsKey(cliquer.getGroupID()));
        cliquer = groupRepository.findByGroupID(cliquer.getGroupID());
        assertEquals(true, cliquer.getGroupMemberIDs().containsKey(buckmaster.getAccountID()));

        groupService.removeGroupMember(cliquer.getGroupID(), jordan.getAccountID(), buckmaster.getAccountID());
        buckmaster.setReputationReq(0.9);
        accountRepository.save(buckmaster);

        result = groupService.inviteEligibleUsers(cliquer.getGroupID(), jordan.getAccountID());
        assertEquals(0, result.size());

        groupService.removeGroupMember(cliquer.getGroupID(), jordan.getAccountID(), shawn.getAccountID());
        cliquer = groupRepository.findByGroupID(cliquer.getGroupID());
        cliquer.setReputationReq(0.75);
        groupRepository.save(cliquer);

        result = groupService.inviteEligibleUsers(cliquer.getGroupID(), jordan.getAccountID());
        assertEquals(0, result.size());
    }

    /* Back end Unit Test for User Story 31 */
    @Test
    public void testChatHistory() {
        Account jordan = accountService.createAccount("reed226", "reed226@pdue.edu", "Jordan", "Reed");
        Account kevin = accountService.createAccount("knagar", "knagar@pdue.edu", "Kevin", "Nagar");

        Group cliquer = groupService.createGroup(
                "Cliquer",
                "To create a web app that facilitates the teaming of people who may have never met before",
                jordan.getAccountID());

        groupService.addGroupMember(cliquer.getGroupID(), jordan.getAccountID(), kevin.getAccountID());

        accountService.sendMessage(jordan.getUsername(), cliquer.getGroupID(), "Hello", Types.CHAT_MESSAGE);
        accountService.sendMessage(kevin.getUsername(), cliquer.getGroupID(), "Hey", Types.CHAT_MESSAGE);
        accountService.sendMessage(jordan.getUsername(), cliquer.getGroupID(), "Bye", Types.CHAT_MESSAGE);

        List<Message> messages = accountService.getChatHistory(cliquer.getGroupID(), kevin.getUsername());

        assertEquals(3, messages.size());

        accountService.reactToChatMessage(cliquer.getGroupID(), kevin.getUsername(), messages.get(0).getMessageID(), String.valueOf(Message.Reactions.UP_VOTE));
        messages = accountService.getChatHistory(cliquer.getGroupID(), jordan.getUsername());
        assertEquals(1, messages.get(0).getReactions().size());
        assertEquals(Message.Reactions.UP_VOTE, messages.get(0).getReaction(kevin.getAccountID()));

        accountService.reactToChatMessage(cliquer.getGroupID(), jordan.getUsername(), messages.get(0).getMessageID(), String.valueOf(Message.Reactions.UP_VOTE));
        messages = accountService.getChatHistory(cliquer.getGroupID(), kevin.getUsername());
        assertEquals(2, messages.get(0).getReactions().size());
        assertEquals(Message.Reactions.UP_VOTE, messages.get(0).getReaction(jordan.getAccountID()));

        accountService.reactToChatMessage(cliquer.getGroupID(), kevin.getUsername(), messages.get(0).getMessageID(), String.valueOf(Message.Reactions.DOWN_VOTE));
        messages = accountService.getChatHistory(cliquer.getGroupID(), kevin.getUsername());
        assertEquals(2, messages.get(0).getReactions().size());
        assertEquals(Message.Reactions.DOWN_VOTE, messages.get(0).getReaction(kevin.getAccountID()));

        accountService.reactToChatMessage(cliquer.getGroupID(), kevin.getUsername(), messages.get(0).getMessageID(), String.valueOf(Message.Reactions.DOWN_VOTE));
        messages = accountService.getChatHistory(cliquer.getGroupID(), kevin.getUsername());
        assertEquals(1, messages.get(0).getReactions().size());
        assertEquals(-1, messages.get(0).getReaction(kevin.getAccountID()));
    }

    /* Back end Unit Test for User Story 34 */
    @Test
    public void testReportGroupMembers() {
        Account jordan = accountService.createAccount("reed226", "reed226@pdue.edu", "Jordan", "Reed");
        Account shawn = accountService.createAccount("montgo38", "montgo38@pdue.edu", "Shawn", "Montgomery");
        Account kevin = accountService.createAccount("knagar", "knagar@purdue.edu", "Kevin", "Nagar");

        Group cliquer = groupService.createGroup(
                "Cliquer",
                "To create a web app that facilitates the teaming of people who may have never met before",
                jordan.getAccountID());

        groupService.addGroupMember(cliquer.getGroupID(), jordan.getAccountID(), shawn.getAccountID());
        
        kevin.setMessageIDs(new TreeMap<>());
        accountRepository.save(kevin);

        Message first = accountService.sendMessage(jordan.getAccountID(), cliquer.getGroupID(),
                "So are you ready to work?", Types.CHAT_MESSAGE);
        Message second = accountService.sendMessage(shawn.getAccountID(), cliquer.getGroupID(),
                "Fuck off, your idea sucks.", Types.CHAT_MESSAGE);
        Message third = accountService.sendMessage(jordan.getAccountID(), cliquer.getGroupID(),
                "What do you not like about it?", Types.CHAT_MESSAGE);
        Message fourth = accountService.sendMessage(shawn.getAccountID(), cliquer.getGroupID(),
                "That you are a faggot.", Types.CHAT_MESSAGE);
        Message fifth = accountService.sendMessage(jordan.getAccountID(), cliquer.getGroupID(),
                "Well that isn't very nice.", Types.CHAT_MESSAGE);
        Message sixth = accountService.sendMessage(shawn.getAccountID(), cliquer.getGroupID(),
                "Neither is your face.", Types.CHAT_MESSAGE);
        Message seventh = accountService.sendMessage(shawn.getAccountID(), cliquer.getGroupID(),
                "I'm leaving this shit hole.", Types.CHAT_MESSAGE);
        Message eighth = accountService.sendMessage(jordan.getAccountID(), cliquer.getGroupID(),
                "Well fine, leave.", Types.CHAT_MESSAGE);
        accountService.leaveGroup(shawn.getUsername(), cliquer.getGroupID());
        accountService.reportGroupMember(cliquer.getGroupID(), jordan.getAccountID(), second.getMessageID(), "Foul Language");

        kevin = accountRepository.findByAccountID(kevin.getAccountID());
        assertEquals(1, kevin.getMessageIDs().size());
        String messageID = null;
        for(String id : kevin.getMessageIDs().keySet()){
            assertEquals(Types.MOD_REPORT, (int)kevin.getMessageIDs().get(id));
            messageID = id;
        }

        List<Message> history = accountService.getReportContext(kevin.getAccountID(), messageID, null, null);
        assertEquals(7, history.size());
        assertEquals(first.getMessageID(), history.get(0).getMessageID());
        assertEquals(seventh.getMessageID(), history.get(6).getMessageID());
        history = accountService.getReportContext(kevin.getAccountID(), messageID, history.get(0).getMessageID(), history.get(history.size() - 1).getMessageID());
        assertEquals(9, history.size());
        assertEquals(eighth.getMessageID(), history.get(7).getMessageID());
        accountService.flagUser(kevin.getAccountID(), messageID);

        jordan = accountRepository.findByAccountID(jordan.getAccountID());
        shawn = accountRepository.findByAccountID(shawn.getAccountID());
        assertEquals(0, jordan.getFlags());
        assertEquals(1, shawn.getFlags());
    }

    /* Back end Unit Test for User Story 38 and 39*/
    @Test
    public void testModeratorReview() {
        Account jordan = accountService.createAccount("reed226", "reed226@pdue.edu", "Jordan", "Reed");
        Account shawn = accountService.createAccount("montgo38", "montgo38@pdue.edu", "Shawn", "Montgomery");
        Account kevin = accountService.createAccount("knagar", "knagar@purdue.edu", "Kevin", "Nagar");

        kevin.setMessageIDs(new TreeMap<>());
        accountRepository.save(kevin);

        accountService.sendFriendInvite(jordan.getAccountID(), shawn.getAccountID());
        accountService.sendFriendInvite(jordan.getAccountID(), shawn.getAccountID());
        accountService.sendFriendInvite(jordan.getAccountID(), shawn.getAccountID());
        accountService.sendFriendInvite(jordan.getAccountID(), shawn.getAccountID());
        accountService.sendFriendInvite(jordan.getAccountID(), shawn.getAccountID());

        shawn = accountRepository.findByAccountID(shawn.getAccountID());
        assertEquals(5, shawn.getMessageIDs().keySet().size());
        accountService.reportUser(shawn.getAccountID(), jordan.getAccountID(), "Spamming friend invites.");

        kevin = accountRepository.findByAccountID(kevin.getAccountID());
        assertEquals(1, kevin.getMessageIDs().size());
        String messageID = null;
        for(String id : kevin.getMessageIDs().keySet()){
            assertEquals(Types.MOD_REPORT, (int)kevin.getMessageIDs().get(id));
            messageID = id;
        }

        Message message = messageRepository.findByMessageID(messageID);
        assertEquals("Spamming friend invites.", message.getContent());
        List<Message> history = accountService.getMessageHistory(kevin.getAccountID(), message.getTopicID());
        assertEquals(5, history.size());
        assertEquals(Types.FRIEND_INVITE, history.get(0).getType());
        accountService.flagUser(kevin.getAccountID(), messageID);
        kevin.setMessageIDs(new TreeMap<>());
        accountRepository.save(kevin);

        jordan = accountRepository.findByAccountID(jordan.getAccountID());
        shawn = accountRepository.findByAccountID(shawn.getAccountID());
        assertEquals(1, jordan.getFlags());
        assertEquals(0, shawn.getFlags());

        accountService.getMessageHistory(jordan.getAccountID(), shawn.getAccountID());
        accountService.getMessageHistory(jordan.getAccountID(), shawn.getAccountID());
        accountService.getMessageHistory(jordan.getAccountID(), shawn.getAccountID());

        kevin.setMessageIDs(new TreeMap<>());
        accountRepository.save(kevin);

        accountService.reportUser(shawn.getAccountID(), jordan.getAccountID(), "Tried getting access to my message history.");

        kevin = accountRepository.findByAccountID(kevin.getAccountID());
        assertEquals(1, kevin.getMessageIDs().size());
        messageID = null;
        for(String id : kevin.getMessageIDs().keySet()){
            assertEquals(Types.MOD_REPORT, (int)kevin.getMessageIDs().get(id));
            messageID = id;
        }
        message = messageRepository.findByMessageID(messageID);
        assertEquals("Tried getting access to my message history.", message.getContent());
        List<String> log = accountService.getActivityLog(kevin.getAccountID(), message.getTopicID(), null, null);
        assertEquals(true, log.get(log.size()-4).contains("Attempted to use moderator tool"));
        assertEquals(true, log.get(log.size()-3).contains("Attempted to use moderator tool"));
        assertEquals(true, log.get(log.size()-2).contains("Attempted to use moderator tool"));
        assertEquals(true, log.get(log.size()-1).contains("Reported by user Shawn Montgomery"));
        accountService.flagUser(kevin.getAccountID(), messageID);
        kevin.setMessageIDs(new TreeMap<>());
        accountRepository.save(kevin);

        jordan = accountRepository.findByAccountID(jordan.getAccountID());
        assertEquals(2, jordan.getFlags());
        jordan.getLogs().set(jordan.getLogs().size()-3, "Attempted to use moderator tool at 06:30 on 2018-04-01");
        accountRepository.save(jordan);

        log = accountService.getActivityLog(kevin.getAccountID(), message.getTopicID(), "2018-04-07", LocalDate.now().toString());
        assertEquals(false, log.get(log.size()-4).contains("Attempted to use moderator tool"));
        assertEquals(true, log.get(log.size()-3).contains("Attempted to use moderator tool"));
        assertEquals(true, log.get(log.size()-2).contains("Attempted to use moderator tool"));
        assertEquals(true, log.get(log.size()-1).contains("Reported by user Shawn Montgomery"));
    }

    @Test
    public void testSuspendUsers() {
        Account jordan = accountService.createAccount("reed226", "reed226@pdue.edu", "Jordan", "Reed");
        Account shawn = accountService.createAccount("montgo38", "montgo38@pdue.edu", "Shawn", "Montgomery");
        Account kevin = accountService.createAccount("knagar", "knagar@purdue.edu", "Kevin", "Nagar");

        kevin.setMessageIDs(new TreeMap<>());
        accountRepository.save(kevin);

        jordan.setFlags(3);
        accountRepository.save(jordan);

        accountService.reportUser(shawn.getAccountID(), jordan.getAccountID(), "Spamming friend invites.");
        kevin = accountRepository.findByAccountID(kevin.getAccountID());
        assertEquals(1, kevin.getMessageIDs().size());
        String messageID = null;
        for(String id : kevin.getMessageIDs().keySet()){
            assertEquals(Types.MOD_REPORT, (int)kevin.getMessageIDs().get(id));
            messageID = id;
        }
        Message message = messageRepository.findByMessageID(messageID);
        assertEquals("Spamming friend invites.", message.getContent());
        accountService.flagUser(kevin.getAccountID(), messageID);
        kevin.setMessageIDs(new TreeMap<>());
        accountRepository.save(kevin);
        jordan = accountRepository.findByAccountID(jordan.getAccountID());
        assertEquals(false, jordan.isSuspended());

        accountService.reportUser(shawn.getAccountID(), jordan.getAccountID(), "Spamming friend invites.");
        kevin = accountRepository.findByAccountID(kevin.getAccountID());
        assertEquals(1, kevin.getMessageIDs().size());
        for(String id : kevin.getMessageIDs().keySet()){
            assertEquals(Types.MOD_REPORT, (int)kevin.getMessageIDs().get(id));
            messageID = id;
        }
        message = messageRepository.findByMessageID(messageID);
        assertEquals("Spamming friend invites.", message.getContent());
        accountService.flagUser(kevin.getAccountID(), messageID);
        accountService.suspendUser(kevin.getAccountID(), messageID);
        kevin.setMessageIDs(new TreeMap<>());
        accountRepository.save(kevin);
        jordan = accountRepository.findByAccountID(jordan.getAccountID());
        assertEquals(true, jordan.isSuspended());

        Account result = accountService.getUserProfile(jordan.getUsername());

        jordan = accountRepository.findByAccountID(jordan.getAccountID());
        assertEquals(2*24*60, jordan.getSuspendTime());
        jordan.setStartSuspendTime(LocalDateTime.of(2017, Month.JANUARY, 1, 12, 30));
        jordan.setFlags(4);
        accountRepository.save(jordan);

        result = accountService.getUserProfile(jordan.getUsername());
        assertNotNull(result);

        accountService.reportUser(shawn.getAccountID(), jordan.getAccountID(), "Spamming friend invites.");
        kevin = accountRepository.findByAccountID(kevin.getAccountID());
        assertEquals(1, kevin.getMessageIDs().size());
        for(String id : kevin.getMessageIDs().keySet()){
            assertEquals(Types.MOD_REPORT, (int)kevin.getMessageIDs().get(id));
            messageID = id;
        }
        message = messageRepository.findByMessageID(messageID);
        assertEquals("Spamming friend invites.", message.getContent());
        accountService.flagUser(kevin.getAccountID(), messageID);
        accountService.suspendUser(kevin.getAccountID(), messageID);
        kevin.setMessageIDs(new TreeMap<>());
        accountService.suspendUser(kevin.getAccountID(), messageID);
        accountRepository.save(kevin);
        jordan = accountRepository.findByAccountID(jordan.getAccountID());
        assertEquals(true, jordan.isSuspended());

        jordan = accountRepository.findByAccountID(jordan.getAccountID());
        assertEquals(4*24*60, jordan.getSuspendTime());
    }

    /* Populates valid skills into database, in case they were deleted */
    @Before
    public void populateSkills() {
        accountService.addSkillToDatabase("Java");
        accountService.addSkillToDatabase("JavaScript");
        accountService.addSkillToDatabase("C");
        accountService.addSkillToDatabase("C++");
        accountService.addSkillToDatabase("Python");
        accountService.addSkillToDatabase("C#");
        accountService.addSkillToDatabase("Ruby");
        accountService.addSkillToDatabase("Pascal");
        accountService.addSkillToDatabase("ARM");
        accountService.addSkillToDatabase("x86");
        accountService.addSkillToDatabase("Verilog");
        accountService.addSkillToDatabase("VIM");
        accountService.addSkillToDatabase("Microsoft Word");
        accountService.addSkillToDatabase("Google Sheets");
        accountService.addSkillToDatabase("Swift");
        accountService.addSkillToDatabase("Real Time Strategy Games");
        accountService.addSkillToDatabase("Role-Playing Games");
        accountService.addSkillToDatabase("Board Games");
        accountService.addSkillToDatabase("Platformer Games");
        accountService.addSkillToDatabase("Massively Multiplayer Online Role-Playing Games");
        accountService.addSkillToDatabase("Basketball");
        accountService.addSkillToDatabase("Lifting");
        accountService.addSkillToDatabase("Football");
        accountService.addSkillToDatabase("Volleyball");
        accountService.addSkillToDatabase("Baseball");
        accountService.addSkillToDatabase("Soccer");
        accountService.addSkillToDatabase("Tennis");
        accountService.addSkillToDatabase("Really Long Skill Name That Likely Needs To Be Shortened When It Is Shown On The Front End");
    }

    /* Function to clear items that should not already be in database */
    @After
    public void clearDatabase() {
        accountRepository.deleteAll();
        skillRepository.deleteAll();
        messageRepository.deleteAll();
        groupRepository.deleteAll();
    }
}
