package com.zhifei.entity;

import com.zhifei.vo.UserVo;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

/**
 * SpringSecurity UserDetails的实现类
 *
 * @author He Zhifei
 * @date 2021/10/14 16:28
 */
public class SecurityUser implements UserDetails {

    public static final String ROLE_PREFIX = "ROLE_";

    private UserVo userVo;

    private String password;

    public SecurityUser(UserVo userVo, String password) {
        this.userVo = userVo;
        this.password = password;
    }

    public UserVo getUserVo() {
        return userVo;
    }

    /**
     * 获取角色和权限
     * 注意：角色设置时，需要加上ROLE_前缀，在Controller上使用注解时，不需要加
     * @return
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<String> roles = userVo.getRoles();
        Set<String> permissions = userVo.getPermissions();
        List<SimpleGrantedAuthority> authorities = new ArrayList<>(roles.size() + permissions.size());
        Optional.ofNullable(roles).orElse(Collections.emptySet()).stream().forEach(role -> {
            authorities.add(new SimpleGrantedAuthority(ROLE_PREFIX + role));
        });
        Optional.ofNullable(permissions).orElse(Collections.emptySet()).stream().forEach(permission -> {
            authorities.add(new SimpleGrantedAuthority(permission));
        });
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return userVo.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return userVo.getEnabled() == 1;
    }
}
