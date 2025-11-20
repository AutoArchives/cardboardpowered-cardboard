/*
 * Copyright 2016 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.cardboardpowered.util.mapping;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;

import net.fabricmc.loader.api.MappingResolver;
import net.fabricmc.mappingio.format.MappingFormat;
import net.fabricmc.mappingio.tree.MappingTree;
import net.fabricmc.mappingio.tree.MemoryMappingTree;

public class MyMappingResolver implements MappingResolver {

	private final MemoryMappingTree tree;
    private final String fromNamespace;
    private final String toNamespace;
    
    private final int targetNamespaceId;

    public MyMappingResolver(Path mappingFile, MappingFormat format, String fromNamespace, String toNamespace) throws IOException {
        this.tree = new MemoryMappingTree();
        // mapping-io can read Tiny v2, Enigma, etc.
        // net.fabricmc.mappingio.format.MappingFormat format =
        //        net.fabricmc.mappingio.format.MappingFormat..detect(mappingFile);
        net.fabricmc.mappingio.MappingReader.read(mappingFile, format, tree);

        this.fromNamespace = Objects.requireNonNull(fromNamespace);
        this.toNamespace = Objects.requireNonNull(toNamespace);
        
        this.targetNamespaceId = tree.getNamespaceId(toNamespace);
    }

	@Override
	public Collection<String> getNamespaces() {
		HashSet<String> namespaces = new HashSet<>(tree.getDstNamespaces());
		namespaces.add(tree.getSrcNamespace());
		return Collections.unmodifiableSet(namespaces);
	}

	@Override
	public String getCurrentRuntimeNamespace() {
		return toNamespace;
	}

	@Override
	public String mapClassName(String namespace, String className) {
		if (className.indexOf('/') >= 0) {
			throw new IllegalArgumentException("Class names must be provided in dot format: " + className);
		}

		return replaceSlashesWithDots(tree.mapClassName(replaceDotsWithSlashes(className), tree.getNamespaceId(namespace), targetNamespaceId));
	}

	@Override
	public String unmapClassName(String namespace, String className) {
		if (className.indexOf('/') >= 0) {
			throw new IllegalArgumentException("Class names must be provided in dot format: " + className);
		}

		return replaceSlashesWithDots(tree.mapClassName(replaceDotsWithSlashes(className), targetNamespaceId, tree.getNamespaceId(namespace)));
	}

	@Override
	public String mapFieldName(String namespace, String owner, String name, String descriptor) {
		if (owner.indexOf('/') >= 0) {
			throw new IllegalArgumentException("Class names must be provided in dot format: " + owner);
		}

		MappingTree.FieldMapping field = tree.getField(replaceDotsWithSlashes(owner), name, descriptor, tree.getNamespaceId(namespace));
		return field == null ? name : field.getName(targetNamespaceId);
	}

	@Override
	public String mapMethodName(String namespace, String owner, String name, String descriptor) {
		if (owner.indexOf('/') >= 0) {
			throw new IllegalArgumentException("Class names must be provided in dot format: " + owner);
		}

		MappingTree.MethodMapping method = tree.getMethod(replaceDotsWithSlashes(owner), name, descriptor, tree.getNamespaceId(namespace));
		return method == null ? name : method.getName(targetNamespaceId);
	}
	
	/**
     * Resolves a method name safely, automatically remapping descriptors
     * into the fromNamespace before lookup.
     *
     * @param ownerClass The class name in fromNamespace
     * @param methodName The method name in fromNamespace
     * @param methodDesc The method descriptor (any namespace)
     * @param descNamespace The namespace the descriptor is currently in
     * @return The mapped method name in toNamespace
     */
    public String mapMethodNameSafe(String namespace, String ownerClass, String methodName, String methodDesc, String descNamespace) {
        // Ensure descriptor is in fromNamespace
        String descInFrom = methodDesc;
        if (!descNamespace.equals(namespace)) {
            descInFrom = tree.mapDesc(methodDesc, tree.getNamespaceId(descNamespace), tree.getNamespaceId(namespace));
        }

        var cls = tree.getClass(ownerClass, tree.getNamespaceId(namespace));
        if (cls == null) return methodName;

        var method = cls.getMethod(methodName, descInFrom, tree.getNamespaceId(namespace));
        return method != null ? method.getName(targetNamespaceId) : methodName;
    }

	private static String replaceSlashesWithDots(String cname) {
		return cname.replace('/', '.');
	}

	private static String replaceDotsWithSlashes(String cname) {
		return cname.replace('.', '/');
	}
}
