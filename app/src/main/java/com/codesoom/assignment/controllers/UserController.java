package com.codesoom.assignment.controllers;

import com.codesoom.assignment.application.UserService;
import com.codesoom.assignment.domain.User;
import com.codesoom.assignment.dto.UserModificationData;
import com.codesoom.assignment.dto.UserRegistrationData;
import com.codesoom.assignment.dto.UserResultData;
import com.codesoom.assignment.security.UserAuthentication;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/users")
@CrossOrigin
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    UserResultData create(@RequestBody @Valid UserRegistrationData registrationData) {
        User user = userService.registerUser(registrationData);
        return getUserResultData(user);
    }

    @PatchMapping("{id}")
    @PreAuthorize("isAuthenticated()")
    UserResultData update(@PathVariable Long id,
                          @RequestBody @Valid UserModificationData modificationData,
                          UserAuthentication authentication) {
        if (!authentication.isSameUser(id)) {
            throw new AccessDeniedException("다른 회원의 정보를 변경할 수 없습니다.");
        }

        User user = userService.updateUser(id, modificationData);
        return getUserResultData(user);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    private UserResultData getUserResultData(User user) {
        return UserResultData.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }
}
