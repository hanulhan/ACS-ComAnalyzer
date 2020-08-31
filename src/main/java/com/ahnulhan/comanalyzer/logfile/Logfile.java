/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ahnulhan.comanalyzer.logfile;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.logging.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 *
 * @author uli
 */
public class Logfile {

    Map timeoutMap = new HashMap<String, LogEvent>();
    HashMap successMap = new HashMap<String, LogEvent>();
    private static final Logger LOGGER = LogManager.getLogger(Logfile.class);
    private static final SimpleDateFormat fmtISOdate = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private static final SimpleDateFormat fmtTime = new SimpleDateFormat("HH:mm");
    private static final SimpleDateFormat fmtDate = new SimpleDateFormat("yyyy-MM-dd");
    private static final String CSV_DELIMITER = ";";

    Scanner scanner;
    int logLines = 0;
    long mediumResponseTime = 0;
    LogEvent firstLog, lastLog;

    public Logfile(String aFile) {

        String myIdent, myMsgId, myRequest, myTimeoutString;
        String myDateString;
        LogEvent myLogEvent;
        long myLogTime = 0;
        int myTimeout;
        Boolean fFirstLog = false;

        firstLog = new LogEvent();
        firstLog.setMsgSendTime(new DateTime().getMillis());
        lastLog = new LogEvent();
        try {
            lastLog.setMsgSendTime(fmtISOdate.parse("2019-01-01 00:00").getTime());
        } catch (ParseException ex) {
            LOGGER.error(ex);
        }

        LOGGER.info("Analyze Timeouts");
        try {
            scanner = new Scanner(new File(aFile));
        } catch (FileNotFoundException ex) {
            LOGGER.error(ex);
        }
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();

            if (line.contains("Response TIMEOUT")) {
                try {
                    myIdent = line.substring(line.indexOf("[") + 1, line.indexOf("]"));
                    myMsgId = line.substring(line.indexOf("(") + 1, line.indexOf(")"));
                    myRequest = line.substring(line.indexOf("Request:") + 8, line.indexOf("(ID:") - 1);
                    myMsgId = myMsgId.substring(3);
                    myDateString = line.substring(0, 16);
                    try {
                        myLogTime = fmtISOdate.parse(myDateString).getTime();
                    } catch (ParseException ex) {
                        LOGGER.error(ex);
                    }
                    myLogEvent = new LogEvent(myIdent, myMsgId);
                    myLogEvent.setCommand(myRequest);
                    myLogEvent.setMsgSendTime(myLogTime);
                    myLogEvent.setMsgReceivedTime(0);
                    timeoutMap.put(myMsgId, myLogEvent);
                } catch (java.lang.StringIndexOutOfBoundsException e) {
                    //LOGGER.error(e);
                }
            }
        }

        LOGGER.info("Analyze success");
        try {
            scanner = new Scanner(new File(aFile));
        } catch (FileNotFoundException ex) {
            LOGGER.error(ex);
        }
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            //line = line.replaceAll("(\\r|\\n)", "");

            if (line.contains("send (ID:")) {
                try {

                    myMsgId = line.substring(line.indexOf("ID:") + 3, line.length() - 1);
                    logLines++;
                    if (!timeoutMap.containsKey(myMsgId)) {
                        myIdent = line.substring(line.indexOf("[") + 1, line.indexOf("]"));
                        myRequest = line.substring(line.indexOf("Message (") + 9, line.indexOf(")"));
                        myDateString = line.substring(0, 16);
                        try {
                            myLogTime = fmtISOdate.parse(myDateString).getTime();
                        } catch (ParseException ex) {
                            LOGGER.error(ex);
                        }

                        myLogEvent = new LogEvent(myIdent, myMsgId);
                        myLogEvent.setMsgSendTime(myLogTime);
                        myLogEvent.setCommand(myRequest);

                        successMap.put(myMsgId, myLogEvent);

                        if (!fFirstLog) {
                            firstLog = new LogEvent(myLogEvent);
                            fFirstLog = true;
                        }
                        lastLog = new LogEvent(myLogEvent);

                    }
                } catch (java.lang.StringIndexOutOfBoundsException e) {
                    //LOGGER.error(e);
                }
            }

