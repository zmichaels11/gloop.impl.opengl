package com.longlinkislong.gloop.glimpl.vk10;

import com.longlinkislong.gloop.glspi.Driver;
import com.longlinkislong.gloop.glspi.DriverProvider;
import java.util.Arrays;
import java.util.List;

public final class VK10DriverProvider implements DriverProvider {

    @Override
    public List<String> getDriverDescription() {
        return Arrays.asList("vulkan", "dsa");
    }

    private VK10Driver instance;

    @Override
    public Driver getDriverInstance() {
        if(instance == null) {
            instance = new VK10Driver();
        }

        return instance;
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
        return false;
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
        return true;
    }

    @Override
    public boolean isSupported() {
        return true;
    }

    @Override
    public boolean isVertexArrayObjectSupported() {
        return true;
    }
    
}