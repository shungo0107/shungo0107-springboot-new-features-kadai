package com.example.samuraitravel.security;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.samuraitravel.entity.User;
import com.example.samuraitravel.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;    
    
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;        
    }
    
    /* SpringSecurityが提供するUserDetailsServiceインターフェースで定義されている
     * 抽象メソッドはloadUserByUsername()のみです。よってUserDetailsServiceImplクラスでは、
     * このloadUserByUsername()を上書きし、具体的な処理内容を作成するだけでOKです。
     * 
     * クラスの役割は、UserDetailsImplクラスのインスタンスを生成することです。そこで、
     * loadUserByUsername()メソッド内では以下の処理をおこなっています。
     * 　1. フォームから送信されたメールアドレスに一致するユーザーを取得する
     * 　2. そのユーザーのロールを取得する
     * 　3. 上記2つの情報をUserDetailsImplクラスのコンストラクタに渡し、インスタンスを生成する
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {  
        try {
            User user = userRepository.findByEmail(email);
            String userRoleName = user.getRole().getName();
            Collection<GrantedAuthority> authorities = new ArrayList<>();         
            authorities.add(new SimpleGrantedAuthority(userRoleName));
            return new UserDetailsImpl(user, authorities);
        } catch (Exception e) {
            throw new UsernameNotFoundException("ユーザーが見つかりませんでした。");
        }
    }   
}

