package de.emir.main;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;
public final class Reflections {
    private static String OBC_PREFIX = Bukkit.getServer().getClass().getPackage().getName();
    private static String NMS_PREFIX = OBC_PREFIX.replace("org.bukkit.craftbukkit", "net.minecraft.server");

    private static String Version = OBC_PREFIX.replace("org.bukkit.craftbukkit", "").replace(".","");

    private static Pattern MATCH_VARIABLE = Pattern.compile("\\{([^\\}]+)\\}");

    private static String expandVariables(String name) {
        StringBuffer output = new StringBuffer();
        Matcher matcher = MATCH_VARIABLE.matcher(name);
        while (matcher.find()) {
            String replacement, variable = matcher.group(1);
            if ("nms".equalsIgnoreCase(variable)) {
                replacement = NMS_PREFIX;
            } else if ("obc".equalsIgnoreCase(variable)) {
                replacement = OBC_PREFIX;
            } else if ("version".equalsIgnoreCase(variable)) {
                replacement = Version;
            } else {
                throw new IllegalArgumentException("Unknown variable: " + variable);
            }
            if (replacement.length() > 0 && matcher.end() < name.length() && name.charAt(matcher.end()) != '.')
                replacement = String.valueOf(replacement) + ".";
            matcher.appendReplacement(output, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(output);
        return output.toString();
    }

    private static Class<?> getCanonicalClass(String canonicalName) {
        try {
            return Class.forName(canonicalName);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Cannot find " + canonicalName, e);
        }
    }

    public static Class<?> getClass(String lookupName) {
        return getCanonicalClass(expandVariables(lookupName));
    }

    public static ConstructorInvoker getConstructor(String className, Class... params) {
        return getConstructor(getClass(className), params);
    }

    public static ConstructorInvoker getConstructor(Class<?> clazz, Class... params) {
        byte b;
        int i;
        Constructor[] arrayOfConstructor;
        for (i = (arrayOfConstructor = (Constructor[])clazz.getDeclaredConstructors()).length, b = 0; b < i; ) {
            final Constructor<?> constructor = arrayOfConstructor[b];
            if (Arrays.equals((Object[])constructor.getParameterTypes(), (Object[])params)) {
                constructor.setAccessible(true);
                return new ConstructorInvoker() {
                    public Object invoke(Object... arguments) {
                        try {
                            return constructor.newInstance(arguments);
                        } catch (Exception e) {
                            throw new RuntimeException("Cannot invoke constructor " + constructor, e);
                        }
                    }
                };
            }
            b++;
        }
        throw new IllegalStateException(String.format("Unable to find constructor for %s (%s).", new Object[] { clazz, Arrays.asList(params) }));
    }

    public static Class<Object> getUntypedClass(String lookupName) {
        Class<Object> clazz = (Class)getClass(lookupName);
        return clazz;
    }

    public static <T> T newInstance(Class<T> type) {
        try {
            return type.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static interface ConstructorInvoker {
        Object invoke(Object... param1VarArgs);
    }

    public static interface FieldAccessor<T> {
        T get(Object param1Object);

        void set(Object param1Object1, Object param1Object2);

        boolean hasField(Object param1Object);
    }

    public static interface MethodInvoker {
        Object invoke(Object param1Object, Object... param1VarArgs);
    }
}

