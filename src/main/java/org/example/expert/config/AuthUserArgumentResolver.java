package org.example.expert.config;

import org.example.expert.domain.auth.exception.AuthException;
import org.example.expert.domain.common.annotation.Auth;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.user.enums.UserRole;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class AuthUserArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean hasAuthAnnotation = parameter.getParameterAnnotation(Auth.class) != null;
        boolean isAuthUserType = parameter.getParameterType().equals(AuthUser.class);

        // @Auth 어노테이션과 AuthUser 타입이 함께 사용되지 않은 경우 예외 발생
        if (hasAuthAnnotation != isAuthUserType) {
            throw new AuthException("@Auth와 AuthUser 타입은 함께 사용되어야 합니다.");
        }

        return hasAuthAnnotation;
    }

    @Override
    public Object resolveArgument(
            @Nullable MethodParameter parameter,
            @Nullable ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            @Nullable WebDataBinderFactory binderFactory
    ) {
        // Spring Security에서 현재 인증된 사용자의 정보를 가져옵니다.
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // 인증된 사용자가 없는 경우 예외를 던질 수 있습니다.
        if (principal == null || !(principal instanceof User)) {
            throw new AuthException("인증된 사용자가 없습니다.");
        }

        User user = (User) principal;

        // JwtFilter에서 설정한 userId, userRole 등은 이 시점에서 SecurityContextHolder에서 가져올 수 있습니다.
        Long userId = Long.valueOf(user.getUsername()); // user.getUsername()은 userId로 사용될 수 있습니다.
        String email = user.getUsername(); // 이메일이 user의 username에 포함된 경우, 또는 다른 방식으로 가져올 수 있습니다.
        UserRole userRole = UserRole.valueOf(user.getAuthorities().toArray()[0].toString());

        return new AuthUser(userId, email, userRole);
    }
}
