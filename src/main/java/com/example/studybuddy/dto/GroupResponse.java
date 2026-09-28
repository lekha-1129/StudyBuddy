package com.example.studybuddy.dto;

public class GroupResponse {

    private Long id;
    private String subject;
    private int maxMembers;
    private int currentMemberCount;

    public GroupResponse(
            Long id,
            String subject,
            int maxMembers,
            int currentMemberCount) {

        this.id = id;
        this.subject = subject;
        this.maxMembers = maxMembers;
        this.currentMemberCount = currentMemberCount;
    }

    public Long getId() {
        return id;
    }

    public String getSubject() {
        return subject;
    }

    public int getMaxMembers() {
        return maxMembers;
    }

    public int getCurrentMemberCount() {
        return currentMemberCount;
    }
}