package in.co.bel.ims.initial.security.jwt;

import java.io.IOException;
import java.time.LocalDateTime;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import in.co.bel.ims.initial.data.repository.SessionManagementRepository;
import in.co.bel.ims.initial.entity.SessionManagement;
import in.co.bel.ims.initial.security.service.UserDetailsServiceImpl;

public class AuthTokenFilter extends OncePerRequestFilter {
	@Autowired
	private JwtUtils jwtUtils;

	@Autowired
	private UserDetailsServiceImpl userDetailsService;
	
	@Autowired
	private SessionManagementRepository managementRepository;

	private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		try {
			String jwt = parseJwt(request);
			if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
				String username = jwtUtils.getUserNameFromJwtToken(jwt);

				UserDetails userDetails = userDetailsService.loadUserByUsername(username);

				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
						userDetails, null, userDetails.getAuthorities());
				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

				SecurityContextHolder.getContext().setAuthentication(authentication);
				
				if(request.getRequestURI().endsWith("logout")) {
					SessionManagement sessionManagement = managementRepository.findByTokenAndValid(jwt, true);
					if(sessionManagement != null) {
						sessionManagement.setValid(false);
						sessionManagement.setInvalidatedOn(LocalDateTime.now());
						managementRepository.save(sessionManagement);
					}
				}
			}
		} catch (Exception e) {
			logger.error("Cannot set user authentication: {}", e);
		}

		filterChain.doFilter(request, response);
	}

	private String parseJwt(HttpServletRequest request) {
		String headerAuth = request.getHeader("Authorization");

		if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
			return headerAuth.substring(7, headerAuth.length());
		}

		return null;
	}

}
