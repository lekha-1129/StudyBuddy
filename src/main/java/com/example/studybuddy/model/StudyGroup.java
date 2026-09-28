package com.example.studybuddy.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "study_groups")
public class StudyGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int maxMembers;

    @ManyToOne
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @ManyToOne
    @JoinColumn(name = "creator_id", nullable = false)
    private Student creator;

    @OneToMany(
        mappedBy = "studyGroup",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<Membership> memberships = new ArrayList<>();

    public StudyGroup() {
    }

    public Long getId() {
        return id;
    }

    public int getMaxMembers() {
        return maxMembers;
    }

    public void setMaxMembers(int maxMembers) {
        this.maxMembers = maxMembers;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public Student getCreator() {
        return creator;
    }

    public void setCreator(Student creator) {
        this.creator = creator;
    }

    public List<Membership> getMemberships() {
        return memberships;
    }

    public int getCurrentMemberCount() {
        return memberships.size();
    }

    @Entity
    @Table(
        name = "memberships",
        uniqueConstraints = @UniqueConstraint(
            columnNames = {"student_id", "study_group_id"}
        )
    )
    public static class Membership {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne
        @JoinColumn(name = "student_id", nullable = false)
        private Student student;

        @ManyToOne
        @JoinColumn(name = "study_group_id", nullable = false)
        private StudyGroup studyGroup;

        public Membership() {
        }

        public Membership(Student student, StudyGroup studyGroup) {
            this.student = student;
            this.studyGroup = studyGroup;
        }

        public Long getId() {
            return id;
        }

        public Student getStudent() {
            return student;
        }

        public StudyGroup getStudyGroup() {
            return studyGroup;
        }
    }
}