/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop.glimpl.vk10;

import com.longlinkislong.gloop.glspi.Renderbuffer;

/**
 *
 * @author zmichaels
 */
final class VK10Renderbuffer implements Renderbuffer {
    long renderbufferHandle = -1;
    
    @Override
    public boolean isValid() {
        return renderbufferHandle != -1;
    }

}
