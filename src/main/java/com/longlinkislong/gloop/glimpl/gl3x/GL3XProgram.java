/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop.glimpl.gl3x;

import com.longlinkislong.gloop.glimpl.GLSPIBaseObject;
import com.longlinkislong.gloop.glspi.Program;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author zmichaels
 */
final class GL3XProgram extends GLSPIBaseObject implements Program {
    int programId = -1;
    final Map<String, Integer> uniformBindings = new HashMap<>(0);
    
    @Override
    public boolean isValid() {
        return programId != -1;
    }
}
