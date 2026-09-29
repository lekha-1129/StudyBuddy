package com.example.studybuddy.controller;

import com.example.studybuddy.model.Subject;
import com.example.studybuddy.repo.SubjectRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subjects")
public class SubjectController {

    private final SubjectRepository subjectRepository;

    public SubjectController(
            SubjectRepository subjectRepository) {

        this.subjectRepository = subjectRepository;
    }

    @PostMapping
    public ResponseEntity<Subject> createSubject(
            @RequestBody Subject subject) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(subjectRepository.save(subject));
    }

    @GetMapping
    public List<Subject> getSubjects() {
        return subjectRepository.findAll();
    }
    @GetMapping("/name/{name}")
public ResponseEntity<Subject> getSubjectByName(
        @PathVariable String name) {

    Subject subject = subjectRepository.findByName(name)
            .orElseThrow(() ->
                    new RuntimeException("Subject not found"));

    return ResponseEntity.ok(subject);
}
}