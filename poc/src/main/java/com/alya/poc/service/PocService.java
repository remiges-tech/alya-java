package com.alya.poc.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alya.poc.model.AlyaEntity;
import com.alya.poc.repository.UserRepository;
import com.remiges.alya.validation.AlyaValidation;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PocService {

    @Autowired
    private AlyaValidation alyaValidation;

    @Autowired
    private UserRepository userRepository;

    public List<AlyaEntity> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<AlyaEntity> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public AlyaEntity createUser(AlyaEntity user) {
        Map<String, String> validationErrors = alyaValidation.alyaValidator(user);
        if (!validationErrors.isEmpty()) {
            // Handle validation errors
            // For demonstration, let's just print the errors
            validationErrors.forEach((field, errorMessage) -> {
                System.out.println("Validation error for field " + field + ": " + errorMessage);
            });
        } else {
            // Continue with saving if validation passes
            userRepository.save(user);
        }
        return user;
    }

    public Optional<AlyaEntity> updateUser(Long id, AlyaEntity alyaEntity) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setName(alyaEntity.getName());
                    user.setEmail(alyaEntity.getEmail());
                    user.setPan(alyaEntity.getPan());
                    user.setAadhar(alyaEntity.getAadhar());
                    return userRepository.save(user);
                });
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}

