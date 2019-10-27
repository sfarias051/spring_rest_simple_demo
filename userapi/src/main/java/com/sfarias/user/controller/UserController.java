package com.sfarias.user.controller;

import com.sfarias.user.dao.PhoneRepository;
import com.sfarias.user.dao.UserRepository;
import com.sfarias.user.model.Phone;
import com.sfarias.user.model.User;
import com.sfarias.user.utils.Constants;
import com.sfarias.user.utils.ex.GeneralException;
import com.sfarias.user.utils.ex.PhoneNotFoundException;
import com.sfarias.user.utils.ex.UserNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.core.env.Environment;
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
@Log4j2
@RequestMapping("users")
public class UserController {

    @Autowired
    private Environment environment;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PhoneRepository phoneRepository;

    @GetMapping("/testload")
    public String testLoadBalancer(){
        String test = String.valueOf(Integer.parseInt(environment.getProperty("local.server.port")));
        log.info("Test LoadBalancer on port {} ", test);
        return test;
    }

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
        user.setPort(Integer.parseInt(environment.getProperty("local.server.port")));
        log.info("{}", savedUser);
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
        user.get().setPort(Integer.parseInt(environment.getProperty("local.server.port")));
        log.info("{}", user.get());
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
