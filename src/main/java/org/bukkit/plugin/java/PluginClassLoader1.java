package org.bukkit.plugin.java;

import com.google.common.base.Preconditions;
import com.google.common.io.ByteStreams;
import com.mohistmc.dynamicenum.MohistDynamEnum;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InaccessibleObjectException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.CodeSigner;
import java.security.CodeSource;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.logging.Level;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.SimplePluginManager;
import org.cardboardpowered.mohistremap.ClassMapping;
import org.cardboardpowered.mohistremap.RemapUtilProvider;
import org.cardboardpowered.util.nms.RemapUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.google.common.base.Preconditions;
import com.mohistmc.dynamicenum.MohistDynamEnum;
import net.md_5.specialsource.repo.RuntimeRepo;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.SimplePluginManager;
import org.cardboardpowered.BukkitLogger;
import org.cardboardpowered.mohistremap.ClassLoaderContext;
import org.cardboardpowered.mohistremap.ClassMapping;
import org.cardboardpowered.mohistremap.RemapUtilProvider;
import org.cardboardpowered.util.MyPluginFixManager;
import org.cardboardpowered.util.nms.RemapUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.security.CodeSigner;
import java.security.CodeSource;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.logging.Level;

/**
 * A ClassLoader for plugins, to allow shared classes across multiple plugins
 */
@org.jetbrains.annotations.ApiStatus.Internal // Paper
public final class PluginClassLoader extends URLClassLoader implements io.papermc.paper.plugin.provider.classloader.ConfiguredPluginClassLoader { // Paper
    private final JavaPluginLoader loader;
    private final Map<String, Class<?>> classes = new ConcurrentHashMap<String, Class<?>>();
    private final PluginDescriptionFile description;
    private final File dataFolder;
    private final File file;
    private final JarFile jar;
    private final Manifest manifest;
    private final URL url;
    private final ClassLoader libraryLoader;
    final JavaPlugin plugin;
    private JavaPlugin pluginInit;
    private IllegalStateException pluginState;
    private final Set<String> seenIllegalAccess = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private java.util.logging.Logger logger; // Paper - add field
    private io.papermc.paper.plugin.provider.classloader.PluginClassLoaderGroup classLoaderGroup; // Paper
    public io.papermc.paper.plugin.provider.entrypoint.DependencyContext dependencyContext; // Paper
    
    private final Set<Package> packageCache = Collections.newSetFromMap(new ConcurrentHashMap<>());

    static {
        ClassLoader.registerAsParallelCapable();
    }

