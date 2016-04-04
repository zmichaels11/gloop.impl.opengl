/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop.glimpl.gl4x;

import com.longlinkislong.gloop.glspi.Renderbuffer;

/**
 *
 * @author zmichaels
 */
final class GL4XRenderbuffer implements Renderbuffer {
    int renderbufferId = -1;
    
    @Override
    public boolean isValid() {
        return renderbufferId != -1;
    }
}
