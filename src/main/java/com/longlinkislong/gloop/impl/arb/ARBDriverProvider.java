/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop.impl.arb;

import com.longlinkislong.gloop.spi.Driver;
import com.longlinkislong.gloop.spi.DriverProvider;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;

/**
 *
 * @author zmichaels
 */
public final class ARBDriverProvider implements DriverProvider {
    private static final boolean ALLOW_ARB_DRIVER = Boolean.getBoolean("com.longlinkislong.gloop.spi.allow_arb_driver")
            || Boolean.getBoolean("gloop.spi.allow_arb_driver")
            || Boolean.getBoolean("com.longlinkislong.gloop.impl.arb.arbdriver.allow_arb_driver");
    
    private static final class Holder {

        private static final ARBDriver INSTANCE = new ARBDriver();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Driver getDriverInstance() {
        return Holder.INSTANCE;
    }

    @Override
    public boolean is64bitUniformsSupported() {
        final GLCapabilities cap = GL.getCapabilities();

        return (cap.GL_ARB_gpu_shader_fp64 && cap.GL_ARB_gpu_shader_int64);
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
        return GL.getCapabilities().GL_ARB_framebuffer_object;
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
        
        return cap.GL_ARB_internalformat_query && cap.GL_ARB_sparse_texture;
    }

    @Override
    public boolean isSupported() {
        final GLCapabilities cap = GL.getCapabilities();
        
        return ALLOW_ARB_DRIVER && cap.OpenGL20 && cap.GL_ARB_direct_state_access;
    }

    @Override
    public boolean isVertexArrayObjectSupported() {
        return GL.getCapabilities().GL_ARB_vertex_array_object;
    }

}
