package cn.origin.cube.guis.mainmenu;

import cn.origin.cube.Cube;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.lwjgl.opengl.GL20.*;

public class GLSLSandboxShader {
    public boolean initialized = false;
    private int programId;
    private int timeUniform;
    private int mouseUniform;
    private int resolutionUniform;

    public GLSLSandboxShader(String name, InputStream is) throws IOException {
        int program = glCreateProgram();

        glAttachShader(program, createShader("assets/fonts/passthrough.vsh", GLSLSandboxShader.class.getResourceAsStream("assets/fonts/passthrough.vsh"), GL_VERTEX_SHADER));
        glAttachShader(program, createShader(name, is, GL_FRAGMENT_SHADER));

        glLinkProgram(program);

        int linked = glGetProgrami(program, GL_LINK_STATUS);

        if (linked == 0) {
            Cube.logger.error(glGetShaderInfoLog(program, glGetProgrami(program, GL_INFO_LOG_LENGTH)));
            return;
        }

        programId = program;

        glUseProgram(program);

        timeUniform = glGetUniformLocation(program, "time");
        mouseUniform = glGetUniformLocation(program, "mouse");
        resolutionUniform = glGetUniformLocation(program, "resolution");

        glUseProgram(0);

        initialized = true;
    }

    public void useShader(int width, int height, float mouseX, float mouseY, float time) {
        glUseProgram(programId);

        glUniform2f(resolutionUniform, width, height);
        glUniform2f(mouseUniform, mouseX / width, 1.0f - mouseY / height);
        glUniform1f(timeUniform, time);
    }

    public int createShader(String check, InputStream inputStream, int shaderType) throws IOException {
        int shader = glCreateShader(shaderType);

        glShaderSource(shader, readStreamToString(inputStream));

        glCompileShader(shader);

        int compiled = glGetShaderi(shader, GL_COMPILE_STATUS);

        if (compiled == 0) {
            Cube.logger.error(glGetShaderInfoLog(shader, glGetShaderi(shader, GL_INFO_LOG_LENGTH)));
            Cube.logger.error("Caused by " + check);
            return 0;
        }

        return shader;
    }

    public String readStreamToString(InputStream inputStream) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        byte[] buffer = new byte[512];

        int read;

        while ((read = inputStream.read(buffer, 0, buffer.length)) != - 1)
            out.write(buffer, 0, read);

        return new String(out.toByteArray(), StandardCharsets.UTF_8);
    }
}
