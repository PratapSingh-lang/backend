package in.co.bel.ims.initial.security.jwt;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import in.co.bel.ims.initial.data.repository.SessionManagementRepository;
import in.co.bel.ims.initial.entity.SessionManagement;
import in.co.bel.ims.initial.security.service.UserDetailsImpl;
import in.co.bel.ims.initial.service.util.RoleEnum;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

@Component
public class JwtUtils {
	private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

	@Value("${jwtSecret}")
	private String jwtSecret;

	@Value("${jwtExpirationMs}")
	private int jwtExpirationMs;
	
	@Value("${jwtExpirationMsHighRoles}")
	private int jwtExpirationMsHighRoles;
	
	@Autowired
	private SessionManagementRepository managementRepository;

	public String generateJwtToken(Authentication authentication) {

		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

		Map<String, Object> claims = new HashMap<>();
		claims.put("role", userPrincipal.getAuthorities());
		int expiryMillis = jwtExpirationMs;
		String currentRole = userPrincipal.getAuthorities().stream().findFirst().get().getAuthority();
		if (currentRole.equals(RoleEnum.ROLE_SUPERADMIN.name()) || currentRole.equals(RoleEnum.ROLE_NODALOFFICER.name())
				|| currentRole.equals(RoleEnum.ROLE_INVITATIONADMIN.name())
				|| currentRole.equals(RoleEnum.ROLE_COUNTEREMP.name()))
			expiryMillis = jwtExpirationMsHighRoles;
		return Jwts.builder().setSubject((userPrincipal.getUsername())).setIssuedAt(new Date()).claim("role", userPrincipal.getAuthorities())
				.setExpiration(new Date((new Date()).getTime() + expiryMillis))
				.signWith(SignatureAlgorithm.HS512, jwtSecret).compact();
	}

	public String getUserNameFromJwtToken(String token) {
		if (token != null)
			token = token.replace("Bearer ", "");
		return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
	}
	
	public String getMobileNoFromJwtToken(String token) {
		if (token != null)
			token = token.replace("Bearer ", "");
		return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getAudience();
	}
	
	public String getRoleFromJwtToken(String token) {
		if(token != null)
			token = token.replace("Bearer ", "");
		 Jws<Claims> claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
		 @SuppressWarnings("unchecked")
		List<Map<String, String>> roles = (List<Map<String, String>>) claims.getBody().get("role");
		 String role = roles.stream().findFirst().get().get("authority");
		 return role;
	}

	public boolean validateJwtToken(String authToken) {
		try {
			Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
			SessionManagement sessionManagement = managementRepository.findByTokenAndValid(authToken, true);
			if(sessionManagement != null)
				return true;
		} catch (SignatureException e) {
			logger.error("Invalid JWT signature: {}", e.getMessage());
		} catch (MalformedJwtException e) {
			logger.error("Invalid JWT token: {}", e.getMessage());
		} catch (ExpiredJwtException e) {
			logger.error("JWT token is expired: {}", e.getMessage());
		} catch (UnsupportedJwtException e) {
			logger.error("JWT token is unsupported: {}", e.getMessage());
		} catch (IllegalArgumentException e) {
			logger.error("JWT claims string is empty: {}", e.getMessage());
		}

		return false;
	}
}
