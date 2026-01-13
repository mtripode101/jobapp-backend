package com.mtripode.jobapp.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mtripode.jobapp.facade.dto.InterviewDto;
import com.mtripode.jobapp.facade.facade.InterviewFacade;
import com.mtripode.jobapp.facade.facade.impl.InterviewFacadeImpl;


@RestController
@RequestMapping("/interview")
@CrossOrigin(origins = "http://localhost:3000") // habilita CORS solo para este controlador
public class InterviewController {

    private final InterviewFacade interviewFacade;

    public InterviewController(InterviewFacadeImpl interviewFacade) {
        this.interviewFacade = interviewFacade;
    }

    @GetMapping("/{id}")
    public ResponseEntity<InterviewDto> getInterviewById(@PathVariable Long id) {
        return interviewFacade.getInterviewById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }    

    @GetMapping
    public List<InterviewDto> getAllInterviews() {
        return this.interviewFacade.getAllInterviews();
    }

    @PostMapping
    public ResponseEntity<InterviewDto> createInterview(@RequestBody InterviewDto interviewDto) {
        return ResponseEntity.ok(this.interviewFacade.saveInterview(interviewDto));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<InterviewDto> updateInterview(@PathVariable Long id, @RequestBody InterviewDto interviewDto) {
        // Assuming InterviewDto has a setId method
        interviewDto.setId(id);
        return ResponseEntity.ok(this.interviewFacade.saveInterview(interviewDto));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInterview(@PathVariable Long id) {    
        this.interviewFacade.deleteInterview(id);
        return ResponseEntity.noContent().build();
    }
}
