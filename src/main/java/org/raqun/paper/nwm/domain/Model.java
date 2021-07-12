package org.raqun.paper.nwm.domain;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Random;

public class Model {
    private ArrayList<Element> elements = new ArrayList<Element>();
    private String id;
    private HashSet<String> usedString = new HashSet<String>();

    private ArrayList<Element> sortedElems = null;
    private Hashtable<String, Element> labelToElem;
    private ArrayList<Element> elementsSortedByLabel;
    public static long maxModelId = -1;

    private static Random random = new Random(System.currentTimeMillis());

    private int mergedFrom = 1; // the number of models that were merged into this model

//	private static boolean isIn(int id, int[] idsToUse){
//		if(idsToUse == null)
//			return true;
//		for(int i=0;i<idsToUse.length;i++){
//			if(id == idsToUse[i])
//				return true;
//		}
//		return false;
//	}

//	public static ArrayList<Model> readModelsFile(String filePath, String sheetName){
//		FileInputStream file;
//		HSSFWorkbook workbook;
//		
//		try {
//			file = new FileInputStream(new File(filePath));
//        
//		//Get the workbook instance for XLS file 
//			workbook = new HSSFWorkbook(file);
//			 
//			//Get first sheet from the workbook
//			HSSFSheet sheet = workbook.getSheet(sheetName);
//			//Get iterator to all the rows in current sheet
//			Iterator<Row> rowIterator = sheet.iterator();
//			ArrayList<Model> models = new ArrayList<Model>();
//			String currModelId ="-1";
//			Model mdl = null;
//			while(rowIterator.hasNext()){
//				Row row = rowIterator.next();
//				Iterator<Cell> cellIterator = row.cellIterator();
//				while(cellIterator.hasNext()){
//					String tmpModelId =cellIterator.next().getStringCellValue();
//					String label = cellIterator.next().getStringCellValue();
//					String props = cellIterator.next().getStringCellValue();
//					 if(!tmpModelId.equals(currModelId)){
//						 mdl = new Model(tmpModelId);
//						 currModelId = tmpModelId;
//						 models.add(mdl);
//					 }
//					 mdl.addElement(new Element(label, props, currModelId));
//				}
//			}
//			return models;
//		}catch(Exception e){e.printStackTrace(); return null;}
//	}

//	public static ArrayList<Model> readModelsFile(String filePath){
//		return readModelsFile(filePath, ",")
//	}

    public static ArrayList<Model> readModelsFile(String filePath) {
        return readModelsFile(filePath, ",");
    }

