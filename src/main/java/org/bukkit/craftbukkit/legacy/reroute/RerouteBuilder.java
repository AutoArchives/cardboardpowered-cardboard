package org.bukkit.craftbukkit.legacy.reroute;

import com.google.common.base.Preconditions;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.craftbukkit.legacy.reroute.RequireCompatibility;
import org.bukkit.craftbukkit.legacy.reroute.DoNotReroute;
import org.bukkit.craftbukkit.legacy.reroute.InjectPluginName;
import org.bukkit.craftbukkit.legacy.reroute.InjectPluginVersion;
import org.bukkit.craftbukkit.legacy.reroute.NotInBukkit;
import org.bukkit.craftbukkit.legacy.reroute.RerouteArgument;
import org.bukkit.craftbukkit.legacy.reroute.RerouteMethodData;
import org.bukkit.craftbukkit.legacy.reroute.RerouteMethodName;
import org.bukkit.craftbukkit.legacy.reroute.RerouteReturn;
import org.bukkit.craftbukkit.legacy.reroute.RerouteStatic;
import org.bukkit.craftbukkit.util.ApiVersion;
import org.objectweb.asm.Type;

public class RerouteBuilder {
    public static Map<String, RerouteMethodData> buildFromClass(Class<?> clazz) {
        Preconditions.checkArgument((!clazz.isInterface() ? 1 : 0) != 0, (Object)"Interface Classes are currently not supported");
        HashMap<String, RerouteMethodData> result = new HashMap<String, RerouteMethodData>();
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isBridge() || method.isSynthetic() || !Modifier.isPublic(method.getModifiers()) || !Modifier.isStatic(method.getModifiers()) || method.isAnnotationPresent(DoNotReroute.class)) continue;
            RerouteMethodData rerouteMethodData = RerouteBuilder.buildFromMethod(method);
            result.put(rerouteMethodData.source(), rerouteMethodData);
        }
        return Collections.unmodifiableMap(result);
    }

    public static RerouteMethodData buildFromMethod(Method method) {
        Type sourceOwner;
        RerouteReturn rerouteReturn = new RerouteReturn(Type.getReturnType((Method)method));
        ArrayList<RerouteArgument> arguments = new ArrayList<RerouteArgument>();
        ArrayList<RerouteArgument> sourceArguments = new ArrayList<RerouteArgument>();
        for (Parameter parameter : method.getParameters()) {
            Type type = Type.getType(parameter.getType());
            boolean injectPluginName = false;
            boolean injectPluginVersion = false;
            if (parameter.isAnnotationPresent(InjectPluginName.class)) {
                if (parameter.getType() != String.class) {
                    throw new RuntimeException("Plugin name argument must be of type name, but got " + String.valueOf(parameter.getType()));
                }
                injectPluginName = true;
            }
            if (parameter.isAnnotationPresent(InjectPluginVersion.class)) {
                if (parameter.getType() != ApiVersion.class) {
                    throw new RuntimeException("Plugin version argument must be of type ApiVersion, but got " + String.valueOf(parameter.getType()));
                }
                injectPluginVersion = true;
            }
            if (injectPluginName && injectPluginVersion) {
                throw new RuntimeException("Wtf?");
            }
            RerouteArgument argument = new RerouteArgument(type, injectPluginName, injectPluginVersion);
            arguments.add(argument);
            if (injectPluginName || injectPluginVersion) continue;
            sourceArguments.add(argument);
        }
        RerouteStatic rerouteStatic = method.getAnnotation(RerouteStatic.class);
        if (rerouteStatic != null) {
            sourceOwner = Type.getObjectType((String)rerouteStatic.value());
        } else {
            RerouteArgument argument = (RerouteArgument)sourceArguments.get(0);
            sourceOwner = argument.type();
            sourceArguments.remove(argument);
        }
        Type sourceDesc = Type.getMethodType((Type)rerouteReturn.type(), (Type[])((Type[])sourceArguments.stream().map(RerouteArgument::type).toArray(Type[]::new)));
        RerouteMethodName rerouteMethodName = method.getAnnotation(RerouteMethodName.class);
        String methodName = rerouteMethodName != null ? rerouteMethodName.value() : method.getName();
        String methodKey = sourceOwner.getInternalName() + " " + sourceDesc.getDescriptor() + " " + methodName;
        Type targetType = Type.getType((Method)method);
        boolean inBukkit = !method.isAnnotationPresent(NotInBukkit.class);
        String requiredCompatibility = null;
        if (method.isAnnotationPresent(RequireCompatibility.class)) {
            requiredCompatibility = method.getAnnotation(RequireCompatibility.class).value();
        }
        return new RerouteMethodData(methodKey, sourceDesc, sourceOwner, methodName, rerouteStatic != null, targetType, Type.getInternalName(method.getDeclaringClass()), method.getName(), arguments, rerouteReturn, inBukkit, requiredCompatibility);
    }
}

