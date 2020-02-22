package com.example.mongodb.service;

import com.example.mongodb.dao.Userdao;
import com.example.mongodb.objects.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Service
public class CustomUserDetailsService implements UserDetailsService{
    @Autowired
    private Userdao userdao;

    public User findUserByHandle(String handle){
        return userdao.findByHandle(handle);
    }
    public User findUserByEmail(String email){
        return userdao.findByEmail(email);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }

    


    
    
}