/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop.impl.gl2x;

import com.longlinkislong.gloop.spi.VertexArray;
import java.util.List;

/**
 *
 * @author zmichaels
 */
final class GL2XVertexArray implements VertexArray {

    GL2XBuffer element = null;
    int vertexArrayId = -1;
    List<VertexAttrib> attribs = null;
    
    @Override
    public boolean isValid() {
        return vertexArrayId != -1;
    }
    
    class VertexAttrib {
        GL2XBuffer buffer;
        int size;
        int type;
        long offset;
        int stride;
        int index;
    }
    
}
