package kr.ac.kopo.cjj.myapp.custom;

import java.util.List;
import kr.ac.kopo.cjj.myapp.domain.UserAccount;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

public class CustomUserDetails extends User {
    private final Long id;
    private final String profilePictureUrl;

    public CustomUserDetails(UserAccount user) {
        super(
                user.getUsername(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority(user.getRole()))
        );
        this.id = user.getId();
        this.profilePictureUrl = user.getProfilePictureUrl() != null && !user.getProfilePictureUrl().isBlank()
                ? user.getProfilePictureUrl()
                : "/images/default-profile.png";
    }

    public Long getId() {
        return id;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }
}
