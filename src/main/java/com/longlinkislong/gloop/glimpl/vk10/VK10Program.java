/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop.glimpl.vk10;

import com.longlinkislong.gloop.glspi.Program;
import org.lwjgl.opengl.GL11;
import org.lwjgl.vulkan.VkPipelineShaderStageCreateInfo;

/**
 *
 * @author zmichaels
 */
final class VK10Program implements Program{
    VkPipelineShaderStageCreateInfo.Buffer shaderStages = null;
    int deleteStatus = GL11.GL_FALSE;

    @Override
    public boolean isValid() {
        return deleteStatus == GL11.GL_FALSE;
    }
}