    /**
     * @hidden
     */
    @org.jetbrains.annotations.ApiStatus.Internal // Paper
    public PluginClassLoader(@Nullable final ClassLoader parent, @NotNull final PluginDescriptionFile description, @NotNull final File dataFolder, @NotNull final File file, @Nullable ClassLoader libraryLoader, JarFile jarFile, io.papermc.paper.plugin.provider.entrypoint.DependencyContext dependencyContext) throws IOException, InvalidPluginException, MalformedURLException { // Paper - use JarFile provided by SpigotPluginProvider
        super(file.getName(), new URL[] {file.toURI().toURL()}, parent);
        this.loader = null; // Paper - pass null into loader field

        this.description = description;
        this.dataFolder = dataFolder;
        this.file = file;
        this.jar = jarFile; // Paper - use JarFile provided by SpigotPluginProvider
        this.manifest = jar.getManifest();
        this.url = file.toURI().toURL();
        this.libraryLoader = libraryLoader;

        this.logger = com.destroystokyo.paper.utils.PaperPluginLogger.getLogger(description); // Paper - Register logger early
        
        // this.logger = BukkitLogger.getPluginLogger(description.getName() + "24");
        
        // Paper start
        this.dependencyContext = dependencyContext;
        this.classLoaderGroup = io.papermc.paper.plugin.provider.classloader.PaperClassLoaderStorage.instance().registerSpigotGroup(this);
        // Paper end

        Class<?> jarClass;
        try {
            jarClass = Class.forName(description.getMain(), true, this);
        } catch (ClassNotFoundException ex) {
            throw new InvalidPluginException("Cannot find main class `" + description.getMain() + "'", ex);
        }

        Class<? extends JavaPlugin> pluginClass;
        try {
            pluginClass = jarClass.asSubclass(JavaPlugin.class);
        } catch (ClassCastException ex) {
            throw new InvalidPluginException("main class `" + description.getMain() + "' must extend JavaPlugin", ex);
        }

        Constructor<? extends JavaPlugin> pluginConstructor;
        try {
            pluginConstructor = pluginClass.getDeclaredConstructor();
        } catch (NoSuchMethodException ex) {
            throw new InvalidPluginException("main class `" + description.getMain() + "' must have a no-args constructor", ex);
        }

        try {
            // Support non-public constructors
            pluginConstructor.setAccessible(true);
        } catch (InaccessibleObjectException | SecurityException ex) {
            throw new InvalidPluginException("main class `" + description.getMain() + "' constructor inaccessible", ex);
        }

        try {
            plugin = pluginConstructor.newInstance();
        } catch (IllegalAccessException ex) {
            throw new InvalidPluginException("main class `" + description.getMain() + "' constructor inaccessible", ex);
        } catch (InstantiationException ex) {
            throw new InvalidPluginException("main class `" + description.getMain() + "' must not be abstract", ex);
        } catch (IllegalArgumentException ex) {
            throw new InvalidPluginException("Could not invoke main class `" + description.getMain() + "' constructor", ex);
        } catch (ExceptionInInitializerError | InvocationTargetException ex) {
            throw new InvalidPluginException("Exception initializing main class `" + description.getMain() + "'", ex);
        }
    }

