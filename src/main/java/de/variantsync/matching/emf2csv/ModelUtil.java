package de.variantsync.matching.emf2csv;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Feature;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.resources.util.UMLResourcesUtil;
import org.sidiff.architecture.Architecture;
import org.sidiff.architecture.Element;

/**
 * Utility class for accessing model contents through the EMF reflective API. 
 * 
 */
public class ModelUtil {

	static {
		// Register the proper EMF resource factories
		final Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
		final Map<String, Object> m = reg.getExtensionToFactoryMap();
		m.put("xmi", new XMIResourceFactoryImpl());
		m.put("arch", new XMIResourceFactoryImpl());
		m.put("ecore", new EcoreResourceFactoryImpl());
	}

	public static List<Feature> getAllFeatures(final Class clazz) {
		final List<Feature> properties = new ArrayList<>();
		properties.addAll(clazz.getAttributes());
		properties.addAll(clazz.getOperations());

		return properties;
	}

	public static List<NamedElement> getAllElements(final Model model, final EClass eClass) {
		final List<NamedElement> elements = new ArrayList<>();
		for (final Iterator<EObject> iterator = model.eAllContents(); iterator.hasNext();) {
			final EObject eObject = iterator.next();
			if (eObject.eClass() == eClass) {
				elements.add((NamedElement) eObject);
			}
		}

		return elements;
	}

	public static List<Element> getAllElements(final Architecture model, final EClass eClass) {
		final List<Element> elements = new ArrayList<>();
		for (final Iterator<EObject> iterator = model.eAllContents(); iterator.hasNext();) {
			final EObject eObject = iterator.next();
			if (eObject.eClass() == eClass) {
				elements.add((Element) eObject);
			}
		}

		return elements;
	}

	public static String getXmiId(final EObject eObject) {
		assert (eObject != null && eObject.eResource() instanceof XMIResource);

		return ((XMIResource) eObject.eResource()).getID(eObject);
	}

	public static Model loadModel(final String path) {
		final ResourceSet resourceSet = new ResourceSetImpl();
		UMLResourcesUtil.init(resourceSet);
		final Resource resource = resourceSet.getResource(URI.createFileURI(path), true);

		return (Model) resource.getContents().get(0);
	}

	public static Architecture loadArchitectureModel(final String path) {
		final ResourceSet resourceSet = new ResourceSetImpl();
		UMLResourcesUtil.init(resourceSet);
		final Resource resource = resourceSet.getResource(URI.createFileURI(path), true);

		return (Architecture) resource.getContents().get(0);
	}

	public static void saveModel(final Model model, final String path) {
		System.out.println(path);
		final ResourceSet resourceSet = new ResourceSetImpl();
		UMLResourcesUtil.init(resourceSet);
		final Resource resource = resourceSet.createResource(URI.createFileURI(path));
		resource.getContents().add(model);

		// now save the content.
		try {
			resource.save(Collections.EMPTY_MAP);
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}
}
