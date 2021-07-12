from pathlib import Path


# Checks for duplicate properties in single elements of a model, in order to validate the input models
def check_for_duplicates(path):
    with open(path, 'r') as f:
        content = f.readlines()

    for line in content:
        parts = line.split(",")
        model_id = parts[0]
        element_id = parts[1]
        element_name = parts[2]
        if len(parts) > 3:
            properties = parts[3]
        else:
            properties = ""
        if len(parts) > 4:
            raise Exception("something went wrong!")

        # Check for duplicate properties for each element
        properties = properties.strip()
        properties = properties.split(";")
        if len(properties) > 0:
            properties_of_element = set()
            for prop in properties:
                prop = prop.strip()
                if prop in properties_of_element:
                    print("The property " + prop + " is already in the element "
                          + element_name + " of model " + model_id + "!")
                properties_of_element.add(prop)


def get_dataset_list(path_to_dir: str):
    names = []
    for filename in Path(path_to_dir).glob('**/*.csv'):
        names.append(filename)
    return names


if __name__ == "__main__":
    target = "path/to/experimental_subjects"
    datasets = get_dataset_list(target)
    for dataset in datasets:
        print(dataset)
        check_for_duplicates(dataset)

