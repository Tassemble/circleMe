package com.game.bomb.thirdaccount.service;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.game.bomb.domain.User;
import com.game.core.exception.BombException;
import com.game.utils.HttpClientUtils;


@Service("sinaWeiboService")
public class SinaWeiboServiceImpl implements SinaWeiboService {

	@Autowired
	HttpClientUtils	httpClientUtils;
	private static final Logger LOG = LoggerFactory.getLogger(SinaWeiboServiceImpl.class);

	@Override
	public User validateAndGetWeiboUser(String accessToken, String uid) {
		try {
			StringBuilder sb = new StringBuilder("https://api.weibo.com/2/users/show.json?").append("uid=").append(uid)
					.append("&access_token=").append(accessToken);
			String result = HttpClientUtils.getHtmlByGetMethod(httpClientUtils.getCommonHttpManager(), sb.toString());
			
			
			if (StringUtils.isBlank(result)) {
				throw new BombException(-201, "token may be invalidate.");
			}
			if (LOG.isDebugEnabled()) {
				LOG.info("request url:" + sb.toString() + ",get from weibo:" + result);
			}
			
			User user = new User();

			user.setNickName(net.sf.json.JSONObject.fromObject(result).getString("screen_name"));
			return user;
		} catch (Exception e) {
			throw new BombException(-202, "token:" + accessToken + ", uid:" + uid + ",error:" + e.getMessage());
		}
	}

}
