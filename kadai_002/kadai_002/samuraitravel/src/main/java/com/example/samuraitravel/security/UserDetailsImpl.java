package com.example.samuraitravel.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.samuraitravel.entity.User;

// SpringSecurityが提供するUserDetailsインターフェースを実装する
public class UserDetailsImpl implements UserDetails {
    private final User user;
    private final Collection<GrantedAuthority> authorities;
    
    public UserDetailsImpl(User user, Collection<GrantedAuthority> authorities) {
        this.user = user;
        this.authorities = authorities;
    }
    
    public User getUser() {
        return user;
    }
    
    // ハッシュ化済みのパスワードを返す
    // @Override・・・オーバーライドしていることを明示する
    @Override
    public String getPassword() {
        return user.getPassword();
    }
    
    // ログイン時に利用するユーザー名（メールアドレス）を返す
    @Override
    public String getUsername() {
        return user.getEmail();
    }
    
    // ロールのコレクションを返す
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }
    
    // アカウントが期限切れでなければtrueを返す
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    
    // ユーザーがロックされていなければtrueを返す
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }    
    
    // ユーザーのパスワードが期限切れでなければtrueを返す
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    
    // ユーザーが有効であればtrueを返す
    @Override
    public boolean isEnabled() {
        return user.getEnabled();
    }
    
    /*Spring Securityでは、以下のメソッドがすべてtrueを返したときにのみログインが成功します。
     *【認証用のメソッド】　　　【説明】
     * 　isAccountNonExpired()　	   アカウントが期限切れでなければtrueを返す。
     * 　isAccountNonLocked()	　    ユーザーがロックされていなければtrueを返す。
     * 　isCredentialsNonExpired()	   ユーザーのパスワードが期限切れでなければtrueを返す。
     * 　isEnabled()	                       ユーザーが有効であればtrueを返す。
     *
     *   ※本アプリではアカウントやパスワードの期限、アカウントのロックといった機能は作成しません。
     *   　よって、それらのメソッドは必ずtrueを返すようにしています。
     */
}