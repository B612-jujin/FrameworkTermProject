package kr.ac.kopo.cjj.myapp.service;

import java.util.Optional;
import kr.ac.kopo.cjj.myapp.custom.CustomUserDetails;
import kr.ac.kopo.cjj.myapp.domain.UserAccount;
import kr.ac.kopo.cjj.myapp.repository.UserAccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserAccountService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(UserAccountService.class);

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;

    public UserAccountService(UserAccountRepository userAccountRepository, PasswordEncoder passwordEncoder) {
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserAccount register(String username, String rawPassword, String profilePictureUrl) {
        return register(username, rawPassword, profilePictureUrl, "ROLE_USER");
    }

    @Transactional
    public UserAccount register(String username, String rawPassword, String profilePictureUrl, String role) {
        if (userAccountRepository.existsByUsername(username)) {
            log.warn("회원가입 실패 - 이미 존재하는 username: {}", username);
            throw new IllegalArgumentException("이미 사용 중인 사용자 이름입니다.");
        }
        log.info("회원가입 시도 - username: {}, role: {}", username, role);
        UserAccount user = UserAccount.builder()
                .username(username)
                .password(passwordEncoder.encode(rawPassword))
                .role(role)
                .profilePictureUrl(profilePictureUrl)
                .build();
        UserAccount saved = userAccountRepository.save(user);
        log.info("회원가입 성공 - id: {}, username: {}", saved.getId(), saved.getUsername());
        return saved;
    }

    @Transactional
    public UserAccount updateProfile(UserAccount user, String newPassword, String profilePictureUrl, String description) {
        if (newPassword != null && !newPassword.isBlank()) {
            user.setPassword(passwordEncoder.encode(newPassword));
        }
        if (profilePictureUrl != null && !profilePictureUrl.isBlank()) {
            user.setProfilePictureUrl(profilePictureUrl);
        }
        user.setDescription(description);
        return userAccountRepository.save(user);
    }

    @Transactional
    public void deleteAccount(UserAccount user) {
        userAccountRepository.delete(user);
    }

    public Optional<UserAccount> findByUsername(String username) {
        return userAccountRepository.findByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserAccount user = userAccountRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("로그인 실패 - 사용자 없음: {}", username);
                    return new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username);
                });
        log.info("로그인 시도 - username: {}, role: {}", user.getUsername(), user.getRole());
        return new CustomUserDetails(user);
    }
}
