package in.co.bel.ims.initial.security.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import in.co.bel.ims.initial.data.repository.ImsUserRepository;
import in.co.bel.ims.initial.data.repository.RoleRepository;
import in.co.bel.ims.initial.data.repository.SessionManagementRepository;
import in.co.bel.ims.initial.entity.ImsUser;
import in.co.bel.ims.initial.entity.SessionManagement;
import in.co.bel.ims.initial.security.jwt.JwtUtils;
import in.co.bel.ims.initial.service.dto.JwtResponse;
import in.co.bel.ims.initial.service.dto.LoginRequest;
import in.co.bel.ims.initial.service.util.ImsCipherUtil;
import in.co.bel.ims.initial.service.util.LogLevelEnum;
import in.co.bel.ims.initial.service.util.LogUtil;
import in.co.bel.ims.initial.service.util.RoleEnum;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/app/api/auth")
public class AuthController {
	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	ImsUserRepository userRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	JwtUtils jwtUtils;

	@Autowired
	private SessionManagementRepository trackerRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	ImsBruteForceProtectionService bruteForceProtectionService;
	
	@Autowired
	LogUtil log;

	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {

		String username = loginRequest.getUsername();
		String password = ImsCipherUtil.decrypt(loginRequest.getPassword());
		String emailOtp = null;
		if(loginRequest.getEmailOtp() != null )
			emailOtp = ImsCipherUtil.decrypt(loginRequest.getEmailOtp());

		ImsUser imsUser = userRepository.getByMobileNoAndDeleted(username, false);
		if (imsUser != null) {

			System.out.println("User Logged-In Successfully! : " + username);
			if (imsUser.getLoggedIn() == 0 || releaseLoggedInUser(imsUser)) {
				if (imsUser.getLocked() && !releaseLockedUser(imsUser)) {
					log.saveLog(null,
							"An account with the mobile number " + username
									+ " has been locked due to 3 failed attempts. It will be unlocked after 24 hours",
							"USER_LOGIN", LogLevelEnum.WARN);
					return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
							"Your account has been locked due to 3 failed attempts. It will be unlocked after 24 hours.");
				} else {
					if (passwordEncoder.matches(password, imsUser.getPassword())) {
						if (imsUser.getRole().getId() != RoleEnum.ROLE_CITIZEN.role
								&& imsUser.getRole().getId() != RoleEnum.ROLE_INVITEE.role) {
							if (!passwordEncoder.matches(emailOtp, imsUser.getPasswordEmail())) {
								return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.UNAUTHORIZED)
										.body("Invalid Email OTP");
							}
						}
						bruteForceProtectionService.resetBruteForceCounter(username);
						Authentication authentication = authenticationManager
								.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
										ImsCipherUtil.decrypt(loginRequest.getPassword())));

						SecurityContextHolder.getContext().setAuthentication(authentication);

						String jwt = jwtUtils.generateJwtToken(authentication);

						SessionManagement sessionTracker = new SessionManagement();
						sessionTracker.setToken(jwt);
						trackerRepository.save(sessionTracker);

						UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
						List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
								.collect(Collectors.toList());

						ImsUser user = userRepository.getByMobileNoAndDeleted(loginRequest.getUsername(), false);
						if (user != null) {
							user.setLoggedIn(1);
							user.setLastLogin(LocalDateTime.now());
							userRepository.save(user);
						}
						log.saveLog(user, "User Logged-In Successfully!", "USER_LOGIN", LogLevelEnum.INFO);
						return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(),
								userDetails.getEmail(), roles, userDetails.getStatus(), userDetails.getName()));
					} else {
						bruteForceProtectionService.registerLoginFailure(username);
						log.saveLog(null,
								"Invalid Username/Password. User tried to login with the mobile number " + username,
								"USER_LOGIN", LogLevelEnum.WARN);
						return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.UNAUTHORIZED)
								.body("Invalid Mobile OTP");
					}
				}
			} else {
				log.saveLog(null, "User with the mobile number " + username + " has already logged-in", "USER_CREATION",
						LogLevelEnum.WARN);
				return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.UNAUTHORIZED)
						.body("User already logged-in!");
			}

		} else {
			log.saveLog(null, "Invalid Username/Password. User tried to login with the mobile number " + username,
					"USER_LOGIN", LogLevelEnum.WARN);
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid OTP");
		}

	}

	private boolean releaseLoggedInUser(ImsUser user) {

		if (user.getLastLogin() != null) {
			LocalDateTime login2Hrs = user.getLastLogin().plus(30, ChronoUnit.MINUTES);
			if (login2Hrs.isBefore(LocalDateTime.now())) {
				user.setLoggedIn(0);
				user.setLastLogin(null);
				userRepository.save(user);

				return true;
			}
		}

		return false;
	}

	private boolean releaseLockedUser(ImsUser user) {
		if (user.getLastLocked() != null) {
			LocalDateTime locked24Hrs = user.getLastLocked().plus(60, ChronoUnit.MINUTES);
			if (locked24Hrs.isBefore(LocalDateTime.now())) {
				user.setLocked(false);
				user.setLastLocked(null);
				user.setLoginAttempts(0);
				userRepository.save(user);
				return true;
			}
		}
		return false;
	}
	
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(Exception.class)
	public String handleCustomExceptions(Exception ex) {
		ex.printStackTrace();
		return "Something went wrong at the server!";
	}
}
