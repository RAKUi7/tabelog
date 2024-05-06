package com.example.tabelog_nagoyameshi.service;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Subscription;
import com.stripe.model.SubscriptionCollection;
import com.stripe.model.checkout.Session;
import com.stripe.param.SubscriptionListParams;
import com.stripe.param.checkout.SessionCreateParams;

@Service
public class StripeService {

    @Value("${stripe.api.key}")
    private String apiKey;
    
    public StripeService() {
        Stripe.apiKey = apiKey;
    }

    public String createCheckoutSession(String priceId, String userId) throws StripeException {
        Stripe.apiKey = apiKey;

        SessionCreateParams params = SessionCreateParams.builder()
            .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
            .addLineItem(SessionCreateParams.LineItem.builder()
                .setQuantity(1L)
                .setPrice(priceId)
                .build())
            .setSuccessUrl("https://tabelpg-nagoyameshi-9a4cce888c02.herokuapp.com/user/success?session_id={CHECKOUT_SESSION_ID}")
            .setCancelUrl("https://tabelpg-nagoyameshi-9a4cce888c02.herokuapp.com/user/index")
            .setClientReferenceId(userId)
            .build();

        Session session = Session.create(params);

        return session.getUrl();
    }
        
    public Session getSession(String sessionId) throws StripeException {
        Stripe.apiKey = apiKey;
        return Session.retrieve(sessionId);
    }
    
    public void cancelSubscription(String subscriptionId) throws StripeException {
        Stripe.apiKey = apiKey;
        Subscription subscription = Subscription.retrieve(subscriptionId);
        subscription.cancel(Map.of());
    }
    
    public static List<Subscription> fetchAllSubscriptions() throws StripeException {
        SubscriptionListParams params = SubscriptionListParams.builder()
            .build();

        SubscriptionCollection subscriptions = Subscription.list(params);
        return subscriptions.getData();
    }
}

