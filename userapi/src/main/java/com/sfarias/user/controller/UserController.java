package com.sfarias.user.controller;

import com.sfarias.user.dao.PhoneRepository;
import com.sfarias.user.dao.UserRepository;
import com.sfarias.user.model.Phone;
import com.sfarias.user.model.User;
import com.sfarias.user.utils.Constants;
import com.sfarias.user.utils.ex.GeneralException;
import com.sfarias.user.utils.ex.PhoneNotFoundException;
import com.sfarias.user.utils.ex.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("users")
public class UserController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PhoneRepository phoneRepository;

    @GetMapping("")
    public List<?> getAllUsers(){
        return userRepository.findAll();
    }

    @PostMapping("")
    public ResponseEntity<Object> createUser(@Valid @RequestBody User user){
        if (userRepository.existsUserByEmail(user.getEmail()))
            throw new GeneralException(Constants.ERROR_EMAIL_EXIST);
        user.setCreated(new Date());
        user.setLastLogin(new Date());
        user.setIsActive(false);
        user.setToken(UUID.randomUUID());
        User savedUser = userRepository.save(user);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}").buildAndExpand(
                        savedUser.getId()).toUri();
        return ResponseEntity.created(location).body(savedUser);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateUser(@PathVariable UUID id, @RequestBody User user){
        Optional<User> userUpdate = userRepository.findById(id);
        if (!userUpdate.isPresent())
            throw new UserNotFoundException("id-" + id);
        user.setUpdated(new Date());
        user.setId(id);
        userRepository.save(user);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable UUID id){
        Optional<User> user = userRepository.findById(id);
        if (!user.isPresent())
            throw new UserNotFoundException("id-" + id);
        return user.get();
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable UUID id){
        userRepository.deleteById(id);
    }

    @GetMapping("/{id}/phones")
    public List<Phone> getPhones(@PathVariable UUID id){
        Optional<User> user = userRepository.findById(id);
        if (!user.isPresent())
            throw new PhoneNotFoundException("id-" + id);
        return user.get().getPhones();
    }

    @DeleteMapping("/{id}/phone")
    public void deletePhone(@PathVariable UUID id){
        phoneRepository.deleteById(id);
    }

    @PostMapping("/{id}/phone")
    public ResponseEntity<Object> createPhone(@PathVariable UUID id, @RequestBody Phone phone){
        Optional<User> userOptional = userRepository.findById(id);
        if (!userOptional.isPresent())
            throw new UserNotFoundException("id-" + id);
        User user = userOptional.get();
        phone.setUser(user);

        phoneRepository.save(phone);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}").buildAndExpand(
                        phone.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

}
