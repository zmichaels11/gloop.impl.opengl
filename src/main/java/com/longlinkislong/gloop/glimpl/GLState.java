/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop.glimpl;

import com.longlinkislong.gloop.glspi.Tweaks;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL21;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL40;

/**
 *
 * @author zmichaels
 */
public final class GLState {   
    private final Tweaks tweaks;
    private final Deque<Integer> programStack = new ArrayDeque<>();
    private final Deque<Integer> vaoStack = new ArrayDeque<>();
    private final Deque<Integer> bufferStack = new ArrayDeque<>();
    private final Deque<Integer> textureStack = new ArrayDeque<>();
    private final Deque<Integer> framebufferStack = new ArrayDeque<>();
    
    public GLState(final Tweaks tweaks) {
        this.tweaks = Objects.requireNonNull(tweaks);
    }

    public void framebufferPop(final int target) {
        if(tweaks.ignoreFramebufferStateReset) {
            // ignore reset.
        } else {
            GL30.glBindFramebuffer(target, this.framebufferStack.pop());
        }
    }
    
    public void framebufferPush(final int target, final int fb) {
        if(tweaks.ignoreFramebufferStateReset) {
            GL30.glBindFramebuffer(target, fb);
        } else {
            this.framebufferStack.push(GL11.glGetInteger(getFramebufferBinding(target)));
            GL30.glBindFramebuffer(target, fb);
        }
    }
    
    public void texturePop(final int target) {
        if(tweaks.ignoreTextureStateReset) {
            // ignore reset
        } else {
            GL11.glBindTexture(target, this.textureStack.pop());
        }
    }
    
    public void texturePush(final int target, final int texture) {
        if(tweaks.ignoreTextureStateReset) {
            GL11.glBindTexture(target, texture);
        } else {
            this.textureStack.push(GL11.glGetInteger(getTextureBinding(target)));
            GL11.glBindTexture(target, texture);
        }
    }
    
    public void bufferPop(final int target) {
        if(tweaks.ignoreBufferStateReset) {
            // ignore reset
        } else {
            GL15.glBindBuffer(target, this.bufferStack.pop());
        }
    }
    
    public void bufferPush(final int target, final int buffer) {
        if(tweaks.ignoreBufferStateReset) {
            GL15.glBindBuffer(target, buffer);
        } else {
            this.bufferStack.push(GL11.glGetInteger(getBufferBinding(target)));
            GL15.glBindBuffer(target, buffer);
        }
    }
    
    
    public void programPop() {
        if(tweaks.ignoreProgramStateReset) {
            // ignore reset
        } else {
            GL20.glUseProgram(programStack.pop());
        }
    }
    
    public void programPush(final int program) {
        if(tweaks.ignoreProgramStateReset) {
            GL20.glUseProgram(program);
        } else {
            this.programStack.push(GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM));
            GL20.glUseProgram(program);
        }
    }
    
    public void vertexArrayPop() {
        if(tweaks.ignoreVaoStateReset) {
            // ignore reset
        } else {
            final int restoreVao = this.vaoStack.pop();            
            GL30.glBindVertexArray(restoreVao);
        }
    }
    
    public void vertexArrayPush(final int vao) {
        if(tweaks.ignoreVaoStateReset) {
            GL30.glBindVertexArray(vao);
        } else {
            vaoStack.push(GL11.glGetInteger(GL30.GL_VERTEX_ARRAY_BINDING));
            GL30.glBindVertexArray(vao);
        }
    }

    public static int getFramebufferBinding(final int target) {
        switch (target) {
            case GL30.GL_FRAMEBUFFER:
                return GL30.GL_FRAMEBUFFER_BINDING;
            case GL30.GL_READ_FRAMEBUFFER:
                return GL30.GL_READ_FRAMEBUFFER_BINDING;
            case GL30.GL_DRAW_FRAMEBUFFER:
                return GL30.GL_DRAW_FRAMEBUFFER_BINDING;
            default:
                throw new UnsupportedOperationException("Unsupported framebuffer target: " + target);
        }
    }

    public static int getTextureBinding(final int target) {
        switch (target) {
            case GL11.GL_TEXTURE_1D:
                return GL11.GL_TEXTURE_BINDING_1D;
            case GL11.GL_TEXTURE_2D:
                return GL11.GL_TEXTURE_BINDING_2D;
            case GL12.GL_TEXTURE_3D:
                return GL12.GL_TEXTURE_BINDING_3D;
            case GL30.GL_TEXTURE_1D_ARRAY:
                return GL30.GL_TEXTURE_BINDING_1D_ARRAY;
            case GL30.GL_TEXTURE_2D_ARRAY:
                return GL30.GL_TEXTURE_BINDING_2D_ARRAY;
            default:
                throw new UnsupportedOperationException("Unsupported texture target: " + target);
        }
    }

    public static int getBufferBinding(final int target) {
        switch (target) {
            case GL15.GL_ARRAY_BUFFER:
                return GL15.GL_ARRAY_BUFFER_BINDING;
            case GL15.GL_ELEMENT_ARRAY_BUFFER:
                return GL15.GL_ELEMENT_ARRAY_BUFFER_BINDING;
            case GL21.GL_PIXEL_PACK_BUFFER:
                return GL21.GL_PIXEL_PACK_BUFFER_BINDING;
            case GL21.GL_PIXEL_UNPACK_BUFFER:
                return GL21.GL_PIXEL_UNPACK_BUFFER_BINDING;
            case GL31.GL_UNIFORM_BUFFER:
                return GL31.GL_UNIFORM_BUFFER_BINDING;
            case GL40.GL_DRAW_INDIRECT_BUFFER:
                return GL40.GL_DRAW_INDIRECT_BUFFER_BINDING;
            default:
                throw new UnsupportedOperationException("Unsupported buffer target: " + target);
        }
    }
}
