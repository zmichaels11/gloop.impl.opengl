/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop.glimpl;

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
}
