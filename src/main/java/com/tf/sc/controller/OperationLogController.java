package com.tf.sc.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tf.sc.annotation.RequireRole;
import com.tf.sc.common.Result;
import com.tf.sc.entity.OperationLog;
import com.tf.sc.service.OperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RequireRole({"2"})
@RestController
@RequestMapping("/operationLog")
public class OperationLogController {

    @Autowired
    private OperationLogService operationLogService;

    @GetMapping("/page")
    public Result<Page<OperationLog>> page(@RequestParam(defaultValue = "1") Long pageNum,
                                           @RequestParam(defaultValue = "10") Long pageSize) {
        return Result.success(operationLogService.page(new Page<>(pageNum, pageSize)));
    }

    @GetMapping("/list")
    public Result<List<OperationLog>> list() {
        return Result.success(operationLogService.list());
    }

    @GetMapping("/{id}")
    public Result<OperationLog> getById(@PathVariable Long id) {
        OperationLog log = operationLogService.getById(id);
        return log == null ? Result.error("日志不存在") : Result.success(log);
    }

    @PostMapping
    public Result<Boolean> save(@RequestBody OperationLog operationLog) {
        return Result.success(operationLogService.save(operationLog));
    }

    @PutMapping
    public Result<Boolean> update(@RequestBody OperationLog operationLog) {
        return Result.success(operationLogService.updateById(operationLog));
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> remove(@PathVariable Long id) {
        return Result.success(operationLogService.removeById(id));
    }
}
