package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.content.mapper.TeachplanMapper;
import com.xuecheng.content.mapper.TeachplanMediaMapper;
import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.model.po.TeachplanMedia;
import com.xuecheng.content.service.TeachplanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Slf4j
public class TeachplanServiceImpl implements TeachplanService {
    @Autowired
    TeachplanMapper teachplanMapper;
    @Autowired
    TeachplanMediaMapper teachplanMediaMapper;

    @Transactional
    @Override
    public void moveDownTeachplan(Long id) {
        //拿到课程计划对象
        Teachplan teachplan = teachplanMapper.selectById(id);
        List<Teachplan> teachplans = getLevelList(id);
        teachplans.stream().forEach(item -> {
                if (item.getId().equals(id)) {
                    int flag = teachplans.indexOf(item);
                    //判断是不是最低优先级
                    if (flag > 0) {
                        //不是，向后移
                        Teachplan nextTeachplan = teachplans.get(flag - 1);
                        int orderby = teachplan.getOrderby();
                        teachplan.setOrderby(nextTeachplan.getOrderby());
                        nextTeachplan.setOrderby(orderby);
                        teachplanMapper.updateById(teachplan);
                        teachplanMapper.updateById(nextTeachplan);
                    }
                    //是，不改动
                }
            });

    }

    @Override
    public void moveUpTeachplan(Long id) {
        //拿到课程计划对象
        Teachplan teachplan = teachplanMapper.selectById(id);
        List<Teachplan> teachplans = getLevelList(id);
        teachplans.stream().forEach(item -> {
            if (item.getId().equals(id)) {
                int flag = teachplans.indexOf(item);
                //判断是不是最高优先级
                if (flag < teachplans.size()-1) {
                    //不是，向前移
                    Teachplan nextTeachplan = teachplans.get(flag +1);
                    int orderby = teachplan.getOrderby();
                    teachplan.setOrderby(nextTeachplan.getOrderby());
                    nextTeachplan.setOrderby(orderby);
                    teachplanMapper.updateById(teachplan);
                    teachplanMapper.updateById(nextTeachplan);
                }
                //是，不改动
            }
        });
    }

    private List<Teachplan> getLevelList(Long id){
        //拿到课程计划对象
        Teachplan teachplan = teachplanMapper.selectById(id);
        //获取这一级所有对象
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teachplan::getCourseId, teachplan.getCourseId());
        queryWrapper.eq(Teachplan::getParentid, teachplan.getParentid());
        queryWrapper.le(Teachplan::getOrderby, teachplan.getOrderby());
        List<Teachplan> teachplans = teachplanMapper.selectList(queryWrapper);
        Collections.sort(teachplans);
        return teachplans;
    }
    @Override
    @Transactional
    public boolean deleteTeachPlan(Long teachPlanId) {
        Teachplan teachplan = teachplanMapper.selectById(teachPlanId);
        //该计划是否有子节点
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teachplan::getParentid, teachPlanId);
        List<Teachplan> teachplans = teachplanMapper.selectList(queryWrapper);
        if (teachplans.size() > 0) {
            //有子节点，一起删除
            int flag = teachplanMapper.deleteById(teachPlanId);
            if (flag == 0) {
                XueChengPlusException.cast("删除失败");
            }
            teachplans.stream().forEach(item -> {
                int sonFlag = teachplanMapper.deleteById(item.getId());
                if (sonFlag == 0) {
                    XueChengPlusException.cast("删除失败");
                }
                //如果有，删除对应媒体
                LambdaQueryWrapper<TeachplanMedia> teachplanMediaLambdaQueryWrapper = new LambdaQueryWrapper<>();
                teachplanMediaLambdaQueryWrapper.eq(TeachplanMedia::getCourseId, item.getCourseId());
                teachplanMediaLambdaQueryWrapper.eq(TeachplanMedia::getTeachplanId, item.getId());
                List<TeachplanMedia> tpmList = teachplanMediaMapper.selectList(teachplanMediaLambdaQueryWrapper);
                if (tpmList.size() > 0) {
                    int mediaFlag = teachplanMediaMapper.delete(teachplanMediaLambdaQueryWrapper);
                    if (mediaFlag == 0) {
                        XueChengPlusException.cast("删除失败");
                    }
                }

            });
        } else {
            //没有子节点,删除该课程计划
            int flag = teachplanMapper.deleteById(teachPlanId);
            if (flag == 0) {
                XueChengPlusException.cast("删除失败");
            }
            //如果有，删除对应媒体
            LambdaQueryWrapper<TeachplanMedia> teachplanMediaLambdaQueryWrapper = new LambdaQueryWrapper<>();
            teachplanMediaLambdaQueryWrapper.eq(TeachplanMedia::getCourseId, teachplan.getCourseId());
            teachplanMediaLambdaQueryWrapper.eq(TeachplanMedia::getTeachplanId, teachplan.getId());
            List<TeachplanMedia> tpmList = teachplanMediaMapper.selectList(teachplanMediaLambdaQueryWrapper);
            if (tpmList.size() > 0) {
                int mediaFlag = teachplanMediaMapper.delete(teachplanMediaLambdaQueryWrapper);
                if (mediaFlag == 0) {
                    XueChengPlusException.cast("删除失败");
                }
            }
        }
        return true;
    }

    @Override
    public List<TeachplanDto> selectTreeNodes(Long courseId) {
        return teachplanMapper.selectTreeNodes(courseId);
    }

    @Override
    public void saveTeachplan(SaveTeachplanDto saveTeachplanDto) {
        //课程计划id
        Long id = saveTeachplanDto.getId();
        if (id != null) {
            //修改
            Teachplan teachplan = teachplanMapper.selectById(id);
            BeanUtils.copyProperties(saveTeachplanDto, teachplan);
            teachplanMapper.updateById(teachplan);
        } else {
            //新增
            Teachplan teachplan = new Teachplan();
            BeanUtils.copyProperties(saveTeachplanDto, teachplan);
            //设置排序字段
            Long courseId = saveTeachplanDto.getCourseId();
            Long parentid = saveTeachplanDto.getParentid();
            Integer count = getTeachplanCount(courseId, parentid);
            teachplan.setOrderby(count + 1);
            teachplanMapper.insert(teachplan);
        }
    }

    private Integer getTeachplanCount(Long courseId, Long parentid) {
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teachplan::getCourseId, courseId).eq(Teachplan::getParentid, parentid);
        Integer count = teachplanMapper.selectCount(queryWrapper);
        return count;
    }
}
