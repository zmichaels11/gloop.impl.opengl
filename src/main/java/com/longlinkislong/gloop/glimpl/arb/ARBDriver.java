/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop.glimpl.arb;

import com.longlinkislong.gloop.glspi.Driver;
import com.longlinkislong.gloop.glspi.Shader;
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
final class ARBDriver implements Driver<
        ARBBuffer, ARBFramebuffer, ARBRenderbuffer, ARBTexture, ARBShader, ARBProgram, ARBSampler, ARBVertexArray, ARBDrawQuery> {

    @Override
    public void bufferBindStorage(ARBBuffer bt, int binding) {
        final GLCapabilities cap = GL.getCapabilities();

        if(!cap.OpenGL40) {
            throw new UnsupportedOperationException("OpenGL 4.0 is not supported!");
        } else if(cap.GL_ARB_shader_storage_buffer_object) {
            throw new UnsupportedOperationException("ARB_shader_storage_buffer_object is not supported!");
        }
    }

    @Override
    public void bufferBindStorage(ARBBuffer bt, int i, long l, long l1) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void bufferBindUniform(ARBBuffer bt, int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void bufferBindUniform(ARBBuffer bt, int i, long l, long l1) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int programGetStorageBlockBinding(ARBProgram pt, String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int programGetUniformBlockBinding(ARBProgram pt, String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void programSetStorageBlockBinding(ARBProgram pt, String string, int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void programSetUniformBlockBinding(ARBProgram pt, String string, int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int shaderGetVersion() {
        final GLCapabilities caps = GL.getCapabilities();
                
        if(caps.OpenGL45) {
            return 450;
        } else if(caps.OpenGL44) {
            return 440;
        } else if(caps.OpenGL43) {
            return 430;
        } else if(caps.OpenGL42) {
            return 420;
        } else if(caps.OpenGL41) {
            return 410;
        } else if(caps.OpenGL40) {
            return 400;
        } else if(caps.OpenGL33) {
            return 330;
        } else if(caps.OpenGL32) {
            return 150;
        } else if(caps.OpenGL31) {
            return 140;
        } else if(caps.OpenGL30) {
            return 130;
        } else if(caps.OpenGL21) {
            return 120;
        } else if(caps.OpenGL20) {
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
    public void bufferAllocate(ARBBuffer buffer, long size, int usage) {
        ARBDirectStateAccess.glNamedBufferData(buffer.bufferId, size, usage);
    }

    @Override
    public void bufferAllocateImmutable(ARBBuffer buffer, long size, int bitflags) {
        ARBDirectStateAccess.glNamedBufferStorage(buffer.bufferId, size, bitflags);
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
        ARBDirectStateAccess.glGetNamedBufferSubData(buffer.bufferId, offset, out);
    }

    @Override
    public int bufferGetParameterI(ARBBuffer buffer, int paramId) {
        return ARBDirectStateAccess.glGetNamedBufferParameteri(buffer.bufferId, paramId);
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
    public ByteBuffer bufferMapData(ARBBuffer buffer, long offset, long length, int accessFlags) {
        buffer.mapBuffer = ARBDirectStateAccess.glMapNamedBufferRange(buffer.bufferId, offset, length, accessFlags, buffer.mapBuffer);
        return buffer.mapBuffer;
    }

    @Override
    public void bufferSetData(ARBBuffer buffer, ByteBuffer data, int usage) {
        ARBDirectStateAccess.glNamedBufferData(buffer.bufferId, data, usage);
    }

    @Override
    public void bufferUnmapData(ARBBuffer buffer) {
        ARBDirectStateAccess.glUnmapNamedBuffer(buffer.bufferId);
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
    public void drawQueryBeginConditionalRender(ARBDrawQuery query, int mode) {
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
    public void drawQueryDisable(int condition) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void drawQueryEnable(int condition, ARBDrawQuery query) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void drawQueryEndConditionRender() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void framebufferAddAttachment(ARBFramebuffer framebuffer, int attachmentId, ARBTexture texture, int mipmapLevel) {
        ARBDirectStateAccess.glNamedFramebufferTexture(
                framebuffer.framebufferId,
                attachmentId,
                texture.textureId,
                mipmapLevel);
    }    

    @Override
    public void framebufferAddRenderbuffer(ARBFramebuffer framebuffer, int attachmentId, ARBRenderbuffer renderbuffer) {
        ARBDirectStateAccess.glNamedFramebufferRenderbuffer(
                framebuffer.framebufferId,
                attachmentId, 
                GL30.GL_RENDERBUFFER, 
                renderbuffer.renderbufferId);
    }

    @Override
    public void framebufferBind(ARBFramebuffer framebuffer, IntBuffer attachments) {
        ARBFramebufferObject.glBindFramebuffer(ARBFramebufferObject.GL_FRAMEBUFFER, framebuffer.framebufferId);

        if (attachments != null) {
            ARBDirectStateAccess.glNamedFramebufferDrawBuffers(framebuffer.framebufferId, attachments);
        }
    }

    @Override
    public void framebufferBlit(
            ARBFramebuffer srcFb, int srcX0, int srcY0, int srcX1, int srcY1,
            ARBFramebuffer dstFb, int dstX0, int dstY0, int dstX1, int dstY1,
            int bitfield, int filter) {

        ARBDirectStateAccess.glBlitNamedFramebuffer(srcFb.framebufferId,
                dstFb.framebufferId,
                srcX0, srcY0, srcX1, srcY1,
                dstX0, dstY0, dstX1, dstY1,
                bitfield, filter);
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
    public void framebufferGetPixels(
            ARBFramebuffer framebuffer,
            int x, int y, int width, int height,
            int format, int type, ARBBuffer dstBuffer) {

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
    public void framebufferGetPixels(
            ARBFramebuffer framebuffer,
            int x, int y, int width, int height,
            int format, int type, ByteBuffer dstBuffer) {

        final int currentFB = GL11.glGetInteger(ARBFramebufferObject.GL_FRAMEBUFFER_BINDING);

        ARBFramebufferObject.glBindFramebuffer(ARBFramebufferObject.GL_FRAMEBUFFER, framebuffer.framebufferId);

        GL11.glReadPixels(
                x, y, width, height,
                format, type,
                dstBuffer);

        ARBFramebufferObject.glBindFramebuffer(ARBFramebufferObject.GL_FRAMEBUFFER, currentFB);
    }

    @Override
    public boolean framebufferIsComplete(ARBFramebuffer framebuffer) {
        return ARBDirectStateAccess.glCheckNamedFramebufferStatus(framebuffer.framebufferId, ARBFramebufferObject.GL_FRAMEBUFFER) == ARBFramebufferObject.GL_FRAMEBUFFER_COMPLETE;
    }

    @Override
    public void maskApply(boolean red, boolean green, boolean blue, boolean alpha, boolean depth, int stencil) {
        GL11.glColorMask(red, green, blue, alpha);
        GL11.glDepthMask(depth);
        GL11.glStencilMask(stencil);
    }

    @Override
    public void polygonSetParameters(
            float pointSize, float lineWidth,
            int frontFace, int cullFace, int polygonMode,
            float offsetFactor, float offsetUnits) {

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
    public void programDispatchCompute(ARBProgram program, int numX, int numY, int numZ) {

        final int currentProgram = GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM);

        GL20.glUseProgram(program.programId);
        ARBComputeShader.glDispatchCompute(numX, numY, numZ);
        GL20.glUseProgram(currentProgram);
    }

    @Override
    public int programGetUniformLocation(ARBProgram program, String name) {
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
    public void programSetAttribLocation(ARBProgram program, int index, String name) {
        GL20.glBindAttribLocation(program.programId, index, name);
    }

    @Override
    public void programSetFeedbackBuffer(ARBProgram program, int varyingLoc, ARBBuffer buffer) {
        ARBUniformBufferObject.glBindBufferBase(GL30.GL_TRANSFORM_FEEDBACK_BUFFER, varyingLoc, buffer.bufferId);
    }

    @Override
    public void programSetFeedbackVaryings(ARBProgram program, String[] varyings) {
        GL30.glTransformFeedbackVaryings(program.programId, varyings, GL30.GL_SEPARATE_ATTRIBS);
    }

    @Override
    public void programSetStorage(ARBProgram program, String storageName, ARBBuffer buffer, int bindingPoint) {
        final int sBlock = ARBProgramInterfaceQuery.glGetProgramResourceLocation(program.programId, ARBProgramInterfaceQuery.GL_SHADER_STORAGE_BLOCK, storageName);

        GL30.glBindBufferBase(ARBShaderStorageBufferObject.GL_SHADER_STORAGE_BUFFER, bindingPoint, buffer.bufferId);
        ARBShaderStorageBufferObject.glShaderStorageBlockBinding(program.programId, sBlock, bindingPoint);
    }

    @Override
    public void programSetUniformBlock(ARBProgram program, String uniformName, ARBBuffer buffer, int bindingPoint) {
        final int uBlock = ARBUniformBufferObject.glGetUniformBlockIndex(program.programId, uniformName);

        ARBUniformBufferObject.glBindBufferBase(ARBUniformBufferObject.GL_UNIFORM_BUFFER, bindingPoint, buffer.bufferId);
        ARBUniformBufferObject.glUniformBlockBinding(program.programId, uBlock, bindingPoint);
    }

    @Override
    public void programSetUniformD(ARBProgram program, int uLoc, double[] value) {
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
                    throw new UnsupportedOperationException("Unsupported uniform vector size: " + value.length);
            }
        } else {
            final int currentProgram = GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM);

            switch (value.length) {
                case 1:
                    GL20.glUseProgram(program.programId);
                    ARBGPUShaderFP64.glUniform1d(uLoc, value[0]);
                    GL20.glUseProgram(currentProgram);
                    break;
                case 2:
                    GL20.glUseProgram(program.programId);
                    ARBGPUShaderFP64.glUniform2d(uLoc, value[0], value[1]);
                    GL20.glUseProgram(currentProgram);
                    break;
                case 3:
                    GL20.glUseProgram(program.programId);
                    ARBGPUShaderFP64.glUniform3d(uLoc, value[0], value[1], value[2]);
                    GL20.glUseProgram(currentProgram);
                    break;
                case 4:
                    GL20.glUseProgram(program.programId);
                    ARBGPUShaderFP64.glUniform4d(uLoc, value[0], value[1], value[2], value[3]);
                    GL20.glUseProgram(currentProgram);
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported uniform vector size: " + value.length);
            }
        }
    }

    @Override
    public void programSetUniformF(ARBProgram program, int uLoc, float[] value) {
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
                default:
                    throw new UnsupportedOperationException("Unsupported uniform vector size: " + value.length);
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
    public void programSetUniformI(ARBProgram program, int uLoc, int[] value) {
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
    public void programSetUniformMatD(ARBProgram program, int uLoc, DoubleBuffer mat) {
        final GLCapabilities cap = GL.getCapabilities();

        if (!(cap.GL_ARB_gpu_shader_fp64 && cap.GL_ARB_gpu_shader_int64)) {
            throw new UnsupportedOperationException("64bit uniforms is not supported!");
        }

        if (cap.GL_ARB_separate_shader_objects) {
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
                    ARBGPUShaderFP64.glUniformMatrix2dv(uLoc, false, mat);
                    GL20.glUseProgram(currentProgram);
                    break;
                case 9:
                    GL20.glUseProgram(program.programId);
                    ARBGPUShaderFP64.glUniformMatrix3dv(uLoc, false, mat);
                    GL20.glUseProgram(currentProgram);
                    break;
                case 16:
                    GL20.glUseProgram(program.programId);
                    ARBGPUShaderFP64.glUniformMatrix4dv(uLoc, false, mat);
                    GL20.glUseProgram(currentProgram);
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported matrix size: " + mat.limit());
            }
        }
    }

    @Override
    public void programSetUniformMatF(ARBProgram program, int uLoc, FloatBuffer mat) {
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
    public void programUse(ARBProgram program) {
        GL20.glUseProgram(program.programId);
    }

    @Override
    public ARBRenderbuffer renderbufferCreate(int internalFormat, int width, int height) {
        final ARBRenderbuffer renderbuffer = new ARBRenderbuffer();
        
        renderbuffer.renderbufferId = ARBDirectStateAccess.glCreateRenderbuffers();
        ARBDirectStateAccess.glNamedRenderbufferStorage(renderbuffer.renderbufferId, internalFormat, width, height);
        
        return renderbuffer;
    }

    @Override
    public void renderbufferDelete(ARBRenderbuffer renderbuffer) {
        GL30.glDeleteRenderbuffers(renderbuffer.renderbufferId);
        renderbuffer.renderbufferId = -1;        
    }

    @Override
    public void samplerBind(int unit, ARBSampler sampler) {
        ARBSamplerObjects.glBindSampler(unit, sampler.samplerId);
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
    public void samplerSetParameter(ARBSampler sampler, int param, int value) {
        ARBSamplerObjects.glSamplerParameteri(sampler.samplerId, param, value);
    }

    @Override
    public void samplerSetParameter(ARBSampler sampler, int param, float value) {
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
    public ARBShader shaderCompile(int type, String source) {
        final ARBShader shader = new ARBShader();

        shader.shaderId = GL20.glCreateShader(type);
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
    public int shaderGetParameterI(ARBShader shader, int pName) {
        return GL20.glGetShaderi(shader.shaderId, pName);
    }

    @Override
    public ARBTexture textureAllocate(int mipmaps, int internalFormat, int width, int height, int depth) {
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
        texture.internalFormat = internalFormat;

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
        }

        return texture;
    }

    @Override
    public void textureAllocatePage(
            ARBTexture texture, int level, int xOffset, int yOffset, int zOffset, int width, int height, int depth) {
        ARBSparseTexture.glTexPageCommitmentARB(
                texture.textureId, level,
                xOffset, yOffset, zOffset,
                width, height, depth,
                true);
    }

    @Override
    public void textureBind(ARBTexture texture, int unit) {
        ARBDirectStateAccess.glBindTextureUnit(unit, texture.textureId);
    }

    @Override
    public void textureDeallocatePage(
            ARBTexture texture, int level,
            int xOffset, int yOffset, int zOffset,
            int width, int height, int depth) {

        ARBSparseTexture.glTexPageCommitmentARB(
                texture.textureId, level,
                xOffset, yOffset, zOffset,
                width, height, depth,
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
    public void textureGetData(ARBTexture texture, int level, int format, int type, ByteBuffer out) {
        ARBDirectStateAccess.glGetTextureImage(texture.target, level, format, type, out);
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
    public int textureGetPageDepth(ARBTexture texture) {
        return ARBInternalformatQuery.glGetInternalformati(texture.target, texture.internalFormat, ARBSparseTexture.GL_VIRTUAL_PAGE_SIZE_Z_ARB);
    }

    @Override
    public int textureGetPageHeight(ARBTexture texture) {
        return ARBInternalformatQuery.glGetInternalformati(texture.target, texture.internalFormat, ARBSparseTexture.GL_VIRTUAL_PAGE_SIZE_Y_ARB);
    }

    @Override
    public int textureGetPageWidth(ARBTexture texture) {
        return ARBInternalformatQuery.glGetInternalformati(texture.target, texture.internalFormat, ARBSparseTexture.GL_VIRTUAL_PAGE_SIZE_X_ARB);
    }

    @Override
    public int textureGetPreferredFormat(int internalFormat) {
        return GL11.GL_RGBA;
    }

    @Override
    public void textureInvalidateData(ARBTexture texture, int level) {
        ARBInvalidateSubdata.glInvalidateTexImage(texture.textureId, level);
    }

    @Override
    public void textureInvalidateRange(
            ARBTexture texture, int level,
            int xOffset, int yOffset, int zOffset, int width, int height, int depth) {

        ARBInvalidateSubdata.glInvalidateTexSubImage(texture.textureId, level, xOffset, yOffset, zOffset, width, height, depth);
    }

    @Override
    public void textureSetData(
            ARBTexture texture,
            int level, int xOffset, int yOffset, int zOffset,
            int width, int height, int depth,
            int format, int type, ByteBuffer data) {

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
                throw new UnsupportedOperationException("Unsupported texture type: " + texture.target);
        }
    }

    @Override
    public void textureSetParameter(ARBTexture texture, int param, int value) {
        ARBDirectStateAccess.glTextureParameteri(texture.textureId, param, value);
    }

    @Override
    public void textureSetParameter(ARBTexture texture, int param, float value) {
        ARBDirectStateAccess.glTextureParameterf(texture.textureId, param, value);
    }

    @Override
    public void vertexArrayAttachBuffer(
            ARBVertexArray vao, int index,
            ARBBuffer buffer, int size, int type,
            int stride, long offset, int divisor) {

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

        ARBDirectStateAccess.glEnableVertexArrayAttrib(vao.vertexArrayId, index);
        ARBDirectStateAccess.glVertexArrayAttribFormat(vao.vertexArrayId, index, size, type, false, 0);
        ARBDirectStateAccess.glVertexArrayVertexBuffer(vao.vertexArrayId, index, buffer.bufferId, offset, stride);
        ARBDirectStateAccess.glVertexArrayAttribBinding(vao.vertexArrayId, index, index);

        if (divisor > 0) {
            ARBDirectStateAccess.glVertexArrayBindingDivisor(vao.vertexArrayId, index, divisor);
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
    public void vertexArrayDrawArrays(ARBVertexArray vao, int drawMode, int start, int count) {
        final int currentVao = GL11.glGetInteger(ARBVertexArrayObject.GL_VERTEX_ARRAY_BINDING);
        ARBVertexArrayObject.glBindVertexArray(vao.vertexArrayId);
        GL11.glDrawArrays(drawMode, start, count);
        ARBVertexArrayObject.glBindVertexArray(currentVao);
    }

    @Override
    public void vertexArrayDrawArraysIndirect(ARBVertexArray vao, ARBBuffer cmdBuffer, int drawMode, long offset) {
        final int currentVao = GL11.glGetInteger(ARBVertexArrayObject.GL_VERTEX_ARRAY_BINDING);
        final int currentIndirect = GL11.glGetInteger(ARBDrawIndirect.GL_DRAW_INDIRECT_BUFFER_BINDING);

        ARBVertexArrayObject.glBindVertexArray(vao.vertexArrayId);
        GL15.glBindBuffer(ARBDrawIndirect.GL_DRAW_INDIRECT_BUFFER, cmdBuffer.bufferId);
        ARBDrawIndirect.glDrawArraysIndirect(drawMode, offset);
        GL15.glBindBuffer(ARBDrawIndirect.GL_DRAW_INDIRECT_BUFFER, currentIndirect);
        ARBVertexArrayObject.glBindVertexArray(currentVao);
    }

    @Override
    public void vertexArrayDrawArraysInstanced(ARBVertexArray vao, int drawMode, int first, int count, int instanceCount) {
        final int currentVao = GL11.glGetInteger(ARBVertexArrayObject.GL_VERTEX_ARRAY_BINDING);

        ARBVertexArrayObject.glBindVertexArray(vao.vertexArrayId);
        GL31.glDrawArraysInstanced(drawMode, first, count, instanceCount);
        ARBVertexArrayObject.glBindVertexArray(currentVao);
    }

    @Override
    public void vertexArrayDrawElements(ARBVertexArray vao, int drawMode, int count, int type, long offset) {
        final int currentVao = GL11.glGetInteger(ARBVertexArrayObject.GL_VERTEX_ARRAY_BINDING);

        ARBVertexArrayObject.glBindVertexArray(vao.vertexArrayId);
        GL11.glDrawElements(drawMode, count, type, offset);
        ARBVertexArrayObject.glBindVertexArray(currentVao);
    }

    @Override
    public void vertexArrayDrawElementsIndirect(ARBVertexArray vao, ARBBuffer cmdBuffer, int drawMode, int indexType, long offset) {
        final int currentVao = GL11.glGetInteger(ARBVertexArrayObject.GL_VERTEX_ARRAY_BINDING);
        final int currentIndirect = GL11.glGetInteger(ARBDrawIndirect.GL_DRAW_INDIRECT_BUFFER_BINDING);

        ARBVertexArrayObject.glBindVertexArray(vao.vertexArrayId);
        GL15.glBindBuffer(ARBDrawIndirect.GL_DRAW_INDIRECT_BUFFER, cmdBuffer.bufferId);
        ARBDrawIndirect.glDrawElementsIndirect(drawMode, indexType, offset);
        GL15.glBindBuffer(ARBDrawIndirect.GL_DRAW_INDIRECT_BUFFER, currentIndirect);
        ARBVertexArrayObject.glBindVertexArray(currentVao);
    }

    @Override
    public void vertexArrayDrawElementsInstanced(ARBVertexArray vao, int drawMode, int count, int type, long offset, int instanceCount) {
        final int currentVao = GL11.glGetInteger(ARBVertexArrayObject.GL_VERTEX_ARRAY_BINDING);

        ARBVertexArrayObject.glBindVertexArray(vao.vertexArrayId);
        GL31.glDrawElementsInstanced(drawMode, count, type, offset, instanceCount);
        ARBVertexArrayObject.glBindVertexArray(currentVao);
    }

    @Override
    public void vertexArrayDrawTransformFeedback(ARBVertexArray vao, int drawMode, int start, int count) {
        final int currentVao = GL11.glGetInteger(ARBVertexArrayObject.GL_VERTEX_ARRAY_BINDING);

        ARBVertexArrayObject.glBindVertexArray(vao.vertexArrayId);
        GL11.glEnable(GL30.GL_RASTERIZER_DISCARD);
        GL30.glBeginTransformFeedback(drawMode);
        GL11.glDrawArrays(drawMode, start, count);
        GL30.glEndTransformFeedback();
        GL11.glDisable(GL30.GL_RASTERIZER_DISCARD);
        ARBVertexArrayObject.glBindVertexArray(currentVao);
    }

    @Override
    public void vertexArrayMultiDrawArrays(ARBVertexArray vao, int drawMode, IntBuffer first, IntBuffer count) {
        final int currentVao = GL11.glGetInteger(ARBVertexArrayObject.GL_VERTEX_ARRAY_BINDING);

        ARBVertexArrayObject.glBindVertexArray(vao.vertexArrayId);
        GL14.glMultiDrawArrays(drawMode, first, count);
        ARBVertexArrayObject.glBindVertexArray(currentVao);
    }

    @Override
    public void viewportApply(int x, int y, int width, int height) {
        GL11.glViewport(x, y, width, height);
    }

}
