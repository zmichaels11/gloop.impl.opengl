/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop.glimpl.gl3x;

import com.longlinkislong.gloop.glimpl.GLState;
import com.longlinkislong.gloop.glspi.Driver;
import com.longlinkislong.gloop.glspi.Shader;
import com.longlinkislong.gloop.glspi.Tweaks;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.lwjgl.opengl.ARBBufferStorage;
import org.lwjgl.opengl.ARBCopyBuffer;
import org.lwjgl.opengl.ARBDrawIndirect;
import org.lwjgl.opengl.ARBGPUShaderFP64;
import org.lwjgl.opengl.ARBInternalformatQuery;
import org.lwjgl.opengl.ARBInvalidateSubdata;
import org.lwjgl.opengl.ARBSamplerObjects;
import org.lwjgl.opengl.ARBSeparateShaderObjects;
import org.lwjgl.opengl.ARBSparseTexture;
import org.lwjgl.opengl.ARBUniformBufferObject;
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
import org.lwjgl.opengl.GLCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author zmichaels
 */
final class GL3XDriver implements Driver<
        GL3XBuffer, GL3XFramebuffer, GL3XRenderbuffer, GL3XTexture, GL3XShader, GL3XProgram, GL3XSampler, GL3XVertexArray, GL3XDrawQuery> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GL3XDriver.class);
    private GLState state = new GLState(new Tweaks());

    @Override
    public void bufferBindStorage(GL3XBuffer bt, int index) {
        throw new UnsupportedOperationException("GL_arb_shader_storage_buffer_object or OpenGL 4.0 is not supported!");
    }

    @Override
    public void bufferBindStorage(GL3XBuffer bt, int index, long offset, long size) {
        throw new UnsupportedOperationException("GL_arb_shader_storage_buffer_object or OpenGL 4.0 is not supported!");
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
        if(pt.uniformBindings.containsKey(ublockName)) {
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
        this.state = new GLState(tweak);
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
        state.bufferPush(GL15.GL_ARRAY_BUFFER, buffer.bufferId);

        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, size, usage);

        state.bufferPop(GL15.GL_ARRAY_BUFFER);
    }

    @Override
    public void bufferAllocateImmutable(GL3XBuffer buffer, long size, int bitflags) {
        if (GL.getCapabilities().GL_ARB_buffer_storage) {
            state.bufferPush(GL15.GL_ARRAY_BUFFER, buffer.bufferId);
            ARBBufferStorage.glBufferStorage(GL15.GL_ARRAY_BUFFER, size, bitflags);
            state.bufferPop(GL15.GL_ARRAY_BUFFER);
        } else {
            LOGGER.trace("immutable buffer allocate is not supported; falling back on buffer allocate!");
            this.bufferAllocate(buffer, size, GL15.GL_DYNAMIC_DRAW);
        }
    }

    @Override
    public void bufferCopyData(GL3XBuffer srcBuffer, long srcOffset, GL3XBuffer dstBuffer, long dstOffset, long size) {
        if (GL.getCapabilities().GL_ARB_copy_buffer) {
            GL15.glBindBuffer(ARBCopyBuffer.GL_COPY_READ_BUFFER, srcBuffer.bufferId);
            GL15.glBindBuffer(ARBCopyBuffer.GL_COPY_WRITE_BUFFER, dstBuffer.bufferId);

            ARBCopyBuffer.glCopyBufferSubData(ARBCopyBuffer.GL_COPY_READ_BUFFER, ARBCopyBuffer.GL_COPY_WRITE_BUFFER, srcOffset, dstOffset, size);
        } else {
            final ByteBuffer src = this.bufferMapData(srcBuffer, srcOffset, size, GL30.GL_MAP_READ_BIT);
            final ByteBuffer dst = this.bufferMapData(dstBuffer, dstOffset, size, GL30.GL_MAP_WRITE_BIT);

            for (int i = 0; i < size; i++) {
                dst.put(i, src.get(i));
            }

            this.bufferUnmapData(dstBuffer);
            this.bufferUnmapData(srcBuffer);
        }
    }

    @Override
    public GL3XBuffer bufferCreate() {
        final GL3XBuffer buffer = new GL3XBuffer();
        buffer.bufferId = GL15.glGenBuffers();
        return buffer;
    }

    @Override
    public void bufferDelete(GL3XBuffer buffer) {
        GL15.glDeleteBuffers(buffer.bufferId);
        buffer.bufferId = -1;
    }

    @Override
    public void bufferGetData(GL3XBuffer buffer, long offset, ByteBuffer out) {
        state.bufferPush(GL15.GL_ARRAY_BUFFER, buffer.bufferId);

        GL15.glGetBufferSubData(GL15.GL_ARRAY_BUFFER, offset, out);

        state.bufferPop(GL15.GL_ARRAY_BUFFER);
    }

    @Override
    public int bufferGetParameterI(GL3XBuffer buffer, int paramId) {
        state.bufferPush(GL15.GL_ARRAY_BUFFER, buffer.bufferId);

        final int res = GL15.glGetBufferParameteri(GL15.GL_ARRAY_BUFFER, buffer.bufferId);

        state.bufferPop(GL15.GL_ARRAY_BUFFER);
        return res;
    }

    @Override
    public void bufferInvalidateData(GL3XBuffer buffer) {
        if (GL.getCapabilities().GL_ARB_invalidate_subdata) {
            ARBInvalidateSubdata.glInvalidateBufferData(buffer.bufferId);
        } else {
            LOGGER.trace("ARB_invalidate_subdata is not supported... Ignoring call to glInvalidateBufferData.");
        }
    }

    @Override
    public void bufferInvalidateRange(GL3XBuffer buffer, long offset, long length) {
        if (GL.getCapabilities().GL_ARB_invalidate_subdata) {
            ARBInvalidateSubdata.glInvalidateBufferSubData(buffer.bufferId, offset, length);
        } else {
            LOGGER.trace("ARB_invalidate_subdata is not supported... Ignoring call to glInvalidateBufferSubdData.");
        }
    }

    @Override
    public ByteBuffer bufferMapData(GL3XBuffer buffer, long offset, long length, int accessFlags) {
        state.bufferPush(GL15.GL_ARRAY_BUFFER, buffer.bufferId);

        buffer.mapBuffer = GL30.glMapBufferRange(GL15.GL_ARRAY_BUFFER, offset, length, accessFlags, buffer.mapBuffer);

        state.bufferPop(GL15.GL_ARRAY_BUFFER);
        return buffer.mapBuffer;
    }

    @Override
    public void bufferSetData(GL3XBuffer buffer, ByteBuffer data, int usage) {
        state.bufferPush(GL15.GL_ARRAY_BUFFER, buffer.bufferId);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, data, usage);
        state.bufferPop(GL15.GL_ARRAY_BUFFER);
    }

    @Override
    public void bufferUnmapData(GL3XBuffer buffer) {
        state.bufferPush(GL15.GL_ARRAY_BUFFER, buffer.bufferId);
        GL15.glUnmapBuffer(GL15.GL_ARRAY_BUFFER);
        state.bufferPop(GL15.GL_ARRAY_BUFFER);
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
    public void drawQueryBeginConditionalRender(GL3XDrawQuery query, int mode) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public GL3XDrawQuery drawQueryCreate() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void drawQueryDelete(GL3XDrawQuery query) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void drawQueryDisable(int condition) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void drawQueryEnable(int condition, GL3XDrawQuery query) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void drawQueryEndConditionRender() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void framebufferAddAttachment(GL3XFramebuffer framebuffer, int attachmentId, GL3XTexture texId, int mipmapLevel) {
        state.framebufferPush(GL30.GL_FRAMEBUFFER, framebuffer.framebufferId);

        switch (texId.target) {
            case GL11.GL_TEXTURE_1D:
                GL30.glFramebufferTexture1D(GL30.GL_FRAMEBUFFER, attachmentId, GL11.GL_TEXTURE_1D, texId.textureId, mipmapLevel);
                break;
            case GL11.GL_TEXTURE_2D:
                GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, attachmentId, GL11.GL_TEXTURE_2D, texId.textureId, mipmapLevel);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported texture target!");
        }

        state.framebufferPop(GL30.GL_FRAMEBUFFER);
    }

    @Override
    public void framebufferAddRenderbuffer(GL3XFramebuffer framebuffer, int attachmentId, GL3XRenderbuffer renderbuffer) {
        state.framebufferPush(GL30.GL_FRAMEBUFFER, framebuffer.framebufferId);

        GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, attachmentId, GL30.GL_RENDERBUFFER, renderbuffer.renderbufferId);

        state.framebufferPop(GL30.GL_FRAMEBUFFER);
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
        state.framebufferPush(GL30.GL_READ_FRAMEBUFFER, srcFb.framebufferId);
        state.framebufferPush(GL30.GL_DRAW_FRAMEBUFFER, dstFb.framebufferId);

        GL30.glBlitFramebuffer(srcX0, srcY0, srcX1, srcY1, dstX0, dstY0, dstX1, dstY1, bitfield, filter);

        state.framebufferPop(GL30.GL_DRAW_FRAMEBUFFER);
        state.framebufferPop(GL30.GL_READ_FRAMEBUFFER);
    }

    @Override
    public GL3XFramebuffer framebufferCreate() {
        final GL3XFramebuffer fb = new GL3XFramebuffer();
        fb.framebufferId = GL30.glGenFramebuffers();
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
        state.framebufferPush(GL30.GL_FRAMEBUFFER, framebuffer.framebufferId);
        state.bufferPush(GL21.GL_PIXEL_PACK_BUFFER, dstBuffer.bufferId);

        GL11.glReadPixels(
                x, y, width, height,
                format, type,
                0L);

        state.bufferPop(GL21.GL_PIXEL_PACK_BUFFER);
        state.framebufferPop(GL30.GL_FRAMEBUFFER);
    }

    @Override
    public void framebufferGetPixels(GL3XFramebuffer framebuffer, int x, int y, int width, int height, int format, int type, ByteBuffer dstBuffer) {
        state.framebufferPush(GL30.GL_FRAMEBUFFER, framebuffer.framebufferId);

        GL11.glReadPixels(
                x, y, width, height,
                format, type,
                dstBuffer);

        state.framebufferPop(GL30.GL_FRAMEBUFFER);
    }

    @Override
    public boolean framebufferIsComplete(GL3XFramebuffer framebuffer) {
        state.framebufferPush(GL30.GL_FRAMEBUFFER, framebuffer.framebufferId);

        final int complete = GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER);

        state.framebufferPop(GL30.GL_FRAMEBUFFER);
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
        state.programPush(program.programId);

        final int res = GL20.glGetUniformLocation(program.programId, name);

        state.programPop();
        return res;
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
    public void programSetFeedbackBuffer(GL3XProgram program, int varyingLoc, GL3XBuffer buffer) {
        GL30.glBindBufferBase(GL30.GL_TRANSFORM_FEEDBACK_BUFFER, varyingLoc, buffer.bufferId);
    }

    @Override
    public void programSetFeedbackVaryings(GL3XProgram program, String[] varyings) {
        GL30.glTransformFeedbackVaryings(program.programId, varyings, GL30.GL_SEPARATE_ATTRIBS);
    }

    @Override
    public void programSetStorage(GL3XProgram program, String storageName, GL3XBuffer buffer, int bindingPoint) {
        throw new UnsupportedOperationException("Shader storage is not supported!");
    }

    @Override
    public void programSetUniformBlock(GL3XProgram program, String uniformName, GL3XBuffer buffer, int bindingPoint) {
        if (GL.getCapabilities().GL_ARB_uniform_buffer_object) {
            final int uBlock = ARBUniformBufferObject.glGetUniformBlockIndex(program.programId, uniformName);

            GL30.glBindBufferBase(ARBUniformBufferObject.GL_UNIFORM_BUFFER, bindingPoint, buffer.bufferId);
            ARBUniformBufferObject.glUniformBlockBinding(program.programId, uBlock, bindingPoint);
        } else {
            throw new UnsupportedOperationException("ARB_uniform_buffer_object is not supported!");
        }
    }

    @Override
    public void programSetUniformD(GL3XProgram program, int uLoc, double[] value) {
        final GLCapabilities cap = GL.getCapabilities();

        if (!(cap.GL_ARB_gpu_shader_fp64 && cap.GL_ARB_gpu_shader_int64)) {
            throw new UnsupportedOperationException("64bit uniforms are not supported!");
        }

        if (cap.GL_ARB_separate_shader_objects) {
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
            state.programPush(program.programId);

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

            state.programPop();
        }
    }

    @Override
    public void programSetUniformF(GL3XProgram program, int uLoc, float[] value) {
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
            state.programPush(program.programId);

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

            state.programPop();
        }
    }

    @Override
    public void programSetUniformI(GL3XProgram program, int uLoc, int[] value) {
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
            state.programPush(program.programId);

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

            state.programPop();
        }
    }

    @Override
    public void programSetUniformMatD(GL3XProgram program, int uLoc, DoubleBuffer mat) {
        final GLCapabilities cap = GL.getCapabilities();

        if (!(cap.GL_ARB_gpu_shader_fp64 && cap.GL_ARB_gpu_shader_int64)) {
            throw new UnsupportedOperationException("64bit uniforms are not supported!");
        }

        if (cap.GL_ARB_separate_shader_objects) {
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
            state.programPush(program.programId);

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

            state.programPop();
        }
    }

    @Override
    public void programSetUniformMatF(GL3XProgram program, int uLoc, FloatBuffer mat) {
        if (GL.getCapabilities().GL_ARB_separate_shader_objects) {
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
            state.programPush(program.programId);

            switch (mat.remaining()) {
                case 4:
                    GL20.glUniformMatrix2fv(uLoc, false, mat);
                    break;
                case 9:
                    GL20.glUniformMatrix3fv(uLoc, false, mat);
                    break;
                case 16:
                    GL20.glUniformMatrix4fv(uLoc, false, mat);
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported matrix size: " + mat.limit());
            }

            state.programPop();
        }
    }

    @Override
    public void programUse(GL3XProgram program) {
        GL20.glUseProgram(program.programId);
    }

    @Override
    public GL3XRenderbuffer renderbufferCreate(int internalFormat, int width, int height) {
        final GL3XRenderbuffer renderbuffer = new GL3XRenderbuffer();

        renderbuffer.renderbufferId = GL30.glGenRenderbuffers();

        GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, renderbuffer.renderbufferId);
        GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, internalFormat, width, height);

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
    public GL3XTexture textureAllocate(int mipmaps, int internalFormat, int width, int height, int depth) {
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

        final GL3XTexture texture = new GL3XTexture();

        texture.textureId = GL11.glGenTextures();
        texture.target = target;
        texture.internalFormat = internalFormat;

        state.texturePush(texture.target, texture.textureId);

        switch (target) {
            case GL11.GL_TEXTURE_1D:
                GL11.glTexParameteri(GL11.GL_TEXTURE_1D, GL12.GL_TEXTURE_BASE_LEVEL, 0);
                GL11.glTexParameteri(GL11.GL_TEXTURE_1D, GL12.GL_TEXTURE_MAX_LEVEL, mipmaps);

                for (int i = 0; i < mipmaps; i++) {
                    GL11.glTexImage1D(GL11.GL_TEXTURE_1D, i, internalFormat, width, 0, guessFormat(internalFormat), GL11.GL_UNSIGNED_BYTE, 0);
                    width = Math.max(1, (width / 2));
                }
                break;
            case GL11.GL_TEXTURE_2D:
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_BASE_LEVEL, 0);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_MAX_LEVEL, mipmaps);

                for (int i = 0; i < mipmaps; i++) {
                    GL11.glTexImage2D(GL11.GL_TEXTURE_2D, i, internalFormat, width, height, 0, guessFormat(internalFormat), GL11.GL_UNSIGNED_BYTE, 0);
                    width = Math.max(1, (width / 2));
                    height = Math.max(1, (height / 2));
                }
                break;
            case GL12.GL_TEXTURE_3D:
                GL11.glTexParameteri(GL12.GL_TEXTURE_3D, GL12.GL_TEXTURE_BASE_LEVEL, 0);
                GL11.glTexParameteri(GL12.GL_TEXTURE_3D, GL12.GL_TEXTURE_MAX_LEVEL, mipmaps);

                for (int i = 0; i < mipmaps; i++) {
                    GL12.glTexImage3D(GL12.GL_TEXTURE_3D, i, internalFormat, width, height, depth, 0, guessFormat(internalFormat), GL11.GL_UNSIGNED_BYTE, 0);
                    width = Math.max(1, (width / 2));
                    height = Math.max(1, (height / 2));
                    depth = Math.max(1, (depth / 2));
                }
                break;
            default:
                throw new UnsupportedOperationException("Unsupported texture target: " + target);
        }

        state.texturePop(texture.target);

        return texture;
    }

    @Override
    public void textureAllocatePage(GL3XTexture texture, int level, int xOffset, int yOffset, int zOffset, int width, int height, int depth) {
        if (GL.getCapabilities().GL_ARB_sparse_texture) {
            ARBSparseTexture.glTexPageCommitmentARB(
                    texture.textureId, level,
                    xOffset, yOffset, zOffset,
                    width, height, depth,
                    true);
        } else {
            throw new UnsupportedOperationException("ARB_sparse_texture is not supported!");
        }
    }

    @Override
    public void textureBind(GL3XTexture texture, int unit) {
        GL13.glActiveTexture(GL13.GL_TEXTURE0 + unit);
        GL11.glBindTexture(texture.target, texture.textureId);
    }

    @Override
    public void textureDeallocatePage(GL3XTexture texture, int level, int xOffset, int yOffset, int zOffset, int width, int height, int depth) {
        if (GL.getCapabilities().GL_ARB_sparse_texture) {
            ARBSparseTexture.glTexPageCommitmentARB(
                    texture.textureId, level,
                    xOffset, yOffset, zOffset,
                    width, height, depth,
                    false);
        } else {
            throw new UnsupportedOperationException("ARB_sparse_texture is not supported!");
        }
    }

    @Override
    public void textureDelete(GL3XTexture texture) {
        GL11.glDeleteTextures(texture.textureId);
        texture.textureId = -1;
    }

    @Override
    public void textureGenerateMipmap(GL3XTexture texture) {
        state.texturePush(texture.target, texture.textureId);
        GL30.glGenerateMipmap(texture.target);
        state.texturePop(texture.target);
    }

    @Override
    public void textureGetData(GL3XTexture texture, int level, int format, int type, ByteBuffer out) {
        state.texturePush(texture.target, texture.textureId);
        GL11.glGetTexImage(texture.target, level, format, type, out);
        state.texturePop(texture.target);
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
    public int textureGetPageDepth(GL3XTexture texture) {
        if (GL.getCapabilities().GL_ARB_internalformat_query) {
            return ARBInternalformatQuery.glGetInternalformati(texture.target, texture.internalFormat, ARBSparseTexture.GL_VIRTUAL_PAGE_SIZE_Z_ARB);
        } else {
            throw new UnsupportedOperationException("ARB_internalformat_query is not supported!");
        }
    }

    @Override
    public int textureGetPageHeight(GL3XTexture texture) {
        if (GL.getCapabilities().GL_ARB_internalformat_query) {
            return ARBInternalformatQuery.glGetInternalformati(texture.target, texture.internalFormat, ARBSparseTexture.GL_VIRTUAL_PAGE_SIZE_Y_ARB);
        } else {
            throw new UnsupportedOperationException("ARB_internalformat_query is not supported!");
        }
    }

    @Override
    public int textureGetPageWidth(GL3XTexture texture) {
        if (GL.getCapabilities().GL_ARB_internalformat_query) {
            return ARBInternalformatQuery.glGetInternalformati(texture.target, texture.internalFormat, ARBSparseTexture.GL_VIRTUAL_PAGE_SIZE_X_ARB);
        } else {
            throw new UnsupportedOperationException("ARB_internalformat_query is not supported!");
        }
    }

    @Override
    public int textureGetPreferredFormat(int internalFormat) {
        return GL11.GL_RGBA;
    }

    @Override
    public void textureInvalidateData(GL3XTexture texture, int level) {
        if (GL.getCapabilities().GL_ARB_invalidate_subdata) {
            ARBInvalidateSubdata.glInvalidateTexImage(texture.target, level);
        } else {
            LOGGER.trace("ARB_invalidate_subdata is not supported... Ignoring call to glInvalidateTexImage.");
        }
    }

    @Override
    public void textureInvalidateRange(GL3XTexture texture, int level, int xOffset, int yOffset, int zOffset, int width, int height, int depth) {
        if (GL.getCapabilities().GL_ARB_invalidate_subdata) {
            ARBInvalidateSubdata.glInvalidateTexSubImage(texture.textureId, level, xOffset, yOffset, zOffset, width, height, depth);
        } else {
            LOGGER.trace("ARB_invalidate_subdata is not supported... Ignoring call to glInvalidateTexSubImage.");
        }
    }

    @Override
    public void textureSetData(GL3XTexture texture, int level, int xOffset, int yOffset, int zOffset, int width, int height, int depth, int format, int type, ByteBuffer data) {
        state.texturePush(texture.target, texture.textureId);

        switch (texture.target) {
            case GL11.GL_TEXTURE_1D:
                GL11.glTexSubImage1D(GL11.GL_TEXTURE_1D, level, xOffset, width, format, type, data);
                break;
            case GL11.GL_TEXTURE_2D:
                GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, level, xOffset, yOffset, width, height, format, type, data);
                break;
            case GL12.GL_TEXTURE_3D:
                GL12.glTexSubImage3D(GL12.GL_TEXTURE_3D, level, xOffset, yOffset, zOffset, width, height, depth, format, type, data);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported texture target: " + texture.target);

        }

        state.texturePop(texture.target);
    }

    @Override
    public void textureSetParameter(GL3XTexture texture, int param, int value) {
        state.texturePush(texture.target, texture.textureId);
        GL11.glTexParameteri(texture.target, param, value);
        state.texturePop(texture.target);
    }

    @Override
    public void textureSetParameter(GL3XTexture texture, int param, float value) {
        state.texturePush(texture.target, texture.textureId);
        GL11.glTexParameterf(texture.target, param, value);
        state.texturePop(texture.target);
    }

    @Override
    public void vertexArrayAttachBuffer(GL3XVertexArray vao, int index, GL3XBuffer buffer, int size, int type, int stride, long offset, int divisor) {
        state.vertexArrayPush(vao.vertexArrayId);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, buffer.bufferId);
        GL20.glEnableVertexAttribArray(index);

        if (type == GL11.GL_DOUBLE) {
            if (GL.getCapabilities().GL_ARB_vertex_attrib_64bit) {
                ARBVertexAttrib64Bit.glVertexAttribLPointer(index, size, type, stride, offset);
            } else {
                throw new UnsupportedOperationException("ARB_vertex_attrib_64bit is not supported!");
            }
        } else {
            GL20.glVertexAttribPointer(index, size, type, false, stride, offset);
        }

        if (divisor > 0) {
            if (GL.getCapabilities().OpenGL33) {
                GL33.glVertexAttribDivisor(index, divisor);
            } else {
                throw new UnsupportedOperationException("OpenGL 3.3 is not supported!");
            }
        }

        state.vertexArrayPop();
    }

    @Override
    public void vertexArrayAttachIndexBuffer(GL3XVertexArray vao, GL3XBuffer buffer) {
        state.vertexArrayPush(vao.vertexArrayId);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer.bufferId);
        state.vertexArrayPop();
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
        state.vertexArrayPush(vao.vertexArrayId);
        GL11.glDrawArrays(drawMode, start, count);
        state.vertexArrayPop();
    }

    @Override
    public void vertexArrayDrawArraysIndirect(GL3XVertexArray vao, GL3XBuffer cmdBuffer, int drawMode, long offset) {
        if (GL.getCapabilities().GL_ARB_draw_indirect) {
            state.vertexArrayPush(vao.vertexArrayId);
            state.bufferPush(ARBDrawIndirect.GL_DRAW_INDIRECT_BUFFER, cmdBuffer.bufferId);

            ARBDrawIndirect.glDrawArraysIndirect(drawMode, offset);

            state.bufferPop(ARBDrawIndirect.GL_DRAW_INDIRECT_BUFFER);
            state.vertexArrayPop();
        } else {
            throw new UnsupportedOperationException("ARB_draw_indirect is not supported!");
        }
    }

    @Override
    public void vertexArrayDrawArraysInstanced(GL3XVertexArray vao, int drawMode, int first, int count, int instanceCount) {
        if (GL.getCapabilities().OpenGL31) {
            state.vertexArrayPush(vao.vertexArrayId);

            GL31.glDrawArraysInstanced(drawMode, first, count, instanceCount);

            state.vertexArrayPop();
        } else {
            throw new UnsupportedOperationException("OpenGL 3.1 is not supported!");
        }
    }

    @Override
    public void vertexArrayDrawElements(GL3XVertexArray vao, int drawMode, int count, int type, long offset) {
        state.vertexArrayPush(vao.vertexArrayId);

        GL11.glDrawElements(drawMode, count, type, offset);

        state.vertexArrayPop();
    }

    @Override
    public void vertexArrayDrawElementsIndirect(GL3XVertexArray vao, GL3XBuffer cmdBuffer, int drawMode, int indexType, long offset) {
        if (GL.getCapabilities().GL_ARB_draw_indirect) {
            state.vertexArrayPush(vao.vertexArrayId);
            state.bufferPush(ARBDrawIndirect.GL_DRAW_INDIRECT_BUFFER, cmdBuffer.bufferId);

            ARBDrawIndirect.glDrawElementsIndirect(drawMode, indexType, offset);

            state.bufferPop(ARBDrawIndirect.GL_DRAW_INDIRECT_BUFFER);
            state.vertexArrayPop();
        } else {
            throw new UnsupportedOperationException("ARB_draw_indirect is not supported!");
        }
    }

    @Override
    public void vertexArrayDrawElementsInstanced(GL3XVertexArray vao, int drawMode, int count, int type, long offset, int instanceCount) {
        if (GL.getCapabilities().OpenGL31) {
            state.vertexArrayPush(vao.vertexArrayId);

            GL31.glDrawElementsInstanced(drawMode, count, type, offset, instanceCount);

            state.vertexArrayPop();
        } else {
            throw new UnsupportedOperationException("OpenGL 3.1 is not supported!");
        }
    }

    @Override
    public void vertexArrayDrawTransformFeedback(GL3XVertexArray vao, int drawMode, int start, int count) {
        state.vertexArrayPush(vao.vertexArrayId);

        GL11.glEnable(GL30.GL_RASTERIZER_DISCARD);
        GL30.glBeginTransformFeedback(drawMode);
        GL11.glDrawArrays(drawMode, start, count);
        GL30.glEndTransformFeedback();
        GL11.glDisable(GL30.GL_RASTERIZER_DISCARD);

        state.vertexArrayPop();
    }

    @Override
    public void vertexArrayMultiDrawArrays(GL3XVertexArray vao, int drawMode, IntBuffer first, IntBuffer count) {
        state.vertexArrayPush(vao.vertexArrayId);
        GL14.glMultiDrawArrays(drawMode, first, count);
        state.vertexArrayPop();
    }

    @Override
    public void viewportApply(int x, int y, int width, int height) {
        GL11.glViewport(x, y, width, height);
    }
}
