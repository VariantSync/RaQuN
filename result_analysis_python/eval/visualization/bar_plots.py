import matplotlib.pyplot as plt
import matplotlib.patches as mpatches
import numpy
from matplotlib.ticker import FuncFormatter
from pathlib import Path

from eval.data.result_data import MethodStatistics
from eval.visualization.misc import get_convergence_point, get_real_name, get_real_dataset
import matplotlib as mpl
mpl.rcParams['pdf.fonttype'] = 42
mpl.rcParams['font.sans-serif'] = ["Verdana", "Arial", "Helvetica", "Avant Garde", "sans-serif"]
mpl.rcParams['figure.dpi'] = 600
mpl.rcParams['savefig.format'] = "png"
save_dir = "./../results/eval-results/fig/"
Path(save_dir).mkdir(parents=True, exist_ok=True)

legend_size = 13
title_size = 16
tick_size = 14
axis_label_size = 16

colors = ["blue", "orange", "red", "purple", "green"]


def create_runtime_plots(methods: [], datasets: [], results_per_method: {}, parameter: str = "Weight"):
    for method in methods:
        if method not in results_per_method:
            continue
        method_statistics = results_per_method[method]  # type: MethodStatistics

        for dataset in datasets:
            # Get the statistics of the current method on this dataset
            raqun_stats = method_statistics.get_results_per_run_id(dataset)
            max_k = len(raqun_stats)

            raqun_runtime_vectors = []
            raqun_vector = []
            k_label = []
            for k in range(1, max_k + 1):
                if k % 2 == 1:
                    k_label.append(k)
                else:
                    k_label.append("")
            k_vector = [k for k in range(1, max_k + 1)]

            # Get the data for which we want to plot the overtake and convergence points
            if parameter == "Weight":
                for k in k_vector:
                    value = numpy.average(raqun_stats[k].lst_weight)
                    raqun_vector.append(value)
            elif parameter == "Precision":
                for k in k_vector:
                    value = numpy.average(raqun_stats[k].lst_precision)
                    raqun_vector.append(value)
            elif parameter == "Recall":
                for k in k_vector:
                    value = numpy.average(raqun_stats[k].lst_recall)
                    raqun_vector.append(value)
            elif parameter == "F-Measure":
                for k in k_vector:
                    value = numpy.average(raqun_stats[k].lst_f_measure)
                    raqun_vector.append(value)

            # Get the runtime data for which we create the boxplots
            for k in k_vector:
                raqun_runtime_vectors.append(raqun_stats[k].lst_runtime)

            # Get the convergence point
            convergence_point = get_convergence_point(raqun_vector)

            # Plot the data
            fig, ax = plt.subplots()
            data_name = get_real_dataset(dataset)
            ax.set_title(data_name, fontsize=title_size)
            ax.boxplot(raqun_runtime_vectors)

            ax.axvline(int(convergence_point), c='r')

            # Plot the dataset size, ergo the static k we choose for bestK
            number_of_models = raqun_stats[1].number_of_models
            ax.axvline(number_of_models, c='b')
            # plt.rcParams.update({'axes.labelsize' : 'medium'})
            labels = []
            labels.append(mpatches.Patch(color='r', label="Best Match Candidates Found"))
            labels.append(mpatches.Patch(color='b', label="k' = n"))
            plt.xticks(range(1, len(k_label)+1), k_label)
            plt.xlabel("Number of Neighbors (k')", fontsize=axis_label_size)
            plt.ylabel("Runtime in Seconds", fontsize=axis_label_size)
            plt.legend(handles=labels, loc=2, prop={'size': legend_size})

            ax.tick_params(axis='x', labelsize=tick_size)
            ax.tick_params(axis='y', labelsize=tick_size)
            plt.tight_layout()
            # plt.show()
            fig.savefig(save_dir + "Runtime" + "_" + method + "_" + dataset + ".png")


