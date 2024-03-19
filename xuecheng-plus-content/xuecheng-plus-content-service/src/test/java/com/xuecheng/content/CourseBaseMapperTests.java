package com.xuecheng.content;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class CourseBaseMapperTests {
    @Autowired
    CourseBaseMapper courseBaseMapper;
    @Test
    public void testCourseBaseMapper(){
        /*CourseBase courseBase = courseBaseMapper.selectById(40);
        System.out.printf(String.valueOf(courseBase));*/

        PageParams pageParams = new PageParams(1, 2);
        Page<CourseBase> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
        //查询条件
        QueryCourseParamsDto queryCourseParamsDto=new QueryCourseParamsDto();
        queryCourseParamsDto.setCourseName("java");
        LambdaQueryWrapper<CourseBase> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(queryCourseParamsDto.getCourseName()),CourseBase::getName,queryCourseParamsDto.getCourseName());
        queryWrapper.eq(StringUtils.isNotBlank(queryCourseParamsDto.getAuditStatus()),CourseBase::getAuditStatus,queryCourseParamsDto.getAuditStatus());
        queryWrapper.eq(StringUtils.isNotBlank(queryCourseParamsDto.getPublishStatus()),CourseBase::getStatus,queryCourseParamsDto.getPublishStatus());

        Page<CourseBase> pageResult=courseBaseMapper.selectPage(page,queryWrapper);
        long total=pageResult.getTotal();
        List<CourseBase> items=pageResult.getRecords();
        PageResult<CourseBase> courseBasePageResult=new PageResult<>(items,total,pageParams.getPageNo(),pageParams.getPageSize());
        System.out.println(courseBasePageResult);

    }

}
