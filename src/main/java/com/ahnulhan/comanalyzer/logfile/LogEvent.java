/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ahnulhan.comanalyzer.logfile;

import java.util.Date;

/**
 *
 * @author uli
 */
public class LogEvent {

    private String Ident;
    private String msgId;
    private long msgSendTime;
    private long msgReceivedTime;
    private String command;
    private int timeout;

    public long getMsgSendTime() {
        return msgSendTime;
    }

    public void setMsgSendTime(long msgSendTime) {
        this.msgSendTime = msgSendTime;
    }

    public long getMsgReceivedTime() {
        return msgReceivedTime;
    }

    public void setMsgReceivedTime(long msgReceivedTime) {
        this.msgReceivedTime = msgReceivedTime;
    }

    public String getIdent() {
        return Ident;
    }

    public void setIdent(String Ident) {
        this.Ident = Ident;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public long getLogTime() {
        return msgSendTime;
    }


    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public LogEvent() {
        super();
    }

    public LogEvent(String Ident, String msgId, long logTime, String command) {
        super();
        this.Ident = Ident;
        this.msgId = msgId;
        this.msgSendTime = logTime;
        this.command = command;
    }

    public LogEvent(String Ident, String msgId, long logTime) {
        super();
        this.Ident = Ident;
        this.msgId = msgId;
        this.msgSendTime = logTime;
    }

    public LogEvent(String ident, String msgId) {
        super();
        this.Ident = ident;
        this.msgId = msgId;
    }

    public LogEvent(LogEvent aLogEvent) {
        super();
        this.Ident = aLogEvent.getIdent();
        this.msgId = aLogEvent.getMsgId();
        this.msgSendTime = aLogEvent.getLogTime();
        this.command = aLogEvent.getCommand();
    }

    @Override
    public String toString() {
        return "LogEvent{" + "Ident=" + Ident + ", msgId=" + msgId + ", msgSendTime=" + msgSendTime + ", msgReceivedTime=" + msgReceivedTime + ", command=" + command + ", timeout=" + timeout + '}';
    }
    
    
    
}
