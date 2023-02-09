package in.co.bel.ims.initial.security.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import in.co.bel.ims.initial.security.service.UserDetailsServiceImpl;

@SuppressWarnings("deprecation")
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
		// securedEnabled = true,
		// jsr250Enabled = true,
		prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	@Autowired
	UserDetailsServiceImpl userDetailsService;

	@Autowired
	private AuthEntryPointJwt unauthorizedHandler;

	@Bean
	public AuthTokenFilter authenticationJwtTokenFilter() {
		return new AuthTokenFilter();
	}

	@Override
	public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
		authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		/*
		 * http .authorizeRequests() .antMatchers(HttpMethod.OPTIONS,"/**").denyAll()
		 * .and() .headers() //.defaultsDisabled()
		 * .contentSecurityPolicy("script-src 'self'") .and()
		 * .contentSecurityPolicy("style-src 'self'");
		 * 
		 * http .headers() //Strict-Transport-Security .httpStrictTransportSecurity()
		 * .includeSubDomains(true) .maxAgeInSeconds(31536000);
		 */
		
		
		    http.cors().and().csrf().disable()
		    .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
			.authorizeRequests().antMatchers("/app/api/auth/**").permitAll().and()
			.authorizeRequests().antMatchers("/app/imsUser/validateUser/**").permitAll().and()
			.authorizeRequests().antMatchers("/app/imsUser/validateAdminUser/**").permitAll().and()
			.authorizeRequests().antMatchers("/app/imsUser/forgotPassword/**").permitAll().and()
			.authorizeRequests().antMatchers("/app/imsUser/resendOtp/**").permitAll().and()
			.authorizeRequests().antMatchers("/app/authentication/getCaptcha/**").permitAll().and()
			.authorizeRequests().antMatchers("/app/scanningOfficer/validateUser/**").permitAll().and()
			.authorizeRequests().antMatchers("/app/scanningOfficer/signin/**").permitAll().and()
			.authorizeRequests().antMatchers("/app/imsUser/validateMobileNo/**").permitAll().and()
			.authorizeRequests().antMatchers("/app/imsUser/resendPwdForgotOtp/**").permitAll().and()
			.authorizeRequests().antMatchers("/app/imsUser/resendPwdForgotEmailOtp/**").permitAll().and()
//			TODO:  JWT token from User defined field from payment GW
			.authorizeRequests().antMatchers("/app/paymentManager/handlePaymentSuccess/**").permitAll().and()
			.authorizeRequests().antMatchers("/app/paymentManager/handlePaymentFailure/**").permitAll().and()
			.authorizeRequests().antMatchers("/app/imsUser/sendOTP/**").permitAll().and()
			.authorizeRequests().antMatchers("/app/imsUser/resendEmailOtp/**").permitAll().and()
			.authorizeRequests().antMatchers("/app/imsUser/resendRegisterOtp/**").permitAll().and()
			.authorizeRequests().antMatchers("/app/passDayLimitCategory/getTicketAvailabilityStats/**").permitAll().and()
			.authorizeRequests().antMatchers("/app/imsUser/register/**").permitAll()
			.anyRequest().authenticated();
//		
//		http.cors().and().csrf().disable()
//		.exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
//		.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
//		.authorizeRequests().antMatchers("/app/**").permitAll()
//		.anyRequest().authenticated();
//		
		http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
	}
}
