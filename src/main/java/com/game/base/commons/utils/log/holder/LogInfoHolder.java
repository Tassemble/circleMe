package com.game.base.commons.utils.log.holder;

public class LogInfoHolder {

    private static ThreadLocal<LogInfo> holder = new ThreadLocal<LogInfo>();

    public static LogInfo getLogInfo() {
        return holder.get();
    }

    public static void setLogInfo(LogInfo logInfo) {
        holder.set(logInfo);
    }
}
