package com.project.cinematch;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.project.cinematch.Model.User;
import com.project.cinematch.Repository.UserRepository;
import com.project.cinematch.Service.CustomUserDetailsServices;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class CustomUserDetailsServicesTest {

    @Mock
    private UserRepository userRepository; // "Ξ¨Ξ΅ΟΟΞΉΞΊΞΏ" repository Ξ³ΞΉΞ± Ξ±ΟΞΏΞΌΟΞ½ΟΟΞ· (Mocking)

    @InjectMocks
    private CustomUserDetailsServices customUserDetailsServices; // Ξ€ΞΏ Service ΟΞΏΟ Ξ΅Ξ»Ξ­Ξ³ΟΞΏΟΞΌΞ΅

    @Test
    public void testLoadUserByUsername_Success() {
        User mockUser = new User();
        mockUser.setUsername("Nikos");
        mockUser.setPasswordHash("encoded_password");

        when(userRepository.findByUsername("Nikos")).thenReturn(Optional.of(mockUser));

        UserDetails userDetails = customUserDetailsServices.loadUserByUsername("Nikos");

        assertNotNull(userDetails);
        assertEquals("Nikos", userDetails.getUsername());
        assertEquals("encoded_password", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("USER")));
    }

    @Test
    public void testLoadUserByUsername_UserNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            customUserDetailsServices.loadUserByUsername("unknown");
        });
    }
}