    public static ArrayList<Model> readModelsFile(String filePath, String seperator) {
        File file = new File(filePath);
        BufferedReader reader;
        ArrayList<Model> models = new ArrayList<Model>();
        String currModelId = "-1";
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            Model mdl = null;
            while (true) {
                String modelLine = reader.readLine();
                if (modelLine == null)
                    break;
                if (modelLine.endsWith(",")) {
                    modelLine += " ";
                }
                String[] parts = modelLine.split(",");

                int partIndex = 0;
                String tmpModelId = parts[partIndex++];
                String groundTruth = parts[partIndex++];
                String lbl = parts[partIndex++];

                String props;
                if (parts.length > 3) {
                    props = parts[partIndex];
                } else {
                    props = "";
                }
                // if(!isIn(TmpModelId, idsToUse))
                //	 continue;
                if (!tmpModelId.equals(currModelId)) {
                    mdl = new Model(tmpModelId);
                    currModelId = tmpModelId;
                    models.add(mdl);
                }
                Element e = new Element(groundTruth, lbl, props, currModelId);
                e.setAsRaw();
                mdl.addElement(e);

            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return models;
    }

    public static ArrayList<Model> batchModelGeneration(int firstModelId, int numOfModels, int minNumOfElements, int maxNumOfElements, int minPropLength, int maxPropLength, int commonVacabularyMin, int diffVacabularyMin) {
        ArrayList<Model> models = new ArrayList<Model>();
        int numOfElementsRange = maxNumOfElements - minNumOfElements;
        int mId = firstModelId;
        for (int i = 0; i < numOfModels; i++) {
            int numOfElems = random.nextInt(numOfElementsRange + 1) + minNumOfElements;

            System.out.println("about to generate, numOfElems: " + numOfElems);
            Model m = generate(++mId, numOfElems, minPropLength, maxPropLength, commonVacabularyMin, diffVacabularyMin);
            System.out.println("generated");
            models.add(m);
        }
        return models;
    }

    public static Model generate(int modelId, int n, int minPropLength, int maxPropLength, int commonVacabularyMin, int diffVacabularyMin) {
        int numOfPropsRange = maxPropLength - minPropLength;

        Model m = new Model("" + modelId);
        while (m.size() != n) {
            int numOfProps = random.nextInt(numOfPropsRange + 1) + minPropLength;
            Element e = new Element(numOfProps, m, commonVacabularyMin, diffVacabularyMin);
            e.setAsRaw();
            m.addElement(e);
        }
        return m;
    }

    public Model(String pId) {
        setId(pId);
    }


//	public Model(HashSet<Element> e){
//		this(maxModelId);
//		elements = e;
//	}

    public Model(String id, ArrayList<Element> e) {
        this(id);
        elements = e;
    }

    public ArrayList<Tuple> getAllTuplesOfCompositeElements() {
        ArrayList<Tuple> res = new ArrayList<Tuple>();
        for (Element e : getElements()) {
            if (e.getBasedUponElements().size() > 1)
                res.add(e.getContaingTuple());
        }
        return res;
    }

//	public Model(String modelLine){
//		String[] contents = modelLine.split(",");
//		setId(new Long(contents[0]).longValue());
//		for(int i=1;i<contents.length;i++){
//			addElement(new Element(contents[i], id));
//		}
//	}

    private void setId(String pId) {
        id = pId;
        //if(id > maxModelId)
        //	maxModelId = id+1;
    }

    public int getMergedFrom() {
        return mergedFrom;
    }

    public void setMergedFrom(int mf) {
        this.mergedFrom = mf;
    }

    public String getId() {
        return id;
    }

    public ArrayList<Element> getElements() {
        return elements;
    }

    public void addElement(Element e) {
        if (!usedString.contains(e.toPrint())) {
            elements.add(e);
            usedString.add(e.toPrint());
        }
    }

    public Element getElementByLabel(String label) {
        Hashtable<String, Element> lbl2Elem = getLabelToLabelMap();
        return lbl2Elem.get(label.trim());
    }

    private Hashtable<String, Element> getLabelToLabelMap() {
        if (this.labelToElem == null) {
            this.labelToElem = new Hashtable<String, Element>();
            for (Element e : getElements()) {
                for (Element bue : e.getBasedUponElements()) {
                    labelToElem.put(bue.getLabel(), bue);
                }
            }
        }
        return labelToElem;
    }

    public int size() {
        return elements.size();
    }


    private ArrayList<Element> sortedElements() {
        if (sortedElems == null) {
            sortedElems = new ArrayList<Element>(elements);
            Collections.sort(sortedElems, new Comparator<Element>() {
                @Override
                public int compare(Element e1, Element e2) {
                    return e1.sortedProperties().toString().compareTo(e2.sortedProperties().toString());
                }
            });
        }
        return sortedElems;
    }

    public Model clone() {
        @SuppressWarnings("unchecked")
        Model other = new Model(getId(), (ArrayList<Element>) getElements().clone());
        return other;
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        StringBuilder sb = new StringBuilder();
        sb.append("==MODEL " + id + " ===");
        int i = 0;
        for (Iterator<Element> iter = sortedElements().iterator(); iter.hasNext(); ) {
            sb.append("\n");
            Element e = (Element) iter.next();
            sb.append(i++).append(") ").append(e).append(" , ");
        }
        sb.append("\n");
        return sb.toString();
    }

    public ArrayList<Element> getElementsNotFoundIn(Model other) {
        ArrayList<Element> otherElements = other.getElements();
        ArrayList<Element> retVal = new ArrayList<Element>();
        ArrayList<Element> elems = getElementsSortedByLabel();
        for (Element e : elems) {
            if (!otherElements.contains(e)) {
                retVal.add(e);
            }
        }
        return retVal;
    }

    public ArrayList<Element> getElementsSortedByLabel() {
        if (this.elementsSortedByLabel == null) {
            elementsSortedByLabel = new ArrayList<Element>(getElements());
            Collections.sort(elementsSortedByLabel, new Comparator<Element>() {
                @Override
                public int compare(Element e1, Element e2) {
                    // TODO Auto-generated method stub
                    return e1.getIdentifyingLabel().compareTo(e2.getIdentifyingLabel());
                }
            });
        }
        return elementsSortedByLabel;
    }

    public void absorb(Model other) {
        for (Element e : other.getElements()) {
            addElement(e);
        }
    }

    public void removeElement(Element e) {
        elements.remove(e);
        elementsSortedByLabel.remove(e);

    }

}
