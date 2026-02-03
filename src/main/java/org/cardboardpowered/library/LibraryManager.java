package org.cardboardpowered.library;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

import javax.net.ssl.HttpsURLConnection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Simple library manager which downloads external dependencies.
 */
public final class LibraryManager {

    public static final Logger logger = LogManager.getLogger("Cardboard|Libraries");

    private final File directory;
    private final boolean validateChecksums;
    private final int maxDownloadAttempts;

    private final Collection<Library> libraries;

    // Snapshot Override Paper Jar
    public static Optional<String> PAPER_OVERRIDE = Optional.empty();

    // Backup Repos
    private static final String[] BACKUP = {
    		"https://repo.papermc.io/repository/maven-snapshots/",
    		"https://maven-central.storage-download.googleapis.com/maven2/",
    		"https://repo1.maven.org/maven2/"
    };

    /**
     * @see io/papermc/paper/plugin/loader/library/impl/MavenLibraryResolver.java
     */
    private static String getCentral() {
        String central = System.getenv("PAPER_DEFAULT_CENTRAL_REPOSITORY");
        if (central == null) { central = System.getProperty("org.bukkit.plugin.java.LibraryLoader.centralURL"); }
        if (central == null) { central = "https://maven-central.storage-download.googleapis.com/maven2"; }
        return central;
    }
    
    public static LibraryManager INSTANCE;

    /**
     * Creates the instance.
     */
    public LibraryManager(String directoryName, boolean validateChecksum, int maxDownloadAttempts, Collection<Library> libraries) {
        checkNotNull(directoryName);
        INSTANCE = this;
        this.directory = new File(directoryName);
        this.validateChecksums = validateChecksum;
        this.maxDownloadAttempts = maxDownloadAttempts;
        this.libraries = libraries;
    }
    
    public String getPaperVersion() {
    	return getLibVersion("paper-api").orElse("unknown?").replaceAll("(paper-api-[0-9.]+)-R.*(-\\d+)", "$1$2");
    }

    public Stream<Library> getLibs(String id) {
    	return libraries.stream().filter(lib -> lib.artifactId.contains(id));
    }

    public String getVersion(String id) {
    	return getLibVersion(id).orElse(null);
    }
    
    public Optional<String> getLibVersion(String id) {
    	for (Library lib : libraries)
    		if (lib.artifactId.contains(id))
    			return Optional.of(lib.version);
    	return Optional.empty();
    }

    /**
     * Downloads the libraries.
     */
    public void run() {
        if (!directory.isDirectory() && !directory.mkdirs()) {
            logger.error("Could not create libraries directory: " + directory);
        }

        // Load all libraries
        for (Library lib : libraries) {
        	String fn = lib.getJarName();
        	File f = new File(directory, fn);
        	System.out.println(lib);
        	if (f.isFile()) {
        		KnotHelper.propose(f);
        	} else {
        		download(lib);
        	}
        }

        String det = "Paper-API: " + getPaperVersion() + "; Bungeechat: " + getVersion("bungeecord-chat") +
        		"; Adventure: " + getVersion("adventure-api") + " (" + getLibs("adventure").count() + ")";
        
        logger.info("Loaded " + libraries.size() + " libraries. " + det);
    }

    public static void main(String[] args) throws Exception {
    	for (Library l : org.cardboardpowered.mixin.CardboardMixinPlugin.getLibs()) {
			try {
				String s = l.readChecksumFromRepo(l.repository.orElse(getCentral()));
				logger.info("Library: " + l + ": Have: " + l.checksumValue + "; Need: " + s);
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    	org.cardboardpowered.mixin.CardboardMixinPlugin.loadLibs();
    }

    public void download(Library library) {
    	String repository = library.repository.orElse(getCentral());
    	if (!repository.endsWith("/")) repository += "/";

    	String fileName = library.getJarName();
    	File file = new File(directory, fileName);

    	if (!file.exists()) {
    		attemptDownloadWithRetries(library, repository, file);
    	} else if (this.validateChecksums && library.needsChecksumValidation()) {
    		if (!library.getChecksum(file)) {
    			logger.warn("Checksum mismatch for '" + fileName + "'. Delete it and restart to redownload.");
    		}
    	}

    	// Add to KnotClassLoader
    	try {
    		KnotHelper.propose(file);
    	} catch (Exception e) {
    		logger.warn("Failed to add to classpath: " + library, e);
    	}
    }

    private void attemptDownloadWithRetries(Library library, String repository, File file) {
    	for (int attempt = 1; attempt <= maxDownloadAttempts; attempt++) {
    		try {
    			logger.info("Downloading " + library + "...");
    			downloadPrimary(library, repository, file);

    			if (this.validateChecksums && library.needsChecksumValidation() && !library.getChecksum(file)) {
    				if (!library.handleChecksumMismatch(repository, file, attempt, maxDownloadAttempts)) {
    					continue; // retry
    				}
    			}
    		} catch (IOException ex) {
    			logger.warn("Failed to download: " + library, ex);
    			file.delete();

    			if (attempt == maxDownloadAttempts) {
    				logger.warn("Restart the server to attempt downloading '" + file.getName() + "' again.");
    				return;
    			}

    			logger.warn("Retrying '" + file.getName() + "' (" + (attempt + 1) + "/" + maxDownloadAttempts + ")");
    		}
    	}
    }

    private boolean downloadPrimary(Library library, String repository, File file) throws IOException {
    	String url = library.getUrl(repository);

    	if (PAPER_OVERRIDE.isPresent() && url.contains("/paper-api/")) {
    		logger.info("Redirecting Paper-API download to: " + PAPER_OVERRIDE.get());
    		url = PAPER_OVERRIDE.get();
    	}

    	try {
    		downloadFromUrl(new URL(url), file);
    		logger.info("Downloaded " + library + '.');
    		return true;
    	} catch (FileNotFoundException ex) {
    		for (String backupRepo : BACKUP) {
    			if (downloadPrimary(library, backupRepo, file)) {
    				return true;
    			}
    		}
    		return false;
    	}
    }

    private void downloadFromUrl(URL url, File file) throws IOException {
    	HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
    	connection.setRequestProperty("User-Agent", "Mozilla/5.0 Chrome/90.0.4430.212");

    	try (
    			ReadableByteChannel input = Channels.newChannel(connection.getInputStream());
    			FileOutputStream output = new FileOutputStream(file)
    			) {
    		output.getChannel().transferFrom(input, 0, Long.MAX_VALUE);
    	}
    }

}