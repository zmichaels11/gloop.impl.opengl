/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop.impl.gl45;

import com.longlinkislong.gloop.spi.Driver;
import com.longlinkislong.gloop.spi.DriverProvider;
import java.util.Arrays;
import java.util.List;
import org.lwjgl.opengl.GL;

/**
 *
 * @author zmichaels
 */
public final class GL45DriverProvider implements DriverProvider {

    @Override
    public List<String> getDriverDescription() {
        return Arrays.asList("opengl", "dsa");
    }

    private static final class Holder {

        private static final GL45Driver INSTANCE = new GL45Driver();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Driver getDriverInstance() {
        return Holder.INSTANCE;
    }

    @Override
    public boolean is64bitUniformsSupported() {
        return true;
    }

    @Override
    public boolean isBufferObjectSupported() {
        return true;
    }

    @Override
    public boolean isComputeShaderSupported() {
        return true;
    }

    @Override
    public boolean isDrawIndirectSupported() {
        return true;
    }

    @Override
    public boolean isDrawInstancedSupported() {
        return true;
    }

    @Override
    public boolean isDrawQuerySupported() {
        return true;
    }

    @Override
    public boolean isFramebufferObjectSupported() {
        return true;
    }

    @Override
    public boolean isImmutableBufferStorageSupported() {
        return true;
    }

    @Override
    public boolean isInvalidateSubdataSupported() {
        return true;
    }

    @Override
    public boolean isProgramSupported() {
        return true;
    }

    @Override
    public boolean isSamplerSupported() {
        return true;
    }

    @Override
    public boolean isSeparateShaderObjectsSupported() {
        return true;
    }

    @Override
    public boolean isSparseTextureSupported() {
        return GL.getCapabilities().GL_ARB_sparse_texture;
    }

    @Override
    public boolean isSupported() {
        return GL.getCapabilities().OpenGL45;
    }

    @Override
    public boolean isVertexArrayObjectSupported() {
        return true;
    }

}
