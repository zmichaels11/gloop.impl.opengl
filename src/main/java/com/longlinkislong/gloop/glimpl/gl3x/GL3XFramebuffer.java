/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop.glimpl.gl3x;

import com.longlinkislong.gloop.glspi.Framebuffer;

/**
 *
 * @author zmichaels
 */
final class GL3XFramebuffer implements Framebuffer {

    int framebufferId = -1;

    @Override
    public boolean isValid() {
        return framebufferId != -1;
    }

}
