package com.game.base.dao.sql;

import org.springframework.stereotype.Repository;

import com.game.base.WPPost;
import com.game.base.WPPostMeta;
import com.game.base.commons.dao.annotation.DomainMetadata;
import com.game.base.commons.dao.sql.BaseDaoSqlImpl;
import com.game.base.dao.WPPostMetaDao;



@DomainMetadata(idColumn="meta_id", domainClass = WPPostMeta.class, tableName = "wp_postmeta", idProperty = "metaId")
@Repository("wpPostMetaDao")
public class WPPostMetaDaoImpl extends BaseDaoSqlImpl<WPPostMeta> implements WPPostMetaDao {

}
