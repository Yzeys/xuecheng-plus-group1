package com.xuecheng.content.service.impl;

import com.xuecheng.content.mapper.CourseCategoryMapper;
import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import com.xuecheng.content.model.po.CourseCategory;
import com.xuecheng.content.service.CourseCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CourseCategoryServiceImpl implements CourseCategoryService {
    @Autowired
    CourseCategoryMapper courseCategoryMapper;

    @Override
    public List<CourseCategoryTreeDto> queryTreeNodes(String id) {
        List<CourseCategoryTreeDto> courseCategoryTreeDtos = courseCategoryMapper.selectTreeNodes(id);
        //转换成map
        Map<String, CourseCategoryTreeDto> map = courseCategoryTreeDtos.stream().filter(item -> !id.equals(item.getId())).collect(Collectors.toMap(key -> key.getId(), value -> value,(key1, key2) -> key2));
        //定义list
        List<CourseCategoryTreeDto> courseCategoryList = new ArrayList<>();

        //遍历List<CourseCategoryTreeDto>
        courseCategoryTreeDtos.stream().filter(item -> !id.equals(item.getId())).forEach((item) -> {
            if (item.getParentid().equals(id)) {
                courseCategoryList.add(item);
            }
            CourseCategoryTreeDto courseCategoryParent = map.get(item.getParentid());
            if (courseCategoryParent != null) {
                //如果父节点ChildrenTreeNodes属性为空new出来
                if (courseCategoryParent.getChildrenTreeNodes() == null) {
                    courseCategoryParent.setChildrenTreeNodes(new ArrayList<CourseCategoryTreeDto>());
                }
                courseCategoryParent.getChildrenTreeNodes().add(item);
            }
        });
        return courseCategoryList;
    }
}
