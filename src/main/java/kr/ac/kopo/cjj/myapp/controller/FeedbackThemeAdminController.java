package kr.ac.kopo.cjj.myapp.controller;

import kr.ac.kopo.cjj.myapp.service.FeedbackThemeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin/themes")
public class FeedbackThemeAdminController {

    private final FeedbackThemeService feedbackThemeService;

    public FeedbackThemeAdminController(FeedbackThemeService feedbackThemeService) {
        this.feedbackThemeService = feedbackThemeService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("themeList", feedbackThemeService.findAll());
        return "theme-admin";
    }

    @PostMapping("/add")
    public String add(@RequestParam("name") String name) {
        feedbackThemeService.add(name);
        return "redirect:/admin/themes";
    }

    @PostMapping("/{id}/edit")
    public String edit(@PathVariable Long id, @RequestParam("name") String name) {
        feedbackThemeService.update(id, name);
        return "redirect:/admin/themes";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        feedbackThemeService.delete(id);
        return "redirect:/admin/themes";
    }
}
