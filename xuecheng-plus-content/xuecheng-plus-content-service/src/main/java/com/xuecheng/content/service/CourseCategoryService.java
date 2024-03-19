package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import com.xuecheng.content.model.po.CourseCategory;

import java.util.List;

public interface CourseCategoryService {
    List<CourseCategoryTreeDto> queryTreeNodes(String id);
}
