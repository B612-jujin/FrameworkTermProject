package kr.ac.kopo.cjj.myapp.controller;

import kr.ac.kopo.cjj.myapp.domain.Portfolio;
import kr.ac.kopo.cjj.myapp.domain.UserAccount;
import kr.ac.kopo.cjj.myapp.model.FeedbackView;
import kr.ac.kopo.cjj.myapp.service.FeedbackService;
import kr.ac.kopo.cjj.myapp.service.FileStorageService;
import kr.ac.kopo.cjj.myapp.service.PortfolioService;
import kr.ac.kopo.cjj.myapp.service.TechStackService;
import kr.ac.kopo.cjj.myapp.service.UserAccountService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.time.Duration;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/portfolios")
public class PortfolioController {

    private final PortfolioService portfolioService;
    private final UserAccountService userAccountService;
    private final FeedbackService feedbackService;
    private final FileStorageService fileStorageService;
    private final TechStackService techStackService;

    public PortfolioController(PortfolioService portfolioService, UserAccountService userAccountService,
                               FeedbackService feedbackService, FileStorageService fileStorageService,
                               TechStackService techStackService) {
        this.portfolioService = portfolioService;
        this.userAccountService = userAccountService;
        this.feedbackService = feedbackService;
        this.fileStorageService = fileStorageService;
        this.techStackService = techStackService;
    }

    @GetMapping
    public String listPortfolios(Model model, Authentication authentication,
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

    @GetMapping("/new")
    public String newPortfolioForm(Model model) {
        model.addAttribute("portfolio", new Portfolio());
        model.addAttribute("techList", techStackService.findAll());
        return "portfolio-form";
    }

    @PostMapping("/new")
    public String createPortfolio(@ModelAttribute Portfolio portfolio,
                                  @RequestParam(value = "techSelections", required = false) List<String> techSelections,
                                  @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                                  Authentication authentication) {
        UserAccount owner = authentication != null
                ? userAccountService.findByUsername(authentication.getName()).orElse(null)
                : null;
        if (techSelections != null && !techSelections.isEmpty()) {
            portfolio.setTechStack(String.join(", ", techSelections));
        }
        if (imageFile != null && !imageFile.isEmpty()) {
            portfolio.setImageUrl(fileStorageService.store(imageFile));
        }
        portfolioService.createPortfolio(portfolio, owner);
        return "redirect:/home";
    }

    @GetMapping("/{id}/edit")
    public String editPortfolio(@PathVariable Long id, Model model) {
        Portfolio portfolio = portfolioService.getById(id);
        model.addAttribute("portfolio", portfolio);
        model.addAttribute("techList", techStackService.findAll());
        return "portfolio-form";
    }

    @PostMapping("/{id}/edit")
    public String updatePortfolio(@PathVariable Long id,
                                  @ModelAttribute Portfolio portfolio,
                                  @RequestParam(value = "techSelections", required = false) List<String> techSelections,
                                  @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {
        if (techSelections != null && !techSelections.isEmpty()) {
            portfolio.setTechStack(String.join(", ", techSelections));
        }
        if (imageFile != null && !imageFile.isEmpty()) {
            portfolio.setImageUrl(fileStorageService.store(imageFile));
        }
        portfolioService.updatePortfolio(id, portfolio);
        return "redirect:/home";
    }

    @GetMapping("/{id}/delete")
    public String deletePortfolio(@PathVariable Long id) {
        portfolioService.deleteById(id);
        return "redirect:/home";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model, Authentication authentication) {
        Portfolio portfolio = portfolioService.getById(id);
        boolean isAdmin = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(auth -> "ROLE_ADMIN".equals(auth.getAuthority()));
        boolean visible = Boolean.TRUE.equals(portfolio.getVisible());
        if (!visible && !isAdmin) {
            return "redirect:/portfolios";
        }
        model.addAttribute("portfolio", portfolio);
        List<FeedbackView> feedbackViews = feedbackService.getByPortfolio(id).stream()
                .map(fb -> FeedbackView.builder()
                        .category(fb.getCategory())
                        .rating(fb.getRating())
                        .content(fb.getContent())
                        .authorName(fb.getAuthor() != null ? fb.getAuthor().getUsername() : "익명")
                        .createdAt(fb.getCreatedAt())
                        .relativeTime(humanize(fb.getCreatedAt()))
                        .build())
                .toList();
        model.addAttribute("feedbackList", feedbackViews);
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("techList", techStackService.findAll());
        return "portfolio-detail";
    }

    @PostMapping("/{id}/feedback")
    public String addFeedback(@PathVariable Long id,
                              @RequestParam("content") String content,
                              @RequestParam(value = "category", required = false) String category,
                              @RequestParam(value = "rating", required = false) Integer rating,
                              Authentication authentication) {
        if (authentication == null) {
            return "redirect:/login";
        }
        Portfolio portfolio = portfolioService.getById(id);
        UserAccount author = userAccountService.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("사용자 정보를 찾을 수 없습니다."));
        feedbackService.addFeedback(portfolio, author, content, category, rating);
        return "redirect:/portfolios/" + id;
    }

    @PostMapping("/{id}/toggle-visibility")
    public String toggleVisibility(@PathVariable Long id) {
        portfolioService.toggleVisibility(id);
        return "redirect:/portfolios/" + id;
    }

    private String humanize(LocalDateTime time) {
        if (time == null) return "";
        Duration d = Duration.between(time, LocalDateTime.now());
        long days = d.toDays();
        if (days >= 1) {
            return days + "일 전";
        }
        long hours = d.toHours();
        if (hours >= 1) {
            return hours + "시간 전";
        }
        long minutes = d.toMinutes();
        if (minutes >= 1) {
            return minutes + "분 전";
        }
        return "방금 전";
    }
}
