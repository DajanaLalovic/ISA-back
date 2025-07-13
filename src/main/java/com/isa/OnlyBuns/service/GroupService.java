package com.isa.OnlyBuns.service;

import com.isa.OnlyBuns.dto.GroupDTO;
import com.isa.OnlyBuns.irepository.IGroupRepository;
import com.isa.OnlyBuns.irepository.IUserRepository;
import com.isa.OnlyBuns.model.Group;
import com.isa.OnlyBuns.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class GroupService {
    @Autowired
    private IGroupRepository groupRepository;
    @Autowired
    private IUserRepository userRepository;

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
        }
         //return groupRepository.save(group);
        return savedGroup;
    }

    public void addUserToGroup(Integer groupId, User userInput){
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        User user = userRepository.findById(userInput.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        group.getMembers().add(user);
        user.getGroups().add(group);
        userRepository.save(user);
        groupRepository.save(group);
    }

    public void removeUserFromGroup(Integer groupId, User user){
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        group.getMembers().remove(user);
        groupRepository.save(group);
    }
    public List<Group> getGroupsForUser(Long userId) {
        return groupRepository.findByMembersId(userId);
    }

    public Group getGroupById(Integer groupId) {
        return groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
    }



}
