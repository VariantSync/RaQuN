package de.variantsync.matching.raqun.tree;

import de.variantsync.matching.raqun.data.RModel;
import de.variantsync.matching.raqun.vectorization.CharacterBasedVectorization;
import de.variantsync.matching.testhelper.TestDataFactory;
import de.variantsync.matching.raqun.data.RElement;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;

import static de.variantsync.matching.testhelper.TestDataFactory.getModels;

public class CharacterBasedVectorizationTest {
    @Test
    public void initializationWithNullElementsInvalid() {
        Assertions.assertThrows(NullPointerException.class, () -> new CharacterBasedVectorization().initialize(null));
    }

    @Test
    public void initializationWithZeroElementsInvalid() {
        List<RModel> models = new ArrayList<>();
        Assertions.assertThrows(IllegalArgumentException.class, () -> new CharacterBasedVectorization().initialize(models));
    }

    @Test
    public void initializationWithExactlyOneElement() {
        List<RElement> elements = new ArrayList<>();
        RElement simpleElement = new RElement("A", "Simple", "1", new ArrayList<>());
        simpleElement.getProperties().add("property1");
        simpleElement.getProperties().add("Property2");

        elements.add(simpleElement);
        CharacterBasedVectorization vectorization = new CharacterBasedVectorization();

        vectorization.initialize(getModels(elements));

        Set<Character> charactersInProperties = getCharactersInProperties(simpleElement);

        Map<Character, Integer> characterDimensions = vectorization.getCharacterDimensions();
        assert characterDimensions.size() == charactersInProperties.size();
        assert characterDimensions.containsKey('p');
        assert characterDimensions.containsKey('1');
        assert characterDimensions.containsKey('y');
        assert !characterDimensions.containsKey('P');
    }

    @Test
    public void initializationWithSeveralElements() {
        List<RElement> elements = TestDataFactory.getElementList();
        CharacterBasedVectorization vectorization = new CharacterBasedVectorization();
        vectorization.initialize(getModels(elements));
        Map<Character, Integer> characterDimensions = vectorization.getCharacterDimensions();

        Set<Character> charactersInProperties = getCharactersInProperties(elements);

        assert characterDimensions.size() == charactersInProperties.size();
        assert vectorization.getNumberOfDimension() == charactersInProperties.size() + 2;
    }

    @Test
    public void generationOfVectors() {
        List<RElement> elements = TestDataFactory.getElementListWithoutNames();
        // Add one element twice to check for this case as well
        elements.add(elements.get(0));
        CharacterBasedVectorization vectorization = new CharacterBasedVectorization();
        vectorization.initialize(getModels(elements));
        Map<Character, Integer> characterDimensions = vectorization.getCharacterDimensions();

        Set<Character> charactersInProperties = getCharactersInProperties(elements);

        for (RElement element : elements) {
            RVector vector = vectorization.vectorFor(element);
            // Two additional dimensions, one for number of props, one for prop name length
            assert vector.getDimensions() == charactersInProperties.size() + 2;

            int dimensionOfP = characterDimensions.get('p');
            int dimensionOfR = characterDimensions.get('r');
            int dimensionOf2 = characterDimensions.get('2');

            assert Double.compare(vector.getCoord(dimensionOfP), 4) == 0
                    || Double.compare(vector.getCoord(dimensionOfP), 6) == 0;
            assert Double.compare(vector.getCoord(dimensionOfR), 4) == 0
                    || Double.compare(vector.getCoord(dimensionOfR), 6) == 0;
            assert Double.compare(vector.getCoord(dimensionOf2), 1) == 0;
        }
    }

    @Test
    public void elementWithoutPropertiesIsInvalid() {
        List<RElement> elements = TestDataFactory.getElementList();
        // Remove all properties of one element to check whether this case is recognized as invalid
        elements.get(0).getProperties().clear();
        CharacterBasedVectorization vectorization = new CharacterBasedVectorization();
        vectorization.initialize(getModels(elements));
        Assertions.assertThrows(IllegalArgumentException.class, () -> vectorization.vectorFor(elements.get(0)));
    }

    private Set<Character> getCharactersInProperties(RElement... elements) {
        return getCharactersInProperties(Arrays.asList(elements));
    }

    private Set<Character> getCharactersInProperties(Collection<RElement> elements) {
        Set<Character> charactersInProperties = new HashSet<>();
        for (RElement element : elements) {
            for (String property : element.getProperties()) {
                for (char c : property.toCharArray()) {
                    c = Character.toLowerCase(c);
                    charactersInProperties.add(c);
                }
            }
        }
        return charactersInProperties;
    }
}