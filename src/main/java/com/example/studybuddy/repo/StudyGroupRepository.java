package com.example.studybuddy.repo;

import com.example.studybuddy.model.StudyGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudyGroupRepository extends JpaRepository<StudyGroup, Long> {

    List<StudyGroup> findBySubjectId(Long subjectId);
}