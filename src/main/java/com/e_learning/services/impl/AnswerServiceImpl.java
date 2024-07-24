package com.e_learning.services.impl;

import java.time.LocalDateTime;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.e_learning.entities.Answer;
import com.e_learning.entities.Exam;
import com.e_learning.exceptions.ResourceNotFoundException;
import com.e_learning.payloads.AnswerDto;
import com.e_learning.repositories.AnswerRepo;
import com.e_learning.repositories.ExamRepo;
import com.e_learning.services.AnswerService;

@Service
public class AnswerServiceImpl implements AnswerService {

    @Autowired
    private ExamRepo examRepo;

    @Autowired
    private AnswerRepo answerRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public AnswerDto createAnswer(AnswerDto answerDto, Integer examId) {
        Exam exam = this.examRepo.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam", "exam id", examId));

        Answer answer = this.modelMapper.map(answerDto, Answer.class);

        answer.setExam(exam);
        answer.setAddedDate(LocalDateTime.now());
        answer.setImageName("default.png");

        Answer savedAnswer = this.answerRepo.save(answer);

        return this.modelMapper.map(savedAnswer, AnswerDto.class);
    }

    @Override
    public void deleteAnswer(Integer answerId) {
        Answer answer = this.answerRepo.findById(answerId)
                .orElseThrow(() -> new ResourceNotFoundException("Answer", "answer id", answerId));
        this.answerRepo.delete(answer);
    }

    @Override
    public AnswerDto updateAnswer(AnswerDto answerDto, Integer answerId) {
        Answer answer = this.answerRepo.findById(answerId)
                .orElseThrow(() -> new ResourceNotFoundException("Answer", "answer id", answerId));

        answer.setImageName(answerDto.getImageName());

        Answer updatedAnswer = this.answerRepo.save(answer);
        return this.modelMapper.map(updatedAnswer, AnswerDto.class);
    }

    @Override
    public AnswerDto getAnswerById(Integer answerId) {
        Answer answer = this.answerRepo.findById(answerId)
                .orElseThrow(() -> new ResourceNotFoundException("Answer", "answer id", answerId));
        return this.modelMapper.map(answer, AnswerDto.class);
    }
}
