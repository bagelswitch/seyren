/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
