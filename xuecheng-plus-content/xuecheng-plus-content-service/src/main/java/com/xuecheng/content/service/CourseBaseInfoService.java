package com.xuecheng.content.service;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.model.dto.AddCourseDto;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.EditCourseDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import org.springframework.stereotype.Service;


public interface CourseBaseInfoService {
    PageResult<CourseBase> queryCourseBaseList(PageParams pageParams, QueryCourseParamsDto queryCourseParamsDto);

    CourseBaseInfoDto creatCourseBase(Long companyId, AddCourseDto addCourseDto);
/*
* 根据课程id查询课程信息
* */
    public CourseBaseInfoDto getCourseBaseInfo(Long id);

    /**
     * 修改课程
     * @param companyId
     * @param editCourseDto
     * @return
     */
    public CourseBaseInfoDto modifyCourseBase(Long companyId,EditCourseDto editCourseDto);
}
