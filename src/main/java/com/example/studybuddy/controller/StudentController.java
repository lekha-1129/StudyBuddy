package com.example.studybuddy.controller;


import com.example.studybuddy.model.Student;
import com.example.studybuddy.repo.StudentRepository;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@CrossOrigin
public class StudentController {

    private final StudentRepository studentRepository;
   

    public StudentController(
            StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
          }

    @PostMapping
    public ResponseEntity<Student> createStudent(
            @RequestBody Student student) {

        return ResponseEntity
                .ok(studentRepository.save(student));
    }

    @GetMapping
    public List<Student> getStudents() {

        return studentRepository.findAll();
    }

}

