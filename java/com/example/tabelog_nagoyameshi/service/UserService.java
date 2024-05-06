package com.example.tabelog_nagoyameshi.service;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.tabelog_nagoyameshi.entity.Role;
import com.example.tabelog_nagoyameshi.entity.User;
import com.example.tabelog_nagoyameshi.form.PasswordChangeForm;
import com.example.tabelog_nagoyameshi.form.SignupForm;
import com.example.tabelog_nagoyameshi.form.UserEditForm;
import com.example.tabelog_nagoyameshi.repository.RoleRepository;
import com.example.tabelog_nagoyameshi.repository.UserRepository;
import com.example.tabelog_nagoyameshi.security.UserDetailsImpl;

@Service
public class UserService {
	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;

	public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Transactional
	public User create(SignupForm signupForm) {
		User user = new User();
		Role role = roleRepository.findByName("ROLE_FREE");

//		Role role = roleOptional.orElseThrow();

		user.setName(signupForm.getName());
		user.setEmail(signupForm.getEmail());
		user.setPassword(passwordEncoder.encode(signupForm.getPassword()));
		user.setPostalCode(signupForm.getPostalCode());
		user.setAddress(signupForm.getAddress());
		user.setPhoneNumber(signupForm.getPhoneNumber());
		user.setRole(role);
		user.setEnabled(false);

		return userRepository.save(user);
	}

	@Transactional
	public void update(UserEditForm userEditForm) {
		User user = userRepository.getReferenceById(userEditForm.getId());

		user.setName(userEditForm.getName());
		user.setEmail(userEditForm.getEmail());
		user.setPostalCode(userEditForm.getPostalCode());
		user.setAddress(userEditForm.getAddress());
		user.setPhoneNumber(userEditForm.getPhoneNumber());

		userRepository.save(user);
	}

	public boolean isEmailRegistered(String email) {
		User user = userRepository.findByEmail(email);
		return user != null;
	}

	public boolean isSamePassword(String password, String passwordConfirmation) {
		return password.equals(passwordConfirmation);
	}

	@Transactional
	public void enableUser(User user) {
		user.setEnabled(true);
		userRepository.save(user);
	}

	public boolean isEmailChanged(UserEditForm userEditForm) {
		User currentUser = userRepository.getReferenceById(userEditForm.getId());
		return !userEditForm.getEmail().equals(currentUser.getEmail());
	}

	@Transactional
	public void upgradeToPremium(Integer userId) {
		User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("ユーザーが見つかりませんでした。"));
		Role premiumRole = roleRepository.findByName("ROLE_PREMIUM");
//				.orElseThrow(() -> new IllegalArgumentException("ロールが見つかりませんでした。"));

		user.setRole(premiumRole);
		userRepository.save(user);
		updateUserSecurityContext(user);
	}

	@Transactional
	public void downgradeToBasic(Integer userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("ユーザーが見つかりませんでした。"));
		Role generalRole = roleRepository.findByName("ROLE_FREE");
//				.orElseThrow(() -> new IllegalArgumentException("ロールが見つかりませんでした。"));

		user.setRole(generalRole);
		userRepository.save(user);
		updateUserSecurityContext(user);

	}

	private void updateUserSecurityContext(User user) {
		Collection<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority(user.getRole().getName()));
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
				new UserDetailsImpl(user, authorities),
				user.getPassword(), authorities);
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}

	public boolean changePassword(User user, PasswordChangeForm passwordChangeForm) {
		// 旧パスワードの検証
		if (!passwordEncoder.matches(passwordChangeForm.getOldPassword(), user.getPassword())) {
			return false;
		}

		// 新パスワードの暗号化と更新
		user.setPassword(passwordEncoder.encode(passwordChangeForm.getNewPassword()));
		userRepository.save(user);
		return true;
	}

}
