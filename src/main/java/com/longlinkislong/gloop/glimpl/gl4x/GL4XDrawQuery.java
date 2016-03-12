/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop.glimpl.gl4x;

import com.longlinkislong.gloop.glspi.DrawQuery;

/**
 *
 * @author zmichaels
 */
final class GL4XDrawQuery implements DrawQuery {
    int drawQueryId = -1;
    
    @Override
    public boolean isValid() {
        return drawQueryId != -1;
    }
    
}
