package org.example.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.antlr.v4.runtime.Token;
import org.example.config.auth.TokenProvider;
import org.example.dto.JwtDto;
import org.example.dto.SignInDto;
import org.example.dto.SignUpDto;
import org.example.entities.User;
import org.example.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Endpoints for user authentication")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private AuthService service;
    @Autowired
    private TokenProvider tokenService;

    @PostMapping("/signup")
    @Operation(summary = "Sign up a new user",description = """
                             Register a new user and return status 201 on successful registration. 
                             Password must meet the following criteria:
                             - At least 8 characters long
                             - At least one digit
                             - At least one lowercase letter
                             - At least one uppercase letter
                             - At least one special character (@#$%^&+=)
                             - Must not contain any spaces
                             """,
            responses = {
                    @ApiResponse(responseCode = "201", description = "User registered successfully",
                            content = @Content(schema = @Schema(implementation = ResponseEntity.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid user data",
                            content = @Content)
            })
    public ResponseEntity<?> signUp(@RequestBody @Valid SignUpDto data) {
        try {
            service.signUp(data);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    @PostMapping("/signin")
    @Operation(summary = "Sign in a new user", description = "Authenticate a new user and return a JWT token.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User authenticated successfully",
                            content = @Content(schema = @Schema(implementation = JwtDto.class))),
                    @ApiResponse(responseCode = "401", description = "Invalid credentials",
                            content = @Content)
            })
    public ResponseEntity<JwtDto> signIn(@RequestBody @Valid SignInDto data) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.login(), data.password());
        var authUser = authenticationManager.authenticate(usernamePassword);
        var accessToken = tokenService.generateAccessToken((User) authUser.getPrincipal());
        return ResponseEntity.ok(new JwtDto(accessToken));
    }
}
