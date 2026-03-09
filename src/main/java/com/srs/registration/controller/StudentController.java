package com.srs.registration.controller;

import com.srs.registration.domain.Student;
import com.srs.registration.domain.StudentForm;
import com.srs.registration.service.DuplicateRegistrationException;
import com.srs.registration.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/")
    public String home(Authentication authentication) {
        if (authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/students";
        }
        return "home";
    }

    @GetMapping("/students")
    public String list(@RequestParam(value = "q", required = false) String keyword,
                       Model model,
                       Authentication authentication) {
        boolean isAdmin = hasRole(authentication, "ROLE_ADMIN");
        model.addAttribute("students", studentService.listStudents(keyword, authentication.getName(), isAdmin));
        model.addAttribute("q", keyword == null ? "" : keyword);
        model.addAttribute("canManageStudents", isAdmin);
        model.addAttribute("currentUserEmail", authentication.getName());
        model.addAttribute("currentUserRole", isAdmin ? "ADMIN" : "USER");
        return "students/list";
    }

    @GetMapping("/students/new")
    public String newForm(Model model) {
        model.addAttribute("studentForm", new StudentForm());
        model.addAttribute("editMode", false);
        return "students/form";
    }

    @PostMapping("/students")
    public String create(@Valid @ModelAttribute("studentForm") StudentForm form,
                         BindingResult bindingResult,
                         Model model,
                         Authentication authentication,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("editMode", false);
            return "students/form";
        }

        try {
            studentService.create(toEntity(form), authentication.getName());
        } catch (DuplicateRegistrationException ex) {
            bindingResult.rejectValue("registrationNumber", "student.registration.duplicate");
            model.addAttribute("editMode", false);
            return "students/form";
        }

        redirectAttributes.addFlashAttribute("successMessage", "student.created");
        return "redirect:/students";
    }

    @GetMapping("/students/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Student student = studentService.getStudent(id);
        model.addAttribute("studentForm", toForm(student));
        model.addAttribute("studentId", student.getId());
        model.addAttribute("editMode", true);
        return "students/form";
    }

    @PostMapping("/students/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("studentForm") StudentForm form,
                         BindingResult bindingResult,
                         Model model,
                         Authentication authentication,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("studentId", id);
            model.addAttribute("editMode", true);
            return "students/form";
        }

        try {
            studentService.update(id, toEntity(form), authentication.getName());
        } catch (DuplicateRegistrationException ex) {
            bindingResult.rejectValue("registrationNumber", "student.registration.duplicate");
            model.addAttribute("studentId", id);
            model.addAttribute("editMode", true);
            return "students/form";
        }

        redirectAttributes.addFlashAttribute("successMessage", "student.updated");
        return "redirect:/students";
    }

    @PostMapping("/students/{id}/delete")
    public String delete(@PathVariable Long id, Authentication authentication, RedirectAttributes redirectAttributes) {
        studentService.delete(id, authentication.getName());
        redirectAttributes.addFlashAttribute("successMessage", "student.deleted");
        return "redirect:/students";
    }

    private boolean hasRole(Authentication authentication, String role) {
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if (role.equals(authority.getAuthority())) {
                return true;
            }
        }
        return false;
    }

    private Student toEntity(StudentForm form) {
        Student student = new Student();
        student.setRegistrationNumber(form.getRegistrationNumber());
        student.setFirstName(form.getFirstName());
        student.setLastName(form.getLastName());
        student.setEmail(form.getEmail());
        student.setPhone(form.getPhone());
        student.setDateOfBirth(form.getDateOfBirth());
        student.setDepartment(form.getDepartment());
        return student;
    }

    private StudentForm toForm(Student student) {
        StudentForm form = new StudentForm();
        form.setRegistrationNumber(student.getRegistrationNumber());
        form.setFirstName(student.getFirstName());
        form.setLastName(student.getLastName());
        form.setEmail(student.getEmail());
        form.setPhone(student.getPhone());
        form.setDateOfBirth(student.getDateOfBirth());
        form.setDepartment(student.getDepartment());
        return form;
    }
}

