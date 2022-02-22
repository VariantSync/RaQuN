
def get_convergence_point(data: list):
    max_value = max(data)
    margin = max_value / 1000

    for index, value in enumerate(data):
        if abs(max_value - value) < margin:
            return index+1
    return len(data)+1


def get_overtake_point(data: list, limit: float):
    for index, value in enumerate(data):
        if value > limit:
            return index+1
    return len(data)+1


def get_real_name(method):
    name = method.replace("PairwiseAsc", "Pairwise Ascending")
    name = name.replace("PairwiseDesc", "Pairwise Descending")
    return name


def get_real_dataset(dataset):
    name = dataset.replace("hospitals", "Hospital")
    name = name.replace("warehouses", "Warehouse")
    name = name.replace("random", "Random")
    name = name.replace("RandomLoose", "Loose")
    name = name.replace("RandomTight", "Tight")
    name = name.replace("ppu_statem", "PPU Behavior")
    name = name.replace("ppu", "PPU Structure")
    name = name.replace("bcms", "bCMS")
    name = name.replace("argouml", "ArgoUML")
    name = replace_argouml_subset_name(name)
    name = name.replace("Apogames", "Apo-Games")
    name = name.replace("bcs", "BCS")
    name = name.replace("simulink_family_", "Simulink Family ")
    name = name.replace("_", "-")
    return name


def replace_argouml_subset_name(name):
    name = name.replace("_p001", "   1\\%")
    name = name.replace("_p005", "   5\\%")
    name = name.replace("_p010", "  10\\%")
    name = name.replace("_p015", "  15\\%")
    name = name.replace("_p020", "  20\\%")
    name = name.replace("_p025", "  25\\%")
    name = name.replace("_p030", "  30\\%")
    name = name.replace("_p035", "  35\\%")
    name = name.replace("_p040", "  40\\%")
    name = name.replace("_p045", "  45\\%")
    name = name.replace("_p050", "  50\\%")
    name = name.replace("_p055", "  55\\%")
    name = name.replace("_p060", "  60\\%")
    name = name.replace("_p065", "  65\\%")
    name = name.replace("_p070", "  70\\%")
    name = name.replace("_p075", "  75\\%")
    name = name.replace("_p080", "  80\\%")
    name = name.replace("_p085", "  85\\%")
    name = name.replace("_p090", "  90\\%")
    name = name.replace("_p095", "  95\\%")
    return name


def get_model_type(dataset):
    model_type = "ERR"
    if dataset == "hospitals":
        model_type = "Simple class diag."
    elif dataset == "warehouses":
        model_type = "Simple class diag."
    elif dataset == "random":
        model_type = "Synthetic"
    elif dataset == "randomLoose":
        model_type = "Synthetic"
    elif dataset == "randomTight":
        model_type = "Synthetic"
    elif dataset == "ppu":
        model_type = "SysML block diag."
    elif dataset == "bcms":
        model_type = "UML class diag."
    elif dataset == "argouml":
        model_type = "UML class diag."
    elif dataset == "Apogames":
        model_type = "Simple class diag."
    elif dataset == "ppu_statem":
        model_type = "UML statemachines"
    elif dataset == "bcs":
        model_type = "Component/connector"
    elif dataset[:6] == "simuli":
        model_type = "Simulink"
    elif dataset == "DEFLT.slx":
        model_type = "Simulink"
    elif dataset == "Driving_ACC_CACC.slx":
        model_type = "Simulink"
    elif dataset == "Driving_ACC_CACC_TL.slx":
        model_type = "Simulink"
    else:
        print("Unknown dataset: " + dataset)
    return model_type
