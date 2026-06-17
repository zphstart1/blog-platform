package com.blog.user.interfaces;

import com.blog.shared.Result;
import com.blog.user.application.AuthApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthApplicationService authApplicationService;

    @PostMapping("/register")
    public Result<Object> register(@Valid @RequestBody Map<String, String> req) {
        return authApplicationService.register(
                req.get("username"), req.get("password"),
                req.get("email"), req.get("nickname"));
    }

    @PostMapping("/login")
    public Result<Object> login(@Valid @RequestBody Map<String, String> req) {
        return authApplicationService.login(req.get("username"), req.get("password"));
    }

    @GetMapping("/me")
    public Result<Object> me() {
        return authApplicationService.getCurrentUser();
    }
}
