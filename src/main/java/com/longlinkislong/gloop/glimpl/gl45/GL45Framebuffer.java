/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop.glimpl.gl45;

import com.longlinkislong.gloop.glimpl.GLSPIBaseObject;
import com.longlinkislong.gloop.glspi.Framebuffer;

/**
 *
 * @author zmichaels
 */
final class GL45Framebuffer extends GLSPIBaseObject  implements Framebuffer{
    int framebufferId = -1;
    
    @Override
    public boolean isValid() {
        return framebufferId != -1;
    }
}
