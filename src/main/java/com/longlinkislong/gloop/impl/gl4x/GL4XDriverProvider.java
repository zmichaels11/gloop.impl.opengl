/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop.impl.gl4x;

import com.longlinkislong.gloop.spi.Driver;
import com.longlinkislong.gloop.spi.DriverProvider;
import org.lwjgl.opengl.GL;

/**
 *
 * @author zmichaels
 */
public final class GL4XDriverProvider implements DriverProvider {
    private static final class Holder {
        private static final GL4XDriver INSTANCE = new GL4XDriver();
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
        return GL.getCapabilities().GL_ARB_compute_shader;
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
        return GL.getCapabilities().GL_ARB_buffer_storage;
    }

    @Override
    public boolean isInvalidateSubdataSupported() {
        return GL.getCapabilities().GL_ARB_invalidate_subdata;
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
        return GL.getCapabilities().GL_ARB_separate_shader_objects;
    }

    @Override
    public boolean isSparseTextureSupported() {
        return GL.getCapabilities().GL_ARB_sparse_texture;
    }

    @Override
    public boolean isSupported() {
        return GL.getCapabilities().OpenGL40;
    }

    @Override
    public boolean isVertexArrayObjectSupported() {
        return true;
    }    
}