def create_runtime_plot_argouml(fig_label, methods: [], datasets: [], results_per_method: {}, use_legend=False, use_log=True):
    fig, ax = plt.subplots()
    ax.set_title("Runtime on ArgoUML Subsets", fontsize=title_size)
    labels = []

    x_labels = ["1"]
    for k in range(5, 101, 5):
        if k % 10 == 0:
            x_labels.append(k)
        else:
            x_labels.append("")
    y_labels = [str(x) + "s" for x in range(0, 200001, 500)]

    for index, method in enumerate(methods):
        if method not in results_per_method:
            continue
        method_statistics = results_per_method[method]  # type: MethodStatistics
        name = get_real_name(method)

        runtime_vector = []
        for dataset in datasets:
            if not method_statistics.has_result(dataset):
                break
            # Get the statistics of the current method on this dataset
            result_stats = method_statistics.get_result(dataset)

            # Append the mean runtime of the current dataset to the list
            # runtime_vector.append(numpy.average(result_stats.lst_runtime) / 60)
            runtime_vector.append(numpy.average(result_stats.lst_runtime))

        # Plot the results of the current method
        ax.plot(runtime_vector, color=colors[index])

        if use_log:
            plt.yscale('log')
        else:
            plt.ylim(0, 3600)

        def major_formatter(x, pos):
            if x < 1:
                return "%.2f" % x
            elif x < 100:
                return "%.0f" % x
            else:
                return "%.0f" % x

        ax.yaxis.set_major_formatter(FuncFormatter(major_formatter))

        plt.xticks(range(0, len(datasets)), x_labels)
        #plt.yticks(range(0, 2), y_labels)
        plt.xlabel("Size of ArgoUML Subsets in Percent (%)", fontsize=axis_label_size)
        if use_log:
            plt.ylabel("Runtime in Seconds (log)", fontsize=axis_label_size)
        else:
            plt.ylabel("Runtime in Seconds", fontsize=axis_label_size)
        labels.append(mpatches.Patch(color=colors[index], label=name))
        if use_legend:
            if use_log:
                plt.legend(handles=labels, loc=4, prop={'size': legend_size})
            else:
                plt.legend(handles=labels, loc=2, prop={'size': legend_size})

    # Show the plot
    ax.tick_params(axis='x', labelsize=tick_size)
    ax.tick_params(axis='y', labelsize=tick_size)
    plt.tight_layout()
    # plt.show()
    fig.savefig(save_dir + fig_label + ".png")


def create_generic_plot_argouml(fig_label, methods: [], datasets: [], results_per_method: {}, parameter: str, use_legend=False):
    fig, ax = plt.subplots()
    ax.set_title(parameter + " on ArgoUML Subsets", fontsize=title_size)
    labels = []

    x_labels = ["1"]
    for k in range(5, 101, 5):
        if k % 10 == 0:
            x_labels.append(k)
        else:
            x_labels.append("")

    percentages = [1]
    percentages.extend([x for x in range(10, 101, 10)])

    for index, method in enumerate(methods):
        if method not in results_per_method:
            continue
        method_statistics = results_per_method[method]  # type: MethodStatistics
        name = get_real_name(method)

        value_vector = []
        for dataset_id, dataset in enumerate(datasets):
            if not method_statistics.has_result(dataset):
                break
            # Get the statistics of the current method on this dataset
            result_stats = method_statistics.get_result(dataset)

            # Append the mean of the current dataset to the list
            value_vector.append(result_stats.get_average(parameter))

        # Plot the results of the current method
        ax.plot(value_vector, color=colors[index])
        plt.xticks(range(0, len(datasets)), x_labels)
        plt.xlabel("Size of ArgoUML Subsets in Percent (%)", fontsize=axis_label_size)
        plt.ylabel(parameter, fontsize=axis_label_size)
        plt.ylim(0, 1)
        labels.append(mpatches.Patch(color=colors[index], label=name))
        if use_legend:
            plt.legend(handles=labels, loc=4, prop={'size': legend_size})

    # Show the plot
    ax.tick_params(axis='x', labelsize=tick_size)
    ax.tick_params(axis='y', labelsize=tick_size)
    plt.tight_layout()
    # plt.show()
    fig.savefig(save_dir + fig_label + ".png")
