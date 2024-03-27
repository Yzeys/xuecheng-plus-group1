package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.content.mapper.TeachplanMapper;
import com.xuecheng.content.mapper.TeachplanMediaMapper;
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

import java.util.List;

@Service
@Slf4j
public class TeachplanServiceImpl implements TeachplanService {
    @Autowired
    TeachplanMapper teachplanMapper;
    @Autowired
    TeachplanMediaMapper teachplanMediaMapper;
    @Override
    @Transactional
    public boolean deleteTeachPlan(Long teachPlanId) {
        Teachplan teachplan = teachplanMapper.selectById(teachPlanId);
        //该计划是否有子节点
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teachplan::getParentid,teachPlanId);
        List<Teachplan> teachplans = teachplanMapper.selectList(queryWrapper);
        if (teachplans.size()>0){
            //有子节点，一起删除
            int flag = teachplanMapper.deleteById(teachPlanId);
            if (flag==0){
                XueChengPlusException.cast("删除失败");
            }
            teachplans.stream().forEach(item->{
                int sonFlag = teachplanMapper.deleteById(item.getId());
                if (sonFlag==0){
                    XueChengPlusException.cast("删除失败");
                }
                //如果有，删除对应媒体
                LambdaQueryWrapper<TeachplanMedia> teachplanMediaLambdaQueryWrapper = new LambdaQueryWrapper<>();
                teachplanMediaLambdaQueryWrapper.eq(TeachplanMedia::getCourseId,item.getCourseId());
                teachplanMediaLambdaQueryWrapper.eq(TeachplanMedia::getTeachplanId,item.getId());
                List<TeachplanMedia> tpmList=teachplanMediaMapper.selectList(teachplanMediaLambdaQueryWrapper);
                if (tpmList.size()>0){
                    int mediaFlag = teachplanMediaMapper.delete(teachplanMediaLambdaQueryWrapper);
                    if (mediaFlag==0){
                        XueChengPlusException.cast("删除失败");
                    }
                }

            });
        }else {
            //没有子节点,删除该课程计划
            int flag = teachplanMapper.deleteById(teachPlanId);
            if (flag==0){
                XueChengPlusException.cast("删除失败");
            }
            //如果有，删除对应媒体
            LambdaQueryWrapper<TeachplanMedia> teachplanMediaLambdaQueryWrapper = new LambdaQueryWrapper<>();
            teachplanMediaLambdaQueryWrapper.eq(TeachplanMedia::getCourseId,teachplan.getCourseId());
            teachplanMediaLambdaQueryWrapper.eq(TeachplanMedia::getTeachplanId,teachplan.getId());
            List<TeachplanMedia> tpmList=teachplanMediaMapper.selectList(teachplanMediaLambdaQueryWrapper);
            if (tpmList.size()>0){
                int mediaFlag = teachplanMediaMapper.delete(teachplanMediaLambdaQueryWrapper);
                if (mediaFlag==0){
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
        if (id!=null){
            //修改
            Teachplan teachplan = teachplanMapper.selectById(id);
            BeanUtils.copyProperties(saveTeachplanDto,teachplan);
            teachplanMapper.updateById(teachplan);
        }else {
            //新增
            Teachplan teachplan = new Teachplan();
            BeanUtils.copyProperties(saveTeachplanDto,teachplan);
            //设置排序字段
            Long courseId = saveTeachplanDto.getCourseId();
            Long parentid = saveTeachplanDto.getParentid();
            Integer count = getTeachplanCount(courseId, parentid);
            teachplan.setOrderby(count+1);
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
