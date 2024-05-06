package com.example.tabelog_nagoyameshi.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.example.tabelog_nagoyameshi.service.UserService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;

@RestController
public class StripeWebhookController {

	@Value("${stripe.webhook.secret}")
	private String webhookSecret;

	private final UserService userService;
	private final Logger logger = LoggerFactory.getLogger(StripeWebhookController.class);

	public StripeWebhookController(UserService userService) {
		this.userService = userService;
	}

	@PostMapping("/stripe-webhook")
	public ResponseEntity<String> handleStripeWebhook(
			@RequestBody String payload,
			@RequestHeader("Stripe-Signature") String sigHeader) {
		try {
			Event event = Webhook.constructEvent(payload, sigHeader, webhookSecret);

			if ("checkout.session.completed".equals(event.getType())) {
				Session session = (Session) event.getData().getObject();
				String userId = session.getClientReferenceId();

				if (userId != null && !userId.trim().isEmpty()) {
					try {
						userService.upgradeToPremium(Integer.parseInt(userId));
					} catch (NumberFormatException e) {
						logger.error("Invalid user ID format: {}", userId, e);
						return ResponseEntity.badRequest().body("Invalid user ID format");
					}
				} else {
					logger.error("User ID is null or empty");
					return ResponseEntity.badRequest().body("User ID is null or empty");
				}
			}

			return ResponseEntity.ok("Webhook processed successfully");
		} catch (SignatureVerificationException e) {
			logger.error("Signature verification failed", e);
			return ResponseEntity.status(400).body("Signature verification failed");
		} catch (Exception e) {
			logger.error("Webhook processing failed", e);
			return ResponseEntity.status(500).body("Webhook processing failed");
		}
	}
}