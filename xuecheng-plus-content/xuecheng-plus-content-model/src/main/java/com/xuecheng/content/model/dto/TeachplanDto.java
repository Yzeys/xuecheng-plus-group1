package com.xuecheng.content.model.dto;

import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.model.po.TeachplanMedia;
import lombok.Data;

import java.util.List;

@Data
public class TeachplanDto extends Teachplan {
    TeachplanMedia teachplanMedia;//一级标题不是没有内容吗？？
    List<TeachplanDto> teachPlanTreeNodes;

}
