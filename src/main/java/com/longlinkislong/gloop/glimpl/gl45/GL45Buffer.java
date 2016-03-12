/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop.glimpl.gl45;

import com.longlinkislong.gloop.glspi.Buffer;
import java.nio.ByteBuffer;

/**
 *
 * @author zmichaels
 */
final class GL45Buffer implements Buffer{
    int bufferId = -1;
    ByteBuffer mapBuffer;
    
    @Override
    public boolean isValid() {
        return bufferId != -1;
    }
}
