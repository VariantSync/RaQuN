import numpy

from eval.data.result_data import MethodStatistics, CombinedResult
from eval.visualization.misc import get_real_name, get_real_dataset, get_model_type

timeout_text = "- - - timeout - - -"


def create_tabular_overview(methods: [], datasets: [], results_per_method: {}):
    # The number of columns that we need is the number of datasets times 2 (weight+runtime),
    # plus one for the method name
    number_of_columns = 1 + len(datasets) * 2

    # Initialize the string that represents the tabular
    tabular = "\\begin{tabular}{  l " + (" c " * (number_of_columns-1)) + "}\n"

    # first we add the dataset names as headers
    tabular += "\\hline\n"
    dataset_headers = ""
    for dataset in datasets:
        header = get_real_dataset(dataset)
        header = header.replace("_", "\\_")
        dataset_headers += " & \\multicolumn{2}{c}{" + header + "}"
    tabular += "Algorithm" + dataset_headers + " \\\\\n"

    # then we add the row with the headers for weight and runtime
    tabular += " & Weight & Time (in s)"*len(datasets) + " \\\\\n"
    tabular += "\\hline\n"

    # We want to track the weight and runtime
    weight_max = {}
    runtime_min = {}
    # finally, we add the results of each method for each dataset
    for method in methods:
        method_statistics = results_per_method[method]  # type: MethodStatistics
        name = get_real_name(method)
        result_line = name
        for dataset in datasets:
            # Initialize the max and min dictionaries
            if dataset not in weight_max:
                weight_max[dataset] = 0
            if dataset not in runtime_min:
                runtime_min[dataset] = 999999999

            try:
                result = method_statistics.get_result(dataset)  # type: CombinedResult

                w, w_min, w_max = get_basic_stats(result.lst_weight)

                rt, rt_min, rt_max = get_basic_stats(result.lst_runtime)

                # Determine the new max for the weight
                if w > weight_max[dataset]:
                    weight_max[dataset] = w
                # Determine the new min for the runtime
                if rt < runtime_min[dataset]:
                    runtime_min[dataset] = rt

                # format the time
                rt = format_time(rt)
                rt_min = format_time_minmax(rt_min)
                rt_max = format_time_minmax(rt_max)

                # Add the fully formatted weight and runtime to the line
                result_line += " & " \
                               + "{0:.2f} ".format(w)
                if rt == "$<$ 1s":
                    result_line += " & " + rt
                else:
                    result_line += " & " + rt \
                                   + " \\begin{tiny}[" \
                                   + rt_min + ", " + rt_max \
                                   + "]\\end{tiny}"
            except KeyError:
                result_line += " & \\multicolumn{2}{c}{" + timeout_text + "}"
        result_line += " \\\\\n"
        tabular += result_line
    tabular += "\\hline\n"
    tabular += "\\end{tabular}\n"

    # Now we set the max weight an min runtime for each dataset in bold letters
    for weight in weight_max.values():
        weight = "{0:.2f}".format(weight)
        tabular = tabular.replace(" " + weight + " ", " \\textbf{" + weight + "} ")
    for runtime in runtime_min.values():
        runtime = format_time(runtime)
        tabular = tabular.replace(" " + runtime + " ", " \\textbf{" + runtime + "} ")
    return tabular


def create_match_overview(methods: [], datasets: [], results_per_method: {}):
    # The number of columns that we need is the number of datasets times 2 (MergeFactor+ElementFit),
    number_of_columns = 1 + len(datasets) * 2

    # Initialize the string that represents the tabular
    tabular = "\\begin{tabular}{  l " + (" c " * (number_of_columns-1)) + "}\n"

    # first we add the dataset names as headers
    tabular += "\\hline\n"
    dataset_headers = ""
    for dataset in datasets:
        header = dataset.replace("_", "\\_")
        header = get_real_dataset(header)
        dataset_headers += " & \\multicolumn{2}{c}{" + header + "}"
    tabular += "Algorithm" + dataset_headers + " \\\\\n"

    # then we add the row with the headers for factor and fit
    tabular += " & Merge Factor & Element Fit"*len(datasets) + " \\\\\n"
    tabular += "\\hline\n"

    # We want to track the min merge factor, and max element fit
    merge_min = {}
    fit_max = {}
    # finally, we add the results of each method for each dataset
    for method in methods:
        method_statistics = results_per_method[method]  # type: MethodStatistics
        name = get_real_name(method)
        result_line = name
        for dataset in datasets:
            # Initialize the max and min dictionaries
            if dataset not in merge_min:
                merge_min[dataset] = 999999999
            if dataset not in fit_max:
                fit_max[dataset] = 0

            try:
                result = method_statistics.get_result(dataset)  # type: CombinedResult

                m, m_min, m_max = get_basic_stats(result.lst_merge_size_factor)
                f, f_min, f_max = get_basic_stats(result.lst_matched_element_fit)

                # Determine the new min for the merge factor
                if m < merge_min[dataset]:
                    merge_min[dataset] = m
                # Determine the new max for the element fit
                if f > fit_max[dataset]:
                    fit_max[dataset] = f

                result_line += " & " + "{0:.2f} ".format(m)

                result_line += " & " + "{0:.2f} ".format(f)
            except KeyError:
                result_line += " & \\multicolumn{2}{c}{NO VALUE}"
        result_line += " \\\\\n"
        tabular += result_line
    tabular += "\\hline\n"
    tabular += "\\end{tabular}\n"

    # Now we set the min merge factor an max fit for each dataset in bold letters
    for merge_value in merge_min.values():
        merge_value = "{0:.2f}".format(merge_value)
        tabular = tabular.replace(" " + merge_value + " ", " \\textbf{" + merge_value + "} ")
    for fit_value in fit_max.values():
        fit_value = "{0:.2f}".format(fit_value)
        tabular = tabular.replace(" " + fit_value + " ", " \\textbf{" + fit_value + "} ")
    return tabular


