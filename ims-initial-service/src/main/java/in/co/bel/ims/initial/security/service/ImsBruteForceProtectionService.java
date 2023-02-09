package in.co.bel.ims.initial.security.service;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.co.bel.ims.initial.data.repository.ImsUserRepository;
import in.co.bel.ims.initial.entity.ImsUser;

@Service("bruteForceProtectionService")
public class ImsBruteForceProtectionService {

	private int maxFailedLogins = 3;
	private int cacheMaxLimit = 1000;

	@Autowired
	ImsUserRepository userRepository;

	private final ConcurrentHashMap<String, FailedLogin> cache;

	public ImsBruteForceProtectionService() {
		this.cache = new ConcurrentHashMap<>(cacheMaxLimit); // setting max limit for cache
	}

	public void registerLoginFailure(String username) {

		ImsUser user = getUser(username);
		if (user != null && !user.getLocked()) {
			int failedCounter = user.getLoginAttempts();
			if (maxFailedLogins < failedCounter + 1) {
				user.setLocked(true); 
				user.setLastLocked(LocalDateTime.now());
			} else {
				user.setLoginAttempts(failedCounter + 1);
			}
			userRepository.save(user);
		}
	}

	public void resetBruteForceCounter(String username) {
		ImsUser user = getUser(username);
		if (user != null) {
			user.setLoginAttempts(0);
			user.setLocked(false);
			user.setLastLocked(null);
			userRepository.save(user);
		}
	}

	public boolean isBruteForceAttack(String username) {
		ImsUser user = getUser(username);
		if (user != null) {
			return user.getLoginAttempts() >= maxFailedLogins ? true : false;
		}
		return false;
	}

	protected FailedLogin getFailedLogin(final String username) {
		FailedLogin failedLogin = cache.get(username);

		if (failedLogin == null) {
			failedLogin = new FailedLogin(0, LocalDateTime.now());
			cache.put(username, failedLogin);
			if (cache.size() > cacheMaxLimit) {
				cache.remove(username);
			}
		}
		return failedLogin;
	}

	private ImsUser getUser(final String username) {
		return userRepository.getByMobileNoAndDeleted(username, false);
	}

	public int getMaxFailedLogins() {
		return maxFailedLogins;
	}

	public void setMaxFailedLogins(int maxFailedLogins) {
		this.maxFailedLogins = maxFailedLogins;
	}

	public class FailedLogin {

		private int count;
		private LocalDateTime date;

		public FailedLogin() {
			this.count = 0;
			this.date = LocalDateTime.now();
		}

		public FailedLogin(int count, LocalDateTime date) {
			this.count = count;
			this.date = date;
		}

		public int getCount() {
			return count;
		}

		public void setCount(int count) {
			this.count = count;
		}

		public LocalDateTime getDate() {
			return date;
		}

		public void setDate(LocalDateTime date) {
			this.date = date;
		}
	}
}
