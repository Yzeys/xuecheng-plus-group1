package com.xuecheng.content.api;

import com.xuecheng.content.mapper.CourseTeacherMapper;
import com.xuecheng.content.model.po.CourseTeacher;
import com.xuecheng.content.service.CourseTeacherService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Api(value = "师资编辑接口",tags = "师资编辑接口")
@RestController
public class CourseTeacherController {
    @Autowired
    CourseTeacherService courseTeacherService;
    @ApiOperation(value = "获取师资列表")
    @GetMapping("/courseTeacher/list/{courseId}")
    public List<CourseTeacher> getTeacherList(@PathVariable Long courseId){
        return courseTeacherService.getTeacherList(courseId);
    }

    @ApiOperation(value = "添加或修改老师")
    @PostMapping("/courseTeacher")
    public CourseTeacher saveTeacher(@RequestBody CourseTeacher courseTeacher){
        Long companyId=1232141425L;
        return courseTeacherService.saveTeacher(companyId,courseTeacher);
    }

    @ApiOperation(value = "删除老师")
    @DeleteMapping("/courseTeacher/course/{courseId}/{teacherId}")
    public void deleteTeacher(@PathVariable Long courseId,@PathVariable Long teacherId){
        Long companyId=1232141425L;
        courseTeacherService.deleteTeacher(companyId,courseId,teacherId);
    }
}
