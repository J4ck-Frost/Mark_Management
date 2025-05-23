package com.examManagement.ExamAdministrationService.service;

import com.examManagement.ExamAdministrationService.Mapper.ExaminerMapper;
import com.examManagement.ExamAdministrationService.dto.ExaminerRequest;
import com.examManagement.ExamAdministrationService.dto.ExaminerResponse;
import com.examManagement.ExamAdministrationService.dto.ExaminerUpdateRequest;
import com.examManagement.ExamAdministrationService.entity.Examiner;
import com.examManagement.ExamAdministrationService.exception.ResourceNotFoundException;
import com.examManagement.ExamAdministrationService.repository.ExaminerRepository;
import com.examManagement.ExamAdministrationService.validation.ExaminerValidator;
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
    private final ExaminerValidator examinerValidator;
    private final ExaminerMapper examinerMapper;


    @Override
    public ExaminerResponse createExaminer(ExaminerRequest request) {
        Examiner examiner = examinerMapper.toEntity(request);
        examinerRepository.save(examiner);
        return examinerMapper.toResponse(examiner);
    }

    @Override
    public List<ExaminerResponse> getAllExaminers() {
        return examinerRepository.findAll().stream()
                .map(examinerMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ExaminerResponse getExaminerById(String id) {
        Examiner examiner = examinerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Examiner not found"));
        return examinerMapper.toResponse(examiner);
    }

    @Override
    public ExaminerResponse updateExaminer(String id, ExaminerUpdateRequest request) {
        Examiner updatedExaminer = examinerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Examiner not found"));
        if (!updatedExaminer.getEmail().equals(request.getEmail())) {
            examinerValidator.validateExaminerEmail(request.getEmail());
            }
        if (!updatedExaminer.getPhoneNumber().equals(request.getPhoneNumber())) {
            examinerValidator.validateExaminerPhoneNumber(request.getPhoneNumber());
        }

        updatedExaminer.setName(request.getName());
        updatedExaminer.setEmail(request.getEmail());
        updatedExaminer.setPhoneNumber(request.getPhoneNumber());
        examinerRepository.save(updatedExaminer);
        return examinerMapper.toResponse(updatedExaminer);
    }


    @Override
    public ExaminerResponse inactiveExaminer(String id){
        Examiner examiner = examinerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found"));
        examinerValidator.validateExaminerNotAssignedToExams(id);

        examiner.setActive(false);
        examinerRepository.save(examiner);
        return  examinerMapper.toResponse(examiner);
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
