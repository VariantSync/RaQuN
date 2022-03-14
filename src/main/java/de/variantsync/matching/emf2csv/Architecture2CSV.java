package de.variantsync.matching.emf2csv;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.sidiff.architecture.Architecture;
import org.sidiff.architecture.ArchitecturePackage;
import org.sidiff.architecture.Component;
import org.sidiff.architecture.Connector;
import org.sidiff.architecture.Element;
import org.sidiff.architecture.Port;

import com.opencsv.CSVWriter;

/**
 * Utility class for transforming a set of architecture models (i.e., variants
 * of each other) that follow the component-connector principle into an
 * element-property representation stored as a single CSV file as required by
 * RaQuN.
 * 
 */
public class Architecture2CSV {

	public static void main(final String[] args) throws Exception {
		System.out.println("Running Architecture2CSV...");

		if (args.length != 1) {
			System.err.println("Required arguments: <model-directory>");
			System.exit(0);
		}

		final File inFolder = new File(args[0]);
		if (!inFolder.exists() || !inFolder.isDirectory()) {
			System.err.println("Directory does not exist: " + inFolder.getAbsolutePath());
			System.exit(0);
		}
		System.out.println("Model Directory: " + inFolder.getAbsolutePath());

		final File outFile = new File("csv-models/" + inFolder.getName() + ".csv");
		outFile.getParentFile().mkdirs();

		final List<EClass> elementTypes = new ArrayList<EClass>();
		elementTypes.add(ArchitecturePackage.eINSTANCE.getComponent());
		elementTypes.add(ArchitecturePackage.eINSTANCE.getConnector());

		convert(inFolder, outFile, elementTypes);
	}

	private static void convert(final File inFolder, final File outFile, final List<EClass> elementTypes) throws Exception {
		final List<String[]> allElementRecords = new ArrayList<>();

		final File[] modelFiles = inFolder.listFiles();
		for (final File file : modelFiles) {
			System.out.println("Processing " + file.getAbsolutePath());
			final Architecture model = ModelUtil.loadArchitectureModel(file.getAbsolutePath());
			String modelId = file.getName();
			modelId = modelId.substring(0, modelId.length() - 5);

			// Elements
			for (final EClass elementType : elementTypes) {
				for (final Element element : ModelUtil.getAllElements(model, elementType)) {
					final String[] elementRecord = new String[4];
					elementRecord[0] = modelId;
					elementRecord[1] = ModelUtil.getXmiId(element);
					elementRecord[2] = element.getName();

					// Properties
					if (elementType == ArchitecturePackage.eINSTANCE.getComponent()) {
						elementRecord[3] = getComponentPropertyString((Component) element);
					}
					if (elementType == ArchitecturePackage.eINSTANCE.getConnector()) {
						elementRecord[3] = getConnectorPropertyString((Connector) element);
					}

					allElementRecords.add(elementRecord);
				}
			}
		}

		writeCSV(allElementRecords, outFile.getAbsolutePath());
	}

	private static String getComponentPropertyString(final Component component) {
		String propertyString = "Component";

		for (final Port port : component.getPorts()) {
			propertyString += ";" + port.getName();

			for (final Connector c : port.getOutgoings()) {
				propertyString += ";out_" + c.getName();
			}
			for (final Connector c : port.getIncomings()) {
				propertyString += ";in_" + c.getName();
			}
		}

		return propertyString;
	}

	private static String getConnectorPropertyString(final Connector connector) {
		String propertyString = "Connector";

		propertyString += ";" + connector.getType().toString();

		propertyString += ";src_" + connector.getSource().getName();
		propertyString += ";tgt_" + connector.getTarget().getName();

		return propertyString;
	}

	private static void writeCSV(final List<String[]> stringArray, final String path) throws Exception {
		System.out.println("Writing Output CSV: " + path);
		final CSVWriter writer = new CSVWriter(new FileWriter(path.toString()), ',', Character.MIN_VALUE);
		writer.writeAll(stringArray);
		writer.close();
	}
}
