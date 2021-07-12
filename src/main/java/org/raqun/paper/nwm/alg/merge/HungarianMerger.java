package org.raqun.paper.nwm.alg.merge;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;

import org.raqun.paper.nwm.alg.Matchable;
import org.raqun.paper.nwm.alg.pair.PairWiseMatch;
import org.raqun.paper.nwm.common.AlgoUtil;
import org.raqun.paper.nwm.common.N_WAY;
import org.raqun.paper.nwm.domain.Element;
import org.raqun.paper.nwm.domain.Model;
import org.raqun.paper.nwm.domain.Tuple;
import org.raqun.paper.nwm.execution.RunResult;


public class HungarianMerger extends Merger implements Matchable {

    private Model model1;
    private Model model2;
    private Tuple[][] tuplesMatrix;
    private double[][] costMatrix;
    protected BigDecimal weight;
    protected ArrayList<Tuple> tuplesInMatch;
    protected int numOfModels;

    public HungarianMerger(Model m1, Model m2, int mdls) {
        super();
        model1 = m1;
        model2 = m2;
        numOfModels = mdls;
        models.add(model1);
        models.add(model2);
    }

    public void runPairing() {
        prepareCostMatrix();
        int[] match = calcMatch();
        transformMatchToTuples(match);
        sumTuplesWeight();
    }

