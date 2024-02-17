package com.zhifei.controller;

import com.zhifei.plugin.exception.annotation.GlobalException;
import com.zhifei.plugin.exception.entity.R;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@GlobalException
@RestController
@RequestMapping
public class TestController {

    @GetMapping("/test1")
    public R test1() {
        return R.success().data("test1");
    }

    @PreAuthorize("hasRole('normal')")
    @GetMapping("/test2")
    public R test2() {
        return R.success().data("test2");
    }

    @PreAuthorize("hasRole('normal') && hasAuthority('user:del')")
    @GetMapping("/test3")
    public R test3() {
        return R.success().data("test3");
    }

}
