package com.game.core.bomb;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.StringUtils;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.game.core.GameMemory;
import com.game.core.action.processor.ActionAnotationProcessor;
import com.game.core.bomb.auth.AuthIoFilter;
import com.game.core.bomb.dispatcher.BaseAction;
import com.game.core.bomb.dto.ActionNameEnum;
import com.game.core.bomb.dto.BaseActionDataDto;
import com.game.core.bomb.dto.OnlineUserDto;
import com.game.core.bomb.dto.ReturnConstant;
import com.game.core.bomb.dto.ReturnDto;
import com.game.core.bomb.logic.RoomLogic;
import com.game.core.bomb.play.dto.PlayRoomDto;
import com.game.core.exception.BombException;
import com.game.core.utils.CellLocker;
import com.game.utils.GsonUtils;
import com.google.common.collect.Maps;

/**
 * 业务处理点入口，除了登录的action在这个类{@link AuthIoFilter}认证之外，其他
 * 所有的action在这里处理，所有的action的类型可以查看{@link ActionNameEnum}
 * 
 * @author CHQ
 * @since 1.0.0
 * @date 2013-7-28
 */
@Component
public class BombMessageBizHandler implements BombMessageHandler{

	private static final Logger	LOG	= LoggerFactory.getLogger(BombMessageBizHandler.class);

	@Autowired
	CellLocker<List<String>>	locker;

	@Autowired
	ListableBeanFactory			listableBeanFactory;
	
	@Autowired
	RoomLogic roomLogic;

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		// TODO Auto-generated method stub

		//
		// locker.lock("", key);
		OnlineUserDto user = GameMemory.SESSION_USERS.get(session.getId());
		if (user == null) {
			return;
		}
		PlayRoomDto room = GameMemory.getRoomByRoomId(user.getRoomId());
		if (room != null) {
			roomLogic.doUserQuit(room, user.getId());
		}
		GameMemory.ONLINE_USERS.remove(user.getId());
		GameMemory.removeSessionUserByKey(session.getId());
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus paramIdleStatus) throws Exception {
		LOG.info("close session for not any operation for a long time");
		session.close(true);
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable throwable) throws Exception {
		
		if (hasProcessBombExceptionNicely(session, throwable)) {
			return;
		}
		
		if (throwable instanceof InvocationTargetException) {
			Throwable target = ((InvocationTargetException)throwable).getTargetException();
			if (target != null) {
				if (hasProcessBombExceptionNicely(session, target)) {
					return;
				}
			}
		}
		
		
		if (throwable instanceof NotImplementedException) {
			session.write(new ReturnDto(-5, "this function has not implemented"));
			return;
		}


		if (LOG.isDebugEnabled()) {
			LOG.debug("session id:" + session.getId(),  throwable);
		}

		session.write(new ReturnDto(-100, throwable.getMessage()));
		return;
	}

	private boolean hasProcessBombExceptionNicely(IoSession session, Throwable throwable) {
		if (BombException.class.isAssignableFrom(throwable.getClass())) {
			BombException exception = (BombException)throwable;
			session.write(new ReturnDto(exception.getCode(), exception.getAction(), exception.getMessage()));
			return true;
		}
		return false;
	}

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		if (LOG.isDebugEnabled()) {
			LOG.debug("receive message from session:" + session.getId() + ", message:" + message.toString() + ",cnt:" + message.toString().length());
		}
		// 特殊输出，如果是单纯字节的话========================start
		JSONObject json = null;
		String action = null;
		try {
			json = JSONObject.fromObject(message);
			action = json.getString("action");
		} catch (Exception e) {
			LOG.warn("sessionID:" + session.getId()+" parse json exeception, message:" + json, e);
		}

		BaseActionDataDto data = null;
		if (StringUtils.isBlank(action)) {
			// 特殊处理
			throw new BombException(-1, "no action find!");
		}

		// 正常逻辑
//		validateAction(action);
		
		//set action
		GameMemory.LOCAL_SESSION_CONTEXT.get().setAction(action);
		
		// ~ 提供了两种灵活的处理方式：1. 既能处理长连接的方式，2. 也能处理Request-Response的方式(类似http请求)
		if (BaseActionDataDto.getClassByAction(action) != null) {
			data = (BaseActionDataDto) GsonUtils.getFromJson(message.toString(),
					BaseActionDataDto.getClassByAction(action));

			// 特殊输出，如果是单纯字节的话========================end
			// ~ 这里是第一种方式 能够应付长连接的情况
			Map<String, BaseAction> processorMap = listableBeanFactory.getBeansOfType(BaseAction.class);
			if (!MapUtils.isEmpty(processorMap)) {
				Collection<BaseAction> processors = processorMap.values();
				if (!CollectionUtils.isEmpty(processors)) {
					for (BaseAction processor : processors) {
						if (processor.getAction().equals(action)) {
							processor.doAction(session, data);
							return;// one time process one thing
						}
					}
				}
			}

			
		} else {
			// ~ 处理request-response的方式 非常简单 使用actionAnotation实现
			@SuppressWarnings("unchecked")
			HashMap<String, Object> valueMapper = (HashMap<String, Object>) GameMemory.actionMapping.get(action);
			if (valueMapper == null) {
				throw new NotImplementedException();
			}
			
			Map<String, Object> model = Maps.newHashMap();
			if (valueMapper != null) {
				model.put("action", action);
				Method method = (Method) valueMapper.get("method");
				ActionAnotationProcessor processor = (ActionAnotationProcessor) valueMapper.get("object");
				Object returnValue = method.invoke(processor, message, model);
				
				//两个条件同时成立时，才进入执行
				if ((returnValue != null) && !void.class.isAssignableFrom(returnValue.getClass())) {//如果返回不是NULL，
					if (returnValue instanceof String) {
						String value = (String)returnValue;
						if (ReturnConstant.OK.equals(value)) {
							Map<String, Object> okMapping = new HashMap<String, Object>();
							okMapping.put("action", action);
							okMapping.put("code", ReturnConstant.CODE_200);
							session.write(okMapping);
							return;
						}
					} else if (Map.class.isAssignableFrom(returnValue.getClass())) {
						session.write(returnValue);
						return;
//						throw new RuntimeException("return Map.class is not support");
					} else {
						session.write(model);
					}
				} else {
					//nothing to do
						
				}
				return;
			}
		}
		
	}

	private void validateAction(String action) {

		if (ActionNameEnum.validateAction(action)) {
			return;
		}
		throw new RuntimeException("action is not invalidate:" + action);

	}

	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
		if (LOG.isDebugEnabled()) {
			if (message != null)
				LOG.debug("sent:" + message + ", cnt:" + message.toString().length());
		}

	}
	

}