    public boolean has(Model m) {
        return model1 == m || model2 == m;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public ArrayList<Tuple> getTuplesInMatch() {
        return tuplesInMatch;
    }

    public Model getLargerModel() {
        if (model1.size() > model2.size())
            return model1;
        return model2;
    }

    public Model getSmallerModel() {
        if (model1.size() < model2.size())
            return model1;
        return model2;
    }

    public Model getModel1() {
        return model1;
    }

    public Model getModel2() {
        return model2;
    }

    public ArrayList<Model> getModels() {
        ArrayList<Model> retVal = new ArrayList<Model>();
        retVal.add(model1);
        retVal.add(model2);
        return retVal;
    }


    private int[] calcMatch() {
        HungarianAlgorithm ha = new HungarianAlgorithm(costMatrix);
        return ha.execute();
    }

    protected void sumTuplesWeight() {
        weight = BigDecimal.ZERO;
        for (Tuple t : tuplesInMatch) {
            weight = weight.add(t.calcWeight(models), N_WAY.MATH_CTX);
        }
    }

    @Override
    public void refreshResultTuplesWeight(Model merged) {
        super.refreshResultTuplesWeight(merged);
        sumTuplesWeight();
    }

    @Override
    public String toString() {
        return "tuples in match of " + model1 + " , " + model2 + " , " + this.getTuplesInMatch().size() + "\n";
    }

    public void fitlerResultBasedOnThreshold() {
        refreshResultTuplesWeight(mergeMatchedModels());
        tuplesInMatch = PairWiseMatch.filterTuplesByTreshold(tuplesInMatch, models);
    }

    private void transformMatchToTuples(int[] match) {
        // value j at index i at the match array means that the tuple at tuplesMatrix[i][j] is in the match
        tuplesInMatch = new ArrayList<Tuple>();
        //HashSet<Element> elementsUsedInMatch = new HashSet<Element>();
        for (int i = 0; i < match.length; i++) {
            int j = match[i];
            if (j < 0)
                continue;
            Tuple t = tuplesMatrix[i][j];
            if (!AlgoUtil.isNonValidTuple(t)) {
                tuplesInMatch.add(t);
                //elementsUsedInMatch.addAll(t.getElements()); //  taking the elements as represented at the models and not the real elements
            }
        }

        Collections.sort(tuplesInMatch, new Comparator<Tuple>() {
            @Override
            public int compare(Tuple t1, Tuple t2) {
                // TODO Auto-generated method stub
                return t1.toString().compareTo(t2.toString());
            }
        });
    }

    private void prepareCostMatrix() {
        double maxWeight = generatePairWiseMatrix().doubleValue();
        costMatrix = new double[tuplesMatrix.length][tuplesMatrix[0].length];
        for (int i = 0; i < costMatrix.length; i++) {
            for (int j = 0; j < costMatrix[0].length; j++) {
                // HA is looking for the min. In order to use it to look for the max, we set each cell
                // in the matrix to be the distance between the maxVal and the cell value
                // then HA will return a result that is the set of tuples that compose the minimal distance, or the maximal tuples values sum
                costMatrix[i][j] = maxWeight - tuplesMatrix[i][j].getWeight().doubleValue();
            }
        }
    }

    private BigDecimal generatePairWiseMatrix() { // at each cell of the matrix there is a tuple
        //.out.println("generatePairWiseMatrix:: "+model1.getId()+ " ,  "+model2.getId());
        ArrayList<Element> elems1 = model1.getElements();
        ArrayList<Element> elems2 = model2.getElements();

//		Comparator<Element> cmp = new Comparator<Element>() {
//			
//			@Override
//			public int compare(Element e1, Element e2) {
//				return -1 * e1.toPrint().compareTo(e2.toPrint());
//			}
//		};

        //	Collections.sort(elems1, cmp);

        //	Collections.sort(elems2, cmp);
        //	Collections.reverse(elems2);

        BigDecimal maxWeight = new BigDecimal(-1, N_WAY.MATH_CTX);
        BigDecimal tmpWeight;
        tuplesMatrix = new Tuple[elems1.size()][elems2.size()];
        int i = -1;
        ArrayList<Tuple> mat = new ArrayList<Tuple>();
        for (Element e1 : elems1) {
            int j = 0;
            i++;
            for (Element e2 : elems2) {

                Tuple t = buildTuple(e1, e2);
                tmpWeight = t.getWeight();
                tuplesMatrix[i][j++] = t;
                if (tmpWeight.compareTo(maxWeight) > 0)
                    maxWeight = tmpWeight;
                mat.add(t);
            }
        }
        //System.out.println("tuples before hungarian:");
        //AlgoUtil.printTuples(mat);
        return maxWeight;
    }

    protected Tuple buildTuple(Element e1, Element e2) {
        return buildTuple(e1, e2, false);
    }

    protected Tuple buildTuple(Element e1, Element e2, boolean optimize) {
        Tuple t = new Tuple();
        t.addElements(e1.getBasedUponElements());
        t.addElements(e2.getBasedUponElements());
        //int numOfModels = model1.getMergedFrom()+model2.getMergedFrom();
        t.setWeight(t.calcWeight(models));
        if (optimize || !AlgoUtil.COMPUTE_RESULTS_CLASSICALLY)
            preCalculationOptimization(t, e1, e2, numOfModels);
        return t;
    }

    protected void preCalculationOptimization(Tuple t, Element e1, Element e2, int numOfModels) {
        if (AlgoUtil.isNonValidTuple(t) ||
                !AlgoUtil.shouldCompose(t, e1, e2, models) ||
                !haveCommonProperty(e1.getBasedUponElements(), e2.getBasedUponElements()) ||
                areNeighnoringElements(e1, e2))
            t.setWeight(BigDecimal.ZERO); // making sure that this tuple will not be selected
    }

    private boolean areNeighnoringElements(Element e1, Element e2) {

        HashSet<String> models1 = new HashSet<String>();
        for (Element e : e1.getBasedUponElements()) {
            models1.add(e.getModelId());
        }

        for (Element bue : e2.getBasedUponElements()) {
            if (models1.contains(bue.getModelId()))
                return true;
        }

        if (AlgoUtil.areNeighbours(e1.getContaingTuple(), e2.getContaingTuple()))
            return true;
        return false;
    }

    private boolean haveCommonProperty(ArrayList<Element> elemsOfT1, ArrayList<Element> elemsOfT2) {
//		HashSet<String> props = new HashSet<String>();
//		for(Element e1:elemsOfT1){
//			props.addAll(e1.getProperties());
//		}
//		for(Element e2: elemsOfT2){
//			for(String prp:e2.getProperties()){
//				if(props.contains(prp))
//					return true;
//			}
//		}
//		return false;
        return true;
    }

    private class HungarianAlgorithm {
        private final double[][] costMatrix;
        private final int rows, cols, dim;
        private final double[] labelByWorker, labelByJob;
        private final int[] minSlackWorkerByJob;
        private final double[] minSlackValueByJob;
        private final int[] matchJobByWorker, matchWorkerByJob;
        private final int[] parentWorkerByCommittedJob;
        private final boolean[] committedWorkers;

        /**
         * Construct an instance of the algorithm.
         *
         * @param costMatrix the cost matrix, where matrix[i][j] holds the cost of
         *                   assigning worker i to job j, for all i, j. The cost matrix
         *                   must not be irregular in the sense that all rows must be the
         *                   same length.
         */
        public HungarianAlgorithm(double[][] costMatrix) {
            this.dim = Math.max(costMatrix.length, costMatrix[0].length);
            this.rows = costMatrix.length;
            this.cols = costMatrix[0].length;
            this.costMatrix = new double[this.dim][this.dim];
            for (int w = 0; w < this.dim; w++) {
                if (w < costMatrix.length) {
                    if (costMatrix[w].length != this.cols) {
                        throw new IllegalArgumentException("Irregular cost matrix");
                    }
                    this.costMatrix[w] = Arrays.copyOf(costMatrix[w], this.dim);
                } else {
                    this.costMatrix[w] = new double[this.dim];
                }
            }
            labelByWorker = new double[this.dim];
            labelByJob = new double[this.dim];
            minSlackWorkerByJob = new int[this.dim];
            minSlackValueByJob = new double[this.dim];
            committedWorkers = new boolean[this.dim];
            parentWorkerByCommittedJob = new int[this.dim];
            matchJobByWorker = new int[this.dim];
            Arrays.fill(matchJobByWorker, -1);
            matchWorkerByJob = new int[this.dim];
            Arrays.fill(matchWorkerByJob, -1);
        }

        /**
         * Compute an initial feasible solution by assigning zero labels to the
         * workers and by assigning to each job a label equal to the minimum cost
         * among its incident edges.
         */
        protected void computeInitialFeasibleSolution() {
            for (int j = 0; j < dim; j++) {
                labelByJob[j] = Double.POSITIVE_INFINITY;
            }
            for (int w = 0; w < dim; w++) {
                for (int j = 0; j < dim; j++) {
                    if (costMatrix[w][j] < labelByJob[j]) {
                        labelByJob[j] = costMatrix[w][j];
                    }
                }
            }
        }

        /**
         * Execute the algorithm.
         *
         * @return the minimum cost matching of workers to jobs based upon the
         * provided cost matrix. A matching value of -1 indicates that the
         * corresponding worker is unassigned.
         */
        public int[] execute() {
            /*
             * Heuristics to improve performance: Reduce rows and columns by their
             * smallest element, compute an initial non-zero dual feasible solution
             * and create a greedy matching from workers to jobs of the cost matrix.
             */
            reduce();
            computeInitialFeasibleSolution();
            greedyMatch();

            int w = fetchUnmatchedWorker();
            while (w < dim) {
                initializePhase(w);
                executePhase();
                w = fetchUnmatchedWorker();
            }
            int[] result = Arrays.copyOf(matchJobByWorker, rows);
            for (w = 0; w < result.length; w++) {
                if (result[w] >= cols) {
                    result[w] = -1;
                }
            }
            return result;
        }

        /**
         * Execute a single phase of the algorithm. A phase of the Hungarian
         * algorithm consists of building a set of committed workers and a set of
         * committed jobs from a root unmatched worker by following alternating
         * unmatched/matched zero-slack edges. If an unmatched job is encountered,
         * then an augmenting path has been found and the matching is grown. If the
         * connected zero-slack edges have been exhausted, the labels of committed
         * workers are increased by the minimum slack among committed workers and
         * non-committed jobs to create more zero-slack edges (the labels of
         * committed jobs are simultaneously decreased by the same amount in order
         * to maintain a feasible labeling).
         * <p>
         * <p>
         * The runtime of a single phase of the algorithm is O(n^2), where n is the
         * dimension of the internal square cost matrix, since each edge is visited
         * at most once and since increasing the labeling is accomplished in time
         * O(n) by maintaining the minimum slack values among non-committed jobs.
         * When a phase completes, the matching will have increased in size.
         */
        protected void executePhase() {
            while (true) {
                int minSlackWorker = -1, minSlackJob = -1;
                double minSlackValue = Double.POSITIVE_INFINITY;
                for (int j = 0; j < dim; j++) {
                    if (parentWorkerByCommittedJob[j] == -1) {
                        if (minSlackValueByJob[j] < minSlackValue) {
                            minSlackValue = minSlackValueByJob[j];
                            minSlackWorker = minSlackWorkerByJob[j];
                            minSlackJob = j;
                        }
                    }
                }
                if (minSlackValue > 0) {
                    updateLabeling(minSlackValue);
                }
                parentWorkerByCommittedJob[minSlackJob] = minSlackWorker;
                if (matchWorkerByJob[minSlackJob] == -1) {
                    /*
                     * An augmenting path has been found.
                     */
                    int committedJob = minSlackJob;
                    int parentWorker = parentWorkerByCommittedJob[committedJob];
                    while (true) {
                        int temp = matchJobByWorker[parentWorker];
                        match(parentWorker, committedJob);
                        committedJob = temp;
                        if (committedJob == -1) {
                            break;
                        }
                        parentWorker = parentWorkerByCommittedJob[committedJob];
                    }
                    return;
                } else {
                    /*
                     * Update slack values since we increased the size of the
                     * committed workers set.
                     */
                    int worker = matchWorkerByJob[minSlackJob];
                    committedWorkers[worker] = true;
                    for (int j = 0; j < dim; j++) {
                        if (parentWorkerByCommittedJob[j] == -1) {
                            double slack = costMatrix[worker][j]
                                    - labelByWorker[worker] - labelByJob[j];
                            if (minSlackValueByJob[j] > slack) {
                                minSlackValueByJob[j] = slack;
                                minSlackWorkerByJob[j] = worker;
                            }
                        }
                    }
                }
            }
        }

        /**
         * @return the first unmatched worker or {@link #dim} if none.
         */
        protected int fetchUnmatchedWorker() {
            int w;
            for (w = 0; w < dim; w++) {
                if (matchJobByWorker[w] == -1) {
                    break;
                }
            }
            return w;
        }

        /**
         * Find a valid matching by greedily selecting among zero-cost matchings.
         * This is a heuristic to jump-start the augmentation algorithm.
         */
        protected void greedyMatch() {
            for (int w = 0; w < dim; w++) {
                for (int j = 0; j < dim; j++) {
                    if (matchJobByWorker[w] == -1
                            && matchWorkerByJob[j] == -1
                            && costMatrix[w][j] - labelByWorker[w] - labelByJob[j] == 0) {
                        match(w, j);
                    }
                }
            }
        }

        /**
         * Initialize the next phase of the algorithm by clearing the committed
         * workers and jobs sets and by initializing the slack arrays to the values
         * corresponding to the specified root worker.
         *
         * @param w the worker at which to root the next phase.
         */
        protected void initializePhase(int w) {
            Arrays.fill(committedWorkers, false);
            Arrays.fill(parentWorkerByCommittedJob, -1);
            committedWorkers[w] = true;
            for (int j = 0; j < dim; j++) {
                minSlackValueByJob[j] = costMatrix[w][j] - labelByWorker[w]
                        - labelByJob[j];
                minSlackWorkerByJob[j] = w;
            }
        }

        /**
         * Helper method to record a matching between worker w and job j.
         */
        protected void match(int w, int j) {
            matchJobByWorker[w] = j;
            matchWorkerByJob[j] = w;
        }

        /**
         * Reduce the cost matrix by subtracting the smallest element of each row
         * from all elements of the row as well as the smallest element of each
         * column from all elements of the column. Note that an optimal assignment
         * for a reduced cost matrix is optimal for the original cost matrix.
         */
        protected void reduce() {
            for (int w = 0; w < dim; w++) {
                double min = Double.POSITIVE_INFINITY;
                for (int j = 0; j < dim; j++) {
                    if (costMatrix[w][j] < min) {
                        min = costMatrix[w][j];
                    }
                }
                for (int j = 0; j < dim; j++) {
                    costMatrix[w][j] -= min;
                }
            }
            double[] min = new double[dim];
            for (int j = 0; j < dim; j++) {
                min[j] = Double.POSITIVE_INFINITY;
            }
            for (int w = 0; w < dim; w++) {
                for (int j = 0; j < dim; j++) {
                    if (costMatrix[w][j] < min[j]) {
                        min[j] = costMatrix[w][j];
                    }
                }
            }
            for (int w = 0; w < dim; w++) {
                for (int j = 0; j < dim; j++) {
                    costMatrix[w][j] -= min[j];
                }
            }
        }

        /**
         * Update labels with the specified slack by adding the slack value for
         * committed workers and by subtracting the slack value for committed jobs.
         * In addition, update the minimum slack values appropriately.
         */
        protected void updateLabeling(double slack) {
            for (int w = 0; w < dim; w++) {
                if (committedWorkers[w]) {
                    labelByWorker[w] += slack;
                }
            }
            for (int j = 0; j < dim; j++) {
                if (parentWorkerByCommittedJob[j] != -1) {
                    labelByJob[j] -= slack;
                } else {
                    minSlackValueByJob[j] -= slack;
                }
            }
        }
    }

    @Override
    protected Matchable getMatch() {
        return this;
    }

    public boolean isOf(String id1, String id2) {
        // TODO Auto-generated method stub
        String m1Id = model1.getId();
        String m2Id = model2.getId();
        if (m1Id.equals(id1) && m2Id.equals(id2)) return true;
        if (m1Id.equals(id2) && m2Id.equals(id1)) return true;
        return false;
    }

    @Override
    public RunResult getRunResult(int numOfModels) {
        // TODO Auto-generated method stub
        return null;
    }

}




