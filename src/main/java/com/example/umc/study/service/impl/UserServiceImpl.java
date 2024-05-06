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
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;


    @Override
    public User createUser(UserRequestDto.JoinDto joinDTO) {
        User user = UserConverter.toUser(joinDTO);
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    @Override
    public User readUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(()-> new UserHandler(ErrorStatus._NOT_FOUND_USER));
    }

    @Transactional(readOnly = true)
    @Override
    public List<User> readUsers() {
        return userRepository.findAll();
    }

    @Override
    public User updateUser(UserRequestDto.UpdateUserDTO updateUserDTO, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserHandler(ErrorStatus._NOT_FOUND_USER));
        user.update(updateUserDTO.getName());
        return user;
    }

    @Override
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(()-> new UserHandler(ErrorStatus._NOT_FOUND_USER));
        userRepository.delete(user);
    }
}