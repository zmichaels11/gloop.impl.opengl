/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop.glimpl.vk10;

import com.longlinkislong.gloop.glspi.Buffer;

/**
 *
 * @author zmichaels
 */
final class VK10Buffer implements Buffer {
    long bufferHandle = -1;

    @Override
    public boolean isValid() {
        return bufferHandle != -1;
    }

}
