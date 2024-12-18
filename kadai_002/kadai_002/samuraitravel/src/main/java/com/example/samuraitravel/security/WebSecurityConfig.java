package com.example.samuraitravel.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/* 
 * クラスの役割（Spring Securityの設定を行うためのクラス）
 * 　・誰に、どのページへのアクセスを許可するか
 * 　・ログインページのURL
 * 　・ログインフォームの送信先URL
 * 　・ログイン成功時または失敗時のリダイレクト先URL
 * 　・ログアウト時のリダイレクト先URL
 */

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {

    @Bean
    // Beanアノ・・・メソッドの戻り値（インスタンス）がDIコンテナに登録されるようになる。
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests((requests) -> requests                
                .requestMatchers("/css/**", 
                		                     "/images/**", 
                		                     "/js/**", 
                		                     "/storage/**",
                		                     "/", 
                		                     "/signup/**", 
                		                     "/houses",
                		                     "/houses/{id}",
                		                     "/stripe/webhook",
                		                     "/review",
                		                     "/review/{id}",
                		                     "/review/list/{id}",
                		                     "/favorite/").permitAll()  // すべてのユーザーにアクセスを許可するURL
                .requestMatchers("/admin/**").hasRole("ADMIN")  // 管理者にのみアクセスを許可するURL
                .anyRequest().authenticated()                   // 上記以外のURLはログインが必要（会員または管理者のどちらでもOK）
            )
            .formLogin((form) -> form
                .loginPage("/login")              // ログインページのURL
                .loginProcessingUrl("/login")     // ログインフォームの送信先URL
                .defaultSuccessUrl("/?loggedIn")  // ログイン成功時のリダイレクト先URL
                .failureUrl("/login?error")       // ログイン失敗時のリダイレクト先URL
                .permitAll()
            )
            .logout((logout) -> logout
                .logoutSuccessUrl("/?loggedOut")  // ログアウト時のリダイレクト先URL
                .permitAll()
            )
            .csrf((csrf) -> csrf
                    .ignoringRequestMatchers("/stripe/webhook"));
            
        return http.build();
    }
    
    /*
     * passwordEncoder()メソッドでは、BCryptPasswordEncoderクラスのインスタンスを返すことで、
     * パスワードのハッシュアルゴリズム（ハッシュ化のルール）を「BCrypt」に設定しています。
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
}
