package kr.ac.kopo.cjj.myapp.controller;

import kr.ac.kopo.cjj.myapp.service.TechStackService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin/tech")
public class TechStackAdminController {

    private final TechStackService techStackService;

    public TechStackAdminController(TechStackService techStackService) {
        this.techStackService = techStackService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("techList", techStackService.findAll());
        return "tech-admin";
    }

    @PostMapping("/add")
    public String add(@RequestParam("name") String name) {
        techStackService.add(name);
        return "redirect:/admin/tech";
    }

    @PostMapping("/{id}/edit")
    public String edit(@PathVariable Long id, @RequestParam("name") String name) {
        techStackService.update(id, name);
        return "redirect:/admin/tech";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        techStackService.delete(id);
        return "redirect:/admin/tech";
    }
}
