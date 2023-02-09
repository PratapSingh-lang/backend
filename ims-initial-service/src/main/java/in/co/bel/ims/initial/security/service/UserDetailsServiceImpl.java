package in.co.bel.ims.initial.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import in.co.bel.ims.initial.data.repository.ImsUserRepository;
import in.co.bel.ims.initial.entity.ImsUser;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
  @Autowired
  ImsUserRepository userRepository;

  @Override
  @Transactional
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		ImsUser user = userRepository.getByMobileNoAndDeleted(username, false);
		return UserDetailsImpl.build(user);
	}

}
