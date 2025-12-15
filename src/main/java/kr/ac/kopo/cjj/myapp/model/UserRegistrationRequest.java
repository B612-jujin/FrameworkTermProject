package kr.ac.kopo.cjj.myapp.model;

import lombok.Data;

@Data
public class UserRegistrationRequest {
    private String username;
    private String password;
    private String profilePictureUrl;
}
