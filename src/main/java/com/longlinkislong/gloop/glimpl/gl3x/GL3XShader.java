/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop.glimpl.gl3x;

import com.longlinkislong.gloop.glimpl.GLSPIBaseObject;
import com.longlinkislong.gloop.glspi.Shader;

/**
 *
 * @author zmichaels
 */
class GL3XShader extends GLSPIBaseObject implements Shader {
    int shaderId = -1;
    
    @Override
    public boolean isValid() {
        return shaderId != -1;
    }
}
