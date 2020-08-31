/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ahnulhan.comanalyzer.logfile;

import org.joda.time.LocalTime;


/**
 *
 * @author uli
 */
public class LogEventTimeSlice {

    //private LocalTime time;
    private String timeString;
    private int timeRangeMinutes;
    long timeoutEventCount;
    long successEventCount;
    
    long totalResponseTime;
    long avgResponseTime;
    
    //public LogEventTimeSlice(LocalTime aTime, int aTimeRangeMinutes)    {
    public LogEventTimeSlice(String aTimeString, int aTimeRangeMinutes)    {
        //this.time= aTime;
        this.timeString= aTimeString;
        this.timeRangeMinutes= aTimeRangeMinutes;
        this.timeoutEventCount = 0;
        this.successEventCount = 0;
        this.totalResponseTime = 0;
        this.avgResponseTime = 0;
    }



//    public LocalTime getTime() {
//        return time;
//    }
//
//    public void setTime(LocalTime time) {
//        this.time = time;
//    }
    public void setTimeString(String timeString) {
        this.timeString = timeString;
    }
    public String getTimeString() {
        return timeString;
    }

    
    public long getTimeoutEventCount() {
        return timeoutEventCount;
    }

    public void increaseTimeoutEventCount() {
        this.timeoutEventCount++;
    }

    public long getSuccessEventCount() {
        return successEventCount;
    }

    public long getTotalResponseTime() {
        return totalResponseTime;
    }

    public void addTotalResponseTime(long aResponseTime) {
        this.totalResponseTime += aResponseTime;
        this.successEventCount++;
        this.avgResponseTime = this.getTotalResponseTime() / this.getSuccessEventCount();
                
    }

    public long getAvgResponseTime() {
        return avgResponseTime;
    }

    public void setAvgResponseTime(int avgResponseTime) {
        this.avgResponseTime = avgResponseTime;
    }

    public int getTimeRangeMinutes() {
        return timeRangeMinutes;
    }

    public void setTimeRangeMinutes(int timeRangeMinutes) {
        this.timeRangeMinutes = timeRangeMinutes;
    }
    
    
    
    
}
