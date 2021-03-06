package com.disney.auth.controller;

import javax.validation.Valid;

import com.disney.auth.dto.AuthenticationRequest;
import com.disney.auth.dto.AuthenticationResponse;
import com.disney.auth.dto.UserDTO;
import com.disney.auth.service.JwtUtils;
import com.disney.auth.service.UserDetailsCustomService;
import com.disney.exception.ErrorsEnum;
import com.disney.exception.UserNotFound;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class UserAuthController {
    
    
    private AuthenticationManager authenticationManager;
    private UserDetailsCustomService userDetailsCustomService;
    private JwtUtils jwtTokenUtil;

    @Autowired
    public UserAuthController(
        AuthenticationManager authenticationManager,
        UserDetailsCustomService userDetailsCustomService, 
        JwtUtils jwtTokenUtil) {
            this.authenticationManager = authenticationManager;
            this.userDetailsCustomService = userDetailsCustomService;
            this.jwtTokenUtil = jwtTokenUtil;
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthenticationResponse> signUp (@Valid @RequestBody UserDTO dto) throws Exception {
     userDetailsCustomService.save(dto);
     return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    
   @PostMapping ("/signin")
    public ResponseEntity<AuthenticationResponse> signIn (@Valid @RequestBody AuthenticationRequest authRequest) throws Exception {
        UserDetails userDetails;
        try {
            Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
            userDetails = (UserDetails) auth.getPrincipal();

        } catch (BadCredentialsException e) {
            throw new UserNotFound(ErrorsEnum.USERNOTFOUND.getMessage());
        }
        final String jwt = jwtTokenUtil.generateToken(userDetails);
        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }

}
