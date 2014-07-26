package com.game.bomb.service;

import java.util.Map;

import com.game.base.commons.service.BaseService;
import com.game.bomb.domain.Transaction;

public interface TransactionService extends BaseService<Transaction>{

	Map<String, Object> createAfterVerified(String data, Map<String, Object> map);

}
