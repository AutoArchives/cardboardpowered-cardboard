package com.mohistmc.banner.bukkit.nms.utils;

import org.cardboardpowered.CardboardMod;
import com.mohistmc.banner.bukkit.nms.model.ClassMapping;
import com.mohistmc.banner.bukkit.nms.remappers.BannerInheritanceMap;
import com.mohistmc.banner.bukkit.nms.remappers.BannerInheritanceProvider;
import com.mohistmc.banner.bukkit.nms.remappers.BannerJarMapping;
import com.mohistmc.banner.bukkit.nms.remappers.BannerJarRemapper;
import com.mohistmc.banner.bukkit.nms.remappers.BannerSuperClassRemapper;
import com.mohistmc.banner.bukkit.nms.remappers.ClassRemapperSupplier;
import com.mohistmc.banner.bukkit.nms.remappers.ReflectMethodRemapper;
import com.mohistmc.banner.bukkit.nms.remappers.ReflectRemapper;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;
import net.md_5.specialsource.InheritanceMap;
import net.md_5.specialsource.provider.JointProvider;
import net.minecraft.util.Identifier;
import net.techcable.srglib.JavaType;
import net.techcable.srglib.MethodSignature;

import org.cardboardpowered.CardboardConfig;
import org.cardboardpowered.impl.world.CraftWorld;
import org.cardboardpowered.util.nms.MappingsReader;
import org.cardboardpowered.util.nms.ReflectionMethodVisitor;
import org.cardboardpowered.util.nms.ReflectionRemapper;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.Remapper;
import org.objectweb.asm.tree.ClassNode;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author pyz
 * @date 2019/6/30 11:50 PM
 */
public class RemapUtils {

    public static BannerJarMapping jarMapping;
    public static BannerJarRemapper jarRemapper;
    private static final List<Remapper> remappers = new ArrayList<>();
    
    public static boolean DEBUG_VERBOSE_CALLS = true;

    public static String NMS_VERSION = "v1_21_R5"; // "v1_21_R3"; // "v1_20_R4";
    
    public static File exportResource(String res, File folder) {
        try (InputStream stream = MappingsReader.class.getClassLoader().getResourceAsStream("mappings/" + res)) {
            if (stream == null) throw new IOException("Null " + res);

            Path p = Paths.get(folder.getAbsolutePath() + File.separator + res);
            Files.copy(stream, p, StandardCopyOption.REPLACE_EXISTING);
            return p.toFile();
        } catch (IOException e) { e.printStackTrace(); return null;}
    }
    
    public static void LOG(String msg) {
    	CardboardMod.LOGGER.info("RemapUtils: " + msg);
    }
    