def create_num_of_comp_overview(datasets: [], results_per_method: {}):
    number_of_columns = 4

    # Initialize the string that represents the tabular
    tabular = "\\begin{tabular}{  l " + (" c " * (number_of_columns-1)) + "}\n"

    # first we add the method names as headers
    tabular += "\\hline\n"
    method_headers = ""
    method_headers += " & \\multicolumn{1}{c}{Full N-Way Matching}"
    method_headers += " & \\multicolumn{2}{c}{RaQuN}"
    tabular += "Dataset" + method_headers + " \\\\\n"

    # then we add the row with the headers for absolute and relative numbers
    tabular += " & \\#Comparisons &  \\#Comparisons & Saved \\\\\n"
    tabular += "\\hline\n"

    for dataset in datasets:
        if dataset == "PPU" or dataset == "Apo-Games":
            tabular += "\\hline\n"
        raqun_stats = results_per_method["RaQuN"]  # type: MethodStatistics

        raqun_result = raqun_stats.get_result(dataset)

        number_of_comparisons_total = raqun_result.dataset_number_of_comparisons_needed
        raqun_abs = raqun_result.get_maximum("Comparisons")

        raqun_rel = 100 * ((number_of_comparisons_total - raqun_abs) / number_of_comparisons_total)

        result_line = get_real_dataset(dataset)
        result_line += " & \\multicolumn{1}{r}{" + "{0:,} ".format(number_of_comparisons_total) + "}"
        result_line += " & \\multicolumn{1}{r}{" + "{0:,} ".format(raqun_abs) + "}"
        result_line += " & \\multicolumn{1}{r}{" + "{0:2.1f}\\% ".format(raqun_rel) + "}"
        result_line += " \\\\\n"
        tabular += result_line
    tabular += "\\hline\n"
    tabular += "\\end{tabular}\n"

    return tabular


def create_model_stats_overview(dataset_dir, all_datasets):
    # Initialize the string that represents the tabular
    tabular = "\\begin{tabular}{llrrrrr}\n"
    # first we add the method names as headers
    tabular += "\\hline\n"
    tabular += " & & & \\multicolumn{2}{c}{Elements} & \\multicolumn{2}{c}{Properties} \\\\\n"
    tabular += "& Model Type & \\#Models & Avg. & Median & Avg. & Median \\\\\n"
    tabular += "\\hline\n"
    tabular += "\\hline\n"

    for dataset in all_datasets:
        path_to_file = dataset_dir + "/" + dataset + ".csv"
        dataset_name = get_real_dataset(dataset)
        with open(path_to_file, 'r') as file:
            lines = file.readlines()
        number_of_props = 0
        model_name = ""
        prop_min = 9999999
        prop_max = 0
        elements_per_model = {}
        properties_per_element = []

        for line in lines:
            line = line.strip()
            parts = line.split(',')
            model_name = parts[0].strip()
            element_name = parts[2].strip()
            prop_parts = parts[-1].split(';')
            properties = []
            for part in prop_parts:
                part = part.strip()
                if len(part) > 0:
                    properties.append(part)
            # Initialize an element with the properties
            element = RElement(element_name, properties)

            # Update the info about properties
            number_of_props += len(properties)
            properties_per_element.append(len(properties))

            if model_name in elements_per_model:
                elements = elements_per_model[model_name]
            else:
                elements = []
                elements_per_model[model_name] = elements
            elements.append(element)

        if dataset_name == "PPU Structure" or dataset_name == "Apo-Games":
            tabular += "\\hline\n"
        tabular += dataset_name + " & "
        tabular += get_model_type(dataset) + " & "
        tabular += "{0:,.0f} ".format(len(elements_per_model)) + " & "
        number_of_elements_per_model = []
        for model in elements_per_model.keys():
            elements = elements_per_model.get(model)
            number_of_elements_per_model.append(len(elements))
        tabular += "{0:,.2f} ".format(numpy.average(number_of_elements_per_model)) + " & "
        tabular += "{0:,.0f} ".format(numpy.median(number_of_elements_per_model)) + " & "

        number_of_elements_total = numpy.sum(number_of_elements_per_model)
        tabular += "{0:,.2f} ".format(number_of_props / number_of_elements_total) + " & "
        tabular += "{0:,.0f} ".format(numpy.median(properties_per_element)) + " \\\\\n "
    tabular += "\\hline\n"
    tabular += "\\end{tabular}\n"

    return tabular


def format_time(time):
    if time >= 100:
        time = "{0:,.2f}".format(time)
    elif time >= 10:
        time = "{0:1.2f}".format(time)
    elif time >= 1:
        time = "{0:.2f}".format(time)
    else:
        time = "{0:0.2f}".format(time)
    return time


def format_time_minmax(time):
    if time >= 100:
        time = "{0:,.2f}".format(time)
    elif time >= 10:
        time = "{0:1.2f}".format(time)
    elif time >= 1:
        time = "{0:.2f}".format(time)
    else:
        time = "{0:0.2f}".format(time)
    return time


def get_basic_stats(values: list):
    return numpy.average(values), numpy.min(values), numpy.max(values)


class RElement:
    def __init__(self, name, properties: list):
        self.name = name
        self.properties = properties
