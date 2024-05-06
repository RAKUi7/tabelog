package com.example.tabelog_nagoyameshi.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.tabelog_nagoyameshi.entity.User;
import com.example.tabelog_nagoyameshi.form.PasswordChangeForm;
import com.example.tabelog_nagoyameshi.form.UserEditForm;
import com.example.tabelog_nagoyameshi.repository.UserRepository;
import com.example.tabelog_nagoyameshi.security.UserDetailsImpl;
import com.example.tabelog_nagoyameshi.service.StripeService;
import com.example.tabelog_nagoyameshi.service.UserService;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;

@Controller
@RequestMapping("/user")
public class UserController {
	private final UserRepository userRepository;
	private final UserService userService;
	private final StripeService stripeService;

	public UserController(UserRepository userRepository, UserService userService, StripeService stpripeService) {
		this.userRepository = userRepository;
		this.userService = userService;
		this.stripeService = stpripeService;

	}

	@GetMapping
	public String index(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, Model model) {
		User user = userRepository.getReferenceById(userDetailsImpl.getUser().getId());
		model.addAttribute("user", user);
		model.addAttribute("roles", user.getRole());

		return "user/index";
	}

	@GetMapping("/edit")
	public String edit(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, Model model) {
		User user = userRepository.getReferenceById(userDetailsImpl.getUser().getId());
		UserEditForm userEditForm = new UserEditForm(user.getId(), user.getName(), user.getEmail(),
				user.getPostalCode(), user.getAddress(), user.getPhoneNumber());

		model.addAttribute("userEditForm", userEditForm);

		return "user/edit";
	}

	@PostMapping("/update")
	public String update(@ModelAttribute @Validated UserEditForm userEditForm, BindingResult bindingResult,
			RedirectAttributes redirectAttributes) {
		if (userService.isEmailChanged(userEditForm) && userService.isEmailRegistered(userEditForm.getEmail())) {
			FieldError fieldError = new FieldError(bindingResult.getObjectName(), "email", "すでに登録済みのメールアドレスです。");
			bindingResult.addError(fieldError);
		}

		if (bindingResult.hasErrors()) {
			return "user/edit";
		}

		userService.update(userEditForm);
		redirectAttributes.addFlashAttribute("successMessage", "会員情報を編集しました。");

		return "redirect:/user";
	}

	@PostMapping("/delete")
	public String delete(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
			RedirectAttributes redirectAttributes) {

		Integer userId = userDetailsImpl.getUser().getId();

		userRepository.deleteById(userId);

		redirectAttributes.addFlashAttribute("successMessage", "退会手続きが完了しました。");

		return "redirect:/";
	}

	@PostMapping("/upgrade")
	public String upgradeToPremium(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
			RedirectAttributes redirectAttributes) {
		try {
	        String userId = String.valueOf(userDetailsImpl.getUser().getId());
	        String checkoutSessionUrl = stripeService.createCheckoutSession("price_1Os0EJLYiINAfgxSCEquWjfA", userId);
	        return "redirect:" + checkoutSessionUrl;
	    } catch (Exception e) {
	        e.printStackTrace();
	        redirectAttributes.addFlashAttribute("error", "サブスクリプションの作成に失敗しました。");
	        return "redirect:/user";
	    }
	}
	
	@GetMapping("/success")
	public String success(@RequestParam String session_id, Model model, @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
		try {
	        Session session = stripeService.getSession(session_id);
	        String clientReferenceId = session.getClientReferenceId();
	        if (clientReferenceId != null && !clientReferenceId.isEmpty()) {
	            userService.upgradeToPremium(Integer.parseInt(clientReferenceId));
	            User updatedUser = userRepository.findById(Integer.parseInt(clientReferenceId)).orElse(null);
	            if (updatedUser != null) {
	                model.addAttribute("user", updatedUser);
	                model.addAttribute("successMessage", "有料会員にアップグレードしました。");
	            } else {
	                model.addAttribute("errorMessage", "ユーザーが見つかりません。");
	            }
	        } else {
	            model.addAttribute("errorMessage", "無効なセッションです。");
	        }
	    } catch (StripeException e) {
	        e.printStackTrace();
	        model.addAttribute("errorMessage", "サブスクリプションの処理中にエラーが発生しました。");
	    }
	    return "user/index";
	}
	
	@PostMapping("/downgrade")
	public String downgradeToBasic(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
	                               RedirectAttributes redirectAttributes) {
	    try {
	        userService.downgradeToBasic(userDetailsImpl.getUser().getId());
	        redirectAttributes.addFlashAttribute("successMessage", "無料会員にダウングレードしました。");
	    } catch (RuntimeException e) {
	        redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
	    }
	    return "redirect:/user";
	}
	
	@GetMapping("/changepass")
	public String showChangePasswordPage(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, Model model) {
	    model.addAttribute("passwordChangeForm", new PasswordChangeForm());
	    return "user/changePassword";
	}

	@PostMapping("/changepass")
	public String changePassword(@ModelAttribute @Validated PasswordChangeForm passwordChangeForm,
	                             BindingResult bindingResult,
	                             @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
	                             RedirectAttributes redirectAttributes) {
	    if (!passwordChangeForm.getNewPassword().equals(passwordChangeForm.getConfirmNewPassword())) {
	        bindingResult.addError(new FieldError("passwordChangeForm", "confirmNewPassword", "新しいパスワードが一致しません"));
	    }

	    if (!bindingResult.hasErrors()) {
	        if (userService.changePassword(userDetailsImpl.getUser(), passwordChangeForm)) {
	            redirectAttributes.addFlashAttribute("successMessage", "パスワードが正常に変更されました");
	            return "redirect:/user";
	        } else {
	            bindingResult.addError(new FieldError("passwordChangeForm", "oldPassword", "現在のパスワードが正しくありません"));
	        }
	    }

	    return "user/changePassword";
	}

}
