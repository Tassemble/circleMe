package com.game.core;

import java.net.SocketAddress;
import java.util.Map;
import java.util.Set;

import org.apache.mina.core.filterchain.IoFilterChain;
import org.apache.mina.core.future.CloseFuture;
import org.apache.mina.core.future.ReadFuture;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.service.IoService;
import org.apache.mina.core.service.TransportMetadata;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.session.IoSessionConfig;
import org.apache.mina.core.write.WriteRequest;
import org.apache.mina.core.write.WriteRequestQueue;

import com.game.utils.GsonUtils;


/**
 * 仅仅是为了实现write to json不出错的问题
 * @author CHQ
 * @date Oct 26, 2013
 * @since 1.0
 */
public class JsonSessionWrapper implements IoSession{

	private IoSession session;
	
	public JsonSessionWrapper(IoSession session) {
		this.session = session;
	}
	
	

	public IoSession getSession() {
		return session;
	}



	public void setSession(IoSession session) {
		this.session = session;
	}



	@Override
	public long getId() {
		return session.getId();
	}

	@Override
	public IoService getService() {
		return session.getService();
	}

	@Override
	public IoHandler getHandler() {
		return session.getHandler();
	}

	@Override
	public IoSessionConfig getConfig() {
		return session.getConfig();
	}

	@Override
	public IoFilterChain getFilterChain() {
		return session.getFilterChain();
	}

	@Override
	public WriteRequestQueue getWriteRequestQueue() {
		return session.getWriteRequestQueue();
	}

	@Override
	public TransportMetadata getTransportMetadata() {
		return session.getTransportMetadata();
	}

	@Override
	public ReadFuture read() {
		return session.read();
	}

	@Override
	public WriteFuture write(Object message) {
		return session.write(GsonUtils.toJson(message));
	}

	@Override
	public WriteFuture write(Object message, SocketAddress destination) {
		return session.write(GsonUtils.toJson(message), destination);
	}

	@Override
	public CloseFuture close(boolean immediately) {
		return session.close(immediately);
	}

	@Override
	public CloseFuture close() {
		return session.close();
	}

	@Override
	public Object getAttachment() {
		return session.getAttachment();
	}

	@Override
	public Object setAttachment(Object attachment) {
		return session.setAttachment(attachment);
	}

	@Override
	public Object getAttribute(Object key) {
		return session.getAttribute(key);
	}

	@Override
	public Object getAttribute(Object key, Object defaultValue) {
		return session.getAttribute(key, defaultValue);
	}

	@Override
	public Object setAttribute(Object key, Object value) {
		return session.setAttribute(key, value);
	}

	@Override
	public Object setAttribute(Object key) {
		return session.setAttribute(key);
	}

	@Override
	public Object setAttributeIfAbsent(Object key, Object value) {
		return session.setAttributeIfAbsent(key, value);
	}

	@Override
	public Object setAttributeIfAbsent(Object key) {
		return session.setAttributeIfAbsent(key);
	}

	@Override
	public Object removeAttribute(Object key) {
		return session.removeAttribute(key);
	}

	@Override
	public boolean removeAttribute(Object key, Object value) {
		return session.removeAttribute(key, value);
	}

	@Override
	public boolean replaceAttribute(Object key, Object oldValue, Object newValue) {
		return session.replaceAttribute(key, oldValue, newValue);
	}

	@Override
	public boolean containsAttribute(Object key) {
		return session.containsAttribute(key);
	}

	@Override
	public Set<Object> getAttributeKeys() {
		return session.getAttributeKeys();
	}

	@Override
	public boolean isConnected() {
		return session.isConnected();
	}

	@Override
	public boolean isClosing() {
		return session.isClosing();
	}

	@Override
	public CloseFuture getCloseFuture() {
		return session.getCloseFuture();
	}

	@Override
	public SocketAddress getRemoteAddress() {
		return session.getRemoteAddress();
	}

	@Override
	public SocketAddress getLocalAddress() {
		return session.getLocalAddress();
	}

	@Override
	public SocketAddress getServiceAddress() {
		return session.getServiceAddress();
	}

	@Override
	public void setCurrentWriteRequest(WriteRequest currentWriteRequest) {
		session.setCurrentWriteRequest(currentWriteRequest);
	}

	@Override
	public void suspendRead() {
		session.suspendRead();
	}

	@Override
	public void suspendWrite() {
		session.suspendWrite();
	}

	@Override
	public void resumeRead() {
		session.resumeRead();
	}

	@Override
	public void resumeWrite() {
		session.resumeWrite();		
	}

	@Override
	public boolean isReadSuspended() {
		return session.isReadSuspended();
	}

	@Override
	public boolean isWriteSuspended() {
		return session.isWriteSuspended();
	}

	@Override
	public void updateThroughput(long currentTime, boolean force) {
		session.updateThroughput(currentTime, force);
	}

	@Override
	public long getReadBytes() {
		return session.getReadBytes();
	}

	@Override
	public long getWrittenBytes() {
		return session.getWrittenBytes();
	}

	@Override
	public long getReadMessages() {
		return session.getReadMessages();
	}

	@Override
	public long getWrittenMessages() {
		return session.getWrittenMessages();
	}

	@Override
	public double getReadBytesThroughput() {
		return session.getReadBytesThroughput();
	}

	@Override
	public double getWrittenBytesThroughput() {
		return session.getWrittenBytesThroughput();
	}

	@Override
	public double getReadMessagesThroughput() {
		return session.getReadBytesThroughput();
	}

	@Override
	public double getWrittenMessagesThroughput() {
		return session.getWrittenBytesThroughput();
	}

	@Override
	public int getScheduledWriteMessages() {
		return session.getScheduledWriteMessages();
	}

	@Override
	public long getScheduledWriteBytes() {
		return session.getScheduledWriteBytes();
	}

	@Override
	public Object getCurrentWriteMessage() {
		return session.getCurrentWriteMessage();
	}

	@Override
	public WriteRequest getCurrentWriteRequest() {
		return session.getCurrentWriteRequest();
	}

	@Override
	public long getCreationTime() {
		return session.getCreationTime();
	}

	@Override
	public long getLastIoTime() {
		return session.getLastIoTime();
	}

	@Override
	public long getLastReadTime() {
		return session.getLastReadTime();
	}

	@Override
	public long getLastWriteTime() {
		return session.getLastWriteTime();
	}

	@Override
	public boolean isIdle(IdleStatus status) {
		return session.isIdle(status);
	}

	@Override
	public boolean isReaderIdle() {
		return session.isReaderIdle();
	}

	@Override
	public boolean isWriterIdle() {
		return session.isWriterIdle();
	}

	@Override
	public boolean isBothIdle() {
		return session.isBothIdle();
	}

	@Override
	public int getIdleCount(IdleStatus status) {
		return session.getIdleCount(status);
	}

	@Override
	public int getReaderIdleCount() {
		return session.getReaderIdleCount();
	}

	@Override
	public int getWriterIdleCount() {
		return session.getWriterIdleCount();
	}

	@Override
	public int getBothIdleCount() {
		return session.getBothIdleCount();
	}

	@Override
	public long getLastIdleTime(IdleStatus status) {
		return session.getLastIdleTime(status);
	}

	@Override
	public long getLastReaderIdleTime() {
		return session.getLastReaderIdleTime();
	}

	@Override
	public long getLastWriterIdleTime() {
		return session.getLastWriterIdleTime();
	}

	@Override
	public long getLastBothIdleTime() {
		return session.getLastBothIdleTime();
	}

}
