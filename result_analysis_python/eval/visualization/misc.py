
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
    name = name.replace("Apogames", "Apo-Games")
    name = name.replace("bcs", "BCS")
    name = name.replace("simulink_family_", "Simulink Family ")
    name = name.replace("_", "-")
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
    elif dataset[:6] == "family":
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
