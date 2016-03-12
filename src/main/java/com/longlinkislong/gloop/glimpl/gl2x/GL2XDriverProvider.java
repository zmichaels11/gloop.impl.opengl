/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop.glimpl.gl2x;

import com.longlinkislong.gloop.glspi.Driver;
import com.longlinkislong.gloop.glspi.DriverProvider;
import java.util.Arrays;
import java.util.List;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;

/**
 *
 * @author zmichaels
 */
public final class GL2XDriverProvider implements DriverProvider {

    @Override
    public List<String> getDriverDescription() {
        return Arrays.asList("opengl");
    }

    private static final class Holder {

        private static final GL2XDriver INSTANCE = new GL2XDriver();

    }

    @SuppressWarnings("rawtypes")
    @Override
    public Driver getDriverInstance() {
        return Holder.INSTANCE;
    }

    @Override
    public boolean is64bitUniformsSupported() {
        return false;
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
        return false;
    }

    @Override
    public boolean isDrawInstancedSupported() {
        return false;
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
        
        return cap.GL_ARB_sparse_texture && cap.GL_ARB_internalformat_query && cap.GL_ARB_framebuffer_object;
    }

    @Override
    public boolean isSupported() {
        return GL.getCapabilities().OpenGL20;
    }

    @Override
    public boolean isVertexArrayObjectSupported() {
        return GL.getCapabilities().GL_ARB_vertex_array_object;
    }

}
