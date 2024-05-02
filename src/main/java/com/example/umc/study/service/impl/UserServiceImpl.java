package com.example.umc.study.service.impl;

import com.example.umc.study.apiPayload.code.status.ErrorStatus;

import com.example.umc.study.apiPayload.exception.handler.UserHandler;
import com.example.umc.study.converter.UserConverter;
import com.example.umc.study.domain.User;
import com.example.umc.study.dto.UserRequestDto;
import com.example.umc.study.repository.UserRepository;
import com.example.umc.study.service.UserService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;


    @Override
    @Transactional
    public User createUser(UserRequestDto.JoinDto joinDTO) {
        User user = UserConverter.toUser(joinDTO);
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    @Override
    public User readUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(()-> new UserHandler(ErrorStatus._NOT_FOUND_USER));
        return user;
    }

    @Transactional(readOnly = true)
    @Override
    public List<User> readUsers() {
        return userRepository.findAll();
    }

    @Transactional
    @Override
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(()-> new UserHandler(ErrorStatus._NOT_FOUND_USER));
        userRepository.delete(user);
    }
}