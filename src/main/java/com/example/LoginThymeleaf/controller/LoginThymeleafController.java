package com.example.LoginThymeleaf.controller;

import com.example.LoginThymeleaf.model.RegisterForm;
import com.example.LoginThymeleaf.service.UserService;
import jakarta.validation.Valid;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.nio.charset.StandardCharsets;
import java.security.Principal;

@Controller
public class LoginThymeleafController {
    private final UserService users;

    public LoginThymeleafController(UserService users) { this.users = users; }

    @GetMapping("/login")
    public String login() { return "login"; }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("registerForm", new RegisterForm());
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute RegisterForm form, BindingResult errors) {
        if (form.getSenha() != null && !form.getSenha().equals(form.getConfirmacaoSenha())) {
            errors.rejectValue("confirmacaoSenha", "mismatch", "As senhas devem ser iguais.");
        }
        if (form.getSenha() != null && form.getSenha().getBytes(StandardCharsets.UTF_8).length > 72) {
            errors.rejectValue("senha", "length", "A senha deve ocupar no máximo 72 bytes (acentos e emojis ocupam mais de um byte).");
        }
        if (errors.hasErrors()) { return "register"; }
        try {
            users.register(form);
        } catch (DuplicateKeyException e) {
            errors.rejectValue("email", "duplicate", "Este email já está cadastrado.");
            return "register";
        }
        return "redirect:/login?registered";
    }

    @GetMapping("/recoverpassword")
    public String recoverpassword() { return "recoverpassword"; }

    @PostMapping("/recoverpassword")
    public String recoverpassword(@RequestParam(defaultValue = "") String email, Model model) {
        if (!email.trim().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$") || email.length() > 254) {
            model.addAttribute("erro", "Informe um email válido.");
        } else {
            model.addAttribute("mensagem", "O envio de email para recuperação de senha não está disponível nesta versão.");
        }
        return "recoverpassword";
    }

    @GetMapping({"/", "/home"})
    public String home(Principal principal, Model model) {
        model.addAttribute("nome", users.findName(principal.getName()));
        model.addAttribute("email", principal.getName());
        return "home";
    }
}
