package com.srs.registration.controller;

import com.srs.registration.domain.StudentAudit;
import com.srs.registration.repository.StudentAuditRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final StudentAuditRepository studentAuditRepository;

    public AdminController(StudentAuditRepository studentAuditRepository) {
        this.studentAuditRepository = studentAuditRepository;
    }

    @GetMapping("/audits")
    public String audits(Model model) {
        List<StudentAudit> audits = studentAuditRepository.findLatest(100);
        model.addAttribute("audits", audits);
        return "admin/audits";
    }
}
