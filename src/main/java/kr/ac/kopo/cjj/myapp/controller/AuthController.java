package kr.ac.kopo.cjj.myapp.controller;

import kr.ac.kopo.cjj.myapp.model.UserRegistrationRequest;
import kr.ac.kopo.cjj.myapp.service.FileStorageService;
import kr.ac.kopo.cjj.myapp.service.UserAccountService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class AuthController {

    private final UserAccountService userAccountService;
    private final FileStorageService fileStorageService;

    public AuthController(UserAccountService userAccountService, FileStorageService fileStorageService) {
        this.userAccountService = userAccountService;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("registrationRequest", new UserRegistrationRequest());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute UserRegistrationRequest request,
                           @RequestParam(value = "profileFile", required = false) MultipartFile profileFile,
                           Model model) {
        try {
            String imageUrl = request.getProfilePictureUrl();
            if (profileFile != null && !profileFile.isEmpty()) {
                imageUrl = fileStorageService.store(profileFile);
            }
            userAccountService.register(
                    request.getUsername(),
                    request.getPassword(),
                    imageUrl
            );
            return "redirect:/login?registered";
        } catch (IllegalArgumentException e) {
            model.addAttribute("registrationRequest", request);
            model.addAttribute("errorMessage", e.getMessage());
            return "register";
        }
    }
}
