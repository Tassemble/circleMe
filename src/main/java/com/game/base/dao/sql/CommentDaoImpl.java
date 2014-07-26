package com.game.base.dao.sql;

import org.springframework.stereotype.Repository;

import com.game.base.Comment;
import com.game.base.commons.dao.annotation.DomainMetadata;
import com.game.base.commons.dao.sql.BaseDaoSqlImpl;
import com.game.base.dao.CommentDao;


@DomainMetadata(domainClass = Comment.class, tableName = "wp_comments", idColumn="comment_ID", idProperty="commentID")
@Repository("commentDao")
public class CommentDaoImpl extends BaseDaoSqlImpl<Comment> implements CommentDao{

}
