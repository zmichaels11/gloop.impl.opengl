/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop.impl.gl2x;

import com.longlinkislong.gloop.spi.Buffer;
import java.nio.ByteBuffer;

/**
 *
 * @author zmichaels
 */
public final class GL2XBuffer implements Buffer {
    int bufferId = -1;
    ByteBuffer mapBuffer;
    
    @Override
    public boolean isValid() {
        return bufferId != -1;
    }
}
