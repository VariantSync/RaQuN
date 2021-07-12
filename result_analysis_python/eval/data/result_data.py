import numpy


class SingleResult:
    def __init__(self, json_content):
        self.run_id = json_content["runID"]
        self.method = json_content["method"]
        self.dataset = json_content["dataset"]
        self.runtime = json_content["runtime"]
        self.number_of_models = json_content["numberOfModels"]
        self.number_of_elements = json_content["numberOfElements"]
        self.number_of_tuples = json_content["numberOfTuples"]
        self.size_of_largest_model = json_content["sizeOfLargestModel"]
        self.k = json_content["k"]
        self.tp_count = json_content["tp"]
        self.fp_count = json_content["fp"]
        self.fn_count = json_content["fn"]
        self.precision = json_content["precision"]
        self.recall = json_content["recall"]
        self.f_measure = json_content["fMeasure"]
        self.weight = json_content["weight"]
        self.merge_size_factor = json_content["mergeSizeFactor"]
        self.average_matched_element_fit = json_content["averageMatchedElementFit"]
        self.matched_element_fit_per_tuple = json_content["matchedElementFitPerTuple"]
        if "numberOfNWayComparisonsTheoreticallyNeeded" in json_content:
            self.dataset_number_of_comparisons_needed = json_content["numberOfNWayComparisonsTheoreticallyNeeded"]
        else:
            self.dataset_number_of_comparisons_needed = -1
        if "numberOfComparisonsActuallyDone" in json_content:
            self.number_of_comparisons_done = json_content["numberOfComparisonsActuallyDone"]
        else:
            self.number_of_comparisons_done = -1


class CombinedResult:
    def __init__(self, l_single_results: list):
        self.l_single_results = l_single_results
        # Make sure there is at least one result in the list
        if l_single_results is None:
            raise ValueError("The list of results is None!")
        elif len(l_single_results) < 1:
            raise ValueError("No results provided!")

        # Initialize all data that has to be the same across all results
        first_result = l_single_results[0]  # type: SingleResult
        self.run_id = first_result.run_id
        self.method = first_result.method
        # Deal with argouml subsets
        temp_dataset = first_result.dataset  # type: str
        if temp_dataset.startswith("argouml_p"):
            temp_dataset = temp_dataset[:-5]
        self.dataset = temp_dataset  # type: str
        self.number_of_models = first_result.number_of_models
        self.number_of_elements = first_result.number_of_elements
        self.size_of_largest_model = first_result.size_of_largest_model
        self.k = first_result.k
        self.dataset_number_of_comparisons_needed = first_result.dataset_number_of_comparisons_needed

        # Make sure that the shared data above is actually the same
        for single_result in l_single_results:  # type: SingleResult
            if not (self.run_id == single_result.run_id
                    and self.method == single_result.method
                    and self.dataset == single_result.dataset
                    and self.number_of_models == single_result.number_of_models
                    and self.k == single_result.k):
                if not self.dataset.startswith("argouml_p"):
                    raise ValueError("The given results for " + self.method + " and " + single_result.method +
                                     " are not from the same setup and should not be averaged!")

        # Lists to collect the data of individual runs
        self.lst_runtime = []
        self.lst_number_of_tuples = []
        self.lst_tp_count = []
        self.lst_fp_count = []
        self.lst_fn_count = []
        self.lst_precision = []
        self.lst_recall = []
        self.lst_f_measure = []
        self.lst_weight = []
        self.lst_merge_size_factor = []
        self.lst_matched_element_fit = []
        self.lst_number_of_comparisons = []

        for single_result in l_single_results:
            self.lst_runtime.append(single_result.runtime)
            self.lst_number_of_tuples.append(single_result.number_of_tuples)
            self.lst_tp_count.append(single_result.tp_count)
            self.lst_fp_count.append(single_result.fp_count)
            self.lst_fn_count.append(single_result.fn_count)
            self.lst_precision.append(single_result.precision)
            self.lst_recall.append(single_result.recall)
            self.lst_f_measure.append(single_result.f_measure)
            self.lst_weight.append(single_result.weight)
            self.lst_merge_size_factor.append(single_result.merge_size_factor)
            self.lst_matched_element_fit.extend(single_result.matched_element_fit_per_tuple)
            self.lst_number_of_comparisons.append(single_result.number_of_comparisons_done)

    def get_average(self, variable_name: str):
        if variable_name == "Weight":
            return numpy.average(self.lst_weight)
        elif variable_name == "Precision":
            return numpy.average(self.lst_precision)
        elif variable_name == "Recall":
            return numpy.average(self.lst_recall)
        elif variable_name == "F-Measure":
            return numpy.average(self.lst_f_measure)
        elif variable_name == "Comparisons":
            return numpy.average(self.lst_number_of_comparisons)

    def get_maximum(self, variable_name: str):
        if variable_name == "Weight":
            return numpy.max(self.lst_weight)
        elif variable_name == "Precision":
            return numpy.max(self.lst_precision)
        elif variable_name == "Recall":
            return numpy.max(self.lst_recall)
        elif variable_name == "F-Measure":
            return numpy.max(self.lst_f_measure)
        elif variable_name == "Comparisons":
            return numpy.max(self.lst_number_of_comparisons)


class MethodStatistics:
    def __init__(self, method_name: str):
        self.method_name = method_name
        self.result_per_dataset = {}

    def add_result(self, result: CombinedResult):
        if result.dataset in self.result_per_dataset:
            # Get the dictionary of results per run_id: e.g: 2 -> AverageResult
            result_per_run_id = self.result_per_dataset.get(result.dataset)  # type: dict
        else:
            result_per_run_id = dict()
            self.result_per_dataset[result.dataset] = result_per_run_id
        if result.run_id in result_per_run_id:
            raise ValueError("There already is a run with id " + result.run_id + " for " + result.dataset +
                             " saved for " + self.method_name + "!")
        result_per_run_id[result.run_id] = result

    def get_result(self, dataset: str) -> CombinedResult:
        result_per_run_id = self.result_per_dataset[dataset]  # type: dict
        keys = result_per_run_id.keys()
        if len(keys) != 1:
            raise AssertionError(self.method_name + " has more than one run id on dataset " + dataset + "!")
        else:
            key = list(keys)[0]
            return result_per_run_id[key]

    def get_results_per_run_id(self, dataset: str) -> dict:
        return self.result_per_dataset[dataset]

    def has_result(self, dataset: str) -> bool:
        return dataset in self.result_per_dataset
