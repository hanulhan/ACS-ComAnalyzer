package com.hanulhan.comanalyzer;

import com.ahnulhan.comanalyzer.logfile.LogEvent;
import com.ahnulhan.comanalyzer.logfile.LogEventTimeSlice;
import com.ahnulhan.comanalyzer.logfile.Logfile;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringBootConsoleApplication implements CommandLineRunner {

    private static final Logger LOGGER = LogManager.getLogger(SpringBootConsoleApplication.class);

    public static void main(String[] args) {

        LOGGER.info("STARTING THE APPLICATION");
        SpringApplication.run(SpringBootConsoleApplication.class, args);
        LOGGER.info("APPLICATION FINISHED");
    }

    @Override
    public void run(String... args) {
        LOGGER.info("EXECUTING : command line runner");

        Logfile acsLog = new Logfile("/home/uli/s/communication-logs/acs-1/communication.log");

//        for (int i = 0; i < args.length; ++i) {
//            LOGGER.info("args[{}]: {}", i, args[i]);
//        }
        Map<String, LogEvent> acsTimeout = acsLog.getTimeouts();
        Map<String, LogEvent> acsSuccess = acsLog.getSuccess();
        
        acsLog.Checker();

        LOGGER.info("Lines in file: " + acsLog.getLogLines());
        float failRate = (float)acsTimeout.size() * (float)100 / (float)acsSuccess.size();
        String format = String.format("%.2f ", failRate);

        LOGGER.info("Anzahl der Timeouts: " + acsTimeout.size() + " (" + format + "%)");
        LOGGER.info("Anzahl Success: " + acsSuccess.size());
        LOGGER.info("Average Response Time: " + acsLog.getMediumResponseTime() + "ms");
        LOGGER.info("First Log Event: " + new DateTime(acsLog.getFirstLog().getLogTime()).toString());
        LOGGER.info("Last  Log Event: " + new DateTime(acsLog.getLastLog().getLogTime()).toString());

        float x = (float)acsLog.getTimeoutsWithResponse().size() * (float)100 / (float)acsSuccess.size();
        format = String.format("%.2f ", x);

        
        LOGGER.info("Timeouts with response: " + acsLog.getTimeoutsWithResponse().size() + " (" + format + "%)");
        

        Map<LocalDate, List<LogEventTimeSlice>> dayReportList;
        
        dayReportList= acsLog.getDayReport();
        List<LogEventTimeSlice> myList;
        for (Map.Entry<LocalDate, List<LogEventTimeSlice>> myEntry : dayReportList.entrySet()) {
            myList = myEntry.getValue();
            acsLog.DayReportToCSV(myEntry.getKey(), "/home/uli/s/communication-logs/acs-1", myList);
        }
        
        

        
        
//        LogEvent myEvent;
//        for (Map.Entry<String, LogEvent> myEntry : acsSuccess.entrySet()) {
//            myEvent = myEntry.getValue();
//            String myLogString;
//            if (myEvent.getTimeout() > 500) {
//
//                myLogString= String.format("[%14s]\t%6dms --> " + myEvent.getCommand(), myEvent.getIdent(), myEvent.getTimeout());
//                LOGGER.info(myLogString);
//            }
//        }



    }
}
