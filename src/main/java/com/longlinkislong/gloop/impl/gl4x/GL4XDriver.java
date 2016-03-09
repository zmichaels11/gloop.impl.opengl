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
public final class GL4XDriver implements Driver<
        GL4XBuffer, GL4XFramebuffer, GL4XTexture, GL4XShader, GL4XProgram, GL4XSampler, GL4XVertexArray, GL4XDrawQuery> {

    @Override
    public void blendingDisable() {
        GL11.glDisable(GL11.GL_BLEND);
    }

    @Override
    public void blendingEnable(long rgbEq, long aEq, long rgbFuncSrc, long rgbFuncDst, long aFuncSrc, long aFuncDst) {
        GL11.glEnable(GL11.GL_BLEND);
        GL14.glBlendFuncSeparate((int) rgbFuncSrc, (int) rgbFuncDst, (int) aFuncSrc, (int) aFuncDst);
        GL20.glBlendEquationSeparate((int) rgbEq, (int) aEq);
    }

    @Override
    public void bufferAllocate(GL4XBuffer buffer, long size, long usage) {
        final int currentBuffer = GL11.glGetInteger(GL15.GL_ARRAY_BUFFER_BINDING);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, buffer.bufferId);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, size, (int) usage);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, currentBuffer);
    }

    @Override
    public void bufferAllocateImmutable(GL4XBuffer buffer, long size, long bitflags) {
        final int currentBuffer = GL11.glGetInteger(GL15.GL_ARRAY_BUFFER_BINDING);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, buffer.bufferId);
        ARBBufferStorage.glBufferStorage(GL15.GL_ARRAY_BUFFER, (int) size, (int) bitflags);
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
    public long bufferGetParameter(GL4XBuffer buffer, long paramId) {
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
    public ByteBuffer bufferMapData(GL4XBuffer buffer, long offset, long length, long accessFlags) {
        final int currentBuffer = GL11.glGetInteger(GL15.GL_ARRAY_BUFFER_BINDING);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, buffer.bufferId);
        buffer.mapBuffer = GL30.glMapBufferRange(GL15.GL_ARRAY_BUFFER, offset, length, (int) accessFlags, buffer.mapBuffer);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, currentBuffer);
        return buffer.mapBuffer;
    }

    @Override
    public void bufferSetData(GL4XBuffer buffer, ByteBuffer data, long usage) {
        final int currentBuffer = GL11.glGetInteger(GL15.GL_ARRAY_BUFFER_BINDING);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, buffer.bufferId);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, data, (int) usage);
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
    public void clear(long bitfield, double red, double green, double blue, double alpha, double depth) {
        GL11.glClearColor((float) red, (float) green, (float) blue, (float) alpha);
        GL11.glClearDepth(depth);
        GL11.glClear((int) bitfield);
    }

    @Override
    public void depthTestDisable() {
        GL11.glDisable(GL11.GL_DEPTH_TEST);
    }

    @Override
    public void depthTestEnable(long depthTest) {
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthFunc((int) depthTest);
    }

    @Override
    public void drawQueryBeginConditionalRender(GL4XDrawQuery query, long mode) {
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
    public void drawQueryDisable(long condition) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void drawQueryEnable(long condition, GL4XDrawQuery query) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void drawQueryEndConditionRender() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void framebufferAddAttachment(GL4XFramebuffer framebuffer, long attachmentId, GL4XTexture texId, long mipmapLevel) {
        final int currentFb = GL11.glGetInteger(GL30.GL_FRAMEBUFFER_BINDING);

        switch (texId.target) {
            case GL11.GL_TEXTURE_1D:
                GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebuffer.framebufferId);
                GL30.glFramebufferTexture1D(GL30.GL_FRAMEBUFFER, (int) attachmentId, GL11.GL_TEXTURE_1D, texId.textureId, (int) mipmapLevel);
                GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, currentFb);
                break;
            case GL11.GL_TEXTURE_2D:
                GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebuffer.framebufferId);
                GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, (int) attachmentId, GL11.GL_TEXTURE_2D, texId.textureId, (int) mipmapLevel);
                GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, currentFb);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported texture target!");
        }
    }

    @Override
    public void framebufferAddDepthAttachment(GL4XFramebuffer framebuffer, GL4XTexture texId, long mipmapLevel) {
        final int currentFb = GL11.glGetInteger(GL30.GL_FRAMEBUFFER_BINDING);

        switch (texId.target) {
            case GL11.GL_TEXTURE_1D:
                GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebuffer.framebufferId);
                GL30.glFramebufferTexture1D(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL11.GL_TEXTURE_1D, texId.textureId, (int) mipmapLevel);
                GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, currentFb);
                break;
            case GL11.GL_TEXTURE_2D:
                GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebuffer.framebufferId);
                GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL11.GL_TEXTURE_2D, texId.textureId, (int) mipmapLevel);
                GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, currentFb);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported texture target!");
        }
    }

    @Override
    public void framebufferAddDepthStencilAttachment(GL4XFramebuffer framebuffer, GL4XTexture texId, long mipmapLevel) {
        final int currentFb = GL11.glGetInteger(GL30.GL_FRAMEBUFFER_BINDING);

        switch (texId.target) {
            case GL11.GL_TEXTURE_1D:
                GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebuffer.framebufferId);
                GL30.glFramebufferTexture1D(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_STENCIL_ATTACHMENT, GL11.GL_TEXTURE_1D, texId.textureId, (int) mipmapLevel);
                GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, currentFb);
                break;
            case GL11.GL_TEXTURE_2D:
                GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebuffer.framebufferId);
                GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_STENCIL_ATTACHMENT, GL11.GL_TEXTURE_2D, texId.textureId, (int) mipmapLevel);
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
    public void framebufferBlit(GL4XFramebuffer srcFb, long srcX0, long srcY0, long srcX1, long srcY1, GL4XFramebuffer dstFb, long dstX0, long dstY0, long dstX1, long dstY1, long bitfield, long filter) {
        final int currentReadFb = GL11.glGetInteger(GL30.GL_READ_FRAMEBUFFER_BINDING);
        final int currentDrawFb = GL11.glGetInteger(GL30.GL_DRAW_FRAMEBUFFER_BINDING);

        GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, srcFb.framebufferId);
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, dstFb.framebufferId);

        GL30.glBlitFramebuffer((int) srcX0, (int) srcY0, (int) srcX1, (int) srcY1, (int) dstX0, (int) dstY0, (int) dstX1, (int) dstY1, (int) bitfield, (int) filter);

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
    public void framebufferGetPixels(GL4XFramebuffer framebuffer, long x, long y, long width, long height, long format, long type, GL4XBuffer dstBuffer) {
        final int currentFB = GL11.glGetInteger(GL30.GL_FRAMEBUFFER_BINDING);
        final int currentBuffer = GL11.glGetInteger(GL21.GL_PIXEL_PACK_BUFFER_BINDING);

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebuffer.framebufferId);

        GL15.glBindBuffer(GL21.GL_PIXEL_PACK_BUFFER, dstBuffer.bufferId);
        GL11.glReadPixels(
                (int) x, (int) y, (int) width, (int) height,
                (int) format, (int) type,
                0L);
        GL15.glBindBuffer(GL21.GL_PIXEL_PACK_BUFFER, currentBuffer);

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, currentFB);
    }

    @Override
    public void framebufferGetPixels(GL4XFramebuffer framebuffer, long x, long y, long width, long height, long format, long type, ByteBuffer dstBuffer) {
        final int currentFB = GL11.glGetInteger(GL30.GL_FRAMEBUFFER_BINDING);

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebuffer.framebufferId);

        GL11.glReadPixels(
                (int) x, (int) y, (int) width, (int) height,
                (int) format, (int) type,
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
    public void maskApply(boolean red, boolean green, boolean blue, boolean alpha, boolean depth, long stencil) {
        GL11.glColorMask(red, green, blue, alpha);
        GL11.glDepthMask(depth);
        GL11.glStencilMask((int) stencil);
    }

    @Override
    public void polygonSetParameters(double pointSize, double lineWidth, long frontFace, long cullFace, long polygonMode, double offsetFactor, double offsetUnits) {
        GL11.glPointSize((float) pointSize);
        GL11.glLineWidth((float) lineWidth);
        GL11.glFrontFace((int) frontFace);

        if (cullFace == 0) {
            GL11.glDisable(GL11.GL_CULL_FACE);
        } else {
            GL11.glEnable(GL11.GL_CULL_FACE);
            GL11.glCullFace((int) cullFace);
        }

        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, (int) polygonMode);
        GL11.glPolygonOffset((float) offsetFactor, (float) offsetUnits);
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
    public void programDispatchCompute(GL4XProgram program, long numX, long numY, long numZ) {
        final int currentProgram = GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM);

        GL20.glUseProgram(program.programId);
        ARBComputeShader.glDispatchCompute((int) numX, (int) numY, (int) numZ);
        GL20.glUseProgram(currentProgram);
    }

    @Override
    public long programGetUniformLocation(GL4XProgram program, String name) {
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
    public void programSetAttribLocation(GL4XProgram program, long index, String name) {
        GL20.glBindAttribLocation(program.programId, (int) index, name);
    }

    @Override
    public void programSetFeedbackBuffer(GL4XProgram program, long varyingLoc, GL4XBuffer buffer) {
        GL30.glBindBufferBase(GL30.GL_TRANSFORM_FEEDBACK_BUFFER, (int) varyingLoc, buffer.bufferId);
    }

    @Override
    public void programSetFeedbackVaryings(GL4XProgram program, String[] varyings) {
        GL30.glTransformFeedbackVaryings(program.programId, varyings, GL30.GL_SEPARATE_ATTRIBS);
    }

    @Override
    public void programSetStorage(GL4XProgram program, String storageName, GL4XBuffer buffer, long bindingPoint) {
        final int sBlock = ARBProgramInterfaceQuery.glGetProgramResourceLocation(program.programId, ARBProgramInterfaceQuery.GL_SHADER_STORAGE_BLOCK, storageName);

        GL30.glBindBufferBase(ARBShaderStorageBufferObject.GL_SHADER_STORAGE_BUFFER, (int) bindingPoint, buffer.bufferId);
        ARBShaderStorageBufferObject.glShaderStorageBlockBinding(program.programId, sBlock, (int) bindingPoint);
    }

    @Override
    public void programSetUniformBlock(GL4XProgram program, String uniformName, GL4XBuffer buffer, long bindingPoint) {
        final int uBlock = GL31.glGetUniformBlockIndex(program.programId, uniformName);

        GL30.glBindBufferBase(GL31.GL_UNIFORM_BUFFER, (int) bindingPoint, buffer.bufferId);
        GL31.glUniformBlockBinding(program.programId, uBlock, (int) bindingPoint);
    }

    @Override
    public void programSetUniformD(GL4XProgram program, long uLoc, double[] value) {
        if (GL.getCapabilities().GL_ARB_separate_shader_objects) {
            switch (value.length) {
                case 1:
                    ARBSeparateShaderObjects.glProgramUniform1d(program.programId, (int) uLoc, value[0]);
                    break;
                case 2:
                    ARBSeparateShaderObjects.glProgramUniform2d(program.programId, (int) uLoc, value[0], value[1]);
                    break;
                case 3:
                    ARBSeparateShaderObjects.glProgramUniform3d(program.programId, (int) uLoc, value[0], value[1], value[2]);
                    break;
                case 4:
                    ARBSeparateShaderObjects.glProgramUniform4d(program.programId, (int) uLoc, value[0], value[1], value[2], value[3]);
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported vector size: " + value.length);
            }
        } else {
            final int currentProgram = GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM);

            switch (value.length) {
                case 1:
                    GL20.glUseProgram(program.programId);
                    GL40.glUniform1d((int) uLoc, value[0]);
                    GL20.glUseProgram(currentProgram);
                    break;
                case 2:
                    GL20.glUseProgram(program.programId);
                    GL40.glUniform2d((int) uLoc, value[0], value[1]);
                    GL20.glUseProgram(currentProgram);
                    break;
                case 3:
                    GL20.glUseProgram(program.programId);
                    GL40.glUniform3d((int) uLoc, value[0], value[1], value[2]);
                    GL20.glUseProgram(currentProgram);
                    break;
                case 4:
                    GL20.glUseProgram(program.programId);
                    GL40.glUniform4d((int) uLoc, value[0], value[1], value[2], value[3]);
                    GL20.glUseProgram(currentProgram);
                    break;
            }
        }
    }

    @Override
    public void programSetUniformF(GL4XProgram program, long uLoc, float[] value) {
        if (GL.getCapabilities().GL_ARB_separate_shader_objects) {
            switch (value.length) {
                case 1:
                    ARBSeparateShaderObjects.glProgramUniform1f(program.programId, (int) uLoc, value[0]);
                    break;
                case 2:
                    ARBSeparateShaderObjects.glProgramUniform2f(program.programId, (int) uLoc, value[0], value[1]);
                    break;
                case 3:
                    ARBSeparateShaderObjects.glProgramUniform3f(program.programId, (int) uLoc, value[0], value[1], value[2]);
                    break;
                case 4:
                    ARBSeparateShaderObjects.glProgramUniform4f(program.programId, (int) uLoc, value[0], value[1], value[2], value[3]);
                    break;
            }
        } else {
            final int currentProgram = GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM);

            switch (value.length) {
                case 1:
                    GL20.glUseProgram(program.programId);
                    GL20.glUniform1f((int) uLoc, value[0]);
                    GL20.glUseProgram(currentProgram);
                    break;
                case 2:
                    GL20.glUseProgram(program.programId);
                    GL20.glUniform2f((int) uLoc, value[0], value[1]);
                    GL20.glUseProgram(currentProgram);
                    break;
                case 3:
                    GL20.glUseProgram(program.programId);
                    GL20.glUniform3f((int) uLoc, value[0], value[1], value[2]);
                    GL20.glUseProgram(currentProgram);
                    break;
                case 4:
                    GL20.glUseProgram(program.programId);
                    GL20.glUniform4f((int) uLoc, value[0], value[1], value[2], value[3]);
                    GL20.glUseProgram(currentProgram);
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported vector size: " + value.length);
            }
        }
    }

    @Override
    public void programSetUniformI(GL4XProgram program, long uLoc, int[] value) {
        if (GL.getCapabilities().GL_ARB_separate_shader_objects) {
            switch (value.length) {
                case 1:
                    ARBSeparateShaderObjects.glProgramUniform1i(program.programId, (int) uLoc, value[0]);
                    break;
                case 2:
                    ARBSeparateShaderObjects.glProgramUniform2i(program.programId, (int) uLoc, value[0], value[1]);
                    break;
                case 3:
                    ARBSeparateShaderObjects.glProgramUniform3i(program.programId, (int) uLoc, value[0], value[1], value[2]);
                    break;
                case 4:
                    ARBSeparateShaderObjects.glProgramUniform4i(program.programId, (int) uLoc, value[0], value[1], value[2], value[3]);
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported uniform vector size: " + value.length);
            }
        } else {
            final int currentProgram = GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM);

            switch (value.length) {
                case 1:
                    GL20.glUseProgram(program.programId);
                    GL20.glUniform1i((int) uLoc, value[0]);
                    GL20.glUseProgram(currentProgram);
                    break;
                case 2:
                    GL20.glUseProgram(program.programId);
                    GL20.glUniform2i((int) uLoc, value[0], value[1]);
                    GL20.glUseProgram(currentProgram);
                    break;
                case 3:
                    GL20.glUseProgram(program.programId);
                    GL20.glUniform3i((int) uLoc, value[0], value[1], value[2]);
                    GL20.glUseProgram(currentProgram);
                    break;
                case 4:
                    GL20.glUseProgram(program.programId);
                    GL20.glUniform4i((int) uLoc, value[0], value[1], value[2], value[3]);
                    GL20.glUseProgram(currentProgram);
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported uniform vector size: " + value.length);
            }
        }
    }

    @Override
    public void programSetUniformMatD(GL4XProgram program, long uLoc, DoubleBuffer mat) {
        if (GL.getCapabilities().GL_ARB_separate_shader_objects) {
            switch (mat.limit()) {
                case 4:
                    ARBSeparateShaderObjects.glProgramUniformMatrix2dv(program.programId, (int) uLoc, false, mat);
                    break;
                case 9:
                    ARBSeparateShaderObjects.glProgramUniformMatrix3dv(program.programId, (int) uLoc, false, mat);
                    break;
                case 16:
                    ARBSeparateShaderObjects.glProgramUniformMatrix4dv(program.programId, (int) uLoc, false, mat);
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported matrix size: " + mat.limit());
            }
        } else {
            final int currentProgram = GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM);

            switch (mat.limit()) {
                case 4:
                    GL20.glUseProgram(program.programId);
                    GL40.glUniformMatrix2dv((int) uLoc, false, mat);
                    GL20.glUseProgram(currentProgram);
                    break;
                case 9:
                    GL20.glUseProgram(program.programId);
                    GL40.glUniformMatrix3dv((int) uLoc, false, mat);
                    GL20.glUseProgram(currentProgram);
                    break;
                case 16:
                    GL20.glUseProgram(program.programId);
                    GL40.glUniformMatrix4dv((int) uLoc, false, mat);
                    GL20.glUseProgram(currentProgram);
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported matrix size: " + mat.limit());
            }
        }
    }

    @Override
    public void programSetUniformMatF(GL4XProgram program, long uLoc, FloatBuffer mat) {
        if (GL.getCapabilities().GL_ARB_separate_shader_objects) {
            switch (mat.limit()) {
                case 4:
                    ARBSeparateShaderObjects.glProgramUniformMatrix2fv(program.programId, (int) uLoc, false, mat);
                    break;
                case 9:
                    ARBSeparateShaderObjects.glProgramUniformMatrix3fv(program.programId, (int) uLoc, false, mat);
                    break;
                case 16:
                    ARBSeparateShaderObjects.glProgramUniformMatrix4fv(program.programId, (int) uLoc, false, mat);
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported matrix size: " + mat.limit());
            }
        } else {
            final int currentProgram = GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM);

            switch (mat.limit()) {
                case 4:
                    GL20.glUseProgram(program.programId);
                    GL20.glUniformMatrix2fv((int) uLoc, false, mat);
                    GL20.glUseProgram(currentProgram);
                    break;
                case 9:
                    GL20.glUseProgram(program.programId);
                    GL20.glUniformMatrix3fv((int) uLoc, false, mat);
                    GL20.glUseProgram(currentProgram);
                    break;
                case 16:
                    GL20.glUseProgram(program.programId);
                    GL20.glUniformMatrix4fv((int) uLoc, false, mat);
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
    public void samplerBind(long unit, GL4XSampler sampler) {
        GL33.glBindSampler((int) unit, sampler.samplerId);
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
    public void samplerSetParameter(GL4XSampler sampler, long param, long value) {
        GL33.glSamplerParameteri(sampler.samplerId, (int) param, (int) value);
    }

    @Override
    public void samplerSetParameter(GL4XSampler sampler, long param, double value) {
        GL33.glSamplerParameterf(sampler.samplerId, (int) param, (float) value);
    }

    @Override
    public void scissorTestDisable() {
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    @Override
    public void scissorTestEnable(long left, long bottom, long width, long height) {
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor((int) left, (int) bottom, (int) width, (int) height);
    }

    @Override
    public GL4XShader shaderCompile(long type, String source) {
        final GL4XShader shader = new GL4XShader();

        shader.shaderId = GL20.glCreateShader((int) type);
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
    public long shaderGetParameter(GL4XShader shader, long pName) {
        return GL20.glGetShaderi(shader.shaderId, (int) pName);
    }

    @Override
    public GL4XTexture textureAllocate(long mipmaps, long internalFormat, long width, long height, long depth) {
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
        texture.internalFormat = (int) internalFormat;

        int currentTexture;
        switch (target) {
            case GL11.GL_TEXTURE_1D:
                currentTexture = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_1D);
                GL11.glBindTexture(GL11.GL_TEXTURE_1D, texture.textureId);
                GL11.glTexParameteri(GL11.GL_TEXTURE_1D, GL12.GL_TEXTURE_BASE_LEVEL, 0);
                GL11.glTexParameteri(GL11.GL_TEXTURE_1D, GL12.GL_TEXTURE_MAX_LEVEL, (int) mipmaps);

                for (int i = 0; i < mipmaps; i++) {
                    GL11.glTexImage1D(GL11.GL_TEXTURE_1D, i, (int) internalFormat, (int) width, 0, guessFormat((int) internalFormat), GL11.GL_UNSIGNED_BYTE, 0);
                    width = Math.max(1, (width / 2));
                }
                GL11.glBindTexture(GL11.GL_TEXTURE_1D, currentTexture);
                break;
            case GL11.GL_TEXTURE_2D:
                currentTexture = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.textureId);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_BASE_LEVEL, 0);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_MAX_LEVEL, (int) mipmaps);

                for (int i = 0; i < mipmaps; i++) {
                    GL11.glTexImage2D(GL11.GL_TEXTURE_2D, i, (int) internalFormat, (int) width, (int) height, 0, guessFormat((int) internalFormat), GL11.GL_UNSIGNED_BYTE, 0);
                    width = Math.max(1, (width / 2));
                    height = Math.max(1, (height / 2));
                }

                GL11.glBindTexture(GL11.GL_TEXTURE_2D, currentTexture);
                break;
            case GL12.GL_TEXTURE_3D:
                currentTexture = GL11.glGetInteger(GL12.GL_TEXTURE_BINDING_3D);
                GL11.glBindTexture(GL12.GL_TEXTURE_3D, texture.textureId);
                GL11.glTexParameteri(GL12.GL_TEXTURE_3D, GL12.GL_TEXTURE_BASE_LEVEL, 0);
                GL11.glTexParameteri(GL12.GL_TEXTURE_3D, GL12.GL_TEXTURE_MAX_LEVEL, (int) mipmaps);

                for (int i = 0; i < mipmaps; i++) {
                    GL12.glTexImage3D(GL12.GL_TEXTURE_3D, i, (int) internalFormat, (int) width, (int) height, (int) depth, 0, guessFormat((int) internalFormat), GL11.GL_UNSIGNED_BYTE, 0);
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
    public void textureAllocatePage(GL4XTexture texture, long level, long xOffset, long yOffset, long zOffset, long width, long height, long depth) {
        ARBSparseTexture.glTexPageCommitmentARB(
                texture.textureId, (int) level,
                (int) xOffset, (int) yOffset, (int) zOffset,
                (int) width, (int) height, (int) depth,
                true);
    }

    @Override
    public void textureBind(GL4XTexture texture, long unit) {
        GL13.glActiveTexture(GL13.GL_TEXTURE0 + (int) unit);
        GL11.glBindTexture(texture.target, texture.textureId);
    }

    @Override
    public void textureDeallocatePage(GL4XTexture texture, long level, long xOffset, long yOffset, long zOffset, long width, long height, long depth) {
        ARBSparseTexture.glTexPageCommitmentARB(
                texture.textureId, (int) level,
                (int) xOffset, (int) yOffset, (int) zOffset,
                (int) width, (int) height, (int) depth,
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
    public void textureGetData(GL4XTexture texture, long level, long format, long type, ByteBuffer out) {
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
        GL11.glGetTexImage(texture.target, (int) level, (int) format, (int) type, out);
        GL11.glBindTexture(texture.target, currentTexture);
    }

    @Override
    public double textureGetMaxAnisotropy() {
        return GL11.glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT);
    }

    @Override
    public long textureGetMaxBoundTextures() {
        return GL11.glGetInteger(GL20.GL_MAX_TEXTURE_IMAGE_UNITS);
    }

    @Override
    public long textureGetMaxSize() {
        return GL11.glGetInteger(GL11.GL_MAX_TEXTURE_SIZE);
    }

    @Override
    public long textureGetPageDepth(GL4XTexture texture) {
        return ARBInternalformatQuery.glGetInternalformati(texture.target, texture.internalFormat, ARBSparseTexture.GL_VIRTUAL_PAGE_SIZE_Z_ARB);
    }

    @Override
    public long textureGetPageHeight(GL4XTexture texture) {
        return ARBInternalformatQuery.glGetInternalformati(texture.target, texture.internalFormat, ARBSparseTexture.GL_VIRTUAL_PAGE_SIZE_Y_ARB);
    }

    @Override
    public long textureGetPageWidth(GL4XTexture texture) {
        return ARBInternalformatQuery.glGetInternalformati(texture.target, texture.internalFormat, ARBSparseTexture.GL_VIRTUAL_PAGE_SIZE_X_ARB);
    }

    @Override
    public long textureGetPreferredFormat(long internalFormat) {
        return GL11.GL_RGBA;
    }

    @Override
    public void textureInvalidateData(GL4XTexture texture, long level) {
        ARBInvalidateSubdata.glInvalidateTexImage(texture.target, (int) level);
    }

    @Override
    public void textureInvalidateRange(GL4XTexture texture, long level, long xOffset, long yOffset, long zOffset, long width, long height, long depth) {
        ARBInvalidateSubdata.glInvalidateTexSubImage(texture.textureId, (int) level, (int) xOffset, (int) yOffset, (int) zOffset, (int) width, (int) height, (int) depth);
    }

    @Override
    public void textureSetData(GL4XTexture texture, long level, long xOffset, long yOffset, long zOffset, long width, long height, long depth, long format, long type, ByteBuffer data) {
        switch (texture.target) {
            case GL11.GL_TEXTURE_1D: {
                final int currentTexture = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_1D);

                GL11.glBindTexture(GL11.GL_TEXTURE_1D, texture.textureId);
                GL11.glTexSubImage1D(GL11.GL_TEXTURE_1D, (int) level, (int) xOffset, (int) width, (int) format, (int) type, data);
                GL11.glBindTexture(GL11.GL_TEXTURE_1D, currentTexture);
            }
            break;
            case GL11.GL_TEXTURE_2D: {
                final int currentTexture = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);

                GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.textureId);
                GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, (int) level, (int) xOffset, (int) yOffset, (int) width, (int) height, (int) format, (int) type, data);
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, currentTexture);
            }
            break;
            case GL12.GL_TEXTURE_3D: {
                final int currentTexture = GL11.glGetInteger(GL12.GL_TEXTURE_BINDING_3D);

                GL11.glBindTexture(GL12.GL_TEXTURE_3D, texture.textureId);
                GL12.glTexSubImage3D(GL12.GL_TEXTURE_3D, (int) level, (int) xOffset, (int) yOffset, (int) zOffset, (int) width, (int) height, (int) depth, (int) format, (int) type, data);
                GL11.glBindTexture(GL12.GL_TEXTURE_3D, currentTexture);
            }
            break;

        }
    }

    @Override
    public void textureSetParameter(GL4XTexture texture, long param, long value) {
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
        GL11.glTexParameteri(texture.target, (int) param, (int) value);
        GL11.glBindTexture(texture.target, currentTexture);
    }

    @Override
    public void textureSetParameter(GL4XTexture texture, long param, double value) {
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
        GL11.glTexParameterf(texture.target, (int) param, (float) value);
        GL11.glBindTexture(texture.target, currentTexture);
    }

    @Override
    public void vertexArrayAttachBuffer(GL4XVertexArray vao, long index, GL4XBuffer buffer, long size, long type, long stride, long offset, long divisor) {
        final int currentVao = GL11.glGetInteger(GL30.GL_VERTEX_ARRAY_BINDING);

        GL30.glBindVertexArray(vao.vertexArrayId);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, buffer.bufferId);
        GL20.glEnableVertexAttribArray((int) index);

        if (type == GL11.GL_DOUBLE) {
            ARBVertexAttrib64Bit.glVertexAttribLPointer((int) index, (int) size, (int) type, (int) stride, offset);
        } else {
            GL20.glVertexAttribPointer((int) index, (int) size, (int) type, false, (int) stride, offset);
        }

        if (divisor > 0) {
            GL33.glVertexAttribDivisor((int) index, (int) divisor);
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
    public void vertexArrayDrawArrays(GL4XVertexArray vao, long drawMode, long start, long count) {
        final int currentVao = GL11.glGetInteger(GL30.GL_VERTEX_ARRAY_BINDING);
        GL30.glBindVertexArray(vao.vertexArrayId);
        GL11.glDrawArrays((int) drawMode, (int) start, (int) count);
        GL30.glBindVertexArray(currentVao);
    }

    @Override
    public void vertexArrayDrawArraysIndirect(GL4XVertexArray vao, GL4XBuffer cmdBuffer, long drawMode, long offset) {
        final int currentVao = GL11.glGetInteger(GL30.GL_VERTEX_ARRAY_BINDING);
        final int currentIndirect = GL11.glGetInteger(GL40.GL_DRAW_INDIRECT_BUFFER_BINDING);

        GL30.glBindVertexArray(vao.vertexArrayId);
        GL15.glBindBuffer(GL40.GL_DRAW_INDIRECT_BUFFER, cmdBuffer.bufferId);
        GL40.glDrawArraysIndirect((int) drawMode, offset);
        GL15.glBindBuffer(GL40.GL_DRAW_INDIRECT_BUFFER, currentIndirect);
        GL30.glBindVertexArray(currentVao);
    }

    @Override
    public void vertexArrayDrawArraysInstanced(GL4XVertexArray vao, long drawMode, long first, long count, long instanceCount) {
        final int currentVao = GL11.glGetInteger(GL30.GL_VERTEX_ARRAY_BINDING);

        GL30.glBindVertexArray(vao.vertexArrayId);
        GL31.glDrawArraysInstanced((int) drawMode, (int) first, (int) count, (int) instanceCount);
        GL30.glBindVertexArray(currentVao);
    }

    @Override
    public void vertexArrayDrawElements(GL4XVertexArray vao, long drawMode, long count, long type, long offset) {
        final int currentVao = GL11.glGetInteger(GL30.GL_VERTEX_ARRAY_BINDING);

        GL30.glBindVertexArray(vao.vertexArrayId);
        GL11.glDrawElements((int) drawMode, (int) count, (int) type, offset);
        GL30.glBindVertexArray(currentVao);
    }

    @Override
    public void vertexArrayDrawElementsIndirect(GL4XVertexArray vao, GL4XBuffer cmdBuffer, long drawMode, long indexType, long offset) {
        final int currentVao = GL11.glGetInteger(GL30.GL_VERTEX_ARRAY_BINDING);
        final int currentIndirect = GL11.glGetInteger(GL40.GL_DRAW_INDIRECT_BUFFER_BINDING);

        GL30.glBindVertexArray(vao.vertexArrayId);
        GL15.glBindBuffer(GL40.GL_DRAW_INDIRECT_BUFFER, cmdBuffer.bufferId);
        GL40.glDrawElementsIndirect((int) drawMode, (int) indexType, offset);
        GL15.glBindBuffer(GL40.GL_DRAW_INDIRECT_BUFFER, currentIndirect);
        GL30.glBindVertexArray(currentVao);
    }

    @Override
    public void vertexArrayDrawElementsInstanced(GL4XVertexArray vao, long drawMode, long count, long type, long offset, long instanceCount) {
        final int currentVao = GL11.glGetInteger(GL30.GL_VERTEX_ARRAY_BINDING);

        GL30.glBindVertexArray(vao.vertexArrayId);
        GL31.glDrawElementsInstanced((int) drawMode, (int) count, (int) type, offset, (int) instanceCount);
        GL30.glBindVertexArray(currentVao);
    }

    @Override
    public void vertexArrayDrawTransformFeedback(GL4XVertexArray vao, long drawMode, long start, long count) {
        final int currentVao = GL11.glGetInteger(GL30.GL_VERTEX_ARRAY_BINDING);

        GL30.glBindVertexArray(vao.vertexArrayId);
        GL11.glEnable(GL30.GL_RASTERIZER_DISCARD);
        GL30.glBeginTransformFeedback((int) drawMode);
        GL11.glDrawArrays((int) drawMode, (int) start, (int) count);
        GL30.glEndTransformFeedback();
        GL11.glDisable(GL30.GL_RASTERIZER_DISCARD);
        GL30.glBindVertexArray(currentVao);
    }

    @Override
    public void vertexArrayMultiDrawArrays(GL4XVertexArray vao, long drawMode, IntBuffer first, IntBuffer count) {
        final int currentVao = GL11.glGetInteger(GL30.GL_VERTEX_ARRAY_BINDING);

        GL30.glBindVertexArray(vao.vertexArrayId);
        GL14.glMultiDrawArrays((int) drawMode, first, count);
        GL30.glBindVertexArray(currentVao);
    }

    @Override
    public void viewportApply(long x, long y, long width, long height) {
        GL11.glViewport((int) x, (int) y, (int) width, (int) height);
    }
}
