package com.tuanyi.voting.config;

import com.tuanyi.voting.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthenticationInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request,
                             @NonNull HttpServletResponse response,
                             @NonNull Object handler) throws Exception {
        var uri = request.getRequestURI();
        var session = request.getSession();

        if (session == null) {
            handleUnauthorized(uri, response);
            return false;
        }

        var user = (User) session.getAttribute("user");

        if (user == null) {
            handleUnauthorized(uri, response);
            return false;
        }

        var privilegedURI = uri.startsWith("/admin") || uri.startsWith("/api/admin");
        if (privilegedURI && !user.isAdmin) {
            handleForbidden(uri, response);
            return false;
        }

        request.setAttribute("user", user);
        request.setAttribute("userId", user.userId);
        return true;
    }

    private void handleUnauthorized(String uri, HttpServletResponse response) throws Exception {
        if (uri.startsWith("/api")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setCharacterEncoding("utf-8");
            var writer = response.getWriter();
            writer.println("尚未登录");
            writer.flush();
        } else {
            response.sendRedirect("/uis");
        }
    }

    private void handleForbidden(String uri, HttpServletResponse response) throws Exception {
        if (uri.startsWith("/api")) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setCharacterEncoding("utf-8");
            var writer = response.getWriter();
            writer.println("没有权限");
            writer.flush();
        } else {
            response.sendRedirect("/forbidden");
        }
    }
}