            if (line.contains("Msg finished in:")) {
                myMsgId = line.substring(line.indexOf("ID:") + 3, line.indexOf(")"));

                if (successMap.containsKey(myMsgId)) {
                    try {
                        myTimeoutString = line.substring(line.indexOf("Msg finished in: ") + 17, line.indexOf("ms. (ID:"));
                        myTimeout = Integer.valueOf(myTimeoutString);
                        myDateString = line.substring(0, 16);

                        myLogEvent = (LogEvent) successMap.get(myMsgId);
                        myLogEvent.setTimeout(myTimeout);

                        try {
                            myLogTime = fmtISOdate.parse(myDateString).getTime();
                        } catch (ParseException ex) {
                            LOGGER.error(ex);
                        }
                        myLogEvent.setMsgReceivedTime(myLogTime);

                        successMap.put(myMsgId, myLogEvent);

                        mediumResponseTime += (long) myTimeout;
                    } catch (java.lang.StringIndexOutOfBoundsException e) {
                        //LOGGER.error(e);
                    }
                } else if (timeoutMap.containsKey(myMsgId)) {
                    try {
                        myDateString = line.substring(0, 16);
                        try {
                            myLogTime = fmtISOdate.parse(myDateString).getTime();
                        } catch (ParseException ex) {
                            LOGGER.error(ex);
                        }
                        myLogEvent = (LogEvent) timeoutMap.get(myMsgId);
                        myLogEvent.setMsgReceivedTime(myLogTime);

                        timeoutMap.put(myMsgId, myLogEvent);
                    } catch (java.lang.StringIndexOutOfBoundsException e) {
                        //LOGGER.error(e);
                    }
                }
            }
        }

        mediumResponseTime = mediumResponseTime / (long) successMap.size();

    }

    public void DayReportToCSV(LocalDate aDate, String aPath, List<LogEventTimeSlice> aDayReport) {

        File myFile = new File(aPath + "/" + aDate.toString() + "-DayReport.csv");
        try {
            FileWriter myFw = new FileWriter(myFile);
            BufferedWriter myBw = new BufferedWriter(myFw);
            myBw.write("Date" + CSV_DELIMITER + aDate.toString());
            myBw.newLine();
            myBw.write("TimeResolution: " + CSV_DELIMITER + aDayReport.get(0).getTimeRangeMinutes());
            myBw.newLine();
            // Headline
            myBw.write("Time" + CSV_DELIMITER + "SuccessCount" + CSV_DELIMITER + "TimeoutCount" + CSV_DELIMITER + "Avg. ResponseTime");
            myBw.newLine();            
            for (LogEventTimeSlice temp: aDayReport)    {
                myBw.write( temp.getTimeString() + CSV_DELIMITER + 
                            temp.getSuccessEventCount() + CSV_DELIMITER +
                            temp.getTimeoutEventCount() + CSV_DELIMITER + 
                            temp.getAvgResponseTime()
                );
                myBw.newLine();
            }

            myBw.close();
            myFw.close();

        } catch (IOException ex) {
            LOGGER.error(ex);
        }

    }

    public void Checker() {
        LOGGER.info("Check if all success Events have valid data");

        LogEvent myEvent;

        for (Iterator<Map.Entry<String, LogEvent>> it = this.getSuccess().entrySet().iterator(); it.hasNext();) {
            //for (Map.Entry<String, LogEvent> myEntry : this.getSuccess().entrySet()) {
            Map.Entry<String, LogEvent> myEntry = it.next();
            myEvent = myEntry.getValue();
            if (myEvent.getMsgReceivedTime() == 0) {
                LOGGER.info("MsgReceive Time is not given" + new DateTime(myEvent.getMsgSendTime()) + ", " + myEvent.getMsgId() + ", " + myEvent.getCommand());
                it.remove();
                this.logLines--;
            }

            if (myEvent.getCommand().length() == 0) {
                LOGGER.info("Command length not correct" + myEvent.toString());
            }

        }
    }

    public Map<String, LogEvent> getTimeoutsWithResponse() {

        LogEvent myEvent;
        Map helperMap = new HashMap<String, LogEvent>();

        for (Map.Entry<String, LogEvent> myEntry : this.getTimeouts().entrySet()) {
            myEvent = myEntry.getValue();
            if (myEvent.getMsgReceivedTime() > 0) {
                helperMap.put(myEntry.getKey(), myEvent);
            }
        }
        return helperMap;
    }

    public Map<LocalDate, List<LogEventTimeSlice>> getDayReport() {

        int myTimeSliceCount = 12;
        LogEvent myEvent;
        LogEventTimeSlice myLogTimeSlice;
        // Map for one Day
        List<LogEventTimeSlice> myDayReport;

        // Map of days
        //Map<LocalDate, List<LogEventTimeSlice>> myDayReportMap = new HashMap<>();
        Map<LocalDate, List<LogEventTimeSlice>> myDayReportMap = new TreeMap<LocalDate, List<LogEventTimeSlice>>(new DateComparator());
        //Map<String, List<LogEventTimeSlice>> myDayReportMap = new HashMap<>();

        int minutesOnSlice = 1440 / myTimeSliceCount;

        LocalTime startTime = LocalTime.parse("00:00");
        //LocalTime myTime;
        String myTime;

        int timeToAdd = minutesOnSlice / 2;

        //LogEvent myStartEvent= this.getSuccess().
        // iterate over successList
        for (Map.Entry<String, LogEvent> myEntry : this.getSuccess().entrySet()) {

            myEvent = myEntry.getValue();
            DateTime z = new DateTime(myEvent.getMsgSendTime());
            LOGGER.info("HoursOfDay: " + z.getHourOfDay() + ", Minutes: " + z.getMinuteOfDay());
            int index = z.getMinuteOfDay() / minutesOnSlice;
            LOGGER.info("Put Event from " + z.toString() + " to index: " + index);
            LocalDate myLocalDate = new LocalDate(myEvent.getMsgSendTime());
            //String myLocalDate = new LocalDate(myEvent.getMsgSendTime()).toString();

            LOGGER.info("Date of LogEvent: " + myLocalDate.toString());
            if (!myDayReportMap.containsKey(myLocalDate)) {
                // Create DayReport with time slice
                LOGGER.info("Create DayReport for " + myLocalDate);
                myDayReport = new ArrayList<LogEventTimeSlice>();
                for (int i = 0; i < myTimeSliceCount; i++) {

                    myTime = startTime.plusMinutes(timeToAdd).toString("hh:mm");
                    LOGGER.info("add time [" + i + "] --> " + myTime);
                    myLogTimeSlice = new LogEventTimeSlice(myTime, minutesOnSlice);
                    myLogTimeSlice.setTimeRangeMinutes(minutesOnSlice);
                    myDayReport.add(myLogTimeSlice);
                    timeToAdd += minutesOnSlice;
                }
                myDayReportMap.put(myLocalDate, myDayReport);
            }
            myDayReport = myDayReportMap.get(myLocalDate);
            LogEventTimeSlice x = myDayReport.get(index);
            x.addTotalResponseTime(myEvent.getTimeout());
            myDayReport.set(index, x);
        }

        // iterate over timeoutMap
        for (Map.Entry<String, LogEvent> myEntry : this.getTimeouts().entrySet()) {

            myEvent = myEntry.getValue();
            DateTime z = new DateTime(myEvent.getMsgSendTime());
            LOGGER.info("HoursOfDay: " + z.getHourOfDay() + ", Minutes: " + z.getMinuteOfDay());
            int index = z.getMinuteOfDay() / minutesOnSlice;
            LOGGER.info("Put Event from " + z.toString() + " to index: " + index);
            LocalDate myLocalDate = new LocalDate(myEvent.getMsgSendTime());
            //String myLocalDate = new LocalDate(myEvent.getMsgSendTime()).toString();

            LOGGER.info("Date of LogEvent: " + myLocalDate.toString());
            if (!myDayReportMap.containsKey(myLocalDate)) {
                // Create DayReport with time slice
                LOGGER.info("Create DayReport for " + myLocalDate);
                myDayReport = new ArrayList<LogEventTimeSlice>();
                for (int i = 0; i < myTimeSliceCount; i++) {
                    myTime = startTime.plusMinutes(timeToAdd).toString();
                    LOGGER.info("add time [" + i + "] --> " + myTime);
                    myLogTimeSlice = new LogEventTimeSlice(myTime, minutesOnSlice);
                    myLogTimeSlice.setTimeRangeMinutes(minutesOnSlice);
                    myDayReport.add(myLogTimeSlice);
                    timeToAdd += minutesOnSlice;
                }
                myDayReportMap.put(myLocalDate, myDayReport);
            }
            myDayReport = myDayReportMap.get(myLocalDate);
            LogEventTimeSlice x = myDayReport.get(index);
            x.increaseTimeoutEventCount();
            myDayReport.set(index, x);
        }

        return myDayReportMap;
    }

    private int getIndex(LocalTime aTime, int aTimeSliceCount) {

        int myMinutesOnSlice = 1440 / aTimeSliceCount;

        int myTotalMinutes = aTime.getMinuteOfHour() + (aTime.getSecondOfMinute() / 60);

        return (myTotalMinutes / myMinutesOnSlice);

    }

    public Map<String, LogEvent> getTimeouts() {
        return timeoutMap;
    }

    public Map<String, LogEvent> getSuccess() {
        return successMap;
    }

    public int getMediumResponseTime() {
        return (int) mediumResponseTime;
    }

    public LogEvent getFirstLog() {
        return firstLog;
    }

    public LogEvent getLastLog() {
        return lastLog;
    }

    public int getLogLines() {
        return logLines;
    }

}
