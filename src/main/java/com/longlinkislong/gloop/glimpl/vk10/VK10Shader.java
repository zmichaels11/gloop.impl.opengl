/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop.glimpl.vk10;

import com.longlinkislong.gloop.glspi.Shader;
import org.lwjgl.opengl.GL11;
import org.lwjgl.vulkan.VkPipelineShaderStageCreateInfo;

/**
 *
 * @author zmichaels
 */
final class VK10Shader implements Shader {
    VkPipelineShaderStageCreateInfo shaderStage = null;
    String src = null;
    int vkType = -1;
    int glType = -1;
    String infoLog = "";
    int compileStatus = GL11.GL_FALSE;
    int deleteStatus = GL11.GL_FALSE;

    @Override
    public boolean isValid() {
        return shaderStage != null;
    }

}
