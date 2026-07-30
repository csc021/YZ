package com.tf.sc.controller;

import com.tf.sc.annotation.RequireRole;
import com.tf.sc.common.Result;
import com.tf.sc.entity.ParcelType;
import com.tf.sc.service.ParcelTypeService;
import com.tf.sc.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequireRole({"0", "1", "2"})
@RestController
@RequestMapping("/api/parcel-types")
public class ParcelTypeController {

    @Autowired
    private ParcelTypeService parcelTypeService;

    @GetMapping
    public Result<List<ParcelType>> list() {
        return Result.success(parcelTypeService.list());
    }

    @GetMapping("/{id}")
    public Result<ParcelType> getById(@PathVariable Long id) {
        return Result.success(parcelTypeService.getById(id));
    }

    @RequireRole({"1", "2"})
    @PostMapping
    public Result<ParcelType> create(@RequestBody ParcelType parcelType) {
        parcelType.setCreatedAt(DateUtil.nowStr());
        parcelTypeService.save(parcelType);
        return Result.success(parcelType);
    }

    @RequireRole({"1", "2"})
    @PutMapping
    public Result<Boolean> update(@RequestBody ParcelType parcelType) {
        return Result.success(parcelTypeService.updateById(parcelType));
    }

    @RequireRole({"1", "2"})
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(parcelTypeService.removeById(id));
    }

    /** 根据物品类型推荐默认分区 */
    @GetMapping("/{id}/recommend-zone")
    public Result<Long> recommendZone(@PathVariable Long id) {
        ParcelType pt = parcelTypeService.getById(id);
        return pt != null && pt.getDefaultZoneId() != null
                ? Result.success(pt.getDefaultZoneId())
                : Result.success(null);
    }
}