/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop.impl.gl45;

import com.longlinkislong.gloop.spi.Shader;

/**
 *
 * @author zmichaels
 */
final class GL45Shader implements Shader {
    int shaderId;
    
    @Override
    public boolean isValid() {
        return shaderId != -1;
    }
}
