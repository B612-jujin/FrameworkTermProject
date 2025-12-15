package kr.ac.kopo.cjj.myapp.controller;

import java.util.List;
import kr.ac.kopo.cjj.myapp.domain.Portfolio;
import kr.ac.kopo.cjj.myapp.service.PortfolioService;
import kr.ac.kopo.cjj.myapp.service.TechStackService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    private final PortfolioService portfolioService;
    private final TechStackService techStackService;

    public LoginController(PortfolioService portfolioService, TechStackService techStackService) {
        this.portfolioService = portfolioService;
        this.techStackService = techStackService;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping({"/", "/home"})
    public String homePage(Model model, Authentication authentication,
                           @RequestParam(value = "tech", required = false) List<String> tech,
                           @RequestParam(value = "q", required = false) String keyword) {
        boolean isAdmin = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(auth -> "ROLE_ADMIN".equals(auth.getAuthority()));
        List<Portfolio> portfolios = portfolioService.filterPortfolios(isAdmin, tech, keyword);
        model.addAttribute("selectedTech", tech);
        model.addAttribute("keyword", keyword);
        model.addAttribute("techList", techStackService.findAll());
        model.addAttribute("portfolios", portfolios);
        return "home";
    }

    @GetMapping("/logout")
    public String logoutPage() {
        return "login";
    }
}
