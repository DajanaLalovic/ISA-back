package com.isa.OnlyBuns.controller;

import com.isa.OnlyBuns.dto.GroupDTO;
import com.isa.OnlyBuns.dto.PostDTO;
import com.isa.OnlyBuns.model.ChatMessage;
import com.isa.OnlyBuns.model.Group;
import com.isa.OnlyBuns.model.User;
import com.isa.OnlyBuns.service.ChatMessageService;
import com.isa.OnlyBuns.service.GroupService;
import com.isa.OnlyBuns.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value="/api/groups",produces= MediaType.APPLICATION_JSON_VALUE)
public class GroupController {
    @Autowired
    private GroupService groupService;

    @Autowired
    private ChatMessageService chatMessageService;
    @Autowired
    private UserService userService;

    @PostMapping("/create")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<GroupDTO> createGroup(@RequestBody GroupDTO groupDTO, Principal principal){
//        String groupName=group.get("groupName");
//        String adminUsername=principal.getName();
//        User admin=userService.findByUsername(adminUsername);
//
////        Group newGroup=groupService.createGroup(groupName,admin);
//        return ResponseEntity.ok(newGroup);
        try{
            Group savedGroup=groupService.createGroup(groupDTO,principal.getName());
            return new ResponseEntity<>(new GroupDTO(savedGroup), HttpStatus.CREATED);
        }catch(IOException e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @PutMapping("/addUser/{groupId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<GroupDTO> addUser(@PathVariable Integer groupId,
                                            @RequestBody Map<String,Long> body,
                                            Principal principal) {
        try {

            Long userId = body.get("userId");
            if (userId == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            User user = userService.findById(userId);
            if (user == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            User currentUser = userService.findByUsername(principal.getName());
            if (currentUser == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            groupService.addUserToGroup(groupId,user);
            return new ResponseEntity<>( HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/removeUser/{groupId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<GroupDTO> removeUser(@PathVariable Integer groupId, Principal principal) {
        try {
            User currentUser = userService.findByUsername(principal.getName());
            if (currentUser == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            groupService.removeUserFromGroup(groupId,currentUser);
            return new ResponseEntity<>( HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    //ili groupDTO?
    @GetMapping("/all")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Group>> getGroupsForUser(Principal principal) {
        try{
            User currentUser = userService.findByUsername(principal.getName());
            if (currentUser == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            List<Group> allGroups=groupService.getGroupsForUser(currentUser.getId());
            return new ResponseEntity<>(allGroups, HttpStatus.OK);

        }catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{groupId}")
    @PreAuthorize("isAuthenticated()")
    //ili izbaci principal?
    public ResponseEntity<Group> getGroup(@PathVariable Integer groupId, Principal principal) {
        try{
            Group group=groupService.getGroupById(groupId);
            return new ResponseEntity<>(group, HttpStatus.OK);
        }catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/chat/{groupId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ChatMessage>> getChatMessages(@PathVariable Integer groupId, Principal principal) {
        try{
            List<ChatMessage> lastTenMessages=chatMessageService.getLastMessages(groupId);
            return new ResponseEntity<>(lastTenMessages, HttpStatus.OK);
        }
        catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        }
    }
    //CUVANJE PORUKA???

    //dodaj ako treba

    @PostMapping("/send")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ChatMessage> sendMessage(@RequestBody ChatMessage chatMessage, Principal principal) {
        try{
//            Integer senderId = Integer.parseInt(principal.getName());
                System.out.println(chatMessage);
                System.out.println("Name "+principal.getName());
//            chatMessage.setSenderId(principal.);
            chatMessage.setTimestamp(LocalDateTime.now());
            System.out.println("Saving message: " + chatMessage);

            chatMessageService.saveMessage(chatMessage);
            return new ResponseEntity<>(chatMessage, HttpStatus.CREATED);

        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/private/{receiverId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ChatMessage>> getPrivateMessages(@PathVariable Integer receiverId,Principal principal) {
        try{
            Integer senderId = Integer.parseInt(principal.getName());

            if (receiverId == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST); 
            }            List<ChatMessage> privateMessages=chatMessageService.getAllPrivateMessages(receiverId,senderId);
            return new ResponseEntity<>(privateMessages, HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }



}




