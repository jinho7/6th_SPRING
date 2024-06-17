package com.example.umc.study.config.filter;

import com.example.umc.study.apiPayLoad.BaseResponse;
import com.example.umc.study.apiPayLoad.code.status.ErrorStatus;
import com.example.umc.study.apiPayLoad.exception.handler.AuthHandler;
import com.example.umc.study.config.PrincipalDetails;
import com.example.umc.study.dto.LoginRequestDto;
import com.example.umc.study.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        LoginRequestDto loginRequestDto = readBody(request);

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                UsernamePasswordAuthenticationToken.unauthenticated(loginRequestDto.getEmail(), loginRequestDto.getPassword());

        return authenticationManager.authenticate(usernamePasswordAuthenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {

        PrincipalDetails principalDetails = (PrincipalDetails) authResult.getPrincipal();

        String email = principalDetails.getUsername();

        Collection<? extends GrantedAuthority> authorities = authResult.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();

        String role = auth.getAuthority();

        String token = jwtUtil.createAccessToken(email, role);

        response.addHeader("Authorization", "Bearer " + token);
        response.setContentType("application/json; charset=UTF-8");
        response.setStatus(HttpStatus.OK.value());

        BaseResponse<Object> errorResponse =
                BaseResponse.onSuccess(null);

        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), errorResponse);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        response.setContentType("application/json; charset=UTF-8");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        ErrorStatus UN_AUTH = ErrorStatus._AUTHENTICATION_FAILED;

        BaseResponse<Object> errorResponse =
                BaseResponse.onFailure(UN_AUTH.getCode(), UN_AUTH.getMessage(), null);

        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), errorResponse);
    }

    private LoginRequestDto readBody(HttpServletRequest request) {
        LoginRequestDto loginRequestDto = null;
        ObjectMapper om = new ObjectMapper();


        try {
            loginRequestDto = om.readValue(request.getInputStream(), LoginRequestDto.class);
        } catch (IOException e) {
            throw new AuthHandler(ErrorStatus._BAD_REQUEST);
        }

        return loginRequestDto;
    }
}