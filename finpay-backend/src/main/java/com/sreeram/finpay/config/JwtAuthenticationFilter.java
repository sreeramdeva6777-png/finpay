package com.sreeram.finpay.config;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.sreeram.finpay.service.JwtService;

import io.jsonwebtoken.JwtException;

import com.sreeram.finpay.service.CustomUserDetailsService;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	private final JwtService jwtService;
	private final CustomUserDetailsService userDetailsService;
	public JwtAuthenticationFilter(
	        JwtService jwtService,
	        CustomUserDetailsService userDetailsService) {

	    this.jwtService = jwtService;
	    this.userDetailsService = userDetailsService;
	}
	@Override
	protected void doFilterInternal(
	        HttpServletRequest request,
	        HttpServletResponse response,
	        FilterChain filterChain)
	        throws ServletException, IOException {
		if (request.getServletPath().equals("/users/login")
		        || request.getServletPath().equals("/users/register")) {

		    filterChain.doFilter(request, response);
		    return;
		}	
		String authHeader = request.getHeader("Authorization");
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
		    filterChain.doFilter(request, response);
		    return;
		}
		try {

		    String jwt = authHeader.substring(7);

		    String username = jwtService.extractUsername(jwt);

		    UserDetails userDetails =
		            userDetailsService.loadUserByUsername(username);

		    if (jwtService.isTokenValid(jwt, userDetails)) {

		        Authentication authentication =
		                new UsernamePasswordAuthenticationToken(
		                        userDetails,
		                        null,
		                        userDetails.getAuthorities()
		                );

		        SecurityContextHolder.getContext()
		                .setAuthentication(authentication);
		    }

		    filterChain.doFilter(request, response);

		} catch (JwtException ex) {

		    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		    return;
		}
	}
}