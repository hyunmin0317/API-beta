package com.smunity.api.domain.question.service.impl;

import com.smunity.api.domain.account.entity.User;
import com.smunity.api.domain.account.repository.UserRepository;
import com.smunity.api.domain.question.entity.Answer;
import com.smunity.api.domain.question.entity.Question;
import com.smunity.api.domain.question.dto.AnswerDto;
import com.smunity.api.domain.question.repository.AnswerRepository;
import com.smunity.api.domain.question.repository.QuestionRepository;
import com.smunity.api.domain.question.service.AnswerService;
import com.smunity.api.global.config.security.JwtTokenProvider;
import com.smunity.api.global.error.exception.RestException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnswerServiceImpl implements AnswerService {
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

    @Override
    public AnswerDto.Response createAnswer(Long questionId, AnswerDto.Request answerDto, String token) {
        if (!jwtTokenProvider.validateToken(token))
            throw new RestException(HttpStatus.UNAUTHORIZED);
        if (!jwtTokenProvider.getIsSuperuser(token))
            throw new RestException(HttpStatus.FORBIDDEN);
        if (answerRepository.existsByQuestionId(questionId))
            throw new RestException(HttpStatus.CONFLICT);
        Question question = questionRepository.findById(questionId).orElseThrow(() -> new RestException(HttpStatus.NOT_FOUND));
        String username = jwtTokenProvider.getUsername(token);
        User user = userRepository.getByUsername(username).orElseThrow(() -> new RestException(HttpStatus.NOT_FOUND));
        Answer answer = answerDto.toEntity(user, question);
        Answer saveAnswer = answerRepository.save(answer);
        return AnswerDto.Response.of(saveAnswer);
    }

    @Override
    public AnswerDto.Response getAnswerQuestionId(Long questionId) {
        Answer answer = answerRepository.findByQuestionId(questionId).orElseThrow(() -> new RestException(HttpStatus.NOT_FOUND));
        return AnswerDto.Response.of(answer);
    }

    @Override
    public AnswerDto.Response updateAnswer(Long questionId, AnswerDto.Request answerDto, String token) {
        Answer answer = answerRepository.findByQuestionId(questionId).orElseThrow(() -> new RestException(HttpStatus.NOT_FOUND));
        if (!jwtTokenProvider.validateToken(token))
            throw new RestException(HttpStatus.UNAUTHORIZED);
        if (!jwtTokenProvider.getIsSuperuser(token))
            throw new RestException(HttpStatus.FORBIDDEN);
        answer.update(answerDto.getContent());
        Answer changedAnswer = answerRepository.save(answer);
        return AnswerDto.Response.of(changedAnswer);
    }

    @Override
    public void deleteAnswer(Long questionId, String token) {
        Answer answer = answerRepository.findByQuestionId(questionId).orElseThrow(() -> new RestException(HttpStatus.NO_CONTENT));
        if (!jwtTokenProvider.validateToken(token))
            throw new RestException(HttpStatus.UNAUTHORIZED);
        if (!jwtTokenProvider.getIsSuperuser(token))
            throw new RestException(HttpStatus.FORBIDDEN);
        answerRepository.delete(answer);
    }
}