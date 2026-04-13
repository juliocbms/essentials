package com.mysaas.essentials.services;



import com.mysaas.essentials.model.dto.UsersDTOS.UserRegisterRequest;
import com.mysaas.essentials.model.dto.UsersDTOS.UserUpdateRequest;
import com.mysaas.essentials.model.entities.Role;
import com.mysaas.essentials.model.entities.User;
import com.mysaas.essentials.model.mappers.UserMapper;
import com.mysaas.essentials.repository.RoleRepository;
import com.mysaas.essentials.repository.UserRepository;
import com.mysaas.essentials.services.exceptions.EmailAlreadyExistsException;
import com.mysaas.essentials.services.exceptions.ResourceNotFoundException;
import com.mysaas.essentials.services.exceptions.UsernameAlreadyExistsException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UserServices {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public UserServices(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder, RoleRepository roleRepository){

        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    @Transactional
    public User insertUser (UserRegisterRequest request){
        if (userRepository.existsByEmail(request.email())){
            throw new EmailAlreadyExistsException(request.email());
        }
        User newUser = userMapper.toEntity(request);
        Role defaultRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Default role not found"));

        newUser.getRoles().add(defaultRole);
        newUser.setPasswordHash(passwordEncoder.encode(newUser.getPassword()));
        newUser.setActive(true);
        try {
            return userRepository.save(newUser);
        }catch ( IllegalArgumentException e){
            throw e;
        }catch (DataIntegrityViolationException ex){
            throw new EmailAlreadyExistsException(newUser.getEmail());
        }

    }

    public User getUserById(UUID id){
        return findUserOrThrow(id);
    }

    @Transactional
    public User updateUser(UserUpdateRequest request, UUID id){

        User updatedUser = findUserOrThrow(id);
        isEmailAndUsernameValid(request.email(),request.username());
        try {
            updatedUser.setActive(request.active());
            userMapper.updateToEntity(request,updatedUser);
            return userRepository.save(updatedUser);

        } catch (IllegalArgumentException e) {
            throw e;
        } catch (DataIntegrityViolationException ex) {
            throw ex;
        }


    }

    @Transactional
    public void deleteUser( UUID id){
        User user = findUserOrThrow(id);
        try{
            userRepository.delete(user);
        }catch ( IllegalArgumentException e){
            throw e;
        } catch (DataIntegrityViolationException ex){
            throw ex;
        }

    }

    private void isEmailAndUsernameValid(String email, String usename){
        if (userRepository.existsByEmail(email)){
            throw new EmailAlreadyExistsException(email);
        } else if (userRepository.existsByUsername(usename)) {
            throw new UsernameAlreadyExistsException(usename);
        }
    }


    private User findUserOrThrow(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> {
                    return new ResourceNotFoundException(id);
                });
    }
}
