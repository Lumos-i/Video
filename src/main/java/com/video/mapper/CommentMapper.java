package com.video.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.video.dto.CommentDto;
import com.video.dto.CommentReplyDto;
import com.video.pojo.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
    Page<CommentReplyDto> getCommentReplyPag(@Param("page") Page<CommentReplyDto> page, @Param("commentId")Integer commentId);
}
