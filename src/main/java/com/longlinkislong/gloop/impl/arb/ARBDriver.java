/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop.impl.arb;

import com.longlinkislong.gloop.spi.Driver;
import com.longlinkislong.gloop.spi.Shader;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.lwjgl.opengl.ARBComputeShader;
import org.lwjgl.opengl.ARBDirectStateAccess;
import org.lwjgl.opengl.ARBDrawIndirect;
import org.lwjgl.opengl.ARBFramebufferObject;
import org.lwjgl.opengl.ARBGPUShaderFP64;
import org.lwjgl.opengl.ARBInternalformatQuery;
import org.lwjgl.opengl.ARBInvalidateSubdata;
import org.lwjgl.opengl.ARBProgramInterfaceQuery;
import org.lwjgl.opengl.ARBSamplerObjects;
import org.lwjgl.opengl.ARBSeparateShaderObjects;
import org.lwjgl.opengl.ARBShaderStorageBufferObject;
import org.lwjgl.opengl.ARBSparseTexture;
import org.lwjgl.opengl.ARBUniformBufferObject;
import org.lwjgl.opengl.ARBVertexArrayObject;
import org.lwjgl.opengl.EXTTextureFilterAnisotropic;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL21;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GLCapabilities;

/**
 *
 * @author zmichaels
 */
