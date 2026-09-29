package com.example.studybuddy.service;

import com.example.studybuddy.model.StudyGroup;

import java.util.List;


public interface StudyGroupService {

    StudyGroup createGroup(
            Long subjectId,
            Long creatorId,
            int maxMembers
    );

    StudyGroup joinGroup(
            Long groupId,
            Long studentId
    );

    void leaveGroup(
            Long groupId,
            Long studentId
    );

    List<StudyGroup> getGroupsBySubject(
            Long subjectId
    );

    void removeMember(
            Long groupId,
            Long creatorId,
            Long memberId
    );

}