package com.tf.sc.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tf.sc.annotation.RequireRole;
import com.tf.sc.common.Result;
import com.tf.sc.entity.Carrier;
import com.tf.sc.service.CarrierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/carrier")
public class CarrierController {

    @Autowired
    private CarrierService carrierService;

    @GetMapping("/page")
    public Result<Page<Carrier>> page(@RequestParam(defaultValue = "1") Long pageNum,
                                      @RequestParam(defaultValue = "10") Long pageSize) {
        return Result.success(carrierService.page(new Page<>(pageNum, pageSize)));
    }

    @GetMapping("/list")
    public Result<List<Carrier>> list() {
        return Result.success(carrierService.list());
    }

    @GetMapping("/{id}")
    public Result<Carrier> getById(@PathVariable Long id) {
        Carrier carrier = carrierService.getById(id);
        return carrier == null ? Result.error("快递公司不存在") : Result.success(carrier);
    }

    @RequireRole({"2"})
    @PostMapping
    public Result<Boolean> save(@RequestBody Carrier carrier) {
        return Result.success(carrierService.save(carrier));
    }

    @RequireRole({"2"})
    @PutMapping
    public Result<Boolean> update(@RequestBody Carrier carrier) {
        return Result.success(carrierService.updateById(carrier));
    }

    @RequireRole({"2"})
    @DeleteMapping("/{id}")
    public Result<Boolean> remove(@PathVariable Long id) {
        return Result.success(carrierService.removeById(id));
    }
}
