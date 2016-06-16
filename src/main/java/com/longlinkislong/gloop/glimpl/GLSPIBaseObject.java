/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop.glimpl;

import java.util.List;

/**
 *
 * @author zmichaels
 */
public class GLSPIBaseObject {
    private long lastUpdated = 0L;
    
    public final void updateTime() {
        this.lastUpdated = System.nanoTime();
    }
    
    public final void resetTime() {
        this.lastUpdated = 0L;
    }
    
    public final long getTimeSinceLastUpdated() {
        return System.nanoTime() - this.lastUpdated;
    }

    public static void recordCall(final List<String> callHistory, String call, Object... params) {
        final StringBuilder record = new StringBuilder(call);

        record.append("(");

        if (params.length > 0) {
            for (int i = 0; i < params.length - 1; i++) {
                if (params[i] == null) {
                    record.append("NULL");
                } else if (params[i] instanceof CharSequence) {
                    record.append("\"");
                    record.append(params[i].toString());
                    record.append("\"");
                } else {
                    record.append(params[i].toString());
                }

                record.append(", ");
            }

            record.append(params[params.length - 1].toString());
        }

        record.append(")");

        callHistory.add(record.toString());
    }
}
