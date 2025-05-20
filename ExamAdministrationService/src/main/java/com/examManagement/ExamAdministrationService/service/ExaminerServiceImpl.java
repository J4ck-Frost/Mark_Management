package com.examManagement.ExamAdministrationService.service;

import com.examManagement.ExamAdministrationService.Mapper.ExaminerMapper;
import com.examManagement.ExamAdministrationService.dto.ExaminerRequest;
import com.examManagement.ExamAdministrationService.dto.ExaminerResponse;
import com.examManagement.ExamAdministrationService.entity.Exam;
import com.examManagement.ExamAdministrationService.entity.Examiner;
import com.examManagement.ExamAdministrationService.exception.ResourceNotFoundException;
import com.examManagement.ExamAdministrationService.repository.ExamRepository;
import com.examManagement.ExamAdministrationService.repository.ExaminerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ExaminerServiceImpl implements ExaminerService{
    private final ExaminerRepository examinerRepository;
    private final ExamRepository examRepository;


    @Override
    public ExaminerResponse createExaminer(ExaminerRequest request) {
        if (examinerRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        Examiner examiner = ExaminerMapper.toEntity(request);
        examinerRepository.save(examiner);
        return ExaminerMapper.toResponse(examiner);
    }

    @Override
    public List<ExaminerResponse> getAllExaminers() {
        return examinerRepository.findAll().stream()
                .map(ExaminerMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ExaminerResponse getExaminerById(String id) {
        Examiner examiner = examinerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Examiner not found"));
        return ExaminerMapper.toResponse(examiner);
    }

    @Override
    public ExaminerResponse updateExaminer(String id, ExaminerRequest request) {
        Examiner updatedExaminer = examinerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Examiner not found"));
        if (!updatedExaminer.getEmail().equals(request.getEmail())) {
            if (examinerRepository.existsByEmail(request.getEmail())) {
                throw new IllegalArgumentException("Email already exists");
            }
        }
        if (!updatedExaminer.getPhoneNumber().equals(request.getPhoneNumber())) {
            if (examinerRepository.existsByPhoneNumber(request.getPhoneNumber())) {
                throw new IllegalArgumentException("Phone number already exists");
            }
        }

        updatedExaminer.setName(request.getName());
        updatedExaminer.setEmail(request.getEmail());
        updatedExaminer.setPhoneNumber(request.getPhoneNumber());
        examinerRepository.save(updatedExaminer);
        return ExaminerMapper.toResponse(updatedExaminer);
    }


    @Override
    public ExaminerResponse inactiveExaminer(String id){
        Examiner examiner = examinerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found"));
        List<Exam> assignedExams = examRepository.findByAssignedExaminerId(id);
        if (!assignedExams.isEmpty()) {
            String assignedExamIds = assignedExams.stream()
                    .map(Exam::getId)
                    .collect(Collectors.joining(", "));
            throw new IllegalStateException("Examiner is assigned to exam(s): " + assignedExamIds);
        }
        examiner.setActive(false);
        examinerRepository.save(examiner);
        return ExaminerMapper.toResponse(examiner);
    }

    public void validateExaminerIds(List<String> examinerIds) {
        List<String> missingIds = examinerIds.stream()
                .filter(id -> !examinerRepository.existsById(id))
                .toList();

        if (!missingIds.isEmpty()) {
            throw new ResourceNotFoundException(
                    "Examiners not found with IDs: " + missingIds
            );
        }
    }
}
