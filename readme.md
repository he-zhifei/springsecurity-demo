```
m1-memory-or-redis: 前后端分离springboot整合springsecurity，token存放内存或redis
注意事项：
1.冻结账号后，调用PersistentTokenRepository的removeUserTokens方法清除token
2.权限修改后，需要同步修改Authentication中的权限
```

```
m2-jwt-auth：Jwt认证模块，引入此模块即可实现认证、授权
m2-sub-system：子系统模块，引入认证模块作示例演示
```