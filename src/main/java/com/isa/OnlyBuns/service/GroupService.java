package com.isa.OnlyBuns.service;

import com.isa.OnlyBuns.dto.GroupDTO;
import com.isa.OnlyBuns.irepository.IGroupMembershipRepository;
import com.isa.OnlyBuns.irepository.IGroupRepository;
import com.isa.OnlyBuns.irepository.IUserRepository;
import com.isa.OnlyBuns.model.Group;
import com.isa.OnlyBuns.model.GroupMembership;
import com.isa.OnlyBuns.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class GroupService {
    @Autowired
    private IGroupRepository groupRepository;
    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private IGroupMembershipRepository groupMembershipRepository;

//    public Group createGroup(Group group){
//        return groupRepository.save(group);
//    }
    public Group createGroup(GroupDTO groupDTO,String username) throws IOException{
        User currentUser = userRepository.findByUsername(username);
        if (currentUser == null) {
            throw new IllegalArgumentException("The user is not authenticated");
        }

        Group group = new Group();
        group.setGroupName(groupDTO.getGroupName());
        group.setAdminId(currentUser.getId());

        Set<User> members=new HashSet<>();
        members.add(currentUser);

        //ostali clanovi
        for(String memberUsername:groupDTO.getMemberUsernames()){
            User member=userRepository.findByUsername(memberUsername);
            if(member!=null){
                members.add(member);
            }
        }
        group.setMembers(members);
        Group savedGroup = groupRepository.save(group);
//        group.getMembers().add(currentUser);//bolje ovako
//        group.setMembers((Set<User>) currentUser);//ne znam jel moze ovako to proveri
        List<User> updatedMembers=new ArrayList<>(members);
        for(User member:updatedMembers){
            member.getGroups().add(savedGroup);
            userRepository.save(member);


            //dodato
            GroupMembership membership = new GroupMembership();
            membership.setUser(member);
            membership.setGroup(savedGroup);
            membership.setJoinedAt(LocalDateTime.now());
            groupMembershipRepository.save(membership);
        }
         //return groupRepository.save(group);
        return savedGroup;
    }

    public void addUserToGroup(Integer groupId, User userInput){
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        User user = userRepository.findById(userInput.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (group.getMembers().contains(user)) return;


        group.getMembers().add(user);
        user.getGroups().add(group);
        userRepository.save(user);
        groupRepository.save(group);

        //for ten messages
        GroupMembership membership = new GroupMembership();
        membership.setGroup(group);
        membership.setUser(user);
        membership.setJoinedAt(LocalDateTime.now());
        groupMembershipRepository.save(membership);
    }

    public void removeUserFromGroup(Integer groupId, User userInput){
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        User user = userRepository.findById(userInput.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean removed = group.getMembers().remove(user); // ovo sad treba da radi
        user.getGroups().remove(group);
        userRepository.save(user);
        groupRepository.save(group);
    }
    public List<Group> getGroupsForUser(Long userId) {
        return groupRepository.findByMembersId(userId);
    }

    public Group getGroupById(Integer groupId) {
        return groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
    }

//    public Group createDirectChat(User u1, User u2) {
//        Group group = new Group();
//        group.setGroupName(null);//no name-direct chat
//        group.setMembers(new HashSet<>(List.of(u1, u2)));
//        group.setAdminId(u1.getId());
//        return groupRepository.save(group);
//    }
    public Group createDirectChat(User u1, User u2) {
        Group group = new Group();
        group.setGroupName(null); // direct chat
        group.setAdminId(u1.getId());

        Set<User> members = new HashSet<>(List.of(u1, u2));
        group.setMembers(members);

        Group savedGroup = groupRepository.save(group);

        u1.getGroups().add(savedGroup);
        u2.getGroups().add(savedGroup);
        userRepository.save(u1);
        userRepository.save(u2);

        LocalDateTime now = LocalDateTime.now();
        GroupMembership m1 = new GroupMembership();
        m1.setUser(u1);
        m1.setGroup(savedGroup);
        m1.setJoinedAt(now);
        groupMembershipRepository.save(m1);

        GroupMembership m2 = new GroupMembership();
        m2.setUser(u2);
        m2.setGroup(savedGroup);
        m2.setJoinedAt(now);
        groupMembershipRepository.save(m2);
        return savedGroup;
    }

    public Optional<Group> findDirectChat(Long userId1, Long userId2) {
        return groupRepository.findAll().stream()
                .filter(g -> g.getGroupName() == null && g.getMembers().size() == 2)
                .filter(g ->
                        g.getMembers().stream().anyMatch(u -> u.getId().equals(userId1)) &&
                                g.getMembers().stream().anyMatch(u -> u.getId().equals(userId2))
                )
                .findFirst();
    }





}