public final class ARBDriver implements Driver<
        ARBBuffer, ARBFramebuffer, ARBTexture, ARBShader, ARBProgram, ARBSampler, ARBVertexArray, ARBDrawQuery> {

    private static final boolean ALLOW_ARB_DRIVER = Boolean.getBoolean("com.longlinkislong.gloop.allow_arb_driver");

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
    public void bufferAllocate(ARBBuffer buffer, long size, long usage) {
        ARBDirectStateAccess.glNamedBufferData(buffer.bufferId, (int) size, (int) usage);
    }

    @Override
    public void bufferAllocateImmutable(ARBBuffer buffer, long size, long bitflags) {
        ARBDirectStateAccess.glNamedBufferStorage(buffer.bufferId, (int) size, (int) bitflags);
    }

    @Override
    public void bufferCopyData(ARBBuffer srcBuffer, long srcOffset, ARBBuffer dstBuffer, long dstOffset, long size) {
        ARBDirectStateAccess.glCopyNamedBufferSubData(srcBuffer.bufferId, dstBuffer.bufferId, srcOffset, dstOffset, size);
    }

    @Override
    public ARBBuffer bufferCreate() {
        final ARBBuffer buffer = new ARBBuffer();
        buffer.bufferId = ARBDirectStateAccess.glCreateBuffers();
        return buffer;
    }

    @Override
    public void bufferDelete(ARBBuffer buffer) {
        GL15.glDeleteBuffers(buffer.bufferId);
        buffer.bufferId = -1;
    }

    @Override
    public void bufferGetData(ARBBuffer buffer, long offset, ByteBuffer out) {
        ARBDirectStateAccess.glGetNamedBufferSubData(buffer.bufferId, (int) offset, out);
    }

    @Override
    public long bufferGetParameter(ARBBuffer buffer, long paramId) {
        return ARBDirectStateAccess.glGetNamedBufferParameteri(buffer.bufferId, (int) paramId);
    }

    @Override
    public void bufferInvalidateData(ARBBuffer buffer) {
        ARBInvalidateSubdata.glInvalidateBufferData(buffer.bufferId);
    }

    @Override
    public void bufferInvalidateRange(ARBBuffer buffer, long offset, long length) {
        ARBInvalidateSubdata.glInvalidateBufferSubData(buffer.bufferId, offset, length);
    }

    @Override
    public ByteBuffer bufferMapData(ARBBuffer buffer, long offset, long length, long accessFlags) {
        buffer.mapBuffer = ARBDirectStateAccess.glMapNamedBufferRange(buffer.bufferId, offset, length, (int) accessFlags, buffer.mapBuffer);
        return buffer.mapBuffer;
    }

    @Override
    public void bufferSetData(ARBBuffer buffer, ByteBuffer data, long usage) {
        ARBDirectStateAccess.glNamedBufferData(buffer.bufferId, data, (int) usage);
    }

    @Override
    public void bufferUnmapData(ARBBuffer buffer) {
        ARBDirectStateAccess.glUnmapNamedBuffer(buffer.bufferId);
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
    public void drawQueryBeginConditionalRender(ARBDrawQuery query, long mode) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ARBDrawQuery drawQueryCreate() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void drawQueryDelete(ARBDrawQuery query) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void drawQueryDisable(long condition) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void drawQueryEnable(long condition, ARBDrawQuery query) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void drawQueryEndConditionRender() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void framebufferAddAttachment(ARBFramebuffer framebuffer, long attachmentId, ARBTexture texture, long mipmapLevel) {
        ARBDirectStateAccess.glNamedFramebufferTexture(
                framebuffer.framebufferId,
                (int) attachmentId,
                texture.textureId,
                (int) mipmapLevel);
    }

    @Override
    public void framebufferAddDepthAttachment(ARBFramebuffer framebuffer, ARBTexture texture, long mipmapLevel) {
        ARBDirectStateAccess.glNamedFramebufferTexture(
                framebuffer.framebufferId,
                ARBFramebufferObject.GL_DEPTH_ATTACHMENT,
                texture.textureId,
                (int) mipmapLevel);
    }

    @Override
    public void framebufferAddDepthStencilAttachment(ARBFramebuffer framebuffer, ARBTexture texture, long mipmapLevel) {
        ARBDirectStateAccess.glNamedFramebufferTexture(
                framebuffer.framebufferId,
                ARBFramebufferObject.GL_DEPTH_STENCIL_ATTACHMENT,
                texture.textureId,
                (int) mipmapLevel);
    }

    @Override
    public void framebufferBind(ARBFramebuffer framebuffer, IntBuffer attachments) {
        ARBFramebufferObject.glBindFramebuffer(ARBFramebufferObject.GL_FRAMEBUFFER, framebuffer.framebufferId);

        if (attachments != null) {
            ARBDirectStateAccess.glNamedFramebufferDrawBuffers(framebuffer.framebufferId, attachments);
        }
    }

    @Override
    public void framebufferBlit(ARBFramebuffer srcFb, long srcX0, long srcY0, long srcX1, long srcY1, ARBFramebuffer dstFb, long dstX0, long dstY0, long dstX1, long dstY1, long bitfield, long filter) {
        ARBDirectStateAccess.glBlitNamedFramebuffer(srcFb.framebufferId,
                dstFb.framebufferId,
                (int) srcX0, (int) srcY0, (int) srcX1, (int) srcY1,
                (int) dstX0, (int) dstY0, (int) dstX1, (int) dstY1,
                (int) bitfield, (int) filter);
    }

    @Override
    public ARBFramebuffer framebufferCreate() {
        final ARBFramebuffer fb = new ARBFramebuffer();
        fb.framebufferId = ARBDirectStateAccess.glCreateFramebuffers();
        return fb;
    }

    @Override
    public void framebufferDelete(ARBFramebuffer framebuffer) {
        ARBFramebufferObject.glDeleteFramebuffers(framebuffer.framebufferId);
        framebuffer.framebufferId = -1;
    }

    @Override
    public ARBFramebuffer framebufferGetDefault() {
        final ARBFramebuffer framebuffer = new ARBFramebuffer();
        framebuffer.framebufferId = 0;
        return framebuffer;
    }

    @Override
    public void framebufferGetPixels(ARBFramebuffer framebuffer, long x, long y, long width, long height, long format, long type, ARBBuffer dstBuffer) {
        final int currentFB = GL11.glGetInteger(ARBFramebufferObject.GL_FRAMEBUFFER);
        final int currentBuffer = GL11.glGetInteger(GL21.GL_PIXEL_PACK_BUFFER_BINDING);

        ARBFramebufferObject.glBindFramebuffer(ARBFramebufferObject.GL_FRAMEBUFFER, framebuffer.framebufferId);

        GL15.glBindBuffer(GL21.GL_PIXEL_PACK_BUFFER, dstBuffer.bufferId);
        GL11.glReadPixels(
                (int) x, (int) y, (int) width, (int) height,
                (int) format, (int) type,
                0L);
        GL15.glBindBuffer(GL21.GL_PIXEL_PACK_BUFFER, currentBuffer);

        ARBFramebufferObject.glBindFramebuffer(ARBFramebufferObject.GL_FRAMEBUFFER, currentFB);
    }

    @Override
    public void framebufferGetPixels(ARBFramebuffer framebuffer, long x, long y, long width, long height, long format, long type, ByteBuffer dstBuffer) {
        final int currentFB = GL11.glGetInteger(ARBFramebufferObject.GL_FRAMEBUFFER);

        ARBFramebufferObject.glBindFramebuffer(ARBFramebufferObject.GL_FRAMEBUFFER, framebuffer.framebufferId);

        GL11.glReadPixels(
                (int) x, (int) y, (int) width, (int) height,
                (int) format, (int) type,
                dstBuffer);

        ARBFramebufferObject.glBindFramebuffer(ARBFramebufferObject.GL_FRAMEBUFFER, currentFB);
    }

    @Override
    public boolean framebufferIsComplete(ARBFramebuffer framebuffer) {
        return ARBDirectStateAccess.glCheckNamedFramebufferStatus(framebuffer.framebufferId, ARBFramebufferObject.GL_FRAMEBUFFER) == ARBFramebufferObject.GL_FRAMEBUFFER_COMPLETE;
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
    public ARBProgram programCreate() {
        ARBProgram program = new ARBProgram();
        program.programId = GL20.glCreateProgram();
        return program;
    }

    @Override
    public void programDelete(ARBProgram program) {
        GL20.glDeleteProgram(program.programId);
        program.programId = -1;
    }

    @Override
    public void programDispatchCompute(ARBProgram program, long numX, long numY, long numZ) {

        final int currentProgram = GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM);

        GL20.glUseProgram(program.programId);
        ARBComputeShader.glDispatchCompute((int) numX, (int) numY, (int) numZ);
        GL20.glUseProgram(currentProgram);
    }

    @Override
    public long programGetUniformLocation(ARBProgram program, String name) {
        final int currentProgram = GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM);

        GL20.glUseProgram(program.programId);
        final int res = GL20.glGetUniformLocation(program.programId, name);
        GL20.glUseProgram(currentProgram);
        return res;
    }

    @Override
    public void programLinkShaders(ARBProgram program, Shader[] shaders) {
        for (Shader shader : shaders) {
            GL20.glAttachShader(program.programId, ((ARBShader) shader).shaderId);
        }

        GL20.glLinkProgram(program.programId);

        for (Shader shader : shaders) {
            GL20.glDetachShader(program.programId, ((ARBShader) shader).shaderId);
        }
    }

    @Override
    public void programSetAttribLocation(ARBProgram program, long index, String name) {
        GL20.glBindAttribLocation(program.programId, (int) index, name);
    }

    @Override
    public void programSetFeedbackBuffer(ARBProgram program, long varyingLoc, ARBBuffer buffer) {
        ARBUniformBufferObject.glBindBufferBase(GL30.GL_TRANSFORM_FEEDBACK_BUFFER, (int) varyingLoc, buffer.bufferId);
    }

    @Override
    public void programSetFeedbackVaryings(ARBProgram program, String[] varyings) {
        GL30.glTransformFeedbackVaryings(program.programId, varyings, GL30.GL_SEPARATE_ATTRIBS);
    }

    @Override
    public void programSetStorage(ARBProgram program, String storageName, ARBBuffer buffer, long bindingPoint) {
        final int sBlock = ARBProgramInterfaceQuery.glGetProgramResourceLocation(program.programId, ARBProgramInterfaceQuery.GL_SHADER_STORAGE_BLOCK, storageName);

        GL30.glBindBufferBase(ARBShaderStorageBufferObject.GL_SHADER_STORAGE_BUFFER, (int) bindingPoint, buffer.bufferId);
        ARBShaderStorageBufferObject.glShaderStorageBlockBinding(program.programId, sBlock, (int) bindingPoint);
    }

    @Override
    public void programSetUniformBlock(ARBProgram program, String uniformName, ARBBuffer buffer, long bindingPoint) {
        final int uBlock = ARBUniformBufferObject.glGetUniformBlockIndex(program.programId, uniformName);

        ARBUniformBufferObject.glBindBufferBase(ARBUniformBufferObject.GL_UNIFORM_BUFFER, (int) bindingPoint, buffer.bufferId);
        ARBUniformBufferObject.glUniformBlockBinding(program.programId, uBlock, (int) bindingPoint);
    }

    @Override
    public void programSetUniformD(ARBProgram program, long uLoc, double[] value) {
        final GLCapabilities cap = GL.getCapabilities();

        if (!(cap.GL_ARB_gpu_shader_fp64 && cap.GL_ARB_gpu_shader_int64)) {
            throw new UnsupportedOperationException("64bit uniforms are not supported!");
        }

        if (cap.GL_ARB_separate_shader_objects) {
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
                    throw new UnsupportedOperationException("Unsupported uniform vector size: " + value.length);
            }
        } else {
            final int currentProgram = GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM);

            switch (value.length) {
                case 1:
                    GL20.glUseProgram(program.programId);
                    ARBGPUShaderFP64.glUniform1d((int) uLoc, value[0]);
                    GL20.glUseProgram(currentProgram);
                    break;
                case 2:
                    GL20.glUseProgram(program.programId);
                    ARBGPUShaderFP64.glUniform2d((int) uLoc, value[0], value[1]);
                    GL20.glUseProgram(currentProgram);
                    break;
                case 3:
                    GL20.glUseProgram(program.programId);
                    ARBGPUShaderFP64.glUniform3d((int) uLoc, value[0], value[1], value[2]);
                    GL20.glUseProgram(currentProgram);
                    break;
                case 4:
                    GL20.glUseProgram(program.programId);
                    ARBGPUShaderFP64.glUniform4d((int) uLoc, value[0], value[1], value[2], value[3]);
                    GL20.glUseProgram(currentProgram);
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported uniform vector size: " + value.length);
            }
        }
    }

    @Override
    public void programSetUniformF(ARBProgram program, long uLoc, float[] value) {
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
                default:
                    throw new UnsupportedOperationException("Unsupported uniform vector size: " + value.length);
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
    public void programSetUniformI(ARBProgram program, long uLoc, int[] value) {
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
    public void programSetUniformMatD(ARBProgram program, long uLoc, DoubleBuffer mat) {
        final GLCapabilities cap = GL.getCapabilities();

        if (!(cap.GL_ARB_gpu_shader_fp64 && cap.GL_ARB_gpu_shader_int64)) {
            throw new UnsupportedOperationException("64bit uniforms is not supported!");
        }

        if (cap.GL_ARB_separate_shader_objects) {
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
                    ARBGPUShaderFP64.glUniformMatrix2dv((int) uLoc, false, mat);
                    GL20.glUseProgram(currentProgram);
                    break;
                case 9:
                    GL20.glUseProgram(program.programId);
                    ARBGPUShaderFP64.glUniformMatrix3dv((int) uLoc, false, mat);
                    GL20.glUseProgram(currentProgram);
                    break;
                case 16:
                    GL20.glUseProgram(program.programId);
                    ARBGPUShaderFP64.glUniformMatrix4dv((int) uLoc, false, mat);
                    GL20.glUseProgram(currentProgram);
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported matrix size: " + mat.limit());
            }
        }
    }

    @Override
    public void programSetUniformMatF(ARBProgram program, long uLoc, FloatBuffer mat) {
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
    public void programUse(ARBProgram program) {
        GL20.glUseProgram(program.programId);
    }

    @Override
    public void samplerBind(long unit, ARBSampler sampler) {
        ARBSamplerObjects.glBindSampler((int) unit, sampler.samplerId);
    }

    @Override
    public ARBSampler samplerCreate() {
        final ARBSampler sampler = new ARBSampler();
        sampler.samplerId = ARBDirectStateAccess.glCreateSamplers();
        return sampler;
    }

    @Override
    public void samplerDelete(ARBSampler sampler) {
        ARBSamplerObjects.glDeleteSamplers(sampler.samplerId);
        sampler.samplerId = -1;
    }

    @Override
    public void samplerSetParameter(ARBSampler sampler, long param, long value) {
        ARBSamplerObjects.glSamplerParameteri(sampler.samplerId, (int) param, (int) value);
    }

    @Override
    public void samplerSetParameter(ARBSampler sampler, long param, double value) {
        ARBSamplerObjects.glSamplerParameterf(sampler.samplerId, (int) param, (float) value);
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
    public ARBShader shaderCompile(long type, String source) {
        final ARBShader shader = new ARBShader();

        shader.shaderId = GL20.glCreateShader((int) type);
        GL20.glShaderSource(shader.shaderId, source);
        GL20.glCompileShader(shader.shaderId);

        return shader;
    }

    @Override
    public void shaderDelete(ARBShader shader) {
        GL20.glDeleteShader(shader.shaderId);
        shader.shaderId = -1;
    }

    @Override
    public String shaderGetInfoLog(ARBShader shader) {
        return GL20.glGetShaderInfoLog(shader.shaderId);
    }

    @Override
    public long shaderGetParameter(ARBShader shader, long pName) {
        return GL20.glGetShaderi(shader.shaderId, (int) pName);
    }

    @Override
    public ARBTexture textureAllocate(long mipmaps, long internalFormat, long width, long height, long depth) {
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

        final ARBTexture texture = new ARBTexture();

        texture.textureId = ARBDirectStateAccess.glCreateTextures(target);
        texture.target = target;
        texture.internalFormat = (int) internalFormat;

        switch (target) {
            case GL11.GL_TEXTURE_1D:
                ARBDirectStateAccess.glTextureStorage1D(texture.textureId, (int) mipmaps, (int) internalFormat, (int) width);
                break;
            case GL11.GL_TEXTURE_2D:
                ARBDirectStateAccess.glTextureStorage2D(texture.textureId, (int) mipmaps, (int) internalFormat, (int) width, (int) height);
                break;
            case GL12.GL_TEXTURE_3D:
                ARBDirectStateAccess.glTextureStorage3D(texture.textureId, (int) mipmaps, (int) internalFormat, (int) width, (int) height, (int) depth);
                break;
        }

        return texture;
    }

    @Override
    public void textureAllocatePage(ARBTexture texture, long level, long xOffset, long yOffset, long zOffset, long width, long height, long depth) {
        ARBSparseTexture.glTexPageCommitmentARB(
                texture.textureId, (int) level,
                (int) xOffset, (int) yOffset, (int) zOffset,
                (int) width, (int) height, (int) depth,
                true);
    }

    @Override
    public void textureBind(ARBTexture texture, long unit) {
        ARBDirectStateAccess.glBindTextureUnit((int) unit, texture.textureId);
    }

    @Override
    public void textureDeallocatePage(ARBTexture texture, long level, long xOffset, long yOffset, long zOffset, long width, long height, long depth) {
        ARBSparseTexture.glTexPageCommitmentARB(
                texture.textureId, (int) level,
                (int) xOffset, (int) yOffset, (int) zOffset,
                (int) width, (int) height, (int) depth,
                false);
    }

    @Override
    public void textureDelete(ARBTexture texture) {
        GL11.glDeleteTextures(texture.textureId);
        texture.textureId = -1;
    }

    @Override
    public void textureGenerateMipmap(ARBTexture texture) {
        ARBDirectStateAccess.glGenerateTextureMipmap(texture.textureId);
    }

    @Override
    public void textureGetData(ARBTexture texture, long level, long format, long type, ByteBuffer out) {
        ARBDirectStateAccess.glGetTextureImage(texture.target, (int) level, (int) format, (int) type, out);
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
    public long textureGetPageDepth(ARBTexture texture) {
        return ARBInternalformatQuery.glGetInternalformati(texture.target, texture.internalFormat, ARBSparseTexture.GL_VIRTUAL_PAGE_SIZE_Z_ARB);
    }

    @Override
    public long textureGetPageHeight(ARBTexture texture) {
        return ARBInternalformatQuery.glGetInternalformati(texture.target, texture.internalFormat, ARBSparseTexture.GL_VIRTUAL_PAGE_SIZE_Y_ARB);
    }

    @Override
    public long textureGetPageWidth(ARBTexture texture) {
        return ARBInternalformatQuery.glGetInternalformati(texture.target, texture.internalFormat, ARBSparseTexture.GL_VIRTUAL_PAGE_SIZE_X_ARB);
    }

    @Override
    public long textureGetPreferredFormat(long internalFormat) {
        return GL11.GL_RGBA;
    }

    @Override
    public void textureInvalidateData(ARBTexture texture, long level) {
        ARBInvalidateSubdata.glInvalidateTexImage(texture.textureId, (int) level);
    }

    @Override
    public void textureInvalidateRange(ARBTexture texture, long level, long xOffset, long yOffset, long zOffset, long width, long height, long depth) {
        ARBInvalidateSubdata.glInvalidateTexSubImage(texture.textureId, (int) level, (int) xOffset, (int) yOffset, (int) zOffset, (int) width, (int) height, (int) depth);
    }

    @Override
    public void textureSetData(ARBTexture texture, long level, long xOffset, long yOffset, long zOffset, long width, long height, long depth, long format, long type, ByteBuffer data) {
        switch (texture.target) {
            case GL11.GL_TEXTURE_1D:
                ARBDirectStateAccess.glTextureSubImage1D(texture.textureId, (int) level, (int) xOffset, (int) width, (int) format, (int) type, data);
                break;
            case GL11.GL_TEXTURE_2D:
                ARBDirectStateAccess.glTextureSubImage2D(texture.textureId, (int) level, (int) xOffset, (int) yOffset, (int) width, (int) height, (int) format, (int) type, data);
                break;
            case GL12.GL_TEXTURE_3D:
                ARBDirectStateAccess.glTextureSubImage3D(texture.textureId, (int) level, (int) xOffset, (int) yOffset, (int) zOffset, (int) width, (int) height, (int) depth, (int) format, (int) type, data);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported texture type: " + texture.target);
        }
    }

    @Override
    public void textureSetParameter(ARBTexture texture, long param, long value) {
        ARBDirectStateAccess.glTextureParameteri(texture.textureId, (int) param, (int) value);
    }

    @Override
    public void textureSetParameter(ARBTexture texture, long param, double value) {
        ARBDirectStateAccess.glTextureParameterf(texture.textureId, (int) param, (float) value);
    }

    @Override
    public void vertexArrayAttachBuffer(ARBVertexArray vao, long index, ARBBuffer buffer, long size, long type, long stride, long offset, long divisor) {
        if (stride == 0) {
            switch ((int) type) {
                case GL11.GL_DOUBLE:
                    stride = size * 8;
                    break;
                case GL11.GL_FLOAT:
                case GL11.GL_INT:
                case GL11.GL_UNSIGNED_INT:
                    stride = size * 4;
                    break;
                case GL11.GL_UNSIGNED_SHORT:
                case GL11.GL_SHORT:
                    stride = size * 2;
                    break;
                case GL11.GL_BYTE:
                case GL11.GL_UNSIGNED_BYTE:
                    stride = size;
            }
        }

        ARBDirectStateAccess.glEnableVertexArrayAttrib(vao.vertexArrayId, (int) index);
        ARBDirectStateAccess.glVertexArrayAttribFormat(vao.vertexArrayId, (int) index, (int) size, (int) type, false, 0);
        ARBDirectStateAccess.glVertexArrayVertexBuffer(vao.vertexArrayId, (int) index, buffer.bufferId, offset, (int) stride);
        ARBDirectStateAccess.glVertexArrayAttribBinding(vao.vertexArrayId, (int) index, (int) index);

        if (divisor > 0) {
            ARBDirectStateAccess.glVertexArrayBindingDivisor(vao.vertexArrayId, (int) index, (int) divisor);
        }
    }

    @Override
    public void vertexArrayAttachIndexBuffer(ARBVertexArray vao, ARBBuffer buffer) {
        ARBDirectStateAccess.glVertexArrayElementBuffer(vao.vertexArrayId, buffer.bufferId);
    }

    @Override
    public ARBVertexArray vertexArrayCreate() {
        final ARBVertexArray vao = new ARBVertexArray();
        vao.vertexArrayId = ARBDirectStateAccess.glCreateVertexArrays();
        return vao;
    }

    @Override
    public void vertexArrayDelete(ARBVertexArray vao) {
        ARBVertexArrayObject.glDeleteVertexArrays(vao.vertexArrayId);
        vao.vertexArrayId = -1;
    }

    @Override
    public void vertexArrayDrawArrays(ARBVertexArray vao, long drawMode, long start, long count) {
        final int currentVao = GL11.glGetInteger(ARBVertexArrayObject.GL_VERTEX_ARRAY_BINDING);
        ARBVertexArrayObject.glBindVertexArray(vao.vertexArrayId);
        GL11.glDrawArrays((int) drawMode, (int) start, (int) count);
        ARBVertexArrayObject.glBindVertexArray(currentVao);
    }

    @Override
    public void vertexArrayDrawArraysIndirect(ARBVertexArray vao, ARBBuffer cmdBuffer, long drawMode, long offset) {
        final int currentVao = GL11.glGetInteger(ARBVertexArrayObject.GL_VERTEX_ARRAY_BINDING);
        final int currentIndirect = GL11.glGetInteger(ARBDrawIndirect.GL_DRAW_INDIRECT_BUFFER);

        ARBVertexArrayObject.glBindVertexArray(vao.vertexArrayId);
        GL15.glBindBuffer(ARBDrawIndirect.GL_DRAW_INDIRECT_BUFFER, cmdBuffer.bufferId);
        ARBDrawIndirect.glDrawArraysIndirect((int) drawMode, offset);
        GL15.glBindBuffer(ARBDrawIndirect.GL_DRAW_INDIRECT_BUFFER, currentIndirect);
        ARBVertexArrayObject.glBindVertexArray(currentVao);
    }

    @Override
    public void vertexArrayDrawArraysInstanced(ARBVertexArray vao, long drawMode, long first, long count, long instanceCount) {
        final int currentVao = GL11.glGetInteger(ARBVertexArrayObject.GL_VERTEX_ARRAY_BINDING);

        ARBVertexArrayObject.glBindVertexArray(vao.vertexArrayId);
        GL31.glDrawArraysInstanced((int) drawMode, (int) first, (int) count, (int) instanceCount);
        ARBVertexArrayObject.glBindVertexArray(currentVao);
    }

    @Override
    public void vertexArrayDrawElements(ARBVertexArray vao, long drawMode, long count, long type, long offset) {
        final int currentVao = GL11.glGetInteger(ARBVertexArrayObject.GL_VERTEX_ARRAY_BINDING);

        ARBVertexArrayObject.glBindVertexArray(vao.vertexArrayId);
        GL11.glDrawElements((int) drawMode, (int) count, (int) type, offset);
        ARBVertexArrayObject.glBindVertexArray(currentVao);
    }

    @Override
    public void vertexArrayDrawElementsIndirect(ARBVertexArray vao, ARBBuffer cmdBuffer, long drawMode, long indexType, long offset) {
        final int currentVao = GL11.glGetInteger(ARBVertexArrayObject.GL_VERTEX_ARRAY_BINDING);
        final int currentIndirect = GL11.glGetInteger(ARBDrawIndirect.GL_DRAW_INDIRECT_BUFFER_BINDING);

        ARBVertexArrayObject.glBindVertexArray(vao.vertexArrayId);
        GL15.glBindBuffer(ARBDrawIndirect.GL_DRAW_INDIRECT_BUFFER, cmdBuffer.bufferId);
        ARBDrawIndirect.glDrawElementsIndirect((int) drawMode, (int) indexType, offset);
        GL15.glBindBuffer(ARBDrawIndirect.GL_DRAW_INDIRECT_BUFFER, currentIndirect);
        ARBVertexArrayObject.glBindVertexArray(currentVao);
    }

    @Override
    public void vertexArrayDrawElementsInstanced(ARBVertexArray vao, long drawMode, long count, long type, long offset, long instanceCount) {
        final int currentVao = GL11.glGetInteger(ARBVertexArrayObject.GL_VERTEX_ARRAY_BINDING);

        ARBVertexArrayObject.glBindVertexArray(vao.vertexArrayId);
        GL31.glDrawElementsInstanced((int) drawMode, (int) count, (int) type, offset, (int) instanceCount);
        ARBVertexArrayObject.glBindVertexArray(currentVao);
    }

    @Override
    public void vertexArrayDrawTransformFeedback(ARBVertexArray vao, long drawMode, long start, long count) {
        final int currentVao = GL11.glGetInteger(ARBVertexArrayObject.GL_VERTEX_ARRAY_BINDING);

        ARBVertexArrayObject.glBindVertexArray(vao.vertexArrayId);
        GL11.glEnable(GL30.GL_RASTERIZER_DISCARD);
        GL30.glBeginTransformFeedback((int) drawMode);
        GL11.glDrawArrays((int) drawMode, (int) start, (int) count);
        GL30.glEndTransformFeedback();
        GL11.glDisable(GL30.GL_RASTERIZER_DISCARD);
        ARBVertexArrayObject.glBindVertexArray(currentVao);
    }

    @Override
    public void vertexArrayMultiDrawArrays(ARBVertexArray vao, long drawMode, IntBuffer first, IntBuffer count) {
        final int currentVao = GL11.glGetInteger(ARBVertexArrayObject.GL_VERTEX_ARRAY_BINDING);

        ARBVertexArrayObject.glBindVertexArray(vao.vertexArrayId);
        GL14.glMultiDrawArrays((int) drawMode, first, count);
        ARBVertexArrayObject.glBindVertexArray(currentVao);
    }

    @Override
    public void viewportApply(long x, long y, long width, long height) {
        GL11.glViewport((int) x, (int) y, (int) width, (int) height);
    }

}
