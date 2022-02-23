
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
    name = name.replace("Driving-", "")
    return name


def replace_argouml_subset_name(name):
    name = name.replace("ArgoUML_p001", "Argo-Subset-1")
    name = name.replace("ArgoUML_p005", "Argo-Subset-5")
    name = name.replace("ArgoUML_p010", "Argo-Subset-10")
    name = name.replace("ArgoUML_p015", "Argo-Subset-15")
    name = name.replace("ArgoUML_p020", "Argo-Subset-20")
    name = name.replace("ArgoUML_p025", "Argo-Subset-25")
    name = name.replace("ArgoUML_p030", "Argo-Subset-30")
    name = name.replace("ArgoUML_p035", "Argo-Subset-35")
    name = name.replace("ArgoUML_p040", "Argo-Subset-40")
    name = name.replace("ArgoUML_p045", "Argo-Subset-45")
    name = name.replace("ArgoUML_p050", "Argo-Subset-50")
    name = name.replace("ArgoUML_p055", "Argo-Subset-55")
    name = name.replace("ArgoUML_p060", "Argo-Subset-60")
    name = name.replace("ArgoUML_p065", "Argo-Subset-65")
    name = name.replace("ArgoUML_p070", "Argo-Subset-70")
    name = name.replace("ArgoUML_p075", "Argo-Subset-75")
    name = name.replace("ArgoUML_p080", "Argo-Subset-80")
    name = name.replace("ArgoUML_p085", "Argo-Subset-85")
    name = name.replace("ArgoUML_p090", "Argo-Subset-90")
    name = name.replace("ArgoUML_p095", "Argo-Subset-95")
    return name


def argouml_subset_size(dataset):
    if dataset == "argouml":
        return "100\\%"
    if dataset[-5:] == "_p001":
        return "1\\%"
    if dataset[-5:] == "_p005":
        return "5\\%"
    else:
        return dataset[-2:] + "\\%"


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
