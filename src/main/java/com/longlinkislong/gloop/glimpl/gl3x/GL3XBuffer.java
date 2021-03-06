/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop.glimpl.gl3x;

import com.longlinkislong.gloop.glimpl.GLSPIBaseObject;
import com.longlinkislong.gloop.glspi.Buffer;
import java.nio.ByteBuffer;

/**
 *
 * @author zmichaels
 */
final class GL3XBuffer extends GLSPIBaseObject  implements Buffer {
    int bufferId = -1;
    ByteBuffer mapBuffer;
    long size;
    int usage;
    
    @Override
    public boolean isValid() {
        return bufferId != -1;
    }
}
