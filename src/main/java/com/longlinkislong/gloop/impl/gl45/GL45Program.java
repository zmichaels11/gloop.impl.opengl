/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop.impl.gl45;

import com.longlinkislong.gloop.spi.Program;

/**
 *
 * @author zmichaels
 */
public final class GL45Program implements Program {        
    int programId = -1;
    
    @Override
    public boolean isValid() {
        return programId != -1;
    }
}
