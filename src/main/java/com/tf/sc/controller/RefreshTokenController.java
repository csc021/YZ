package com.tf.sc.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tf.sc.annotation.RequireRole;
import com.tf.sc.common.Result;
import com.tf.sc.entity.RefreshToken;
import com.tf.sc.service.RefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RequireRole({"2"})
@RestController
@RequestMapping("/refreshToken")
public class RefreshTokenController {

    @Autowired
    private RefreshTokenService refreshTokenService;

    @GetMapping("/page")
    public Result<Page<RefreshToken>> page(@RequestParam(defaultValue = "1") Long pageNum,
                                           @RequestParam(defaultValue = "10") Long pageSize) {
        return Result.success(refreshTokenService.page(new Page<>(pageNum, pageSize)));
    }

    @GetMapping("/list")
    public Result<List<RefreshToken>> list() {
        return Result.success(refreshTokenService.list());
    }

    @GetMapping("/{id}")
    public Result<RefreshToken> getById(@PathVariable Long id) {
        RefreshToken token = refreshTokenService.getById(id);
        return token == null ? Result.error("Token不存在") : Result.success(token);
    }

    @PostMapping
    public Result<Boolean> save(@RequestBody RefreshToken refreshToken) {
        return Result.success(refreshTokenService.save(refreshToken));
    }

    @PutMapping
    public Result<Boolean> update(@RequestBody RefreshToken refreshToken) {
        return Result.success(refreshTokenService.updateById(refreshToken));
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> remove(@PathVariable Long id) {
        return Result.success(refreshTokenService.removeById(id));
    }
}
