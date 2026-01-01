package com.project.cinematch;

import com.project.cinematch.Controller.UserController;
import com.project.cinematch.Model.User;
import com.project.cinematch.Service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class logintest {


    @Test
    void testLoginUserNotFound() {
        
        UserService mockService = mock(UserService.class);
        when(mockService.findByUsername("unknown")).thenReturn(Optional.empty());

        UserController controller = new UserController();
        controller.setUserService(mockService);

        User login = new User();
        login.setUsername("unknown");
        login.setPasswordHash("pass");

        ResponseEntity<?> response = controller.login(login);

        assertEquals(401, response.getStatusCode().value());
        assertEquals("User not found", response.getBody());
    }

    @Test
    void testLoginWrongPassword() {
        User user = new User();
        user.setUsername("john");
        user.setPasswordHash("correctPass");

        UserService mockService = mock(UserService.class);
        when(mockService.findByUsername("john")).thenReturn(Optional.of(user));

        UserController controller = new UserController();
        controller.setUserService(mockService);

        User login = new User();
        login.setUsername("john");
        login.setPasswordHash("wrongPass");

        ResponseEntity<?> response = controller.login(login);

        assertEquals(401, response.getStatusCode().value());
        assertEquals("Wrong password", response.getBody());
    }

    @Test
    void testLoginSuccess() {
        User user = new User();
        user.setUsername("john");
        user.setPasswordHash("correctPass");

        UserService mockService = mock(UserService.class);
        when(mockService.findByUsername("john")).thenReturn(Optional.of(user));

        UserController controller = new UserController();
        controller.setUserService(mockService);

        User login = new User();
        login.setUsername("john");
        login.setPasswordHash("correctPass");

        ResponseEntity<?> response = controller.login(login);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(user, response.getBody());
    }

    @Test
    void testRegisterAddsNoopPrefix() {
        User user = new User();
        user.setUsername("john");
        user.setPasswordHash("mypassword");

        UserService mockService = mock(UserService.class);
        when(mockService.register(any(User.class))).thenAnswer(i -> i.getArgument(0));

        UserController controller = new UserController();
        controller.setUserService(mockService);

        User result = controller.register(user);

        assertEquals("{noop}mypassword", result.getPasswordHash());
    }
}