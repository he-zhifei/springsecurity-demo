package com.zhifei.dao;

import com.zhifei.entity.SysMenu;
import com.zhifei.entity.SysRole;
import com.zhifei.entity.SysUser;

import java.util.List;

public interface SysUserMapper {
    SysUser getByUsername(String username);

    List<SysRole> getRolesByUserId(Long id);

    List<SysMenu> getMenusByUserId(Long id);

    List<SysRole> getLowerLevelRoles(int level);

    List<SysMenu> getLowerLevelMenus(int level);
}
