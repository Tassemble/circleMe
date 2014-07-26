package com.game.bomb.Dao.sql;

import org.springframework.stereotype.Component;

import com.game.base.commons.dao.annotation.DomainMetadata;
import com.game.base.commons.dao.sql.BaseDaoSqlImpl;
import com.game.bomb.Dao.GameAttributeDao;
import com.game.bomb.domain.GameAttribute;


@Component
@DomainMetadata(domainClass=GameAttribute.class)
public class GameAttributeDaoImpl extends BaseDaoSqlImpl<GameAttribute> implements GameAttributeDao{

}
