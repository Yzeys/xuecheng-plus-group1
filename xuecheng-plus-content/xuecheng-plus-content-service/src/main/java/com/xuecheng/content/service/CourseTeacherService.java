package com.xuecheng.content.service;

import com.xuecheng.content.model.po.CourseTeacher;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface CourseTeacherService {
    List<CourseTeacher> getTeacherList(Long courseId);
    CourseTeacher saveTeacher(Long companyId,CourseTeacher courseTeacher);
    void deleteTeacher(Long companyId,Long courseId,Long teacherId);
}
