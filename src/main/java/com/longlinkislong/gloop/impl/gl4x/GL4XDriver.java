/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop.impl.gl4x;

import com.longlinkislong.gloop.spi.Driver;
import com.longlinkislong.gloop.spi.Shader;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.lwjgl.opengl.ARBBufferStorage;
import org.lwjgl.opengl.ARBComputeShader;
import org.lwjgl.opengl.ARBInternalformatQuery;
import org.lwjgl.opengl.ARBInvalidateSubdata;
import org.lwjgl.opengl.ARBProgramInterfaceQuery;
import org.lwjgl.opengl.ARBSeparateShaderObjects;
import org.lwjgl.opengl.ARBShaderStorageBufferObject;
import org.lwjgl.opengl.ARBSparseTexture;
import org.lwjgl.opengl.ARBVertexAttrib64Bit;
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
import org.lwjgl.opengl.GL40;

/**
 *
 * @author zmichaels
 */
final class GL4XDriver implements Driver<
        GL4XBuffer, GL4XFramebuffer, GL4XTexture, GL4XShader, GL4XProgram, GL4XSampler, GL4XVertexArray, GL4XDrawQuery> {

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
    public void bufferAllocate(GL4XBuffer buffer, long size, int usage) {
        final int currentBuffer = GL11.glGetInteger(GL15.GL_ARRAY_BUFFER_BINDING);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, buffer.bufferId);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, size, usage);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, currentBuffer);
    }

    @Override
    public void bufferAllocateImmutable(GL4XBuffer buffer, long size, int bitflags) {
        final int currentBuffer = GL11.glGetInteger(GL15.GL_ARRAY_BUFFER_BINDING);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, buffer.bufferId);
        ARBBufferStorage.glBufferStorage(GL15.GL_ARRAY_BUFFER, size, bitflags);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, currentBuffer);
    }

    @Override
    public void bufferCopyData(GL4XBuffer srcBuffer, long srcOffset, GL4XBuffer dstBuffer, long dstOffset, long size) {
        GL15.glBindBuffer(GL31.GL_COPY_READ_BUFFER, srcBuffer.bufferId);
        GL15.glBindBuffer(GL31.GL_COPY_WRITE_BUFFER, dstBuffer.bufferId);

        GL31.glCopyBufferSubData(GL31.GL_COPY_READ_BUFFER, GL31.GL_COPY_WRITE_BUFFER, srcOffset, dstOffset, size);

        GL15.glBindBuffer(GL31.GL_COPY_READ_BUFFER, 0);
        GL15.glBindBuffer(GL31.GL_COPY_WRITE_BUFFER, 0);
    }

    @Override
    public GL4XBuffer bufferCreate() {
        final GL4XBuffer buffer = new GL4XBuffer();
        buffer.bufferId = GL15.glGenBuffers();
        return buffer;
    }

    @Override
    public void bufferDelete(GL4XBuffer buffer) {
        GL15.glDeleteBuffers(buffer.bufferId);
        buffer.bufferId = -1;
    }

    @Override
    public void bufferGetData(GL4XBuffer buffer, long offset, ByteBuffer out) {
        final int currentBuffer = GL11.glGetInteger(GL15.GL_ARRAY_BUFFER_BINDING);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, buffer.bufferId);
        GL15.glGetBufferSubData(GL15.GL_ARRAY_BUFFER, offset, out);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, currentBuffer);
    }

    @Override
    public int bufferGetParameterI(GL4XBuffer buffer, int paramId) {
        final int currentAB = GL11.glGetInteger(GL15.GL_ARRAY_BUFFER_BINDING);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, buffer.bufferId);
        final int res = GL15.glGetBufferParameteri(GL15.GL_ARRAY_BUFFER, buffer.bufferId);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, currentAB);
        return res;
    }

    @Override
    public void bufferInvalidateData(GL4XBuffer buffer) {
        ARBInvalidateSubdata.glInvalidateBufferData(buffer.bufferId);
    }

    @Override
    public void bufferInvalidateRange(GL4XBuffer buffer, long offset, long length) {
        ARBInvalidateSubdata.glInvalidateBufferSubData(buffer.bufferId, offset, length);
    }

    @Override
    public ByteBuffer bufferMapData(GL4XBuffer buffer, long offset, long length, int accessFlags) {
        final int currentBuffer = GL11.glGetInteger(GL15.GL_ARRAY_BUFFER_BINDING);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, buffer.bufferId);
        buffer.mapBuffer = GL30.glMapBufferRange(GL15.GL_ARRAY_BUFFER, offset, length, accessFlags, buffer.mapBuffer);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, currentBuffer);
        return buffer.mapBuffer;
    }

    @Override
    public void bufferSetData(GL4XBuffer buffer, ByteBuffer data, int usage) {
        final int currentBuffer = GL11.glGetInteger(GL15.GL_ARRAY_BUFFER_BINDING);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, buffer.bufferId);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, data, usage);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, currentBuffer);
    }

    @Override
    public void bufferUnmapData(GL4XBuffer buffer) {
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
    public void drawQueryBeginConditionalRender(GL4XDrawQuery query, int mode) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public GL4XDrawQuery drawQueryCreate() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void drawQueryDelete(GL4XDrawQuery query) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void drawQueryDisable(int condition) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void drawQueryEnable(int condition, GL4XDrawQuery query) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void drawQueryEndConditionRender() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void framebufferAddAttachment(GL4XFramebuffer framebuffer, int attachmentId, GL4XTexture texId, int mipmapLevel) {
        final int currentFb = GL11.glGetInteger(GL30.GL_FRAMEBUFFER_BINDING);

        switch (texId.target) {
            case GL11.GL_TEXTURE_1D:
                GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebuffer.framebufferId);
                GL30.glFramebufferTexture1D(GL30.GL_FRAMEBUFFER, attachmentId, GL11.GL_TEXTURE_1D, texId.textureId, mipmapLevel);
                GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, currentFb);
                break;
            case GL11.GL_TEXTURE_2D:
                GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebuffer.framebufferId);
                GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, attachmentId, GL11.GL_TEXTURE_2D, texId.textureId, mipmapLevel);
                GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, currentFb);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported texture target!");
        }
    }

    @Override
    public void framebufferAddDepthAttachment(GL4XFramebuffer framebuffer, GL4XTexture texId, int mipmapLevel) {
        final int currentFb = GL11.glGetInteger(GL30.GL_FRAMEBUFFER_BINDING);

        switch (texId.target) {
            case GL11.GL_TEXTURE_1D:
                GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebuffer.framebufferId);
                GL30.glFramebufferTexture1D(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL11.GL_TEXTURE_1D, texId.textureId, mipmapLevel);
                GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, currentFb);
                break;
            case GL11.GL_TEXTURE_2D:
                GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebuffer.framebufferId);
                GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL11.GL_TEXTURE_2D, texId.textureId, mipmapLevel);
                GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, currentFb);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported texture target!");
        }
    }

    @Override
    public void framebufferAddDepthStencilAttachment(GL4XFramebuffer framebuffer, GL4XTexture texId, int mipmapLevel) {
        final int currentFb = GL11.glGetInteger(GL30.GL_FRAMEBUFFER_BINDING);

        switch (texId.target) {
            case GL11.GL_TEXTURE_1D:
                GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebuffer.framebufferId);
                GL30.glFramebufferTexture1D(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_STENCIL_ATTACHMENT, GL11.GL_TEXTURE_1D, texId.textureId, mipmapLevel);
                GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, currentFb);
                break;
            case GL11.GL_TEXTURE_2D:
                GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebuffer.framebufferId);
                GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_STENCIL_ATTACHMENT, GL11.GL_TEXTURE_2D, texId.textureId, mipmapLevel);
                GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, currentFb);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported texture target!");
        }
    }

    @Override
    public void framebufferBind(GL4XFramebuffer framebuffer, IntBuffer attachments) {
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebuffer.framebufferId);

        if (attachments != null) {
            GL20.glDrawBuffers(attachments);
        }
    }

    @Override
    public void framebufferBlit(GL4XFramebuffer srcFb, int srcX0, int srcY0, int srcX1, int srcY1, GL4XFramebuffer dstFb, int dstX0, int dstY0, int dstX1, int dstY1, int bitfield, int filter) {
        final int currentReadFb = GL11.glGetInteger(GL30.GL_READ_FRAMEBUFFER_BINDING);
        final int currentDrawFb = GL11.glGetInteger(GL30.GL_DRAW_FRAMEBUFFER_BINDING);

        GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, srcFb.framebufferId);
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, dstFb.framebufferId);

        GL30.glBlitFramebuffer(srcX0, srcY0, srcX1, srcY1, dstX0, dstY0, dstX1, dstY1, bitfield, filter);

        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, currentDrawFb);
        GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, currentReadFb);
    }

    @Override
    public GL4XFramebuffer framebufferCreate() {
        final GL4XFramebuffer fb = new GL4XFramebuffer();
        fb.framebufferId = GL30.glGenFramebuffers();
        return fb;
    }

    @Override
    public void framebufferDelete(GL4XFramebuffer framebuffer) {
        GL30.glDeleteFramebuffers(framebuffer.framebufferId);
        framebuffer.framebufferId = -1;
    }

    @Override
    public GL4XFramebuffer framebufferGetDefault() {
        final GL4XFramebuffer fb = new GL4XFramebuffer();
        fb.framebufferId = 0;
        return fb;
    }

    @Override
    public void framebufferGetPixels(GL4XFramebuffer framebuffer, int x, int y, int width, int height, int format, int type, GL4XBuffer dstBuffer) {
        final int currentFB = GL11.glGetInteger(GL30.GL_FRAMEBUFFER_BINDING);
        final int currentBuffer = GL11.glGetInteger(GL21.GL_PIXEL_PACK_BUFFER_BINDING);

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebuffer.framebufferId);

        GL15.glBindBuffer(GL21.GL_PIXEL_PACK_BUFFER, dstBuffer.bufferId);
        GL11.glReadPixels(
                x, y, width, height,
                format, type,
                0L);
        GL15.glBindBuffer(GL21.GL_PIXEL_PACK_BUFFER, currentBuffer);

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, currentFB);
    }

    @Override
    public void framebufferGetPixels(GL4XFramebuffer framebuffer, int x, int y, int width, int height, int format, int type, ByteBuffer dstBuffer) {
        final int currentFB = GL11.glGetInteger(GL30.GL_FRAMEBUFFER_BINDING);

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebuffer.framebufferId);

        GL11.glReadPixels(
                x, y, width, height,
                format, type,
                dstBuffer);

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, currentFB);
    }

    @Override
    public boolean framebufferIsComplete(GL4XFramebuffer framebuffer) {
        final int currentFb = GL11.glGetInteger(GL30.GL_FRAMEBUFFER_BINDING);

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebuffer.framebufferId);
        final int complete = GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER);

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, currentFb);
        return complete == GL30.GL_FRAMEBUFFER_COMPLETE;
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
    public GL4XProgram programCreate() {
        GL4XProgram program = new GL4XProgram();
        program.programId = GL20.glCreateProgram();
        return program;
    }

    @Override
    public void programDelete(GL4XProgram program) {
        GL20.glDeleteProgram(program.programId);
        program.programId = -1;
    }

    @Override
    public void programDispatchCompute(GL4XProgram program, int numX, int numY, int numZ) {
        final int currentProgram = GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM);

        GL20.glUseProgram(program.programId);
        ARBComputeShader.glDispatchCompute(numX, numY, numZ);
        GL20.glUseProgram(currentProgram);
    }

    @Override
    public int programGetUniformLocation(GL4XProgram program, String name) {
        final int currentProgram = GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM);

        GL20.glUseProgram(program.programId);
        final int res = GL20.glGetUniformLocation(program.programId, name);
        GL20.glUseProgram(currentProgram);
        return res;
    }

    @Override
    public void programLinkShaders(GL4XProgram program, Shader[] shaders) {
        for (Shader shader : shaders) {
            GL20.glAttachShader(program.programId, ((GL4XShader) shader).shaderId);
        }

        GL20.glLinkProgram(program.programId);

        for (Shader shader : shaders) {
            GL20.glDetachShader(program.programId, ((GL4XShader) shader).shaderId);
        }
    }

    @Override
    public void programSetAttribLocation(GL4XProgram program, int index, String name) {
        GL20.glBindAttribLocation(program.programId, index, name);
    }

    @Override
    public void programSetFeedbackBuffer(GL4XProgram program, int varyingLoc, GL4XBuffer buffer) {
        GL30.glBindBufferBase(GL30.GL_TRANSFORM_FEEDBACK_BUFFER, varyingLoc, buffer.bufferId);
    }

    @Override
    public void programSetFeedbackVaryings(GL4XProgram program, String[] varyings) {
        GL30.glTransformFeedbackVaryings(program.programId, varyings, GL30.GL_SEPARATE_ATTRIBS);
    }

    @Override
    public void programSetStorage(GL4XProgram program, String storageName, GL4XBuffer buffer, int bindingPoint) {
        final int sBlock = ARBProgramInterfaceQuery.glGetProgramResourceLocation(program.programId, ARBProgramInterfaceQuery.GL_SHADER_STORAGE_BLOCK, storageName);

        GL30.glBindBufferBase(ARBShaderStorageBufferObject.GL_SHADER_STORAGE_BUFFER, bindingPoint, buffer.bufferId);
        ARBShaderStorageBufferObject.glShaderStorageBlockBinding(program.programId, sBlock, bindingPoint);
    }

    @Override
    public void programSetUniformBlock(GL4XProgram program, String uniformName, GL4XBuffer buffer, int bindingPoint) {
        final int uBlock = GL31.glGetUniformBlockIndex(program.programId, uniformName);

        GL30.glBindBufferBase(GL31.GL_UNIFORM_BUFFER, bindingPoint, buffer.bufferId);
        GL31.glUniformBlockBinding(program.programId, uBlock, bindingPoint);
    }

    @Override
    public void programSetUniformD(GL4XProgram program, int uLoc, double[] value) {
        if (GL.getCapabilities().GL_ARB_separate_shader_objects) {
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
            final int currentProgram = GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM);

            switch (value.length) {
                case 1:
                    GL20.glUseProgram(program.programId);
                    GL40.glUniform1d(uLoc, value[0]);
                    GL20.glUseProgram(currentProgram);
                    break;
                case 2:
                    GL20.glUseProgram(program.programId);
                    GL40.glUniform2d(uLoc, value[0], value[1]);
                    GL20.glUseProgram(currentProgram);
                    break;
                case 3:
                    GL20.glUseProgram(program.programId);
                    GL40.glUniform3d(uLoc, value[0], value[1], value[2]);
                    GL20.glUseProgram(currentProgram);
                    break;
                case 4:
                    GL20.glUseProgram(program.programId);
                    GL40.glUniform4d(uLoc, value[0], value[1], value[2], value[3]);
                    GL20.glUseProgram(currentProgram);
                    break;
            }
        }
    }

    @Override
    public void programSetUniformF(GL4XProgram program, int uLoc, float[] value) {
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
    public void programSetUniformI(GL4XProgram program, int uLoc, int[] value) {
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
    public void programSetUniformMatD(GL4XProgram program, int uLoc, DoubleBuffer mat) {
        if (GL.getCapabilities().GL_ARB_separate_shader_objects) {
            switch (mat.limit()) {
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
            final int currentProgram = GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM);

            switch (mat.limit()) {
                case 4:
                    GL20.glUseProgram(program.programId);
                    GL40.glUniformMatrix2dv(uLoc, false, mat);
                    GL20.glUseProgram(currentProgram);
                    break;
                case 9:
                    GL20.glUseProgram(program.programId);
                    GL40.glUniformMatrix3dv(uLoc, false, mat);
                    GL20.glUseProgram(currentProgram);
                    break;
                case 16:
                    GL20.glUseProgram(program.programId);
                    GL40.glUniformMatrix4dv(uLoc, false, mat);
                    GL20.glUseProgram(currentProgram);
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported matrix size: " + mat.limit());
            }
        }
    }

    @Override
    public void programSetUniformMatF(GL4XProgram program, int uLoc, FloatBuffer mat) {
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
    public void programUse(GL4XProgram program) {
        GL20.glUseProgram(program.programId);
    }

    @Override
    public void samplerBind(int unit, GL4XSampler sampler) {
        GL33.glBindSampler(unit, sampler.samplerId);
    }

    @Override
    public GL4XSampler samplerCreate() {
        final GL4XSampler sampler = new GL4XSampler();
        sampler.samplerId = GL33.glGenSamplers();
        return sampler;
    }

    @Override
    public void samplerDelete(GL4XSampler sampler) {
        GL33.glDeleteSamplers(sampler.samplerId);
        sampler.samplerId = -1;
    }

    @Override
    public void samplerSetParameter(GL4XSampler sampler, int param, int value) {
        GL33.glSamplerParameteri(sampler.samplerId, param, value);
    }

    @Override
    public void samplerSetParameter(GL4XSampler sampler, int param, float value) {
        GL33.glSamplerParameterf(sampler.samplerId, param, value);
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
    public GL4XShader shaderCompile(int type, String source) {
        final GL4XShader shader = new GL4XShader();

        shader.shaderId = GL20.glCreateShader(type);
        GL20.glShaderSource(shader.shaderId, source);
        GL20.glCompileShader(shader.shaderId);
        return shader;
    }

    @Override
    public void shaderDelete(GL4XShader shader) {
        GL20.glDeleteShader(shader.shaderId);
        shader.shaderId = -1;
    }

    @Override
    public String shaderGetInfoLog(GL4XShader shader) {
        return GL20.glGetShaderInfoLog(shader.shaderId);
    }

    @Override
    public int shaderGetParameterI(GL4XShader shader, int pName) {
        return GL20.glGetShaderi(shader.shaderId, pName);
    }

    @Override
    public GL4XTexture textureAllocate(int mipmaps, int internalFormat, int width, int height, int depth) {
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

        final GL4XTexture texture = new GL4XTexture();

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
                    GL11.glTexImage1D(GL11.GL_TEXTURE_1D, i, internalFormat, width, 0, guessFormat(internalFormat), GL11.GL_UNSIGNED_BYTE, 0);
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
                    GL11.glTexImage2D(GL11.GL_TEXTURE_2D, i, internalFormat, width, height, 0, guessFormat(internalFormat), GL11.GL_UNSIGNED_BYTE, 0);
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
                    GL12.glTexImage3D(GL12.GL_TEXTURE_3D, i, internalFormat, width, height, depth, 0, guessFormat(internalFormat), GL11.GL_UNSIGNED_BYTE, 0);
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
    public void textureAllocatePage(GL4XTexture texture, int level, int xOffset, int yOffset, int zOffset, int width, int height, int depth) {
        ARBSparseTexture.glTexPageCommitmentARB(
                texture.textureId, level,
                xOffset, yOffset, zOffset,
                width, height, depth,
                true);
    }

    @Override
    public void textureBind(GL4XTexture texture, int unit) {
        GL13.glActiveTexture(GL13.GL_TEXTURE0 + unit);
        GL11.glBindTexture(texture.target, texture.textureId);
    }

    @Override
    public void textureDeallocatePage(GL4XTexture texture, int level, int xOffset, int yOffset, int zOffset, int width, int height, int depth) {
        ARBSparseTexture.glTexPageCommitmentARB(
                texture.textureId, level,
                xOffset, yOffset, zOffset,
                width, height, depth,
                false);
    }

    @Override
    public void textureDelete(GL4XTexture texture) {
        GL11.glDeleteTextures(texture.textureId);
        texture.textureId = -1;
    }

    @Override
    public void textureGenerateMipmap(GL4XTexture texture) {
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
        GL30.glGenerateMipmap(texture.target);
        GL11.glBindTexture(texture.target, currentTexture);
    }

    @Override
    public void textureGetData(GL4XTexture texture, int level, int format, int type, ByteBuffer out) {
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
    public int textureGetPageDepth(GL4XTexture texture) {
        return ARBInternalformatQuery.glGetInternalformati(texture.target, texture.internalFormat, ARBSparseTexture.GL_VIRTUAL_PAGE_SIZE_Z_ARB);
    }

    @Override
    public int textureGetPageHeight(GL4XTexture texture) {
        return ARBInternalformatQuery.glGetInternalformati(texture.target, texture.internalFormat, ARBSparseTexture.GL_VIRTUAL_PAGE_SIZE_Y_ARB);
    }

    @Override
    public int textureGetPageWidth(GL4XTexture texture) {
        return ARBInternalformatQuery.glGetInternalformati(texture.target, texture.internalFormat, ARBSparseTexture.GL_VIRTUAL_PAGE_SIZE_X_ARB);
    }

    @Override
    public int textureGetPreferredFormat(int internalFormat) {
        return GL11.GL_RGBA;
    }

    @Override
    public void textureInvalidateData(GL4XTexture texture, int level) {
        ARBInvalidateSubdata.glInvalidateTexImage(texture.target, level);
    }

    @Override
    public void textureInvalidateRange(GL4XTexture texture, int level, int xOffset, int yOffset, int zOffset, int width, int height, int depth) {
        ARBInvalidateSubdata.glInvalidateTexSubImage(texture.textureId, level, xOffset, yOffset, zOffset, width, height, depth);
    }

    @Override
    public void textureSetData(GL4XTexture texture, int level, int xOffset, int yOffset, int zOffset, int width, int height, int depth, int format, int type, ByteBuffer data) {
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
    public void textureSetParameter(GL4XTexture texture, int param, int value) {
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
    public void textureSetParameter(GL4XTexture texture, int param, float value) {
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
    public void vertexArrayAttachBuffer(GL4XVertexArray vao, int index, GL4XBuffer buffer, int size, int type, int stride, long offset, int divisor) {
        final int currentVao = GL11.glGetInteger(GL30.GL_VERTEX_ARRAY_BINDING);

        GL30.glBindVertexArray(vao.vertexArrayId);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, buffer.bufferId);
        GL20.glEnableVertexAttribArray(index);

        if (type == GL11.GL_DOUBLE) {
            ARBVertexAttrib64Bit.glVertexAttribLPointer(index, size, type, stride, offset);
        } else {
            GL20.glVertexAttribPointer(index, size, type, false, stride, offset);
        }

        if (divisor > 0) {
            GL33.glVertexAttribDivisor(index, divisor);
        }

        GL30.glBindVertexArray(currentVao);
    }

    @Override
    public void vertexArrayAttachIndexBuffer(GL4XVertexArray vao, GL4XBuffer buffer) {
        final int currentVao = GL11.glGetInteger(GL30.GL_VERTEX_ARRAY_BINDING);

        GL30.glBindVertexArray(vao.vertexArrayId);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer.bufferId);
        GL30.glBindVertexArray(currentVao);
    }

    @Override
    public GL4XVertexArray vertexArrayCreate() {
        final GL4XVertexArray vao = new GL4XVertexArray();
        vao.vertexArrayId = GL30.glGenVertexArrays();
        return vao;
    }

    @Override
    public void vertexArrayDelete(GL4XVertexArray vao) {
        GL30.glDeleteVertexArrays(vao.vertexArrayId);
        vao.vertexArrayId = -1;
    }

    @Override
    public void vertexArrayDrawArrays(GL4XVertexArray vao, int drawMode, int start, int count) {
        final int currentVao = GL11.glGetInteger(GL30.GL_VERTEX_ARRAY_BINDING);
        GL30.glBindVertexArray(vao.vertexArrayId);
        GL11.glDrawArrays(drawMode, start, count);
        GL30.glBindVertexArray(currentVao);
    }

    @Override
    public void vertexArrayDrawArraysIndirect(GL4XVertexArray vao, GL4XBuffer cmdBuffer, int drawMode, long offset) {
        final int currentVao = GL11.glGetInteger(GL30.GL_VERTEX_ARRAY_BINDING);
        final int currentIndirect = GL11.glGetInteger(GL40.GL_DRAW_INDIRECT_BUFFER_BINDING);

        GL30.glBindVertexArray(vao.vertexArrayId);
        GL15.glBindBuffer(GL40.GL_DRAW_INDIRECT_BUFFER, cmdBuffer.bufferId);
        GL40.glDrawArraysIndirect(drawMode, offset);
        GL15.glBindBuffer(GL40.GL_DRAW_INDIRECT_BUFFER, currentIndirect);
        GL30.glBindVertexArray(currentVao);
    }

    @Override
    public void vertexArrayDrawArraysInstanced(GL4XVertexArray vao, int drawMode, int first, int count, int instanceCount) {
        final int currentVao = GL11.glGetInteger(GL30.GL_VERTEX_ARRAY_BINDING);

        GL30.glBindVertexArray(vao.vertexArrayId);
        GL31.glDrawArraysInstanced(drawMode, first, count, instanceCount);
        GL30.glBindVertexArray(currentVao);
    }

    @Override
    public void vertexArrayDrawElements(GL4XVertexArray vao, int drawMode, int count, int type, long offset) {
        final int currentVao = GL11.glGetInteger(GL30.GL_VERTEX_ARRAY_BINDING);

        GL30.glBindVertexArray(vao.vertexArrayId);
        GL11.glDrawElements(drawMode, count, type, offset);
        GL30.glBindVertexArray(currentVao);
    }

    @Override
    public void vertexArrayDrawElementsIndirect(GL4XVertexArray vao, GL4XBuffer cmdBuffer, int drawMode, int indexType, long offset) {
        final int currentVao = GL11.glGetInteger(GL30.GL_VERTEX_ARRAY_BINDING);
        final int currentIndirect = GL11.glGetInteger(GL40.GL_DRAW_INDIRECT_BUFFER_BINDING);

        GL30.glBindVertexArray(vao.vertexArrayId);
        GL15.glBindBuffer(GL40.GL_DRAW_INDIRECT_BUFFER, cmdBuffer.bufferId);
        GL40.glDrawElementsIndirect(drawMode, indexType, offset);
        GL15.glBindBuffer(GL40.GL_DRAW_INDIRECT_BUFFER, currentIndirect);
        GL30.glBindVertexArray(currentVao);
    }

    @Override
    public void vertexArrayDrawElementsInstanced(GL4XVertexArray vao, int drawMode, int count, int type, long offset, int instanceCount) {
        final int currentVao = GL11.glGetInteger(GL30.GL_VERTEX_ARRAY_BINDING);

        GL30.glBindVertexArray(vao.vertexArrayId);
        GL31.glDrawElementsInstanced(drawMode, count, type, offset, instanceCount);
        GL30.glBindVertexArray(currentVao);
    }

    @Override
    public void vertexArrayDrawTransformFeedback(GL4XVertexArray vao, int drawMode, int start, int count) {
        final int currentVao = GL11.glGetInteger(GL30.GL_VERTEX_ARRAY_BINDING);

        GL30.glBindVertexArray(vao.vertexArrayId);
        GL11.glEnable(GL30.GL_RASTERIZER_DISCARD);
        GL30.glBeginTransformFeedback(drawMode);
        GL11.glDrawArrays(drawMode, start, count);
        GL30.glEndTransformFeedback();
        GL11.glDisable(GL30.GL_RASTERIZER_DISCARD);
        GL30.glBindVertexArray(currentVao);
    }

    @Override
    public void vertexArrayMultiDrawArrays(GL4XVertexArray vao, int drawMode, IntBuffer first, IntBuffer count) {
        final int currentVao = GL11.glGetInteger(GL30.GL_VERTEX_ARRAY_BINDING);

        GL30.glBindVertexArray(vao.vertexArrayId);
        GL14.glMultiDrawArrays(drawMode, first, count);
        GL30.glBindVertexArray(currentVao);
    }

    @Override
    public void viewportApply(int x, int y, int width, int height) {
        GL11.glViewport(x, y, width, height);
    }
}
