package com.cst438.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.cst438.domain.Assignment;
import com.cst438.domain.AssignmentDTO;
import com.cst438.domain.AssignmentRepository;
import com.cst438.domain.Course;
import com.cst438.domain.CourseRepository;

@RestController
@CrossOrigin 
public class AssignmentController {
	
	@Autowired
	AssignmentRepository assignmentRepository;
	
	@Autowired
	CourseRepository courseRepository;
	
	@GetMapping("/assignment")
	public AssignmentDTO[] getAllAssignmentsForInstructor() {
		// get all assignments for this instructor
		String instructorEmail = "dwisneski@csumb.edu";  // user name (should be instructor's email) 
		List<Assignment> assignments = assignmentRepository.findByEmail(instructorEmail);
		AssignmentDTO[] result = new AssignmentDTO[assignments.size()];
		for (int i=0; i<assignments.size(); i++) {
			Assignment as = assignments.get(i);
			AssignmentDTO dto = new AssignmentDTO(
					as.getId(), 
					as.getName(), 
					as.getDueDate().toString(), 
					as.getCourse().getTitle(), 
					as.getCourse().getCourse_id());
			result[i]=dto;
		}
		return result;
	}
	
	// TODO create CRUD methods for Assignment
	
	// get by primary key
	@GetMapping("/assignment/{assignment_id}")
	public AssignmentDTO getAssignment(@PathVariable("assignment_id") int assignment_id) {
		String instructorEmail = "dwisneski@csumb.edu";  // user name (should be instructor's email) 
		List<Assignment> assignments = assignmentRepository.findByEmail(instructorEmail);
		Assignment temp = assignments.get(0);
		for (int i=0; i<assignments.size(); i++) {
			Assignment as = assignments.get(i);
			if (as.getId() == assignment_id) {
				temp = as;
				break;
			}
		}
		AssignmentDTO result = new AssignmentDTO(
					temp.getId(),
					temp.getName(),
					temp.getDueDate().toString(),
					temp.getCourse().toString(),
					temp.getCourse().getCourse_id());
		
		return result;
		
	}
	
	// create a new course, return the system generated assignment_id
	@PostMapping("/assignment")
	public int createAssignment(@RequestBody AssignmentDTO assignmentDTO) {
		Assignment temp = new Assignment();
		temp.setName(assignmentDTO.assignmentName());
		Assignment saved = assignmentRepository.save(temp);
		return saved.getId();
	}
	
	// delete a course
	//   DELETE /course/12389
	//   DELETE /course/12389?force=yes
	//    @RequestParam("force") Optional<String> force)
	@DeleteMapping("/assignment/{assignment_id}")
	public void deleteAssignment(@PathVariable("assignment_id") int assignment_id,
							@RequestParam("force") Optional<String> force) {
		if (force.equals("yes")) {
			assignmentRepository.deleteById(assignment_id);
		} else {
			
			boolean hasGrades = assignmentRepository.findById(assignment_id).isPresent();

            if (hasGrades) {
                // Warn the user about associated grades and do not delete
                System.out.println("Assignment has associated grades. Delete with force=yes to proceed.");
            } else {
                // Perform a regular deletion if no associated grades
                assignmentRepository.deleteById(assignment_id);
                System.out.println("Assignment deleted.");
            }
		}
		
	}
	
	@PutMapping("/assignment/{assingment_id}")
	public void updateAssignment(@PathVariable("assignment_id") int assignment_id,
							@RequestBody AssignmentDTO assignmentDTO) {
		Optional<Assignment> temp = assignmentRepository.findById(assignment_id);
		Assignment curr = temp.get();
		
		curr.setId(assignmentDTO.id());
		curr.setName(assignmentDTO.assignmentName());
		
	}
}
