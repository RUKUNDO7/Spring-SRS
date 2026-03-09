package com.srs.registration.controller;

import com.srs.registration.domain.Role;
import com.srs.registration.domain.SignupForm;
import com.srs.registration.service.AppUserService;
import com.srs.registration.service.DuplicateEmailException;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Objects;

@Controller
public class AuthController {

    private final AppUserService appUserService;

    public AuthController(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("signupForm", new SignupForm());
        model.addAttribute("roles", Role.values());
        return "auth/signup";
    }

    @PostMapping("/signup")
    public String signup(@Valid @ModelAttribute("signupForm") SignupForm form,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (!Objects.equals(form.getPassword(), form.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "auth.password.mismatch");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("roles", Role.values());
            return "auth/signup";
        }

        try {
            appUserService.register(form);
        } catch (DuplicateEmailException ex) {
            bindingResult.rejectValue("email", "auth.email.duplicate");
            model.addAttribute("roles", Role.values());
            return "auth/signup";
        }

        redirectAttributes.addFlashAttribute("successMessage", "auth.signup.success");
        return "redirect:/login";
    }
}
