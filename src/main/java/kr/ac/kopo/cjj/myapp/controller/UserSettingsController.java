package kr.ac.kopo.cjj.myapp.controller;

import kr.ac.kopo.cjj.myapp.domain.UserAccount;
import kr.ac.kopo.cjj.myapp.service.FileStorageService;
import kr.ac.kopo.cjj.myapp.service.UserAccountService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/settings")
public class UserSettingsController {

    private final UserAccountService userAccountService;
    private final FileStorageService fileStorageService;

    public UserSettingsController(UserAccountService userAccountService, FileStorageService fileStorageService) {
        this.userAccountService = userAccountService;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping
    public String settings(Model model, Authentication authentication) {
        if (authentication == null) return "redirect:/login";
        UserAccount user = userAccountService.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("사용자 정보를 찾을 수 없습니다."));
        model.addAttribute("user", user);
        return "user-settings";
    }

    @PostMapping("/update")
    public String update(@RequestParam(value = "newPassword", required = false) String newPassword,
                         @RequestParam(value = "profilePictureUrl", required = false) String profilePictureUrl,
                         @RequestParam(value = "description", required = false) String description,
                         @RequestParam(value = "profileFile", required = false) MultipartFile profileFile,
                         Authentication authentication) {
        if (authentication == null) return "redirect:/login";
        UserAccount user = userAccountService.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("사용자 정보를 찾을 수 없습니다."));
        String imageUrl = profilePictureUrl;
        if (profileFile != null && !profileFile.isEmpty()) {
            imageUrl = fileStorageService.store(profileFile);
        }
        userAccountService.updateProfile(user, newPassword, imageUrl, description);
        return "redirect:/settings";
    }

    @PostMapping("/delete")
    public String delete(Authentication authentication) {
        if (authentication == null) return "redirect:/login";
        UserAccount user = userAccountService.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("사용자 정보를 찾을 수 없습니다."));
        userAccountService.deleteAccount(user);
        return "redirect:/login?deleted";
    }
}
