package com.example.springboot.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.springboot.entity.Vehicle;
import com.example.springboot.utils.Result;

public interface VehicleService extends IService<Vehicle> {
    Result add(Vehicle vehicle);

    Result get(String id);

    Result updateVehicle(Vehicle vehicle);

    Result delete(String id);

    Result getAll();
}
