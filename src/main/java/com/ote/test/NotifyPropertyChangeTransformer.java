package com.ote.test;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.stream.Stream;

public class NotifyPropertyChangeTransformer implements ClassFileTransformer {


    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

        byte[] byteCode = classfileBuffer;

        if (!className.equals("com/ote/test/User")) {
            return byteCode;
        }

        ClassPool cp = ClassPool.getDefault();
        try {
            CtClass cc = cp.get(className.replaceAll("/", "."));

            Stream.of(cc.getDeclaredFields()).
                    filter(field -> field.hasAnnotation(NotifyPropertyChange.class)).
                    forEach(field ->
                    {
                        try {
                            CtMethod triggerMethod = cc.getDeclaredMethod(((NotifyPropertyChange) field.getAnnotation(NotifyPropertyChange.class)).method());

                            Stream.of(cc.getDeclaredMethods()).
                                    filter(m -> m.getName().startsWith("set") && m.getName().toLowerCase().contains(field.getName())).
                                    findAny().ifPresent(m -> {
                                try {
                                    m.insertAfter("{" + triggerMethod.getName() + "();}", true);
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            });

                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });

            byteCode = cc.toBytecode();
            cc.detach();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return byteCode;
    }
}
