package com.project.cinematch.Controller;

import com.project.cinematch.Model.User;
import com.project.cinematch.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;


    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginRequest) {
        var user = userService.findByUsername(loginRequest.getUsername());

        if (user.isEmpty())
            return ResponseEntity.status(401).body("User not found");

        if (!user.get().getPasswordHash().equals(loginRequest.getPasswordHash()))
            return ResponseEntity.status(401).body("Wrong password");

        return ResponseEntity.ok(user.get());
    }

    @PostMapping("/register")
    public User register(@RequestBody User user) {
        user.setPasswordHash("{noop}" + user.getPasswordHash());
        return userService.register(user);

    }
}
