package com.example.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.springboot.entity.Vehicle;
import com.example.springboot.entity.WarnSignal;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;


@Mapper
@Repository
public interface WarnSignalMapper extends BaseMapper<WarnSignal> {

}
