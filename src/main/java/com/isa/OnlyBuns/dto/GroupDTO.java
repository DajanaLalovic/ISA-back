package com.isa.OnlyBuns.dto;

import com.isa.OnlyBuns.model.Group;
import com.isa.OnlyBuns.model.User;

import java.util.Set;
import java.util.stream.Collectors;

public class GroupDTO {

    private Integer id;
    private String groupName;
    private Long adminId;
    private Set<String> memberUsernames; //samo korisnicko ime clanova

    public GroupDTO(){}

    public GroupDTO(Group group)
    {
        this.id=group.getId();
        this.groupName= group.getGroupName();
        this.adminId=group.getAdminId();
        this.memberUsernames=group.getMembers().stream().map(User::getUsername).collect(Collectors.toSet());
    }

    public Integer getId() {return id;}
    public String getGroupName() {return groupName;}
    public Long getAdminId() {return adminId;}
    public Set<String> getMemberUsernames() {return memberUsernames;}

    public void setId(Integer id) {this.id = id;}
    public void setGroupName(String groupName) {this.groupName = groupName;}

    public void setAdminId(Long adminId) {this.adminId = adminId;}
    public void setMemberUsernames(Set<String> memberUsernames) {this.memberUsernames = memberUsernames;}

    @Override
    public String toString() {
        return "GroupDTO{" +
                "id=" + id +
                ", groupName='" + groupName + '\'' +
                ", adminId='" + adminId + '\'' +
                ", memberUsernames=" + memberUsernames +
                '}';
    }
}
