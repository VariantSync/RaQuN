from pathlib import Path


# Changes the encoding of element names for the datasets Random, Tight, and Loose, so that the names can be parsed more
# easily
def fix_random_names(path_to_fix):
    fixed_lines = []
    with open(path_to_fix, 'r') as file:
        lines = file.readlines()

        for line_number, line in enumerate(lines):
            # Fix random dataset element names
            parts_temp = line.split("[")
            fixed_line = ""
            for part in parts_temp:
                for sub_part in part.split("]"):
                    sub_part = sub_part.replace(",", "")
                    sub_part = sub_part.replace("\"", "")
                    sub_part = sub_part.replace("\"", "")
                    fixed_line += sub_part + ","
            # Remove the trailing ","
            fixed_line = fixed_line[:-1]
            fixed_lines.append(fixed_line)

    with open(path_to_fix, 'w') as file:
        file.writelines(fixed_lines)


# Adds incremental UUIDs to datasets without UUIDs for easier parsing, this does not affect the experimental results,
# as we do not use precision, recall, and f-measure to evaluate these results
def fix_element_ids(path_to_fix):
    fixed_lines = []
    with open(path_to_fix, 'r') as file:
        lines = file.readlines()

        for line_number, line in enumerate(lines):
            # Add a unique UUID to each element
            parts_temp = line.split(",")
            if len(parts_temp) < 4:
                fixed_line = ""

                fixed_line += parts_temp[0] + ","
                fixed_line += str(line_number) + ","
                for i in range(1, len(parts_temp)):
                    fixed_line += parts_temp[i] + ","
                # Remove the trailing ","
                fixed_line = fixed_line[:-1]
                fixed_lines.append(fixed_line)
            else:
                fixed_lines.append(line)

    with open(path_to_fix, 'w') as file:
        file.writelines(fixed_lines)


# Adds the name of each element as one of its properties for easier parsing
def add_names_as_properties(path_to_fix):
    fixed_lines = []
    with open(path_to_fix, 'r') as file:
        lines = file.readlines()

        # Add the name of each element as one of its properties
        for line_number, line in enumerate(lines):
            parts_temp = line.split(",")
            fixed_line = ""
            name_of_element = parts_temp[2]
            name_of_element = name_of_element[0].lower() + name_of_element[1:]
            name_of_element = "n_" + name_of_element

            # Add all parts except for the properties back to the line
            for i in range(0, len(parts_temp) - 1):
                fixed_line += parts_temp[i] + ","

            # Handle the properties
            properties = parts_temp[-1]
            if name_of_element in properties:
                # In this case we do not have to do anything
                pass
            else:
                # In this case we have to add the name as first property
                if len(properties.strip()) == 0:
                    properties = name_of_element + properties
                else:
                    properties = name_of_element + ";" + properties
            fixed_line += properties
            fixed_lines.append(fixed_line)

    with open(path_to_fix, 'w') as file:
        file.writelines(fixed_lines)


# Enumerates properties so that we can distinguish between multiple occurrences of the same property in a single element
# This does not affect the results
def enumerate_properties(path_to_fix):
    fixed_lines = []
    with open(path_to_fix, 'r') as file:
        lines = file.readlines()

        # Enumerate the properties to deal with multiple occurrences in the same element
        for line_number, line in enumerate(lines):
            parts_temp = line.split(",")
            fixed_line = ""

            # Add all parts except for the properties back to the line
            for i in range(0, len(parts_temp) - 1):
                fixed_line += parts_temp[i] + ","

            # Handle the properties
            properties = parts_temp[-1]
            # Remove line-breaks and white space
            properties = properties.strip()
            # split into separate properties
            properties = properties.split(";")
            # Initialize a dict that holds how often a properties appears in this element
            props_of_element = dict()
            props_fixed = []
            for prop in properties:
                # remove whitespace
                prop = prop.strip()
                if len(prop) < 3 or prop[-2] != "_":
                    if prop in props_of_element:
                        # Increase the counter
                        prop_count = props_of_element[prop] + 1
                    else:
                        # Create a new counter
                        prop_count = 1
                    # Rename the property and add it to the fixed properties
                    props_fixed.append(prop + "_" + str(prop_count))
                    # Set the new value of the counter in the dict
                    props_of_element[prop] = prop_count

            for prop in props_fixed:
                fixed_line += prop + ";"
            # Remove the trailing ";"
            fixed_line = fixed_line[:-1]
            # Add a linebreak
            fixed_line += "\n"
            fixed_lines.append(fixed_line)

    with open(path_to_fix, 'w') as file:
        file.writelines(fixed_lines)


# Replaces commas in properties with underscores, as comma is used as a separator when reading the dataset
def commas_in_properties(path_to_fix):
    fixed_lines = []
    with open(path_to_fix, 'r') as file:
        lines = file.readlines()

        # replace commas in line
        for line_number, line in enumerate(lines):
            parts_temp = line.split(",")
            if len(parts_temp) <= 4:
                # No property with comma
                fixed_lines.append(line)
            else:
                fixed_line = ""

                # Add all parts except for the properties back to the line
                for i in range(0, 3):
                    fixed_line += parts_temp[i] + ","

                # Handle the property parts
                properties = parts_temp[3]
                for i in range(4, len(parts_temp)):
                    properties += "_" + parts_temp[i]

                fixed_line += properties
                fixed_lines.append(fixed_line)

    with open(path_to_fix, 'w') as file:
        file.writelines(fixed_lines)


def get_dataset_list(path_to_dir: str):
    names = []
    for filename in Path(path_to_dir).glob('**/*.csv'):
        names.append(filename)
    return names


if __name__ == "__main__":
    datasets = get_dataset_list("path/to/experimental_subjects")
    exclude = ["argouml",
               "argouml_",
               "hospitals",
               "warehouses",
               "random", "randomLoose", "randomTight",
               "example", "webamp",
               "Apogames",
               "ppu.csv",
               "ppu_state",
               "bcms",
               "bcs"
               ]
    for dataset in datasets:
        should_exclude = False
        for exclude_this in exclude:
            if exclude_this in str(dataset):
                should_exclude = True
        if not should_exclude:
            print("Fixing " + str(dataset) + "...")
            # commas_in_properties(dataset)
            # fix_random_names(dataset)
            # fix_element_ids(dataset)
            # add_names_as_properties(dataset)
            # enumerate_properties(dataset)
        pass
