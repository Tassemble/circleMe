package com.game.bomb.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.game.base.commons.dao.BaseDao;
import com.game.base.commons.service.impl.BaseServiceImpl;
import com.game.base.commons.utils.text.JsonUtils;
import com.game.bomb.Dao.TransactionDao;
import com.game.bomb.Dao.WealthBudgetDao;
import com.game.bomb.config.BombConfig;
import com.game.bomb.constant.ProductType;
import com.game.bomb.domain.Transaction;
import com.game.bomb.domain.User;
import com.game.bomb.domain.WealthBudget;
import com.game.bomb.mobile.dto.MobileUserDto;
import com.game.bomb.service.TransactionService;
import com.game.bomb.service.UserService;
import com.game.core.GameMemory;
import com.game.core.bomb.dto.OnlineUserDto;
import com.game.core.exception.ActionFailedException;
import com.game.core.exception.BombException;
import com.game.core.exception.ExceptionConstant;
import com.game.utils.GsonUtils;
import com.game.utils.HttpClientUtils;
import com.game.utils.HttpDataProvider;

@Component
public class TransationServcieImpl extends BaseServiceImpl<BaseDao<Transaction>, Transaction> implements TransactionService{

	
	@Autowired
	BombConfig bombConfig;
	
	TransactionDao transactionDao;
	
	
	@Autowired
	WealthBudgetDao wealthBudgetDao;
	
	@Autowired
	HttpClientUtils httpClientUtils;
	

	
	@Autowired
	UserService userService;
	
	
	private  Logger LOG = LoggerFactory.getLogger("transaction");




	public TransactionDao getTransactionDao() {
		return transactionDao;
	}







	@Autowired
	public void setTransactionDao(TransactionDao transactionDao) {
		this.transactionDao = transactionDao;
		super.setBaseDao(transactionDao);
	}






	@Override
	public Map<String, Object> createAfterVerified(final String data, Map<String, Object> map) {
		MobileUserDto user = null;
		try {
			user = new MobileUserDto(GameMemory.getUser());
			String responseData = HttpClientUtils.getDefaultHtmlByPostMethod(httpClientUtils.getVerifyReceiptDataHttpManager(), new HttpDataProvider() {
				
				@Override
				public String getUrl() {
					return bombConfig.getVerifyReceiptUrl();
				}
				
				@Override
				public HttpEntity getHttpEntity() {
					try {
						Map<String, String> map = new HashMap<String, String>();
						map.put("receipt-data", data);
						LOG.info("send:" + GsonUtils.toJson(map));
						return new StringEntity(GsonUtils.toJson(map), ContentType.APPLICATION_JSON);
					} catch (Exception e) {
						return null;
					}
				}
				
				@Override
				public List<Header> getHeaders() {
					return null;
				}
			});
			
			
			if (StringUtils.isBlank(responseData)) {
				LOG.error("verify failed, apple server response nothing, buyer " + JsonUtils.toJson(GameMemory.getUser()));
			}
			ObjectMapper mapper = new ObjectMapper();
			HashMap<String, Object> dataFromAppleMapping = mapper.readValue(responseData, HashMap.class);
			Integer status = (Integer)dataFromAppleMapping.get("status");
			if (status == null || !status.equals(0)) {
				map.put("code", -1);
				map.put("message", "verify failed, see verify-result for more details");
			} else {
				map.put("code", 200);
				Map<String, Object> receiptMapping = (HashMap<String, Object>)dataFromAppleMapping.get("receipt");
				Date now = new Date();
				
				Transaction query = new Transaction();
				query.setTransactionId((String)receiptMapping.get("transaction_id"));
				List<Transaction> results = this.getByDomainObjectSelective(query);
				if (CollectionUtils.isEmpty(results)) {
					Transaction transaction = new Transaction();
					transaction.setGmtCreate(now);
					transaction.setGmtModified(now);
					transaction.setProductId((String)receiptMapping.get("product_id"));
					transaction.setPurchaseDateMs(Long.valueOf((String)receiptMapping.get("purchase_date_ms")));
					transaction.setQuantity(Integer.valueOf((String)receiptMapping.get("quantity")));
					transaction.setTransactionId((String)receiptMapping.get("transaction_id"));
					transaction.setUid(GameMemory.getUser().getId());
					transaction.setUniqueIdentifier((String)receiptMapping.get("unique_identifier"));
					add(transaction);
					

					
					long quantity = ProductType.getQuantityByProductType(transaction.getProductId());
					WealthBudget wealth = new WealthBudget();
					wealth.setBudgetType(WealthBudget.BUDGET_TYPE_PAY);
					wealth.setQuantity(quantity);
					wealth.setOrderId(transaction.getId());
					wealth.setUid(GameMemory.getUser().getId());
					wealth.setGmtCreate(now);
					wealth.setGmtModified(now);
					wealthBudgetDao.add(wealth);
					
					
					WealthBudget queryWealth = new WealthBudget();
					queryWealth.setUid(GameMemory.getUser().getId());
					List<WealthBudget> allWealth = wealthBudgetDao.getByDomainObjectSelective(queryWealth);
					long sum = 0;
					for (WealthBudget wealthBudget : allWealth) {
						sum += wealthBudget.getQuantity();
					}
					
					User updateUser = new User();
					updateUser.setInGot(sum);
					updateUser.setId(GameMemory.getUser().getId());
					userService.updateSelectiveById(updateUser);
					
				} else {
					if (!results.get(0).getUid().equals(GameMemory.getUser().getId())) {
						LOG.error("user " + GameMemory.getUser().getId() + " may be a hack, for transaction:" + results.get(0).getTransactionId());
						throw new BombException(ExceptionConstant.TRANSACTION_DUP, "transaction exsited:" + results.get(0).getTransactionId());
					}
				}
				if (bombConfig.isDebug()) {
					map.put("message", "verify successful");
				}
			}
			
			if (bombConfig.isDebug()) {
				//map.put("verify-result-for-debug", dataFromAppleMapping);
			}
			return map;
		} catch (Exception e) {
			LOG.error(e.getMessage() + " buyer " + GsonUtils.toJson(user), e);
			throw new ActionFailedException(e.getMessage(), GameMemory.LOCAL_SESSION_CONTEXT.get().getAction());
		}
	}
	
}
