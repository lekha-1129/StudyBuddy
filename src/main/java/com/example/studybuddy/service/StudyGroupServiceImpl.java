package com.example.studybuddy.service;

import com.example.studybuddy.model.Student;
import com.example.studybuddy.model.Subject;
import com.example.studybuddy.model.StudyGroup;
import com.example.studybuddy.model.StudyGroup.Membership;
import com.example.studybuddy.repo.StudentRepository;
import com.example.studybuddy.repo.SubjectRepository;
import com.example.studybuddy.repo.StudyGroupRepository;
import com.example.studybuddy.exception.DuplicateMembershipException;
import com.example.studybuddy.exception.GroupFullException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudyGroupServiceImpl implements StudyGroupService {

    private final StudyGroupRepository studyGroupRepository;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;

    public StudyGroupServiceImpl(
            StudyGroupRepository studyGroupRepository,
            StudentRepository studentRepository,
            SubjectRepository subjectRepository) {

        this.studyGroupRepository = studyGroupRepository;
        this.studentRepository = studentRepository;
        this.subjectRepository = subjectRepository;
    }

    @Override
    public StudyGroup createGroup(
            Long subjectId,
            Long creatorId,
            int maxMembers) {

        if (maxMembers <= 0) {
            throw new IllegalArgumentException(
                    "Maximum members must be greater than zero"
            );
        }

        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Subject not found"
                        ));

        Student creator = studentRepository.findById(creatorId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Creator student not found"
                        ));

        StudyGroup group = new StudyGroup();

        group.setSubject(subject);
        group.setCreator(creator);
        group.setMaxMembers(maxMembers);

        Membership membership =
                new Membership(creator, group);

        group.getMemberships().add(membership);

        return studyGroupRepository.save(group);
    }

    @Override
    public StudyGroup joinGroup(
            Long groupId,
            Long studentId) {

        StudyGroup group = studyGroupRepository.findById(groupId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Study group not found"
                        ));

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Student not found"
                        ));

        // RULE 1: Student cannot join twice
        boolean alreadyMember =
                group.getMemberships()
                        .stream()
                        .anyMatch(m ->
                                m.getStudent()
                                        .getId()
                                        .equals(studentId));

        if (alreadyMember) {
            throw new DuplicateMembershipException(
                    "Student has already joined this group"
            );
        }

        // RULE 2: Check capacity BEFORE saving
        if (group.getCurrentMemberCount()
                >= group.getMaxMembers()) {

            throw new GroupFullException(
                    "Study group is full. Maximum members: "
                            + group.getMaxMembers()
            );
        }

        Membership membership =
                new Membership(student, group);

        group.getMemberships().add(membership);

        return studyGroupRepository.save(group);
    }

    @Override
    public void leaveGroup(
            Long groupId,
            Long studentId) {

        StudyGroup group = studyGroupRepository.findById(groupId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Study group not found"
                        ));

        Membership membership =
                group.getMemberships()
                        .stream()
                        .filter(m ->
                                m.getStudent()
                                        .getId()
                                        .equals(studentId))
                        .findFirst()
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Student is not a member of this group"
                                ));

        group.getMemberships().remove(membership);

        studyGroupRepository.save(group);
    }

    @Override
    public List<StudyGroup> getGroupsBySubject(
            Long subjectId) {

        if (!subjectRepository.existsById(subjectId)) {
            throw new IllegalArgumentException(
                    "Subject not found"
            );
        }

        return studyGroupRepository
                .findBySubjectId(subjectId);
    }

    @Override
    public void removeMember(
            Long groupId,
            Long creatorId,
            Long memberId) {

        StudyGroup group = studyGroupRepository.findById(groupId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Study group not found"
                        ));

        // Only creator can remove
        if (!group.getCreator()
                .getId()
                .equals(creatorId)) {

            throw new SecurityException(
                    "Only the group creator can remove members"
            );
        }

        Membership membership =
                group.getMemberships()
                        .stream()
                        .filter(m ->
                                m.getStudent()
                                        .getId()
                                        .equals(memberId))
                        .findFirst()
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Student is not a member of this group"
                                ));

        group.getMemberships().remove(membership);

        studyGroupRepository.save(group);
    }
}