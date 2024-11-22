package com.example.samuraitravel.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.example.samuraitravel.entity.User;

@Component
/* Publisherクラスはイベントを発行する（発生させる）クラスです。
 * ここまでをまとめると、イベントは以下の流れで実行されます。
 *  1. Publisherクラスがイベントを発行する
 *  2. イベントが発行されたことをEventクラスがListenerクラスに通知する
 *  3. Listenerクラスのメソッドが実行される
 */
public class SignupEventPublisher {
    private final ApplicationEventPublisher applicationEventPublisher;
    
    public SignupEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;                
    }
    
    public void publishSignupEvent(User user, String requestUrl) {
        applicationEventPublisher.publishEvent(new SignupEvent(this, user, requestUrl));
    }
}