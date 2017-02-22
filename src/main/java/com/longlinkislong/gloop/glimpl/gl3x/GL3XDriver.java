/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop.glimpl.gl3x;

import com.longlinkislong.gloop.glspi.Driver;
import com.longlinkislong.gloop.glspi.Shader;
import com.longlinkislong.gloop.glspi.Tweaks;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.lwjgl.opengl.ARBBufferStorage;
import org.lwjgl.opengl.ARBDirectStateAccess;
import org.lwjgl.opengl.ARBDrawIndirect;
import org.lwjgl.opengl.ARBGPUShaderFP64;
import org.lwjgl.opengl.ARBInvalidateSubdata;
import org.lwjgl.opengl.ARBSamplerObjects;
import org.lwjgl.opengl.ARBSeparateShaderObjects;
import org.lwjgl.opengl.ARBTextureStorage;
import org.lwjgl.opengl.EXTDirectStateAccess;
import org.lwjgl.opengl.EXTTextureFilterAnisotropic;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL21;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL33;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author zmichaels
 */
final class GL3XDriver implements Driver<
        GL3XBuffer, GL3XFramebuffer, GL3XRenderbuffer, GL3XTexture, GL3XShader, GL3XProgram, GL3XSampler, GL3XVertexArray> {

    private static final boolean EXCLUSIVE_CONTEXT = Boolean.getBoolean("com.longlinkislong.gloop.glimpl.exclusive_context");
    private static final boolean ARB_DSA = !Boolean.getBoolean("com.longlinkislong.gloop.glimpl.disable.GL_ARB_direct_state_access");
    private static final boolean EXT_DSA = Boolean.getBoolean("com.longlinkislong.gloop.glimpl.enable.GL_EXT_direct_state_access");

    private static final Logger LOGGER = LoggerFactory.getLogger(GL3XDriver.class);

    @Override
    public void bufferBindAtomic(GL3XBuffer bt, int i) {
        throw new UnsupportedOperationException("OpenGL 4.2 is not supported!"); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void bufferBindAtomic(GL3XBuffer bt, int i, long l, long l1) {
        throw new UnsupportedOperationException("OpenGL 4.2 is not supported!"); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int bufferGetMaxUniformBindings() {
        return GL11.glGetInteger(GL31.GL_MAX_UNIFORM_BUFFER_BINDINGS);
    }

    @Override
    public int bufferGetMaxUniformBlockSize() {
        return GL11.glGetInteger(GL31.GL_MAX_UNIFORM_BLOCK_SIZE);
    }

    @Override
    public void bufferBindStorage(GL3XBuffer bt, int index) {
        throw new UnsupportedOperationException("GL_arb_shader_storage_buffer_object or OpenGL 4.0 is not supported!");
    }

    @Override
    public void bufferBindStorage(GL3XBuffer bt, int index, long offset, long size) {
        throw new UnsupportedOperationException("GL_arb_shader_storage_buffer_object or OpenGL 4.0 is not supported!");
    }

    @Override
    public void bufferBindFeedback(GL3XBuffer bt, int index) {
        GL30.glBindBufferBase(GL30.GL_TRANSFORM_FEEDBACK_BUFFER, index, bt.bufferId);
    }

    @Override
    public void bufferBindFeedback(GL3XBuffer bt, int index, long offset, long size) {
        GL30.glBindBufferRange(GL30.GL_TRANSFORM_FEEDBACK_BUFFER, index, bt.bufferId, offset, size);
    }

    @Override
    public void bufferBindUniform(GL3XBuffer bt, int index) {
        final GLCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL31 || cap.GL_ARB_uniform_buffer_object) {
            GL30.glBindBufferBase(GL31.GL_UNIFORM_BUFFER, index, bt.bufferId);
        } else {
            throw new UnsupportedOperationException("GL_arb_uniform_buffer_object or OpenGL 3.1 is not supported!");
        }
    }

    @Override
    public void bufferBindUniform(GL3XBuffer bt, int index, long offset, long size) {
        GL30.glBindBufferRange(GL31.GL_UNIFORM_BUFFER, index, bt.bufferId, offset, size);
    }

    @Override
    public int programGetStorageBlockBinding(GL3XProgram pt, String string) {
        throw new UnsupportedOperationException("GL_arb_shader_storage_buffer_object is not supported!");
    }

    @Override
    public int programGetUniformBlockBinding(GL3XProgram pt, String ublockName) {
        if (pt.uniformBindings.containsKey(ublockName)) {
            return pt.uniformBindings.get(ublockName);
        } else {
            return -1;
        }
    }

    @Override
    public void programSetStorageBlockBinding(GL3XProgram pt, String string, int i) {
        throw new UnsupportedOperationException("GL_arb_shader_storage_buffer_object is not supported!");
    }

    @Override
    public void programSetUniformBlockBinding(GL3XProgram pt, String ublockName, int binding) {
        final int ublockIndex = GL31.glGetUniformBlockIndex(pt.programId, ublockName);

        GL31.glUniformBlockBinding(pt.programId, ublockIndex, binding);
        pt.uniformBindings.put(ublockName, binding);
    }

    @Override
    public int shaderGetVersion() {
        final GLCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL33) {
            return 330;
        } else if (cap.OpenGL32) {
            return 150;
        } else if (cap.OpenGL31) {
            return 140;
        } else {
            return 130;
        }
    }

    @Override
    public void applyTweaks(final Tweaks tweak) {
    }

    @Override
    public void blendingDisable() {
        GL11.glDisable(GL11.GL_BLEND);
    }

    @Override
    public void blendingEnable(int rgbEq, int aEq, int rgbFuncSrc, int rgbFuncDst, int aFuncSrc, int aFuncDst) {
        GL11.glEnable(GL11.GL_BLEND);
        GL14.glBlendFuncSeparate(rgbFuncSrc, rgbFuncDst, aFuncSrc, aFuncDst);
        GL20.glBlendEquationSeparate(rgbEq, aEq);
    }

    @Override
    public void bufferAllocate(GL3XBuffer buffer, long size, int usage) {
        final GLCapabilities caps = GL.getCapabilities();

        if (caps.GL_ARB_direct_state_access && ARB_DSA) {
            ARBDirectStateAccess.glNamedBufferData(buffer.bufferId, size, usage);
        } else if (caps.GL_EXT_direct_state_access && EXT_DSA) {
            EXTDirectStateAccess.glNamedBufferDataEXT(buffer.bufferId, size, usage);
        } else if (EXCLUSIVE_CONTEXT) {
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, buffer.bufferId);
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, size, usage);
        } else {
            final int currentBuf = GL11.glGetInteger(GL15.GL_ARRAY_BUFFER_BINDING);

            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, buffer.bufferId);
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, size, usage);
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, currentBuf);
        }
    }

    @Override
    public void bufferAllocateImmutable(GL3XBuffer buffer, long size, int bitflags) {
        final GLCapabilities caps = GL.getCapabilities();

        if (caps.GL_ARB_buffer_storage) {
            if (caps.GL_ARB_direct_state_access && ARB_DSA) {
                ARBDirectStateAccess.glNamedBufferStorage(buffer.bufferId, size, bitflags);
            } else if (caps.GL_EXT_direct_state_access && EXT_DSA) {
                ARBBufferStorage.glNamedBufferStorageEXT(buffer.bufferId, size, bitflags);
            } else if (EXCLUSIVE_CONTEXT) {
                GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, buffer.bufferId);
                ARBBufferStorage.glBufferStorage(GL15.GL_ARRAY_BUFFER, size, bitflags);
            } else {
                final int currentBuf = GL11.glGetInteger(GL15.GL_ARRAY_BUFFER_BINDING);

                GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, buffer.bufferId);
                ARBBufferStorage.glBufferStorage(GL15.GL_ARRAY_BUFFER, size, bitflags);
                GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, currentBuf);
            }
        } else {
            this.bufferAllocate(buffer, size, GL15.GL_DYNAMIC_DRAW);
        }
    }

    @Override
    public void bufferCopyData(GL3XBuffer srcBuffer, long srcOffset, GL3XBuffer dstBuffer, long dstOffset, long size) {
        final GLCapabilities caps = GL.getCapabilities();

        if (caps.GL_ARB_copy_buffer) {
            if (caps.GL_ARB_direct_state_access && ARB_DSA) {
                ARBDirectStateAccess.glCopyNamedBufferSubData(srcBuffer.bufferId, dstBuffer.bufferId, srcOffset, dstOffset, size);
            } else {
                GL15.glBindBuffer(GL31.GL_COPY_READ_BUFFER, srcBuffer.bufferId);
                GL15.glBindBuffer(GL31.GL_COPY_WRITE_BUFFER, dstBuffer.bufferId);
                GL31.glCopyBufferSubData(GL31.GL_COPY_READ_BUFFER, GL31.GL_COPY_WRITE_BUFFER, srcOffset, dstOffset, size);
            }
        } else if (EXCLUSIVE_CONTEXT) {
            final ByteBuffer src = this.bufferMapData(srcBuffer, srcOffset, size, GL30.GL_MAP_READ_BIT);

            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, dstBuffer.bufferId);
            GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, dstOffset, src);

            this.bufferUnmapData(srcBuffer);
        } else {
            final int currentBuf = GL11.glGetInteger(GL15.GL_ARRAY_BUFFER_BINDING);
            final ByteBuffer src = this.bufferMapData(srcBuffer, srcOffset, size, GL30.GL_MAP_READ_BIT);

            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, dstBuffer.bufferId);
            GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, dstOffset, src);
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, currentBuf);

            this.bufferUnmapData(srcBuffer);
        }
    }

    @Override
    public GL3XBuffer bufferCreate() {
        final GLCapabilities caps = GL.getCapabilities();
        final GL3XBuffer buffer = new GL3XBuffer();

        if (caps.GL_ARB_direct_state_access && ARB_DSA) {
            buffer.bufferId = ARBDirectStateAccess.glCreateBuffers();
        } else {
            buffer.bufferId = GL15.glGenBuffers();
        }

        return buffer;
    }

    @Override
    public void bufferDelete(GL3XBuffer buffer) {
        GL15.glDeleteBuffers(buffer.bufferId);
        buffer.bufferId = -1;
    }

    @Override
    public void bufferGetData(GL3XBuffer buffer, long offset, ByteBuffer out) {
        final GLCapabilities caps = GL.getCapabilities();

        if (caps.GL_ARB_direct_state_access && ARB_DSA) {
            ARBDirectStateAccess.glGetNamedBufferSubData(buffer.bufferId, offset, out);
        } else if (caps.GL_EXT_direct_state_access && EXT_DSA) {
            EXTDirectStateAccess.glGetNamedBufferSubDataEXT(buffer.bufferId, offset, out);
        } else if (EXCLUSIVE_CONTEXT) {
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, buffer.bufferId);
            GL15.glGetBufferSubData(GL15.GL_ARRAY_BUFFER, offset, out);
        } else {
            final int currentBuf = GL11.glGetInteger(GL15.GL_ARRAY_BUFFER_BINDING);

            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, buffer.bufferId);
            GL15.glGetBufferSubData(GL15.GL_ARRAY_BUFFER, offset, out);
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, currentBuf);
        }
    }

    @Override
    public int bufferGetParameterI(GL3XBuffer buffer, int paramId) {
        final GLCapabilities caps = GL.getCapabilities();

        if (caps.GL_ARB_direct_state_access && ARB_DSA) {
            return ARBDirectStateAccess.glGetNamedBufferParameteri(buffer.bufferId, paramId);
        } else if (caps.GL_EXT_direct_state_access && EXT_DSA) {
            return EXTDirectStateAccess.glGetNamedBufferParameteriEXT(buffer.bufferId, paramId);
        } else if (EXCLUSIVE_CONTEXT) {
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, buffer.bufferId);

            return GL15.glGetBufferParameteri(GL15.GL_ARRAY_BUFFER, paramId);
        } else {
            final int currentBuf = GL11.glGetInteger(GL15.GL_ARRAY_BUFFER_BINDING);
            final int res;

            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, buffer.bufferId);
            res = GL15.glGetBufferParameteri(GL15.GL_ARRAY_BUFFER, paramId);
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, currentBuf);

            return res;
        }
    }

    @Override
    public void bufferInvalidateData(GL3XBuffer buffer) {
        if (GL.getCapabilities().GL_ARB_invalidate_subdata) {
            ARBInvalidateSubdata.glInvalidateBufferData(buffer.bufferId);
        }
    }

    @Override
    public void bufferInvalidateRange(GL3XBuffer buffer, long offset, long length) {
        if (GL.getCapabilities().GL_ARB_invalidate_subdata) {
            ARBInvalidateSubdata.glInvalidateBufferSubData(buffer.bufferId, offset, length);
        }
    }

    @Override
    public ByteBuffer bufferMapData(GL3XBuffer buffer, long offset, long length, int accessFlags) {
        final GLCapabilities caps = GL.getCapabilities();

        if (caps.GL_ARB_direct_state_access && ARB_DSA) {
            buffer.mapBuffer = ARBDirectStateAccess.glMapNamedBufferRange(buffer.bufferId, offset, length, accessFlags, buffer.mapBuffer);
        } else if (caps.GL_EXT_direct_state_access && EXT_DSA) {
            buffer.mapBuffer = EXTDirectStateAccess.glMapNamedBufferRangeEXT(buffer.bufferId, offset, length, accessFlags, buffer.mapBuffer);
        } else if (EXCLUSIVE_CONTEXT) {
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, buffer.bufferId);
            buffer.mapBuffer = GL30.glMapBufferRange(GL15.GL_ARRAY_BUFFER, offset, length, accessFlags, buffer.mapBuffer);
        } else {
            final int currentBuf = GL11.glGetInteger(GL15.GL_ARRAY_BUFFER_BINDING);

            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, buffer.bufferId);
            buffer.mapBuffer = GL30.glMapBufferRange(GL15.GL_ARRAY_BUFFER, offset, length, accessFlags, buffer.mapBuffer);
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, currentBuf);
        }

        return buffer.mapBuffer;
    }

    @Override
    public void bufferSetData(GL3XBuffer buffer, ByteBuffer data, int usage) {
        final GLCapabilities caps = GL.getCapabilities();

        if (caps.GL_ARB_direct_state_access && ARB_DSA) {
            ARBDirectStateAccess.glNamedBufferData(buffer.bufferId, data, usage);
        } else if (caps.GL_EXT_direct_state_access && EXT_DSA) {
            EXTDirectStateAccess.glNamedBufferDataEXT(buffer.bufferId, data, usage);
        } else if (EXCLUSIVE_CONTEXT) {
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, buffer.bufferId);
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, data, usage);
        } else {
            final int currentBuf = GL11.glGetInteger(GL15.GL_ARRAY_BUFFER_BINDING);

            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, buffer.bufferId);
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, data, usage);
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, currentBuf);
        }
    }

    @Override
    public void bufferUnmapData(GL3XBuffer buffer) {
        final GLCapabilities caps = GL.getCapabilities();

        if (caps.GL_ARB_direct_state_access && ARB_DSA) {
            ARBDirectStateAccess.glUnmapNamedBuffer(buffer.bufferId);
        } else if (caps.GL_EXT_direct_state_access && EXT_DSA) {
            EXTDirectStateAccess.glUnmapNamedBufferEXT(buffer.bufferId);
        } else if (EXCLUSIVE_CONTEXT) {
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, buffer.bufferId);
            GL15.glUnmapBuffer(GL15.GL_ARRAY_BUFFER);
        } else {
            final int currentBuf = GL11.glGetInteger(GL15.GL_ARRAY_BUFFER_BINDING);

            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, buffer.bufferId);
            GL15.glUnmapBuffer(GL15.GL_ARRAY_BUFFER);
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, currentBuf);
        }
    }

    @Override
    public void clear(int bitfield, float red, float green, float blue, float alpha, double depth) {
        GL11.glClearColor(red, green, blue, alpha);
        GL11.glClearDepth(depth);
        GL11.glClear(bitfield);
    }

    @Override
    public void depthTestDisable() {
        GL11.glDisable(GL11.GL_DEPTH_TEST);
    }

    @Override
    public void depthTestEnable(int depthTest) {
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthFunc(depthTest);
    }

    @Override
    public void framebufferAddAttachment(GL3XFramebuffer framebuffer, int attachmentId, GL3XTexture texId, int mipmapLevel) {
        final GLCapabilities caps = GL.getCapabilities();

        if (caps.GL_ARB_direct_state_access && ARB_DSA) {
            ARBDirectStateAccess.glNamedFramebufferTexture(framebuffer.framebufferId, attachmentId, texId.textureId, mipmapLevel);
        } else if (caps.GL_EXT_direct_state_access && EXT_DSA) {
            switch (texId.target) {
                case GL11.GL_TEXTURE_1D:
                    EXTDirectStateAccess.glNamedFramebufferTexture1DEXT(framebuffer.framebufferId, attachmentId, GL11.GL_TEXTURE_1D, texId.textureId, mipmapLevel);
                    break;
                case GL11.GL_TEXTURE_2D:
                    EXTDirectStateAccess.glNamedFramebufferTexture2DEXT(framebuffer.framebufferId, attachmentId, GL11.GL_TEXTURE_2D, texId.textureId, mipmapLevel);
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported texture target: " + texId.target);
            }
        } else if (EXCLUSIVE_CONTEXT) {
            switch (texId.target) {
                case GL11.GL_TEXTURE_1D:
                    GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebuffer.framebufferId);
                    GL30.glFramebufferTexture1D(GL30.GL_FRAMEBUFFER, attachmentId, GL11.GL_TEXTURE_1D, texId.textureId, mipmapLevel);
                    break;
                case GL11.GL_TEXTURE_2D:
                    GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebuffer.framebufferId);
                    GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, attachmentId, GL11.GL_TEXTURE_2D, texId.textureId, mipmapLevel);
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported texture target: " + texId.target);
            }
        } else {
            final int currentFb;

            switch (texId.target) {
                case GL11.GL_TEXTURE_1D:
                    currentFb = GL11.glGetInteger(GL30.GL_FRAMEBUFFER_BINDING);
                    GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebuffer.framebufferId);
                    GL30.glFramebufferTexture1D(GL30.GL_FRAMEBUFFER, attachmentId, GL11.GL_TEXTURE_1D, texId.textureId, mipmapLevel);
                    GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, currentFb);
                    break;
                case GL11.GL_TEXTURE_2D:
                    currentFb = GL11.glGetInteger(GL30.GL_FRAMEBUFFER_BINDING);
                    GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebuffer.framebufferId);
                    GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, attachmentId, GL11.GL_TEXTURE_2D, texId.textureId, mipmapLevel);
                    GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, currentFb);
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported texture target: " + texId.target);
            }
        }
    }

    @Override
    public void framebufferAddRenderbuffer(GL3XFramebuffer framebuffer, int attachmentId, GL3XRenderbuffer renderbuffer) {
        final GLCapabilities caps = GL.getCapabilities();

        if (caps.GL_ARB_direct_state_access && ARB_DSA) {
            ARBDirectStateAccess.glNamedFramebufferRenderbuffer(framebuffer.framebufferId, attachmentId, GL30.GL_RENDERBUFFER, renderbuffer.renderbufferId);
        } else if (caps.GL_EXT_direct_state_access && EXT_DSA) {
            EXTDirectStateAccess.glNamedFramebufferRenderbufferEXT(framebuffer.framebufferId, attachmentId, GL30.GL_RENDERBUFFER, renderbuffer.renderbufferId);
        } else if (EXCLUSIVE_CONTEXT) {
            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebuffer.framebufferId);
            GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, attachmentId, GL30.GL_RENDERBUFFER, renderbuffer.renderbufferId);
        } else {
            final int currentFb = GL11.glGetInteger(GL30.GL_FRAMEBUFFER_BINDING);

            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebuffer.framebufferId);
            GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, attachmentId, GL30.GL_RENDERBUFFER, renderbuffer.renderbufferId);
            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, currentFb);
        }
    }

    @Override
    public void framebufferBind(GL3XFramebuffer framebuffer, IntBuffer attachments) {
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebuffer.framebufferId);

        if (attachments != null) {
            GL20.glDrawBuffers(attachments);
        }
    }

    @Override
    public void framebufferBlit(GL3XFramebuffer srcFb, int srcX0, int srcY0, int srcX1, int srcY1, GL3XFramebuffer dstFb, int dstX0, int dstY0, int dstX1, int dstY1, int bitfield, int filter) {
        final GLCapabilities caps = GL.getCapabilities();

        if (caps.GL_ARB_direct_state_access && ARB_DSA) {
            ARBDirectStateAccess.glBlitNamedFramebuffer(srcFb.framebufferId, dstFb.framebufferId, srcX0, srcY0, srcX1, srcY1, dstX0, dstY0, dstX1, dstY1, bitfield, filter);
        } else {
            GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, srcFb.framebufferId);
            GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, dstFb.framebufferId);

            GL30.glBlitFramebuffer(srcX0, srcY0, srcX1, srcY1, dstX0, dstY0, dstX1, dstY1, bitfield, filter);
        }
    }

    @Override
    public GL3XFramebuffer framebufferCreate() {
        final GLCapabilities caps = GL.getCapabilities();
        final GL3XFramebuffer fb = new GL3XFramebuffer();

        if (caps.GL_ARB_direct_state_access && ARB_DSA) {
            fb.framebufferId = ARBDirectStateAccess.glCreateFramebuffers();
        } else {
            fb.framebufferId = GL30.glGenFramebuffers();
        }

        return fb;
    }

    @Override
    public void framebufferDelete(GL3XFramebuffer framebuffer) {
        GL30.glDeleteFramebuffers(framebuffer.framebufferId);
        framebuffer.framebufferId = -1;
    }

    @Override
    public GL3XFramebuffer framebufferGetDefault() {
        final GL3XFramebuffer fb = new GL3XFramebuffer();
        fb.framebufferId = 0;
        return fb;
    }

    @Override
    public void framebufferGetPixels(GL3XFramebuffer framebuffer, int x, int y, int width, int height, int format, int type, GL3XBuffer dstBuffer) {
        if (EXCLUSIVE_CONTEXT) {
            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebuffer.framebufferId);
            GL15.glBindBuffer(GL21.GL_PIXEL_PACK_BUFFER, dstBuffer.bufferId);
            GL11.glReadPixels(x, y, width, height, format, type, 0L);
            GL15.glBindBuffer(GL21.GL_PIXEL_PACK_BUFFER, 0);
        } else {
            final int currentFb = GL11.glGetInteger(GL30.GL_FRAMEBUFFER_BINDING);

            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebuffer.framebufferId);
            GL15.glBindBuffer(GL21.GL_PIXEL_PACK_BUFFER, dstBuffer.bufferId);
            GL11.glReadPixels(x, y, width, height, format, type, 0L);
            GL15.glBindBuffer(GL21.GL_PIXEL_PACK_BUFFER, 0);
            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, currentFb);
        }
    }

    @Override
    public void framebufferGetPixels(GL3XFramebuffer framebuffer, int x, int y, int width, int height, int format, int type, ByteBuffer dstBuffer) {
        if (EXCLUSIVE_CONTEXT) {
            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebuffer.framebufferId);
            GL11.glReadPixels(x, y, width, height, format, type, dstBuffer);
        } else {
            final int currentFb = GL11.glGetInteger(GL30.GL_FRAMEBUFFER_BINDING);

            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebuffer.framebufferId);
            GL11.glReadPixels(x, y, width, height, format, type, dstBuffer);
            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, currentFb);
        }
    }

    @Override
    public boolean framebufferIsComplete(GL3XFramebuffer framebuffer) {
        final GLCapabilities caps = GL.getCapabilities();

        if (caps.GL_ARB_direct_state_access && ARB_DSA) {
            return ARBDirectStateAccess.glCheckNamedFramebufferStatus(framebuffer.framebufferId, GL30.GL_FRAMEBUFFER) == GL30.GL_FRAMEBUFFER_COMPLETE;
        } else if (caps.GL_EXT_direct_state_access && EXT_DSA) {
            return EXTDirectStateAccess.glCheckNamedFramebufferStatusEXT(framebuffer.framebufferId, GL30.GL_FRAMEBUFFER) == GL30.GL_FRAMEBUFFER_COMPLETE;
        } else if (EXCLUSIVE_CONTEXT) {
            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebuffer.framebufferId);

            return GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) == GL30.GL_FRAMEBUFFER_COMPLETE;
        } else {
            final int currentFb = GL11.glGetInteger(GL30.GL_FRAMEBUFFER_BINDING);
            final int res;

            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebuffer.framebufferId);
            res = GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER);
            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, currentFb);

            return res == GL30.GL_FRAMEBUFFER_COMPLETE;
        }
    }

    @Override
    public void maskApply(boolean red, boolean green, boolean blue, boolean alpha, boolean depth, int stencil) {
        GL11.glColorMask(red, green, blue, alpha);
        GL11.glDepthMask(depth);
        GL11.glStencilMask(stencil);
    }

    @Override
    public void polygonSetParameters(float pointSize, float lineWidth, int frontFace, int cullFace, int polygonMode, float offsetFactor, float offsetUnits) {
        GL11.glPointSize(pointSize);
        GL11.glLineWidth(lineWidth);
        GL11.glFrontFace(frontFace);

        if (cullFace == 0) {
            GL11.glDisable(GL11.GL_CULL_FACE);
        } else {
            GL11.glEnable(GL11.GL_CULL_FACE);
            GL11.glCullFace(cullFace);
        }

        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, polygonMode);
        GL11.glPolygonOffset(offsetFactor, offsetUnits);
    }

    @Override
    public GL3XProgram programCreate() {
        GL3XProgram program = new GL3XProgram();
        program.programId = GL20.glCreateProgram();
        return program;
    }

    @Override
    public void programDelete(GL3XProgram program) {
        GL20.glDeleteProgram(program.programId);
        program.programId = -1;
    }

    @Override
    public void programDispatchCompute(GL3XProgram program, int numX, int numY, int numZ) {
        throw new UnsupportedOperationException("Compute shaders are not supported!");
    }

    @Override
    public int programGetUniformLocation(GL3XProgram program, String name) {
        return GL20.glGetUniformLocation(program.programId, name);
    }

    @Override
    public void programLinkShaders(GL3XProgram program, Shader[] shaders) {
        for (Shader shader : shaders) {
            GL20.glAttachShader(program.programId, ((GL3XShader) shader).shaderId);
        }

        GL20.glLinkProgram(program.programId);

        for (Shader shader : shaders) {
            GL20.glDetachShader(program.programId, ((GL3XShader) shader).shaderId);
        }
    }

    @Override
    public void programSetAttribLocation(GL3XProgram program, int index, String name) {
        GL20.glBindAttribLocation(program.programId, index, name);
    }

    @Override
    public void programSetFeedbackVaryings(GL3XProgram program, String[] varyings) {
        GL30.glTransformFeedbackVaryings(program.programId, varyings, GL30.GL_SEPARATE_ATTRIBS);
    }

    @Override
    public void programSetUniformD(GL3XProgram program, int uLoc, double[] value) {
        final GLCapabilities cap = GL.getCapabilities();

        if (cap.GL_ARB_separate_shader_objects) {
            if (cap.GL_ARB_gpu_shader_fp64) {
                switch (value.length) {
                    case 1:
                        ARBSeparateShaderObjects.glProgramUniform1d(program.programId, uLoc, value[0]);
                        break;
                    case 2:
                        ARBSeparateShaderObjects.glProgramUniform2d(program.programId, uLoc, value[0], value[1]);
                        break;
                    case 3:
                        ARBSeparateShaderObjects.glProgramUniform3d(program.programId, uLoc, value[0], value[1], value[2]);
                        break;
                    case 4:
                        ARBSeparateShaderObjects.glProgramUniform4d(program.programId, uLoc, value[0], value[1], value[2], value[3]);
                        break;
                    default:
                        throw new UnsupportedOperationException("Unsupported vector size: " + value.length);
                }
            } else {
                switch (value.length) {
                    case 1:
                        ARBSeparateShaderObjects.glProgramUniform1f(program.programId, uLoc, (float) value[0]);
                        break;
                    case 2:
                        ARBSeparateShaderObjects.glProgramUniform2f(program.programId, uLoc, (float) value[0], (float) value[1]);
                        break;
                    case 3:
                        ARBSeparateShaderObjects.glProgramUniform3f(program.programId, uLoc, (float) value[0], (float) value[1], (float) value[2]);
                        break;
                    case 4:
                        ARBSeparateShaderObjects.glProgramUniform4f(program.programId, uLoc, (float) value[0], (float) value[1], (float) value[2], (float) value[3]);
                        break;
                    default:
                        throw new UnsupportedOperationException("Unsupported vector size: " + value.length);
                }
            }
        } else if (EXCLUSIVE_CONTEXT) {
            if (cap.GL_ARB_gpu_shader_fp64) {
                GL20.glUseProgram(program.programId);

                switch (value.length) {
                    case 1:
                        ARBGPUShaderFP64.glUniform1d(uLoc, value[0]);
                        break;
                    case 2:
                        ARBGPUShaderFP64.glUniform2d(uLoc, value[0], value[1]);
                        break;
                    case 3:
                        ARBGPUShaderFP64.glUniform3d(uLoc, value[0], value[1], value[2]);
                        break;
                    case 4:
                        ARBGPUShaderFP64.glUniform4d(uLoc, value[0], value[1], value[2], value[3]);
                        break;
                    default:
                        throw new UnsupportedOperationException("Unsupported vector size: " + value.length);
                }
            } else {
                GL20.glUseProgram(program.programId);

                switch (value.length) {
                    case 1:
                        GL20.glUniform1f(uLoc, (float) value[0]);
                        break;
                    case 2:
                        GL20.glUniform2f(uLoc, (float) value[0], (float) value[1]);
                        break;
                    case 3:
                        GL20.glUniform3f(uLoc, (float) value[0], (float) value[1], (float) value[2]);
                        break;
                    case 4:
                        GL20.glUniform4f(uLoc, (float) value[0], (float) value[1], (float) value[2], (float) value[3]);
                        break;
                    default:
                        throw new UnsupportedOperationException("Unsupported vector size: " + value.length);
                }
            }
        } else {
            if (cap.GL_ARB_gpu_shader_fp64) {
                final int currentProg = GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM);

                GL20.glUseProgram(program.programId);

                switch (value.length) {
                    case 1:
                        ARBGPUShaderFP64.glUniform1d(uLoc, value[0]);
                        break;
                    case 2:
                        ARBGPUShaderFP64.glUniform2d(uLoc, value[0], value[1]);
                        break;
                    case 3:
                        ARBGPUShaderFP64.glUniform3d(uLoc, value[0], value[1], value[2]);
                        break;
                    case 4:
                        ARBGPUShaderFP64.glUniform4d(uLoc, value[0], value[1], value[2], value[3]);
                        break;
                    default:
                        throw new UnsupportedOperationException("Unsupported vector size: " + value.length);
                }

                GL20.glUseProgram(currentProg);
            } else {
                final int currentProg = GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM);

                switch (value.length) {
                    case 1:
                        GL20.glUniform1f(uLoc, (float) value[0]);
                        break;
                    case 2:
                        GL20.glUniform2f(uLoc, (float) value[0], (float) value[1]);
                        break;
                    case 3:
                        GL20.glUniform3f(uLoc, (float) value[0], (float) value[1], (float) value[2]);
                        break;
                    case 4:
                        GL20.glUniform4f(uLoc, (float) value[0], (float) value[1], (float) value[2], (float) value[3]);
                        break;
                    default:
                        throw new UnsupportedOperationException("Unsupported vector size: " + value.length);
                }

                GL20.glUseProgram(currentProg);
            }
        }
    }

    @Override
    public void programSetUniformF(GL3XProgram program, int uLoc, float[] value) {
        final GLCapabilities caps = GL.getCapabilities();

        if (caps.GL_ARB_separate_shader_objects) {
            switch (value.length) {
                case 1:
                    ARBSeparateShaderObjects.glProgramUniform1f(program.programId, uLoc, value[0]);
                    break;
                case 2:
                    ARBSeparateShaderObjects.glProgramUniform2f(program.programId, uLoc, value[0], value[1]);
                    break;
                case 3:
                    ARBSeparateShaderObjects.glProgramUniform3f(program.programId, uLoc, value[0], value[1], value[2]);
                    break;
                case 4:
                    ARBSeparateShaderObjects.glProgramUniform4f(program.programId, uLoc, value[0], value[1], value[2], value[3]);
                    break;
            }
        } else if (EXCLUSIVE_CONTEXT) {
            GL20.glUseProgram(program.programId);

            switch (value.length) {
                case 1:
                    GL20.glUniform1f(uLoc, value[0]);
                    break;
                case 2:
                    GL20.glUniform2f(uLoc, value[0], value[1]);
                    break;
                case 3:
                    GL20.glUniform3f(uLoc, value[0], value[1], value[2]);
                    break;
                case 4:
                    GL20.glUniform4f(uLoc, value[0], value[1], value[2], value[3]);
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported vector size: " + value.length);
            }
        } else {
            final int currentProg = GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM);

            GL20.glUseProgram(program.programId);

            switch (value.length) {
                case 1:
                    GL20.glUniform1f(uLoc, value[0]);
                    break;
                case 2:
                    GL20.glUniform2f(uLoc, value[0], value[1]);
                    break;
                case 3:
                    GL20.glUniform3f(uLoc, value[0], value[1], value[2]);
                    break;
                case 4:
                    GL20.glUniform4f(uLoc, value[0], value[1], value[2], value[3]);
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported vector size: " + value.length);
            }

            GL20.glUseProgram(currentProg);
        }
    }

    @Override
    public void programSetUniformI(GL3XProgram program, int uLoc, int[] value) {
        final GLCapabilities caps = GL.getCapabilities();

        if (caps.GL_ARB_separate_shader_objects) {
            switch (value.length) {
                case 1:
                    ARBSeparateShaderObjects.glProgramUniform1i(program.programId, uLoc, value[0]);
                    break;
                case 2:
                    ARBSeparateShaderObjects.glProgramUniform2i(program.programId, uLoc, value[0], value[1]);
                    break;
                case 3:
                    ARBSeparateShaderObjects.glProgramUniform3i(program.programId, uLoc, value[0], value[1], value[2]);
                    break;
                case 4:
                    ARBSeparateShaderObjects.glProgramUniform4i(program.programId, uLoc, value[0], value[1], value[2], value[3]);
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported uniform vector size: " + value.length);
            }
        } else if (EXCLUSIVE_CONTEXT) {
            GL20.glUseProgram(program.programId);

            switch (value.length) {
                case 1:
                    GL20.glUniform1i(uLoc, value[0]);
                    break;
                case 2:
                    GL20.glUniform2i(uLoc, value[0], value[1]);
                    break;
                case 3:
                    GL20.glUniform3i(uLoc, value[0], value[1], value[2]);
                    break;
                case 4:
                    GL20.glUniform4i(uLoc, value[0], value[1], value[2], value[3]);
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported uniform vector size: " + value.length);
            }
        } else {
            final int currentProg = GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM);

            GL20.glUseProgram(program.programId);

            switch (value.length) {
                case 1:
                    GL20.glUniform1i(uLoc, value[0]);
                    break;
                case 2:
                    GL20.glUniform2i(uLoc, value[0], value[1]);
                    break;
                case 3:
                    GL20.glUniform3i(uLoc, value[0], value[1], value[2]);
                    break;
                case 4:
                    GL20.glUniform4i(uLoc, value[0], value[1], value[2], value[3]);
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported uniform vector size: " + value.length);
            }

            GL20.glUseProgram(currentProg);
        }
    }

    @Override
    public void programSetUniformMatD(GL3XProgram program, int uLoc, DoubleBuffer mat) {
        final GLCapabilities cap = GL.getCapabilities();

        if (cap.GL_ARB_separate_shader_objects) {
            if (cap.GL_ARB_gpu_shader_fp64) {
                switch (mat.remaining()) {
                    case 4:
                        ARBSeparateShaderObjects.glProgramUniformMatrix2dv(program.programId, uLoc, false, mat);
                        break;
                    case 9:
                        ARBSeparateShaderObjects.glProgramUniformMatrix3dv(program.programId, uLoc, false, mat);
                        break;
                    case 16:
                        ARBSeparateShaderObjects.glProgramUniformMatrix4dv(program.programId, uLoc, false, mat);
                        break;
                    default:
                        throw new UnsupportedOperationException("Unsupported matrix size: " + mat.limit());
                }
            } else {
                final int size = mat.remaining();
                final FloatBuffer fmat = MemoryUtil.memAllocFloat(size);

                for (int i = 0; i < size; i++) {
                    fmat.put((float) mat.get());
                }

                fmat.flip();

                switch (size) {
                    case 4:
                        ARBSeparateShaderObjects.glProgramUniformMatrix2fv(program.programId, uLoc, false, fmat);
                        MemoryUtil.memFree(fmat);
                        break;
                    case 9:
                        ARBSeparateShaderObjects.glProgramUniformMatrix3fv(program.programId, uLoc, false, fmat);
                        MemoryUtil.memFree(fmat);
                        break;
                    case 16:
                        ARBSeparateShaderObjects.glProgramUniformMatrix4fv(program.programId, uLoc, false, fmat);
                        MemoryUtil.memFree(fmat);
                        break;
                    default:
                        MemoryUtil.memFree(fmat);
                        throw new UnsupportedOperationException("Unsupported matrix size: " + mat.limit());
                }
            }
        } else if (EXCLUSIVE_CONTEXT) {
            if (cap.GL_ARB_gpu_shader_fp64) {
                GL20.glUseProgram(program.programId);

                switch (mat.remaining()) {
                    case 4:
                        ARBGPUShaderFP64.glUniformMatrix2dv(uLoc, false, mat);
                        break;
                    case 9:
                        ARBGPUShaderFP64.glUniformMatrix3dv(uLoc, false, mat);
                        break;
                    case 16:
                        ARBGPUShaderFP64.glUniformMatrix4dv(uLoc, false, mat);
                        break;
                    default:
                        throw new UnsupportedOperationException("Unsupported matrix size: " + mat.limit());
                }
            } else {
                final int size = mat.remaining();
                final FloatBuffer fmat = MemoryUtil.memAllocFloat(size);

                for (int i = 0; i < size; i++) {
                    fmat.put((float) mat.get());
                }

                fmat.flip();

                final int currentProg;
                switch (mat.remaining()) {
                    case 4:
                        currentProg = GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM);
                        GL20.glUseProgram(program.programId);
                        GL20.glUniformMatrix2fv(uLoc, false, fmat);
                        GL20.glUseProgram(currentProg);
                        MemoryUtil.memFree(fmat);
                        break;
                    case 9:
                        currentProg = GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM);
                        GL20.glUseProgram(program.programId);
                        GL20.glUniformMatrix3fv(uLoc, false, fmat);
                        GL20.glUseProgram(currentProg);
                        MemoryUtil.memFree(fmat);
                        break;
                    case 16:
                        currentProg = GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM);
                        GL20.glUseProgram(program.programId);
                        GL20.glUniformMatrix4fv(uLoc, false, fmat);
                        GL20.glUseProgram(currentProg);
                        MemoryUtil.memFree(fmat);
                        break;
                    default:
                        MemoryUtil.memFree(fmat);
                        throw new UnsupportedOperationException("Unsupported matrix size: " + mat.limit());
                }
            }
        }
    }

    @Override
    public void programSetUniformMatF(GL3XProgram program, int uLoc, FloatBuffer mat) {
        final GLCapabilities caps = GL.getCapabilities();

        if (caps.GL_ARB_separate_shader_objects) {
            switch (mat.remaining()) {
                case 4:
                    ARBSeparateShaderObjects.glProgramUniformMatrix2fv(program.programId, uLoc, false, mat);
                    break;
                case 9:
                    ARBSeparateShaderObjects.glProgramUniformMatrix3fv(program.programId, uLoc, false, mat);
                    break;
                case 16:
                    ARBSeparateShaderObjects.glProgramUniformMatrix4fv(program.programId, uLoc, false, mat);
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported matrix size: " + mat.limit());
            }
        } else if (EXCLUSIVE_CONTEXT) {
            GL20.glUseProgram(program.programId);

            switch (mat.remaining()) {
                case 4:
                    ARBSeparateShaderObjects.glProgramUniformMatrix2fv(program.programId, uLoc, false, mat);
                    break;
                case 9:
                    ARBSeparateShaderObjects.glProgramUniformMatrix3fv(program.programId, uLoc, false, mat);
                    break;
                case 16:
                    ARBSeparateShaderObjects.glProgramUniformMatrix4fv(program.programId, uLoc, false, mat);
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported matrix size: " + mat.limit());
            }
        } else {
            final int currentProg = GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM);

            GL20.glUseProgram(program.programId);

            switch (mat.remaining()) {
                case 4:
                    ARBSeparateShaderObjects.glProgramUniformMatrix2fv(program.programId, uLoc, false, mat);
                    break;
                case 9:
                    ARBSeparateShaderObjects.glProgramUniformMatrix3fv(program.programId, uLoc, false, mat);
                    break;
                case 16:
                    ARBSeparateShaderObjects.glProgramUniformMatrix4fv(program.programId, uLoc, false, mat);
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported matrix size: " + mat.limit());
            }

            GL20.glUseProgram(currentProg);
        }
    }

    @Override
    public void programUse(GL3XProgram program) {
        GL20.glUseProgram(program.programId);
    }

    @Override
    public GL3XRenderbuffer renderbufferCreate(int internalFormat, int width, int height) {
        final GLCapabilities caps = GL.getCapabilities();
        final GL3XRenderbuffer renderbuffer = new GL3XRenderbuffer();

        if (caps.GL_ARB_direct_state_access && ARB_DSA) {
            renderbuffer.renderbufferId = ARBDirectStateAccess.glCreateRenderbuffers();

            ARBDirectStateAccess.glNamedRenderbufferStorage(GL30.GL_RENDERBUFFER, internalFormat, width, height);
        } else {
            renderbuffer.renderbufferId = GL30.glGenRenderbuffers();

            GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, renderbuffer.renderbufferId);
            GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, internalFormat, width, height);
        }

        return renderbuffer;
    }

    @Override
    public void renderbufferDelete(GL3XRenderbuffer renderbuffer) {
        GL30.glDeleteRenderbuffers(renderbuffer.renderbufferId);
        renderbuffer.renderbufferId = -1;
    }

    @Override
    public void samplerBind(int unit, GL3XSampler sampler) {
        if (GL.getCapabilities().GL_ARB_sampler_objects) {
            ARBSamplerObjects.glBindSampler(unit, sampler.samplerId);
        } else {
            throw new UnsupportedOperationException("ARB_sampler_objects is not supported!");
        }
    }

    @Override
    public GL3XSampler samplerCreate() {
        final GL3XSampler sampler = new GL3XSampler();

        if (GL.getCapabilities().GL_ARB_sampler_objects) {
            sampler.samplerId = ARBSamplerObjects.glGenSamplers();
        } else {
            sampler.samplerId = -1;
        }

        return sampler;
    }

    @Override
    public void samplerDelete(GL3XSampler sampler) {
        if (GL.getCapabilities().GL_ARB_sampler_objects) {
            ARBSamplerObjects.glDeleteSamplers(sampler.samplerId);
            sampler.samplerId = -1;
        } else {
            throw new UnsupportedOperationException("ARB_sampler_objects is not supported!");
        }
    }

    @Override
    public void samplerSetParameter(GL3XSampler sampler, int param, int value) {
        if (GL.getCapabilities().GL_ARB_sampler_objects) {
            ARBSamplerObjects.glSamplerParameteri(sampler.samplerId, param, value);
        } else {
            throw new UnsupportedOperationException("ARB_sampler_objects is not supported!");
        }
    }

    @Override
    public void samplerSetParameter(GL3XSampler sampler, int param, float value) {
        if (GL.getCapabilities().GL_ARB_sampler_objects) {
            ARBSamplerObjects.glSamplerParameterf(sampler.samplerId, param, value);
        } else {
            throw new UnsupportedOperationException("ARB_sampler_objects is not supported!");
        }
    }

    @Override
    public void scissorTestDisable() {
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    @Override
    public void scissorTestEnable(int left, int bottom, int width, int height) {
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(left, bottom, width, height);
    }

    @Override
    public GL3XShader shaderCompile(int type, String source) {
        final GL3XShader shader = new GL3XShader();

        shader.shaderId = GL20.glCreateShader(type);
        GL20.glShaderSource(shader.shaderId, source);
        GL20.glCompileShader(shader.shaderId);
        return shader;
    }

    @Override
    public void shaderDelete(GL3XShader shader) {
        GL20.glDeleteShader(shader.shaderId);
        shader.shaderId = -1;
    }

    @Override
    public String shaderGetInfoLog(GL3XShader shader) {
        return GL20.glGetShaderInfoLog(shader.shaderId);
    }

    @Override
    public int shaderGetParameterI(GL3XShader shader, int pName) {
        return GL20.glGetShaderi(shader.shaderId, pName);
    }

    @Override
    public GL3XTexture textureAllocate(int mipmaps, int internalFormat, int width, int height, int depth, int dataType) {
        final int target;

        if (width < 1 || height < 1 || depth < 1) {
            throw new IllegalArgumentException("Invalid dimensions!");
        } else if (width >= 1 && height == 1 && depth == 1) {
            target = GL11.GL_TEXTURE_1D;
        } else if (width >= 1 && height > 1 && depth == 1) {
            target = GL11.GL_TEXTURE_2D;
        } else if (width >= 1 && height >= 1 && depth > 1) {
            target = GL12.GL_TEXTURE_3D;
        } else {
            throw new IllegalArgumentException("Invalid dimensions!");
        }

        final GLCapabilities caps = GL.getCapabilities();
        final GL3XTexture texture = new GL3XTexture();

        if (caps.GL_ARB_direct_state_access && ARB_DSA && caps.GL_ARB_texture_storage) {
            texture.textureId = ARBDirectStateAccess.glCreateTextures(target);
            texture.target = target;
            texture.internalFormat = internalFormat;

            ARBDirectStateAccess.glTextureParameteri(texture.textureId, GL12.GL_TEXTURE_BASE_LEVEL, 0);
            ARBDirectStateAccess.glTextureParameteri(texture.textureId, GL12.GL_TEXTURE_MAX_LEVEL, mipmaps - 1);

            switch (target) {
                case GL11.GL_TEXTURE_1D:
                    ARBDirectStateAccess.glTextureStorage1D(texture.textureId, mipmaps, internalFormat, width);
                    break;
                case GL11.GL_TEXTURE_2D:
                    ARBDirectStateAccess.glTextureStorage2D(texture.textureId, mipmaps, internalFormat, width, height);
                    break;
                case GL12.GL_TEXTURE_3D:
                    ARBDirectStateAccess.glTextureStorage3D(texture.textureId, mipmaps, internalFormat, width, height, depth);
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported texture target: " + target);
            }
        } else if (caps.GL_EXT_direct_state_access && EXT_DSA && caps.GL_ARB_texture_storage) {
            texture.textureId = GL11.glGenTextures();
            texture.target = target;
            texture.internalFormat = internalFormat;

            EXTDirectStateAccess.glTextureParameteriEXT(texture.textureId, texture.target, GL12.GL_TEXTURE_BASE_LEVEL, 0);
            EXTDirectStateAccess.glTextureParameteriEXT(texture.textureId, texture.target, GL12.GL_TEXTURE_MAX_LEVEL, mipmaps - 1);

            switch (target) {
                case GL11.GL_TEXTURE_1D:
                    ARBTextureStorage.glTextureStorage1DEXT(texture.textureId, GL11.GL_TEXTURE_1D, depth, internalFormat, width);
                    break;
                case GL11.GL_TEXTURE_2D:
                    ARBTextureStorage.glTextureStorage2DEXT(texture.textureId, GL11.GL_TEXTURE_2D, depth, internalFormat, width, height);
                    break;
                case GL12.GL_TEXTURE_3D:
                    ARBTextureStorage.glTextureStorage3DEXT(texture.textureId, GL12.GL_TEXTURE_3D, depth, internalFormat, width, height, depth);
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported texture target: " + target);
            }
        } else if (EXCLUSIVE_CONTEXT) {
            texture.textureId = GL11.glGenTextures();
            texture.target = target;
            texture.internalFormat = internalFormat;

            GL11.glBindTexture(target, texture.textureId);
            GL11.glTexParameteri(target, GL12.GL_TEXTURE_BASE_LEVEL, 0);
            GL11.glTexParameteri(target, GL12.GL_TEXTURE_MAX_LEVEL, mipmaps - 1);

            if (caps.GL_ARB_texture_storage) {
                switch (target) {
                    case GL11.GL_TEXTURE_1D:
                        ARBTextureStorage.glTexStorage1D(GL11.GL_TEXTURE_1D, mipmaps, internalFormat, width);
                        break;
                    case GL11.GL_TEXTURE_2D:
                        ARBTextureStorage.glTexStorage2D(GL11.GL_TEXTURE_2D, mipmaps, internalFormat, width, height);
                        break;
                    case GL12.GL_TEXTURE_3D:
                        ARBTextureStorage.glTexStorage3D(GL12.GL_TEXTURE_3D, mipmaps, internalFormat, width, height, depth);
                        break;
                    default:
                        throw new UnsupportedOperationException("Unsupported texture target: " + target);
                }
            } else {
                final int format = guessFormat(internalFormat);

                switch (target) {
                    case GL11.GL_TEXTURE_1D:
                        for (int i = 0; i < mipmaps; i++) {
                            GL11.glTexImage1D(GL11.GL_TEXTURE_1D, i, internalFormat, width, 0, format, dataType, 0);
                            width = Math.max(1, width / 2);
                        }
                        break;
                    case GL11.GL_TEXTURE_2D:
                        for (int i = 0; i < mipmaps; i++) {
                            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, i, internalFormat, width, height, 0, format, dataType, 0);
                            width = Math.max(1, width / 2);
                            height = Math.max(1, height / 2);
                        }
                        break;
                    case GL12.GL_TEXTURE_3D:
                        for (int i = 0; i < mipmaps; i++) {
                            GL12.glTexImage3D(GL12.GL_TEXTURE_3D, i, internalFormat, width, height, depth, 0, format, dataType, 0);
                            width = Math.max(1, width / 2);
                            height = Math.max(1, height / 2);
                            depth = Math.max(1, depth / 2);
                        }
                        break;
                    default:
                        throw new UnsupportedOperationException("Unsupported texture target: " + target);
                }
            }
        } else {
            texture.textureId = GL11.glGenTextures();
            texture.target = target;
            texture.internalFormat = internalFormat;

            final int currentTex;

            if (caps.GL_ARB_texture_storage) {
                switch (target) {
                    case GL11.GL_TEXTURE_1D:
                        currentTex = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_1D);
                        GL11.glBindTexture(GL11.GL_TEXTURE_1D, texture.textureId);
                        GL11.glTexParameteri(GL11.GL_TEXTURE_1D, GL12.GL_TEXTURE_BASE_LEVEL, 0);
                        GL11.glTexParameteri(GL11.GL_TEXTURE_1D, GL12.GL_TEXTURE_MAX_LEVEL, mipmaps - 1);
                        ARBTextureStorage.glTexStorage1D(GL11.GL_TEXTURE_1D, mipmaps, internalFormat, width);
                        GL11.glBindTexture(GL11.GL_TEXTURE_1D, currentTex);
                        break;
                    case GL11.GL_TEXTURE_2D:
                        currentTex = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
                        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.textureId);
                        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_BASE_LEVEL, 0);
                        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_MAX_LEVEL, mipmaps - 1);
                        ARBTextureStorage.glTexStorage2D(GL11.GL_TEXTURE_2D, mipmaps, internalFormat, width, height);
                        GL11.glBindTexture(GL11.GL_TEXTURE_2D, currentTex);
                        break;
                    case GL12.GL_TEXTURE_3D:
                        currentTex = GL11.glGetInteger(GL12.GL_TEXTURE_BINDING_3D);
                        GL11.glBindTexture(GL12.GL_TEXTURE_3D, texture.textureId);
                        GL11.glTexParameteri(GL12.GL_TEXTURE_3D, GL12.GL_TEXTURE_BASE_LEVEL, 0);
                        GL11.glTexParameteri(GL12.GL_TEXTURE_3D, GL12.GL_TEXTURE_MAX_LEVEL, mipmaps - 1);
                        ARBTextureStorage.glTexStorage3D(GL12.GL_TEXTURE_3D, mipmaps, internalFormat, width, height, depth);
                        GL11.glBindTexture(GL12.GL_TEXTURE_3D, currentTex);
                        break;
                    default:
                        throw new UnsupportedOperationException("Unsupported texture target: " + target);
                }
            } else {
                final int format = guessFormat(internalFormat);

                switch (target) {
                    case GL11.GL_TEXTURE_1D:
                        currentTex = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_1D);
                        GL11.glBindTexture(GL11.GL_TEXTURE_1D, texture.textureId);
                        GL11.glTexParameteri(GL11.GL_TEXTURE_1D, GL12.GL_TEXTURE_BASE_LEVEL, 0);
                        GL11.glTexParameteri(GL11.GL_TEXTURE_1D, GL12.GL_TEXTURE_MAX_LEVEL, mipmaps - 1);

                        for (int i = 0; i < mipmaps; i++) {
                            GL11.glTexImage1D(target, i, internalFormat, width, 0, format, dataType, 0);
                            width = Math.max(1, width / 2);
                        }

                        GL11.glBindTexture(GL11.GL_TEXTURE_1D, currentTex);
                        break;
                    case GL11.GL_TEXTURE_2D:
                        currentTex = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
                        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.textureId);
                        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_BASE_LEVEL, 0);
                        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_MAX_LEVEL, mipmaps - 1);

                        for (int i = 0; i < mipmaps; i++) {
                            GL11.glTexImage2D(target, i, internalFormat, width, height, 0, format, dataType, 0);
                            width = Math.max(1, width / 2);
                            height = Math.max(1, height / 2);
                        }

                        GL11.glBindTexture(GL11.GL_TEXTURE_2D, currentTex);
                        break;
                    case GL12.GL_TEXTURE_3D:
                        currentTex = GL11.glGetInteger(GL12.GL_TEXTURE_BINDING_3D);
                        GL11.glBindTexture(GL12.GL_TEXTURE_3D, texture.textureId);
                        GL11.glTexParameteri(GL12.GL_TEXTURE_3D, GL12.GL_TEXTURE_BASE_LEVEL, 0);
                        GL11.glTexParameteri(GL12.GL_TEXTURE_3D, GL12.GL_TEXTURE_MAX_LEVEL, mipmaps - 1);

                        for (int i = 0; i < mipmaps; i++) {
                            GL12.glTexImage3D(target, i, internalFormat, width, height, depth, 0, format, dataType, 0);
                            width = Math.max(1, width / 2);
                            height = Math.max(1, height / 2);
                            depth = Math.max(1, depth / 2);
                        }

                        GL11.glBindTexture(GL12.GL_TEXTURE_3D, currentTex);
                        break;
                    default:
                        throw new UnsupportedOperationException("Unsupported texture target: " + target);
                }
            }
        }

        return texture;
    }

    @Override
    public void textureBind(GL3XTexture texture, int unit) {
        final GLCapabilities caps = GL.getCapabilities();

        if (caps.GL_ARB_direct_state_access && ARB_DSA) {
            ARBDirectStateAccess.glBindTextureUnit(unit, texture.textureId);
        } else {
            GL13.glActiveTexture(GL13.GL_TEXTURE0 + unit);
            GL11.glBindTexture(texture.target, texture.textureId);
        }
    }

    @Override
    public void textureDelete(GL3XTexture texture) {
        GL11.glDeleteTextures(texture.textureId);
        texture.textureId = -1;
    }

    @Override
    public void textureGenerateMipmap(GL3XTexture texture) {
        final GLCapabilities caps = GL.getCapabilities();

        if (caps.GL_ARB_direct_state_access && ARB_DSA) {
            ARBDirectStateAccess.glGenerateTextureMipmap(texture.textureId);
        } else if (caps.GL_EXT_direct_state_access && EXT_DSA) {
            EXTDirectStateAccess.glGenerateTextureMipmapEXT(texture.textureId, texture.target);
        } else if (EXCLUSIVE_CONTEXT) {
            GL11.glBindTexture(texture.target, texture.textureId);
            GL30.glGenerateMipmap(texture.target);
        } else {
            final int currentTex;

            switch (texture.target) {
                case GL11.GL_TEXTURE_1D:
                    currentTex = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_1D);
                    GL11.glBindTexture(GL11.GL_TEXTURE_1D, texture.textureId);
                    GL30.glGenerateMipmap(GL11.GL_TEXTURE_1D);
                    GL11.glBindTexture(GL11.GL_TEXTURE_1D, currentTex);
                    break;
                case GL11.GL_TEXTURE_2D:
                    currentTex = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
                    GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.textureId);
                    GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
                    GL11.glBindTexture(GL11.GL_TEXTURE_2D, currentTex);
                    break;
                case GL12.GL_TEXTURE_3D:
                    currentTex = GL11.glGetInteger(GL12.GL_TEXTURE_BINDING_3D);
                    GL11.glBindTexture(GL12.GL_TEXTURE_3D, texture.textureId);
                    GL30.glGenerateMipmap(GL12.GL_TEXTURE_3D);
                    GL11.glBindTexture(GL12.GL_TEXTURE_3D, currentTex);
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported texture target: " + texture.target);
            }
        }
    }

    @Override
    public void textureGetData(GL3XTexture texture, int level, int format, int type, ByteBuffer out) {
        final GLCapabilities caps = GL.getCapabilities();

        if (caps.GL_ARB_direct_state_access && ARB_DSA) {
            ARBDirectStateAccess.glGetTextureImage(texture.textureId, level, format, type, out);
        } else if (caps.GL_EXT_direct_state_access && EXT_DSA) {
            EXTDirectStateAccess.glGetTextureImageEXT(texture.textureId, texture.target, level, format, type, out);
        } else if (EXCLUSIVE_CONTEXT) {
            GL11.glBindTexture(texture.target, texture.textureId);
            GL11.glGetTexImage(texture.target, level, format, type, out);
        } else {
            final int currentTex;

            switch (texture.target) {
                case GL11.GL_TEXTURE_1D:
                    currentTex = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_1D);
                    GL11.glBindTexture(GL11.GL_TEXTURE_1D, texture.textureId);
                    GL11.glGetTexImage(GL11.GL_TEXTURE_1D, level, format, type, out);
                    GL11.glBindTexture(GL11.GL_TEXTURE_1D, currentTex);
                    break;
                case GL11.GL_TEXTURE_2D:
                    currentTex = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
                    GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.textureId);
                    GL11.glGetTexImage(GL11.GL_TEXTURE_2D, level, format, type, out);
                    GL11.glBindTexture(GL11.GL_TEXTURE_2D, currentTex);
                    break;
                case GL12.GL_TEXTURE_3D:
                    currentTex = GL11.glGetInteger(GL12.GL_TEXTURE_BINDING_3D);
                    GL11.glBindTexture(GL12.GL_TEXTURE_3D, texture.textureId);
                    GL11.glGetTexImage(GL12.GL_TEXTURE_3D, level, format, type, out);
                    GL11.glBindTexture(GL12.GL_TEXTURE_3D, currentTex);
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported texture target: " + texture.target);
            }
        }
    }

    @Override
    public float textureGetMaxAnisotropy() {
        if (GL.getCapabilities().GL_EXT_texture_filter_anisotropic) {
            return GL11.glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT);
        } else {
            return 1F;
        }
    }

    @Override
    public int textureGetMaxBoundTextures() {
        return GL11.glGetInteger(GL20.GL_MAX_TEXTURE_IMAGE_UNITS);
    }

    @Override
    public int textureGetMaxSize() {
        return GL11.glGetInteger(GL11.GL_MAX_TEXTURE_SIZE);
    }

    @Override
    public int textureGetPreferredFormat(int internalFormat) {
        return GL11.GL_RGBA;
    }

    @Override
    public void textureInvalidateData(GL3XTexture texture, int level) {
        if (GL.getCapabilities().GL_ARB_invalidate_subdata) {
            ARBInvalidateSubdata.glInvalidateTexImage(texture.target, level);
        }
    }

    @Override
    public void textureInvalidateRange(GL3XTexture texture, int level, int xOffset, int yOffset, int zOffset, int width, int height, int depth) {
        if (GL.getCapabilities().GL_ARB_invalidate_subdata) {
            ARBInvalidateSubdata.glInvalidateTexSubImage(texture.textureId, level, xOffset, yOffset, zOffset, width, height, depth);
        }
    }

    @Override
    public long textureMap(GL3XTexture tt) {
        throw new UnsupportedOperationException("OpenGL 4.0 is not supported!");
    }

    @Override
    public void textureSetData(GL3XTexture texture, int level, int xOffset, int yOffset, int zOffset, int width, int height, int depth, int format, int type, ByteBuffer data) {
        final GLCapabilities caps = GL.getCapabilities();

        if (caps.GL_ARB_direct_state_access && ARB_DSA) {
            switch (texture.target) {
                case GL11.GL_TEXTURE_1D:
                    ARBDirectStateAccess.glTextureSubImage1D(texture.textureId, level, xOffset, width, format, type, data);
                    break;
                case GL11.GL_TEXTURE_2D:
                    ARBDirectStateAccess.glTextureSubImage2D(texture.textureId, level, xOffset, yOffset, width, height, format, type, data);
                    break;
                case GL12.GL_TEXTURE_3D:
                    ARBDirectStateAccess.glTextureSubImage3D(texture.textureId, level, xOffset, yOffset, zOffset, width, height, depth, format, type, data);
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported texture target: " + texture.target);
            }
        } else if (caps.GL_EXT_direct_state_access && EXT_DSA) {
            switch (texture.target) {
                case GL11.GL_TEXTURE_1D:
                    EXTDirectStateAccess.glTextureSubImage1DEXT(texture.textureId, GL11.GL_TEXTURE_1D, level, xOffset, width, format, type, data);
                    break;
                case GL11.GL_TEXTURE_2D:
                    EXTDirectStateAccess.glTextureSubImage2DEXT(texture.textureId, GL11.GL_TEXTURE_2D, level, xOffset, yOffset, width, height, format, type, data);
                    break;
                case GL12.GL_TEXTURE_3D:
                    EXTDirectStateAccess.glTextureSubImage3DEXT(texture.textureId, GL12.GL_TEXTURE_3D, level, xOffset, yOffset, zOffset, width, height, depth, format, type, data);
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported texture target: " + texture.target);
            }
        } else if (EXCLUSIVE_CONTEXT) {
            switch (texture.target) {
                case GL11.GL_TEXTURE_1D:
                    GL11.glBindTexture(GL11.GL_TEXTURE_1D, texture.textureId);
                    GL11.glTexSubImage1D(GL11.GL_TEXTURE_1D, level, xOffset, width, format, type, data);
                    break;
                case GL11.GL_TEXTURE_2D:
                    GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.textureId);
                    GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, level, xOffset, yOffset, width, height, format, type, data);
                    break;
                case GL12.GL_TEXTURE_3D:
                    GL11.glBindTexture(GL12.GL_TEXTURE_3D, texture.textureId);
                    GL12.glTexSubImage3D(GL12.GL_TEXTURE_3D, level, xOffset, yOffset, zOffset, width, height, depth, format, type, data);
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported texture target: " + texture.target);
            }
        } else {
            final int currentTex;
            switch (texture.target) {
                case GL11.GL_TEXTURE_1D:
                    currentTex = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_1D);
                    GL11.glBindTexture(GL11.GL_TEXTURE_1D, texture.textureId);
                    GL11.glTexSubImage1D(GL11.GL_TEXTURE_1D, level, xOffset, width, format, type, data);
                    GL11.glBindTexture(GL11.GL_TEXTURE_1D, currentTex);
                    break;
                case GL11.GL_TEXTURE_2D:
                    currentTex = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
                    GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.textureId);
                    GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, level, xOffset, yOffset, width, height, format, type, data);
                    GL11.glBindTexture(GL11.GL_TEXTURE_2D, currentTex);
                    break;
                case GL12.GL_TEXTURE_3D:
                    currentTex = GL11.glGetInteger(GL12.GL_TEXTURE_BINDING_3D);
                    GL11.glBindTexture(GL12.GL_TEXTURE_3D, texture.textureId);
                    GL12.glTexSubImage3D(GL12.GL_TEXTURE_3D, level, xOffset, yOffset, zOffset, width, height, depth, format, type, data);
                    GL11.glBindTexture(GL12.GL_TEXTURE_3D, currentTex);
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported texture target: " + texture.target);
            }
        }
    }

    @Override
    public void textureSetParameter(GL3XTexture texture, int param, int value) {
        final GLCapabilities caps = GL.getCapabilities();

        if (caps.GL_ARB_direct_state_access && ARB_DSA) {
            ARBDirectStateAccess.glTextureParameteri(texture.textureId, param, value);
        } else if (caps.GL_EXT_direct_state_access && EXT_DSA) {
            EXTDirectStateAccess.glTextureParameteriEXT(texture.textureId, texture.target, param, value);
        } else if (EXCLUSIVE_CONTEXT) {
            GL11.glBindTexture(texture.target, texture.textureId);
            GL11.glTexParameteri(texture.target, param, value);
        } else {
            final int currentTex;

            switch (texture.target) {
                case GL11.GL_TEXTURE_1D:
                    currentTex = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_1D);
                    GL11.glBindTexture(GL11.GL_TEXTURE_1D, texture.textureId);
                    GL11.glTexParameteri(GL11.GL_TEXTURE_1D, param, value);
                    GL11.glBindTexture(GL11.GL_TEXTURE_1D, currentTex);
                    break;
                case GL11.GL_TEXTURE_2D:
                    currentTex = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
                    GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.textureId);
                    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, param, value);
                    GL11.glBindTexture(GL11.GL_TEXTURE_2D, currentTex);
                    break;
                case GL12.GL_TEXTURE_3D:
                    currentTex = GL11.glGetInteger(GL12.GL_TEXTURE_BINDING_3D);
                    GL11.glBindTexture(GL12.GL_TEXTURE_3D, texture.textureId);
                    GL11.glTexParameteri(GL12.GL_TEXTURE_3D, param, value);
                    GL11.glBindTexture(GL12.GL_TEXTURE_3D, currentTex);
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported texture target: " + texture.target);
            }
        }
    }

    @Override
    public void textureSetParameter(GL3XTexture texture, int param, float value) {
        final GLCapabilities caps = GL.getCapabilities();

        if (caps.GL_ARB_direct_state_access && ARB_DSA) {
            ARBDirectStateAccess.glTextureParameterf(texture.textureId, param, value);
        } else if (caps.GL_EXT_direct_state_access && EXT_DSA) {
            EXTDirectStateAccess.glTextureParameterfEXT(texture.textureId, texture.target, param, value);
        } else if (EXCLUSIVE_CONTEXT) {
            GL11.glBindTexture(texture.target, texture.textureId);
            GL11.glTexParameterf(texture.target, param, value);
        } else {
            final int currentTex;

            switch (texture.target) {
                case GL11.GL_TEXTURE_1D:
                    currentTex = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_1D);
                    GL11.glBindTexture(GL11.GL_TEXTURE_1D, texture.textureId);
                    GL11.glTexParameterf(GL11.GL_TEXTURE_1D, param, value);
                    GL11.glBindTexture(GL11.GL_TEXTURE_1D, currentTex);
                    break;
                case GL11.GL_TEXTURE_2D:
                    currentTex = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
                    GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.textureId);
                    GL11.glTexParameterf(GL11.GL_TEXTURE_2D, param, value);
                    GL11.glBindTexture(GL11.GL_TEXTURE_2D, currentTex);
                    break;
                case GL12.GL_TEXTURE_3D:
                    currentTex = GL11.glGetInteger(GL12.GL_TEXTURE_BINDING_3D);
                    GL11.glBindTexture(GL12.GL_TEXTURE_3D, texture.textureId);
                    GL11.glTexParameterf(GL12.GL_TEXTURE_3D, param, value);
                    GL11.glBindTexture(GL12.GL_TEXTURE_3D, currentTex);
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported texture target: " + texture.target);
            }
        }
    }

    @Override
    public void textureUnmap(GL3XTexture tt) {
        throw new UnsupportedOperationException("OpenGL 4.0 is not supported!");
    }

    @Override
    public void transformFeedbackBegin(int drawMode) {
        GL11.glEnable(GL30.GL_RASTERIZER_DISCARD);
        GL30.glBeginTransformFeedback(drawMode);
    }

    @Override
    public void transformFeedbackEnd() {
        GL30.glEndTransformFeedback();
        GL11.glDisable(GL30.GL_RASTERIZER_DISCARD);
    }

    @Override
    public void vertexArrayAttachBuffer(GL3XVertexArray vao, int index, GL3XBuffer buffer, int size, int type, int stride, long offset, int divisor) {
        final GLCapabilities caps = GL.getCapabilities();

        if (EXCLUSIVE_CONTEXT) {
            GL30.glBindVertexArray(vao.vertexArrayId);
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, buffer.bufferId);
            GL20.glEnableVertexAttribArray(index);

            // FLOAT must go in VertexAttribPointer
            if (type == GL11.GL_FLOAT) {
                GL20.glVertexAttribPointer(index, size, type, false, stride, offset);
            } else {
                GL30.glVertexAttribIPointer(index, size, type, stride, offset);
            }

            if (divisor > 0) {
                if (caps.OpenGL33) {
                    GL33.glVertexAttribDivisor(index, divisor);
                } else {
                    throw new UnsupportedOperationException("Attribute Divisors are not supported!");
                }
            }
        } else {
            final int currentVao = GL11.glGetInteger(GL30.GL_VERTEX_ARRAY_BINDING);

            GL30.glBindVertexArray(vao.vertexArrayId);
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, buffer.bufferId);
            GL20.glEnableVertexAttribArray(index);

            // FLOAT must go in VertexAttribPointer
            if (type == GL11.GL_FLOAT) {
                GL20.glVertexAttribPointer(index, size, type, false, stride, offset);
            } else {
                GL30.glVertexAttribIPointer(index, size, type, stride, offset);
            }

            if (divisor > 0) {
                if (caps.OpenGL33) {
                    GL33.glVertexAttribDivisor(index, divisor);
                } else {
                    throw new UnsupportedOperationException("Attribute Divisors are not supported!");
                }
            }

            GL30.glBindVertexArray(currentVao);
        }
    }

    @Override
    public void vertexArrayAttachIndexBuffer(GL3XVertexArray vao, GL3XBuffer buffer) {
        if (EXCLUSIVE_CONTEXT) {
            GL30.glBindVertexArray(vao.vertexArrayId);
            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer.bufferId);
        } else {
            final int currentVao = GL11.glGetInteger(GL30.GL_VERTEX_ARRAY_BINDING);

            GL30.glBindVertexArray(vao.vertexArrayId);
            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer.bufferId);
            GL30.glBindVertexArray(currentVao);
        }
    }

    @Override
    public GL3XVertexArray vertexArrayCreate() {
        final GL3XVertexArray vao = new GL3XVertexArray();
        vao.vertexArrayId = GL30.glGenVertexArrays();
        return vao;
    }

    @Override
    public void vertexArrayDelete(GL3XVertexArray vao) {
        GL30.glDeleteVertexArrays(vao.vertexArrayId);
        vao.vertexArrayId = -1;
    }

    @Override
    public void vertexArrayDrawArrays(GL3XVertexArray vao, int drawMode, int start, int count) {
        if (EXCLUSIVE_CONTEXT) {
            GL30.glBindVertexArray(vao.vertexArrayId);
            GL11.glDrawArrays(drawMode, count, count);
        } else {
            final int currentVao = GL11.glGetInteger(GL30.GL_VERTEX_ARRAY_BINDING);

            GL30.glBindVertexArray(vao.vertexArrayId);
            GL11.glDrawArrays(drawMode, count, count);
            GL30.glBindVertexArray(currentVao);
        }
    }

    @Override
    public void vertexArrayDrawArraysIndirect(GL3XVertexArray vao, GL3XBuffer cmdBuffer, int drawMode, long offset) {
        final GLCapabilities caps = GL.getCapabilities();

        if (caps.GL_ARB_draw_indirect) {
            if (EXCLUSIVE_CONTEXT) {
                GL30.glBindVertexArray(vao.vertexArrayId);
                GL15.glBindBuffer(ARBDrawIndirect.GL_DRAW_INDIRECT_BUFFER, cmdBuffer.bufferId);
                ARBDrawIndirect.glDrawArraysIndirect(drawMode, offset);
                GL15.glBindBuffer(ARBDrawIndirect.GL_DRAW_INDIRECT_BUFFER, 0);
            } else {
                final int currentVao = GL11.glGetInteger(GL30.GL_VERTEX_ARRAY_BINDING);

                GL30.glBindVertexArray(vao.vertexArrayId);
                GL15.glBindBuffer(ARBDrawIndirect.GL_DRAW_INDIRECT_BUFFER, cmdBuffer.bufferId);
                ARBDrawIndirect.glDrawArraysIndirect(drawMode, offset);
                GL15.glBindBuffer(ARBDrawIndirect.GL_DRAW_INDIRECT_BUFFER, 0);
                GL30.glBindVertexArray(currentVao);
            }
        }
    }

    @Override
    public void vertexArrayDrawArraysInstanced(GL3XVertexArray vao, int drawMode, int first, int count, int instanceCount) {
        final GLCapabilities caps = GL.getCapabilities();

        if (caps.OpenGL31) {
            if (EXCLUSIVE_CONTEXT) {
                GL30.glBindVertexArray(vao.vertexArrayId);
                GL31.glDrawArraysInstanced(drawMode, first, count, instanceCount);
            } else {
                final int currentVao = GL11.glGetInteger(GL30.GL_VERTEX_ARRAY_BINDING);

                GL30.glBindVertexArray(vao.vertexArrayId);
                GL31.glDrawArraysInstanced(drawMode, first, count, instanceCount);
                GL30.glBindVertexArray(currentVao);
            }
        } else {
            throw new UnsupportedOperationException("OpenGL 3.1 is not supported!");
        }
    }

    @Override
    public void vertexArrayDrawElements(GL3XVertexArray vao, int drawMode, int count, int type, long offset) {
        if (EXCLUSIVE_CONTEXT) {
            GL30.glBindVertexArray(vao.vertexArrayId);
            GL11.glDrawElements(drawMode, count, type, offset);
        } else {
            final int currentVao = GL11.glGetInteger(GL30.GL_VERTEX_ARRAY_BINDING);

            GL30.glBindVertexArray(vao.vertexArrayId);
            GL11.glDrawElements(drawMode, count, type, offset);
            GL30.glBindVertexArray(currentVao);
        }
    }

    @Override
    public void vertexArrayDrawElementsIndirect(GL3XVertexArray vao, GL3XBuffer cmdBuffer, int drawMode, int indexType, long offset) {
        final GLCapabilities caps = GL.getCapabilities();

        if (caps.GL_ARB_draw_indirect) {
            if (EXCLUSIVE_CONTEXT) {
                GL30.glBindVertexArray(vao.vertexArrayId);
                GL15.glBindBuffer(ARBDrawIndirect.GL_DRAW_INDIRECT_BUFFER, cmdBuffer.bufferId);
                ARBDrawIndirect.glDrawElementsIndirect(drawMode, indexType, offset);
                GL15.glBindBuffer(ARBDrawIndirect.GL_DRAW_INDIRECT_BUFFER, 0);
            } else {
                final int currentVao = GL11.glGetInteger(GL30.GL_VERTEX_ARRAY_BINDING);

                GL30.glBindVertexArray(vao.vertexArrayId);
                GL15.glBindBuffer(ARBDrawIndirect.GL_DRAW_INDIRECT_BUFFER, cmdBuffer.bufferId);
                ARBDrawIndirect.glDrawElementsIndirect(drawMode, indexType, offset);
                GL15.glBindBuffer(ARBDrawIndirect.GL_DRAW_INDIRECT_BUFFER, 0);
                GL30.glBindVertexArray(currentVao);
            }
        }
    }

    @Override
    public void vertexArrayDrawElementsInstanced(GL3XVertexArray vao, int drawMode, int count, int type, long offset, int instanceCount) {
        final GLCapabilities caps = GL.getCapabilities();

        if (caps.OpenGL31) {
            if (EXCLUSIVE_CONTEXT) {
                GL30.glBindVertexArray(vao.vertexArrayId);
                GL31.glDrawElementsInstanced(drawMode, count, type, offset, instanceCount);
            } else {
                final int currentVao = GL11.glGetInteger(GL30.GL_VERTEX_ARRAY_BINDING);

                GL30.glBindVertexArray(vao.vertexArrayId);
                GL31.glDrawElementsInstanced(drawMode, count, type, offset, instanceCount);
                GL30.glBindVertexArray(currentVao);
            }
        } else {
            throw new UnsupportedOperationException("OpenGL 3.1 is not supported!");
        }
    }

    @Override
    public void viewportApply(int x, int y, int width, int height) {
        GL11.glViewport(x, y, width, height);
    }

    @Override
    public void textureGetData(GL3XTexture texture, int level, int format, int type, GL3XBuffer out, long offset, int size) {
        final GLCapabilities caps = GL.getCapabilities();

        if (caps.GL_ARB_direct_state_access && ARB_DSA) {
            GL15.glBindBuffer(GL21.GL_PIXEL_PACK_BUFFER, out.bufferId);
            ARBDirectStateAccess.glGetTextureImage(texture.textureId, level, format, type, size, offset);
            GL15.glBindBuffer(GL21.GL_PIXEL_PACK_BUFFER, 0);
        } else if (caps.GL_EXT_direct_state_access && EXT_DSA) {
            GL15.glBindBuffer(GL21.GL_PIXEL_PACK_BUFFER, out.bufferId);
            EXTDirectStateAccess.glGetTextureImageEXT(texture.textureId, texture.target, level, format, type, 0L);
            GL15.glBindBuffer(GL21.GL_PIXEL_PACK_BUFFER, 0);
        } else if (EXCLUSIVE_CONTEXT) {
            switch (texture.textureId) {
                case GL11.GL_TEXTURE_1D:
                    GL11.glBindTexture(GL11.GL_TEXTURE_1D, texture.textureId);
                    GL15.glBindBuffer(GL21.GL_PIXEL_PACK_BUFFER, out.bufferId);
                    GL11.glGetTexImage(GL11.GL_TEXTURE_1D, level, format, type, 0L);
                    GL15.glBindBuffer(GL21.GL_PIXEL_PACK_BUFFER, 0);
                    break;
                case GL11.GL_TEXTURE_2D:
                    GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.textureId);
                    GL15.glBindBuffer(GL21.GL_PIXEL_PACK_BUFFER, out.bufferId);
                    GL11.glGetTexImage(GL11.GL_TEXTURE_2D, level, format, type, 0L);
                    GL15.glBindBuffer(GL21.GL_PIXEL_PACK_BUFFER, 0);
                    break;
                case GL12.GL_TEXTURE_3D:
                    GL11.glBindTexture(GL12.GL_TEXTURE_3D, texture.textureId);
                    GL15.glBindBuffer(GL21.GL_PIXEL_PACK_BUFFER, out.bufferId);
                    GL11.glGetTexImage(GL12.GL_TEXTURE_3D, level, format, type, 0L);
                    GL15.glBindBuffer(GL21.GL_PIXEL_PACK_BUFFER, 0);
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported texture target: " + texture.target);
            }
        } else {
            final int currentTex;

            switch (texture.textureId) {
                case GL11.GL_TEXTURE_1D:
                    currentTex = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_1D);
                    GL11.glBindTexture(GL11.GL_TEXTURE_1D, texture.textureId);
                    GL15.glBindBuffer(GL21.GL_PIXEL_PACK_BUFFER, out.bufferId);
                    GL11.glGetTexImage(GL11.GL_TEXTURE_1D, level, format, type, 0L);
                    GL15.glBindBuffer(GL21.GL_PIXEL_PACK_BUFFER, 0);
                    GL11.glBindTexture(GL11.GL_TEXTURE_1D, currentTex);
                    break;
                case GL11.GL_TEXTURE_2D:
                    currentTex = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
                    GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.textureId);
                    GL15.glBindBuffer(GL21.GL_PIXEL_PACK_BUFFER, out.bufferId);
                    GL11.glGetTexImage(GL11.GL_TEXTURE_2D, level, format, type, 0L);
                    GL15.glBindBuffer(GL21.GL_PIXEL_PACK_BUFFER, 0);
                    GL11.glBindTexture(GL11.GL_TEXTURE_2D, currentTex);
                    break;
                case GL12.GL_TEXTURE_3D:
                    currentTex = GL11.glGetInteger(GL12.GL_TEXTURE_BINDING_3D);
                    GL11.glBindTexture(GL12.GL_TEXTURE_3D, texture.textureId);
                    GL15.glBindBuffer(GL21.GL_PIXEL_PACK_BUFFER, out.bufferId);
                    GL11.glGetTexImage(GL12.GL_TEXTURE_3D, level, format, type, 0L);
                    GL15.glBindBuffer(GL21.GL_PIXEL_PACK_BUFFER, 0);
                    GL11.glBindTexture(GL12.GL_TEXTURE_3D, currentTex);
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported texture target: " + texture.target);
            }
        }
    }

    @Override
    public void textureSetData(GL3XTexture texture, int level, int xOffset, int yOffset, int zOffset, int width, int height, int depth, int format, int type, GL3XBuffer buffer, long offset) {
        final GLCapabilities caps = GL.getCapabilities();

        if (caps.GL_ARB_direct_state_access && ARB_DSA) {
            switch (texture.textureId) {
                case GL11.GL_TEXTURE_1D:
                    GL15.glBindBuffer(GL21.GL_PIXEL_UNPACK_BUFFER, buffer.bufferId);
                    ARBDirectStateAccess.glTextureSubImage1D(texture.textureId, level, xOffset, width, format, type, 0L);
                    GL11.glTexSubImage1D(GL11.GL_TEXTURE_1D, level, xOffset, width, format, type, 0L);
                    GL15.glBindBuffer(GL21.GL_PIXEL_UNPACK_BUFFER, 0);
                    break;
                case GL11.GL_TEXTURE_2D:
                    GL15.glBindBuffer(GL21.GL_PIXEL_UNPACK_BUFFER, buffer.bufferId);
                    ARBDirectStateAccess.glTextureSubImage2D(texture.textureId, level, xOffset, yOffset, width, height, format, type, 0L);
                    GL15.glBindBuffer(GL21.GL_PIXEL_UNPACK_BUFFER, 0);
                    break;
                case GL12.GL_TEXTURE_3D:
                    GL15.glBindBuffer(GL21.GL_PIXEL_UNPACK_BUFFER, buffer.bufferId);
                    ARBDirectStateAccess.glTextureSubImage3D(type, level, xOffset, yOffset, zOffset, width, height, depth, format, type, 0L);
                    GL15.glBindBuffer(GL21.GL_PIXEL_UNPACK_BUFFER, 0);
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported texture target: " + texture.target);
            }
        } else if (caps.GL_EXT_direct_state_access && EXT_DSA) {
            switch (texture.textureId) {
                case GL11.GL_TEXTURE_1D:
                    GL15.glBindBuffer(GL21.GL_PIXEL_UNPACK_BUFFER, buffer.bufferId);
                    EXTDirectStateAccess.glTextureSubImage1DEXT(texture.textureId, texture.target, level, xOffset, width, format, type, 0L);
                    GL11.glTexSubImage1D(GL11.GL_TEXTURE_1D, level, xOffset, width, format, type, 0L);
                    GL15.glBindBuffer(GL21.GL_PIXEL_UNPACK_BUFFER, 0);
                    break;
                case GL11.GL_TEXTURE_2D:
                    GL15.glBindBuffer(GL21.GL_PIXEL_UNPACK_BUFFER, buffer.bufferId);
                    EXTDirectStateAccess.glTextureImage2DEXT(texture.textureId, texture.target, level, format, width, height, format, format, type, 0L);
                    GL15.glBindBuffer(GL21.GL_PIXEL_UNPACK_BUFFER, 0);
                    break;
                case GL12.GL_TEXTURE_3D:
                    GL15.glBindBuffer(GL21.GL_PIXEL_UNPACK_BUFFER, buffer.bufferId);
                    EXTDirectStateAccess.glTextureSubImage3DEXT(texture.textureId, texture.target, level, xOffset, yOffset, zOffset, width, height, depth, format, type, 0L);
                    GL15.glBindBuffer(GL21.GL_PIXEL_UNPACK_BUFFER, 0);
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported texture target: " + texture.target);
            }
        } else if (EXCLUSIVE_CONTEXT) {
            switch (texture.textureId) {
                case GL11.GL_TEXTURE_1D:
                    GL11.glBindTexture(GL11.GL_TEXTURE_1D, texture.textureId);
                    GL15.glBindBuffer(GL21.GL_PIXEL_UNPACK_BUFFER, buffer.bufferId);
                    GL11.glTexSubImage1D(GL11.GL_TEXTURE_1D, level, xOffset, width, format, type, 0L);
                    GL15.glBindBuffer(GL21.GL_PIXEL_UNPACK_BUFFER, 0);
                    break;
                case GL11.GL_TEXTURE_2D:
                    GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.textureId);
                    GL15.glBindBuffer(GL21.GL_PIXEL_UNPACK_BUFFER, buffer.bufferId);
                    GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, level, xOffset, yOffset, width, height, format, type, 0L);
                    GL15.glBindBuffer(GL21.GL_PIXEL_UNPACK_BUFFER, 0);
                    break;
                case GL12.GL_TEXTURE_3D:
                    GL11.glBindTexture(GL12.GL_TEXTURE_3D, texture.textureId);
                    GL15.glBindBuffer(GL21.GL_PIXEL_UNPACK_BUFFER, buffer.bufferId);
                    GL12.glTexSubImage3D(GL12.GL_TEXTURE_3D, level, xOffset, yOffset, zOffset, width, height, depth, format, type, 0L);
                    GL15.glBindBuffer(GL21.GL_PIXEL_UNPACK_BUFFER, 0);
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported texture target: " + texture.target);
            }
        } else {
            final int currentTex;
            switch (texture.textureId) {
                case GL11.GL_TEXTURE_1D:
                    currentTex = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_1D);
                    GL11.glBindTexture(GL11.GL_TEXTURE_1D, texture.textureId);
                    GL15.glBindBuffer(GL21.GL_PIXEL_UNPACK_BUFFER, buffer.bufferId);
                    GL11.glTexSubImage1D(GL11.GL_TEXTURE_1D, level, xOffset, width, format, type, 0L);
                    GL15.glBindBuffer(GL21.GL_PIXEL_UNPACK_BUFFER, 0);
                    GL11.glBindTexture(GL11.GL_TEXTURE_1D, currentTex);
                    break;
                case GL11.GL_TEXTURE_2D:
                    currentTex = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
                    GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.textureId);
                    GL15.glBindBuffer(GL21.GL_PIXEL_UNPACK_BUFFER, buffer.bufferId);
                    GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, level, xOffset, yOffset, width, height, format, type, 0L);
                    GL15.glBindBuffer(GL21.GL_PIXEL_UNPACK_BUFFER, 0);
                    GL11.glBindTexture(GL11.GL_TEXTURE_2D, currentTex);
                    break;
                case GL12.GL_TEXTURE_3D:
                    currentTex = GL11.glGetInteger(GL12.GL_TEXTURE_BINDING_3D);
                    GL11.glBindTexture(GL12.GL_TEXTURE_3D, texture.textureId);
                    GL15.glBindBuffer(GL21.GL_PIXEL_UNPACK_BUFFER, buffer.bufferId);
                    GL12.glTexSubImage3D(GL12.GL_TEXTURE_3D, level, xOffset, yOffset, zOffset, width, height, depth, format, type, 0L);
                    GL15.glBindBuffer(GL21.GL_PIXEL_UNPACK_BUFFER, 0);
                    GL11.glBindTexture(GL12.GL_TEXTURE_3D, currentTex);
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported texture target: " + texture.target);
            }
        }
    }
}
