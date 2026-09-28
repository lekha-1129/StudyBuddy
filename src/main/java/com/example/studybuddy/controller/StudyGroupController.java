package com.example.studybuddy.controller;

import com.example.studybuddy.dto.CreateGroupRequest;
import com.example.studybuddy.dto.GroupResponse;
import com.example.studybuddy.model.StudyGroup;
import com.example.studybuddy.service.StudyGroupService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
public class StudyGroupController {

    private final StudyGroupService studyGroupService;

    public StudyGroupController(
            StudyGroupService studyGroupService) {

        this.studyGroupService = studyGroupService;
    }

    @PostMapping
    public ResponseEntity<GroupResponse> createGroup(
            @RequestBody CreateGroupRequest request) {

        StudyGroup group =
                studyGroupService.createGroup(
                        request.getSubjectId(),
                        request.getCreatorId(),
                        request.getMaxMembers()
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toResponse(group));
    }

    @PostMapping("/{groupId}/join/{studentId}")
    public ResponseEntity<GroupResponse> joinGroup(
            @PathVariable Long groupId,
            @PathVariable Long studentId) {

        StudyGroup group =
                studyGroupService.joinGroup(
                        groupId,
                        studentId
                );

        return ResponseEntity.ok(toResponse(group));
    }

    @DeleteMapping("/{groupId}/leave/{studentId}")
    public ResponseEntity<String> leaveGroup(
            @PathVariable Long groupId,
            @PathVariable Long studentId) {

        studyGroupService.leaveGroup(
                groupId,
                studentId
        );

        return ResponseEntity.ok(
                "Student left the group successfully"
        );
    }

    @GetMapping("/subject/{subjectId}")
    public ResponseEntity<List<GroupResponse>>
    getGroupsBySubject(
            @PathVariable Long subjectId) {

        List<GroupResponse> groups =
                studyGroupService
                        .getGroupsBySubject(subjectId)
                        .stream()
                        .map(this::toResponse)
                        .toList();

        return ResponseEntity.ok(groups);
    }

    @DeleteMapping("/{groupId}/members/{memberId}")
    public ResponseEntity<String> removeMember(
            @PathVariable Long groupId,
            @PathVariable Long memberId,
            @RequestParam Long creatorId) {

        studyGroupService.removeMember(
                groupId,
                creatorId,
                memberId
        );

        return ResponseEntity.ok(
                "Member removed successfully"
        );
    }

    private GroupResponse toResponse(
            StudyGroup group) {

        return new GroupResponse(
                group.getId(),
                group.getSubject().getName(),
                group.getMaxMembers(),
                group.getCurrentMemberCount()
        );
    }
}