    @Override
    public URL getResource(String name) {
        // Paper start
        URL resource = findResource(name);
        if (resource == null && libraryLoader != null) {
            return libraryLoader.getResource(name);
        }
        return resource;
        // Paper end
    }

    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        // Paper start
        java.util.ArrayList<URL> resources = new java.util.ArrayList<>();
        addEnumeration(resources, findResources(name));
        if (libraryLoader != null) {
            addEnumeration(resources, libraryLoader.getResources(name));
        }
        return Collections.enumeration(resources);
        // Paper end
    }

    // Paper start
    private <T> void addEnumeration(java.util.ArrayList<T> list, Enumeration<T> enumeration) {
        while (enumeration.hasMoreElements()) {
            list.add(enumeration.nextElement());
        }
    }
    // Paper end

    // Paper start
    @Override
    public Class<?> loadClass(@NotNull String name, boolean resolve, boolean checkGlobal, boolean checkLibraries) throws ClassNotFoundException {
        return this.loadClass0(name, resolve, checkGlobal, checkLibraries);
    }
    @Override
    public io.papermc.paper.plugin.configuration.PluginMeta getConfiguration() {
        return this.description;
    }

    @Override
    public void init(JavaPlugin plugin) {
        this.initialize(plugin);
    }

    @Override
    public JavaPlugin getPlugin() {
        return this.plugin;
    }
    // Paper end

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        return loadClass0(name, resolve, true, true);
    }

    Class<?> loadClass0(@NotNull String name, boolean resolve, boolean checkGlobal, boolean checkLibraries) throws ClassNotFoundException {
        try {
            Class<?> result = super.loadClass(name, resolve);

            // SPIGOT-6749: Library classes will appear in the above, but we don't want to return them to other plugins
            if (checkGlobal || result.getClassLoader() == this) {
                return result;
            }
        } catch (ClassNotFoundException ex) {
        }

        if (checkLibraries && libraryLoader != null) {
            try {
                return libraryLoader.loadClass(name);
            } catch (ClassNotFoundException ex) {
            }
        }

        if (checkGlobal) {
            // This ignores the libraries of other plugins, unless they are transitive dependencies.
            Class<?> result = this.classLoaderGroup.getClassByName(name, resolve, this); // Paper

            if (result != null) {
                // If the class was loaded from a library instead of a PluginClassLoader, we can assume that its associated plugin is a transitive dependency and can therefore skip this check.
                // Paper - Totally delete the illegal access logic, we are never going to enforce it anyways here.

                return result;
            }
        }

        throw new ClassNotFoundException(name);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
    	
    	if (RemapUtilProvider.get().needRemap(name.replace('/','.'))) {
        	
        	RemapUtils remapUtils = (RemapUtils) RemapUtilProvider.get();
        	
            ClassMapping remappedClassMapping = remapUtils.jarMapping.byNMSName.get(name);
            if(remappedClassMapping == null){
                throw new ClassNotFoundException(name.replace('/','.'));
            }
            String remappedClass = remappedClassMapping.getMcpName();
            return Class.forName(remappedClass);
        }
    	
        if (name.startsWith("org.bukkit.") || name.startsWith("net.minecraft.")) {
            throw new ClassNotFoundException(name);
        }
        Class<?> result = classes.get(name);

        if (result == null) {
            String path = name.replace('.', '/').concat(".class");
            // Add details to zip file errors - help debug classloading
            JarEntry entry;
            try {
                entry = jar.getJarEntry(path);
            } catch (IllegalStateException zipFileClosed) {
                if (plugin == null) {
                    throw zipFileClosed;
                }
                throw new IllegalStateException("The plugin classloader for " + plugin.getName() + " has thrown a zip file error.", zipFileClosed);
            }

            if (entry != null) {
                byte[] classBytes;

                try (InputStream is = jar.getInputStream(entry)) {
                    // classBytes = ByteStreams.toByteArray(is);
                    classBytes = RemapUtilProvider.get().getJarRemapper().remapClassFile(is, RuntimeRepo.getInstance());
                } catch (IOException ex) {
                    throw new ClassNotFoundException(name, ex);
                }
                
                // byte[] bytecode = RemapUtilProvider.get().getJarRemapper().remapClassFile(stream, RuntimeRepo.getInstance());


                // classBytes = org.bukkit.Bukkit.getServer().getUnsafe().processClass(description, path, classBytes); // Paper

                classBytes = org.bukkit.Bukkit.getServer().getUnsafe().processClass(description, path, classBytes);
                classBytes = RemapUtilProvider.get().remapFindClass(classBytes);
                classBytes = modifyByteCode(name, classBytes); // Mohist: add entry point for asm or mixin
                classBytes = MyPluginFixManager.injectPluginFix(name, classBytes); // Mohist - Inject plugin fix
                
                int dot = name.lastIndexOf('.');
                if (dot != -1) {
                    String pkgName = name.substring(0, dot);
                    if (getPackage(pkgName) == null) {
                        try {
                            if (manifest != null) {
                                definePackage(pkgName, manifest, url);
                            } else {
                                definePackage(pkgName, null, null, null, null, null, null, null);
                            }
                        } catch (IllegalArgumentException ex) {
                            if (getPackage(pkgName) == null) {
                                throw new IllegalStateException("Cannot find package " + pkgName);
                            }
                        }
                    }
                }

                CodeSigner[] signers = entry.getCodeSigners();
                CodeSource source = new CodeSource(url, signers);

                result = defineClass(name, classBytes, 0, classBytes.length, source);
            }

            if (result == null) {
                result = super.findClass(name);
            }

            classes.put(name, result);
            this.setClass(name, result); // Paper
        }

        return result;
    }

    @Override
    public void close() throws IOException {
        try {
            // Paper start
            Collection<Class<?>> classes = getClasses();
            for (Class<?> clazz : classes) {
                removeClass(clazz);
            }
            // Paper end
            super.close();
        } finally {
            jar.close();
        }
    }

    @NotNull
    Collection<Class<?>> getClasses() {
        return classes.values();
    }

    public synchronized void initialize(@NotNull JavaPlugin javaPlugin) { // Paper
        Preconditions.checkArgument(javaPlugin != null, "Initializing plugin cannot be null");
        Preconditions.checkArgument(javaPlugin.getClass().getClassLoader() == this, "Cannot initialize plugin outside of this class loader");
        if (this.plugin != null || this.pluginInit != null) {
            throw new IllegalArgumentException("Plugin already initialized!", pluginState);
        }

        pluginState = new IllegalStateException("Initial initialization");
        this.pluginInit = javaPlugin;

        javaPlugin.init(org.bukkit.Bukkit.getServer(), description, dataFolder, file, this, description, this.logger); // Paper
    }

    // Paper start
    @Override
    public String toString() {
        JavaPlugin currPlugin = plugin != null ? plugin : pluginInit;
        return "PluginClassLoader{" +
                   "plugin=" + currPlugin +
                   ", pluginEnabled=" + (currPlugin == null ? "uninitialized" : currPlugin.isEnabled()) +
                   ", url=" + file +
                   '}';
    }

    void setClass(@NotNull final String name, @NotNull final Class<?> clazz) {
        if (org.bukkit.configuration.serialization.ConfigurationSerializable.class.isAssignableFrom(clazz)) {
            Class<? extends org.bukkit.configuration.serialization.ConfigurationSerializable> serializable = clazz.asSubclass(org.bukkit.configuration.serialization.ConfigurationSerializable.class);
            org.bukkit.configuration.serialization.ConfigurationSerialization.registerClass(serializable);
        }
    }

    private void removeClass(@NotNull Class<?> clazz) {
        if (org.bukkit.configuration.serialization.ConfigurationSerializable.class.isAssignableFrom(clazz)) {
            Class<? extends org.bukkit.configuration.serialization.ConfigurationSerializable> serializable = clazz.asSubclass(org.bukkit.configuration.serialization.ConfigurationSerializable.class);
            org.bukkit.configuration.serialization.ConfigurationSerialization.unregisterClass(serializable);
        }
    }

    @Override
    public @Nullable io.papermc.paper.plugin.provider.classloader.PluginClassLoaderGroup getGroup() {
        return this.classLoaderGroup;
    }

    // Paper end
    
    private static File debug_folder = new File("C:\\Users\\isaia\\");

    private Class<?> remappedFindClass(String name) {
        Class<?> result = null;

        try {
            // Load the resource to the name
            String path = name.replace('.', '/').concat(".class");
            URL url = this.findResource(path);
            if (url != null) {
                InputStream stream = url.openStream();
                if (stream != null) {
                    byte[] bytecode = RemapUtilProvider.get().getJarRemapper().remapClassFile(stream, RuntimeRepo.getInstance());
                    
                    //if (path.contains("/worldedit/bukkit/adapter/impl/")) {
                    //	System.out.println("Debug: Processing class: " + path);
                    //}
                    
                    bytecode = loader.server.getUnsafe().processClass(description, path, bytecode);
                    bytecode = RemapUtilProvider.get().remapFindClass(bytecode);

                    bytecode = modifyByteCode(name, bytecode); // Mohist: add entry point for asm or mixin

                    bytecode = MyPluginFixManager.injectPluginFix(name, bytecode); // Mohist - Inject plugin fix

                    JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
                    URL jarURL = jarURLConnection.getJarFileURL();

                    final Manifest manifest = jarURLConnection.getManifest();
                    fixPackage(manifest, url, name);

                    CodeSource codeSource = new CodeSource(jarURL, new CodeSigner[0]);
                    result = this.defineClass(name, bytecode, 0, bytecode.length, codeSource);
                    if (result != null) {
                        // Resolve it - sets the class loader of the class
                        this.resolveClass(result);
                    }
                    
                    if (debug_folder.isDirectory() && (path.contains("worldedit") || path.contains("essentials") || path.contains("ess3") || path.contains("earth2me")) ) {
	                    File out = new File("C:\\Users\\isaia\\Documents\\fo\\" + name.replace('.', File.separatorChar) + ".class");
	                    try {
	                    	Files.createDirectories(out.toPath().getParent());
	    					Files.write(out.toPath(), bytecode);
	    				} catch (IOException e) {
	    					// TODO Auto-generated catch block
	    					e.printStackTrace();
	    				}
                    }
					
                }
            }
        } catch (Exception t) {
            t.printStackTrace();
        }

        return result;
    }

    // Mohist start: add entry point for asm or mixin
    private byte[] modifyByteCode(String className, byte[] bytes) {
        return bytes;
    }
    //Mohist end

    private void fixPackage(Manifest manifest, URL url, String name) {
        int dot = name.lastIndexOf('.');
        if (dot != -1) {
            String pkgName = name.substring(0, dot);
            Package pkg = getPackage(pkgName);
            if (pkg == null) {
                try {
                    if (manifest != null) {
                        pkg = definePackage(pkgName, manifest, url);
                    } else {
                        pkg = definePackage(pkgName, null, null, null, null, null, null, null);
                    }
                } catch (IllegalArgumentException ignored) {
                }
            }
            if (pkg != null && manifest != null) {
                if (!packageCache.contains(pkg)) {
                    Attributes attributes = manifest.getMainAttributes();
                    if (attributes != null) {
                        try {
                            try {
                                Object versionInfo = MohistDynamEnum.getField(pkg, Package.class.getDeclaredField("versionInfo"));
                                if (versionInfo != null) {
                                    Class<?> Package$VersionInfo = Class.forName("java.lang.Package$VersionInfo");
                                    MohistDynamEnum.setField(versionInfo, attributes.getValue(Attributes.Name.IMPLEMENTATION_TITLE), Package$VersionInfo.getDeclaredField("implTitle"));
                                    MohistDynamEnum.setField(versionInfo, attributes.getValue(Attributes.Name.IMPLEMENTATION_VERSION), Package$VersionInfo.getDeclaredField("implVersion"));
                                    MohistDynamEnum.setField(versionInfo, attributes.getValue(Attributes.Name.IMPLEMENTATION_VENDOR), Package$VersionInfo.getDeclaredField("implVendor"));
                                    MohistDynamEnum.setField(versionInfo, attributes.getValue(Attributes.Name.SPECIFICATION_TITLE), Package$VersionInfo.getDeclaredField("specTitle"));
                                    MohistDynamEnum.setField(versionInfo, attributes.getValue(Attributes.Name.SPECIFICATION_VERSION), Package$VersionInfo.getDeclaredField("specVersion"));
                                    MohistDynamEnum.setField(versionInfo, attributes.getValue(Attributes.Name.SPECIFICATION_VENDOR), Package$VersionInfo.getDeclaredField("specVendor"));
                                }
                            } catch (Exception ignored) {
                            }
                        } finally {
                            packageCache.add(pkg);
                        }
                    }
                }
            }
        }
    }
}

