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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private Logger logger = LoggerFactory.getLogger(UserServices.class.getName());

    public UserServices(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder, RoleRepository roleRepository){

        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    @Transactional
    public User insertUser (UserRegisterRequest request){
        logger.info("Starting a register for a new user.");
       isEmailAndUsernameValidForInsert(request.email(), request.username());
        User newUser = userMapper.toEntity(request);
        newUser.setPasswordHash(passwordEncoder.encode(request.password()));
        addDefaultRole(newUser);
        newUser.setActive(true);
        try {
            logger.info("User with email: " +request.email()+", Username: " + request.username() + " created!");
            return userRepository.save(newUser);
        }catch ( IllegalArgumentException e){
            logger.error("Error: "+ e.getMessage());
            throw e;
        }catch (DataIntegrityViolationException ex){
            logger.error("Error: "+ ex.getMessage());
            throw new EmailAlreadyExistsException(newUser.getEmail());
        }

    }

    public User getUserById(UUID id){
        return findUserOrThrow(id);
    }

    @Transactional
    public User updateUser(UserUpdateRequest request, UUID id){
        User updatedUser = findUserOrThrow(id);
        isUsernameValidForUpdate( request.username(), id);
        try {
            updatedUser.setActive(request.active());
            userMapper.updateToEntity(request,updatedUser);
            logger.info("User with id: "+ id +"updated!");
            return userRepository.save(updatedUser);

        } catch (IllegalArgumentException | DataIntegrityViolationException e) {
            logger.error("Error: "+ e.getMessage());
            throw e;
        }


    }

    @Transactional
    public void deleteUser( UUID id){
        User user = findUserOrThrow(id);
        try{
            logger.info("User with id" + id + "deleted");
            userRepository.delete(user);
        }catch (IllegalArgumentException | DataIntegrityViolationException e){
            logger.error("Error: "+ e.getMessage());
            throw e;
        }
    }

    private Role findDefaultRole() {
        return roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Default role not found"));
    }

    private void addDefaultRole(User user) {
        user.getRoles().add(findDefaultRole());
    }

    private void isEmailAndUsernameValidForInsert(String email, String username){
        if (userRepository.existsByEmail(email)){
            logger.error("User with email" + email + "already exists");
            throw new EmailAlreadyExistsException(email);
        } else if (userRepository.existsByUsername(username)) {
            logger.error("User with username" + username + "already exists");
            throw new UsernameAlreadyExistsException(username);
        }
    }

    private void isUsernameValidForUpdate( String username, UUID currentId) {
        if (userRepository.existsByUsernameAndIdNot(username, currentId)) {
            logger.error("User with username" + username + "already exists");
            throw new UsernameAlreadyExistsException(username);
        }

        if (userRepository.existsByUsernameAndIdNot(username, currentId)) {
            logger.error("User with username" + username + "already exists");
            throw new UsernameAlreadyExistsException(username);
        }
    }


    private User findUserOrThrow(UUID id) {
        logger.info("searching for user with id:" + id);
        return userRepository.findById(id)
                .orElseThrow(() -> {
                    return new ResourceNotFoundException(id);
                });
    }
}
