/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop.impl.arb;

import com.longlinkislong.gloop.spi.VertexArray;

/**
 *
 * @author zmichaels
 */
final class ARBVertexArray implements VertexArray {
    int vertexArrayId = -1;
    
    @Override
    public boolean isValid() {
        return vertexArrayId != -1;
    }
}
