package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.mapper.CourseTeacherMapper;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.model.po.CourseTeacher;
import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.service.CourseTeacherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class CourseTeacherServiceImpl implements CourseTeacherService {

    @Autowired
    CourseTeacherMapper courseTeacherMapper;
    @Autowired
    CourseBaseMapper courseBaseMapper;
    @Override
    public List<CourseTeacher> getTeacherList(Long courseId) {
        LambdaQueryWrapper<CourseTeacher> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseTeacher::getCourseId,courseId);
        return courseTeacherMapper.selectList(queryWrapper);
    }

    @Override
    public CourseTeacher saveTeacher(Long companyId,CourseTeacher courseTeacher) {
        CourseTeacher oldCourseTeacher = courseTeacherMapper.selectById(courseTeacher.getId());
        CourseBase courseBase = courseBaseMapper.selectById(courseTeacher.getCourseId());
        if (!courseBase.getCompanyId().equals(companyId)){
            XueChengPlusException.cast("本机构只能修改本机构老师fk");
        }
        if (oldCourseTeacher==null){
            //没有，新增
            courseTeacher.setCreateDate(LocalDateTime.now());
            int insert = courseTeacherMapper.insert(courseTeacher);
            if (!(insert>0)){
                XueChengPlusException.cast("新增师资失败fk");
            }
            return courseTeacherMapper.selectById(courseTeacher.getId());
        }else {
            //有，修改
//            BeanUtils.copyProperties(courseTeacher,oldCourseTeacher);
            int insert = courseTeacherMapper.updateById(courseTeacher);
            if (!(insert>0)){
                XueChengPlusException.cast("修改师资失败fk");
            }
            return courseTeacherMapper.selectById(courseTeacher.getId());
        }
    }

    @Override
    public void deleteTeacher(Long companyId,Long courseId, Long teacherId) {
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if (!courseBase.getCompanyId().equals(companyId)){
            XueChengPlusException.cast("本机构只能删除本机构老师fk");
        }
        LambdaQueryWrapper<CourseTeacher> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseTeacher::getCourseId,courseId).eq(CourseTeacher::getId,teacherId);
        int delete = courseTeacherMapper.delete(queryWrapper);
        if (delete!=1){
            XueChengPlusException.cast("删除失败fk");
        }
    }
}
