package com.lzcge.crowd.mapper;

import com.lzcge.crowd.pojo.po.ProjectItemPicPO;
import com.lzcge.crowd.pojo.po.ProjectItemPicPOExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProjectItemPicPOMapper {
    int countByExample(ProjectItemPicPOExample example);

    int deleteByExample(ProjectItemPicPOExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(ProjectItemPicPO record);

    int insertSelective(ProjectItemPicPO record);

    List<ProjectItemPicPO> selectByExample(ProjectItemPicPOExample example);

    ProjectItemPicPO selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") ProjectItemPicPO record, @Param("example") ProjectItemPicPOExample example);

    int updateByExample(@Param("record") ProjectItemPicPO record, @Param("example") ProjectItemPicPOExample example);

    int updateByPrimaryKeySelective(ProjectItemPicPO record);

    int updateByPrimaryKey(ProjectItemPicPO record);

	void insertBatch(@Param("projectPOId") Integer projectPOId, @Param("detailPicturePathList") List<String> detailPicturePathList);
}