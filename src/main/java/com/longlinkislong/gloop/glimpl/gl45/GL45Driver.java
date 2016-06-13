/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop.glimpl.gl45;

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
import org.lwjgl.opengl.ARBSparseTexture;
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
import org.lwjgl.opengl.GL33;
import org.lwjgl.opengl.GL40;
import org.lwjgl.opengl.GL41;
import org.lwjgl.opengl.GL42;
import org.lwjgl.opengl.GL43;
import org.lwjgl.opengl.GL45;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author zmichaels
 */
final class GL45Driver implements Driver<
        GL45Buffer, GL45Framebuffer, GL45Renderbuffer, GL45Texture, GL45Shader, GL45Program, GL45Sampler, GL45VertexArray, GL45DrawQuery> {

    private final static boolean RECORD_CALLS = Boolean.getBoolean("com.longlinkislong.gloop.glimpl.gl45.gl45driver.record_calls");
    private final List<String> callHistory = new ArrayList<>(0);

    private final Logger LOGGER = LoggerFactory.getLogger("GL45Driver");
    private GLState state = new GLState(new Tweaks());

    private void recordCall(String call, Object... params) {
        final StringBuilder record = new StringBuilder(call);

        record.append("(");

        if (params.length > 0) {
            for (int i = 0; i < params.length - 1; i++) {
                record.append(params[i].toString());
                record.append(", ");
            }

            record.append(params[params.length - 1].toString());
        }

        record.append(")");

        callHistory.add(record.toString());
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
        return 450;
    }

    @Override
    public void applyTweaks(final Tweaks tweaks) {
        this.state = new GLState(tweaks);
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
    public void bufferAllocate(GL45Buffer buffer, long size, int usage) {
        if (RECORD_CALLS) {
            recordCall("glNamedBufferData", buffer.bufferId, size, usage);
        }

        GL45.glNamedBufferData(buffer.bufferId, size, usage);
    }

    @Override
    public void bufferAllocateImmutable(GL45Buffer buffer, long size, int bitflags) {
        if (RECORD_CALLS) {
            recordCall("glNamedBufferStorage", buffer.bufferId, size, bitflags);
        }

        GL45.glNamedBufferStorage(buffer.bufferId, size, bitflags);
    }

    @Override
    public void bufferCopyData(GL45Buffer srcBuffer, long srcOffset, GL45Buffer dstBuffer, long dstOffset, long size) {
        if (RECORD_CALLS) {
            recordCall("glCopyNamedBufferSubData", srcBuffer.bufferId, dstBuffer.bufferId, srcOffset, dstOffset, size);
        }

        GL45.glCopyNamedBufferSubData(srcBuffer.bufferId, dstBuffer.bufferId, srcOffset, dstOffset, size);
    }

    @Override
    public GL45Buffer bufferCreate() {
        final GL45Buffer buffer = new GL45Buffer();

        if (RECORD_CALLS) {
            recordCall("#bufferId = glCreateBuffers");
        }

        buffer.bufferId = GL45.glCreateBuffers();

        return buffer;
    }

    @Override
    public void bufferDelete(GL45Buffer buffer) {
        if (RECORD_CALLS) {
            recordCall("glDeleteBuffers", buffer.bufferId);
        }

        GL15.glDeleteBuffers(buffer.bufferId);

        buffer.bufferId = -1;
    }

    @Override
    public void bufferGetData(GL45Buffer buffer, long offset, ByteBuffer out) {
        if (RECORD_CALLS) {
            recordCall("glGetNamedBufferSubData", buffer.bufferId, offset, out);
        }

        GL45.glGetNamedBufferSubData(buffer.bufferId, offset, out);
    }

    @Override
    public int bufferGetParameterI(GL45Buffer buffer, int paramId) {
        if (RECORD_CALLS) {
            recordCall("#val = glGetNamedBufferParameteri", buffer.bufferId, paramId);
        }

        return GL45.glGetNamedBufferParameteri(buffer.bufferId, paramId);
    }

    @Override
    public void bufferInvalidateData(GL45Buffer buffer) {
        if (RECORD_CALLS) {
            recordCall("glInvalidateBufferData", buffer.bufferId);
        }

        GL43.glInvalidateBufferData(buffer.bufferId);
    }

    @Override
    public void bufferInvalidateRange(GL45Buffer buffer, long offset, long length) {
        if (RECORD_CALLS) {
            recordCall("glInvalidateBufferSubData", buffer.bufferId, offset, length);
        }

        GL43.glInvalidateBufferSubData(buffer.bufferId, offset, length);
    }

    @Override
    public ByteBuffer bufferMapData(GL45Buffer buffer, long offset, long length, int accessFlags) {
        if (RECORD_CALLS) {
            recordCall("glMapNamedBufferRange", buffer.bufferId, offset, length, accessFlags, buffer.mapBuffer);
        }
        
        buffer.mapBuffer = GL45.glMapNamedBufferRange(buffer.bufferId, offset, length, accessFlags, buffer.mapBuffer);
        return buffer.mapBuffer;
    }

    @Override
    public void bufferSetData(GL45Buffer buffer, ByteBuffer data, int usage) {
        if (RECORD_CALLS) {
            recordCall("glNamedBufferData", buffer.bufferId, data, usage);
        }

        GL45.glNamedBufferData(buffer.bufferId, data, usage);
    }

    @Override
    public void bufferUnmapData(GL45Buffer buffer) {
        if (RECORD_CALLS) {
            recordCall("glUnmapNamedBuffer", buffer.bufferId);
        }

        GL45.glUnmapNamedBuffer(buffer.bufferId);
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
    public void drawQueryBeginConditionalRender(GL45DrawQuery query, int mode) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public GL45DrawQuery drawQueryCreate() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void drawQueryDelete(GL45DrawQuery query) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void drawQueryDisable(int condition) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void drawQueryEnable(int condition, GL45DrawQuery query) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void drawQueryEndConditionRender() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void framebufferAddAttachment(GL45Framebuffer framebuffer, int attachmentId, GL45Texture texture, int mipmapLevel) {
        if (RECORD_CALLS) {
            recordCall("glNamedFramebufferTexture", framebuffer.framebufferId, attachmentId, texture.textureId, mipmapLevel);
        }

        GL45.glNamedFramebufferTexture(
                framebuffer.framebufferId,
                attachmentId,
                texture.textureId,
                mipmapLevel);
    }

    @Override
    public void framebufferAddRenderbuffer(GL45Framebuffer framebuffer, int attachmentId, GL45Renderbuffer renderbuffer) {
        if (RECORD_CALLS) {
            recordCall("glNamedFramebufferRenderbuffer", framebuffer.framebufferId, attachmentId, "GL_RENDERBUFFER", renderbuffer.renderbufferId);
        }

        GL45.glNamedFramebufferRenderbuffer(
                framebuffer.framebufferId,
                attachmentId,
                GL30.GL_RENDERBUFFER,
                renderbuffer.renderbufferId);
    }

    @Override
    public void framebufferBind(GL45Framebuffer framebuffer, IntBuffer attachments) {
        if (RECORD_CALLS) {
            recordCall("glBindFramebuffer", "GL_FRAMEBUFFER", framebuffer.framebufferId);
        }

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebuffer.framebufferId);

        if (attachments != null) {
            if (RECORD_CALLS) {
                recordCall("glNamedFramebufferDrawBuffers", framebuffer.framebufferId, attachments);
            }

            GL45.glNamedFramebufferDrawBuffers(framebuffer.framebufferId, attachments);
        }
    }

    @Override
    public void framebufferBlit(GL45Framebuffer srcFb, int srcX0, int srcY0, int srcX1, int srcY1, GL45Framebuffer dstFb, int dstX0, int dstY0, int dstX1, int dstY1, int bitfield, int filter) {
        if (RECORD_CALLS) {
            recordCall("glBlitNamedFramebuffer",
                    srcFb.framebufferId, dstFb.framebufferId,
                    srcX0, srcY0, srcX1, srcY1,
                    dstX0, dstY0, dstX1, dstY1,
                    bitfield, filter);
        }

        GL45.glBlitNamedFramebuffer(
                srcFb.framebufferId,
                dstFb.framebufferId,
                srcX0, srcY0, srcX1, srcY1,
                dstX0, dstY0, dstX1, dstY1,
                bitfield, filter);
    }

    @Override
    public GL45Framebuffer framebufferCreate() {
        if (RECORD_CALLS) {
            recordCall("#fbId = glCreateFramebuffers");
        }

        final int fbId = GL45.glCreateFramebuffers();
        final GL45Framebuffer framebuffer = new GL45Framebuffer();

        framebuffer.framebufferId = fbId;
        return framebuffer;
    }

    @Override
    public void framebufferDelete(GL45Framebuffer framebuffer) {
        if (RECORD_CALLS) {
            recordCall("glDeleteFramebuffers", framebuffer.framebufferId);
        }

        GL30.glDeleteFramebuffers(framebuffer.framebufferId);
        framebuffer.framebufferId = -1;
    }

    @Override
    public GL45Framebuffer framebufferGetDefault() {
        final GL45Framebuffer fb = new GL45Framebuffer();
        fb.framebufferId = 0;
        return fb;
    }

    @Override
    public void framebufferGetPixels(GL45Framebuffer framebuffer, int x, int y, int width, int height, int format, int type, GL45Buffer dstBuffer) {
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
    public void framebufferGetPixels(GL45Framebuffer framebuffer, int x, int y, int width, int height, int format, int type, ByteBuffer dstBuffer) {
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
    public boolean framebufferIsComplete(GL45Framebuffer framebuffer) {
        if (RECORD_CALLS) {
            recordCall("#res = glCheckNamedFramebufferStatus", framebuffer.framebufferId, "GL_FRAMEBUFFER");
        }

        final int result = GL45.glCheckNamedFramebufferStatus(framebuffer.framebufferId, GL30.GL_FRAMEBUFFER);

        switch (result) {
            case GL30.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT:
                LOGGER.warn("GLFramebuffer has an incomplete attachment!");
                return false;
            case GL30.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER:
                LOGGER.warn("GLFramebuffer has an incomplete draw buffer!");
                return false;
            case GL30.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT:
                LOGGER.warn("GLFramebuffer has a missing attachment!");
                return false;
            case GL30.GL_FRAMEBUFFER_INCOMPLETE_MULTISAMPLE:
                LOGGER.warn("GLFramebuffer has incomplete multisample!");
                return false;
            case GL30.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER:
                LOGGER.warn("GLFramebuffer has incomplete read buffer!");
                return false;
            case GL30.GL_FRAMEBUFFER_COMPLETE:
                return true;
            default:
                LOGGER.warn("Unknown incomplete framebuffer status: {}", result);
                return false;
        }
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
    public GL45Program programCreate() {
        if (RECORD_CALLS) {
            recordCall("#pId = glCreateProgram");
        }

        final GL45Program program = new GL45Program();

        program.programId = GL20.glCreateProgram();
        return program;
    }

    @Override
    public void programDelete(GL45Program program) {
        if (RECORD_CALLS) {
            recordCall("glDeleteProgram", program.programId);
        }

        GL20.glDeleteProgram(program.programId);
        program.programId = -1;
    }

    @Override
    public void programDispatchCompute(GL45Program program, int numX, int numY, int numZ) {
        state.programPush(program.programId);

        if (RECORD_CALLS) {
            recordCall("glDispatchCompute", numX, numY, numZ);
        }

        GL43.glDispatchCompute(numX, numY, numZ);

        state.programPop();
    }

    @Override
    public int programGetUniformLocation(GL45Program program, String name) {
        state.programPush(program.programId);

        if (RECORD_CALLS) {
            recordCall("glGetUniformLocation", program.programId, name);
        }

        final int res = GL20.glGetUniformLocation(program.programId, name);

        state.programPop();

        return res;
    }

    @Override
    public void programLinkShaders(GL45Program program, Shader[] shaders) {
        for (Shader shader : shaders) {
            final int shaderId = ((GL45Shader) shader).shaderId;

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
            final int shaderId = ((GL45Shader) shader).shaderId;

            if (RECORD_CALLS) {
                recordCall("glDetachShader", program.programId, shaderId);
            }

            GL20.glDetachShader(program.programId, shaderId);
        }
    }

    @Override
    public void programSetAttribLocation(GL45Program program, int index, String name) {
        if (RECORD_CALLS) {
            recordCall("glBindAttribLocation", program.programId, index, name);
        }

        GL20.glBindAttribLocation(program.programId, index, name);
    }

    @Override
    public void programSetFeedbackBuffer(GL45Program program, int varyingLoc, GL45Buffer buffer) {
        if (RECORD_CALLS) {
            recordCall("glBindBufferBase", "GL_TRANSFORM_FEEDBACK_BUFFER", varyingLoc, buffer.bufferId);
        }

        GL30.glBindBufferBase(GL30.GL_TRANSFORM_FEEDBACK_BUFFER, varyingLoc, buffer.bufferId);
    }

    @Override
    public void programSetFeedbackVaryings(GL45Program program, String[] varyings) {
        if (RECORD_CALLS) {
            recordCall("glTransformFeedbackVaryings", program.programId, varyings, "GL_SEPARATE_ATTRIBS");
        }

        GL30.glTransformFeedbackVaryings(program.programId, varyings, GL30.GL_SEPARATE_ATTRIBS);
    }

    @Override
    public void programSetStorage(GL45Program program, String storageName, GL45Buffer buffer, int bindingPoint) {
        if (RECORD_CALLS) {
            recordCall("#sBlock = glGetProgramResourceLocation", program.programId, "GL_SHADER_STORAGE_BLOCK", storageName);
            recordCall("glBindBufferBase", "GL_SHADER_STORAGE_BUFFER", bindingPoint, buffer.bufferId);
            recordCall("glShaderStorageBlockBinding", program.programId, "#sBlock", bindingPoint);
        }

        final int sBlock = GL43.glGetProgramResourceLocation(program.programId, GL43.GL_SHADER_STORAGE_BLOCK, storageName);

        GL30.glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, bindingPoint, buffer.bufferId);
        GL43.glShaderStorageBlockBinding(program.programId, sBlock, bindingPoint);
    }

    @Override
    public void programSetUniformBlock(GL45Program program, String uniformName, GL45Buffer buffer, int bindingPoint) {
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
    public void programSetUniformD(GL45Program program, int uLoc, double[] value) {
        switch (value.length) {
            case 1:
                if (RECORD_CALLS) {
                    recordCall("glProgramUniform1d", program.programId, uLoc, value[0]);
                }

                GL41.glProgramUniform1d(program.programId, uLoc, value[0]);
                break;
            case 2:
                if (RECORD_CALLS) {
                    recordCall("glProgramUniform2d", program.programId, uLoc, value[0], value[1]);
                }

                GL41.glProgramUniform2d(program.programId, uLoc, value[0], value[1]);
                break;
            case 3:
                if (RECORD_CALLS) {
                    recordCall("glProgramUniform3d", program.programId, uLoc, value[0], value[1], value[2]);
                }

                GL41.glProgramUniform3d(program.programId, uLoc, value[0], value[1], value[2]);
                break;
            case 4:
                if (RECORD_CALLS) {
                    recordCall("glProgramUniform4d", program.programId, uLoc, value[0], value[1], value[2], value[3]);
                }

                GL41.glProgramUniform4d(program.programId, uLoc, value[0], value[1], value[2], value[3]);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported uniform vector size: " + value.length);
        }
    }

    @Override
    public void programSetUniformF(GL45Program program, int uLoc, float[] value) {
        switch (value.length) {
            case 1:
                if (RECORD_CALLS) {
                    recordCall("glProgramUniform1f", program.programId, uLoc, value[0]);
                }

                GL41.glProgramUniform1f(program.programId, uLoc, value[0]);
                break;
            case 2:
                if (RECORD_CALLS) {
                    recordCall("glProgramUniform2f", program.programId, uLoc, value[0], value[1]);
                }

                GL41.glProgramUniform2f(program.programId, uLoc, value[0], value[1]);
                break;
            case 3:
                if (RECORD_CALLS) {
                    recordCall("glProgramUniform3f", program.programId, uLoc, value[0], value[1], value[2]);
                }

                GL41.glProgramUniform3f(program.programId, uLoc, value[0], value[1], value[2]);
                break;
            case 4:
                if (RECORD_CALLS) {
                    recordCall("glProgramUniform4f", program.programId, uLoc, value[0], value[1], value[2], value[3]);
                }

                GL41.glProgramUniform4f(program.programId, uLoc, value[0], value[1], value[2], value[3]);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported uniform vector size: " + value.length);
        }
    }

    @Override
    public void programSetUniformI(GL45Program program, int uLoc, int[] value) {
        switch (value.length) {
            case 1:
                if (RECORD_CALLS) {
                    recordCall("glProgramUniform1i", program.programId, uLoc, value[0]);
                }

                GL41.glProgramUniform1i(program.programId, uLoc, value[0]);
                break;
            case 2:
                if (RECORD_CALLS) {
                    recordCall("glProgramUniform2i", program.programId, uLoc, value[0], value[1]);
                }

                GL41.glProgramUniform2i(program.programId, uLoc, value[0], value[1]);
                break;
            case 3:
                if (RECORD_CALLS) {
                    recordCall("glProgramUniform3i", program.programId, uLoc, value[0], value[1], value[2]);
                }

                GL41.glProgramUniform3i(program.programId, uLoc, value[0], value[1], value[2]);
                break;
            case 4:
                if (RECORD_CALLS) {
                    recordCall("glProgramUniform4i", program.programId, uLoc, value[0], value[1], value[2], value[3]);
                }

                GL41.glProgramUniform4i(program.programId, uLoc, value[0], value[1], value[2], value[3]);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported uniform vector size: " + value.length);
        }
    }

    @Override
    public void programSetUniformMatD(GL45Program program, int uLoc, DoubleBuffer mat) {
        switch (mat.remaining()) {
            case 4:
                if (RECORD_CALLS) {
                    recordCall("glProgramUniformMatrix2dv", program.programId, uLoc, false, mat);
                }

                GL41.glProgramUniformMatrix2dv(program.programId, uLoc, false, mat);
                break;
            case 9:
                if (RECORD_CALLS) {
                    recordCall("glProgramUniformMatrix3dv", program.programId, uLoc, false, mat);
                }

                GL41.glProgramUniformMatrix3dv(program.programId, uLoc, false, mat);
                break;
            case 16:
                if (RECORD_CALLS) {
                    recordCall("glProgramUniformMatrix4dv", program.programId, uLoc, false, mat);
                }

                GL41.glProgramUniformMatrix4dv(program.programId, uLoc, false, mat);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported matrix size: " + mat.limit());
        }
    }

    @Override
    public void programSetUniformMatF(GL45Program program, int uLoc, FloatBuffer mat) {
        switch (mat.remaining()) {
            case 4:
                if (RECORD_CALLS) {
                    recordCall("glProgramUniformMatrix2fv", program.programId, uLoc, false, mat);
                }

                GL41.glProgramUniformMatrix2fv(program.programId, uLoc, false, mat);
                break;
            case 9:
                if (RECORD_CALLS) {
                    recordCall("glProgramUniformMatrix3fv", program.programId, uLoc, false, mat);
                }

                GL41.glProgramUniformMatrix3fv(program.programId, uLoc, false, mat);
                break;
            case 16:
                if (RECORD_CALLS) {
                    recordCall("glProgramUniformMatrix4fv", program.programId, uLoc, false, mat);
                }

                GL41.glProgramUniformMatrix4fv(program.programId, uLoc, false, mat);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported matrix size: " + mat.limit());
        }
    }

    @Override
    public void programUse(GL45Program program) {
        if (RECORD_CALLS) {
            recordCall("glUseProgram", program.programId);
        }

        GL20.glUseProgram(program.programId);
    }

    @Override
    public GL45Renderbuffer renderbufferCreate(int internalFormat, int width, int height) {
        if (RECORD_CALLS) {
            recordCall("#renderbufferId = glCreateRenderbuffers");
        }

        final GL45Renderbuffer renderbuffer = new GL45Renderbuffer();

        renderbuffer.renderbufferId = GL45.glCreateRenderbuffers();
        GL45.glNamedRenderbufferStorage(renderbuffer.renderbufferId, internalFormat, width, height);

        return renderbuffer;
    }

    @Override
    public void renderbufferDelete(GL45Renderbuffer renderbuffer) {
        if (RECORD_CALLS) {
            recordCall("glDeleteRenderbuffers", renderbuffer.renderbufferId);
        }

        GL30.glDeleteRenderbuffers(renderbuffer.renderbufferId);
        renderbuffer.renderbufferId = -1;
    }

    @Override
    public void samplerBind(int unit, GL45Sampler sampler) {
        if (RECORD_CALLS) {
            recordCall("glBindSampler", unit, sampler.samplerId);
        }

        GL33.glBindSampler(unit, sampler.samplerId);
    }

    @Override
    public GL45Sampler samplerCreate() {
        if (RECORD_CALLS) {
            recordCall("#samplerId = glCreateSamplers");
        }

        final GL45Sampler sampler = new GL45Sampler();
        sampler.samplerId = GL45.glCreateSamplers();
        return sampler;
    }

    @Override
    public void samplerDelete(GL45Sampler sampler) {
        if (RECORD_CALLS) {
            recordCall("glDeleteSamplers", sampler.samplerId);
        }

        GL33.glDeleteSamplers(sampler.samplerId);
        sampler.samplerId = -1;
    }

    @Override
    public void samplerSetParameter(GL45Sampler sampler, int param, int value) {
        if (RECORD_CALLS) {
            recordCall("glSamplerParameteri", sampler.samplerId, param, value);
        }

        GL33.glSamplerParameteri(sampler.samplerId, param, value);
    }

    @Override
    public void samplerSetParameter(GL45Sampler sampler, int param, float value) {
        if (RECORD_CALLS) {
            recordCall("glSamplerParameterf", sampler.samplerId, param, value);
        }

        GL33.glSamplerParameterf(sampler.samplerId, param, value);
    }

    @Override
    public void scissorTestDisable() {
        if (RECORD_CALLS) {
            recordCall("glDisable", "GL_SCISSOR_TEST");
        }

        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    @Override
    public void scissorTestEnable(int left, int bottom, int width, int height) {
        if (RECORD_CALLS) {
            recordCall("glEnable", "GL_SCISSOR_TEST");
            recordCall("glScissor", left, bottom, width, height);
        }

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(left, bottom, width, height);
    }

    @Override
    public GL45Shader shaderCompile(int type, String source) {
        if (RECORD_CALLS) {
            recordCall("#shaderId = glCreateShader", type);
            recordCall("glShaderSource", "#shaderId", source);
            recordCall("glCompileShader", "#shaderId");
        }

        final GL45Shader shader = new GL45Shader();

        shader.shaderId = GL20.glCreateShader(type);
        GL20.glShaderSource(shader.shaderId, source);
        GL20.glCompileShader(shader.shaderId);

        return shader;
    }

    @Override
    public void shaderDelete(GL45Shader shader) {
        if (RECORD_CALLS) {
            recordCall("glDeleteShader", shader.shaderId);
        }

        GL20.glDeleteShader(shader.shaderId);
        shader.shaderId = -1;
    }

    @Override
    public String shaderGetInfoLog(GL45Shader shader) {
        if (RECORD_CALLS) {
            recordCall("$shaderInfoLog = glGetShaderInfoLog", shader.shaderId);
        }

        return GL20.glGetShaderInfoLog(shader.shaderId);
    }

    @Override
    public int shaderGetParameterI(GL45Shader shader, int pName) {
        if (RECORD_CALLS) {
            recordCall("#val = glGetShaderi", shader.shaderId, pName);
        }

        return GL20.glGetShaderi(shader.shaderId, pName);
    }

    @Override
    public GL45Texture textureAllocate(int mipmaps, int internalFormat, int width, int height, int depth) {
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

        if (RECORD_CALLS) {
            recordCall("#textureId = glCreateTextures", target);
        }

        final GL45Texture texture = new GL45Texture();

        texture.textureId = GL45.glCreateTextures(target);
        texture.target = target;
        texture.internalFormat = internalFormat;

        switch (target) {
            case GL11.GL_TEXTURE_1D:
                if (RECORD_CALLS) {
                    recordCall("glTextureStorage1D", texture.textureId, mipmaps, internalFormat, width);
                }

                GL45.glTextureStorage1D(texture.textureId, mipmaps, internalFormat, width);
                break;
            case GL11.GL_TEXTURE_2D:
                if (RECORD_CALLS) {
                    recordCall("glTextureStorage2D", texture.textureId, mipmaps, internalFormat, width, height);
                }

                GL45.glTextureStorage2D(texture.textureId, mipmaps, internalFormat, width, height);
                break;
            case GL12.GL_TEXTURE_3D:
                if (RECORD_CALLS) {
                    recordCall("glTextureStorage3D", texture.textureId, mipmaps, internalFormat, width, height, depth);
                }

                GL45.glTextureStorage3D(texture.textureId, mipmaps, internalFormat, width, height, depth);
                break;
        }

        return texture;
    }

    @Override
    public void textureAllocatePage(GL45Texture texture, int level, int xOffset, int yOffset, int zOffset, int width, int height, int depth) {
        if (RECORD_CALLS) {
            recordCall("glTexPageCommitmentARB",
                    texture.textureId, level,
                    xOffset, yOffset, zOffset,
                    width, height, depth,
                    true);
        }

        ARBSparseTexture.glTexPageCommitmentARB(
                texture.textureId, level,
                xOffset, yOffset, zOffset,
                width, height, depth,
                true);
    }

    @Override
    public void textureBind(GL45Texture texture, int unit) {
        if (RECORD_CALLS) {
            recordCall("glBindTextureUnit", unit, texture.textureId);
        }

        GL45.glBindTextureUnit(unit, texture.textureId);
    }

    @Override
    public void textureDeallocatePage(GL45Texture texture, int level, int xOffset, int yOffset, int zOffset, int width, int height, int depth) {
        if (RECORD_CALLS) {
            recordCall("glTexPageCommitmentARB",
                    texture.textureId, level,
                    xOffset, yOffset, zOffset,
                    width, height, depth,
                    false);
        }

        ARBSparseTexture.glTexPageCommitmentARB(
                texture.textureId, level,
                xOffset, yOffset, zOffset,
                width, height, depth,
                false);
    }

    @Override
    public void textureDelete(GL45Texture texture) {
        if (RECORD_CALLS) {
            recordCall("glDeleteTextures", texture.textureId);
        }

        GL11.glDeleteTextures(texture.textureId);
        texture.textureId = -1;
        texture.target = -1;
    }

    @Override
    public void textureGenerateMipmap(GL45Texture texture) {
        if (RECORD_CALLS) {
            recordCall("glGenerateTextureMipmap", texture.textureId);
        }

        GL45.glGenerateTextureMipmap(texture.textureId);
    }

    @Override
    public void textureGetData(GL45Texture texture, int level, int format, int type, ByteBuffer out) {
        if (RECORD_CALLS) {
            recordCall("glGetTextureImage", texture.target, level, format, type, out);
        }

        GL45.glGetTextureImage(texture.target, level, format, type, out);
    }

    @Override
    public float textureGetMaxAnisotropy() {
        if (GL.getCapabilities().GL_EXT_texture_filter_anisotropic) {
            if (RECORD_CALLS) {
                recordCall("#maxAniso = glGetFloat", "GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT");
            }

            return GL11.glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT);
        } else {
            return 1F;
        }
    }

    @Override
    public int textureGetMaxBoundTextures() {
        if (RECORD_CALLS) {
            recordCall("#units = glGetInteger", "GL_MAX_TEXTURE_IMAGE_UNITS");
        }

        return GL11.glGetInteger(GL20.GL_MAX_TEXTURE_IMAGE_UNITS);
    }

    @Override
    public int textureGetMaxSize() {
        if (RECORD_CALLS) {
            recordCall("#maxSize = glGetInteger", "GL_MAX_TEXTURE_SIZE");
        }

        return GL11.glGetInteger(GL11.GL_MAX_TEXTURE_SIZE);
    }

    @Override
    public int textureGetPageDepth(GL45Texture texture) {
        if (RECORD_CALLS) {
            recordCall("#depth = glGetIntegernalformati", texture.target, texture.internalFormat, "GL_VIRTUAL_PAGE_SIZE_Z_ARB");
        }

        return GL42.glGetInternalformati(texture.target, texture.internalFormat, ARBSparseTexture.GL_VIRTUAL_PAGE_SIZE_Z_ARB);
    }

    @Override
    public int textureGetPageHeight(GL45Texture texture) {
        if (RECORD_CALLS) {
            recordCall("#height = glGetInternalformati", texture.target, texture.internalFormat, "GL_VIRTUAL_PAGE_SIZE_Y_ARB");
        }

        return GL42.glGetInternalformati(texture.target, texture.internalFormat, ARBSparseTexture.GL_VIRTUAL_PAGE_SIZE_Y_ARB);
    }

    @Override
    public int textureGetPageWidth(GL45Texture texture) {
        if (RECORD_CALLS) {
            recordCall("#width = glGetInternalformati", texture.target, texture.internalFormat, "GL_VIRTUAL_PAGE_SIZE_X_ARB");
        }

        return GL42.glGetInternalformati(texture.target, texture.internalFormat, ARBSparseTexture.GL_VIRTUAL_PAGE_SIZE_X_ARB);
    }

    @Override
    public int textureGetPreferredFormat(int internalFormat) {
        return GL11.GL_RGBA;
    }

    @Override
    public void textureInvalidateData(GL45Texture texture, int level) {
        if (RECORD_CALLS) {
            recordCall("glInvalidateTexImage", texture.textureId, level);
        }

        GL43.glInvalidateTexImage(texture.textureId, level);
    }

    @Override
    public void textureInvalidateRange(GL45Texture texture, int level, int xOffset, int yOffset, int zOffset, int width, int height, int depth) {
        if (RECORD_CALLS) {
            recordCall("glInvalidateTexSubImage", texture.textureId, level, xOffset, yOffset, zOffset, width, height, depth);
        }

        GL43.glInvalidateTexSubImage(texture.textureId, level, xOffset, yOffset, zOffset, width, height, depth);
    }

    @Override
    public void textureSetData(GL45Texture texture, int level, int xOffset, int yOffset, int zOffset, int width, int height, int depth, int format, int type, ByteBuffer data) {
        switch (texture.target) {
            case GL11.GL_TEXTURE_1D:
                if (RECORD_CALLS) {
                    recordCall("glTextureSubImage1D", texture.textureId, level, xOffset, width, format, type, data);
                }

                GL45.glTextureSubImage1D(texture.textureId, level, xOffset, width, format, type, data);
                break;
            case GL11.GL_TEXTURE_2D:
                if (RECORD_CALLS) {
                    recordCall("glTextureSubImage2D", texture.textureId, level, xOffset, yOffset, width, height, format, type, data);
                }

                GL45.glTextureSubImage2D(texture.textureId, level, xOffset, yOffset, width, height, format, type, data);
                break;
            case GL12.GL_TEXTURE_3D:
                if (RECORD_CALLS) {
                    recordCall("glTextureSubImage3D", texture.textureId, level, xOffset, yOffset, zOffset, width, height, depth, format, type, data);
                }

                GL45.glTextureSubImage3D(texture.textureId, level, xOffset, yOffset, zOffset, width, height, depth, format, type, data);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported texture type: " + texture.target);
        }
    }

    @Override
    public void textureSetParameter(GL45Texture texture, int param, int value) {
        if (RECORD_CALLS) {
            recordCall("glTextureParameteri", texture.textureId, param, value);
        }

        GL45.glTextureParameteri(texture.textureId, param, value);
    }

    @Override
    public void textureSetParameter(GL45Texture texture, int param, float value) {
        if (RECORD_CALLS) {
            recordCall("glTextureParameterf", texture.textureId, param, value);
        }

        GL45.glTextureParameterf(texture.textureId, param, value);
    }

    @Override
    public void vertexArrayAttachBuffer(GL45VertexArray vao, int index, GL45Buffer buffer, int size, int type, int stride, long offset, int divisor) {
        if (stride == 0) {
            switch (type) {
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

        if (RECORD_CALLS) {
            final int vaoId = vao.vertexArrayId;

            recordCall("glEnableVertexArrayAttrib", vaoId, index);
            recordCall("glVertexArrayAttribFormat", vaoId, index, size, type, false, 0);
            recordCall("glVertexArrayVertexBuffer", vaoId, index, buffer.bufferId, offset, stride);
            recordCall("glVertexArrayAttribBinding", vaoId, index, index);
        }

        GL45.glEnableVertexArrayAttrib(vao.vertexArrayId, index);
        GL45.glVertexArrayAttribFormat(vao.vertexArrayId, index, size, type, false, 0);
        GL45.glVertexArrayVertexBuffer(vao.vertexArrayId, index, buffer.bufferId, offset, stride);
        GL45.glVertexArrayAttribBinding(vao.vertexArrayId, index, index);

        if (divisor > 0) {
            if (RECORD_CALLS) {
                recordCall("glVertexArrayBindingDivisor", vao.vertexArrayId, index, divisor);
            }

            GL45.glVertexArrayBindingDivisor(vao.vertexArrayId, index, divisor);
        }
    }

    @Override
    public void vertexArrayAttachIndexBuffer(GL45VertexArray vao, GL45Buffer buffer) {
        if (RECORD_CALLS) {
            recordCall("glVertexArrayElementBuffer", vao.vertexArrayId, buffer.bufferId);
        }

        GL45.glVertexArrayElementBuffer(vao.vertexArrayId, buffer.bufferId);
    }

    @Override
    public GL45VertexArray vertexArrayCreate() {
        if (RECORD_CALLS) {
            recordCall("#vaoId = glCreateVertexArrays");
        }

        final GL45VertexArray vao = new GL45VertexArray();
        vao.vertexArrayId = GL45.glCreateVertexArrays();
        return vao;
    }

    @Override
    public void vertexArrayDelete(GL45VertexArray vao) {
        if (RECORD_CALLS) {
            recordCall("glDeleteVertexArrays", vao.vertexArrayId);
        }

        GL30.glDeleteVertexArrays(vao.vertexArrayId);
        vao.vertexArrayId = -1;
    }

    @Override
    public void vertexArrayDrawArrays(GL45VertexArray vao, int drawMode, int start, int count) {
        state.vertexArrayPush(vao.vertexArrayId);

        if (RECORD_CALLS) {
            recordCall("glDrawArrays", drawMode, start, count);
        }

        GL11.glDrawArrays(drawMode, start, count);

        state.vertexArrayPop();
    }

    @Override
    public void vertexArrayDrawArraysIndirect(GL45VertexArray vao, GL45Buffer cmdBuffer, int drawMode, long offset) {
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
    public void vertexArrayDrawArraysInstanced(GL45VertexArray vao, int drawMode, int first, int count, int instanceCount) {
        state.vertexArrayPush(vao.vertexArrayId);

        if (RECORD_CALLS) {
            recordCall("glDrawArraysInstanced", drawMode, first, count, instanceCount);
        }

        GL31.glDrawArraysInstanced(drawMode, first, count, instanceCount);

        state.vertexArrayPop();
    }

    @Override
    public void vertexArrayDrawElements(GL45VertexArray vao, int drawMode, int count, int type, long offset) {
        state.vertexArrayPush(vao.vertexArrayId);

        if (RECORD_CALLS) {
            recordCall("glDrawElements", drawMode, count, type, offset);
        }

        GL11.glDrawElements(drawMode, count, type, offset);

        state.vertexArrayPop();
    }

    @Override
    public void vertexArrayDrawElementsIndirect(GL45VertexArray vao, GL45Buffer cmdBuffer, int drawMode, int indexType, long offset) {
        state.vertexArrayPush(vao.vertexArrayId);
        state.bufferPush(GL40.GL_DRAW_INDIRECT_BUFFER, cmdBuffer.bufferId);

        if (RECORD_CALLS) {
            recordCall("glDrawElementsIndirect", drawMode, indexType, offset);
        }

        GL40.glDrawElementsIndirect(drawMode, indexType, offset);

        state.bufferPop(GL40.GL_DRAW_INDIRECT_BUFFER);
        state.vertexArrayPop();
    }

    @Override
    public void vertexArrayDrawElementsInstanced(GL45VertexArray vao, int drawMode, int count, int type, long offset, int instanceCount) {
        state.vertexArrayPush(vao.vertexArrayId);

        if (RECORD_CALLS) {
            recordCall("glDrawElementsInstanced", drawMode, count, type, offset, instanceCount);
        }
        
        GL31.glDrawElementsInstanced(drawMode, count, type, offset, instanceCount);

        state.vertexArrayPop();
    }

    @Override
    public void vertexArrayDrawTransformFeedback(GL45VertexArray vao, int drawMode, int start, int count) {
        state.vertexArrayPush(vao.vertexArrayId);

        if (RECORD_CALLS) {
            recordCall("glEnable", "GL_RASTERIZER_DISCARD");
            recordCall("glBeginTransformFeedback", drawMode);
            recordCall("glDrawArrays", drawMode, start, count);
            recordCall("glEndTransformFeedback", drawMode, start, count);
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
    public void vertexArrayMultiDrawArrays(GL45VertexArray vao, int drawMode, IntBuffer first, IntBuffer count) {
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
