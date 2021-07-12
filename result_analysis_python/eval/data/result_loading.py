import glob
import json

from eval.data.result_data import SingleResult, CombinedResult, MethodStatistics


def load_result_file(path_to_file: str):
    with open(path_to_file, "r") as file:
        file_content = file.readlines()
    l_single_results = []
    for line in file_content:
        # Load each line separately to parse it
        json_content = json.loads(line)
        l_single_results.append(SingleResult(json_content))

    return l_single_results


def load_results(methods, result_dir):
    results_per_method = {}
    for method in methods:
        if method == "argouml" or method == "webamp":
            continue
        stat_directory = result_dir + "/" + method
        stat_files = glob.glob(stat_directory + "/*_stats.json")

        for stat_file in stat_files:
            l_single_results = load_result_file(stat_file)
            grouped_results = group_results_by_run_id(l_single_results)

            for run_id in grouped_results.keys():
                averaged_result = CombinedResult(grouped_results[run_id])

                if averaged_result.method != method:
                    raise AssertionError("Names do not match: " + averaged_result.method + " - - " + method)

                if method in results_per_method:
                    method_statistics = results_per_method[method]
                else:
                    method_statistics = MethodStatistics(method)
                    results_per_method[method] = method_statistics

                method_statistics.add_result(averaged_result)
    return results_per_method


def group_results_by_run_id(l_single_results: list):
    result_per_run_id = dict()
    for single_result in l_single_results:  # type: SingleResult
        run_id = single_result.run_id
        if run_id in result_per_run_id:
            grouped_results = result_per_run_id[run_id]
        else:
            grouped_results = []
            result_per_run_id[run_id] = grouped_results
        grouped_results.append(single_result)
    return result_per_run_id
