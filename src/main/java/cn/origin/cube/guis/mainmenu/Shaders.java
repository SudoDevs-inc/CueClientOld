package cn.origin.cube.guis.mainmenu;

import cn.origin.cube.Cube;
import cn.origin.cube.module.modules.client.MainMenuShader;

import java.io.InputStream;

public class Shaders {
    static String randomname = null;
    static InputStream fis = null;
    public GLSLSandboxShader currentshader;
    public long time;

    public static void clear() {
        randomname = null;

        fis = null;
    }

    public void init() {
        try {
            Object[] shader = getShader();
            clear();
            if (shader == null) {
                currentshader = null;
                Cube.logger.info("MainMenuShaders Disabled.");
            } else {
                String name = (String) shader[0];
                InputStream is = (InputStream) shader[1];

                currentshader = new GLSLSandboxShader(name, is);
                if (! currentshader.initialized)
                    currentshader = null;
                else
                    time = System.currentTimeMillis();
            }
        } catch (Exception e) {
            e.printStackTrace();
            currentshader = null;
        }
    }

    public Object[] getShader() {
        if (!MainMenuShader.INSTANCE.isEnabled()) {
            return null;
        }
        randomname = String.valueOf(GLSLSandboxShader.class.getResource("assets/fonts/flow.frag"));
        fis = GLSLSandboxShader.class.getResourceAsStream("assets/fonts/flow.frag");
        return new Object[]{randomname, fis};
    }
}
