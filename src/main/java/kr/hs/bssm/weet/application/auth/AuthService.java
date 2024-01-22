package kr.hs.bssm.weet.application.auth;

import kr.hs.bssm.weet.application.user.UserService;
import kr.hs.bssm.weet.domain.user.User;
import kr.hs.bssm.weet.global.jwt.util.JwtProvider;
import kr.hs.bssm.weet.presentation.auth.dto.response.LoginResponseDto;
import leehj050211.bsmOauth.BsmOauth;
import leehj050211.bsmOauth.dto.resource.BsmUserResource;
import leehj050211.bsmOauth.exception.BsmOAuthCodeNotFoundException;
import leehj050211.bsmOauth.exception.BsmOAuthInvalidClientException;
import leehj050211.bsmOauth.exception.BsmOAuthTokenNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final BsmOauth bsmOauth;
    private final UserService userService;
    private final JwtProvider jwtProvider;

    public LoginResponseDto login(String code) throws BsmOAuthInvalidClientException, IOException, BsmOAuthCodeNotFoundException, BsmOAuthTokenNotFoundException {
        String bsmOAuthToken = bsmOauth.getToken(code);
        BsmUserResource resource = bsmOauth.getResource(bsmOAuthToken);

        User user = userService.findByEmail(resource.getEmail())
                .orElseGet(() -> userService.createUser(resource));

        return new LoginResponseDto(
                jwtProvider.accessToken(user),
                jwtProvider.refreshToken(user)
        );
    }
}