/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop.impl.gl3x;

import com.longlinkislong.gloop.spi.Sampler;

/**
 *
 * @author zmichaels
 */
public final class GL3XSampler implements Sampler {
    int samplerId = -1;
    
    @Override
    public boolean isValid() {
        return samplerId != -1;
    }
    
}
