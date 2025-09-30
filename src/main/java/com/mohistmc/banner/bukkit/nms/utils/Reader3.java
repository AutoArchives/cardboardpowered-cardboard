package com.mohistmc.banner.bukkit.nms.utils;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.*;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;
import net.techcable.srglib.JavaType;
import net.techcable.srglib.MethodSignature;

public class Reader3 {

	public static MappingResolver mr = FabricLoader.getInstance().getMappingResolver(); 
	
	public static void main(String[] args) throws IOException {
		BufferedReader buf = new BufferedReader(new InputStreamReader(RemapUtils.class.getClassLoader()
                .getResourceAsStream("mappings/spigot2srg-1.20.srg")));
		
		List<String> lines = readLinesStream(buf);
		List<String> outt = new ArrayList<>();
		
		for (var s : lines) {
			if (s.startsWith("CL: ")) {
				continue;
			}
			
			if (s.startsWith("FD: ")) {
				// TODO
				continue;
			}
			
			if (!s.contains("method_19282")) {
				// Testing
				// continue;
			}
			
			String[] spl = s.split(" ");
			
			String spigotName = spl[1];
			
			String spigotClaz = spigotName.substring(0, spigotName.lastIndexOf('/'));
			String spigotMeth = spigotName.substring(spigotName.lastIndexOf('/') + 1);
			String spigitSig = spl[2];
			
			String intermedName = spl[3];
			
			String intermedClaz = intermedName.substring(0, intermedName.lastIndexOf('/'));
			String intermedMeth = intermedName.substring(intermedName.lastIndexOf('/') + 1);
			String intermedSig = spl[4];
			
			// outt.add(s);
			// System.out.println(s);
			// System.out.println(intermedClaz + " " + intermedMeth + " " + intermedSig);
			
			String check = intermedClaz + " " + spigotMeth + " " + intermedSig;
			
			/*
			System.out.println("====== METHOD DETAILS: ====== ");
			System.out.println("SPIGOT NAME: " + spigotName);
			System.out.println("SPIGOT SIGN: " + spigitSig);
			System.out.println("INTERM CLAZ: " + intermedClaz);
			System.out.println("INTERM METH: " + intermedMeth);
			System.out.println("INTERM SIGN: " + intermedSig);
			System.out.println("CHECK: " + check);
			System.out.println("============================= ");
			*/
			
			System.out.println("CHECK: " + check);
			
			String obfClaz = mr.unmapClassName("official", intermedClaz.replace('/', '.'));
			String obfSig = remapSig(intermedSig);
			
			String res = mr.mapMethodName("official", obfClaz, spigotMeth, obfSig);
			System.out.println("AFTER: " + res);
			
			outt.add(res);
			
			if (s.contains("method_19282")) {
				// Testing
				break;
			}
		}
		
		File dir = new File("build");
		File out = new File(dir, "mappings-genn.srg");
		System.out.println(out.toPath().toAbsolutePath().toString());
		Files.write(out.toPath(), outt);
	}
	
    public static List<String> readLinesStream(BufferedReader br) {
        List<String> lines = new ArrayList<>();
        //try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            lines = br.lines().collect(Collectors.toList());
        //} catch (IOException e) {
        //    System.err.println("Error reading file (stream): " + e.getMessage());
       // }
        return lines;
    }
	
    public static List<String> readLinesStream(String filePath) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            lines = br.lines().collect(Collectors.toList());
        } catch (IOException e) {
            System.err.println("Error reading file (stream): " + e.getMessage());
        }
        return lines;
    }
	
    public static String remapSig(String desc) {
		String imdesc = RemapUtils.remapMethodDesc(desc);

    	MethodSignature sig = MethodSignature.fromDescriptor(imdesc);
    	
    	List<JavaType> jts = sig.getParameterTypes();
    	
    	String sigg = "(";
    	
    
    	
    	for (JavaType jt : jts) {
    		if (jt.getDescriptor().indexOf("L") != -1 || jt.getDescriptor().indexOf("[[L") != -1) {
    			String in = jt.getInternalName().replace('/', '.');
    			
    			String ll = mr.unmapClassName("official", in);
    			
    			if (!ll.contains("class_")) {
					if (ll.indexOf('$') != -1) {
						String[] spll = ll.split(Pattern.quote("$"));
						String map_ll = RemapUtils.map(spll[0]);
						
						ll = map_ll + "$" + spll[1];
						
						/*String ll_intermed = map_ll.replace('/', '.') + "$" + spll[1];
						ll_intermed = mr.mapClassName("official", ll_intermed);
						
						try {
							Class.forName(ll_intermed);
							ll = mr.unmapClassName("official", ll_intermed);
						} catch (ClassNotFoundException e) {
							String map_lla = mr.unmapClassName("official", map_ll.replace('/', '.'));
							//ll = mr.mapClassName("official", map_lla + "$" + spll[1]);
							ll = map_lla + "$" + spll[1];
						}*/
					}
				}
    			
    			sigg += jt.getDescriptor().replace(jt.getInternalName(), ll).replace('.', '/');
    		} else {
    			sigg += jt.getDescriptor();
    		}
    	}

    	sigg += ")";
    	
    	JavaType jt  = sig.getReturnType();
    	
    	if (jt.getDescriptor().startsWith("L") || jt.getDescriptor().contains("[[L")) {
			String in = jt.getInternalName().replace('/', '.');

			String ll = mr.unmapClassName("official", in);

			if (!ll.contains("class_")) {
				if (ll.indexOf('$') != -1) {
					String[] spll = ll.split(Pattern.quote("$"));
					String map_ll = RemapUtils.map(spll[0]);
					
					ll = map_ll + "$" + spll[1];
					
					/*String ll_intermed = map_ll.replace('/', '.') + "$" + spll[1];
					ll_intermed = mr.mapClassName("official", ll_intermed);
					
					try {
						Class.forName(ll_intermed);
						ll = mr.unmapClassName("official", ll_intermed);
					} catch (ClassNotFoundException e) {
						String map_lla = mr.unmapClassName("official", map_ll.replace('/', '.'));
						ll = map_lla + "$" + spll[1];//mr.mapClassName("official", map_lla + "$" + spll[1]);
					}*/
				}
			}
			
			sigg += jt.getDescriptor().replace(jt.getInternalName(), ll).replace('.', '/');
		} else {
			sigg += jt.getDescriptor();
		}
    	return sigg;
	}
    
}
