package com.project.cinematch;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import com.project.cinematch.Controller.UserController;
import com.project.cinematch.Model.User;
import com.project.cinematch.Service.UserService;

public class UserControllerTest {
    @Test
    void loginUser_wrongPassword() {
        var service = Mockito.mock(UserService.class);
        var controller = new UserController();
        controller.setUserService(service);

        var req = new User();
        req.setUsername("me");
        req.setPasswordHash("pass");

        var tester = new User();
        tester.setUsername("me");
        tester.setPasswordHash("otherpass");

        Mockito.when(service.findByUsername("me")).thenReturn(Optional.of(tester));

        var resp = controller.login(req);

        assertEquals(401, resp.getStatusCode().value());
        assertEquals("Wrong password", resp.getBody());
    }
}
