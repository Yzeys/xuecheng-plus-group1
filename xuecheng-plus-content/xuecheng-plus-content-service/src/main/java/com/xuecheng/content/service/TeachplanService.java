package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

public interface TeachplanService {
    List<TeachplanDto> selectTreeNodes(Long courseId);

    void saveTeachplan(SaveTeachplanDto teachplanDto);

    boolean deleteTeachPlan(Long teachPlanId);

    void moveDownTeachplan(@PathVariable Long id);

    void moveUpTeachplan(@PathVariable Long id);
}