package com.zhifei.vo;

import com.zhifei.entity.SysMenu;
import com.zhifei.entity.SysRole;

import java.util.Set;

/**
 * 包装用户实际的所有角色与菜单权限
 *
 * @author He Zhifei
 * @date 2023/10/10 20:46
 */
public class RolesAndMenus {

    /**
     * 角色
     */
    private Set<SysRole> roleSet;

    /**
     * 菜单权限
     */
    private Set<SysMenu> menuSet;

    public Set<SysRole> getRoleSet() {
        return roleSet;
    }

    public void setRoleSet(Set<SysRole> roleSet) {
        this.roleSet = roleSet;
    }

    public Set<SysMenu> getMenuSet() {
        return menuSet;
    }

    public void setMenuSet(Set<SysMenu> menuSet) {
        this.menuSet = menuSet;
    }
}
