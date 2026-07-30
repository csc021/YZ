package com.tf.sc.controller;

import com.tf.sc.annotation.RequireRole;
import com.tf.sc.common.Result;
import com.tf.sc.entity.Shelf;
import com.tf.sc.service.ShelfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequireRole({"1", "2"})
@RestController
@RequestMapping("/api/shelf")
public class ShelfController {

    @Autowired
    private ShelfService shelfService;

    /**
     * 创建货架
     */
    @PostMapping("/create")
    public Result<Shelf> create(@RequestBody Shelf shelf) {
        boolean success = shelfService.createShelf(shelf);
        return success ? Result.success(shelf) : Result.error("创建失败，货架编码可能已存在");
    }

    /**
     * 更新货架
     */
    @PutMapping("/update")
    public Result<Boolean> update(@RequestBody Shelf shelf) {
        boolean success = shelfService.updateShelf(shelf);
        return success ? Result.success(true) : Result.error("更新失败，货架编码可能冲突");
    }

    /**
     * 根据ID查询
     */
    @GetMapping("/{id}")
    public Result<Shelf> getById(@PathVariable Long id) {
        Shelf shelf = shelfService.getById(id);
        return shelf == null ? Result.error("货架不存在") : Result.success(shelf);
    }

    /**
     * 查询驿站下所有货架
     */
    @GetMapping("/station/{stationId}")
    public Result<List<Shelf>> getByStation(@PathVariable Long stationId) {
        List<Shelf> list = shelfService.getByStationId(stationId);
        return Result.success(list);
    }

    /**
     * 查询驿站下正常状态的货架
     */
    @GetMapping("/station/{stationId}/normal")
    public Result<List<Shelf>> getNormalByStation(@PathVariable Long stationId) {
        List<Shelf> list = shelfService.getNormalByStationId(stationId);
        return Result.success(list);
    }

    /**
     * 删除货架
     */
    @DeleteMapping("/delete/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        boolean success = shelfService.deleteShelf(id);
        return success ? Result.success(true) : Result.error("删除失败");
    }

    /**
     * 启用货架
     */
    @PostMapping("/enable/{id}")
    public Result<Boolean> enable(@PathVariable Long id) {
        boolean success = shelfService.enableShelf(id);
        return success ? Result.success(true) : Result.error("启用失败");
    }

    /**
     * 停用货架
     */
    @PostMapping("/disable/{id}")
    public Result<Boolean> disable(@PathVariable Long id) {
        boolean success = shelfService.disableShelf(id);
        return success ? Result.success(true) : Result.error("停用失败");
    }
}
