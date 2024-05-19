package com.mohistmc.banner.bukkit.nms.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import org.cardboardpowered.util.nms.ReflectionMethodVisitor;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;
// import net.fabricmc.loader.api.MappingResolver;
import net.techcable.srglib.JavaType;
import net.techcable.srglib.MethodSignature;

public class SpigotReader2 {

	// net/minecraft/world/level/World ag ()Ljava/util/function/Supplier; getProfilerSupplier
	
	public static void main(String[] args) {
		stuff_test();
	}
	
	public static void stuff_test() {
        File dir = new File("mappings");
        dir.mkdirs();
        
        File f = RemapUtils.exportResource("spigot_members.csrg", dir);
        File f2 = new File(dir, "spigot_members_obfsig.csrg");
        
        List<String> lines = new ArrayList<>();
        
        HashMap<String, String> cm = new HashMap<>();
        
        MappingResolver mr = FabricLoader.getInstance().getMappingResolver(); 
        
        float good = 0;
        float bad = 0;
        
        try {
			for (String line : Files.readAllLines(f.toPath())) {
				if (!(line.startsWith("# "))) {
					String[] spl = line.split(" ");
					
					// A                              B   C                              D
					//net/minecraft/world/level/World ag ()Ljava/util/function/Supplier; getProfilerSupplier
					
					if (spl.length == 4) {
						// METHOD
						String mowner = spl[0];
						String obf = spl[1];
						String desc = spl[2];
						String named = spl[3];
						
						//String owner = RemapUtils.map(mowner);
						//String owner_official = mr.unmapClassName("official", owner.replace('/', '.'));
						//String owner_intermed = mr.mapClassName("official", owner_official);
						
						//lines.add(mowner + " " + obf + " " + desc + " " + named);
						
						/*if (owner.equalsIgnoreCase(mowner)) {
							if (owner.indexOf('$') != -1) {
								String[] spll = mowner.split(Pattern.quote("$"));
								owner = RemapUtils.map(spll[0]);
								
								owner_official = mr.unmapClassName("official", owner.replace('/', '.')) + "$" + spll[1];
								
								owner_intermed = owner.replace('/', '.') + "$" + spll[1];
								owner_intermed = mr.mapClassName("official", owner_intermed);
								
								try {
									Class.forName(owner_intermed);
								} catch (ClassNotFoundException e) {
									String ownera = mr.unmapClassName("official", owner.replace('/', '.'));
									owner_intermed = mr.mapClassName("official", ownera + "$" + spll[1]);
								}
							}
						}*/
					
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
								}
							}
							
							sigg += jt.getDescriptor().replace(jt.getInternalName(), ll).replace('.', '/');
						} else {
							sigg += jt.getDescriptor();
						}
				    	
				    	lines.add(mowner + " " + obf + " " + sigg + " " + named);

				    	/*String mapped = mr.mapMethodName("official", owner_official, obf, sigg);
				    	
				    	if ( !(mapped.startsWith("method_") || mapped.startsWith("comp_")) ) {
				    		String dotest = ReflectionMethodVisitor.do_map(owner_intermed, obf, sigg);
				    		if (dotest.indexOf('_') != -1) {
				    			mapped = dotest;
				    		}
				    	}
				    	
				    	
				    	if (mapped.startsWith("method_") || mapped.startsWith("comp_")) {
				    		// System.out.println("MPED: " + mowner + "(" + owner_official + ")" + " / " + obf  + " " + sigg + " = " + mapped);
				    		good += 1;
				    	} else {
				    		//System.out.println("SMD: " + mowner + " " + obf + " = " + named);
				    		System.out.println("MPED: " + owner_official + " " + obf  + " " + sigg + " = " + mapped + " (" + named + ")");
				    		bad += 1;
				    	}*/
					}
					
					if (spl.length == 3) {
						// FIELD
					}

				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
        
       // System.out.println("GOOD: " + good);
       // System.out.println("BAD: " + bad);
        
		// Convert ArrayList to the text file 
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(f2.getAbsolutePath())))  
        { 
            for (String str : lines) { 
                writer.write(str); 
                writer.newLine();  
            } 
            System.out.println("ArrayList written to file successfully."); 
        } catch (IOException e) { 
            e.printStackTrace(); 
        } 
        
	}
	
}
