package com.zhifei.service;

import com.zhifei.entity.SysMenu;
import com.zhifei.entity.SysRole;
import com.zhifei.entity.SysUser;
import com.zhifei.vo.RolesAndMenus;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface SysUserService extends UserDetailsService {

    /**
     * 用户名获取用户
     * @param username
     * @return
     */
    SysUser getByUsername(String username);

    /**
     * 获取用户的角色
     * @param id
     * @return
     */
    List<SysRole> getRolesByUserId(Long id);

    /**
     * 获取用户的菜单权限
     * @param id
     * @return
     */
    List<SysMenu> getMenusByUserId(Long id);

    /**
     * 获取比level低级别的角色
     * @param level
     * @return
     */
    List<SysRole>  getLowerLevelRoles(int level);

    /**
     * 获取比level低级别的菜单
     * @param level
     * @return
     */
    List<SysMenu> getLowerLevelMenus(int level);

    /**
     * 获取用户实际上的角色和菜单权限
     * @param userId
     * @return
     */
    RolesAndMenus getRealRolesAndMenus(Long userId);
}
