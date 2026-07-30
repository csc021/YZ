package com.tf.sc.controller;

import com.tf.sc.annotation.RequireRole;
import com.tf.sc.common.Result;
import com.tf.sc.entity.Station;
import com.tf.sc.service.StationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequireRole({ "1","2"})
@RestController
@RequestMapping("/api/station")
public class StationController {

    @Autowired
    private StationService stationService;

    @RequireRole({"2"})
    @PostMapping("/create")
    public Result<Station> create(@RequestBody Station station) {
        boolean success = stationService.createStation(station);
        return success ? Result.success(station) : Result.error("Create station failed");
    }

    @RequireRole({"2"})
    @PutMapping("/update")
    public Result<Boolean> update(@RequestBody Station station) {
        boolean success = stationService.updateStation(station);
        return success ? Result.success(true) : Result.error("Update station failed");
    }

    @RequireRole({"2"})
    @PutMapping("/brand")
    public Result<Boolean> updateBrand(@RequestParam Long stationId, @RequestParam String brand) {
        boolean success = stationService.updateBrand(stationId, brand);
        return success ? Result.success(true) : Result.error("Update brand failed");
    }

    @RequireRole({"0", "1", "2"})
    @GetMapping("/{id}")
    public Result<Station> getById(@PathVariable Long id) {
        Station station = stationService.getById(id);
        return station == null ? Result.error("Station not found") : Result.success(station);
    }

    @RequireRole({"0", "1", "2"})
    @GetMapping("/list")
    public Result<List<Station>> list() {
        return Result.success(stationService.getAllStations());
    }

    @RequireRole({"0", "1", "2"})
    @GetMapping("/manager/{managerId}")
    public Result<Station> getByManagerId(@PathVariable Long managerId) {
        Station station = stationService.getByManagerId(managerId);
        return station == null ? Result.error("Station not found") : Result.success(station);
    }

    @RequireRole({"0", "1", "2"})
    @GetMapping("/status/{status}")
    public Result<List<Station>> getByStatus(@PathVariable Integer status) {
        return Result.success(stationService.getByStatus(status));
    }

    @RequireRole({"2"})
    @DeleteMapping("/delete/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        boolean success = stationService.deleteStation(id);
        return success ? Result.success(true) : Result.error("Delete station failed");
    }

    @RequireRole({"2"})
    @PostMapping("/enable/{id}")
    public Result<Boolean> enable(@PathVariable Long id) {
        boolean success = stationService.enableStation(id);
        return success ? Result.success(true) : Result.error("Enable station failed");
    }

    @RequireRole({"2"})
    @PostMapping("/disable/{id}")
    public Result<Boolean> disable(@PathVariable Long id) {
        boolean success = stationService.disableStation(id);
        return success ? Result.success(true) : Result.error("Disable station failed");
    }
}
