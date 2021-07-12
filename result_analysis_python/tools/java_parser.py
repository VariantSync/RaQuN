import re
from pathlib import Path


test_string = ""
UUID = 0


# Parses Java-software variants to simple element-property models
def parse_file_content(content: []):
    class_name = ""

    content_str = ""
    for line in content:
        content_str += line + "\n"

    # Filter all comment lines
    comments = re.findall(r"(?s)/\*.*?\*/", content_str)
    comments.extend(re.findall(r".*?//.*\n", content_str))
    for comment in comments:
        content_str = content_str.replace(comment, "")

    content = content_str.split("\n")
    multi_line = ""
    field_lines = []
    method_lines = []
    block_counter = 0
    for code_line in content:  # type: str
        code_line = code_line.strip()

        if class_name == "":
            if "class" in code_line or "interface" in code_line or "enum" in code_line:
                token = code_line.split()
                is_next = False
                for tok in token:
                    if is_next:
                        class_name = tok
                        break
                    elif tok == "class" or tok == "interface" or tok == "enum":
                        is_next = True
                class_name = class_name.replace("{", "")
                class_name = class_name.strip()
                if class_name == "":
                    print("ERROR")

        if block_counter == 1 and code_line != "":
            multi_line += code_line
            if code_line[-1] == "{" or code_line[-1] == ";" or code_line[-1] == "}":
                # Remove definition of field values
                multi_line = multi_line.split("=")[0]

                # Filter not needed token from the line
                multi_line = multi_line.replace("{", "")
                multi_line = multi_line.replace("}", "")
                multi_line = multi_line.replace(";", "")
                multi_line = multi_line.strip()

                method_id = re.findall(r"\s*\w+\s*\(.*\)", multi_line)
                if len(method_id) == 0:
                    # Account for multiple field declarations in one statement
                    multi_field_parts = multi_line.split(",")
                    for multi in multi_field_parts:
                        splits = multi.split()
                        last_token = ""
                        if len(splits) > 0:
                            last_token = multi.split()[-1]

                        if last_token != "static" and last_token != "":
                            field_lines.append(last_token)
                else:
                    parts = multi_line.split("(")
                    name_token = parts[0]
                    parameter_token = parts[1]

                    names = []
                    parameter_token = parameter_token.replace(")", "")
                    parameter_token = parameter_token.replace(", ", ",")
                    if len(parameter_token.strip()) > 1:
                        for name in parameter_token.split(","):
                            words = name.split()
                            word = words[0].strip()
                            if len(word) > 0:
                                names.append(word.strip())

                    method_name = name_token.strip().split()[-1]
                    for name in names:
                        method_name += "_" + name
                    if len(names) == 0:
                        method_name += "_"
                    method_lines.append(method_name)
                multi_line = ""

        if "{" in code_line:
            block_counter += 1

        if "}" in code_line:
            block_counter -= 1

    result = class_name + ","
    for field in set(field_lines):
        result += field + ";"
    for method in set(method_lines):
        result += method + ";"
    while result[-1] == ";":
        result = result[:-1]

    return result


def get_class_list(path_to_dir: str):
    names = []
    for filename in Path(path_to_dir).glob('**/*.java'):
        names.append(filename)
    return names


def parse_model(path_to_dir: str, model_name: str):
    global UUID
    class_files = get_class_list(path_to_dir)
    unique_class_names = set()
    result_string = ""
    for path in class_files:  # type: str
        with open(path, 'r') as source:
            lines = source.readlines()
        representation = parse_file_content(lines)

        class_name = representation.split(",")[0]
        if class_name in unique_class_names:
            print("WARNING: The class " + class_name + " has been found several times in model " + model_name)
        unique_class_names.add(class_name)

        result_string += model_name + "," + str(UUID) + "," + representation + "\n"
        UUID = UUID + 1
    return result_string


def parse_variants(model_paths: []):
    variants_string = ""
    for path in model_paths:  # type: str
        model_name = path.split("/")[-1]
        model_representation = parse_model(path, model_name)
        variants_string += model_representation
    return variants_string


def get_dirs_in_dir(path_to_dir: str):
    import os
    return [dI for dI in os.listdir(path_to_dir) if os.path.isdir(os.path.join(path_to_dir, dI))]


if __name__ == "__main__":
    target = "path/to/dataset/Raw"
    save_dir = "path/to/dataset/Processed"

    projects = get_dirs_in_dir(target)
    for project in projects:
        variants = get_dirs_in_dir(target + "/" + project)

        temp = []
        for variant in variants:
            temp.append(target + "/" + project + "/" + variant)
        variants = temp

        variants_representation = parse_variants(variants)
        with open(save_dir + "/" + project + ".csv", 'w') as save_file:
            save_file.write(variants_representation)
