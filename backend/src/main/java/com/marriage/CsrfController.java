package com.marriage;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;
@RestController public class CsrfController {@GetMapping("/api/csrf") public CsrfToken token(CsrfToken token){return token;}}
