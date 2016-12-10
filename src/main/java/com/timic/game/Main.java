package com.timic.game;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static com.timic.engine.util.IOUtil.ioResourceToByteBuffer;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_load_from_memory;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.MemoryUtil.memAllocInt;
import static org.lwjgl.system.MemoryUtil.memFree;

public class Main {

    private long window;

    private void run() {
        System.out.println("Running LWJGL " + Version.getVersion());

        try {
            init();
            loop();

            glfwFreeCallbacks(window);
            glfwDestroyWindow(window);
        } finally {
            glfwTerminate();
            glfwSetErrorCallback(null).free();
        }
    }

    private void init() {
        GLFWErrorCallback.createPrint(System.err).set();

        if(!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW!");

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);

        // TODO: Make them constants
        int WIDTH = 1270;
        int HEIGHT = 768;

        window = glfwCreateWindow(WIDTH, HEIGHT, "gEngine v1.1", NULL, NULL);
        if(window == NULL)
            throw new RuntimeException("Failed to create the GLFW window!");

        IntBuffer w = memAllocInt(1);
        IntBuffer h = memAllocInt(1);
        IntBuffer comp = memAllocInt(1);

        // TODO: Perhaps a... custom cursor?

        ByteBuffer icon16;
        ByteBuffer icon32;
        try {
            icon16 = ioResourceToByteBuffer("icon16.png", 2048);
            icon32 = ioResourceToByteBuffer("icon32.png", 4096);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }

        try(GLFWImage.Buffer icons = GLFWImage.malloc(1)) {
            ByteBuffer pixels16 = stbi_load_from_memory(icon16, w, h, comp, 4);
            icons
                    .position(0)
                    .width(w.get(0))
                    .height(w.get(0))
                    .pixels(pixels16);
            ByteBuffer pixels32 = stbi_load_from_memory(icon32, w, h, comp, 4);
            icons
                    .position(0)
                    .width(w.get(0))
                    .height(h.get(0))
                    .pixels(pixels32);
            icons.position(0);
            glfwSetWindowIcon(window, icons);

            stbi_image_free(pixels32);
            stbi_image_free(pixels16);
        }

        memFree(comp);
        memFree(h);
        memFree(w);

        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if(key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                glfwSetWindowShouldClose(window, true);
        });

        GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());

        glfwSetWindowPos(
                window,
                (vidMode.width()-WIDTH)/2,
                (vidMode.height()-HEIGHT)/2
        );

        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);

        glfwShowWindow(window);
    }

    private void loop() {
        GL.createCapabilities();

        glClearColor(1.0f, 0.0f, 0.0f, 0.0f);

        while(!glfwWindowShouldClose(window)) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }

    public static void main(String[] args) {
        new Main().run();
    }
}