/*
package org.bukkit.plugin.java;

import com.google.common.base.Preconditions;
import com.mohistmc.dynamicenum.MohistDynamEnum;
import net.md_5.specialsource.repo.RuntimeRepo;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.SimplePluginManager;
import org.cardboardpowered.mohistremap.ClassLoaderContext;
import org.cardboardpowered.mohistremap.ClassMapping;
import org.cardboardpowered.mohistremap.RemapUtilProvider;
import org.cardboardpowered.util.MyPluginFixManager;
import org.cardboardpowered.util.nms.RemapUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.security.CodeSigner;
import java.security.CodeSource;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.logging.Level;

/**
 * A ClassLoader for plugins, to allow shared classes across multiple plugins
 *
public class PluginClassLoader extends URLClassLoader {
    private final JavaPluginLoader loader;
    private final Map<String, Class<?>> classes = new ConcurrentHashMap<String, Class<?>>();
    private final PluginDescriptionFile description;
    private final File dataFolder;
    private final File file;
    private final JarFile jar;
    private final Manifest manifest;
    private final URL url;
    private final ClassLoader libraryLoader;
    final JavaPlugin plugin;
    private JavaPlugin pluginInit;
    private IllegalStateException pluginState;
    private final Set<String> seenIllegalAccess = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private final Set<Package> packageCache = Collections.newSetFromMap(new ConcurrentHashMap<>());

    static {
        ClassLoader.registerAsParallelCapable();
    }
    
    @Nullable
    public JavaPlugin getPlugin() {
       return this.plugin;
    }


    PluginClassLoader(@NotNull final JavaPluginLoader loader, @Nullable final ClassLoader parent, @NotNull final PluginDescriptionFile description, @NotNull final File dataFolder, @NotNull final File file, @Nullable ClassLoader libraryLoader) throws IOException, InvalidPluginException, MalformedURLException {
        super(new URL[] {file.toURI().toURL()}, parent);
        Preconditions.checkArgument(loader != null, "Loader cannot be null");

        this.loader = loader;
        this.description = description;
        this.dataFolder = dataFolder;
        this.file = file;
        this.jar = new JarFile(file);
        this.manifest = jar.getManifest();
        this.url = file.toURI().toURL();
        this.libraryLoader = libraryLoader;

        try {
            Class<?> jarClass;
            try {
                jarClass = Class.forName(description.getMain(), true, this);
            } catch (ClassNotFoundException ex) {
                throw new InvalidPluginException("Cannot find main class `" + description.getMain() + "'", ex);
            }

            Class<? extends JavaPlugin> pluginClass;
            try {
                pluginClass = jarClass.asSubclass(JavaPlugin.class);
            } catch (ClassCastException ex) {
                throw new InvalidPluginException("main class `" + description.getMain() + "' does not extend JavaPlugin", ex);
            }

            plugin = pluginClass.newInstance();
        } catch (IllegalAccessException ex) {
            throw new InvalidPluginException("No public constructor", ex);
        } catch (InstantiationException ex) {
            throw new InvalidPluginException("Abnormal plugin type", ex);
        }
    }

    // private String mapCraftBukkit(String name) {
    //     if (name.startsWith("org.bukkit.craftbukkit.")) {
    //         int nameStart = name.indexOf('.', 23);
    //         if (nameStart != -1) {
    //             return "org.bukkit.craftbukkit" + name.substring(nameStart);
    //         }
    //     } else if(name.startsWith("org/bukkit/craftbukkit")) {
    //         int nameStart = name.indexOf('/', 23);
    //         if (nameStart != -1) {
    //             return "org/bukkit/craftbukkit" + name.substring(nameStart);
    //         }
    //     }
    //
    //     return name;
    // }

    @Override
    public URL getResource(String name) {
        return findResource(name);
    }

    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        return findResources(name);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        return loadClass0(name, resolve, true, true);
    }

    Class<?> loadClass0(@NotNull String name, boolean resolve, boolean checkGlobal, boolean checkLibraries) throws ClassNotFoundException {
        // name = mapCraftBukkit(name);

        try {
            Class<?> result = super.loadClass(name, resolve);

            // SPIGOT-6749: Library classes will appear in the above, but we don't want to return them to other plugins
            if (checkGlobal || result.getClassLoader() == this) {
                return result;
            }
        } catch (ClassNotFoundException ex) {
        }

        if (checkLibraries && libraryLoader != null) {
            try {
                return libraryLoader.loadClass(name);
            } catch (ClassNotFoundException ex) {
            }
        }

        if (checkGlobal) {
            // This ignores the libraries of other plugins, unless they are transitive dependencies.
            Class<?> result = loader.getClassByName(name, resolve, description);

            if (result != null) {
                // If the class was loaded from a library instead of a PluginClassLoader, we can assume that its associated plugin is a transitive dependency and can therefore skip this check.
                if (result.getClassLoader() instanceof PluginClassLoader) {
                    PluginDescriptionFile provider = ((PluginClassLoader) result.getClassLoader()).description;

                    if (provider != description
                            && !seenIllegalAccess.contains(provider.getName())
                            && !((SimplePluginManager) loader.server.getPluginManager()).isTransitiveDepend(description, provider)) {

                        seenIllegalAccess.add(provider.getName());
                        if (plugin != null) {
                            plugin.getLogger().log(Level.WARNING, "Loaded class {0} from {1} which is not a depend or softdepend of this plugin.", new Object[]{name, provider.getFullName()});
                        } else {
                            // In case the bad access occurs on construction
                            loader.server.getLogger().log(Level.WARNING, "[{0}] Loaded class {1} from {2} which is not a depend or softdepend of this plugin.", new Object[]{description.getName(), name, provider.getFullName()});
                        }
                    }
                }

                return result;
            }
        }

        throw new ClassNotFoundException(name);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        // name = mapCraftBukkit(name);

        ClassLoaderContext.put(this);
        Class<?> result;
        try {
            if (RemapUtilProvider.get().needRemap(name.replace('/','.'))) {
            	
            	RemapUtils remapUtils = (RemapUtils) RemapUtilProvider.get();
            	
                ClassMapping remappedClassMapping = remapUtils.jarMapping.byNMSName.get(name);
                if(remappedClassMapping == null){
                    throw new ClassNotFoundException(name.replace('/','.'));
                }
                String remappedClass = remappedClassMapping.getMcpName();
                return Class.forName(remappedClass);
            }
            if (name.startsWith("org.bukkit.")) {
                throw new ClassNotFoundException(name);
            }
            result = classes.get(name);
            synchronized (name.intern()) {
                if (result == null) {
                    result = remappedFindClass(name);

                    if (result != null) {
                        loader.setClass(name, result);
                    }

                    if (result == null) {
                        try {
                            result = CraftServer.server.getClass().getClassLoader().loadClass(name);
                        } catch (Throwable throwable) {
                            throw new ClassNotFoundException(name, throwable);
                        }
                    }

                    loader.setClass(name, result);
                    classes.put(name, result);
                }
            }
        } finally {
            ClassLoaderContext.pop();
        }
        return result;
    }
    
    private static File debug_folder = new File("C:\\Users\\isaia\\");

    private Class<?> remappedFindClass(String name) {
        Class<?> result = null;

        try {
            // Load the resource to the name
            String path = name.replace('.', '/').concat(".class");
            URL url = this.findResource(path);
            if (url != null) {
                InputStream stream = url.openStream();
                if (stream != null) {
                    byte[] bytecode = RemapUtilProvider.get().getJarRemapper().remapClassFile(stream, RuntimeRepo.getInstance());
                    
                    //if (path.contains("/worldedit/bukkit/adapter/impl/")) {
                    //	System.out.println("Debug: Processing class: " + path);
                    //}
                    
                    bytecode = loader.server.getUnsafe().processClass(description, path, bytecode);
                    bytecode = RemapUtilProvider.get().remapFindClass(bytecode);

                    bytecode = modifyByteCode(name, bytecode); // Mohist: add entry point for asm or mixin

                    bytecode = MyPluginFixManager.injectPluginFix(name, bytecode); // Mohist - Inject plugin fix

                    JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
                    URL jarURL = jarURLConnection.getJarFileURL();

                    final Manifest manifest = jarURLConnection.getManifest();
                    fixPackage(manifest, url, name);

                    CodeSource codeSource = new CodeSource(jarURL, new CodeSigner[0]);
                    result = this.defineClass(name, bytecode, 0, bytecode.length, codeSource);
                    if (result != null) {
                        // Resolve it - sets the class loader of the class
                        this.resolveClass(result);
                    }
                    
                    if (debug_folder.isDirectory() && (path.contains("worldedit") || path.contains("essentials") || path.contains("ess3") || path.contains("earth2me")) ) {
	                    File out = new File("C:\\Users\\isaia\\Documents\\fo\\" + name.replace('.', File.separatorChar) + ".class");
	                    try {
	                    	Files.createDirectories(out.toPath().getParent());
	    					Files.write(out.toPath(), bytecode);
	    				} catch (IOException e) {
	    					// TODO Auto-generated catch block
	    					e.printStackTrace();
	    				}
                    }
					
                }
            }
        } catch (Exception t) {
            t.printStackTrace();
        }

        return result;
    }

    // Mohist start: add entry point for asm or mixin
    private byte[] modifyByteCode(String className, byte[] bytes) {
        return bytes;
    }
    //Mohist end

    private void fixPackage(Manifest manifest, URL url, String name) {
        int dot = name.lastIndexOf('.');
        if (dot != -1) {
            String pkgName = name.substring(0, dot);
            Package pkg = getPackage(pkgName);
            if (pkg == null) {
                try {
                    if (manifest != null) {
                        pkg = definePackage(pkgName, manifest, url);
                    } else {
                        pkg = definePackage(pkgName, null, null, null, null, null, null, null);
                    }
                } catch (IllegalArgumentException ignored) {
                }
            }
            if (pkg != null && manifest != null) {
                if (!packageCache.contains(pkg)) {
                    Attributes attributes = manifest.getMainAttributes();
                    if (attributes != null) {
                        try {
                            try {
                                Object versionInfo = MohistDynamEnum.getField(pkg, Package.class.getDeclaredField("versionInfo"));
                                if (versionInfo != null) {
                                    Class<?> Package$VersionInfo = Class.forName("java.lang.Package$VersionInfo");
                                    MohistDynamEnum.setField(versionInfo, attributes.getValue(Attributes.Name.IMPLEMENTATION_TITLE), Package$VersionInfo.getDeclaredField("implTitle"));
                                    MohistDynamEnum.setField(versionInfo, attributes.getValue(Attributes.Name.IMPLEMENTATION_VERSION), Package$VersionInfo.getDeclaredField("implVersion"));
                                    MohistDynamEnum.setField(versionInfo, attributes.getValue(Attributes.Name.IMPLEMENTATION_VENDOR), Package$VersionInfo.getDeclaredField("implVendor"));
                                    MohistDynamEnum.setField(versionInfo, attributes.getValue(Attributes.Name.SPECIFICATION_TITLE), Package$VersionInfo.getDeclaredField("specTitle"));
                                    MohistDynamEnum.setField(versionInfo, attributes.getValue(Attributes.Name.SPECIFICATION_VERSION), Package$VersionInfo.getDeclaredField("specVersion"));
                                    MohistDynamEnum.setField(versionInfo, attributes.getValue(Attributes.Name.SPECIFICATION_VENDOR), Package$VersionInfo.getDeclaredField("specVendor"));
                                }
                            } catch (Exception ignored) {
                            }
                        } finally {
                            packageCache.add(pkg);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void close() throws IOException {
        try {
            super.close();
        } finally {
            jar.close();
        }
    }

    @NotNull
    Collection<Class<?>> getClasses() {
        return classes.values();
    }

    synchronized void initialize(@NotNull JavaPlugin javaPlugin) {
        Preconditions.checkArgument(javaPlugin != null, "Initializing plugin cannot be null");
        Preconditions.checkArgument(javaPlugin.getClass()
                .getClassLoader() == this, "Cannot initialize plugin outside of this class loader");
        if (this.plugin != null || this.pluginInit != null) {
            throw new IllegalArgumentException("Plugin already initialized!", pluginState);
        }

        pluginState = new IllegalStateException("Initial initialization");
        this.pluginInit = javaPlugin;

        javaPlugin.init(loader, loader.server, description, dataFolder, file, this);
    }
}*/