/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop.glimpl.gl4x;

import com.longlinkislong.gloop.glimpl.GLSPIBaseObject;
import com.longlinkislong.gloop.glimpl.GLState;
import com.longlinkislong.gloop.glspi.Driver;
import com.longlinkislong.gloop.glspi.Shader;
import com.longlinkislong.gloop.glspi.Tweaks;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
import org.lwjgl.opengl.GLCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author zmichaels
 */
final class GL4XDriver implements Driver<
        GL4XBuffer, GL4XFramebuffer, GL4XRenderbuffer, GL4XTexture, GL4XShader, GL4XProgram, GL4XSampler, GL4XVertexArray, GL4XDrawQuery> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GL4XDriver.class);
    private static final boolean RECORD_CALLS = Boolean.getBoolean("com.longlinkislong.gloop.record_calls");

    private GLState state = new GLState(new Tweaks());
    private final List<String> callHistory = RECORD_CALLS ? new ArrayList<>(1024) : Collections.emptyList();

    private void recordCall(String call, Object... params) {
        GLSPIBaseObject.recordCall(callHistory, call, params);
    }

    @Override
    public List<String> getCallHistory() {
        return Collections.unmodifiableList(new ArrayList<>(this.callHistory));
    }

    @Override
    public void clearCallHistory() {
        this.callHistory.clear();
    }

    @Override
    public int shaderGetVersion() {
        final GLCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL45) {
            return 450;
        } else if (cap.OpenGL44) {
            return 440;
        } else if (cap.OpenGL43) {
            return 430;
        } else if (cap.OpenGL42) {
            return 420;
        } else if (cap.OpenGL41) {
            return 410;
        } else {
            return 400;
        }
    }

    @Override
    public void applyTweaks(final Tweaks tweak) {
        this.state = new GLState(tweak);
    }

    @Override
    public void blendingDisable() {
        if (RECORD_CALLS) {
            recordCall("glDisable", "GL_BLEND");
        }

        GL11.glDisable(GL11.GL_BLEND);
    }

    @Override
    public void blendingEnable(int rgbEq, int aEq, int rgbFuncSrc, int rgbFuncDst, int aFuncSrc, int aFuncDst) {
        if (RECORD_CALLS) {
            recordCall("glEnable", "GL_BLEND");
            recordCall("glBlendFuncSeparate", rgbFuncSrc, rgbFuncDst, aFuncSrc, aFuncDst);
            recordCall("glBlendEquationSeparate", rgbEq, aEq);
        }

        GL11.glEnable(GL11.GL_BLEND);
        GL14.glBlendFuncSeparate(rgbFuncSrc, rgbFuncDst, aFuncSrc, aFuncDst);
        GL20.glBlendEquationSeparate(rgbEq, aEq);
    }

    @Override
    public void bufferAllocate(GL4XBuffer buffer, long size, int usage) {
        state.bufferPush(GL15.GL_ARRAY_BUFFER, buffer.bufferId);

        if (RECORD_CALLS) {
            recordCall("glBufferData", "GL_ARRAY_BUFFER", size, usage);
        }

        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, size, usage);

        state.bufferPop(GL15.GL_ARRAY_BUFFER);
    }

    @Override
    public void bufferAllocateImmutable(GL4XBuffer buffer, long size, int bitflags) {
        if (GL.getCapabilities().GL_ARB_buffer_storage) {
            state.bufferPush(GL15.GL_ARRAY_BUFFER, buffer.bufferId);

            if (RECORD_CALLS) {
                recordCall("glBufferStorage", "GL_ARRAY_BUFFER", size, bitflags);
            }

            ARBBufferStorage.glBufferStorage(GL15.GL_ARRAY_BUFFER, size, bitflags);

            state.bufferPop(GL15.GL_ARRAY_BUFFER);
        } else {
            LOGGER.trace("Immutable buffers are not supported! Falling back on bufferAllocate...");
            this.bufferAllocate(buffer, size, GL15.GL_DYNAMIC_DRAW); //
        }
    }

    @Override
    public void bufferCopyData(GL4XBuffer srcBuffer, long srcOffset, GL4XBuffer dstBuffer, long dstOffset, long size) {
        if (RECORD_CALLS) {
            recordCall("glBindBuffer", "GL_COPY_READ_BUFFER", srcBuffer.bufferId);
            recordCall("glBindBuffer", "GL_COPY_WRITE_BUFFER", dstBuffer.bufferId);
            recordCall("glCopyBufferSubData", "GL_COPY_READ_BUFFER", "GL_COPY_WRITE_BUFFER", srcOffset, dstOffset, size);
        }

        GL15.glBindBuffer(GL31.GL_COPY_READ_BUFFER, srcBuffer.bufferId);
        GL15.glBindBuffer(GL31.GL_COPY_WRITE_BUFFER, dstBuffer.bufferId);
        GL31.glCopyBufferSubData(GL31.GL_COPY_READ_BUFFER, GL31.GL_COPY_WRITE_BUFFER, srcOffset, dstOffset, size);
    }

    @Override
    public GL4XBuffer bufferCreate() {
        if (RECORD_CALLS) {
            recordCall("#bufferId = glGenBuffers");
        }

        final GL4XBuffer buffer = new GL4XBuffer();
        buffer.bufferId = GL15.glGenBuffers();
        return buffer;
    }

    @Override
    public void bufferDelete(GL4XBuffer buffer) {
        if (RECORD_CALLS) {
            recordCall("glDeleteBuffers", buffer.bufferId);
        }

        GL15.glDeleteBuffers(buffer.bufferId);
        buffer.bufferId = -1;
    }

    @Override
    public void bufferGetData(GL4XBuffer buffer, long offset, ByteBuffer out) {
        state.bufferPush(GL15.GL_ARRAY_BUFFER, buffer.bufferId);

        if (RECORD_CALLS) {
            recordCall("glGetBufferSubData", "GL_ARRAY_BUFFER", offset, out);
        }

        GL15.glGetBufferSubData(GL15.GL_ARRAY_BUFFER, offset, out);

        state.bufferPop(GL15.GL_ARRAY_BUFFER);
    }

    @Override
    public int bufferGetParameterI(GL4XBuffer buffer, int paramId) {
        state.bufferPush(GL15.GL_ARRAY_BUFFER, buffer.bufferId);

        if (RECORD_CALLS) {
            recordCall("#val = glGetBufferParameteri", "GL_ARRAY_BUFFER", buffer.bufferId);
        }

        final int res = GL15.glGetBufferParameteri(GL15.GL_ARRAY_BUFFER, buffer.bufferId);

        state.bufferPop(GL15.GL_ARRAY_BUFFER);
        return res;
    }

    @Override
    public void bufferInvalidateData(GL4XBuffer buffer) {
        if (GL.getCapabilities().GL_ARB_invalidate_subdata) {
            if (RECORD_CALLS) {
                recordCall("glInvalidateBufferData", buffer.bufferId);
            }

            ARBInvalidateSubdata.glInvalidateBufferData(buffer.bufferId);
        } else {
            if (RECORD_CALLS) {
                recordCall("[ignored] glInvalidateBufferData", buffer.bufferId);
            }

            LOGGER.trace("ARB_invalidate_subdata is not supported... Ignoring call to glInvalidateBufferData.");
        }
    }

    @Override
    public void bufferInvalidateRange(GL4XBuffer buffer, long offset, long length) {
        if (GL.getCapabilities().GL_ARB_invalidate_subdata) {
            if (RECORD_CALLS) {
                recordCall("glInvalidateBufferSubData", buffer.bufferId, offset, length);
            }

            ARBInvalidateSubdata.glInvalidateBufferSubData(buffer.bufferId, offset, length);
        } else {
            if (RECORD_CALLS) {
                recordCall("[ignored] glInvalidateBufferSubData", buffer.bufferId, offset, length);
            }

            LOGGER.trace("ARB_invalidate_subdata is not supported... Ignoring call to glInvalidateBufferSubData");
        }
    }

    @Override
    public ByteBuffer bufferMapData(GL4XBuffer buffer, long offset, long length, int accessFlags) {
        state.bufferPush(GL15.GL_ARRAY_BUFFER, buffer.bufferId);

        if (RECORD_CALLS) {
            recordCall("glMapBufferRange", "GL_ARRAY_BUFFER", offset, length, accessFlags, buffer.mapBuffer);
        }

        buffer.mapBuffer = GL30.glMapBufferRange(GL15.GL_ARRAY_BUFFER, offset, length, accessFlags, buffer.mapBuffer);

        state.bufferPop(GL15.GL_ARRAY_BUFFER);

        return buffer.mapBuffer;
    }

    @Override
    public void bufferSetData(GL4XBuffer buffer, ByteBuffer data, int usage) {
        state.bufferPush(GL15.GL_ARRAY_BUFFER, buffer.bufferId);

        if (RECORD_CALLS) {
            recordCall("glBufferData", "GL_ARRAY_BUFFER", data, usage);
        }

        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, data, usage);

        state.bufferPop(GL15.GL_ARRAY_BUFFER);
    }

    @Override
    public void bufferUnmapData(GL4XBuffer buffer) {
        state.bufferPush(GL15.GL_ARRAY_BUFFER, buffer.bufferId);

        if (RECORD_CALLS) {
            recordCall("glUnmapBuffer", "GL_ARRAY_BUFFER");
        }

        GL15.glUnmapBuffer(GL15.GL_ARRAY_BUFFER);

        state.bufferPop(GL15.GL_ARRAY_BUFFER);
    }

    @Override
    public void clear(int bitfield, float red, float green, float blue, float alpha, double depth) {
        if (RECORD_CALLS) {
            recordCall("glClearColor", red, green, blue, alpha);
            recordCall("glClearDepth", depth);
            recordCall("glClear", bitfield);
        }

        GL11.glClearColor(red, green, blue, alpha);
        GL11.glClearDepth(depth);
        GL11.glClear(bitfield);
    }

    @Override
    public void depthTestDisable() {
        if (RECORD_CALLS) {
            recordCall("glDisable", "GL_DEPTH_TEST");
        }

        GL11.glDisable(GL11.GL_DEPTH_TEST);
    }

    @Override
    public void depthTestEnable(int depthTest) {
        if (RECORD_CALLS) {
            recordCall("glEnable", "GL_DEPTH_TEST");
            recordCall("glDepthFunc", depthTest);
        }

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
        state.framebufferPush(GL30.GL_FRAMEBUFFER, framebuffer.framebufferId);

        switch (texId.target) {
            case GL11.GL_TEXTURE_1D:
                if (RECORD_CALLS) {
                    recordCall("glFramebufferTexture1D", "GL_FRAMEBUFFER", attachmentId, "GL_TEXTURE_1D", texId.textureId, mipmapLevel);
                }

                GL30.glFramebufferTexture1D(GL30.GL_FRAMEBUFFER, attachmentId, GL11.GL_TEXTURE_1D, texId.textureId, mipmapLevel);
                break;
            case GL11.GL_TEXTURE_2D:
                if (RECORD_CALLS) {
                    recordCall("glFramebufferTexture2D", "GL_FRAMEBUFFER", attachmentId, "GL_TEXTURE_2D", texId.textureId, mipmapLevel);
                }

                GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, attachmentId, GL11.GL_TEXTURE_2D, texId.textureId, mipmapLevel);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported texture target!");
        }

        state.framebufferPop(GL30.GL_FRAMEBUFFER);
    }

    @Override
    public void framebufferAddRenderbuffer(GL4XFramebuffer framebuffer, int attachmentId, GL4XRenderbuffer renderbuffer) {
        state.framebufferPush(GL30.GL_FRAMEBUFFER, framebuffer.framebufferId);

        if (RECORD_CALLS) {
            recordCall("glFramebufferRenderbuffer", "GL_FRAMEBUFFER", attachmentId, "GL_RENDERBUFFER", renderbuffer.renderbufferId);
        }
        // documents does not specify if renderbuffer needs to be bound...
        GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, attachmentId, GL30.GL_RENDERBUFFER, renderbuffer.renderbufferId);

        state.framebufferPop(GL30.GL_FRAMEBUFFER);
    }

    @Override
    public void framebufferBind(GL4XFramebuffer framebuffer, IntBuffer attachments) {
        if (RECORD_CALLS) {
            recordCall("glBindFramebuffer", "GL_FRAMEBUFFER", framebuffer.framebufferId);
        }

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebuffer.framebufferId);

        if (attachments != null) {
            if (RECORD_CALLS) {
                recordCall("glDrawBuffers", attachments);
            }

            GL20.glDrawBuffers(attachments);
        }
    }

    @Override
    public void framebufferBlit(GL4XFramebuffer srcFb, int srcX0, int srcY0, int srcX1, int srcY1, GL4XFramebuffer dstFb, int dstX0, int dstY0, int dstX1, int dstY1, int bitfield, int filter) {
        state.framebufferPush(GL30.GL_READ_FRAMEBUFFER, srcFb.framebufferId);
        state.framebufferPush(GL30.GL_DRAW_FRAMEBUFFER, dstFb.framebufferId);

        if (RECORD_CALLS) {
            recordCall("glBlitFramebuffer", srcX0, srcY0, srcX1, srcY1, dstX0, dstY0, dstX1, dstY1, bitfield, filter);
        }

        GL30.glBlitFramebuffer(srcX0, srcY0, srcX1, srcY1, dstX0, dstY0, dstX1, dstY1, bitfield, filter);

        state.framebufferPop(GL30.GL_DRAW_FRAMEBUFFER);
        state.framebufferPop(GL30.GL_READ_FRAMEBUFFER);
    }

    @Override
    public GL4XFramebuffer framebufferCreate() {
        if (RECORD_CALLS) {
            recordCall("#fbId = glGenFramebuffers");
        }

        final GL4XFramebuffer fb = new GL4XFramebuffer();
        fb.framebufferId = GL30.glGenFramebuffers();
        return fb;
    }

    @Override
    public void framebufferDelete(GL4XFramebuffer framebuffer) {
        if (RECORD_CALLS) {
            recordCall("glDeleteFramebuffers", framebuffer.framebufferId);
        }

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
        state.framebufferPush(GL30.GL_FRAMEBUFFER, framebuffer.framebufferId);
        state.bufferPush(GL21.GL_PIXEL_PACK_BUFFER, dstBuffer.bufferId);

        if (RECORD_CALLS) {
            recordCall("glReadPixels", x, y, width, height, format, type, 0L);
        }

        GL11.glReadPixels(
                x, y, width, height,
                format, type,
                0L);

        state.bufferPop(GL21.GL_PIXEL_PACK_BUFFER);
        state.framebufferPop(GL30.GL_FRAMEBUFFER);
    }

    @Override
    public void framebufferGetPixels(GL4XFramebuffer framebuffer, int x, int y, int width, int height, int format, int type, ByteBuffer dstBuffer) {
        state.framebufferPush(GL30.GL_FRAMEBUFFER, framebuffer.framebufferId);

        if (RECORD_CALLS) {
            recordCall("glReadPixels", x, y, width, height, format, type, dstBuffer);
        }

        GL11.glReadPixels(
                x, y, width, height,
                format, type,
                dstBuffer);

        state.framebufferPop(GL30.GL_FRAMEBUFFER);
    }

    @Override
    public boolean framebufferIsComplete(GL4XFramebuffer framebuffer) {
        state.framebufferPush(GL30.GL_FRAMEBUFFER, framebuffer.framebufferId);

        if (RECORD_CALLS) {
            recordCall("#complete = glCheckFramebufferStatus", "GL_FRAMEBUFFER");
        }

        final int complete = GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER);

        state.framebufferPop(GL30.GL_FRAMEBUFFER);

        return complete == GL30.GL_FRAMEBUFFER_COMPLETE;
    }

    @Override
    public void maskApply(boolean red, boolean green, boolean blue, boolean alpha, boolean depth, int stencil) {
        if (RECORD_CALLS) {
            recordCall("glColorMask", red, green, blue, alpha);
            recordCall("glDepthMask", depth);
            recordCall("glStencilMask", stencil);
        }

        GL11.glColorMask(red, green, blue, alpha);
        GL11.glDepthMask(depth);
        GL11.glStencilMask(stencil);
    }

    @Override
    public void polygonSetParameters(float pointSize, float lineWidth, int frontFace, int cullFace, int polygonMode, float offsetFactor, float offsetUnits) {
        if (RECORD_CALLS) {
            recordCall("glPointSize", pointSize);
            recordCall("glLineWidth", lineWidth);
            recordCall("glFrontFace", frontFace);
        }

        GL11.glPointSize(pointSize);
        GL11.glLineWidth(lineWidth);
        GL11.glFrontFace(frontFace);

        if (cullFace == 0) {
            if (RECORD_CALLS) {
                recordCall("glDisable", "GL_CULL_FACE");
            }

            GL11.glDisable(GL11.GL_CULL_FACE);
        } else {
            if (RECORD_CALLS) {
                recordCall("glEnable", "GL_CULL_FACE");
                recordCall("glCullFace", cullFace);
            }

            GL11.glEnable(GL11.GL_CULL_FACE);
            GL11.glCullFace(cullFace);
        }

        if (RECORD_CALLS) {
            recordCall("glPolygonMode", "GL_FRONT_AND_BACK", polygonMode);
            recordCall("glPolygonOffset", offsetFactor, offsetUnits);
        }

        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, polygonMode);
        GL11.glPolygonOffset(offsetFactor, offsetUnits);
    }

    @Override
    public GL4XProgram programCreate() {
        if (RECORD_CALLS) {
            recordCall("#programId = glCreateProgram");
        }

        GL4XProgram program = new GL4XProgram();
        program.programId = GL20.glCreateProgram();
        return program;
    }

    @Override
    public void programDelete(GL4XProgram program) {
        if (RECORD_CALLS) {
            recordCall("glDeleteProgram", program.programId);
        }

        GL20.glDeleteProgram(program.programId);
        program.programId = -1;
    }

    @Override
    public void programDispatchCompute(GL4XProgram program, int numX, int numY, int numZ) {
        if (GL.getCapabilities().GL_ARB_compute_shader) {
            state.programPush(program.programId);

            if (RECORD_CALLS) {
                recordCall("glDispatchCompute", numX, numY, numZ);
            }

            ARBComputeShader.glDispatchCompute(numX, numY, numZ);

            state.programPop();
        } else {
            if (RECORD_CALLS) {
                recordCall("[unsupported] glDispatchCompute", numX, numY, numZ);
            }

            throw new UnsupportedOperationException("ARB_compute_shader is not supported!");
        }
    }

    @Override
    public int programGetUniformLocation(GL4XProgram program, String name) {
        state.programPush(program.programId);

        if (RECORD_CALLS) {
            recordCall("#loc = glGetUniformLocation", program.programId, name);
        }

        final int res = GL20.glGetUniformLocation(program.programId, name);

        state.programPop();
        return res;
    }

    @Override
    public void programLinkShaders(GL4XProgram program, Shader[] shaders) {
        for (Shader shader : shaders) {
            final int shaderId = ((GL4XShader) shader).shaderId;

            if (RECORD_CALLS) {
                recordCall("glAttachShader", program.programId, shaderId);
            }

            GL20.glAttachShader(program.programId, shaderId);
        }

        if (RECORD_CALLS) {
            recordCall("glLinkProgram", program.programId);
        }

        GL20.glLinkProgram(program.programId);

        for (Shader shader : shaders) {
            final int shaderId = ((GL4XShader) shader).shaderId;

            if (RECORD_CALLS) {
                recordCall("glDetachShader", program.programId, shaderId);
            }

            GL20.glDetachShader(program.programId, ((GL4XShader) shader).shaderId);
        }
    }

    @Override
    public void programSetAttribLocation(GL4XProgram program, int index, String name) {
        if (RECORD_CALLS) {
            recordCall("glBindAttribLocation", program.programId, index, name);
        }

        GL20.glBindAttribLocation(program.programId, index, name);
    }

    @Override
    public void programSetFeedbackBuffer(GL4XProgram program, int varyingLoc, GL4XBuffer buffer) {
        if (RECORD_CALLS) {
            recordCall("glBindBufferBase", "GL_TRANSFORM_FEEDBACK_BUFFER", varyingLoc, buffer.bufferId);
        }

        GL30.glBindBufferBase(GL30.GL_TRANSFORM_FEEDBACK_BUFFER, varyingLoc, buffer.bufferId);
    }

    @Override
    public void programSetFeedbackVaryings(GL4XProgram program, String[] varyings) {
        if (RECORD_CALLS) {
            recordCall("glTransformFeedbackVaryings", program.programId, varyings, "GL_SEPARATE_ATTRIBS");
        }

        GL30.glTransformFeedbackVaryings(program.programId, varyings, GL30.GL_SEPARATE_ATTRIBS);
    }

    @Override
    public void programSetStorage(GL4XProgram program, String storageName, GL4XBuffer buffer, int bindingPoint) {
        if (GL.getCapabilities().GL_ARB_shader_storage_buffer_object) {
            if (RECORD_CALLS) {
                recordCall("#sBlock = glGetProgramResourceLocation", program.programId, "GL_SHADER_STORAGE_BLOCK", storageName);
                recordCall("glBindBufferBase", "GL_SHADER_STORAGE_BLOCK", bindingPoint, buffer.bufferId);
                recordCall("glShaderStorageBlockBinding", program.programId, "#sBlock", bindingPoint);
            }

            final int sBlock = ARBProgramInterfaceQuery.glGetProgramResourceLocation(program.programId, ARBProgramInterfaceQuery.GL_SHADER_STORAGE_BLOCK, storageName);

            GL30.glBindBufferBase(ARBShaderStorageBufferObject.GL_SHADER_STORAGE_BUFFER, bindingPoint, buffer.bufferId);
            ARBShaderStorageBufferObject.glShaderStorageBlockBinding(program.programId, sBlock, bindingPoint);
        } else {
            if (RECORD_CALLS) {
                recordCall("#sBlock = glGetProgramResourceLocation", program.programId, "GL_SHADER_STORAGE_BLOCK", storageName);
                recordCall("glBindBufferBase", "GL_SHADER_STORAGE_BLOCK", bindingPoint, buffer.bufferId);
                recordCall("[unsupported] glShaderStorageBlockBinding", program.programId, "#sBlock", bindingPoint);
            }

            throw new UnsupportedOperationException("ARB_shader_storage_buffer_object is not supported!");
        }
    }

    @Override
    public void programSetUniformBlock(GL4XProgram program, String uniformName, GL4XBuffer buffer, int bindingPoint) {
        if (RECORD_CALLS) {
            recordCall("#uBlock = glGetUniformBlockIndex", program.programId, uniformName);
            recordCall("glBindBufferBase", "GL_UNIFORM_BUFFER", bindingPoint, buffer.bufferId);
            recordCall("glUniformBlockBinding", program.programId, "#uBlock", bindingPoint);
        }

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
            state.programPush(program.programId);

            switch (value.length) {
                case 1:
                    GL40.glUniform1d(uLoc, value[0]);
                    break;
                case 2:
                    GL40.glUniform2d(uLoc, value[0], value[1]);
                    break;
                case 3:
                    GL40.glUniform3d(uLoc, value[0], value[1], value[2]);
                    break;
                case 4:
                    GL40.glUniform4d(uLoc, value[0], value[1], value[2], value[3]);
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported vector size: " + value.length);
            }

            state.programPop();
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
    public void programSetUniformMatD(GL4XProgram program, int uLoc, DoubleBuffer mat) {
        if (GL.getCapabilities().GL_ARB_separate_shader_objects) {
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
                    GL40.glUniformMatrix2dv(uLoc, false, mat);
                    break;
                case 9:
                    GL40.glUniformMatrix3dv(uLoc, false, mat);
                    break;
                case 16:
                    GL40.glUniformMatrix4dv(uLoc, false, mat);
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported matrix size: " + mat.limit());
            }

            state.programPop();
        }
    }

    @Override
    public void programSetUniformMatF(GL4XProgram program, int uLoc, FloatBuffer mat) {
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
    public void programUse(GL4XProgram program) {
        GL20.glUseProgram(program.programId);
    }

    @Override
    public GL4XRenderbuffer renderbufferCreate(int internalFormat, int width, int height) {
        final GL4XRenderbuffer renderbuffer = new GL4XRenderbuffer();

        renderbuffer.renderbufferId = GL30.glGenRenderbuffers();

        GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, renderbuffer.renderbufferId);
        GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, internalFormat, width, height);
        return renderbuffer;
    }

    @Override
    public void renderbufferDelete(GL4XRenderbuffer renderbuffer) {
        GL30.glDeleteRenderbuffers(renderbuffer.renderbufferId);
        renderbuffer.renderbufferId = -1;
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
    public void textureAllocatePage(GL4XTexture texture, int level, int xOffset, int yOffset, int zOffset, int width, int height, int depth) {
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
    public void textureBind(GL4XTexture texture, int unit) {
        GL13.glActiveTexture(GL13.GL_TEXTURE0 + unit);
        GL11.glBindTexture(texture.target, texture.textureId);
    }

    @Override
    public void textureDeallocatePage(GL4XTexture texture, int level, int xOffset, int yOffset, int zOffset, int width, int height, int depth) {
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
    public void textureDelete(GL4XTexture texture) {
        GL11.glDeleteTextures(texture.textureId);
        texture.textureId = -1;
    }

    @Override
    public void textureGenerateMipmap(GL4XTexture texture) {
        state.texturePush(texture.target, texture.textureId);

        GL30.glGenerateMipmap(texture.target);

        state.texturePop(texture.target);
    }

    @Override
    public void textureGetData(GL4XTexture texture, int level, int format, int type, ByteBuffer out) {
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
    public int textureGetPageDepth(GL4XTexture texture) {
        if (GL.getCapabilities().GL_ARB_internalformat_query) {
            return ARBInternalformatQuery.glGetInternalformati(texture.target, texture.internalFormat, ARBSparseTexture.GL_VIRTUAL_PAGE_SIZE_Z_ARB);
        } else {
            throw new UnsupportedOperationException("ARB_internalformat_query is not supported!");
        }
    }

    @Override
    public int textureGetPageHeight(GL4XTexture texture) {
        if (GL.getCapabilities().GL_ARB_internalformat_query) {
            if(RECORD_CALLS) {
                recordCall("#VAL = [unsupported] glGetInternalformati", texture.target, texture.internalFormat, "GL_VIRTUAL_PAGE_SIZE_Y_ARB");
            }

            return ARBInternalformatQuery.glGetInternalformati(texture.target, texture.internalFormat, ARBSparseTexture.GL_VIRTUAL_PAGE_SIZE_Y_ARB);
        } else {
            if(RECORD_CALLS) {
                recordCall("#val = [unsupported] glGetInternalformati", texture.target, texture.internalFormat, "GL_VIRTUAL_PAGE_SIZE_Y_ARB");
            }

            throw new UnsupportedOperationException("ARB_internalformat_query is not supported!");
        }
    }

    @Override
    public int textureGetPageWidth(GL4XTexture texture) {
        if (GL.getCapabilities().GL_ARB_internalformat_query) {
            if(RECORD_CALLS) {
                recordCall("#val = glGetIntegernalformati", texture.target, texture.internalFormat, "GL_VIRTUAL_PAGE_SIZE_X_ARB");
            }

            return ARBInternalformatQuery.glGetInternalformati(texture.target, texture.internalFormat, ARBSparseTexture.GL_VIRTUAL_PAGE_SIZE_X_ARB);
        } else {
            if(RECORD_CALLS) {
                recordCall("#val = [unsupported] glGetInternalformati", texture.target, texture.internalFormat, "GL_VIRTUAL_PAGE_SIZE_X_ARB");
            }
            
            throw new UnsupportedOperationException("ARB_internalformat_query is not supported!");
        }
    }

    @Override
    public int textureGetPreferredFormat(int internalFormat) {
        return GL11.GL_RGBA;
    }

    @Override
    public void textureInvalidateData(GL4XTexture texture, int level) {
        if (GL.getCapabilities().GL_ARB_invalidate_subdata) {
            if(RECORD_CALLS) {
                recordCall("glInvalidateTexImage", texture.target, level);
            }

            ARBInvalidateSubdata.glInvalidateTexImage(texture.target, level);
        } else {
            if(RECORD_CALLS) {
                recordCall("glInvalidateTexImage", texture.target, level);
            }
            
            LOGGER.trace("ARB_invalidate_subdata is not supported... Ignoring call to glInvalidateTexImage.");
        }
    }

    @Override
    public void textureInvalidateRange(GL4XTexture texture, int level, int xOffset, int yOffset, int zOffset, int width, int height, int depth) {
        if (GL.getCapabilities().GL_ARB_invalidate_subdata) {
            if(RECORD_CALLS) {
                recordCall("glInvalidateTexSubImage", texture.textureId, level, xOffset, yOffset, zOffset, width, height, depth);
            }

            ARBInvalidateSubdata.glInvalidateTexSubImage(texture.textureId, level, xOffset, yOffset, zOffset, width, height, depth);
        } else {
            if(RECORD_CALLS) {
                recordCall("[ignored] glInvalidateTexSubImage", texture.textureId, level, xOffset, yOffset, zOffset, width, height, depth);
            }
            
            LOGGER.trace("ARB_invalidate_subdata is not supported... Ignoring call to glInvalidateTexSubImage.");
        }
    }

    @Override
    public void textureSetData(GL4XTexture texture, int level, int xOffset, int yOffset, int zOffset, int width, int height, int depth, int format, int type, ByteBuffer data) {
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
    public void textureSetParameter(GL4XTexture texture, int param, int value) {
        state.texturePush(texture.target, texture.textureId);
        GL11.glTexParameteri(texture.target, param, value);
        state.texturePop(texture.target);
    }

    @Override
    public void textureSetParameter(GL4XTexture texture, int param, float value) {
        state.texturePush(texture.target, texture.textureId);
        GL11.glTexParameterf(texture.target, param, value);
        state.texturePop(texture.target);
    }

    @Override
    public void vertexArrayAttachBuffer(GL4XVertexArray vao, int index, GL4XBuffer buffer, int size, int type, int stride, long offset, int divisor) {
        state.vertexArrayPush(vao.vertexArrayId);

        if (RECORD_CALLS) {
            recordCall("glBindBuffer", "GL_ARRAY_BUFFER", buffer.bufferId);
            recordCall("glEnableVertexAttribArray", index);
        }

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, buffer.bufferId);
        GL20.glEnableVertexAttribArray(index);

        if (type == GL11.GL_DOUBLE) {
            if (GL.getCapabilities().GL_ARB_vertex_attrib_64bit) {
                if(RECORD_CALLS) {
                    recordCall("glVertexAttribLPointer", index, size, type, stride, offset);
                }

                ARBVertexAttrib64Bit.glVertexAttribLPointer(index, size, type, stride, offset);
            } else {
                if(RECORD_CALLS) {
                    recordCall("[unsupported] glVertexAttribLPointer", index, size, type, stride, offset);
                }
                
                throw new UnsupportedOperationException("ARB_vertex_attrib_64bit is not supported!");
            }
        } else {
            if(RECORD_CALLS) {
                recordCall("glVertexAttribPointer", index, size, type, false, stride, offset);
            }

            GL20.glVertexAttribPointer(index, size, type, false, stride, offset);
        }

        if (divisor > 0) {
            if(RECORD_CALLS) {
                recordCall("glVertexAttribDivisor", index, divisor);
            }
            
            GL33.glVertexAttribDivisor(index, divisor);
        }

        state.vertexArrayPop();
    }

    @Override
    public void vertexArrayAttachIndexBuffer(GL4XVertexArray vao, GL4XBuffer buffer) {
        state.vertexArrayPush(vao.vertexArrayId);

        if (RECORD_CALLS) {
            recordCall("glBindBuffer", "GL_ELEMENT_ARRAY_BUFFER", buffer.bufferId);
        }

        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer.bufferId);

        state.vertexArrayPop();
    }

    @Override
    public GL4XVertexArray vertexArrayCreate() {
        final GL4XVertexArray vao = new GL4XVertexArray();

        if (RECORD_CALLS) {
            recordCall("#vaoId = glGenVertexArrays");
        }

        vao.vertexArrayId = GL30.glGenVertexArrays();
        return vao;
    }

    @Override
    public void vertexArrayDelete(GL4XVertexArray vao) {
        if (RECORD_CALLS) {
            recordCall("glDeleteVertexArrays", vao.vertexArrayId);
        }

        GL30.glDeleteVertexArrays(vao.vertexArrayId);
        vao.vertexArrayId = -1;
    }

    @Override
    public void vertexArrayDrawArrays(GL4XVertexArray vao, int drawMode, int start, int count) {
        state.vertexArrayPush(vao.vertexArrayId);

        if (RECORD_CALLS) {
            recordCall("glDrawArrays", drawMode, start, count);
        }

        GL11.glDrawArrays(drawMode, start, count);

        state.vertexArrayPop();
    }

    @Override
    public void vertexArrayDrawArraysIndirect(GL4XVertexArray vao, GL4XBuffer cmdBuffer, int drawMode, long offset) {
        state.vertexArrayPush(vao.vertexArrayId);
        state.bufferPush(GL40.GL_DRAW_INDIRECT_BUFFER, cmdBuffer.bufferId);

        if (RECORD_CALLS) {
            recordCall("glDrawArraysIndirect", drawMode, offset);
        }

        GL40.glDrawArraysIndirect(drawMode, offset);

        state.bufferPop(GL40.GL_DRAW_INDIRECT_BUFFER);
        state.vertexArrayPop();
    }

    @Override
    public void vertexArrayDrawArraysInstanced(GL4XVertexArray vao, int drawMode, int first, int count, int instanceCount) {
        state.vertexArrayPush(vao.vertexArrayId);

        if (RECORD_CALLS) {
            recordCall("glDrawArraysInstanced", drawMode, first, count, instanceCount);
        }

        GL31.glDrawArraysInstanced(drawMode, first, count, instanceCount);

        state.vertexArrayPop();
    }

    @Override
    public void vertexArrayDrawElements(GL4XVertexArray vao, int drawMode, int count, int type, long offset) {
        state.vertexArrayPush(vao.vertexArrayId);

        if (RECORD_CALLS) {
            recordCall("glDrawElements", drawMode, count, type, offset);
        }

        GL11.glDrawElements(drawMode, count, type, offset);

        state.vertexArrayPop();
    }

    @Override
    public void vertexArrayDrawElementsIndirect(GL4XVertexArray vao, GL4XBuffer cmdBuffer, int drawMode, int indexType, long offset) {
        state.vertexArrayPush(vao.vertexArrayId);
        state.bufferPush(GL40.GL_DRAW_INDIRECT_BUFFER, cmdBuffer.bufferId);

        if (RECORD_CALLS) {
            recordCall("glDrawElementsInstanced", drawMode, indexType, offset);
        }

        GL40.glDrawElementsIndirect(drawMode, indexType, offset);

        state.bufferPop(GL40.GL_DRAW_INDIRECT_BUFFER);
        state.vertexArrayPop();
    }

    @Override
    public void vertexArrayDrawElementsInstanced(GL4XVertexArray vao, int drawMode, int count, int type, long offset, int instanceCount) {
        state.vertexArrayPush(vao.vertexArrayId);

        if (RECORD_CALLS) {
            recordCall("glDrawElementsInstanced", drawMode, count, type, offset, instanceCount);
        }

        GL31.glDrawElementsInstanced(drawMode, count, type, offset, instanceCount);

        state.vertexArrayPop();
    }

    @Override
    public void vertexArrayDrawTransformFeedback(GL4XVertexArray vao, int drawMode, int start, int count) {
        state.vertexArrayPush(vao.vertexArrayId);

        if (RECORD_CALLS) {
            recordCall("glEnable", "GL_RASTERIZER_DISCARD");
            recordCall("glBeginTransformFeedback", drawMode);
            recordCall("glDrawArrays", drawMode, start, count);
            recordCall("glEndTransformFeedback");
            recordCall("glDisable", "GL_RASTERIZER_DISCARD");
        }

        GL11.glEnable(GL30.GL_RASTERIZER_DISCARD);
        GL30.glBeginTransformFeedback(drawMode);
        GL11.glDrawArrays(drawMode, start, count);
        GL30.glEndTransformFeedback();
        GL11.glDisable(GL30.GL_RASTERIZER_DISCARD);

        state.vertexArrayPop();
    }

    @Override
    public void vertexArrayMultiDrawArrays(GL4XVertexArray vao, int drawMode, IntBuffer first, IntBuffer count) {
        state.vertexArrayPush(vao.vertexArrayId);

        if (RECORD_CALLS) {
            recordCall("glMultiDrawArrays", drawMode, first, count);
        }
        GL14.glMultiDrawArrays(drawMode, first, count);

        state.vertexArrayPop();
    }

    @Override
    public void viewportApply(int x, int y, int width, int height) {
        if (RECORD_CALLS) {
            recordCall("glViewport", x, y, width, height);
        }
        GL11.glViewport(x, y, width, height);
    }
}
