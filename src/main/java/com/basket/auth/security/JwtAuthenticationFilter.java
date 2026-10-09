package com.basket.auth.security;

import com.basket.auth.entity.User;
import com.basket.auth.repository.UserRepository;
import com.basket.auth.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            UserRepository userRepository)
    {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }
    @Override
    protected void doFilterInternal(
                                    HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
                                     throws ServletException, IOException {

        String authorizationHeader =
                request.getHeader("Authorization");

        if(authorizationHeader == null || !authorizationHeader.startsWith("Bearer "))
        {
            filterChain.doFilter(request,response);
            return;
        }

            //Bearer removed and jwt will be extract
         String token = authorizationHeader.substring(7);

        try{
            if(jwtService.isTokenValid(token))
            {
                //jwt se email extract
                String email = jwtService.extractEmail(token);

                //dB se user find karo
                User user =  userRepository.findByEmail(email).orElseThrow(()->
                                new RuntimeException("User not found"));

                //tell spring security that user is authenticated
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        user,
                        null,
                        List.of(
                                new SimpleGrantedAuthority(
                                        "ROLE_" + user.getRole().name()
                                )
                        )
                );

                SecurityContextHolder
                        .getContext()
                        .setAuthentication(authentication);

            }
        }catch (Exception e)
        {
            System.out.println("Jwt Validation Failed...." + e.getMessage());
        }

        filterChain.doFilter(request,response);

    }
}
