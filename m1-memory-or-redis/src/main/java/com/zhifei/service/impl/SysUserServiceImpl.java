package com.zhifei.service.impl;

import com.zhifei.dao.SysUserMapper;
import com.zhifei.entity.SecurityUser;
import com.zhifei.entity.SysMenu;
import com.zhifei.entity.SysRole;
import com.zhifei.entity.SysUser;
import com.zhifei.plugin.exception.enums.RCode;
import com.zhifei.security.exception.AuthException;
import com.zhifei.service.SysUserService;
import com.zhifei.vo.RolesAndMenus;
import com.zhifei.vo.UserVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SysUserServiceImpl implements SysUserService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    public SysUser getByUsername(String username) {
        return sysUserMapper.getByUsername(username);
    }

    @Override
    public List<SysRole> getRolesByUserId(Long id) {
        return sysUserMapper.getRolesByUserId(id);
    }

    @Override
    public List<SysMenu> getMenusByUserId(Long id) {
        return sysUserMapper.getMenusByUserId(id);
    }

    @Override
    public List<SysRole>  getLowerLevelRoles(int level) {
        return sysUserMapper.getLowerLevelRoles(level);
    }

    @Override
    public List<SysMenu> getLowerLevelMenus(int level) {
        return sysUserMapper.getLowerLevelMenus(level);
    }

    @Override
    public RolesAndMenus getRealRolesAndMenus(Long userId) {
        // 先查找属于当前用户的角色和权限
        List<SysRole> roles = getRolesByUserId(userId);
        List<SysMenu> menus = getMenusByUserId(userId);

        // 记录当前用户所有角色中的最高级别
        int topLevel = 1;
        if (roles != null && roles.size() > 0) {
            for (int i = 0, size = roles.size(); i < size; i++) {
                topLevel = Math.max(topLevel, roles.get(i).getLevel());
            }
        }

        // 再查找比当前用户所有角色最高级别低的角色和权限（高级别的角色包含自身的权限和低级别的所有角色权限）
        List<SysRole> lowerLevelRoles = getLowerLevelRoles(topLevel);
        List<SysMenu> lowerLevelMenus = getLowerLevelMenus(topLevel);

        // 已经重写了SysRole、SysMenu的equals和hashCode方法，添加到Set中自动去重
        Set<SysRole> roleSet = new HashSet<SysRole>();
        Set<SysMenu> menuSet = new HashSet<SysMenu>();
        roleSet.addAll(roles);
        roleSet.addAll(lowerLevelRoles);
        menuSet.addAll(menus);
        menuSet.addAll(lowerLevelMenus);

        RolesAndMenus rolesAndMenus = new RolesAndMenus();
        rolesAndMenus.setRoleSet(roleSet);
        rolesAndMenus.setMenuSet(menuSet);
        return rolesAndMenus;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser sysUser = getByUsername(username);
        checkSysUser(sysUser);

        UserVo userVo = new UserVo();
        BeanUtils.copyProperties(sysUser, userVo);

        // 设置token、角色、菜单路由、菜单权限
        userVo.setToken(null);
        RolesAndMenus rolesAndMenus = getRealRolesAndMenus(sysUser.getId());
        Set<String> roles = new HashSet<String>();
        Optional.ofNullable(rolesAndMenus.getRoleSet()).orElse(Collections.emptySet()).forEach(role -> {
            roles.add(role.getName());
        });
        userVo.setRoles(roles);
        Set<String> routers = new HashSet<String>();
        Set<String> permissions = new HashSet<String>();
        Optional.ofNullable(rolesAndMenus.getMenuSet()).orElse(Collections.emptySet()).forEach(menu -> {
            String menuName = menu.getName();
            if (StringUtils.isNotBlank(menuName)) {
                routers.add(menuName);
            }
            permissions.add(menu.getPermission());
        });
        userVo.setRouters(routers);
        userVo.setPermissions(permissions);

        SecurityUser securityUser = new SecurityUser(userVo, sysUser.getPassword());
        return securityUser;
    }

    /**
     * 检查用户是否存在且可用
     * @param user
     */
    private void checkSysUser(SysUser user) {
        if (user == null) {
            throw new AuthException(RCode.INCORRECT_PRINCIPAL);
        }
        if (user.getEnabled() == 0) {
            throw new AuthException(RCode.ACCOUNT_LOCKED);
        }
    }
}