    public static void init() {
    	LOG("Remap Util init");
    	
        jarMapping = new BannerJarMapping();
        // v1_20_R1
        jarMapping.packages.put("org/bukkit/craftbukkit/" + NMS_VERSION + "/", "org/bukkit/craftbukkit/");
        jarMapping.packages.put("org/bukkit/craftbukkit/" + NMS_VERSION, "org/bukkit/craftbukkit");
        //jarMapping.packages.put("org/bukkit/craftbukkit/v1_19_R3/", "org/bukkit/craftbukkit/");
        jarMapping.packages.put("org/bukkit/craftbukkit/libs/it/unimi/dsi/fastutil/", "it/unimi/dsi/fastutil/");
        jarMapping.packages.put("org/bukkit/craftbukkit/libs/jline/", "jline/");
        jarMapping.packages.put("org/bukkit/craftbukkit/libs/org/apache/commons/", "org/apache/commons/");
        jarMapping.packages.put("org/bukkit/craftbukkit/libs/org/objectweb/asm/", "org/objectweb/asm/");
        
        
        //jarMapping.classes.put("org/bukkit/craftbukkit/" + NMS_VERSION + "/CraftServer", "org/bukkit/craftbukkit/CraftServer");
        //jarMapping.classes.put("org/bukkit/craftbukkit/" + NMS_VERSION + "/CraftWorld", "org/cardboardpowered/impl/world/CraftWorld");
        
        jarMapping.classes.put("org/spigotmc/event/entity/EntityMountEvent", "org/bukkit/event/entity/EntityMountEvent");
        jarMapping.classes.put("org/spigotmc/event/entity/EntityDismountEvent", "org/bukkit/event/entity/EntityDismountEvent");
        
    	// jarMapping.classes.put("net/ess3/provider/providers/LegacyPotionMetaProvider", "net/ess3/provider/providers/ModernPotionMetaProvider");

        
        jarMapping.setInheritanceMap(new BannerInheritanceMap());
        jarMapping.setFallbackInheritanceProvider(new BannerInheritanceProvider());

        MappingResolver mr = FabricLoader.getInstance().getMappingResolver(); 
        
        // intermediary
        
        // mr.unmapClassName("official", owner.replace('/', '.'));
        
        try {
            jarMapping.loadMappings(
                    new BufferedReader(new InputStreamReader(RemapUtils.class.getClassLoader()
                            .getResourceAsStream("mappings/spigot2srg-1.20.srg"))),
                    null,
                    null, false);
        } catch (Exception e) {
        	System.out.println("debug: error loading remaputils");
            e.printStackTrace();
        }

        File dir = new File("mappings");
        dir.mkdirs();
        
        // File f = exportResource("bukkit-1.20.4-cl-intermed.csrg", dir);
        
        String bukkit_cl_srg = "bukkit-1.21.8-cl.csrg";
        File bukkit_class_mappings_file = exportResource(bukkit_cl_srg, dir);
        
        jarMapping.classes.put("org/bukkit/craftbukkit/" + NMS_VERSION + "/CraftWorld", "org/cardboardpowered/impl/world/CraftWorld");

        // some missing
        jarMapping.classes.put("net/minecraft/nbt/ListTag", "net/minecraft/class_2499");
        jarMapping.classes.put("net/minecraft/class_7225$a", "net/minecraft/class_7225$class_7874");
        
        // HashMap<String, String> cm = new HashMap<>();
        
        String namespace = mr.getCurrentRuntimeNamespace();
        
        /*
        try {
			for (String c : Files.readAllLines(f.toPath())) {
				// mr.getNamespaces().forEach(ss -> { System.out.println(ss); });
				if (!(c.startsWith("# "))) {
					String[] spl = c.split(" ");
					if (!jarMapping.classes.containsKey(spl[0])) {
						
						String intermed = spl[1];
						
						// DEV
						if (namespace.equalsIgnoreCase("named")) {
							String n1 = mr.mapClassName("intermediary", intermed.replace('/', '.'));
							intermed = n1.replace('.', '/');
							// System.out.println(spl[1] + " / " + intermed);

							jarMapping.registerClassMapping(spl[1], intermed);
						}
						
						jarMapping.registerClassMapping(spl[0], intermed);
						cm.put(spl[0], intermed);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		*/
        
        try {
        	LOG("Reading \"" + bukkit_cl_srg + "\".");

        	List<String> lines = Files.readAllLines(bukkit_class_mappings_file.toPath());
        	int count = 0, total = 0;
        	for (String line : lines) {
        		if (line.startsWith("# ")) {
        			continue;
        		}
        		
        		String[] spl = line.split(" ");
        		
        		// Get class names
        		String obf_claz = spl[0];
        		String spi_claz = spl[1];
        		String fab_claz = mr.mapClassName("official", obf_claz.replace('/', '.')).replace('.', '/');
        		
        		if (spl.length == 3) {
        			// System.out.println("LIN! " + line + " / " + fab_claz);
        		}
        		
        		// System.out.println(obf_claz + " / " + spi_claz + " / " + fab_claz);
        		
        		if (!jarMapping.classes.containsKey(spi_claz)) {
        			jarMapping.registerClassMapping(spi_claz, fab_claz);
        			count += 1;
        		} else {
        			String key = jarMapping.classes.get(spi_claz);
        			
        			boolean replace = true;
        			
        			if (namespace.equalsIgnoreCase("named") || replace) {
        				jarMapping.byNMSSrcName.remove(spi_claz);
        				ClassMapping cam = jarMapping.registerClassMapping(spi_claz, fab_claz);

        				cam.setNmsSrcName(spi_claz);
        				cam.setMcpSrcName(fab_claz);
        				count += 1;

        				// jarMapping.classes.put(key, namespace);
        			}
        			// String key2 = jarMapping.classes.get(spi_claz);
        			
        			// System.out.println(obf_claz + " / " + spi_claz + " / " + fab_claz + " / " + key + " / " + key2);
        			
        		}
        		total += 1;
        	}
        	LOG("Parsed " + count + " classes from " + bukkit_cl_srg);
        } catch (IOException e) {
        	e.printStackTrace();
        }

        JointProvider provider = new JointProvider();
        provider.add(new BannerInheritanceProvider());
        //jarMapping.setInheritanceMap(new InheritanceMap());
        jarMapping.setFallbackInheritanceProvider(provider);
        jarRemapper = new BannerJarRemapper(jarMapping);
        remappers.add(jarRemapper);
        remappers.add(new ReflectRemapper());
        jarMapping.initFastMethodMapping(jarRemapper);
        ReflectMethodRemapper.init();

        // SpigotReader.stuff_test();
        // System.exit(1);
        
        try {
            Class.forName("com.mohistmc.banner.bukkit.nms.proxy.ProxyMethodHandlesLookup");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        
        /*
        try {
			Reader3.main(null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
        
        DEBUG_VERBOSE_CALLS = CardboardConfig.DEBUG_VERBOSE_CALLS;
    }


    public static byte[] remapFindClass(byte[] bs) {
        ClassReader reader = new ClassReader(bs); // Turn from bytes into visitor
        ClassNode classNode = new ClassNode();
        reader.accept(classNode, ClassReader.EXPAND_FRAMES);
        for (Remapper remapper : remappers) {

            ClassNode container = new ClassNode();
            ClassRemapper classRemapper;
            if (remapper instanceof ClassRemapperSupplier) {
                classRemapper = ((ClassRemapperSupplier) remapper).getClassRemapper(container);
            } else {
                classRemapper = new ClassRemapper(container, remapper);
            }
            classNode.accept(classRemapper);
            classNode = container;
        }
        BannerSuperClassRemapper.init(classNode);
        ClassWriter writer = new ClassWriter(0);
        classNode.accept(writer);
        return writer.toByteArray();

    }

    public static MappingResolver mr = FabricLoader.getInstance().getMappingResolver(); 
    
    public static String map(String typeName) {
        typeName = mapPackage(typeName);
        String res = jarMapping.classes.getOrDefault(typeName, typeName);
        
        //if (res.contains("class_7225")) {
        	// System.out.println("MIS: " + typeName + " / " + res);
        //}
        
        if (typeName.contains("$") && (typeName.contains("net") && typeName.contains("minecraft"))) {
        	
        	
        	if (!typeName.equalsIgnoreCase(res)) {
        		return res;
        	}
        	
        	String[] spl = typeName.split(Pattern.quote("$"));
        	String nam1 = spl[0];
        	String res1 = jarMapping.classes.getOrDefault(nam1, nam1);
        	
        	if (spl[1].contains("class_")) {
        		return res;
        	}
        	
        	String unnam = mr.unmapClassName("official", res1.replace('/', '.'));
        	String namm = mr.mapClassName("official", (unnam + "$" + spl[1]).replace('/', '.'));
        	
        	if (res.indexOf('/') != -1) {
        		namm = namm.replace('.', '/');
        	}
        	
        	// System.out.println("$ NAME: " + typeName + " = " + res + " = " + namm);
        	return namm;
        }
        
        /*
        if (typeName.contains("net") && typeName.contains("minecraft")) {
        	if (!res.contains("class_")) {
        		// System.out.println("RemapUtils.map: NAME: " + typeName + " = " + res);
        	}
        }
        */
        
        return res; // jarMapping.classes.getOrDefault(typeName, typeName);
    }

    public static String reverseMap(String typeName) {
        ClassMapping mapping = jarMapping.byNMSInternalName.get(typeName);
        return mapping == null ? typeName : mapping.getNmsSrcName();
    }

    public static String reverseMap(Class<?> clazz) {
        ClassMapping mapping = jarMapping.byMCPName.get(clazz.getName());
        return mapping == null ? ASMUtils.toInternalName(clazz) : mapping.getNmsSrcName();
    }
    
    public static String reverseMap_name(String class_name) {
        ClassMapping mapping = jarMapping.byMCPName.get(class_name);
        return mapping == null ? ASMUtils.toInternalName(class_name) : mapping.getNmsSrcName();
    }

    public static String mapPackage(String typeName) {
        for (Map.Entry<String, String> entry : jarMapping.packages.entrySet()) {
            String prefix = entry.getKey();
            if (typeName.startsWith(prefix)) {
                return entry.getValue() + typeName.substring(prefix.length());
            }
        }
        return typeName;
    }

    public static String remapMethodDesc(String methodDescriptor) {
        Type rt = Type.getReturnType(methodDescriptor);
        Type[] ts = Type.getArgumentTypes(methodDescriptor);
        rt = Type.getType(ASMUtils.toDescriptorV2(map(ASMUtils.getInternalName(rt))));
        for (int i = 0; i < ts.length; i++) {
            ts[i] = Type.getType(ASMUtils.toDescriptorV2(map(ASMUtils.getInternalName(ts[i]))));
        }
        return Type.getMethodType(rt, ts).getDescriptor();
    }

    public static String mapMethodName(Class<?> clazz, String name, MethodType methodType) {
        return mapMethodName(clazz, name, methodType.parameterArray());
    }

    public static String mapMethodName(Class<?> type, String name, Class<?>... parameterTypes) {
    	
    	// ReflectionMethodVisitor.do_map(type.getName(), name, parameterTypes);
    	
    	String mm = "";
    	for (Class<?> pt : parameterTypes) {
    		mm += ", " + pt.getName();
    	}
    	mm += ")";
    	
    	if (mm.length() > 2) {
    		mm = mm.substring(2);
    	}
    	
    	String res = jarMapping.fastMapMethodName(type, name, parameterTypes);
    	
    	//String resv = reverseMap(type);
    	
    	//System.out.println("DEBUG: " + type.getName() + "(" + resv + ")" + " / " + name + " (" + mm + " = " + res);
    	
    	if (res.length() <= 2) {
    		String cl = type.getName();
    		String cll = mr.unmapClassName("official", cl);
    		String match = find_meth(cll, type, name, parameterTypes);

    		if (match.contains("method_")) {
    			return match;
    		}
    	}

    	if (DEBUG_VERBOSE_CALLS) {
    		System.out.println("Reflection: " + type.getName() + " / " + res);
    	}
    	
        return res;
    }
    
    /**
     * Find matching declared method of a class.
     */
    public static String find_meth(String obfClaz, Class<?> type, String name, Class<?>... parameterTypes) {
    	System.out.println("FINDING MATCH FOR: " + obfClaz + "." + name);
    	for (Method m : type.getDeclaredMethods()) {
    		int pc = m.getParameterCount();
    		
    		if (parameterTypes.length != pc) {
    			continue;
    		}

    		try {
	    		String desc = getMethodDescriptor(m, "official");
	    		String mmm = mr.mapMethodName("official", obfClaz, name, desc);
	    		// System.out.println("POSS: " + obfClaz + "." + mmm);

	    		if (mmm.contains("method_")) {
	    			// Found Match
	    			return mmm;
	    		}
    		} catch (IllegalArgumentException e) {
    			// noop
    		}
    		
    	}
    	return name;
    }

    public static String inverseMapMethodName(Class<?> type, String name, Class<?>... parameterTypes) {
        return jarMapping.fastReverseMapMethodName(type, name, parameterTypes);
    }

    public static String mapFieldName(Class<?> type, String fieldName) {
    	if (DEBUG_VERBOSE_CALLS) {
    		System.out.println("Reflection: " + type.getName() + " / " + fieldName);
    	}
    	
        String key = reverseMap(type) + "/" + fieldName;
        String mapped = jarMapping.fields.get(key);
        if (mapped == null) {
            Class<?> superClass = type.getSuperclass();
            if (superClass != null) {
                mapped = mapFieldName(superClass, fieldName);
            }
        }
        
        String res = mapped != null ? mapped : fieldName;

        if (DEBUG_VERBOSE_CALLS) {
        	System.out.println("DEBUG: FIELD: " + type.getName() + " / " + fieldName + " = " + (mapped != null ? mapped : fieldName));
        }
        
        return mapped != null ? mapped : fieldName;
    }

    public static String inverseMapFieldName(Class<?> type, String fieldName) {
        return jarMapping.fastReverseMapFieldName(type, fieldName);
    }

    public static String inverseMapName(Class<?> clazz) {
        ClassMapping mapping = jarMapping.byMCPName.get(clazz.getName());
        return mapping == null ? clazz.getName() : mapping.getNmsName();
    }

    public static String inverseMapSimpleName(Class<?> clazz) {
        ClassMapping mapping = jarMapping.byMCPName.get(clazz.getName());
        return mapping == null ? clazz.getSimpleName() : mapping.getNmsSimpleName();
    }

    public static boolean isNMSClass(String className) {
        return className.startsWith("net.minecraft.");
    }

    public static boolean needRemap(String className){
        return className.startsWith("net.minecraft.");
    }
    
    public static String getMethodDescriptor(Method method, String name) {
        StringBuilder descriptor = new StringBuilder();
        descriptor.append("(");
        for (java.lang.reflect.Type parameterType : method.getGenericParameterTypes()) {
            descriptor.append(getTypeDescriptor(parameterType, name));
        }
        descriptor.append(")");
        descriptor.append(getTypeDescriptor(method.getGenericReturnType(), name));
        return descriptor.toString();
    }

    private static String getTypeDescriptor(java.lang.reflect.Type parameterType, String name) {
        if (parameterType instanceof Class<?>) {
            return getClassDescriptor((Class<?>) parameterType, name);
        } else if (parameterType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) parameterType;
            StringBuilder descriptor = new StringBuilder();
            descriptor.append(getClassDescriptor((Class<?>) parameterizedType.getRawType(), name));
            for (java.lang.reflect.Type arg : parameterizedType.getActualTypeArguments()) {
                descriptor.append(getTypeDescriptor(arg, name));
            }
            return descriptor.toString();
        }
        throw new IllegalArgumentException("Unsupported type: " + parameterType);
    }

    private static String getClassDescriptor(Class<?> clazz, String name) {
        if (clazz.isArray()) {
            return "[" + getClassDescriptor(clazz.getComponentType(), name);
        } else if (clazz.isPrimitive()) {
            if (clazz == void.class) return "V";
            if (clazz == boolean.class) return "Z";
            if (clazz == byte.class) return "B";
            if (clazz == char.class) return "C";
            if (clazz == short.class) return "S";
            if (clazz == int.class) return "I";
            if (clazz == long.class) return "J";
            if (clazz == float.class) return "F";
            if (clazz == double.class) return "D";
        } else {
        	
        	if (!name.isEmpty()) {
        		// String in = jt.getInternalName().replace('/', '.');
    			String ll = mr.unmapClassName(name, clazz.getName().replace('/', '.'));
    			return "L" + ll.replace('.', '/') + ";";
        	}
        	
            return "L" + clazz.getName().replace('.', '/') + ";";
        }
        throw new IllegalArgumentException("Unsupported class: " + clazz);
    }
}
