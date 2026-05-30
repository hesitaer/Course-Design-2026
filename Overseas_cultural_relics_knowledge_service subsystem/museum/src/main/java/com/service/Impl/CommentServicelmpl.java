package com.service.Impl;

import com.entity.Comment;
import com.entity.CommentView;
import com.mapper.CommentMapper;
import com.service.ICommentService;
import com.service.exception.DeleteException;
import com.service.exception.DeleteLoss;
import com.service.exception.InsertException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServicelmpl implements ICommentService {
    @Autowired
    CommentMapper commentMapper;

    @Override
    public Long addComment(Long userId, Integer museumId, String objectId, String content) {
        Comment comment = new Comment();
        comment.setUserId(userId);
        comment.setMuseumId(museumId);
        comment.setObjectId(objectId);
        comment.setContent(content);
        
        Integer rows = commentMapper.insert(comment);
        if (rows != 1) {
            throw new InsertException("评论添加失败");
        }
        return comment.getCommentId();
    }

    @Override
    public void softDeleteComment(Long commentId) {
        Comment comment = commentMapper.findByCid(commentId);
        if (comment == null) {
            throw new DeleteLoss("未找到要删除的评论");
        }
        Integer rows = commentMapper.softDelete(commentId);
        if (rows != 1) {
            throw new DeleteException("删除评论失败");
        }
    }

    @Override
    public void deleteComment(Long commentId) {
        Comment comment = commentMapper.findByCid(commentId);
        if (comment == null) {
            throw new DeleteLoss("未找到要删除的评论");
        }
        Integer rows = commentMapper.delete(commentId);
        if (rows != 1) {
            throw new DeleteException("删除评论失败");
        }
    }

    @Override
    public List<CommentView> getCommentsByArtifact(Integer museumId, String objectId) {
        return commentMapper.findByArtifact(museumId, objectId);
    }

    @Override
    public List<CommentView> getCommentsByUser(Long userId) {
        return commentMapper.findByUserId(userId);
    }

    @Override
    public List<CommentView> getCommentsByUserAndArtifact(Long userId, Integer museumId, String objectId) {
        return commentMapper.findByUserIdAndArtifact(userId, museumId, objectId);
    }
}
