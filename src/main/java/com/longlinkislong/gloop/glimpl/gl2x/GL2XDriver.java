/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop.glimpl.gl2x;

import com.longlinkislong.gloop.glspi.Driver;
import com.longlinkislong.gloop.glspi.Shader;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import org.lwjgl.opengl.ARBBufferStorage;
import org.lwjgl.opengl.ARBCopyBuffer;
import org.lwjgl.opengl.ARBFramebufferObject;
import org.lwjgl.opengl.ARBInvalidateSubdata;
import org.lwjgl.opengl.ARBMapBufferRange;
import org.lwjgl.opengl.ARBSamplerObjects;
import org.lwjgl.opengl.ARBSeparateShaderObjects;
import org.lwjgl.opengl.ARBUniformBufferObject;
import org.lwjgl.opengl.ARBVertexArrayObject;
import org.lwjgl.opengl.EXTTextureFilterAnisotropic;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL21;
import org.lwjgl.opengl.GLCapabilities;

/**
 *
 * @author zmichaels
 */
final class GL2XDriver implements Driver<
        GL2XBuffer, GL2XFramebuffer, GL2XRenderbuffer, GL2XTexture, GL2XShader, GL2XProgram, GL2XSampler, GL2XVertexArray> {

    @Override
    public void bufferBindAtomic(GL2XBuffer bt, int i) {
        throw new UnsupportedOperationException("OpenGL 4.2 is not supported!");
    }

    @Override
    public void bufferBindAtomic(GL2XBuffer bt, int i, long l, long l1) {
        throw new UnsupportedOperationException("OpenGL 4.2 is not supported!");
    }

    @Override
    public void bufferBindStorage(GL2XBuffer bt, int binding) {
        throw new UnsupportedOperationException("ARB_shader_storage_buffer_object is not supported!");
    }

    @Override
    public void bufferBindStorage(GL2XBuffer bt, int binding, long offset, long size) {
        throw new UnsupportedOperationException("ARB_shader_storage_buffer_object is not supported!");
    }

    @Override
    public void bufferBindFeedback(GL2XBuffer bt, int i) {
        throw new UnsupportedOperationException("Transform Feedback requires OpenGL3.0!");
    }

    @Override
    public void bufferBindFeedback(GL2XBuffer bt, int i, long l, long l1) {
        throw new UnsupportedOperationException("Transform Feedback requires OpenGL3.0!");
    }

    @Override
    public void bufferBindUniform(GL2XBuffer bt, int binding) {
        if (GL.getCapabilities().GL_ARB_uniform_buffer_object) {
            ARBUniformBufferObject.glBindBufferBase(ARBUniformBufferObject.GL_UNIFORM_BUFFER, binding, bt.bufferId);
        } else {
            throw new UnsupportedOperationException("ARB_uniform_buffer_object is not supported!");
        }
    }

    @Override
    public void bufferBindUniform(GL2XBuffer bt, int binding, long offset, long size) {
        if (GL.getCapabilities().GL_ARB_uniform_buffer_object) {
            ARBUniformBufferObject.glBindBufferRange(ARBUniformBufferObject.GL_UNIFORM_BUFFER, binding, bt.bufferId, offset, size);
        } else {
            throw new UnsupportedOperationException("ARB_uniform_buffer_object is not supported!");
        }
    }

    @Override
    public int bufferGetMaxUniformBindings() {
        throw new UnsupportedOperationException("OpenGL 3.0 is not supported!");
    }

    @Override
    public int bufferGetMaxUniformBlockSize() {
        throw new UnsupportedOperationException("OpenGL 3.0 is not supported!");
    }

    @Override
    public int programGetStorageBlockBinding(GL2XProgram pt, String sName) {
        throw new UnsupportedOperationException("ARB_shader_storage_buffer_object is not supported!");
    }

    @Override
    public int programGetUniformBlockBinding(GL2XProgram pt, String ublockName) {
        if (pt.uniformBindings.containsKey(ublockName)) {
            return pt.uniformBindings.get(ublockName);
        } else {
            return -1;
        }
    }

    @Override
    public void programSetStorageBlockBinding(GL2XProgram pt, String string, int i) {
        throw new UnsupportedOperationException("ARB_shader_storage_buffer_object is not supported!");
    }

    @Override
    public void programSetUniformBlockBinding(GL2XProgram pt, String ublockName, int binding) {
        if (GL.getCapabilities().GL_ARB_uniform_buffer_object) {
            final int ublockIndex = ARBUniformBufferObject.glGetUniformBlockIndex(pt.programId, ublockName);

            ARBUniformBufferObject.glUniformBlockBinding(pt.programId, ublockIndex, binding);
        } else {
            throw new UnsupportedOperationException("ARB_uniform_buffer_object is not supported!");
        }
    }

    @Override
    public int shaderGetVersion() {
        final GLCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL21) {
            return 120;
        } else if (cap.OpenGL20) {
            return 110;
        } else {
            return 100;
        }
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
    public void bufferAllocate(GL2XBuffer buffer, long size, int usage) {
        final int currentBuffer = GL11.glGetInteger(GL15.GL_ARRAY_BUFFER_BINDING);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, buffer.bufferId);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, size, usage);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, currentBuffer);
    }

    @Override
    public void bufferAllocateImmutable(GL2XBuffer buffer, long size, int bitflags) {
        final int currentBuffer = GL11.glGetInteger(GL15.GL_ARRAY_BUFFER_BINDING);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, buffer.bufferId);
        ARBBufferStorage.glBufferStorage(GL15.GL_ARRAY_BUFFER, size, bitflags);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, currentBuffer);
    }

    @Override
    public void bufferCopyData(GL2XBuffer srcBuffer, long srcOffset, GL2XBuffer dstBuffer, long dstOffset, long size) {
        if (GL.getCapabilities().GL_ARB_copy_buffer) {
            GL15.glBindBuffer(ARBCopyBuffer.GL_COPY_READ_BUFFER, srcBuffer.bufferId);
            GL15.glBindBuffer(ARBCopyBuffer.GL_COPY_WRITE_BUFFER, dstBuffer.bufferId);

            ARBCopyBuffer.glCopyBufferSubData(ARBCopyBuffer.GL_COPY_READ_BUFFER, ARBCopyBuffer.GL_COPY_WRITE_BUFFER, srcOffset, dstOffset, size);

            GL15.glBindBuffer(ARBCopyBuffer.GL_COPY_READ_BUFFER, 0);
            GL15.glBindBuffer(ARBCopyBuffer.GL_COPY_WRITE_BUFFER, 0);
        } else {
            final ByteBuffer src = this.bufferMapData(srcBuffer, srcOffset, size, ARBMapBufferRange.GL_MAP_READ_BIT);
            final ByteBuffer dst = this.bufferMapData(dstBuffer, dstOffset, size, ARBMapBufferRange.GL_MAP_WRITE_BIT);

            for (int i = 0; i < size; i++) {
                dst.put(i, src.get(i));
            }

            this.bufferUnmapData(dstBuffer);
            this.bufferUnmapData(srcBuffer);
        }
    }

    @Override
    public GL2XBuffer bufferCreate() {
        final GL2XBuffer buffer = new GL2XBuffer();
        buffer.bufferId = GL15.glGenBuffers();
        return buffer;
    }

    @Override
    public void bufferDelete(GL2XBuffer buffer) {
        GL15.glDeleteBuffers(buffer.bufferId);
        buffer.bufferId = -1;
    }

    @Override
    public void bufferGetData(GL2XBuffer buffer, long offset, int[] out) {
        final int currentBuffer = GL11.glGetInteger(GL15.GL_ARRAY_BUFFER_BINDING);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, buffer.bufferId);
        GL15.glGetBufferSubData(GL15.GL_ARRAY_BUFFER, offset, out);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, currentBuffer);
    }

    @Override
    public void bufferGetData(GL2XBuffer buffer, long offset, float[] out) {
        final int currentBuffer = GL11.glGetInteger(GL15.GL_ARRAY_BUFFER_BINDING);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, buffer.bufferId);
        GL15.glGetBufferSubData(GL15.GL_ARRAY_BUFFER, offset, out);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, currentBuffer);
    }

    @Override
    public void bufferGetData(GL2XBuffer buffer, long offset, ByteBuffer out) {
        final int currentBuffer = GL11.glGetInteger(GL15.GL_ARRAY_BUFFER_BINDING);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, buffer.bufferId);
        GL15.glGetBufferSubData(GL15.GL_ARRAY_BUFFER, offset, out);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, currentBuffer);
    }

    @Override
    public int bufferGetParameterI(GL2XBuffer buffer, int paramId) {
        final int currentAB = GL11.glGetInteger(GL15.GL_ARRAY_BUFFER_BINDING);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, buffer.bufferId);
        final int res = GL15.glGetBufferParameteri(GL15.GL_ARRAY_BUFFER, buffer.bufferId);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, currentAB);
        return res;
    }

    @Override
    public void bufferInvalidateData(GL2XBuffer buffer) {
        ARBInvalidateSubdata.glInvalidateBufferData(buffer.bufferId);
    }

    @Override
    public void bufferInvalidateRange(GL2XBuffer buffer, long offset, long length) {
        ARBInvalidateSubdata.glInvalidateBufferSubData(buffer.bufferId, offset, length);
    }

    @Override
    public ByteBuffer bufferMapData(GL2XBuffer buffer, long offset, long length, int accessFlags) {
        final int currentBuffer = GL11.glGetInteger(GL15.GL_ARRAY_BUFFER_BINDING);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, buffer.bufferId);
        buffer.mapBuffer = ARBMapBufferRange.glMapBufferRange(GL15.GL_ARRAY_BUFFER, offset, length, accessFlags, buffer.mapBuffer);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, currentBuffer);
        //TODO: fallback on mapBuffer
        return buffer.mapBuffer;
    }
    
    @Override
    public void bufferSetData(GL2XBuffer buffer, long offset, float[] data) {
        final int currentBuffer = GL11.glGetInteger(GL15.GL_ARRAY_BUFFER_BINDING);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, buffer.bufferId);
        GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, offset, data);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, currentBuffer);
    }

    @Override
    public void bufferSetData(GL2XBuffer buffer, long offset, int[] data) {
        final int currentBuffer = GL11.glGetInteger(GL15.GL_ARRAY_BUFFER_BINDING);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, buffer.bufferId);
        GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, offset, data);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, currentBuffer);
    }

    @Override
    public void bufferSetData(GL2XBuffer buffer, long offset, ByteBuffer data) {
        final int currentBuffer = GL11.glGetInteger(GL15.GL_ARRAY_BUFFER_BINDING);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, buffer.bufferId);
        GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, offset, data);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, currentBuffer);
    }

    @Override
    public void bufferUnmapData(GL2XBuffer buffer) {
        final int currentBuffer = GL11.glGetInteger(GL15.GL_ARRAY_BUFFER_BINDING);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, buffer.bufferId);
        GL15.glUnmapBuffer(GL15.GL_ARRAY_BUFFER);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, currentBuffer);
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
    public void framebufferAddAttachment(GL2XFramebuffer framebuffer, int attachmentId, GL2XTexture texId, int mipmapLevel) {
        final int currentFb = GL11.glGetInteger(ARBFramebufferObject.GL_FRAMEBUFFER_BINDING);

        switch (texId.target) {
            case GL11.GL_TEXTURE_1D:
                ARBFramebufferObject.glBindFramebuffer(ARBFramebufferObject.GL_FRAMEBUFFER, framebuffer.framebufferId);
                ARBFramebufferObject.glFramebufferTexture1D(ARBFramebufferObject.GL_FRAMEBUFFER, attachmentId, GL11.GL_TEXTURE_1D, texId.textureId, mipmapLevel);
                ARBFramebufferObject.glBindFramebuffer(ARBFramebufferObject.GL_FRAMEBUFFER, currentFb);
                break;
            case GL11.GL_TEXTURE_2D:
                ARBFramebufferObject.glBindFramebuffer(ARBFramebufferObject.GL_FRAMEBUFFER, framebuffer.framebufferId);
                ARBFramebufferObject.glFramebufferTexture2D(ARBFramebufferObject.GL_FRAMEBUFFER, attachmentId, GL11.GL_TEXTURE_2D, texId.textureId, mipmapLevel);
                ARBFramebufferObject.glBindFramebuffer(ARBFramebufferObject.GL_FRAMEBUFFER, currentFb);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported texture target!");
        }
    }

    @Override
    public void framebufferAddRenderbuffer(GL2XFramebuffer framebuffer, int attachmentId, GL2XRenderbuffer renderbuffer) {
        final int currentFb = GL11.glGetInteger(ARBFramebufferObject.GL_FRAMEBUFFER_BINDING);

        ARBFramebufferObject.glBindFramebuffer(ARBFramebufferObject.GL_FRAMEBUFFER, framebuffer.framebufferId);
        ARBFramebufferObject.glFramebufferRenderbuffer(ARBFramebufferObject.GL_FRAMEBUFFER, attachmentId, ARBFramebufferObject.GL_RENDERBUFFER, renderbuffer.renderbufferId);
        ARBFramebufferObject.glBindFramebuffer(ARBFramebufferObject.GL_FRAMEBUFFER, currentFb);
    }

    @Override
    public void framebufferBind(GL2XFramebuffer framebuffer, IntBuffer attachments) {
        ARBFramebufferObject.glBindFramebuffer(ARBFramebufferObject.GL_FRAMEBUFFER, framebuffer.framebufferId);

        if (attachments != null) {
            GL20.glDrawBuffers(attachments);
        }
    }

    @Override
    public void framebufferBlit(GL2XFramebuffer srcFb, int srcX0, int srcY0, int srcX1, int srcY1, GL2XFramebuffer dstFb, int dstX0, int dstY0, int dstX1, int dstY1, int bitfield, int filter) {
        final int currentReadFb = GL11.glGetInteger(ARBFramebufferObject.GL_READ_FRAMEBUFFER_BINDING);
        final int currentDrawFb = GL11.glGetInteger(ARBFramebufferObject.GL_DRAW_FRAMEBUFFER_BINDING);

        ARBFramebufferObject.glBindFramebuffer(ARBFramebufferObject.GL_READ_FRAMEBUFFER, srcFb.framebufferId);
        ARBFramebufferObject.glBindFramebuffer(ARBFramebufferObject.GL_DRAW_FRAMEBUFFER, dstFb.framebufferId);

        ARBFramebufferObject.glBlitFramebuffer(srcX0, srcY0, srcX1, srcY1, dstX0, dstY0, dstX1, dstY1, bitfield, filter);

        ARBFramebufferObject.glBindFramebuffer(ARBFramebufferObject.GL_DRAW_FRAMEBUFFER, currentDrawFb);
        ARBFramebufferObject.glBindFramebuffer(ARBFramebufferObject.GL_READ_FRAMEBUFFER, currentReadFb);
    }

    @Override
    public GL2XFramebuffer framebufferCreate() {
        final GL2XFramebuffer fb = new GL2XFramebuffer();
        fb.framebufferId = ARBFramebufferObject.glGenFramebuffers();
        return fb;
    }

    @Override
    public void framebufferDelete(GL2XFramebuffer framebuffer) {
        ARBFramebufferObject.glDeleteFramebuffers(framebuffer.framebufferId);
        framebuffer.framebufferId = -1;
    }

    @Override
    public GL2XFramebuffer framebufferGetDefault() {
        final GL2XFramebuffer fb = new GL2XFramebuffer();
        fb.framebufferId = 0;
        return fb;
    }

    @Override
    public void framebufferGetPixels(GL2XFramebuffer framebuffer, int x, int y, int width, int height, int format, int type, GL2XBuffer dstBuffer) {
        final int currentFB = GL11.glGetInteger(ARBFramebufferObject.GL_FRAMEBUFFER_BINDING);
        final int currentBuffer = GL11.glGetInteger(GL21.GL_PIXEL_PACK_BUFFER_BINDING);

        ARBFramebufferObject.glBindFramebuffer(ARBFramebufferObject.GL_FRAMEBUFFER, framebuffer.framebufferId);

        GL15.glBindBuffer(GL21.GL_PIXEL_PACK_BUFFER, dstBuffer.bufferId);
        GL11.glReadPixels(
                x, y, width, height,
                format, type,
                0L);
        GL15.glBindBuffer(GL21.GL_PIXEL_PACK_BUFFER, currentBuffer);

        ARBFramebufferObject.glBindFramebuffer(ARBFramebufferObject.GL_FRAMEBUFFER, currentFB);
    }

    @Override
    public void framebufferGetPixels(GL2XFramebuffer framebuffer, int x, int y, int width, int height, int format, int type, int[] dstBuffer) {
        final int currentFB = GL11.glGetInteger(ARBFramebufferObject.GL_FRAMEBUFFER_BINDING);

        ARBFramebufferObject.glBindFramebuffer(ARBFramebufferObject.GL_FRAMEBUFFER, framebuffer.framebufferId);

        GL11.glReadPixels(
                x, y, width, height,
                format, type,
                dstBuffer);

        ARBFramebufferObject.glBindFramebuffer(ARBFramebufferObject.GL_FRAMEBUFFER, currentFB);
    }

    @Override
    public void framebufferGetPixels(GL2XFramebuffer framebuffer, int x, int y, int width, int height, int format, int type, float[] dstBuffer) {
        final int currentFB = GL11.glGetInteger(ARBFramebufferObject.GL_FRAMEBUFFER_BINDING);

        ARBFramebufferObject.glBindFramebuffer(ARBFramebufferObject.GL_FRAMEBUFFER, framebuffer.framebufferId);

        GL11.glReadPixels(
                x, y, width, height,
                format, type,
                dstBuffer);

        ARBFramebufferObject.glBindFramebuffer(ARBFramebufferObject.GL_FRAMEBUFFER, currentFB);
    }
    
    @Override
    public void framebufferGetPixels(GL2XFramebuffer framebuffer, int x, int y, int width, int height, int format, int type, ByteBuffer dstBuffer) {
        final int currentFB = GL11.glGetInteger(ARBFramebufferObject.GL_FRAMEBUFFER_BINDING);

        ARBFramebufferObject.glBindFramebuffer(ARBFramebufferObject.GL_FRAMEBUFFER, framebuffer.framebufferId);

        GL11.glReadPixels(
                x, y, width, height,
                format, type,
                dstBuffer);

        ARBFramebufferObject.glBindFramebuffer(ARBFramebufferObject.GL_FRAMEBUFFER, currentFB);
    }

    @Override
    public boolean framebufferIsComplete(GL2XFramebuffer framebuffer) {
        final int currentFb = GL11.glGetInteger(ARBFramebufferObject.GL_FRAMEBUFFER_BINDING);

        ARBFramebufferObject.glBindFramebuffer(ARBFramebufferObject.GL_FRAMEBUFFER, framebuffer.framebufferId);
        final int complete = ARBFramebufferObject.glCheckFramebufferStatus(ARBFramebufferObject.GL_FRAMEBUFFER);

        ARBFramebufferObject.glBindFramebuffer(ARBFramebufferObject.GL_FRAMEBUFFER, currentFb);
        return complete == ARBFramebufferObject.GL_FRAMEBUFFER_COMPLETE;
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
    public GL2XProgram programCreate() {
        GL2XProgram program = new GL2XProgram();
        program.programId = GL20.glCreateProgram();
        return program;
    }

    @Override
    public void programDelete(GL2XProgram program) {
        GL20.glDeleteProgram(program.programId);
        program.programId = -1;
    }

    @Override
    public void programDispatchCompute(GL2XProgram program, int numX, int numY, int numZ) {
        throw new UnsupportedOperationException("Compute shaders are not supported!");
    }

    @Override
    public int programGetUniformLocation(GL2XProgram program, String name) {
        final int currentProgram = GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM);

        GL20.glUseProgram(program.programId);
        final int res = GL20.glGetUniformLocation(program.programId, name);
        GL20.glUseProgram(currentProgram);
        return res;
    }

    @Override
    public void programLinkShaders(GL2XProgram program, Shader[] shaders) {
        for (Shader shader : shaders) {
            GL20.glAttachShader(program.programId, ((GL2XShader) shader).shaderId);
        }

        GL20.glLinkProgram(program.programId);

        for (Shader shader : shaders) {
            GL20.glDetachShader(program.programId, ((GL2XShader) shader).shaderId);
        }
    }

    @Override
    public void programSetAttribLocation(GL2XProgram program, int index, String name) {
        GL20.glBindAttribLocation(program.programId, index, name);
    }

    @Override
    public void programSetFeedbackVaryings(GL2XProgram program, String[] varyings) {
        throw new UnsupportedOperationException("Feedback buffers is not supported!");
    }

    @Override
    public void programSetUniformD(GL2XProgram program, int uLoc, double[] value) {
        throw new UnsupportedOperationException("64bit uniforms are not supported!");
    }

    @Override
    public void programSetUniformF(GL2XProgram program, int uLoc, float[] value) {
        if (GL.getCapabilities().GL_ARB_separate_shader_objects) {
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
        } else {
            final int currentProgram = GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM);

            switch (value.length) {
                case 1:
                    GL20.glUseProgram(program.programId);
                    GL20.glUniform1f(uLoc, value[0]);
                    GL20.glUseProgram(currentProgram);
                    break;
                case 2:
                    GL20.glUseProgram(program.programId);
                    GL20.glUniform2f(uLoc, value[0], value[1]);
                    GL20.glUseProgram(currentProgram);
                    break;
                case 3:
                    GL20.glUseProgram(program.programId);
                    GL20.glUniform3f(uLoc, value[0], value[1], value[2]);
                    GL20.glUseProgram(currentProgram);
                    break;
                case 4:
                    GL20.glUseProgram(program.programId);
                    GL20.glUniform4f(uLoc, value[0], value[1], value[2], value[3]);
                    GL20.glUseProgram(currentProgram);
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported vector size: " + value.length);
            }
        }
    }

    @Override
    public void programSetUniformI(GL2XProgram program, int uLoc, int[] value) {
        if (GL.getCapabilities().GL_ARB_separate_shader_objects) {
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
        } else {
            final int currentProgram = GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM);

            switch (value.length) {
                case 1:
                    GL20.glUseProgram(program.programId);
                    GL20.glUniform1i(uLoc, value[0]);
                    GL20.glUseProgram(currentProgram);
                    break;
                case 2:
                    GL20.glUseProgram(program.programId);
                    GL20.glUniform2i(uLoc, value[0], value[1]);
                    GL20.glUseProgram(currentProgram);
                    break;
                case 3:
                    GL20.glUseProgram(program.programId);
                    GL20.glUniform3i(uLoc, value[0], value[1], value[2]);
                    GL20.glUseProgram(currentProgram);
                    break;
                case 4:
                    GL20.glUseProgram(program.programId);
                    GL20.glUniform4i(uLoc, value[0], value[1], value[2], value[3]);
                    GL20.glUseProgram(currentProgram);
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported uniform vector size: " + value.length);
            }
        }
    }

    @Override
    public void programSetUniformMatD(GL2XProgram program, int uLoc, DoubleBuffer mat) {
        throw new UnsupportedOperationException("64bit uniforms are not supported!");
    }          

    @Override
    public void programSetUniformMatD(GL2XProgram program, int uLoc, double[] mat) {
        throw new UnsupportedOperationException("64bit uniforms are not supported!");
    }

    @Override
    public void programSetUniformMatF(GL2XProgram program, int uLoc, float[] mat) {
        if (GL.getCapabilities().GL_ARB_separate_shader_objects) {
            switch (mat.length) {
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
                    throw new UnsupportedOperationException("Unsupported matrix size: " + mat.length);
            }
        } else {
            final int currentProgram = GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM);

            switch (mat.length) {
                case 4:
                    GL20.glUseProgram(program.programId);
                    GL20.glUniformMatrix2fv(uLoc, false, mat);
                    GL20.glUseProgram(currentProgram);
                    break;
                case 9:
                    GL20.glUseProgram(program.programId);
                    GL20.glUniformMatrix3fv(uLoc, false, mat);
                    GL20.glUseProgram(currentProgram);
                    break;
                case 16:
                    GL20.glUseProgram(program.programId);
                    GL20.glUniformMatrix4fv(uLoc, false, mat);
                    GL20.glUseProgram(currentProgram);
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported matrix size: " + mat.length);
            }
        }
    }
    
    @Override
    public void programSetUniformMatF(GL2XProgram program, int uLoc, FloatBuffer mat) {
        if (GL.getCapabilities().GL_ARB_separate_shader_objects) {
            switch (mat.limit()) {
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
            final int currentProgram = GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM);

            switch (mat.limit()) {
                case 4:
                    GL20.glUseProgram(program.programId);
                    GL20.glUniformMatrix2fv(uLoc, false, mat);
                    GL20.glUseProgram(currentProgram);
                    break;
                case 9:
                    GL20.glUseProgram(program.programId);
                    GL20.glUniformMatrix3fv(uLoc, false, mat);
                    GL20.glUseProgram(currentProgram);
                    break;
                case 16:
                    GL20.glUseProgram(program.programId);
                    GL20.glUniformMatrix4fv(uLoc, false, mat);
                    GL20.glUseProgram(currentProgram);
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported matrix size: " + mat.limit());
            }
        }
    }

    @Override
    public void programUse(GL2XProgram program) {
        GL20.glUseProgram(program.programId);
    }

    @Override
    public GL2XRenderbuffer renderbufferCreate(int internalFormat, int width, int height) {
        final GL2XRenderbuffer renderbuffer = new GL2XRenderbuffer();

        renderbuffer.renderbufferId = ARBFramebufferObject.glGenRenderbuffers();
        ARBFramebufferObject.glBindRenderbuffer(ARBFramebufferObject.GL_RENDERBUFFER, renderbuffer.renderbufferId);
        ARBFramebufferObject.glRenderbufferStorage(ARBFramebufferObject.GL_RENDERBUFFER, internalFormat, width, height);

        return renderbuffer;
    }

    @Override
    public void renderbufferDelete(GL2XRenderbuffer renderbuffer) {
        ARBFramebufferObject.glDeleteRenderbuffers(renderbuffer.renderbufferId);
        renderbuffer.renderbufferId = -1;
    }

    @Override
    public void samplerBind(int unit, GL2XSampler sampler) {
        ARBSamplerObjects.glBindSampler(unit, sampler.samplerId);
    }

    @Override
    public GL2XSampler samplerCreate() {
        final GL2XSampler sampler = new GL2XSampler();
        sampler.samplerId = ARBSamplerObjects.glGenSamplers();
        return sampler;
    }

    @Override
    public void samplerDelete(GL2XSampler sampler) {
        ARBSamplerObjects.glDeleteSamplers(sampler.samplerId);
        sampler.samplerId = -1;
    }

    @Override
    public void samplerSetParameter(GL2XSampler sampler, int param, int value) {
        ARBSamplerObjects.glSamplerParameteri(sampler.samplerId, param, value);
    }

    @Override
    public void samplerSetParameter(GL2XSampler sampler, int param, float value) {
        ARBSamplerObjects.glSamplerParameterf(sampler.samplerId, param, value);
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
    public GL2XShader shaderCompile(int type, String source) {
        final GL2XShader shader = new GL2XShader();

        shader.shaderId = GL20.glCreateShader(type);
        GL20.glShaderSource(shader.shaderId, source);
        GL20.glCompileShader(shader.shaderId);
        return shader;
    }

    @Override
    public void shaderDelete(GL2XShader shader) {
        GL20.glDeleteShader(shader.shaderId);
        shader.shaderId = -1;
    }

    @Override
    public String shaderGetInfoLog(GL2XShader shader) {
        return GL20.glGetShaderInfoLog(shader.shaderId);
    }

    @Override
    public int shaderGetParameterI(GL2XShader shader, int pName) {
        return GL20.glGetShaderi(shader.shaderId, pName);
    }

    @Override
    public GL2XTexture textureAllocate(int mipmaps, int internalFormat, int width, int height, int depth, int dataType) {
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

        final GL2XTexture texture = new GL2XTexture();

        texture.textureId = GL11.glGenTextures();
        texture.target = target;
        texture.internalFormat = internalFormat;

        int currentTexture;
        switch (target) {
            case GL11.GL_TEXTURE_1D:
                currentTexture = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_1D);
                GL11.glBindTexture(GL11.GL_TEXTURE_1D, texture.textureId);
                GL11.glTexParameteri(GL11.GL_TEXTURE_1D, GL12.GL_TEXTURE_BASE_LEVEL, 0);
                GL11.glTexParameteri(GL11.GL_TEXTURE_1D, GL12.GL_TEXTURE_MAX_LEVEL, mipmaps);

                for (int i = 0; i < mipmaps; i++) {
                    GL11.glTexImage1D(GL11.GL_TEXTURE_1D, i, internalFormat, width, 0, guessFormat(internalFormat), dataType, 0);
                    width = Math.max(1, (width / 2));
                }
                GL11.glBindTexture(GL11.GL_TEXTURE_1D, currentTexture);
                break;
            case GL11.GL_TEXTURE_2D:
                currentTexture = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.textureId);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_BASE_LEVEL, 0);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_MAX_LEVEL, mipmaps);

                for (int i = 0; i < mipmaps; i++) {
                    GL11.glTexImage2D(GL11.GL_TEXTURE_2D, i, internalFormat, width, height, 0, guessFormat(internalFormat), dataType, 0);
                    width = Math.max(1, (width / 2));
                    height = Math.max(1, (height / 2));
                }

                GL11.glBindTexture(GL11.GL_TEXTURE_2D, currentTexture);
                break;
            case GL12.GL_TEXTURE_3D:
                currentTexture = GL11.glGetInteger(GL12.GL_TEXTURE_BINDING_3D);
                GL11.glBindTexture(GL12.GL_TEXTURE_3D, texture.textureId);
                GL11.glTexParameteri(GL12.GL_TEXTURE_3D, GL12.GL_TEXTURE_BASE_LEVEL, 0);
                GL11.glTexParameteri(GL12.GL_TEXTURE_3D, GL12.GL_TEXTURE_MAX_LEVEL, mipmaps);

                for (int i = 0; i < mipmaps; i++) {
                    GL12.glTexImage3D(GL12.GL_TEXTURE_3D, i, internalFormat, width, height, depth, 0, guessFormat(internalFormat), dataType, 0);
                    width = Math.max(1, (width / 2));
                    height = Math.max(1, (height / 2));
                    depth = Math.max(1, (depth / 2));
                }

                GL11.glBindTexture(GL12.GL_TEXTURE_3D, currentTexture);
                break;
        }

        return texture;
    }

    @Override
    public void textureBind(GL2XTexture texture, int unit) {
        GL13.glActiveTexture(GL13.GL_TEXTURE0 + unit);
        GL11.glBindTexture(texture.target, texture.textureId);
    }

    @Override
    public void textureDelete(GL2XTexture texture) {
        GL11.glDeleteTextures(texture.textureId);
        texture.textureId = -1;
    }

    @Override
    public void textureGenerateMipmap(GL2XTexture texture) {
        final int binding;

        switch (texture.target) {
            case GL11.GL_TEXTURE_1D:
                binding = GL11.GL_TEXTURE_BINDING_1D;
                break;
            case GL11.GL_TEXTURE_2D:
                binding = GL11.GL_TEXTURE_BINDING_2D;
                break;
            case GL12.GL_TEXTURE_3D:
                binding = GL12.GL_TEXTURE_BINDING_3D;
                break;
            default:
                throw new UnsupportedOperationException("Unsupported texture target: " + texture.target);
        }

        final int currentTexture = GL11.glGetInteger(binding);

        GL11.glBindTexture(texture.target, texture.textureId);
        ARBFramebufferObject.glGenerateMipmap(texture.target);
        GL11.glBindTexture(texture.target, currentTexture);
    }
    
    @Override
    public void textureGetData(GL2XTexture texture, int level, int format, int type, int[] out) {
        final int binding;

        switch (texture.target) {
            case GL11.GL_TEXTURE_1D:
                binding = GL11.GL_TEXTURE_BINDING_1D;
                break;
            case GL11.GL_TEXTURE_2D:
                binding = GL11.GL_TEXTURE_BINDING_2D;
                break;
            case GL12.GL_TEXTURE_3D:
                binding = GL12.GL_TEXTURE_BINDING_3D;
                break;
            default:
                throw new UnsupportedOperationException("Unsupported texture target: " + texture.target);
        }

        final int currentTexture = GL11.glGetInteger(binding);

        GL11.glBindTexture(texture.target, texture.textureId);
        GL11.glGetTexImage(texture.target, level, format, type, out);
        GL11.glBindTexture(texture.target, currentTexture);
    }

    @Override
    public void textureGetData(GL2XTexture texture, int level, int format, int type, float[] out) {
        final int binding;

        switch (texture.target) {
            case GL11.GL_TEXTURE_1D:
                binding = GL11.GL_TEXTURE_BINDING_1D;
                break;
            case GL11.GL_TEXTURE_2D:
                binding = GL11.GL_TEXTURE_BINDING_2D;
                break;
            case GL12.GL_TEXTURE_3D:
                binding = GL12.GL_TEXTURE_BINDING_3D;
                break;
            default:
                throw new UnsupportedOperationException("Unsupported texture target: " + texture.target);
        }

        final int currentTexture = GL11.glGetInteger(binding);

        GL11.glBindTexture(texture.target, texture.textureId);
        GL11.glGetTexImage(texture.target, level, format, type, out);
        GL11.glBindTexture(texture.target, currentTexture);
    }

    @Override
    public void textureGetData(GL2XTexture texture, int level, int format, int type, ByteBuffer out) {
        final int binding;

        switch (texture.target) {
            case GL11.GL_TEXTURE_1D:
                binding = GL11.GL_TEXTURE_BINDING_1D;
                break;
            case GL11.GL_TEXTURE_2D:
                binding = GL11.GL_TEXTURE_BINDING_2D;
                break;
            case GL12.GL_TEXTURE_3D:
                binding = GL12.GL_TEXTURE_BINDING_3D;
                break;
            default:
                throw new UnsupportedOperationException("Unsupported texture target: " + texture.target);
        }

        final int currentTexture = GL11.glGetInteger(binding);

        GL11.glBindTexture(texture.target, texture.textureId);
        GL11.glGetTexImage(texture.target, level, format, type, out);
        GL11.glBindTexture(texture.target, currentTexture);
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
    public void textureInvalidateData(GL2XTexture texture, int level) {
        ARBInvalidateSubdata.glInvalidateTexImage(texture.target, level);
    }

    @Override
    public void textureInvalidateRange(GL2XTexture texture, int level, int xOffset, int yOffset, int zOffset, int width, int height, int depth) {
        ARBInvalidateSubdata.glInvalidateTexSubImage(texture.textureId, level, xOffset, yOffset, zOffset, width, height, depth);
    }

    @Override
    public long textureMap(GL2XTexture tt) {
        throw new UnsupportedOperationException("ARB_bindless_texture requires OpenGL 4.0!");
    }
    
    @Override
    public void textureSetData(GL2XTexture texture, int level, int xOffset, int yOffset, int zOffset, int width, int height, int depth, int format, int type, int[] data) {
        switch (texture.target) {
            case GL11.GL_TEXTURE_1D: {
                final int currentTexture = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_1D);

                GL11.glBindTexture(GL11.GL_TEXTURE_1D, texture.textureId);
                GL11.glTexSubImage1D(GL11.GL_TEXTURE_1D, level, xOffset, width, format, type, data);
                GL11.glBindTexture(GL11.GL_TEXTURE_1D, currentTexture);
            }
            break;
            case GL11.GL_TEXTURE_2D: {
                final int currentTexture = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);

                GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.textureId);
                GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, level, xOffset, yOffset, width, height, format, type, data);
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, currentTexture);
            }
            break;
            case GL12.GL_TEXTURE_3D: {
                final int currentTexture = GL11.glGetInteger(GL12.GL_TEXTURE_BINDING_3D);

                GL11.glBindTexture(GL12.GL_TEXTURE_3D, texture.textureId);
                GL12.glTexSubImage3D(GL12.GL_TEXTURE_3D, level, xOffset, yOffset, zOffset, width, height, depth, format, type, data);
                GL11.glBindTexture(GL12.GL_TEXTURE_3D, currentTexture);
            }
            break;

        }
    }

    @Override
    public void textureSetData(GL2XTexture texture, int level, int xOffset, int yOffset, int zOffset, int width, int height, int depth, int format, int type, float[] data) {
        switch (texture.target) {
            case GL11.GL_TEXTURE_1D: {
                final int currentTexture = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_1D);

                GL11.glBindTexture(GL11.GL_TEXTURE_1D, texture.textureId);
                GL11.glTexSubImage1D(GL11.GL_TEXTURE_1D, level, xOffset, width, format, type, data);
                GL11.glBindTexture(GL11.GL_TEXTURE_1D, currentTexture);
            }
            break;
            case GL11.GL_TEXTURE_2D: {
                final int currentTexture = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);

                GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.textureId);
                GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, level, xOffset, yOffset, width, height, format, type, data);
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, currentTexture);
            }
            break;
            case GL12.GL_TEXTURE_3D: {
                final int currentTexture = GL11.glGetInteger(GL12.GL_TEXTURE_BINDING_3D);

                GL11.glBindTexture(GL12.GL_TEXTURE_3D, texture.textureId);
                GL12.glTexSubImage3D(GL12.GL_TEXTURE_3D, level, xOffset, yOffset, zOffset, width, height, depth, format, type, data);
                GL11.glBindTexture(GL12.GL_TEXTURE_3D, currentTexture);
            }
            break;
        }
    }

    @Override
    public void textureSetData(GL2XTexture texture, int level, int xOffset, int yOffset, int zOffset, int width, int height, int depth, int format, int type, ByteBuffer data) {
        switch (texture.target) {
            case GL11.GL_TEXTURE_1D: {
                final int currentTexture = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_1D);

                GL11.glBindTexture(GL11.GL_TEXTURE_1D, texture.textureId);
                GL11.glTexSubImage1D(GL11.GL_TEXTURE_1D, level, xOffset, width, format, type, data);
                GL11.glBindTexture(GL11.GL_TEXTURE_1D, currentTexture);
            }
            break;
            case GL11.GL_TEXTURE_2D: {
                final int currentTexture = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);

                GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.textureId);
                GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, level, xOffset, yOffset, width, height, format, type, data);
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, currentTexture);
            }
            break;
            case GL12.GL_TEXTURE_3D: {
                final int currentTexture = GL11.glGetInteger(GL12.GL_TEXTURE_BINDING_3D);

                GL11.glBindTexture(GL12.GL_TEXTURE_3D, texture.textureId);
                GL12.glTexSubImage3D(GL12.GL_TEXTURE_3D, level, xOffset, yOffset, zOffset, width, height, depth, format, type, data);
                GL11.glBindTexture(GL12.GL_TEXTURE_3D, currentTexture);
            }
            break;

        }
    }

    @Override
    public void textureSetParameter(GL2XTexture texture, int param, int value) {
        final int currentTexture;

        switch (texture.target) {
            case GL11.GL_TEXTURE_1D:
                currentTexture = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_1D);
                break;
            case GL11.GL_TEXTURE_2D:
                currentTexture = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
                break;
            case GL12.GL_TEXTURE_3D:
                currentTexture = GL11.glGetInteger(GL12.GL_TEXTURE_BINDING_3D);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported texture target: " + texture.target);
        }

        GL11.glBindTexture(texture.target, texture.textureId);
        GL11.glTexParameteri(texture.target, param, value);
        GL11.glBindTexture(texture.target, currentTexture);
    }

    @Override
    public void textureSetParameter(GL2XTexture texture, int param, float value) {
        final int currentTexture;

        switch (texture.target) {
            case GL11.GL_TEXTURE_1D:
                currentTexture = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_1D);
                break;
            case GL11.GL_TEXTURE_2D:
                currentTexture = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
                break;
            case GL12.GL_TEXTURE_3D:
                currentTexture = GL11.glGetInteger(GL12.GL_TEXTURE_BINDING_3D);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported texture target: " + texture.target);
        }

        GL11.glBindTexture(texture.target, texture.textureId);
        GL11.glTexParameterf(texture.target, param, value);
        GL11.glBindTexture(texture.target, currentTexture);
    }

    @Override
    public void textureUnmap(GL2XTexture tt) {
        throw new UnsupportedOperationException("ARB_bindless_texture requires OpenGL 4.0!");
    }

    @Override
    public void transformFeedbackBegin(int i) {
        throw new UnsupportedOperationException("OpenGL 3.0 is not supported!");
    }

    @Override
    public void transformFeedbackEnd() {
        throw new UnsupportedOperationException("OpenGL 3.0 is not supported!");
    }

    @Override
    public void vertexArrayAttachBuffer(GL2XVertexArray vao, int index, GL2XBuffer buffer, int size, int type, int stride, long offset, int divisor) {
        if (GL.getCapabilities().GL_ARB_vertex_array_object) {
            final int currentVao = GL11.glGetInteger(ARBVertexArrayObject.GL_VERTEX_ARRAY_BINDING);

            ARBVertexArrayObject.glBindVertexArray(vao.vertexArrayId);

            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, buffer.bufferId);
            GL20.glEnableVertexAttribArray(index);

            if (type == GL11.GL_DOUBLE) {
                throw new UnsupportedOperationException("64bit vertex attributes are not supported!");
            } else {
                GL20.glVertexAttribPointer(index, size, type, false, stride, offset);
            }

            if (divisor > 0) {
                throw new UnsupportedOperationException("Vertex divisor is not supported!");
            }

            ARBVertexArrayObject.glBindVertexArray(currentVao);
        } else {
            GL2XVertexArray.VertexAttrib attrib = vao.new VertexAttrib();
            attrib.buffer = buffer;
            attrib.index = index;
            attrib.size = size;
            attrib.type = type;
            attrib.stride = stride;
            attrib.offset = offset;

            vao.attribs.add(attrib);
        }
    }

    @Override
    public void vertexArrayAttachIndexBuffer(GL2XVertexArray vao, GL2XBuffer buffer) {
        if (GL.getCapabilities().GL_ARB_vertex_array_object) {
            final int currentVao = GL11.glGetInteger(ARBVertexArrayObject.GL_VERTEX_ARRAY_BINDING);

            ARBVertexArrayObject.glBindVertexArray(vao.vertexArrayId);
            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer.bufferId);
            ARBVertexArrayObject.glBindVertexArray(currentVao);
        } else {
            vao.element = buffer;
        }
    }

    @Override
    public GL2XVertexArray vertexArrayCreate() {
        if (GL.getCapabilities().GL_ARB_vertex_array_object) {
            final GL2XVertexArray vao = new GL2XVertexArray();
            vao.vertexArrayId = ARBVertexArrayObject.glGenVertexArrays();
            return vao;
        } else {
            final GL2XVertexArray vao = new GL2XVertexArray();
            vao.vertexArrayId = 1;
            vao.attribs = new ArrayList<>();
            return vao;
        }
    }

    @Override
    public void vertexArrayDelete(GL2XVertexArray vao) {
        ARBVertexArrayObject.glDeleteVertexArrays(vao.vertexArrayId);
        vao.vertexArrayId = -1;
        vao.attribs = null;
        vao.element = null;
    }

    @Override
    public void vertexArrayDrawArrays(GL2XVertexArray vao, int drawMode, int start, int count) {
        if (GL.getCapabilities().GL_ARB_vertex_array_object) {
            final int currentVao = GL11.glGetInteger(ARBVertexArrayObject.GL_VERTEX_ARRAY_BINDING);
            ARBVertexArrayObject.glBindVertexArray(vao.vertexArrayId);
            GL11.glDrawArrays(drawMode, start, count);
            ARBVertexArrayObject.glBindVertexArray(currentVao);
        } else {
            vao.attribs.forEach(attrib -> {
                GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, attrib.buffer.bufferId);
                GL20.glEnableVertexAttribArray(attrib.index);
                GL20.glVertexAttribPointer(attrib.index, attrib.size, attrib.type, false, attrib.stride, attrib.offset);
            });

            GL11.glDrawArrays(drawMode, start, count);

            vao.attribs.forEach(attrib -> GL20.glDisableVertexAttribArray(attrib.index));
        }
    }

    @Override
    public void vertexArrayDrawArraysIndirect(GL2XVertexArray vat, GL2XBuffer bt, int i, long l) {
        throw new UnsupportedOperationException("ARB_draw_indirect requires OpenGL 3.1!");
    }

    @Override
    public void vertexArrayDrawArraysInstanced(GL2XVertexArray vat, int i, int i1, int i2, int i3) {
        throw new UnsupportedOperationException("OpenGL 3.1 is not supported!");
    }

    @Override
    public void vertexArrayDrawElements(GL2XVertexArray vao, int drawMode, int count, int type, long offset) {
        if (GL.getCapabilities().GL_ARB_vertex_array_object) {
            final int currentVao = GL11.glGetInteger(ARBVertexArrayObject.GL_VERTEX_ARRAY_BINDING);

            ARBVertexArrayObject.glBindVertexArray(vao.vertexArrayId);
            GL11.glDrawElements(drawMode, count, type, offset);
            ARBVertexArrayObject.glBindVertexArray(currentVao);
        } else {
            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vao.element.bufferId);
            vao.attribs.forEach(attrib -> {
                GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, attrib.buffer.bufferId);
                GL20.glEnableVertexAttribArray(attrib.index);
                GL20.glVertexAttribPointer(attrib.index, attrib.size, attrib.type, false, attrib.stride, attrib.offset);
            });

            GL11.glDrawElements(drawMode, count, type, offset);
            vao.attribs.forEach(attrib -> GL20.glDisableVertexAttribArray(attrib.index));
        }
    }

    @Override
    public void vertexArrayDrawElementsIndirect(GL2XVertexArray vao, GL2XBuffer cmdBuffer, int drawMode, int indexType, long offset) {
        throw new UnsupportedOperationException("ARB_draw_arrays_instanced requires OpenGL 3.1!");
    }

    @Override
    public void vertexArrayDrawElementsInstanced(GL2XVertexArray vao, int drawMode, int count, int type, long offset, int instanceCount) {
        throw new UnsupportedOperationException("OpenGL 3.1 is not supported!");
    }

    @Override
    public void viewportApply(int x, int y, int width, int height) {
        GL11.glViewport(x, y, width, height);
    }

    @Override
    public void textureGetData(GL2XTexture texture, int level, int format, int type, GL2XBuffer out, long offset, int size) {
        final GLCapabilities caps = GL.getCapabilities();

        if (caps.OpenGL21 || caps.GL_ARB_pixel_buffer_object) {
            final int currentTex;
            switch (texture.target) {
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
        } else {
            throw new UnsupportedOperationException("Pixel Buffer Objects are not supported!");
        }
    }

    @Override
    public void textureSetData(GL2XTexture texture, int level, int xOffset, int yOffset, int zOffset, int width, int height, int depth, int format, int type, GL2XBuffer buffer, long offset) {
        final GLCapabilities caps = GL.getCapabilities();

        if (caps.OpenGL21 || caps.GL_ARB_pixel_buffer_object) {
            final int currentTex;

            switch (texture.target) {
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
        } else {
            throw new UnsupportedOperationException("Pixel Buffer Objects are not supported!");
        }
    }        
}
