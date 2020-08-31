/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ahnulhan.comanalyzer.logfile;

import java.util.Comparator;
import org.joda.time.LocalDate;

/**
 *
 * @author uli
 */
public class DateComparator implements Comparator<LocalDate>{

    public DateComparator() {
        super();
    }

    @Override
    public int compare(LocalDate t1, LocalDate t2) {
        return t1.compareTo(t2);
    }
    
}
