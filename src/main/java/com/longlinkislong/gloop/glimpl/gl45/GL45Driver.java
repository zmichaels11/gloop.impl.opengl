/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop.glimpl.gl45;

import com.longlinkislong.gloop.glspi.Driver;
import com.longlinkislong.gloop.glspi.Shader;
import com.longlinkislong.gloop.glspi.Tweaks;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.lwjgl.opengl.ARBBindlessTexture;
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
        GL45Buffer, GL45Framebuffer, GL45Renderbuffer, GL45Texture, GL45Shader, GL45Program, GL45Sampler, GL45VertexArray> {    

    private static final boolean EXCLUSIVE_CONTEXT = Boolean.getBoolean("com.longlinkislong.gloop.glimpl.exclusive_context");
    private static final Logger LOGGER = LoggerFactory.getLogger("GL45Driver");

    @Override
    public void bufferBindAtomic(GL45Buffer bt, int index) {
        GL30.glBindBufferBase(GL42.GL_ATOMIC_COUNTER_BUFFER, index, bt.bufferId);
    }

    @Override
    public void bufferBindAtomic(GL45Buffer bt, int index, long offset, long size) {
        GL30.glBindBufferRange(GL42.GL_ATOMIC_COUNTER_BUFFER, index, bt.bufferId, offset, size);
    }

    @Override
    public void bufferBindStorage(GL45Buffer bt, int index) {
        GL30.glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, index, bt.bufferId);
    }

    @Override
    public void bufferBindStorage(GL45Buffer bt, int index, long offset, long size) {
        GL30.glBindBufferRange(GL43.GL_SHADER_STORAGE_BUFFER, index, bt.bufferId, offset, size);
    }

    @Override
    public void bufferBindFeedback(GL45Buffer bt, int index) {
        GL30.glBindBufferBase(GL30.GL_TRANSFORM_FEEDBACK_BUFFER, index, bt.bufferId);
    }

    @Override
    public void bufferBindFeedback(GL45Buffer bt, int index, long offset, long size) {
        GL30.glBindBufferRange(GL30.GL_TRANSFORM_FEEDBACK_BUFFER, index, bt.bufferId, offset, size);
    }

    @Override
    public void bufferBindUniform(GL45Buffer bt, int index) {
        GL30.glBindBufferBase(GL31.GL_UNIFORM_BUFFER, index, bt.bufferId);
    }

    @Override
    public void bufferBindUniform(GL45Buffer bt, int index, long offset, long size) {
        GL30.glBindBufferRange(GL31.GL_UNIFORM_BUFFER, index, bt.bufferId, offset, size);
    }

    @Override
    public int bufferGetMaxUniformBindings() {
        return GL11.glGetInteger(GL31.GL_MAX_UNIFORM_BUFFER_BINDINGS);
    }

    @Override
    public int programGetStorageBlockBinding(GL45Program pt, String storageBlockName) {
        if (pt.storageBindings.containsKey(storageBlockName)) {
            return pt.storageBindings.get(storageBlockName);
        } else {
            return -1;
        }
    }

    @Override
    public int programGetUniformBlockBinding(GL45Program pt, String uniformBlockName) {
        if (pt.uniformBindings.containsKey(uniformBlockName)) {
            return pt.uniformBindings.get(uniformBlockName);
        } else {
            return -1;
        }
    }

    @Override
    public void programSetStorageBlockBinding(GL45Program pt, String uniformBlockName, int binding) {
        final int uBlockIndex = GL31.glGetUniformBlockIndex(pt.programId, uniformBlockName);

        GL43.glShaderStorageBlockBinding(pt.programId, uBlockIndex, binding);
        pt.storageBindings.put(uniformBlockName, binding);
    }

    @Override
    public void programSetUniformBlockBinding(GL45Program pt, String uniformBlockName, int binding) {
        final int sBlockIndex = GL31.glGetUniformBlockIndex(pt.programId, uniformBlockName);

        GL31.glUniformBlockBinding(pt.programId, sBlockIndex, binding);
        pt.uniformBindings.put(uniformBlockName, binding);
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
    public int shaderGetVersion() {
        return 450;
    }

    @Override
    public void applyTweaks(final Tweaks tweaks) {        
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
    public void bufferAllocate(GL45Buffer buffer, long size, int usage) {
        GL45.glNamedBufferData(buffer.bufferId, size, usage);
    }

    @Override
    public void bufferAllocateImmutable(GL45Buffer buffer, long size, int bitflags) {
        GL45.glNamedBufferStorage(buffer.bufferId, size, bitflags);
    }

    @Override
    public void bufferCopyData(GL45Buffer srcBuffer, long srcOffset, GL45Buffer dstBuffer, long dstOffset, long size) {
        GL45.glCopyNamedBufferSubData(srcBuffer.bufferId, dstBuffer.bufferId, srcOffset, dstOffset, size);
    }

    @Override
    public GL45Buffer bufferCreate() {
        final GL45Buffer buffer = new GL45Buffer();

        buffer.bufferId = GL45.glCreateBuffers();

        return buffer;
    }

    @Override
    public void bufferDelete(GL45Buffer buffer) {
        GL15.glDeleteBuffers(buffer.bufferId);

        buffer.bufferId = -1;
    }

    @Override
    public void bufferGetData(GL45Buffer buffer, long offset, ByteBuffer out) {
        GL45.glGetNamedBufferSubData(buffer.bufferId, offset, out);
    }

    @Override
    public int bufferGetMaxUniformBlockSize() {
        return GL11.glGetInteger(GL31.GL_MAX_UNIFORM_BLOCK_SIZE);
    }

    @Override
    public int bufferGetParameterI(GL45Buffer buffer, int paramId) {
        return GL45.glGetNamedBufferParameteri(buffer.bufferId, paramId);
    }

    @Override
    public void bufferInvalidateData(GL45Buffer buffer) {
        GL43.glInvalidateBufferData(buffer.bufferId);
    }

    @Override
    public void bufferInvalidateRange(GL45Buffer buffer, long offset, long length) {
        GL43.glInvalidateBufferSubData(buffer.bufferId, offset, length);
    }

    @Override
    public ByteBuffer bufferMapData(GL45Buffer buffer, long offset, long length, int accessFlags) {
        buffer.mapBuffer = GL45.glMapNamedBufferRange(buffer.bufferId, offset, length, accessFlags, buffer.mapBuffer);

        return buffer.mapBuffer;
    }

    @Override
    public void bufferSetData(GL45Buffer buffer, ByteBuffer data, int usage) {
        GL45.glNamedBufferData(buffer.bufferId, data, usage);
    }

    @Override
    public void bufferUnmapData(GL45Buffer buffer) {
        GL45.glUnmapNamedBuffer(buffer.bufferId);
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
    public void framebufferAddAttachment(GL45Framebuffer framebuffer, int attachmentId, GL45Texture texture, int mipmapLevel) {
        GL45.glNamedFramebufferTexture(
                framebuffer.framebufferId,
                attachmentId,
                texture.textureId,
                mipmapLevel);
    }

    @Override
    public void framebufferAddRenderbuffer(GL45Framebuffer framebuffer, int attachmentId, GL45Renderbuffer renderbuffer) {
        GL45.glNamedFramebufferRenderbuffer(
                framebuffer.framebufferId,
                attachmentId,
                GL30.GL_RENDERBUFFER,
                renderbuffer.renderbufferId);
    }

    @Override
    public void framebufferBind(GL45Framebuffer framebuffer, IntBuffer attachments) {
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebuffer.framebufferId);

        if (attachments != null) {
            GL45.glNamedFramebufferDrawBuffers(framebuffer.framebufferId, attachments);
        }
    }

    @Override
    public void framebufferBlit(GL45Framebuffer srcFb, int srcX0, int srcY0, int srcX1, int srcY1, GL45Framebuffer dstFb, int dstX0, int dstY0, int dstX1, int dstY1, int bitfield, int filter) {
        GL45.glBlitNamedFramebuffer(
                srcFb.framebufferId,
                dstFb.framebufferId,
                srcX0, srcY0, srcX1, srcY1,
                dstX0, dstY0, dstX1, dstY1,
                bitfield, filter);
    }

    @Override
    public GL45Framebuffer framebufferCreate() {
        final int fbId = GL45.glCreateFramebuffers();
        final GL45Framebuffer framebuffer = new GL45Framebuffer();

        framebuffer.framebufferId = fbId;
        return framebuffer;
    }

    @Override
    public void framebufferDelete(GL45Framebuffer framebuffer) {
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
    public void framebufferGetPixels(GL45Framebuffer framebuffer, int x, int y, int width, int height, int format, int type, ByteBuffer dstBuffer) {
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
    public boolean framebufferIsComplete(GL45Framebuffer framebuffer) {
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
    public GL45Program programCreate() {
        final GL45Program program = new GL45Program();

        program.programId = GL20.glCreateProgram();
        return program;
    }

    @Override
    public void programDelete(GL45Program program) {
        GL20.glDeleteProgram(program.programId);

        program.programId = -1;
        program.storageBindings.clear();
        program.uniformBindings.clear();
    }

    @Override
    public void programDispatchCompute(GL45Program program, int numX, int numY, int numZ) {
        if (EXCLUSIVE_CONTEXT) {
            GL20.glUseProgram(program.programId);
            GL43.glDispatchCompute(numX, numY, numZ);
        } else {
            final int currentProg = GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM);
            
            GL20.glUseProgram(program.programId);
            GL43.glDispatchCompute(numX, numY, numZ);
            GL20.glUseProgram(currentProg);
        }
    }

    @Override
    public int programGetUniformLocation(GL45Program program, String name) {
        return GL20.glGetUniformLocation(program.programId, name);
    }

    @Override
    public void programLinkShaders(GL45Program program, Shader[] shaders) {
        for (Shader shader : shaders) {
            final int shaderId = ((GL45Shader) shader).shaderId;

            GL20.glAttachShader(program.programId, shaderId);
        }

        GL20.glLinkProgram(program.programId);

        for (Shader shader : shaders) {
            final int shaderId = ((GL45Shader) shader).shaderId;

            GL20.glDetachShader(program.programId, shaderId);
        }
    }

    @Override
    public void programSetAttribLocation(GL45Program program, int index, String name) {
        GL20.glBindAttribLocation(program.programId, index, name);
    }

    @Override
    public void programSetFeedbackVaryings(GL45Program program, String[] varyings) {
        GL30.glTransformFeedbackVaryings(program.programId, varyings, GL30.GL_SEPARATE_ATTRIBS);
    }

    @Override
    public void programSetUniformD(GL45Program program, int uLoc, double[] value) {
        switch (value.length) {
            case 1:
                GL41.glProgramUniform1d(program.programId, uLoc, value[0]);
                break;
            case 2:
                GL41.glProgramUniform2d(program.programId, uLoc, value[0], value[1]);
                break;
            case 3:
                GL41.glProgramUniform3d(program.programId, uLoc, value[0], value[1], value[2]);
                break;
            case 4:
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
                GL41.glProgramUniform1f(program.programId, uLoc, value[0]);
                break;
            case 2:
                GL41.glProgramUniform2f(program.programId, uLoc, value[0], value[1]);
                break;
            case 3:
                GL41.glProgramUniform3f(program.programId, uLoc, value[0], value[1], value[2]);
                break;
            case 4:
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
                GL41.glProgramUniform1i(program.programId, uLoc, value[0]);
                break;
            case 2:
                GL41.glProgramUniform2i(program.programId, uLoc, value[0], value[1]);
                break;
            case 3:
                GL41.glProgramUniform3i(program.programId, uLoc, value[0], value[1], value[2]);
                break;
            case 4:
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
                GL41.glProgramUniformMatrix2dv(program.programId, uLoc, false, mat);
                break;
            case 9:
                GL41.glProgramUniformMatrix3dv(program.programId, uLoc, false, mat);
                break;
            case 16:
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
                GL41.glProgramUniformMatrix2fv(program.programId, uLoc, false, mat);
                break;
            case 9:
                GL41.glProgramUniformMatrix3fv(program.programId, uLoc, false, mat);
                break;
            case 16:
                GL41.glProgramUniformMatrix4fv(program.programId, uLoc, false, mat);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported matrix size: " + mat.limit());
        }
    }

    @Override
    public void programUse(GL45Program program) {
        GL20.glUseProgram(program.programId);
    }

    @Override
    public GL45Renderbuffer renderbufferCreate(int internalFormat, int width, int height) {
        final GL45Renderbuffer renderbuffer = new GL45Renderbuffer();

        renderbuffer.renderbufferId = GL45.glCreateRenderbuffers();
        GL45.glNamedRenderbufferStorage(renderbuffer.renderbufferId, internalFormat, width, height);

        return renderbuffer;
    }

    @Override
    public void renderbufferDelete(GL45Renderbuffer renderbuffer) {
        GL30.glDeleteRenderbuffers(renderbuffer.renderbufferId);
        renderbuffer.renderbufferId = -1;
    }

    @Override
    public void samplerBind(int unit, GL45Sampler sampler) {
        GL33.glBindSampler(unit, sampler.samplerId);
    }

    @Override
    public GL45Sampler samplerCreate() {
        final GL45Sampler sampler = new GL45Sampler();
        sampler.samplerId = GL45.glCreateSamplers();
        return sampler;
    }

    @Override
    public void samplerDelete(GL45Sampler sampler) {
        GL33.glDeleteSamplers(sampler.samplerId);
        sampler.samplerId = -1;
    }

    @Override
    public void samplerSetParameter(GL45Sampler sampler, int param, int value) {
        GL33.glSamplerParameteri(sampler.samplerId, param, value);
    }

    @Override
    public void samplerSetParameter(GL45Sampler sampler, int param, float value) {
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
    public GL45Shader shaderCompile(int type, String source) {
        final GL45Shader shader = new GL45Shader();

        shader.shaderId = GL20.glCreateShader(type);
        GL20.glShaderSource(shader.shaderId, source);
        GL20.glCompileShader(shader.shaderId);

        return shader;
    }

    @Override
    public void shaderDelete(GL45Shader shader) {
        GL20.glDeleteShader(shader.shaderId);
        shader.shaderId = -1;
    }

    @Override
    public String shaderGetInfoLog(GL45Shader shader) {
        return GL20.glGetShaderInfoLog(shader.shaderId);
    }

    @Override
    public int shaderGetParameterI(GL45Shader shader, int pName) {
        return GL20.glGetShaderi(shader.shaderId, pName);
    }

    @Override
    public GL45Texture textureAllocate(int mipmaps, int internalFormat, int width, int height, int depth, int dataType) {
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

        final GL45Texture texture = new GL45Texture();

        texture.textureId = GL45.glCreateTextures(target);
        texture.target = target;
        texture.internalFormat = internalFormat;

        switch (target) {
            case GL11.GL_TEXTURE_1D:
                GL45.glTextureStorage1D(texture.textureId, mipmaps, internalFormat, width);
                break;
            case GL11.GL_TEXTURE_2D:
                GL45.glTextureStorage2D(texture.textureId, mipmaps, internalFormat, width, height);
                break;
            case GL12.GL_TEXTURE_3D:
                GL45.glTextureStorage3D(texture.textureId, mipmaps, internalFormat, width, height, depth);
                break;
        }

        return texture;
    }
    
    @Override
    public void textureBind(GL45Texture texture, int unit) {
        GL45.glBindTextureUnit(unit, texture.textureId);
    }
   
    @Override
    public long textureMap(GL45Texture texture) {
        if (texture.pHandle != -1) {
            return texture.pHandle;
        }

        if (GL.getCapabilities().GL_ARB_bindless_texture) {
            texture.pHandle = ARBBindlessTexture.glGetTextureHandleARB(texture.textureId);
            ARBBindlessTexture.glMakeTextureHandleResidentARB(texture.pHandle);
            return texture.pHandle;
        } else {
            throw new UnsupportedOperationException("ARB_bindless_texture is not supported!");
        }
    }

    @Override
    public void textureUnmap(GL45Texture texture) {
        if (texture.pHandle == -1) {
            return;
        }

        if (GL.getCapabilities().GL_ARB_bindless_texture) {
            ARBBindlessTexture.glMakeTextureHandleNonResidentARB(texture.pHandle);
            texture.pHandle = -1;
        } else {
            throw new UnsupportedOperationException("ARB_bindless_texture is not supported!");
        }
    }

    @Override
    public void textureDelete(GL45Texture texture) {
        GL11.glDeleteTextures(texture.textureId);
        texture.textureId = -1;
        texture.target = -1;
    }

    @Override
    public void textureGenerateMipmap(GL45Texture texture) {
        GL45.glGenerateTextureMipmap(texture.textureId);
    }

    @Override
    public void textureGetData(GL45Texture texture, int level, int format, int type, ByteBuffer out) {
        GL45.glGetTextureImage(texture.target, level, format, type, out);
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
    public void textureInvalidateData(GL45Texture texture, int level) {
        GL43.glInvalidateTexImage(texture.textureId, level);
    }

    @Override
    public void textureInvalidateRange(GL45Texture texture, int level, int xOffset, int yOffset, int zOffset, int width, int height, int depth) {
        GL43.glInvalidateTexSubImage(texture.textureId, level, xOffset, yOffset, zOffset, width, height, depth);
    }

    @Override
    public void textureSetData(GL45Texture texture, int level, int xOffset, int yOffset, int zOffset, int width, int height, int depth, int format, int type, ByteBuffer data) {
        switch (texture.target) {
            case GL11.GL_TEXTURE_1D:
                GL45.glTextureSubImage1D(texture.textureId, level, xOffset, width, format, type, data);
                break;
            case GL11.GL_TEXTURE_2D:
                GL45.glTextureSubImage2D(texture.textureId, level, xOffset, yOffset, width, height, format, type, data);
                break;
            case GL12.GL_TEXTURE_3D:
                GL45.glTextureSubImage3D(texture.textureId, level, xOffset, yOffset, zOffset, width, height, depth, format, type, data);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported texture type: " + texture.target);
        }
    }

    @Override
    public void textureSetParameter(GL45Texture texture, int param, int value) {
        GL45.glTextureParameteri(texture.textureId, param, value);
    }

    @Override
    public void textureSetParameter(GL45Texture texture, int param, float value) {
        GL45.glTextureParameterf(texture.textureId, param, value);
    }

    @Override
    public void vertexArrayAttachBuffer(GL45VertexArray vao, int index, GL45Buffer buffer, int size, int type, int stride, long offset, int divisor) {
        if (stride == 0) {
            switch (type) {
                case GL11.GL_DOUBLE:
                case ARBBindlessTexture.GL_UNSIGNED_INT64_ARB:
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

        final boolean isFloatingPoint;
        final boolean is64Bit;

        switch (type) {
            case GL11.GL_DOUBLE:
                isFloatingPoint = true;
                is64Bit = true;
                break;
            case GL11.GL_FLOAT:
                isFloatingPoint = true;
                is64Bit = false;
                break;
            case ARBBindlessTexture.GL_UNSIGNED_INT64_ARB:
                isFloatingPoint = false;
                is64Bit = true;
                break;
            default:
                isFloatingPoint = false;
                is64Bit = false;
                break;
        }

        GL45.glEnableVertexArrayAttrib(vao.vertexArrayId, index);

        if (is64Bit) {
            // ARB_bindless_texture states that this accepts EITHER DOUBLE or UNSIGNED_INT64_ARB
            GL45.glVertexArrayAttribLFormat(vao.vertexArrayId, index, size, type, 0);
        } else {
            if (isFloatingPoint) {
                // OpenGL spec 4.5 states that this ONLY accepts floating point types. DOUBLE is converted to FLOAT here
                GL45.glVertexArrayAttribFormat(vao.vertexArrayId, index, size, type, false, 0);
            } else {
                GL45.glVertexArrayAttribIFormat(vao.vertexArrayId, index, size, type, 0);
            }
        }

        GL45.glVertexArrayVertexBuffer(vao.vertexArrayId, index, buffer.bufferId, offset, stride);
        GL45.glVertexArrayAttribBinding(vao.vertexArrayId, index, index);

        if (divisor > 0) {
            GL45.glVertexArrayBindingDivisor(vao.vertexArrayId, index, divisor);
        }
    }

    @Override
    public void vertexArrayAttachIndexBuffer(GL45VertexArray vao, GL45Buffer buffer) {
        GL45.glVertexArrayElementBuffer(vao.vertexArrayId, buffer.bufferId);
    }

    @Override
    public GL45VertexArray vertexArrayCreate() {
        final GL45VertexArray vao = new GL45VertexArray();
        vao.vertexArrayId = GL45.glCreateVertexArrays();
        return vao;
    }

    @Override
    public void vertexArrayDelete(GL45VertexArray vao) {
        GL30.glDeleteVertexArrays(vao.vertexArrayId);
        vao.vertexArrayId = -1;
    }

    @Override
    public void vertexArrayDrawArrays(GL45VertexArray vao, int drawMode, int start, int count) {
        if (EXCLUSIVE_CONTEXT) {
            GL30.glBindVertexArray(vao.vertexArrayId);
            GL11.glDrawArrays(drawMode, start, count);
        } else {
            final int currentVao = GL11.glGetInteger(GL30.GL_VERTEX_ARRAY_BINDING);
            
            GL30.glBindVertexArray(vao.vertexArrayId);
            GL11.glDrawArrays(drawMode, start, count);
            GL30.glBindVertexArray(currentVao);
        }
    }

    @Override
    public void vertexArrayDrawArraysIndirect(GL45VertexArray vao, GL45Buffer cmdBuffer, int drawMode, long offset) {
        if (EXCLUSIVE_CONTEXT) {
            GL30.glBindVertexArray(vao.vertexArrayId);
            GL15.glBindBuffer(GL40.GL_DRAW_INDIRECT_BUFFER, cmdBuffer.bufferId);
            GL40.glDrawArraysIndirect(drawMode, offset);
            GL15.glBindBuffer(GL40.GL_DRAW_INDIRECT_BUFFER, 0);
        } else {
            final int currentVao = GL11.glGetInteger(GL30.GL_VERTEX_ARRAY_BINDING);
            
            GL30.glBindVertexArray(vao.vertexArrayId);
            GL15.glBindBuffer(GL40.GL_DRAW_INDIRECT_BUFFER, cmdBuffer.bufferId);
            GL40.glDrawArraysIndirect(drawMode, offset);
            GL15.glBindBuffer(GL40.GL_DRAW_INDIRECT_BUFFER, 0);
            GL30.glBindVertexArray(currentVao);
        }
    }

    @Override
    public void vertexArrayDrawArraysInstanced(GL45VertexArray vao, int drawMode, int first, int count, int instanceCount) {
        if (EXCLUSIVE_CONTEXT) {
            GL30.glBindVertexArray(vao.vertexArrayId);
            GL31.glDrawArraysInstanced(drawMode, first, count, instanceCount);
        } else {
            final int currentVao = GL11.glGetInteger(GL30.GL_VERTEX_ARRAY_BINDING);
            
            GL30.glBindVertexArray(vao.vertexArrayId);
            GL31.glDrawArraysInstanced(drawMode, first, count, instanceCount);
            GL30.glBindVertexArray(currentVao);
        }
    }

    @Override
    public void vertexArrayDrawElements(GL45VertexArray vao, int drawMode, int count, int type, long offset) {
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
    public void vertexArrayDrawElementsIndirect(GL45VertexArray vao, GL45Buffer cmdBuffer, int drawMode, int indexType, long offset) {
        if (EXCLUSIVE_CONTEXT) {
            GL30.glBindVertexArray(vao.vertexArrayId);
            GL15.glBindBuffer(GL40.GL_DRAW_INDIRECT_BUFFER, cmdBuffer.bufferId);
            GL40.glDrawElementsIndirect(drawMode, indexType, offset);
            GL15.glBindBuffer(GL40.GL_DRAW_INDIRECT_BUFFER, 0);
        } else {
            final int currentVao = GL11.glGetInteger(GL30.GL_VERTEX_ARRAY_BINDING);
            
            GL30.glBindVertexArray(vao.vertexArrayId);
            GL15.glBindBuffer(GL40.GL_DRAW_INDIRECT_BUFFER, cmdBuffer.bufferId);
            GL40.glDrawElementsIndirect(drawMode, indexType, offset);
            GL15.glBindBuffer(GL40.GL_DRAW_INDIRECT_BUFFER, 0);
            GL30.glBindVertexArray(currentVao);
        }
    }

    @Override
    public void vertexArrayDrawElementsInstanced(GL45VertexArray vao, int drawMode, int count, int type, long offset, int instanceCount) {
        if (EXCLUSIVE_CONTEXT) {
            GL30.glBindVertexArray(vao.vertexArrayId);
            GL31.glDrawElementsInstanced(drawMode, count, type, offset, instanceCount);
        } else {
            final int currentVao = GL11.glGetInteger(GL30.GL_VERTEX_ARRAY_BINDING);
            
            GL30.glBindVertexArray(vao.vertexArrayId);
            GL31.glDrawElementsInstanced(drawMode, count, type, offset, instanceCount);
            GL30.glBindVertexArray(currentVao);
        }
    }

    @Override
    public void viewportApply(int x, int y, int width, int height) {
        GL11.glViewport(x, y, width, height);
    }

    @Override
    public void textureGetData(GL45Texture texture, int level, int format, int type, GL45Buffer out, long offset, int size) {
        GL15.glBindBuffer(GL21.GL_PIXEL_PACK_BUFFER, out.bufferId);
        GL45.glGetTextureImage(texture.textureId, level, format, type, size, offset);
        GL15.glBindBuffer(GL21.GL_PIXEL_PACK_BUFFER, 0);
    }

    @Override
    public void textureSetData(GL45Texture texture, int level, int xOffset, int yOffset, int zOffset, int width, int height, int depth, int format, int type, GL45Buffer buffer, long offset) {
        switch (texture.target) {
            case GL11.GL_TEXTURE_1D:
                GL15.glBindBuffer(GL21.GL_PIXEL_UNPACK_BUFFER, buffer.bufferId);
                GL45.glTextureSubImage1D(texture.textureId, level, xOffset, width, format, type, 0L);
                GL15.glBindBuffer(GL21.GL_PIXEL_UNPACK_BUFFER, 0);
                break;
            case GL11.GL_TEXTURE_2D:
                GL15.glBindBuffer(GL21.GL_PIXEL_UNPACK_BUFFER, buffer.bufferId);
                GL45.glTextureSubImage2D(texture.textureId, level, xOffset, yOffset, width, height, format, type, 0L);
                GL15.glBindBuffer(GL21.GL_PIXEL_UNPACK_BUFFER, 0);
                break;
            case GL12.GL_TEXTURE_3D:
                GL15.glBindBuffer(GL21.GL_PIXEL_UNPACK_BUFFER, buffer.bufferId);
                GL45.glTextureSubImage3D(texture.textureId, level, xOffset, yOffset, zOffset, width, height, depth, format, type, 0L);
                GL15.glBindBuffer(GL21.GL_PIXEL_UNPACK_BUFFER, 0);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported texture target: " + texture.target);
        }
    }
}
