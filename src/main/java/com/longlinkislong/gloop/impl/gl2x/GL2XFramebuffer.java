/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop.impl.gl2x;

import com.longlinkislong.gloop.spi.Framebuffer;

/**
 *
 * @author zmichaels
 */
public final class GL2XFramebuffer implements Framebuffer {

    int framebufferId = -1;

    @Override
    public boolean isValid() {
        return framebufferId != -1;
    }

}
