package com.example.samuraitravel.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.example.samuraitravel.service.StripeService;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;

@Controller
public class StripeWebhookController {
    private final StripeService stripeService;

    @Value("${stripe.api-key}")
    private String stripeApiKey;

    @Value("${stripe.webhook-secret}")
    private String webhookSecret;

    public StripeWebhookController(StripeService stripeService) {
        this.stripeService = stripeService;
    }

    @PostMapping("/stripe/webhook")
    /* 予約一覧ページへのリダイレクトはStripe側でおこなってくれるため、このwebhook()メソッドの
     * 役割は「イベント通知を受け、予約情報をデータベースに登録すること」のみである。
     * よって、最後はビューを表示したりリダイレクトさせたりする代わりにHTTPのステータスコードを返している。
     */
    public ResponseEntity<String> webhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {
        Stripe.apiKey = stripeApiKey;
        Event event = null;
        
        // stripeから決済失敗や完了のイベントを受け取り、event変数に格納している
        try {
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        /* Stripeから受信できるイベントにはたくさんの種類がありますが、今回受信するのは
         * "checkout.session.completed"、つまり「決済成功イベント」である。
         * そのため以下のコードでは、イベントの種類が"checkout.session.completed"と等しいときに、
         * StripeServiceクラスに定義したprocessSessionCompleted()メソッドを呼び出しています。
         */
        
        if ("checkout.session.completed".equals(event.getType())) {
            stripeService.processSessionCompleted(event);
        }

        return new ResponseEntity<>("Success", HttpStatus.OK);
    }
}