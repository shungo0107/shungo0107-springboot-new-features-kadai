package com.example.samuraitravel.service;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.samuraitravel.form.ReservationRegisterForm;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.param.checkout.SessionRetrieveParams;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class StripeService {
    @Value("${stripe.api-key}")
    private String stripeApiKey;

    private final ReservationService reservationService;
    
    public StripeService(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    // セッションを作成し、Stripeに必要な情報を返す
    public String createStripeSession(String houseName, 
    		                                              ReservationRegisterForm reservationRegisterForm, 
    		                                              HttpServletRequest httpServletRequest) {
        Stripe.apiKey = stripeApiKey;
        String requestUrl = new String(httpServletRequest.getRequestURL());
        SessionCreateParams params =
            SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .addLineItem(
                    SessionCreateParams.LineItem.builder()
                        .setPriceData(
                            SessionCreateParams.LineItem.PriceData.builder()   
                                .setProductData(
                                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                        .setName(houseName)
                                        .build())
                                .setUnitAmount((long)reservationRegisterForm.getAmount())
                                .setCurrency("jpy")                                
                                .build())
                        .setQuantity(1L)
                        .build())
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(requestUrl.replaceAll("/houses/[0-9]+/reservations/confirm", "") + "/reservations?reserved")
                .setCancelUrl(requestUrl.replace("/reservations/confirm", ""))
                .setPaymentIntentData(
                    SessionCreateParams.PaymentIntentData.builder()
                        .putMetadata("houseId", reservationRegisterForm.getHouseId().toString())
                        .putMetadata("userId", reservationRegisterForm.getUserId().toString())
                        .putMetadata("checkinDate", reservationRegisterForm.getCheckinDate())
                        .putMetadata("checkoutDate", reservationRegisterForm.getCheckoutDate())
                        .putMetadata("numberOfPeople", reservationRegisterForm.getNumberOfPeople().toString())
                        .putMetadata("amount", reservationRegisterForm.getAmount().toString())
                        .build())
                .build();
        try {
            Session session = Session.create(params);
            return session.getId();
        } catch (StripeException e) {
            e.printStackTrace();
            return "";
        }
    } 
    
    // セッションから予約情報を取得し、ReservationServiceクラスを介してデータベースに登録する
    /* このメソッドでは、メソッドでは、引数として受け取ったEventオブジェクトからStripeObjectオブジェクトを取得し、
     * それをSessionオブジェクトに型変換しています。
     * 　・Eventオブジェクト           ＝ Stripeから通知されるイベントの内容を表現したオブジェクト
     * 　・StripeObjectオブジェクト＝ StripeのAPIから返されるデータを表現する基本的なオブジェクト
     */
    public void processSessionCompleted(Event event) {
    	/* Optionalとは、nullを持つ可能性のあるオブジェクトを扱うためのクラスのことです。
    	 * Optionalクラスを使う主なメリットは以下のとおりです。
    	 *     ・nullチェックを簡単に行えるため、コードがシンプルになる
    	 *     ・オブジェクトがnullであるために発生するエラーのリスクを減らせる
    	 *     ・オブジェクトがnullを返す可能性があることを明示的に示せる（nullチェックの必要性を明確にできる）
    	 *  Optionalクラスが提供するifPresent()メソッドを使うことで、以下のように簡潔にnullチェックができる。
    	 *  
    	 *    Optional<StripeObject> optionalStripeObject = event.getDataObjectDeserializer().getObject();
    	 *    optionalStripeObject.ifPresent(stripeObject -> { nullでない場合の処理 });
    	 */
        Optional<StripeObject> optionalStripeObject = event.getDataObjectDeserializer().getObject();
        optionalStripeObject.ifPresentOrElse(stripeObject -> {
            Session session = (Session)stripeObject;
            /*以下のコードでは"payment_intent"情報を展開する（詳細情報を含める）ように
             * 指定したSessionRetrieveParamsオブジェクトを生成しています
             * （このあと詳細なSessionオブジェクトを取得するために必要）
             */
            SessionRetrieveParams params = SessionRetrieveParams.builder().addExpand("payment_intent").build();
            
            /* Sessionオブジェクトから取得したセッションIDと前述のSessionRetrieveParamsオブジェクトを
             * 渡し、支払い情報を含む詳細なセッション情報を取得している。さらに、取得した詳細な
             * セッション情報からメタデータ（予約情報）を取り出し、それをpaymentIntentObjectとして
             * reservationServiceクラスのcreate()メソッドに渡している
             */
            try {
            	// stribeからリダイレクトされたsessionの情報を取得し、Mapに格納する。そして、最後にデータベースに登録する
                session = Session.retrieve(session.getId(), params, null);
                Map<String, String> paymentIntentObject = session.getPaymentIntentObject().getMetadata();
                reservationService.create(paymentIntentObject);
            } catch (StripeException e) {
                e.printStackTrace();
            }
            System.out.println("予約一覧ページの登録処理が成功しました。");
            System.out.println("Stripe API Version: " + event.getApiVersion());
            System.out.println("stripe-java Version: " + Stripe.VERSION);
        },
        () -> {
            System.out.println("予約一覧ページの登録処理が失敗しました。");
            System.out.println("Stripe API Version: " + event.getApiVersion());
            System.out.println("stripe-java Version: " + Stripe.VERSION);
        });
    }
}