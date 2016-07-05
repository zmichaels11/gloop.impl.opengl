/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop.glimpl.vk10;

import java.nio.LongBuffer;
import static org.lwjgl.system.MemoryUtil.memAllocLong;
import static org.lwjgl.system.MemoryUtil.memFree;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkCommandPoolCreateInfo;
import org.lwjgl.vulkan.VkDevice;

/**
 *
 * @author zmichaels
 */
final class Vk10States {
    VkDevice device;

    private long createCommandPool(int queueNodeIndex) {
        final VkCommandPoolCreateInfo cmdPoolInfo = VkCommandPoolCreateInfo.calloc()
                .sType(VK10.VK_STRUCTURE_TYPE_COMMAND_POOL_CREATE_INFO)
                .queueFamilyIndex(queueNodeIndex)
                .flags(VK10.VK_COMMAND_POOL_CREATE_RESET_COMMAND_BUFFER_BIT);

        final LongBuffer pCmdPool = memAllocLong(1);
        final int err = VK10.vkCreateCommandPool(device, cmdPoolInfo, null, pCmdPool);
        final long cmdPool = pCmdPool.get(0);

        memFree(pCmdPool);
        cmdPoolInfo.free();

        if(err != VK10.VK_SUCCESS) {
            throw new VK10Error(err);
        }

        return cmdPool;
    }
}
