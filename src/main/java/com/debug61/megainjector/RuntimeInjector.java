package com.debug61.megainjector;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.net.URL;
import java.net.URLClassLoader;

public class RuntimeInjector
{

    private static Instrumentation instrumentation;

    private static final String mainClassName = "net.pandaew.reconnect.VariableProviderReconnect";

    public static void premain(String agentArgs, Instrumentation inst) {
        instrumentation = inst;
    }

    public static void agentmain(String agentArgs, Instrumentation inst) {
        System.out.println("[MegaInjector] Агент подключен к работающему процессу");
        instrumentation = inst;

        try {
            loadReconnectModule(agentArgs);
        } catch (Exception e) {
            System.out.println("[MegaInjector] Ошибка при загрузке модуля");
            e.printStackTrace();
        }


    }

    private static void loadReconnectModule(String modulePath) throws Exception {
        File moduleFile = new File(modulePath);
        URL moduleURL = moduleFile.toURI().toURL();
        URLClassLoader loader = new URLClassLoader(
                new URL[]{moduleURL}, ClassLoader.getSystemClassLoader()
        );

        Class<?> reconnectClass = loader.loadClass(mainClassName);

        Object moduleInstance = reconnectClass.getDeclaredConstructor().newInstance();

        reconnectClass.getMethod("onInit").invoke(moduleInstance);

        System.out.println("[MegaInjector] Модуль " + mainClassName + " успешно инициализирован");
    }

}
