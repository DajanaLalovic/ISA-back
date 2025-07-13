package com.isa.OnlyBuns.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import javax.annotation.processing.Generated;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "CHAT_GROUP")
public class Group {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String groupName;

    @Column(name = "admin_id", nullable = false)
    private Long adminId; // admin grupe

//    @JsonIgnore
//    @ManyToMany(mappedBy = "groups", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private Set<User> members = new HashSet<>();
    @JsonIgnore
    @ManyToMany(mappedBy = "groups")
    private Set<User> members = new HashSet<>();


    public Group() { }

    public Group(Integer id, String groupName, Long adminId, Set<User> members) {
        this.id = id;
        this.groupName = groupName;
        this.adminId = adminId;
        this.members = members;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Long getAdminId() {
        return adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }

    public Set<User> getMembers() {
        return members;
    }

    public void setMembers(Set<User> members) {
        this.members = members;
    }

    @Override
    public String toString() {
        return "Group{" +
                "id=" + id +
                ", groupName='" + groupName + '\'' +
                ", adminId=" + adminId +
                '}';
    }
}
