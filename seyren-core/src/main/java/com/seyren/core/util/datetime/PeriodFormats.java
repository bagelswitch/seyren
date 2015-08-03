package com.seyren.core.util.datetime;

import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

public final class PeriodFormats {

    public static final PeriodFormatter DEFAULT;
    static {
        PeriodFormatterBuilder fb = new PeriodFormatterBuilder();
        String[][] suff = new String[][]{
                new String[]{"s", "sec", "second", "seconds"},
                new String[]{"m", "min", "minute", "minutes"},
                new String[]{"h", "hour", "hours"},
                new String[]{"d", "day", "days"},
        };
        fb.append(new PeriodFormatterBuilder().appendSeconds().appendSuffix(suff[0], suff[0]).toFormatter())
                .append(new PeriodFormatterBuilder().appendMinutes().appendSuffix(suff[1], suff[1]).toFormatter())
                .append(new PeriodFormatterBuilder().appendHours().appendSuffix(suff[2], suff[2]).toFormatter())
                .append(new PeriodFormatterBuilder().appendDays().appendSuffix(suff[3], suff[3]).toFormatter());
        DEFAULT = fb.toFormatter();
    }

    private PeriodFormats() {
    }
}
