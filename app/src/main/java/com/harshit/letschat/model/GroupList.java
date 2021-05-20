package com.harshit.letschat.model;

public class GroupList {

    private String id;
    private String groupName;
    private String groupImage;
    private String groupAdmin;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupImage() {
        return groupImage;
    }

    public void setGroupImage(String groupImage) {
        this.groupImage = groupImage;
    }

    public GroupList(String id, String groupName, String groupImage) {
        this.id = id;
        this.groupName = groupName;
        this.groupImage = groupImage;
    }

    public GroupList(String id) {
        this.id = id;
        this.groupImage = "";
        this.groupName = "";

    }

}
