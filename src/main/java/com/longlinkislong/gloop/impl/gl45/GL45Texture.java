/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop.impl.gl45;

import com.longlinkislong.gloop.spi.Texture;

/**
 *
 * @author zmichaels
 */
class GL45Texture implements Texture{
    int textureId = -1;
    int target = -1;
    int internalFormat = -1;
    
    @Override
    public boolean isValid() {
        return textureId != -1;
    }
}
