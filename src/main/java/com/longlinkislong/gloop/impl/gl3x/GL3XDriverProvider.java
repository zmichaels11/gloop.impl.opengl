/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop.impl.gl3x;

import com.longlinkislong.gloop.spi.Driver;
import com.longlinkislong.gloop.spi.DriverProvider;
import java.util.Arrays;
import java.util.List;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;

/**
 *
 * @author zmichaels
 */
public final class GL3XDriverProvider implements DriverProvider {

    @Override
    public List<String> getDriverDescription() {
        return Arrays.asList("opengl");
    }

    private static final class Holder {
        private static final GL3XDriver INSTANCE = new GL3XDriver();        
    }
    
    @SuppressWarnings("rawtypes")
    @Override
    public Driver getDriverInstance() {
        return Holder.INSTANCE;
    }

    @Override
    public boolean is64bitUniformsSupported() {
        final GLCapabilities cap = GL.getCapabilities();
        
        return cap.GL_ARB_gpu_shader_fp64 && cap.GL_ARB_gpu_shader_int64;
    }

    @Override
    public boolean isBufferObjectSupported() {
        return true;
    }

    @Override
    public boolean isComputeShaderSupported() {
        return false;
    }

    @Override
    public boolean isDrawIndirectSupported() {
        return GL.getCapabilities().GL_ARB_draw_indirect;
    }

    @Override
    public boolean isDrawInstancedSupported() {
        return GL.getCapabilities().OpenGL31;
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
        return GL.getCapabilities().GL_ARB_sampler_objects;
    }

    @Override
    public boolean isSeparateShaderObjectsSupported() {
        return GL.getCapabilities().GL_ARB_separate_shader_objects;
    }

    @Override
    public boolean isSparseTextureSupported() {
        final GLCapabilities cap = GL.getCapabilities();

        return cap.GL_ARB_sparse_texture && cap.GL_ARB_internalformat_query;
    }

    @Override
    public boolean isSupported() {
        return GL.getCapabilities().OpenGL30;
    }

    @Override
    public boolean isVertexArrayObjectSupported() {
        return true;
    }
    
}
