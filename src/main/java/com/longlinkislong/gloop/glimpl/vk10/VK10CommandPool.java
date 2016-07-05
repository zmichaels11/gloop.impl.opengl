/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop.glimpl.vk10;

import java.nio.LongBuffer;
import org.lwjgl.PointerBuffer;
import static org.lwjgl.system.MemoryUtil.memAllocLong;
import static org.lwjgl.system.MemoryUtil.memAllocPointer;
import static org.lwjgl.system.MemoryUtil.memFree;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkCommandBufferAllocateInfo;
import org.lwjgl.vulkan.VkCommandPoolCreateInfo;
import org.lwjgl.vulkan.VkDevice;

/**
 *
 * @author zmichaels
 */
class VK10CommandPool {
    final long commandPool;
    final VkDevice device;

    VK10CommandPool(VkDevice device, int queueNodeIndex) {
        this.device = device;

        final VkCommandPoolCreateInfo cmdPoolInfo = VkCommandPoolCreateInfo.calloc()
                .sType(VK10.VK_STRUCTURE_TYPE_COMMAND_POOL_CREATE_INFO)
                .queueFamilyIndex(queueNodeIndex)
                .flags(VK10.VK_COMMAND_POOL_CREATE_RESET_COMMAND_BUFFER_BIT);

        final LongBuffer pCmdPool = memAllocLong(1);
        final int err = VK10.vkCreateCommandPool(device, cmdPoolInfo, null, pCmdPool);

        this.commandPool = pCmdPool.get(0);

        memFree(pCmdPool);
        cmdPoolInfo.free();

        if(err != VK10.VK_SUCCESS) {
            throw new VK10Error(err);
        }
    }

    class CommandBuffer {
        final long commandBuffer;

        CommandBuffer() {
            VkCommandBufferAllocateInfo cmdBufferAllocateInfo = VkCommandBufferAllocateInfo.calloc()
                    .sType(VK10.VK_STRUCTURE_TYPE_COMMAND_BUFFER_ALLOCATE_INFO)
                    .commandPool(commandPool)
                    .level(VK10.VK_COMMAND_BUFFER_LEVEL_PRIMARY)
                    .commandBufferCount(1);

            final PointerBuffer pCommandBuffer = memAllocPointer(1);
            final int err = VK10.vkAllocateCommandBuffers(device, cmdBufferAllocateInfo, pCommandBuffer);

            this.commandBuffer = pCommandBuffer.get(0);

            memFree(pCommandBuffer);
            cmdBufferAllocateInfo.free();

            if(err != VK10.VK_SUCCESS) {
                throw new VK10Error(err);
            }
        }
    }
}
