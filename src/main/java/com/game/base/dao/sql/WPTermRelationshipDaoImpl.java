package com.game.base.dao.sql;

import org.springframework.stereotype.Repository;

import com.game.base.WPPost;
import com.game.base.WPTermRelationship;
import com.game.base.commons.dao.annotation.DomainMetadata;
import com.game.base.commons.dao.sql.BaseDaoSqlImpl;
import com.game.base.dao.WPTermRelationshipDao;



@DomainMetadata(domainClass = WPPost.class, tableName = "wp_term_relationships")
@Repository("wpTermRelationshipDao")
public class WPTermRelationshipDaoImpl extends BaseDaoSqlImpl<WPTermRelationship> implements WPTermRelationshipDao {

}
