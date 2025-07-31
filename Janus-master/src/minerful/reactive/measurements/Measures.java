package minerful.reactive.measurements;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;


/**
 * Class containing the measurement functions.
 * <p>
 * Most of the measures are taken from :
 * Geng, Liqiang, and Howard J. Hamilton. ‘Interestingness Measures for Data Mining: A Survey’. ACM Computing Surveys 38, no. 3 (30 September 2006): 9-es. https://doi.org/10.1145/1132960.1132963.
 */
public class Measures {
    static float[] currentTraceProbability = new float[9];  // performaces optimization: creating a new temporary array for each measurement makes the GC go in overhead with big datasets

    //    	TODO improve this hard-code shame
    public static String[] MEASURE_NAMES = {
            "Support",      // 0
            "Confidence",   // 1
            "Recall",       // 2
            "Lovinger",     // 3
            "Specificity",  // 4
            "Accuracy",     // 5
            "Lift",         // 6
            "Leverage",     // 7
            "Compliance",   // 8
            "Odds Ratio",   // 9
            "Gini Index",   // 10
            "Certainty factor",   // 11
            "Coverage",     // 12
            "Prevalence",     // 13
            "Added Value",     // 14
            "Relative Risk",     // 15
            "Jaccard",     // 16
            "Ylue Q",     // 17
            "Ylue Y",     // 18
            "Klosgen",     // 19
            "Conviction",     // 20
            "Interestingness Weighting Dependency",     // 21
            "Collective Strength",     // 22
            "Laplace Correction",     // 23
            "J Measure",     // 24
            "One-way Support",     // 25
            "Two-way Support",     // 26
            "Two-way Support Variation",     // 27
            "Linear Correlation Coefficient",     // 28
            "Piatetsky-Shapiro",     // 29
            "Cosine",     // 30
            "Information Gain",     // 31
            "Sebag-Schoenauer",     // 32
            "Least Contradiction",     // 33
            "Odd Multiplier",     // 34
            "Example and Counterexample Rate",     // 35
            "Zhang"     // 36
    };

    //    	TODO improve this hard-code shame
    public static int MEASURE_NUM = MEASURE_NAMES.length;

    /**
     * Retrieves the name of a measure given its index
     *
     * @param measureIndex
     * @return
     */
    public static String getMeasureName(int measureIndex) {
        return MEASURE_NAMES[measureIndex];
    }

    /**
     * Retrieve the index of a measure given its name.
     * WARNING case-sensitive
     *
     * @param measureName
     * @return
     */
    public static int getMeasureIndex(String measureName) {
//        TODO make a stronger search not case-sensitive, ignoring spaces and symbols like bars
        return ArrayUtils.indexOf(MEASURE_NAMES, measureName);
    }

    public static float getTraceMeasure(byte[] reactiveConstraintEvaluation, int measureIndex, boolean nanTraceSubstituteFlag, double nanTraceSubstituteValue) {
        float[] traceProbabilities = getTraceProbabilities(reactiveConstraintEvaluation);

        float result = getTraceMeasure(traceProbabilities, measureIndex);

        // according to the input setting, substitute the measure value if it is NaN
        if (nanTraceSubstituteFlag && Float.isNaN(result))
            return (float) nanTraceSubstituteValue;

        return result;
    }

    public static float getTraceMeasure(int[] traceEvaluation, int measureIndex, boolean nanTraceSubstituteFlag, double nanTraceSubstituteValue) {
        float[] traceProbabilities = getTraceProbabilities(traceEvaluation);

        float result = getTraceMeasure(traceProbabilities, measureIndex);

        // according to the input setting, substitute the measure value if it is NaN
        if (nanTraceSubstituteFlag && Float.isNaN(result))
            return (float) nanTraceSubstituteValue;

        return result;
    }

//    /**
//     * LEGACY Generic method to return  the trace measure for a specific measure using the single events evaluation.
//     * <p>
//     * The usage of this function is intended for batch measurement involving all measures, to avoid to call them one by one.
//     *
//     * @param reactiveConstraintEvaluation
//     * @param measureIndex
//     * @return
//     */
//    @Deprecated
//    public static double getTraceMeasure(byte[] reactiveConstraintEvaluation, int measureIndex, boolean nanTraceSubstituteFlag, double nanTraceSubstituteValue) {
//        //    	TODO improve this hard-code shame
//        double result = 0;
//        switch (measureIndex) {
//            case 0:
////				support
//                result = getTraceSupport(reactiveConstraintEvaluation);
//                break;
//            case 1:
////				confidence
//                result = getTraceConfidence(reactiveConstraintEvaluation);
//                break;
//            case 2:
////				recall
//                result = getTraceRecall(reactiveConstraintEvaluation);
//                break;
//            case 3:
////				Lovinger
//                result = getTraceLovinger(reactiveConstraintEvaluation);
//                break;
//            case 4:
////				Specificity
//                result = getTraceSpecificity(reactiveConstraintEvaluation);
//                break;
//            case 5:
////				Accuracy
//                result = getTraceAccuracy(reactiveConstraintEvaluation);
//                break;
//            case 6:
////				Accuracy
//                result = getTraceLift(reactiveConstraintEvaluation);
//                break;
//            case 7:
////				Leverage
//                result = getTraceLeverage(reactiveConstraintEvaluation);
//                break;
//            case 8:
////				Compliance
//                result = getTraceCompliance(reactiveConstraintEvaluation);
//                break;
//            case 9:
////				Odds Ratio
//                result = getTraceOddsRatio(reactiveConstraintEvaluation);
//                break;
//            case 10:
////				Gini Index
//                result = getTraceGiniIndex(reactiveConstraintEvaluation);
//                break;
//            case 11:
////				Certainty factor
//                result = getTraceCertaintyFactor(reactiveConstraintEvaluation);
//                break;
//            case 12:
////				Coverage
//                result = getTraceCoverage(reactiveConstraintEvaluation);
//                break;
//            case 13:
////				Prevalence
//                result = getTracePrevalence(reactiveConstraintEvaluation);
//                break;
//            case 14:
////				Added Value
//                result = getTraceAddedValue(reactiveConstraintEvaluation);
//                break;
//            case 15:
////				Relative Risk
//                result = getTraceRelativeRisk(reactiveConstraintEvaluation);
//                break;
//            case 16:
////				Jaccard
//                result = getTraceJaccard(reactiveConstraintEvaluation);
//                break;
//            case 17:
////				Ylue Q
//                result = getTraceYlueQ(reactiveConstraintEvaluation);
//                break;
//            case 18:
////				Ylue Y
//                result = getTraceYlueY(reactiveConstraintEvaluation);
//                break;
//            case 19:
////				Klosgen
//                result = getTraceKlosgen(reactiveConstraintEvaluation);
//                break;
//            case 20:
////				Conviction
//                result = getTraceConviction(reactiveConstraintEvaluation);
//                break;
//            case 21:
////				Interestingness Weighting Dependency
//                result = getTraceInterestingnessWeightingDependency(reactiveConstraintEvaluation);
//                break;
//            case 22:
////				Collective Strength
//                result = getTraceCollectiveStrength(reactiveConstraintEvaluation);
//                break;
//            case 23:
////				Laplace Correction
//                result = getTraceLaplaceCorrection(reactiveConstraintEvaluation);
//                break;
//            case 24:
////				J Measure
//                result = getTraceJMeasure(reactiveConstraintEvaluation);
//                break;
//            case 25:
////				One-way Support
//                result = getTraceOneWaySupport(reactiveConstraintEvaluation);
//                break;
//            case 26:
////				Two-way Support
//                result = getTraceTwoWaySupport(reactiveConstraintEvaluation);
//                break;
//            case 27:
////				Two-way Support Variation
//                result = getTraceTwoWaySupportVariation(reactiveConstraintEvaluation);
//                break;
//            case 28:
////				Linear Correlation Coefficient
//                result = getTraceLinearCorrelationCoefficient(reactiveConstraintEvaluation);
//                break;
//            case 29:
////				Piatetsky-Shapiro
//                result = getTracePiatetskyShapiro(reactiveConstraintEvaluation);
//                break;
//            case 30:
////				Cosine
//                result = getTraceCosine(reactiveConstraintEvaluation);
//                break;
//            case 31:
////				Information Gain
//                result = getTraceInformationGain(reactiveConstraintEvaluation);
//                break;
//            case 32:
////				Sebag-Schoenauer
//                result = getTraceSebagSchoenauer(reactiveConstraintEvaluation);
//                break;
//            case 33:
////				Least Contradiction
//                result = getTraceLeastContradiction(reactiveConstraintEvaluation);
//                break;
//            case 34:
////				Odd Multiplier
//                result = getTraceOddMultiplier(reactiveConstraintEvaluation);
//                break;
//            case 35:
////				Example and Counterexample Rate
//                result = getTraceExampleCounterexampleRate(reactiveConstraintEvaluation);
//                break;
//            case 36:
////				Zhang
//                result = getTraceZhang(reactiveConstraintEvaluation);
//                break;
//        }
//
//        // according to the input setting, substitute the measure value if it is NaN
//        if (nanTraceSubstituteFlag && Double.isNaN(result))
//            return nanTraceSubstituteValue;
//
//        return result;
//
//    }

    /**
     * Generic method to return  the log measure for a specific measure using the already computed log probabilities.
     * <p>
     * The usage of this function is intended for batch measurement involving all measures, to avoid to call them one by one.
     *
     * @param logProbabilities
     * @param measureIndex
     * @return
     */
    public static float getLogMeasure(float[] logProbabilities, int measureIndex) {
        /* TODO traces and log measures shares the same formulae,
            you just have to compute the trace/log probabilities in different way.
            Refactor functions to reflect this */
        return getTraceMeasure(logProbabilities, measureIndex);
    }

    /**
     * Generic method to return  the trace measure for a specific measure using the already computed trace probabilities.
     * <p>
     * The usage of this function is intended for batch measurement involving all measures, to avoid to call them one by one.
     *
     * @param traceProbabilities
     * @param measureIndex
     * @return
     */
    public static float getTraceMeasure(float[] traceProbabilities, int measureIndex) {
        //    	TODO improve this hard-code shame
        float result = 0;
        switch (measureIndex) {
            case 0:
//				support
                result = getTraceSupport(traceProbabilities);
                break;
            case 1:
//				confidence
                result = getTraceConfidence(traceProbabilities);
                break;
            case 2:
//				recall
                result = getTraceRecall(traceProbabilities);
                break;
            case 3:
//				Lovinger
                result = getTraceLovinger(traceProbabilities);
                break;
            case 4:
//				Specificity
                result = getTraceSpecificity(traceProbabilities);
                break;
            case 5:
//				Accuracy
                result = getTraceAccuracy(traceProbabilities);
                break;
            case 6:
//				Accuracy
                result = getTraceLift(traceProbabilities);
                break;
            case 7:
//				Leverage
                result = getTraceLeverage(traceProbabilities);
                break;
            case 8:
//				Compliance
                result = getTraceCompliance(traceProbabilities);
                break;
            case 9:
//				Odds Ratio
                result = getTraceOddsRatio(traceProbabilities);
                break;
            case 10:
//				Gini Index
                result = getTraceGiniIndex(traceProbabilities);
                break;
            case 11:
//				Certainty factor
                result = getTraceCertaintyFactor(traceProbabilities);
                break;
            case 12:
//				Coverage
                result = getTraceCoverage(traceProbabilities);
                break;
            case 13:
//				Prevalence
                result = getTracePrevalence(traceProbabilities);
                break;
            case 14:
//				Added Value
                result = getTraceAddedValue(traceProbabilities);
                break;
            case 15:
//				Relative Risk
                result = getTraceRelativeRisk(traceProbabilities);
                break;
            case 16:
//				Jaccard
                result = getTraceJaccard(traceProbabilities);
                break;
            case 17:
//				Ylue Q
                result = getTraceYlueQ(traceProbabilities);
                break;
            case 18:
//				Ylue Y
                result = getTraceYlueY(traceProbabilities);
                break;
            case 19:
//				Klosgen
                result = getTraceKlosgen(traceProbabilities);
                break;
            case 20:
//				Conviction
                result = getTraceConviction(traceProbabilities);
                break;
            case 21:
//				Interestingness Weighting Dependency
                result = getTraceInterestingnessWeightingDependency(traceProbabilities);
                break;
            case 22:
//				Collective Strength
                result = getTraceCollectiveStrength(traceProbabilities);
                break;
            case 23:
//				Laplace Correction
                result = getTraceLaplaceCorrection(traceProbabilities);
                break;
            case 24:
//				J Measure
                result = getTraceJMeasure(traceProbabilities);
                break;
            case 25:
//				One-way Support
                result = getTraceOneWaySupport(traceProbabilities);
                break;
            case 26:
//				Two-way Support
                result = getTraceTwoWaySupport(traceProbabilities);
                break;
            case 27:
//				Two-way Support Variation
                result = getTraceTwoWaySupportVariation(traceProbabilities);
                break;
            case 28:
//				Linear Correlation Coefficient
                result = getTraceLinearCorrelationCoefficient(traceProbabilities);
                break;
            case 29:
//				Piatetsky-Shapiro
                result = getTracePiatetskyShapiro(traceProbabilities);
                break;
            case 30:
//				Cosine
                result = getTraceCosine(traceProbabilities);
                break;
            case 31:
//				Information Gain
                result = getTraceInformationGain(traceProbabilities);
                break;
            case 32:
//				Sebag-Schoenauer
                result = getTraceSebagSchoenauer(traceProbabilities);
                break;
            case 33:
//				Least Contradiction
                result = getTraceLeastContradiction(traceProbabilities);
                break;
            case 34:
//				Odd Multiplier
                result = getTraceOddMultiplier(traceProbabilities);
                break;
            case 35:
//				Example and Counterexample Rate
                result = getTraceExampleCounterexampleRate(traceProbabilities);
                break;
            case 36:
//				Zhang
                result = getTraceZhang(traceProbabilities);
                break;
        }

        return result;

    }


    /**
     * From the events evaluation, retrieve the probabilities of both activator and target (plus their negatives) formula of a reactive constraint.
     * * i.e. P(A),P(T),P(¬A),P(¬T)
     * and the probabilities of the combinations of  activator and target formula of a reactive constraint.
     * * i.e. P(¬A¬T),P(A¬T),P(¬AT),P(¬A¬T)
     * and the lenght of the trace
     *
     * @param reactiveConstraintEvaluation
     * @return
     */
    public static float[] getTraceProbabilities(byte[] reactiveConstraintEvaluation) {
//        float[] currentTraceProbability = new float[9];
//        if (reactiveConstraintEvaluation.length == 0) return currentTraceProbability;
        if (reactiveConstraintEvaluation.length == 0) return new float[9];

        currentTraceProbability = new float[9];

        // result { 0: activation, 1: target, 2: no activation, 3: no target}
        // result {4: 00, 5: 01, , 6: 10, 7:11}
        for (byte eval : reactiveConstraintEvaluation) {
            currentTraceProbability[0] += eval / 2; // the activator is true if the byte is >1, i.e. 2 or 3
            currentTraceProbability[1] += eval % 2; // the target is true if the byte is odd, i,e, 1 or 3
            currentTraceProbability[eval + 4]++;  // eval:{0,1,2,3} bite encodes already the intersection of A and T, +4 is for the indexing
        }
        float l = reactiveConstraintEvaluation.length;
        currentTraceProbability[2] = l - currentTraceProbability[0];
        currentTraceProbability[3] = l - currentTraceProbability[1];

        currentTraceProbability[0] /= l;
        currentTraceProbability[1] /= l;
        currentTraceProbability[2] /= l;
        currentTraceProbability[3] /= l;
        currentTraceProbability[4] /= l;
        currentTraceProbability[5] /= l;
        currentTraceProbability[6] /= l;
        currentTraceProbability[7] /= l;
        currentTraceProbability[8] = l;
        return currentTraceProbability;
    }

    /**
     * From the trace evaluation, retrieve the probabilities of both activator and target (plus their negatives) formula of a reactive constraint.
     * * i.e. P(A),P(T),P(¬A),P(¬T)
     * and the probabilities of the combinations of  activator and target formula of a reactive constraint.
     * * i.e. P(¬A¬T),P(A¬T),P(¬AT),P(¬A¬T)
     * and the lenght of the trace
     *
     * @param traceEvaluation
     * @return
     */
    public static float[] getTraceProbabilities(int[] traceEvaluation) {
        float[] result = new float[9];
        if (traceEvaluation.length == 0) return result;

        float l = traceEvaluation[8];

        result[0] = traceEvaluation[0] / l;
        result[1] = traceEvaluation[1] / l;
        result[2] = traceEvaluation[2] / l;
        result[3] = traceEvaluation[3] / l;
        result[4] = traceEvaluation[4] / l;
        result[5] = traceEvaluation[5] / l;
        result[6] = traceEvaluation[6] / l;
        result[7] = traceEvaluation[7] / l;
        result[8] = l;
        return result;
    }

    /**
     * Retrieve the probabilities of both activator and target (plus their negatives) formula of a reactive constraint.
     *
     * @param reactiveConstraintEvaluation byte array of {0,1,2,3} encoding the bolean evaluation of both the activator and the target of a reactive constraint
     * @return
     */
    public static float[] getReactiveProbabilities(byte[] reactiveConstraintEvaluation) {
        float[] result = {0, 0, 0, 0};  // result { 0: activation, 1: target, 2: no activation, 3: no target}
        if (reactiveConstraintEvaluation.length == 0) return result;
        for (byte eval : reactiveConstraintEvaluation) {
            result[0] += eval / 2; // the activator is true if the byte is >1, i.e. 2 or 3
            result[1] += eval % 2; // the target is true if the byte is odd, i,e, 1 or 3
        }
        result[2] = reactiveConstraintEvaluation.length - result[0];
        result[3] = reactiveConstraintEvaluation.length - result[1];

        result[0] /= reactiveConstraintEvaluation.length;
        result[1] /= reactiveConstraintEvaluation.length;
        result[2] /= reactiveConstraintEvaluation.length;
        result[3] /= reactiveConstraintEvaluation.length;
        return result;
    }

    /**
     * Retrieve the probabilities of the combinations of  activator and target formula of a reactive constraint.
     * i.e. P(¬A¬T),P(A¬T),P(¬AT),P(¬A¬T)
     *
     * @param reactiveConstraintEvaluation byte array of {0,1,2,3} encoding the bolean evaluation of both the activator and the target of a reactive constraint
     * @return
     */
    public static float[] getReactiveIntersectionsProbabilities(byte[] reactiveConstraintEvaluation) {
        float[] result = {0, 0, 0, 0};  // result {0: 00, 1: 01, , 2: 10, 3:11}
        if (reactiveConstraintEvaluation.length == 0) return result;
        for (byte eval : reactiveConstraintEvaluation) {
            result[eval]++;
        }
        result[0] /= reactiveConstraintEvaluation.length;
        result[1] /= reactiveConstraintEvaluation.length;
        result[2] /= reactiveConstraintEvaluation.length;
        result[3] /= reactiveConstraintEvaluation.length;
        return result;
    }

    /**
     * Retrieve the probability of a formula holding true in a trace given its evaluation on the trace.
     * BEWARE: this probability is defined for a single formula, not the entire reactive constraint A->B
     *
     * @param formulaEvaluation Byte array (representing a bit array) of 0s and 1s
     * @return
     */
    public static float getFormulaProbability(byte[] formulaEvaluation) {
        if (formulaEvaluation.length == 0) return 0;
        float result = 0;
        for (byte eval : formulaEvaluation) {
            result += eval;
        }
        return result / formulaEvaluation.length;
    }

    /**
     * retrieve the support measure of a constraint for a given trace.
     * <p>
     * The support measure is defined as:
     * Supp(A->T) = P(A' intersection T') =
     *
     * @return
     */
    public static float getTraceSupport(byte[] reactiveConstraintEvaluation) {
        if (reactiveConstraintEvaluation.length == 0) return 0;
        float result = 0;
        for (byte eval : reactiveConstraintEvaluation) {
            result += eval / 3; // activator and target are both true when the byte is 3
        }
        return result / reactiveConstraintEvaluation.length;
//        float[] p = getReactiveProbabilities(reactiveConstraintEvaluation);// result { 0: activation, 1: target, 2: no activation, 3: no target}
//        float pA = p[0];
//        float pnA = p[2];
//        float pT = p[1];
//        float pnT = p[3];
//        float[] pIntersection = getReactiveIntersectionsProbabilities(reactiveConstraintEvaluation);// result {0: 00, 1: 01, , 2: 10, 3:11}
//        float pnAnT = pIntersection[0];
//        float pnAT = pIntersection[1];
//        float pAnT = pIntersection[2];
//        float pAT = pIntersection[3];
//
//        return pAT;
    }

    /**
     * retrieve the support measure of a constraint for a given trace.
     * <p>
     * The support measure is defined as:
     * Supp(A->T) = P(A' intersection T') =
     *
     * @return
     */
    public static float getTraceSupport(float[] p) {
        float pA = p[0];
        float pnA = p[2];
        float pT = p[1];
        float pnT = p[3];
        float pnAnT = p[4];
        float pnAT = p[5];
        float pAnT = p[6];
        float pAT = p[7];

        return pAT;
    }

    /**
     * retrieve the confidence of a constraint for a given trace.
     * <p>
     * The confidence measure is defined as:
     * Conf(A->T) = P(T'|A') =  P(T' intersection A') / P(A') = Supp(A'->T')/P(A')
     *
     * @return
     */
    public static float getTraceConfidence(byte[] reactiveConstraintEvaluation) {
        byte[] activatorEval = getActivatorEvaluation(reactiveConstraintEvaluation);
        float denominator = getFormulaProbability(activatorEval);
//        if (denominator == 0) return float.NaN;
        return getTraceSupport(reactiveConstraintEvaluation) / denominator;
//        float[] p = getReactiveProbabilities(reactiveConstraintEvaluation);// result { 0: activation, 1: target, 2: no activation, 3: no target}
//        float pA = p[0];
//        float pnA = p[2];
//        float pT = p[1];
//        float pnT = p[3];
//        float[] pIntersection = getReactiveIntersectionsProbabilities(reactiveConstraintEvaluation);// result {0: 00, 1: 01, , 2: 10, 3:11}
//        float pnAnT = pIntersection[0];
//        float pnAT = pIntersection[1];
//        float pAnT = pIntersection[2];
//        float pAT = pIntersection[3];
//
//        float result= pAT / pA;
//
//        if (float.isNaN(result)){
//            return 0;
//        }
//        else {
//            return result;
//        }
    }

    /**
     * retrieve the confidence of a constraint for a given trace.
     * <p>
     * The confidence measure is defined as:
     * Conf(A->T) = P(T'|A') =  P(T' intersection A') / P(A') = Supp(A'->T')/P(A')
     *
     * @return
     */
    public static float getTraceConfidence(float[] p) {
        float pA = p[0];
        float pnA = p[2];
        float pT = p[1];
        float pnT = p[3];
        float pnAnT = p[4];
        float pnAT = p[5];
        float pAnT = p[6];
        float pAT = p[7];

        float result = pAT / pA;

        return result;
    }

    /**
     * retrieve the recall of a constraint for a given trace.
     * <p>
     * The recall measure is defined as:
     * Recall(A->T) = P(A'|T') =  P(T' intersection A') / P(T') = Supp(A'->T')/P(T')
     *
     * @return
     */
    public static float getTraceRecall(byte[] reactiveConstraintEvaluation) {
        byte[] targetEval = getTargetEvaluation(reactiveConstraintEvaluation);
        float denominator = getFormulaProbability(targetEval);
//        if (denominator == 0) return 0;
        return getTraceSupport(reactiveConstraintEvaluation) / denominator;
//        float[] p = getReactiveProbabilities(reactiveConstraintEvaluation);// result { 0: activation, 1: target, 2: no activation, 3: no target}
//        float pA = p[0];
//        float pnA = p[2];
//        float pT = p[1];
//        float pnT = p[3];
//        float[] pIntersection = getReactiveIntersectionsProbabilities(reactiveConstraintEvaluation);// result {0: 00, 1: 01, , 2: 10, 3:11}
//        float pnAnT = pIntersection[0];
//        float pnAT = pIntersection[1];
//        float pAnT = pIntersection[2];
//        float pAT = pIntersection[3];
//
//        float result= pAT / pT;
//
//        if (float.isNaN(result)){
//            return 0;
//        }
//        else {
//            return result;
//        }
    }

    /**
     * retrieve the recall of a constraint for a given trace.
     * <p>
     * The recall measure is defined as:
     * Recall(A->T) = P(A'|T') =  P(T' intersection A') / P(T') = Supp(A'->T')/P(T')
     *
     * @return
     */
    public static float getTraceRecall(float[] p) {
        float pA = p[0];
        float pnA = p[2];
        float pT = p[1];
        float pnT = p[3];
        float pnAnT = p[4];
        float pnAT = p[5];
        float pAnT = p[6];
        float pAT = p[7];

        float result = pAT / pT;

        return result;
    }

    /**
     * Retrieve the Lovinger's Measure of a constraint for a given trace.
     * <p>
     * The Lovinger's measure is defined as:
     * Lov(A->T) = 1 − ((P(A)P(¬T))/P(A¬T)))
     *
     * @return
     */
    public static float getTraceLovinger(byte[] reactiveConstraintEvaluation) {
        float[] p = getReactiveProbabilities(reactiveConstraintEvaluation);// result { 0: activation, 1: target, 2: no activation, 3: no target}
        float pA = p[0];
        float pnA = p[2];
        float pT = p[1];
        float pnT = p[3];
        float[] pIntersection = getReactiveIntersectionsProbabilities(reactiveConstraintEvaluation);// result {0: 00, 1: 01, , 2: 10, 3:11}
        float pnAnT = pIntersection[0];
        float pnAT = pIntersection[1];
        float pAnT = pIntersection[2];
        float pAT = pIntersection[3];

        float result = 1 - ((pA * pnT) / (pAnT));


        return result;
    }

    /**
     * Retrieve the Lovinger's Measure of a constraint for a given trace.
     * <p>
     * The Lovinger's measure is defined as:
     * Lov(A->T) = 1 − ((P(A)P(¬T))/P(A¬T)))
     *
     * @return
     */
    public static float getTraceLovinger(float[] p) {
        float pA = p[0];
        float pnA = p[2];
        float pT = p[1];
        float pnT = p[3];
        float pnAnT = p[4];
        float pnAT = p[5];
        float pAnT = p[6];
        float pAT = p[7];

        float result = 1 - ((pA * pnT) / (pAnT));


        return result;
    }

    /**
     * Retrieve the Specificity Measure of a constraint for a given trace.
     * <p>
     * The Specificity measure is defined as:
     * Specificity(A->T) = P(¬T'|¬A') = (Conf(¬A'->¬T'))
     *
     * @return
     */
    public static float getTraceSpecificity(byte[] reactiveConstraintEvaluation) {
        return getTraceConfidence(getNegativeReactiveConstraintEvaluation(reactiveConstraintEvaluation));
//        TODO test the validity of this function with experiment del:e-Response(e,f)
//        float[] p = getReactiveProbabilities(reactiveConstraintEvaluation);// result { 0: activation, 1: target, 2: no activation, 3: no target}
//        float pA = p[0];
//        float pnA = p[2];
//        float pT = p[1];
//        float pnT = p[3];
//        float[] pIntersection = getReactiveIntersectionsProbabilities(reactiveConstraintEvaluation);// result {0: 00, 1: 01, , 2: 10, 3:11}
//        float pnAnT = pIntersection[0];
//        float pnAT = pIntersection[1];
//        float pAnT = pIntersection[2];
//        float pAT = pIntersection[3];
//
//        float result= pnAnT / pnA;
//
//        if (float.isNaN(result)){
//            return 0;
//        }
//        else {
//            return result;
//        }
    }

    /**
     * Retrieve the Specificity Measure of a constraint for a given trace.
     * <p>
     * The Specificity measure is defined as:
     * Specificity(A->T) = P(¬T'|¬A') = (Conf(¬A'->¬T'))
     *
     * @return
     */
    public static float getTraceSpecificity(float[] p) {
        float pA = p[0];
        float pnA = p[2];
        float pT = p[1];
        float pnT = p[3];
        float pnAnT = p[4];
        float pnAT = p[5];
        float pAnT = p[6];
        float pAT = p[7];

        float result = pnAnT / pnA;

        return result;
    }

    /**
     * Retrieve the Accuracy Measure of a constraint for a given trace.
     * <p>
     * The Accuracy measure is defined as:
     * Accuracy(A->T) = P(T' intersection A') + P(¬T' intersection ¬A') = (Supp(A'->T') + Supp(¬A'->¬T'))
     *
     * @return
     */
    public static float getTraceAccuracy(byte[] reactiveConstraintEvaluation) {
        return getTraceSupport(reactiveConstraintEvaluation) + getTraceSupport(getNegativeReactiveConstraintEvaluation(reactiveConstraintEvaluation));
//        float[] p = getReactiveProbabilities(reactiveConstraintEvaluation);// result { 0: activation, 1: target, 2: no activation, 3: no target}
//        float pA = p[0];
//        float pnA = p[2];
//        float pT = p[1];
//        float pnT = p[3];
//        float[] pIntersection = getReactiveIntersectionsProbabilities(reactiveConstraintEvaluation);// result {0: 00, 1: 01, , 2: 10, 3:11}
//        float pnAnT = pIntersection[0];
//        float pnAT = pIntersection[1];
//        float pAnT = pIntersection[2];
//        float pAT = pIntersection[3];
//
//        return pAT + pnAnT;
    }

    /**
     * Retrieve the Accuracy Measure of a constraint for a given trace.
     * <p>
     * The Accuracy measure is defined as:
     * Accuracy(A->T) = P(T' intersection A') + P(¬T' intersection ¬A') = (Supp(A'->T') + Supp(¬A'->¬T'))
     *
     * @return
     */
    public static float getTraceAccuracy(float[] p) {
        float pA = p[0];
        float pnA = p[2];
        float pT = p[1];
        float pnT = p[3];
        float pnAnT = p[4];
        float pnAT = p[5];
        float pAnT = p[6];
        float pAT = p[7];

        float result = pAT + pnAnT;

        return result;
    }

    /**
     * Retrieve the Lift Measure of a constraint for a given trace.
     * <p>
     * The Lift measure is defined as:
     * Specificity(A->T) = P(T'|A') / P(T') = (Conf(A'->T') / P(T'))
     *
     * @return
     */
    public static float getTraceLift(byte[] reactiveConstraintEvaluation) {
        byte[] targetEval = getTargetEvaluation(reactiveConstraintEvaluation);
        float denominator = getFormulaProbability(targetEval);
//        if (denominator == 0) return 0;
        return getTraceConfidence(reactiveConstraintEvaluation) / denominator;
//        float[] p = getReactiveProbabilities(reactiveConstraintEvaluation);// result { 0: activation, 1: target, 2: no activation, 3: no target}
//        float pA = p[0];
//        float pnA = p[2];
//        float pT = p[1];
//        float pnT = p[3];
//        float[] pIntersection = getReactiveIntersectionsProbabilities(reactiveConstraintEvaluation);// result {0: 00, 1: 01, , 2: 10, 3:11}
//        float pnAnT = pIntersection[0];
//        float pnAT = pIntersection[1];
//        float pAnT = pIntersection[2];
//        float pAT = pIntersection[3];
//
//        float result= pAT / (pA * pT);
//
//        if (float.isNaN(result)){
//            return 0;
//        }
//        else {
//            return result;
//        }
    }

    /**
     * Retrieve the Lift Measure of a constraint for a given trace.
     * <p>
     * The Lift measure is defined as:
     * Specificity(A->T) = P(T'|A') / P(T') = (Conf(A'->T') / P(T'))
     *
     * @return
     */
    public static float getTraceLift(float[] p) {
        float pA = p[0];
        float pnA = p[2];
        float pT = p[1];
        float pnT = p[3];
        float pnAnT = p[4];
        float pnAT = p[5];
        float pAnT = p[6];
        float pAT = p[7];

        float result = pAT / (pA * pT);

        return result;
    }

    /**
     * Retrieve the Leverage Measure of a constraint for a given trace.
     * <p>
     * The Leverage measure is defined as:
     * Specificity(A->T) = P(T'|A') - P(A')P(T') = (Conf(A'->T') - P(A')P(T'))
     *
     * @return
     */
    public static float getTraceLeverage(byte[] reactiveConstraintEvaluation) {
        byte[] activatorEval = getActivatorEvaluation(reactiveConstraintEvaluation);
        byte[] targetEval = getTargetEvaluation(reactiveConstraintEvaluation);
        float pA = getFormulaProbability(activatorEval);
        float pT = getFormulaProbability(targetEval);
        return getTraceConfidence(reactiveConstraintEvaluation) - (pA * pT);
//        float[] p = getReactiveProbabilities(reactiveConstraintEvaluation);// result { 0: activation, 1: target, 2: no activation, 3: no target}
//        float pA = p[0];
//        float pnA = p[2];
//        float pT = p[1];
//        float pnT = p[3];
//        float[] pIntersection = getReactiveIntersectionsProbabilities(reactiveConstraintEvaluation);// result {0: 00, 1: 01, , 2: 10, 3:11}
//        float pnAnT = pIntersection[0];
//        float pnAT = pIntersection[1];
//        float pAnT = pIntersection[2];
//        float pAT = pIntersection[3];
//
//        float result= (pAT / pA) - pA * pT;
//
//        if (float.isNaN(result)){
//            return 0;
//        }
//        else {
//            return result;
//        }
    }

    /**
     * Retrieve the Leverage Measure of a constraint for a given trace.
     * <p>
     * The Leverage measure is defined as:
     * Specificity(A->T) = P(T'|A') - P(A')P(T') = (Conf(A'->T') - P(A')P(T'))
     *
     * @return
     */
    public static float getTraceLeverage(float[] p) {
        float pA = p[0];
        float pnA = p[2];
        float pT = p[1];
        float pnT = p[3];
        float pnAnT = p[4];
        float pnAT = p[5];
        float pAnT = p[6];
        float pAT = p[7];

        float result = (pAT / pA) - (pA * pT);

        return result;
    }

    /**
     * ORIGINAL MEASURE! Retrieve the Compliance Measure of a constraint for a given trace.
     * We developed this measure to emulate the original support intuition,
     * i.e., the percentage of the trace which do not conflict with the constraint.
     * Thus we count all the points except the active violations (activation true but target false)
     * <p>
     * The Compliance measure is defined as:
     * Compliance(A->T) = 1 - P(A' intersection ¬T')
     *
     * @return
     */
    public static float getTraceCompliance(byte[] reactiveConstraintEvaluation) {
        if (reactiveConstraintEvaluation.length == 0) return 0;
        float result = 0;
        for (byte eval : reactiveConstraintEvaluation) {
            if (eval == 2) { // activator true but target false when byte equal to 2
                result++;
            }
        }
        return 1 - result / reactiveConstraintEvaluation.length;
//        float[] p = getReactiveProbabilities(reactiveConstraintEvaluation);// result { 0: activation, 1: target, 2: no activation, 3: no target}
//        float pA = p[0];
//        float pnA = p[2];
//        float pT = p[1];
//        float pnT = p[3];
//        float[] pIntersection = getReactiveIntersectionsProbabilities(reactiveConstraintEvaluation);// result {0: 00, 1: 01, , 2: 10, 3:11}
//        float pnAnT = pIntersection[0];
//        float pnAT = pIntersection[1];
//        float pAnT = pIntersection[2];
//        float pAT = pIntersection[3];
//
//        return 1 - pAnT;
    }

    /**
     * ORIGINAL MEASURE! Retrieve the Compliance Measure of a constraint for a given trace.
     * We developed this measure to emulate the original support intuition,
     * i.e., the percentage of the trace which do not conflict with the constraint.
     * Thus we count all the points except the active violations (activation true but target false)
     * <p>
     * The Compliance measure is defined as:
     * Compliance(A->T) = 1 - P(A' intersection ¬T')
     *
     * @return
     */
    public static float getTraceCompliance(float[] p) {
        float pA = p[0];
        float pnA = p[2];
        float pT = p[1];
        float pnT = p[3];
        float pnAnT = p[4];
        float pnAT = p[5];
        float pAnT = p[6];
        float pAT = p[7];

        float result = 1 - pAnT;

        return result;
    }

    /**
     * Retrieve the Odds Ratio Measure of a constraint for a given trace.
     * <p>
     * The Odds Ratio measure is defined as:
     * OddsRatio(A->T) = ( P(A' intersection T') P(¬A' intersection ¬T') ) / ( P(A' intersection ¬T') P(¬A' intersection T') )
     *
     * @return
     */
    public static float getTraceOddsRatio(byte[] reactiveConstraintEvaluation) {
        float[] p = getReactiveProbabilities(reactiveConstraintEvaluation);// result { 0: activation, 1: target, 2: no activation, 3: no target}
        float pA = p[0];
        float pnA = p[2];
        float pT = p[1];
        float pnT = p[3];
        float[] pIntersection = getReactiveIntersectionsProbabilities(reactiveConstraintEvaluation);// result {0: 00, 1: 01, , 2: 10, 3:11}
        float pnAnT = pIntersection[0];
        float pnAT = pIntersection[1];
        float pAnT = pIntersection[2];
        float pAT = pIntersection[3];

        float result = (pAT * pnAnT) / (pAnT * pnAT);

        return result;
    }

    /**
     * Retrieve the Odds Ratio Measure of a constraint for a given trace.
     * <p>
     * The Odds Ratio measure is defined as:
     * OddsRatio(A->T) = ( P(A' intersection T') P(¬A' intersection ¬T') ) / ( P(A' intersection ¬T') P(¬A' intersection T') )
     *
     * @return
     */
    public static float getTraceOddsRatio(float[] p) {
        float pA = p[0];
        float pnA = p[2];
        float pT = p[1];
        float pnT = p[3];
        float pnAnT = p[4];
        float pnAT = p[5];
        float pAnT = p[6];
        float pAT = p[7];

        float result = (pAT * pnAnT) / (pAnT * pnAT);

        return result;
    }

    /**
     * Retrieve the Gini Index Measure of a constraint for a given trace.
     * <p>
     * The Gini Index measure is defined as:
     * GiniIndex(A->T) = P(A) ∗ {P(B|A)^2 + P(¬B|A)^2} + P(¬A) ∗ {P(B|¬A)^2 * +P(¬B|¬A)^2} − P(B)^2 − P(¬B)^2
     *
     * @return
     */
    public static float getTraceGiniIndex(byte[] reactiveConstraintEvaluation) {
        float[] p = getReactiveProbabilities(reactiveConstraintEvaluation);// result { 0: activation, 1: target, 2: no activation, 3: no target}
        float pA = p[0];
        float pnA = p[2];
        float pT = p[1];
        float pnT = p[3];
        float[] pIntersection = getReactiveIntersectionsProbabilities(reactiveConstraintEvaluation);// result {0: 00, 1: 01, , 2: 10, 3:11}
        float pnAnT = pIntersection[0];
        float pnAT = pIntersection[1];
        float pAnT = pIntersection[2];
        float pAT = pIntersection[3];

        double result = pA * (Math.pow((pAT / pA), 2) + Math.pow(pAnT / pA, 2)) + pnA * (Math.pow(pnAT / pnA, 2) + Math.pow(pnAnT / pnA, 2)) - Math.pow(pT, 2) - Math.pow(pnT, 2);

        return (float) result;
    }

    /**
     * Retrieve the Gini Index Measure of a constraint for a given trace.
     * <p>
     * The Gini Index measure is defined as:
     * GiniIndex(A->T) = P(A) ∗ {P(B|A)^2 + P(¬B|A)^2} + P(¬A) ∗ {P(B|¬A)^2 * +P(¬B|¬A)^2} − P(B)^2 − P(¬B)^2
     *
     * @return
     */
    public static float getTraceGiniIndex(float[] p) {
        float pA = p[0];
        float pnA = p[2];
        float pT = p[1];
        float pnT = p[3];
        float pnAnT = p[4];
        float pnAT = p[5];
        float pAnT = p[6];
        float pAT = p[7];

        double result = pA * (Math.pow((pAT / pA), 2) + Math.pow(pAnT / pA, 2)) + pnA * (Math.pow(pnAT / pnA, 2) + Math.pow(pnAnT / pnA, 2)) - Math.pow(pT, 2) - Math.pow(pnT, 2);

        return (float) result;
    }

    /**
     * Retrieve the Certainty Factor Measure of a constraint for a given trace.
     * <p>
     * The Certainty Factor measure is defined as:
     * CertaintyFactor(A->T) = (P(B|A) − P(B))/(1 − P(B))
     *
     * @return
     */
    public static float getTraceCertaintyFactor(byte[] reactiveConstraintEvaluation) {
        float[] p = getReactiveProbabilities(reactiveConstraintEvaluation);// result { 0: activation, 1: target, 2: no activation, 3: no target}
        float pA = p[0];
        float pnA = p[2];
        float pT = p[1];
        float pnT = p[3];
        float[] pIntersection = getReactiveIntersectionsProbabilities(reactiveConstraintEvaluation);// result {0: 00, 1: 01, , 2: 10, 3:11}
        float pnAnT = pIntersection[0];
        float pnAT = pIntersection[1];
        float pAnT = pIntersection[2];
        float pAT = pIntersection[3];

        float result = ((pAT / pA) - pT) / (1 - pT);

        return result;
    }

    /**
     * Retrieve the Certainty Factor Measure of a constraint for a given trace.
     * <p>
     * The Certainty Factor measure is defined as:
     * CertaintyFactor(A->T) = (P(B|A) − P(B))/(1 − P(B))
     *
     * @return
     */
    public static float getTraceCertaintyFactor(float[] p) {
        float pA = p[0];
        float pnA = p[2];
        float pT = p[1];
        float pnT = p[3];
        float pnAnT = p[4];
        float pnAT = p[5];
        float pAnT = p[6];
        float pAT = p[7];

        float result = ((pAT / pA) - pT) / (1 - pT);

        return result;
    }

    /**
     * Retrieve the Coverage Measure of a constraint for a given trace.
     * <p>
     * The coverage measure is defined as:
     * Coverage(A->T) = P(A)
     *
     * @return
     */
    public static float getTraceCoverage(byte[] reactiveConstraintEvaluation) {
        float[] p = getReactiveProbabilities(reactiveConstraintEvaluation);// result { 0: activation, 1: target, 2: no activation, 3: no target}
        float pA = p[0];
        float pnA = p[2];
        float pT = p[1];
        float pnT = p[3];
        float[] pIntersection = getReactiveIntersectionsProbabilities(reactiveConstraintEvaluation);// result {0: 00, 1: 01, , 2: 10, 3:11}
        float pnAnT = pIntersection[0];
        float pnAT = pIntersection[1];
        float pAnT = pIntersection[2];
        float pAT = pIntersection[3];

        float result = pA;

        return result;
    }

    /**
     * Retrieve the Coverage Measure of a constraint for a given trace.
     * <p>
     * The coverage measure is defined as:
     * Coverage(A->T) = P(A)
     *
     * @return
     */
    public static float getTraceCoverage(float[] p) {
        float pA = p[0];
        float pnA = p[2];
        float pT = p[1];
        float pnT = p[3];
        float pnAnT = p[4];
        float pnAT = p[5];
        float pAnT = p[6];
        float pAT = p[7];

        float result = pA;

        return result;
    }

    /**
     * Retrieve the Prevalence Measure of a constraint for a given trace.
     * <p>
     * The prevalence measure is defined as:
     * Prevalence(A->T) = P(T)
     *
     * @return
     */
    public static float getTracePrevalence(byte[] reactiveConstraintEvaluation) {
        float[] p = getReactiveProbabilities(reactiveConstraintEvaluation);// result { 0: activation, 1: target, 2: no activation, 3: no target}
        float pA = p[0];
        float pnA = p[2];
        float pT = p[1];
        float pnT = p[3];
        float[] pIntersection = getReactiveIntersectionsProbabilities(reactiveConstraintEvaluation);// result {0: 00, 1: 01, , 2: 10, 3:11}
        float pnAnT = pIntersection[0];
        float pnAT = pIntersection[1];
        float pAnT = pIntersection[2];
        float pAT = pIntersection[3];

        float result = pT;

        return result;
    }

    /**
     * Retrieve the Prevalence Measure of a constraint for a given trace.
     * <p>
     * The prevalence measure is defined as:
     * Prevalence(A->T) = P(T)
     *
     * @return
     */
    public static float getTracePrevalence(float[] p) {
        float pA = p[0];
        float pnA = p[2];
        float pT = p[1];
        float pnT = p[3];
        float pnAnT = p[4];
        float pnAT = p[5];
        float pAnT = p[6];
        float pAT = p[7];

        float result = pT;

        return result;
    }

    /**
     * Retrieve the Added Value Measure of a constraint for a given trace.
     * <p>
     * The Added Value measure is defined as:
     * AddedValue(A->T) = P(T|A)-P(T) = P(AT)/P(A) - P(T)
     *
     * @return
     */
    public static float getTraceAddedValue(byte[] reactiveConstraintEvaluation) {
        float[] p = getReactiveProbabilities(reactiveConstraintEvaluation);// result { 0: activation, 1: target, 2: no activation, 3: no target}
        float pA = p[0];
        float pnA = p[2];
        float pT = p[1];
        float pnT = p[3];
        float[] pIntersection = getReactiveIntersectionsProbabilities(reactiveConstraintEvaluation);// result {0: 00, 1: 01, , 2: 10, 3:11}
        float pnAnT = pIntersection[0];
        float pnAT = pIntersection[1];
        float pAnT = pIntersection[2];
        float pAT = pIntersection[3];

        float result = pAT / pA - pT;

        return result;
    }

    /**
     * Retrieve the Added Value Measure of a constraint for a given trace.
     * <p>
     * The Added Value measure is defined as:
     * AddedValue(A->T) = P(T|A)-P(T) = P(AT)/P(A) - P(T)
     *
     * @return
     */
    public static float getTraceAddedValue(float[] p) {
        float pA = p[0];
        float pnA = p[2];
        float pT = p[1];
        float pnT = p[3];
        float pnAnT = p[4];
        float pnAT = p[5];
        float pAnT = p[6];
        float pAT = p[7];

        float result = (pAT / pA) - pT;

        return result;
    }

    /**
     * Retrieve the Relative Risk Measure of a constraint for a given trace.
     * <p>
     * The Relative Risk measure is defined as:
     * RelativeRisk(A->T) = P(T|A)/P(T|¬A) = ( P(AT)/P(A) ) / ( P(¬AT)/P(¬A) )
     *
     * @return
     */
    public static float getTraceRelativeRisk(byte[] reactiveConstraintEvaluation) {
        float[] p = getReactiveProbabilities(reactiveConstraintEvaluation);// result { 0: activation, 1: target, 2: no activation, 3: no target}
        float pA = p[0];
        float pnA = p[2];
        float pT = p[1];
        float pnT = p[3];
        float[] pIntersection = getReactiveIntersectionsProbabilities(reactiveConstraintEvaluation);// result {0: 00, 1: 01, , 2: 10, 3:11}
        float pnAnT = pIntersection[0];
        float pnAT = pIntersection[1];
        float pAnT = pIntersection[2];
        float pAT = pIntersection[3];

        float result = (pAT / pA) / (pnAT / pnA);

        return result;
    }

    /**
     * Retrieve the Relative Risk Measure of a constraint for a given trace.
     * <p>
     * The Relative Risk measure is defined as:
     * RelativeRisk(A->T) = P(T|A)/P(T|¬A) = ( P(AT)/P(A) ) / ( P(¬AT)/P(¬A) )
     *
     * @return
     */
    public static float getTraceRelativeRisk(float[] p) {
        float pA = p[0];
        float pnA = p[2];
        float pT = p[1];
        float pnT = p[3];
        float pnAnT = p[4];
        float pnAT = p[5];
        float pAnT = p[6];
        float pAT = p[7];

        float result = (pAT / pA) / (pnAT / pnA);

        return result;
    }

    /**
     * Retrieve the Jaccard Measure of a constraint for a given trace.
     * <p>
     * The Jaccard measure is defined as:
     * Jaccard(A->T) = P(AT)/ ( P(A)+P(T) - P(AT) )
     *
     * @return
     */
    public static float getTraceJaccard(byte[] reactiveConstraintEvaluation) {
        float[] p = getReactiveProbabilities(reactiveConstraintEvaluation);// result { 0: activation, 1: target, 2: no activation, 3: no target}
        float pA = p[0];
        float pnA = p[2];
        float pT = p[1];
        float pnT = p[3];
        float[] pIntersection = getReactiveIntersectionsProbabilities(reactiveConstraintEvaluation);// result {0: 00, 1: 01, , 2: 10, 3:11}
        float pnAnT = pIntersection[0];
        float pnAT = pIntersection[1];
        float pAnT = pIntersection[2];
        float pAT = pIntersection[3];

        float result = pAT / (pA + pT - pAT);

        return result;
    }

    /**
     * Retrieve the Jaccard Measure of a constraint for a given trace.
     * <p>
     * The Jaccard measure is defined as:
     * Jaccard(A->T) = P(AT)/ ( P(A)+P(T) - P(AT) )
     *
     * @return
     */
    public static float getTraceJaccard(float[] p) {
        float pA = p[0];
        float pnA = p[2];
        float pT = p[1];
        float pnT = p[3];
        float pnAnT = p[4];
        float pnAT = p[5];
        float pAnT = p[6];
        float pAT = p[7];

        float result = pAT / (pA + pT - pAT);

        return result;
    }

    /**
     * Retrieve the Ylue Q Measure of a constraint for a given trace.
     * <p>
     * The Ylue Q measure is defined as:
     * YlueQ(A->T) = ( P(AT) P(¬A¬T) - P(A¬T)P(¬AT) ) / ( P(AT) P(¬A¬T) + P(A¬T)P(¬AT) )
     *
     * @return
     */
    public static float getTraceYlueQ(byte[] reactiveConstraintEvaluation) {
        float[] p = getReactiveProbabilities(reactiveConstraintEvaluation);// result { 0: activation, 1: target, 2: no activation, 3: no target}
        float pA = p[0];
        float pnA = p[2];
        float pT = p[1];
        float pnT = p[3];
        float[] pIntersection = getReactiveIntersectionsProbabilities(reactiveConstraintEvaluation);// result {0: 00, 1: 01, , 2: 10, 3:11}
        float pnAnT = pIntersection[0];
        float pnAT = pIntersection[1];
        float pAnT = pIntersection[2];
        float pAT = pIntersection[3];

        float result = (pAT * pnAnT - pAnT * pnAT) / (pAT * pnAnT + pAnT * pnAT);

        return result;
    }

    /**
     * Retrieve the Ylue Q Measure of a constraint for a given trace.
     * <p>
     * The Ylue Q measure is defined as:
     * YlueQ(A->T) = ( P(AT) P(¬A¬T) - P(A¬T)P(¬AT) ) / ( P(AT) P(¬A¬T) + P(A¬T)P(¬AT) )
     *
     * @return
     */
    public static float getTraceYlueQ(float[] p) {
        float pA = p[0];
        float pnA = p[2];
        float pT = p[1];
        float pnT = p[3];
        float pnAnT = p[4];
        float pnAT = p[5];
        float pAnT = p[6];
        float pAT = p[7];

        float result = ((pAT * pnAnT) - (pAnT * pnAT)) / ((pAT * pnAnT) + (pAnT * pnAT));

        return result;
    }

    /**
     * Retrieve the Ylue Y Measure of a constraint for a given trace.
     * <p>
     * The Ylue Y measure is defined as:
     * YlueY(A->T) = ( (P(AT) P(¬A¬T))^1/2 - (P(A¬T)P(¬AT))^1/2 ) / ( (P(AT) P(¬A¬T))^1/2 + (P(A¬T)P(¬AT))^1/2 )
     *
     * @return
     */
    public static float getTraceYlueY(byte[] reactiveConstraintEvaluation) {
        float[] p = getReactiveProbabilities(reactiveConstraintEvaluation);// result { 0: activation, 1: target, 2: no activation, 3: no target}
        float pA = p[0];
        float pnA = p[2];
        float pT = p[1];
        float pnT = p[3];
        float[] pIntersection = getReactiveIntersectionsProbabilities(reactiveConstraintEvaluation);// result {0: 00, 1: 01, , 2: 10, 3:11}
        float pnAnT = pIntersection[0];
        float pnAT = pIntersection[1];
        float pAnT = pIntersection[2];
        float pAT = pIntersection[3];

        double result = (Math.sqrt(pAT * pnAnT) - Math.sqrt(pAnT * pnAT)) / (Math.sqrt(pAT * pnAnT) + Math.sqrt(pAnT * pnAT));

        return (float) result;
    }

    /**
     * Retrieve the Ylue Y Measure of a constraint for a given trace.
     * <p>
     * The Ylue Y measure is defined as:
     * YlueY(A->T) = ( (P(AT) P(¬A¬T))^1/2 - (P(A¬T)P(¬AT))^1/2 ) / ( (P(AT) P(¬A¬T))^1/2 + (P(A¬T)P(¬AT))^1/2 )
     *
     * @return
     */
    public static float getTraceYlueY(float[] p) {
        float pA = p[0];
        float pnA = p[2];
        float pT = p[1];
        float pnT = p[3];
        float pnAnT = p[4];
        float pnAT = p[5];
        float pAnT = p[6];
        float pAT = p[7];

        double result = (Math.sqrt(pAT * pnAnT) - Math.sqrt(pAnT * pnAT)) / (Math.sqrt(pAT * pnAnT) + Math.sqrt(pAnT * pnAT));

        return (float) result;
    }

    /**
     * Retrieve the Klosgen Measure of a constraint for a given trace.
     * <p>
     * The Klosgen measure is defined as:
     * Klosgen(A->T) = P(AT)^1/2 * Max( P(T|A) - P(T) , P(A|T) -P(A) ) = P(AT)^1/2 * Max( P(AT)/P(A) - P(T) , P(AT)/P(T) -P(A) )
     *
     * @return
     */
    public static float getTraceKlosgen(byte[] reactiveConstraintEvaluation) {
        float[] p = getReactiveProbabilities(reactiveConstraintEvaluation);// result { 0: activation, 1: target, 2: no activation, 3: no target}
        float pA = p[0];
        float pnA = p[2];
        float pT = p[1];
        float pnT = p[3];
        float[] pIntersection = getReactiveIntersectionsProbabilities(reactiveConstraintEvaluation);// result {0: 00, 1: 01, , 2: 10, 3:11}
        float pnAnT = pIntersection[0];
        float pnAT = pIntersection[1];
        float pAnT = pIntersection[2];
        float pAT = pIntersection[3];

        double result = Math.sqrt(pAT) * Math.max(pAT / pA - pT, pAT / pT - pA);

        return (float) result;
    }

    /**
     * Retrieve the Klosgen Measure of a constraint for a given trace.
     * <p>
     * The Klosgen measure is defined as:
     * Klosgen(A->T) = P(AT)^1/2 * Max( P(T|A) - P(T) , P(A|T) -P(A) ) = P(AT)^1/2 * Max( P(AT)/P(A) - P(T) , P(AT)/P(T) -P(A) )
     *
     * @return
     */
    public static float getTraceKlosgen(float[] p) {
        float pA = p[0];
        float pnA = p[2];
        float pT = p[1];
        float pnT = p[3];
        float pnAnT = p[4];
        float pnAT = p[5];
        float pAnT = p[6];
        float pAT = p[7];

        double result = Math.sqrt(pAT) * Math.max(((pAT / pA) - pT), ((pAT / pT) - pA));

        return (float) result;
    }

    /**
     * Retrieve the Conviction Measure of a constraint for a given trace.
     * <p>
     * The Conviction measure is defined as:
     * Conviction(A->T) = ( P(A) P(¬T)) / P(A¬T)
     *
     * @return
     */
    public static float getTraceConviction(byte[] reactiveConstraintEvaluation) {
        float[] p = getReactiveProbabilities(reactiveConstraintEvaluation);// result { 0: activation, 1: target, 2: no activation, 3: no target}
        float pA = p[0];
        float pnA = p[2];
        float pT = p[1];
        float pnT = p[3];
        float[] pIntersection = getReactiveIntersectionsProbabilities(reactiveConstraintEvaluation);// result {0: 00, 1: 01, , 2: 10, 3:11}
        float pnAnT = pIntersection[0];
        float pnAT = pIntersection[1];
        float pAnT = pIntersection[2];
        float pAT = pIntersection[3];

        float result = (pA * pnT) / pAnT;

        return result;
    }

    /**
     * Retrieve the Conviction Measure of a constraint for a given trace.
     * <p>
     * The Conviction measure is defined as:
     * Conviction(A->T) = ( P(A) P(¬T)) / P(A¬T)
     *
     * @return
     */
    public static float getTraceConviction(float[] p) {
        float pA = p[0];
        float pnA = p[2];
        float pT = p[1];
        float pnT = p[3];
        float pnAnT = p[4];
        float pnAT = p[5];
        float pAnT = p[6];
        float pAT = p[7];

        float result = (pA * pnT) / pAnT;

        return result;
    }

    /**
     * Retrieve the Interestingness Weighting Dependency Measure of a constraint for a given trace.
     * <p>
     * The Interestingness Weighting Dependency measure is defined as:
     * InterestingnessWeightingDependency(A->T) = ( (P(AT)/( P(A)P(T) ))^k -1) * (P(AT))^m
     * we assume m=2 and k=2 like in (Le and Lo 2015)
     *
     * @return
     */
    public static float getTraceInterestingnessWeightingDependency(byte[] reactiveConstraintEvaluation) {
        float[] p = getReactiveProbabilities(reactiveConstraintEvaluation);// result { 0: activation, 1: target, 2: no activation, 3: no target}
        float pA = p[0];
        float pnA = p[2];
        float pT = p[1];
        float pnT = p[3];
        float[] pIntersection = getReactiveIntersectionsProbabilities(reactiveConstraintEvaluation);// result {0: 00, 1: 01, , 2: 10, 3:11}
        float pnAnT = pIntersection[0];
        float pnAT = pIntersection[1];
        float pAnT = pIntersection[2];
        float pAT = pIntersection[3];

        int m = 2;
        int k = 2;

        double result = (Math.pow(pAT / (pA * pT), k) - 1) * Math.pow(pAT, m);

        return (float) result;
    }

    /**
     * Retrieve the Interestingness Weighting Dependency Measure of a constraint for a given trace.
     * <p>
     * The Interestingness Weighting Dependency measure is defined as:
     * InterestingnessWeightingDependency(A->T) = ( (P(AT)/( P(A)P(T) ))^k -1) * (P(AT))^m
     * we assume m=2 and k=2 like in (Le and Lo 2015)
     *
     * @return
     */
    public static float getTraceInterestingnessWeightingDependency(float[] p) {
        float pA = p[0];
        float pnA = p[2];
        float pT = p[1];
        float pnT = p[3];
        float pnAnT = p[4];
        float pnAT = p[5];
        float pAnT = p[6];
        float pAT = p[7];

        int m = 2;
        int k = 2;

        double result = (Math.pow(pAT / (pA * pT), k) - 1) * Math.pow(pAT, m);

        return (float) result;
    }

    /**
     * Retrieve the Collective Strength Measure of a constraint for a given trace.
     * <p>
     * The Collective Strength measure is defined as:
     * CollectiveStrength(A->T) = ( P(AT)+P(¬T|¬A) )/( P(A)P(T)+P(¬A)P(¬B) ) * ( 1-P(A)P(T)-P(¬A)P(¬T) )/( 1-P(AT)-P(¬T|¬A) ) =
     * = ( P(AT)+P(¬T¬A)/P(¬A) )/( P(A)P(T)+P(¬A)P(¬B) ) * ( 1-P(A)P(T)-P(¬A)P(¬T) )/( 1-P(AT)-P(¬T¬A)/P(¬A) )
     *
     * @return
     */
    public static float getTraceCollectiveStrength(byte[] reactiveConstraintEvaluation) {
        float[] p = getReactiveProbabilities(reactiveConstraintEvaluation);// result { 0: activation, 1: target, 2: no activation, 3: no target}
        float pA = p[0];
        float pnA = p[2];
        float pT = p[1];
        float pnT = p[3];
        float[] pIntersection = getReactiveIntersectionsProbabilities(reactiveConstraintEvaluation);// result {0: 00, 1: 01, , 2: 10, 3:11}
        float pnAnT = pIntersection[0];
        float pnAT = pIntersection[1];
        float pAnT = pIntersection[2];
        float pAT = pIntersection[3];

        float result = (pAT + (pnAnT / pnA)) / (pA * pT + pnA * pnT) * (1 - pA * pT - pnA * pnT) / (1 - pAT - (pnAnT / pnA));

        return result;
    }

    /**
     * Retrieve the Collective Strength Measure of a constraint for a given trace.
     * <p>
     * The Collective Strength measure is defined as:
     * CollectiveStrength(A->T) = ( P(AT)+P(¬T|¬A) )/( P(A)P(T)+P(¬A)P(¬B) ) * ( 1-P(A)P(T)-P(¬A)P(¬T) )/( 1-P(AT)-P(¬T|¬A) ) =
     * = ( P(AT)+P(¬T¬A)/P(¬A) )/( P(A)P(T)+P(¬A)P(¬B) ) * ( 1-P(A)P(T)-P(¬A)P(¬T) )/( 1-P(AT)-P(¬T¬A)/P(¬A) )
     *
     * @return
     */
    public static float getTraceCollectiveStrength(float[] p) {
        float pA = p[0];
        float pnA = p[2];
        float pT = p[1];
        float pnT = p[3];
        float pnAnT = p[4];
        float pnAT = p[5];
        float pAnT = p[6];
        float pAT = p[7];

        float result = ((pAT + (pnAnT / pnA)) / ((pA * pT) + (pnA * pnT))) * ((1 - (pA * pT) - (pnA * pnT)) / (1 - pAT - (pnAnT / pnA)));

        return result;
    }

    /**
     * Retrieve the Laplace Correction Measure of a constraint for a given trace.
     * <p>
     * The Laplace Correction measure is defined as:
     * LaplaceCorrection(A->T) = ( N(AT) +1 ) / (N(A) + 2) = (n*P(AT) +1)/(n*P(A)+2)
     * where N(x) is not the probability but the number of occurrence of x in the trace, thus n=trace length
     * e.g. P(AB) = N(AB)/Length(Trace)
     *
     * @return
     */
    public static float getTraceLaplaceCorrection(byte[] reactiveConstraintEvaluation) {
        float[] p = getReactiveProbabilities(reactiveConstraintEvaluation);// result { 0: activation, 1: target, 2: no activation, 3: no target}
        float pA = p[0];
        float pnA = p[2];
        float pT = p[1];
        float pnT = p[3];
        float[] pIntersection = getReactiveIntersectionsProbabilities(reactiveConstraintEvaluation);// result {0: 00, 1: 01, , 2: 10, 3:11}
        float pnAnT = pIntersection[0];
        float pnAT = pIntersection[1];
        float pAnT = pIntersection[2];
        float pAT = pIntersection[3];

        int n = reactiveConstraintEvaluation.length;

        float result = (n * pAT + 1) / (n * pA + 2);

        return result;
    }

    /**
     * Retrieve the Laplace Correction Measure of a constraint for a given trace.
     * <p>
     * The Laplace Correction measure is defined as:
     * LaplaceCorrection(A->T) = ( N(AT) +1 ) / (N(A) + 2) = (n*P(AT) +1)/(n*P(A)+2)
     * where N(x) is not the probability but the number of occurrence of x in the trace, thus n=trace length
     * e.g. P(AB) = N(AB)/Length(Trace)
     *
     * @return
     */
    public static float getTraceLaplaceCorrection(float[] p) {
        float pA = p[0];
        float pnA = p[2];
        float pT = p[1];
        float pnT = p[3];
        float pnAnT = p[4];
        float pnAT = p[5];
        float pAnT = p[6];
        float pAT = p[7];
        float n = p[8];

//        int n = reactiveConstraintEvaluation.length;

//        TODO check ranges of this function: supposedly [0.5-1], but there are lower results
        float result = (n * pAT + 1) / (n * pA + 2);

        return result;
    }

    /**
     * Retrieve the J-Measure Measure of a constraint for a given trace.
     * <p>
     * The J-Measure measure is defined as:
     * JMeasure(A->T) = P(AT) log(P(T|A)/P(T)) + P(A¬T) log( P(¬T|A)/P(¬T) )
     *
     * @return
     */
    public static float getTraceJMeasure(byte[] reactiveConstraintEvaluation) {
        float[] p = getReactiveProbabilities(reactiveConstraintEvaluation);// result { 0: activation, 1: target, 2: no activation, 3: no target}
        float pA = p[0];
        float pnA = p[2];
        float pT = p[1];
        float pnT = p[3];
        float[] pIntersection = getReactiveIntersectionsProbabilities(reactiveConstraintEvaluation);// result {0: 00, 1: 01, , 2: 10, 3:11}
        float pnAnT = pIntersection[0];
        float pnAT = pIntersection[1];
        float pAnT = pIntersection[2];
        float pAT = pIntersection[3];

        double result = pAT * Math.log((pAT / pA) / pT) + pAnT * Math.log((pAnT / pA) / pnT);

        return (float) result;
    }

    /**
     * Retrieve the J-Measure Measure of a constraint for a given trace.
     * <p>
     * The J-Measure measure is defined as:
     * JMeasure(A->T) = P(AT) log(P(T|A)/P(T)) + P(A¬T) log( P(¬T|A)/P(¬T) )
     *
     * @return
     */
    public static float getTraceJMeasure(float[] p) {
        float pA = p[0];
        float pnA = p[2];
        float pT = p[1];
        float pnT = p[3];
        float pnAnT = p[4];
        float pnAT = p[5];
        float pAnT = p[6];
        float pAT = p[7];
        float n = p[8];

        double result = (pAT * Math.log((pAT / pA) / pT)) + (pAnT * Math.log((pAnT / pA) / pnT));

        return (float) result;
    }

    /**
     * Retrieve the One-way Support Measure of a constraint for a given trace.
     * <p>
     * The One-way Support measure is defined as:
     * OnewaySupport(A->T) = P(T|A) log_2(P(AT)/(P(A)P(T)))
     *
     * @return
     */
    public static float getTraceOneWaySupport(byte[] reactiveConstraintEvaluation) {
        float[] p = getReactiveProbabilities(reactiveConstraintEvaluation);// result { 0: activation, 1: target, 2: no activation, 3: no target}
        float pA = p[0];
        float pnA = p[2];
        float pT = p[1];
        float pnT = p[3];
        float[] pIntersection = getReactiveIntersectionsProbabilities(reactiveConstraintEvaluation);// result {0: 00, 1: 01, , 2: 10, 3:11}
        float pnAnT = pIntersection[0];
        float pnAT = pIntersection[1];
        float pAnT = pIntersection[2];
        float pAT = pIntersection[3];

        double result = pAT / pA * log2(pAT / (pA * pT));

        return (float) result;
    }

    /**
     * Retrieve the One-way Support Measure of a constraint for a given trace.
     * <p>
     * The One-way Support measure is defined as:
     * OnewaySupport(A->T) = P(T|A) log_2(P(AT)/(P(A)P(T)))
     *
     * @return
     */
    public static float getTraceOneWaySupport(float[] p) {
        float pA = p[0];
        float pnA = p[2];
        float pT = p[1];
        float pnT = p[3];
        float pnAnT = p[4];
        float pnAT = p[5];
        float pAnT = p[6];
        float pAT = p[7];
        float n = p[8];

        double result = (pAT / pA) * log2(pAT / (pA * pT));

        return (float) result;
    }

    /**
     * Retrieve the Two-way Support Measure of a constraint for a given trace.
     * <p>
     * The Two-way Support measure is defined as:
     * TwoWaySupport(A->T) = P(AT) log_2(P(AT)/(P(A)P(T)))
     *
     * @return
     */
    public static float getTraceTwoWaySupport(byte[] reactiveConstraintEvaluation) {
        float[] p = getReactiveProbabilities(reactiveConstraintEvaluation);// result { 0: activation, 1: target, 2: no activation, 3: no target}
        float pA = p[0];
        float pnA = p[2];
        float pT = p[1];
        float pnT = p[3];
        float[] pIntersection = getReactiveIntersectionsProbabilities(reactiveConstraintEvaluation);// result {0: 00, 1: 01, , 2: 10, 3:11}
        float pnAnT = pIntersection[0];
        float pnAT = pIntersection[1];
        float pAnT = pIntersection[2];
        float pAT = pIntersection[3];

        double result = pAT * log2(pAT / (pA * pT));

        return (float) result;
    }

    /**
     * Retrieve the Two-way Support Measure of a constraint for a given trace.
     * <p>
     * The Two-way Support measure is defined as:
     * TwoWaySupport(A->T) = P(AT) log_2(P(AT)/(P(A)P(T)))
     *
     * @return
     */
    public static float getTraceTwoWaySupport(float[] p) {
        float pA = p[0];
        float pnA = p[2];
        float pT = p[1];
        float pnT = p[3];
        float pnAnT = p[4];
        float pnAT = p[5];
        float pAnT = p[6];
        float pAT = p[7];
        float n = p[8];

        double result = pAT * log2(pAT / (pA * pT));

        return (float) result;
    }


    /**
     * Retrieve the Two-way Support Variation Measure of a constraint for a given trace.
     * <p>
     * The Two-way Support Variation measure is defined as:
     * TwoWaySupportVariation(A->T) = P(AT) log_2( P(AT)/(P(A)P(T)) ) + P(A¬T) log_2( P(A¬T)/(P(A)P(¬T)) )
     * + P(¬AT) log_2( P(¬AT)/(P(¬A)P(T)) ) + P(¬A¬T) log_2( P(¬A¬T)/(P(¬A)P(¬T)) )
     *
     * @return
     */
    public static float getTraceTwoWaySupportVariation(byte[] reactiveConstraintEvaluation) {
        float[] p = getReactiveProbabilities(reactiveConstraintEvaluation);// result { 0: activation, 1: target, 2: no activation, 3: no target}
        float pA = p[0];
        float pnA = p[2];
        float pT = p[1];
        float pnT = p[3];
        float[] pIntersection = getReactiveIntersectionsProbabilities(reactiveConstraintEvaluation);// result {0: 00, 1: 01, , 2: 10, 3:11}
        float pnAnT = pIntersection[0];
        float pnAT = pIntersection[1];
        float pAnT = pIntersection[2];
        float pAT = pIntersection[3];

        double result = pAT * log2(pAT / (pA * pT)) +
                pAnT * log2(pAnT / (pA * pnT)) +
                pnAT * log2(pnAT / (pnA * pT)) +
                pnAnT * log2(pnAnT / (pnA * pnT));

        return (float) result;
    }

    /**
     * Retrieve the Two-way Support Variation Measure of a constraint for a given trace.
     * <p>
     * The Two-way Support Variation measure is defined as:
     * TwoWaySupportVariation(A->T) = P(AT) log_2( P(AT)/(P(A)P(T)) ) + P(A¬T) log_2( P(A¬T)/(P(A)P(¬T)) )
     * + P(¬AT) log_2( P(¬AT)/(P(¬A)P(T)) ) + P(¬A¬T) log_2( P(¬A¬T)/(P(¬A)P(¬T)) )
     *
     * @return
     */
    public static float getTraceTwoWaySupportVariation(float[] p) {
        float pA = p[0];
        float pnA = p[2];
        float pT = p[1];
        float pnT = p[3];
        float pnAnT = p[4];
        float pnAT = p[5];
        float pAnT = p[6];
        float pAT = p[7];
        float n = p[8];

        double result = pAT * log2(pAT / (pA * pT)) +
                pAnT * log2(pAnT / (pA * pnT)) +
                pnAT * log2(pnAT / (pnA * pT)) +
                pnAnT * log2(pnAnT / (pnA * pnT));

        return (float) result;
    }

    /**
     * Retrieve the Linear Correlation Coefficient Measure of a constraint for a given trace.
     * <p>
     * The Linear Correlation Coefficient measure is defined as:
     * LinearCorrelationCoefficient(A->T) = (P(AT)-P(A)P(B)) / ((P(A)P(T)P(¬A)P(¬T))^1/2)
     *
     * @return
     */
    public static float getTraceLinearCorrelationCoefficient(byte[] reactiveConstraintEvaluation) {
        float[] p = getReactiveProbabilities(reactiveConstraintEvaluation);// result { 0: activation, 1: target, 2: no activation, 3: no target}
        float pA = p[0];
        float pnA = p[2];
        float pT = p[1];
        float pnT = p[3];
        float[] pIntersection = getReactiveIntersectionsProbabilities(reactiveConstraintEvaluation);// result {0: 00, 1: 01, , 2: 10, 3:11}
        float pnAnT = pIntersection[0];
        float pnAT = pIntersection[1];
        float pAnT = pIntersection[2];
        float pAT = pIntersection[3];

        double result = (pAT - pA * pT) / Math.sqrt(pA * pT * pnA * pnT);

        return (float) result;
    }

    /**
     * Retrieve the Linear Correlation Coefficient Measure of a constraint for a given trace.
     * <p>
     * The Linear Correlation Coefficient measure is defined as:
     * LinearCorrelationCoefficient(A->T) = (P(AT)-P(A)P(B)) / ((P(A)P(T)P(¬A)P(¬T))^1/2)
     *
     * @return
     */
    public static float getTraceLinearCorrelationCoefficient(float[] p) {
        float pA = p[0];
        float pnA = p[2];
        float pT = p[1];
        float pnT = p[3];
        float pnAnT = p[4];
        float pnAT = p[5];
        float pAnT = p[6];
        float pAT = p[7];
        float n = p[8];

        double result = (pAT - pA * pT) / Math.sqrt(pA * pT * pnA * pnT);

        return (float) result;
    }

    /**
     * Retrieve the Piatetsky-Shapiro Measure of a constraint for a given trace.
     * <p>
     * The Piatetsky-Shapiro measure is defined as:
     * PiatetskyShapiro(A->T) = P(AT)-P(A)P(T)
     *
     * @return
     */
    public static float getTracePiatetskyShapiro(byte[] reactiveConstraintEvaluation) {
        float[] p = getReactiveProbabilities(reactiveConstraintEvaluation);// result { 0: activation, 1: target, 2: no activation, 3: no target}
        float pA = p[0];
        float pnA = p[2];
        float pT = p[1];
        float pnT = p[3];
        float[] pIntersection = getReactiveIntersectionsProbabilities(reactiveConstraintEvaluation);// result {0: 00, 1: 01, , 2: 10, 3:11}
        float pnAnT = pIntersection[0];
        float pnAT = pIntersection[1];
        float pAnT = pIntersection[2];
        float pAT = pIntersection[3];

        float result = pAT - pA * pT;

        return result;
    }

    /**
     * Retrieve the Piatetsky-Shapiro Measure of a constraint for a given trace.
     * <p>
     * The Piatetsky-Shapiro measure is defined as:
     * PiatetskyShapiro(A->T) = P(AT)-P(A)P(T)
     *
     * @return
     */
    public static float getTracePiatetskyShapiro(float[] p) {
        float pA = p[0];
        float pnA = p[2];
        float pT = p[1];
        float pnT = p[3];
        float pnAnT = p[4];
        float pnAT = p[5];
        float pAnT = p[6];
        float pAT = p[7];
        float n = p[8];

        float result = pAT - (pA * pT);

        return result;
    }

    /**
     * Retrieve the Cosine Measure of a constraint for a given trace.
     * <p>
     * The Cosine measure is defined as:
     * Cosine(A->T) = P(AT)/(P(A)P(T))^1/2
     *
     * @return
     */
    public static float getTraceCosine(byte[] reactiveConstraintEvaluation) {
        float[] p = getReactiveProbabilities(reactiveConstraintEvaluation);// result { 0: activation, 1: target, 2: no activation, 3: no target}
        float pA = p[0];
        float pnA = p[2];
        float pT = p[1];
        float pnT = p[3];
        float[] pIntersection = getReactiveIntersectionsProbabilities(reactiveConstraintEvaluation);// result {0: 00, 1: 01, , 2: 10, 3:11}
        float pnAnT = pIntersection[0];
        float pnAT = pIntersection[1];
        float pAnT = pIntersection[2];
        float pAT = pIntersection[3];

        double result = pAT / Math.sqrt(pA * pT);

        return (float) result;
    }

    /**
     * Retrieve the Cosine Measure of a constraint for a given trace.
     * <p>
     * The Cosine measure is defined as:
     * Cosine(A->T) = P(AT)/(P(A)P(T))^1/2
     *
     * @return
     */
    public static float getTraceCosine(float[] p) {
        float pA = p[0];
        float pnA = p[2];
        float pT = p[1];
        float pnT = p[3];
        float pnAnT = p[4];
        float pnAT = p[5];
        float pAnT = p[6];
        float pAT = p[7];
        float n = p[8];

        double result = pAT / Math.sqrt(pA * pT);

        return (float) result;
    }

    /**
     * Retrieve the Information Gain Measure of a constraint for a given trace.
     * <p>
     * The Information Gain measure is defined as:
     * InformationGain(A->T) = log( P(AT)/(P(A)P(T)) )
     *
     * @return
     */
    public static float getTraceInformationGain(byte[] reactiveConstraintEvaluation) {
        float[] p = getReactiveProbabilities(reactiveConstraintEvaluation);// result { 0: activation, 1: target, 2: no activation, 3: no target}
        float pA = p[0];
        float pnA = p[2];
        float pT = p[1];
        float pnT = p[3];
        float[] pIntersection = getReactiveIntersectionsProbabilities(reactiveConstraintEvaluation);// result {0: 00, 1: 01, , 2: 10, 3:11}
        float pnAnT = pIntersection[0];
        float pnAT = pIntersection[1];
        float pAnT = pIntersection[2];
        float pAT = pIntersection[3];

        double result = Math.log(pAT / (pA * pT));

        return (float) result;
    }

    /**
     * Retrieve the Information Gain Measure of a constraint for a given trace.
     * <p>
     * The Information Gain measure is defined as:
     * InformationGain(A->T) = log( P(AT)/(P(A)P(T)) )
     *
     * @return
     */
    public static float getTraceInformationGain(float[] p) {
        float pA = p[0];
        float pnA = p[2];
        float pT = p[1];
        float pnT = p[3];
        float pnAnT = p[4];
        float pnAT = p[5];
        float pAnT = p[6];
        float pAT = p[7];
        float n = p[8];

        double result = Math.log(pAT / (pA * pT));

        return (float) result;
    }

    /**
     * Retrieve the Sebag-Schoenauer Measure of a constraint for a given trace.
     * <p>
     * The Sebag-Schoenauer measure is defined as:
     * SebagSchoenauer(A->T) = P(AT)/P(A¬T)
     *
     * @return
     */
    public static float getTraceSebagSchoenauer(byte[] reactiveConstraintEvaluation) {
        float[] p = getReactiveProbabilities(reactiveConstraintEvaluation);// result { 0: activation, 1: target, 2: no activation, 3: no target}
        float pA = p[0];
        float pnA = p[2];
        float pT = p[1];
        float pnT = p[3];
        float[] pIntersection = getReactiveIntersectionsProbabilities(reactiveConstraintEvaluation);// result {0: 00, 1: 01, , 2: 10, 3:11}
        float pnAnT = pIntersection[0];
        float pnAT = pIntersection[1];
        float pAnT = pIntersection[2];
        float pAT = pIntersection[3];

        float result = pAT / pAnT;

        return result;
    }

    /**
     * Retrieve the Sebag-Schoenauer Measure of a constraint for a given trace.
     * <p>
     * The Sebag-Schoenauer measure is defined as:
     * SebagSchoenauer(A->T) = P(AT)/P(A¬T)
     *
     * @return
     */
    public static float getTraceSebagSchoenauer(float[] p) {
        float pA = p[0];
        float pnA = p[2];
        float pT = p[1];
        float pnT = p[3];
        float pnAnT = p[4];
        float pnAT = p[5];
        float pAnT = p[6];
        float pAT = p[7];
        float n = p[8];

        float result = pAT / pAnT;

        return result;
    }

    /**
     * Retrieve the Least Contradiction Measure of a constraint for a given trace.
     * <p>
     * The Least Contradiction measure is defined as:
     * LeastContradiction(A->T) = (P(AT)-P(A¬T)/P(T)
     *
     * @return
     */
    public static float getTraceLeastContradiction(byte[] reactiveConstraintEvaluation) {
        float[] p = getReactiveProbabilities(reactiveConstraintEvaluation);// result { 0: activation, 1: target, 2: no activation, 3: no target}
        float pA = p[0];
        float pnA = p[2];
        float pT = p[1];
        float pnT = p[3];
        float[] pIntersection = getReactiveIntersectionsProbabilities(reactiveConstraintEvaluation);// result {0: 00, 1: 01, , 2: 10, 3:11}
        float pnAnT = pIntersection[0];
        float pnAT = pIntersection[1];
        float pAnT = pIntersection[2];
        float pAT = pIntersection[3];

        float result = (pAT - pAnT) / pT;

        return result;
    }

    /**
     * Retrieve the Least Contradiction Measure of a constraint for a given trace.
     * <p>
     * The Least Contradiction measure is defined as:
     * LeastContradiction(A->T) = (P(AT)-P(A¬T)/P(T)
     *
     * @return
     */
    public static float getTraceLeastContradiction(float[] p) {
        float pA = p[0];
        float pnA = p[2];
        float pT = p[1];
        float pnT = p[3];
        float pnAnT = p[4];
        float pnAT = p[5];
        float pAnT = p[6];
        float pAT = p[7];
        float n = p[8];

        float result = (pAT - pAnT) / pT;

        return result;
    }

    /**
     * Retrieve the Odd Multiplier Measure of a constraint for a given trace.
     * <p>
     * The Odd Multiplier measure is defined as:
     * OddMultiplier(A->T) = ( P(AT)P(¬T) )/( P(T)P(A¬T) )
     *
     * @return
     */
    public static float getTraceOddMultiplier(byte[] reactiveConstraintEvaluation) {
        float[] p = getReactiveProbabilities(reactiveConstraintEvaluation);// result { 0: activation, 1: target, 2: no activation, 3: no target}
        float pA = p[0];
        float pnA = p[2];
        float pT = p[1];
        float pnT = p[3];
        float[] pIntersection = getReactiveIntersectionsProbabilities(reactiveConstraintEvaluation);// result {0: 00, 1: 01, , 2: 10, 3:11}
        float pnAnT = pIntersection[0];
        float pnAT = pIntersection[1];
        float pAnT = pIntersection[2];
        float pAT = pIntersection[3];

        float result = (pAT * pnT) / (pT * pAnT);

        return result;
    }

    /**
     * Retrieve the Odd Multiplier Measure of a constraint for a given trace.
     * <p>
     * The Odd Multiplier measure is defined as:
     * OddMultiplier(A->T) = ( P(AT)P(¬T) )/( P(T)P(A¬T) )
     *
     * @return
     */
    public static float getTraceOddMultiplier(float[] p) {
        float pA = p[0];
        float pnA = p[2];
        float pT = p[1];
        float pnT = p[3];
        float pnAnT = p[4];
        float pnAT = p[5];
        float pAnT = p[6];
        float pAT = p[7];
        float n = p[8];

        float result = (pAT * pnT) / (pT * pAnT);

        return result;
    }

    /**
     * Retrieve the Example and Counterexample Rate Measure of a constraint for a given trace.
     * <p>
     * The Example and Counterexample Rate measure is defined as:
     * ExampleCounterexampleRate(A->T) = 1- P(A¬T)/P(AT)
     *
     * @return
     */
    public static float getTraceExampleCounterexampleRate(byte[] reactiveConstraintEvaluation) {
        float[] p = getReactiveProbabilities(reactiveConstraintEvaluation);// result { 0: activation, 1: target, 2: no activation, 3: no target}
        float pA = p[0];
        float pnA = p[2];
        float pT = p[1];
        float pnT = p[3];
        float[] pIntersection = getReactiveIntersectionsProbabilities(reactiveConstraintEvaluation);// result {0: 00, 1: 01, , 2: 10, 3:11}
        float pnAnT = pIntersection[0];
        float pnAT = pIntersection[1];
        float pAnT = pIntersection[2];
        float pAT = pIntersection[3];

        float result = 1 - pAnT / pAT;

        return result;
    }

    /**
     * Retrieve the Example and Counterexample Rate Measure of a constraint for a given trace.
     * <p>
     * The Example and Counterexample Rate measure is defined as:
     * ExampleCounterexampleRate(A->T) = 1- P(A¬T)/P(AT)
     *
     * @return
     */
    public static float getTraceExampleCounterexampleRate(float[] p) {
        float pA = p[0];
        float pnA = p[2];
        float pT = p[1];
        float pnT = p[3];
        float pnAnT = p[4];
        float pnAT = p[5];
        float pAnT = p[6];
        float pAT = p[7];
        float n = p[8];

        float result = 1 - (pAnT / pAT);

        return result;
    }

    /**
     * Retrieve the Zhang Measure of a constraint for a given trace.
     * <p>
     * The Zhang measure is defined as:
     * Zhang(A->T) =  ( P(AT)-P(A)P(T) ) / Max( P(AT)P(¬T), P(T)P(A¬T))
     *
     * @return
     */
    public static float getTraceZhang(byte[] reactiveConstraintEvaluation) {
        float[] p = getReactiveProbabilities(reactiveConstraintEvaluation);// result { 0: activation, 1: target, 2: no activation, 3: no target}
        float pA = p[0];
        float pnA = p[2];
        float pT = p[1];
        float pnT = p[3];
        float[] pIntersection = getReactiveIntersectionsProbabilities(reactiveConstraintEvaluation);// result {0: 00, 1: 01, , 2: 10, 3:11}
        float pnAnT = pIntersection[0];
        float pnAT = pIntersection[1];
        float pAnT = pIntersection[2];
        float pAT = pIntersection[3];

        float result = (pAT - pA * pT) / Math.max(pAT * pnT, pT * pAnT);

        return result;
    }

    /**
     * Retrieve the Zhang Measure of a constraint for a given trace.
     * <p>
     * The Zhang measure is defined as:
     * Zhang(A->T) =  ( P(AT)-P(A)P(T) ) / Max( P(AT)P(¬T), P(T)P(A¬T))
     *
     * @return
     */
    public static float getTraceZhang(float[] p) {
        float pA = p[0];
        float pnA = p[2];
        float pT = p[1];
        float pnT = p[3];
        float pnAnT = p[4];
        float pnAT = p[5];
        float pAnT = p[6];
        float pAT = p[7];
        float n = p[8];

        float result = (pAT - (pA * pT)) / Math.max((pAT * pnT), (pT * pAnT));

        return result;
    }

    /**
     * return the support measure for a given constraint over the entire log
     *
     * @return
     */
//	@Deprecated
    public static float getLogSupport(int constraintIndex, MegaMatrixMonster matrix) {
        return getMeasureAverage(constraintIndex, 0, matrix.getTraceMeasuresMatrix());
//		return getLogDuckTapeMeasures(constraintIndex, 0, matrix.getMatrix());
    }

    /**
     * return the given measure of a constraint over the entire log using the "tape" method:
     * Consider the log as a single trace and compute the measure with the trace methods
     * <p>
     * BEWARE! It is the bottleneck of the aggregated measure output function. Temporary disabled
     *
     * @param constraintIndex
     * @param measureIndex
     * @param bytesMatrix
     * @return
     */
    public static double getLogDuckTapeMeasures(int constraintIndex, int measureIndex, byte[][][] bytesMatrix, boolean nanTraceSubstituteFlag, double nanTraceSubstituteValue) {
        double result = 0;
        byte[] tapeLog = {};

        for (byte[][] trace : bytesMatrix) {
            tapeLog = ArrayUtils.addAll(tapeLog, trace[constraintIndex]);
        }

        return getTraceMeasure(tapeLog, measureIndex, nanTraceSubstituteFlag, nanTraceSubstituteValue);
    }

    /**
     * Compute the probability for a SINGLE constraint over the entire log seen as a single (duck)tape
     *
     * @return
     */
    public static double getLogDuckTapeProbability() {
//		TODO LogDuckTape
        return 0;
    }

    /**
     * return the X measure of a constraint over the entire log as the average of the support within all the traces
     *
     * @return
     */
//	@Deprecated
    public static float getMeasureAverage(int constraintIndex, int measureIndex, float[][][] traceMeasuresMatrix) {
        float result = 0;
        for (float[][] traceEval : traceMeasuresMatrix) {
            result += traceEval[constraintIndex][measureIndex];
        }

        return result / traceMeasuresMatrix.length;
    }


    /**
     * Retrieve the measure distribution info.
     * it takes the results of all the traces and draw the distribution properties.
     * i.e. average value, standard deviation, quartile, max, min
     *
     * @param traceMeasures  array containing the measure value for each trace
     * @param nanLogSkipFlag skip NaN values during the computation
     * @return array with the distribution values
     */
    public static double[] getMeasureDistribution(double[] traceMeasures, boolean nanLogSkipFlag) {
        SummaryStatistics measureDistribution = new SummaryStatistics();
        for (double measure : traceMeasures) {
            if (nanLogSkipFlag && Double.isNaN(measure)) continue;
            measureDistribution.addValue(measure);
        }
        double[] result = {
                measureDistribution.getMean(),
                measureDistribution.getGeometricMean(),
                measureDistribution.getVariance(),
                measureDistribution.getPopulationVariance(),
                measureDistribution.getStandardDeviation(),
                measureDistribution.getMax(),
                measureDistribution.getMin()
        };

        return result;
    }

    /**
     * Returns an object containing the statistic of the measure distribution for a given measure and constraint over the MegaMatrixMonster
     *
     * @param constraintIndex
     * @param measureIndex
     * @param traceMeasuresMatrix
     * @param nanLogSkipFlag
     * @return
     */
    public static SummaryStatistics getMeasureDistributionObject(int constraintIndex, int measureIndex, float[][][] traceMeasuresMatrix, boolean nanLogSkipFlag) {
        SummaryStatistics measureDistribution = new SummaryStatistics();
        for (float[][] traceEval : traceMeasuresMatrix) {
            if (nanLogSkipFlag && Float.isNaN(traceEval[constraintIndex][measureIndex]))
                continue;
            /*
                infinity vales make SummaryStatistics returns NaN for the mean and other stats.
                Either skip them or change the mean function to consider them
            */
            if (Float.isInfinite(traceEval[constraintIndex][measureIndex])) {
                if (traceEval[constraintIndex][measureIndex] > 0)
                    measureDistribution.addValue(Float.MAX_VALUE);
                else
                    measureDistribution.addValue(Float.MIN_VALUE);
            } else
                measureDistribution.addValue(traceEval[constraintIndex][measureIndex]);
        }

        return measureDistribution;
    }

    /**
     * Returns an object containing the statistic of the measure distribution for a given constraint given the matrix result of only one measure over the log
     *
     * @param constraintIndex
     * @param traceMeasureMatrix
     * @param nanLogSkipFlag
     * @return
     */
    public static SummaryStatistics getMeasureDistributionObject(int constraintIndex, float[][] traceMeasureMatrix, boolean nanLogSkipFlag) {
        SummaryStatistics measureDistribution = new SummaryStatistics();
        for (float[] traceEval : traceMeasureMatrix) {
            if (nanLogSkipFlag && Float.isNaN(traceEval[constraintIndex]))
                continue;
            measureDistribution.addValue(traceEval[constraintIndex]);
        }

        return measureDistribution;
    }

    /**
     * given an evaluation array of a reactive constraint, extract the result of only the activator as an array of 0s and 1s
     *
     * @param reactiveConstraintEvaluation
     * @return
     */
    private static byte[] getActivatorEvaluation(byte[] reactiveConstraintEvaluation) {
        byte[] result = new byte[reactiveConstraintEvaluation.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = (byte) (reactiveConstraintEvaluation[i] / 2); // the activator is true if the byte is >1, i.e. 2 or 3
        }
        return result;
    }

    /**
     * given an evaluation array of a reactive constraint, extract the result of only the target as an array of 0s and 1s
     *
     * @param reactiveConstraintEvaluation
     * @return
     */
    private static byte[] getTargetEvaluation(byte[] reactiveConstraintEvaluation) {
        byte[] result = new byte[reactiveConstraintEvaluation.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = (byte) (reactiveConstraintEvaluation[i] % 2); // the target is true if the byte is obb, i,e, 1 or 3
        }
        return result;
    }

    /**
     * Return the inverse result evaluation, i.e., swap of 1 to 0 and vice-versa
     *
     * @param evaluation
     * @return
     */
    private static byte[] getNegativeEvaluation(byte[] evaluation) {
        byte[] result = evaluation.clone();
        for (int i = 0; i < result.length; i++) {
            if (result[i] == 1) {
                result[i] = 0;
            } else {
                result[i] = 1;
            }
        }
        return result;
    }


    /**
     * Return the inverse result evaluation, i.e., swap of 1 to 0 and vice-versa
     *
     * @param reactiveConstraintEvaluation
     * @return
     */
    private static byte[] getNegativeReactiveConstraintEvaluation(byte[] reactiveConstraintEvaluation) {
        byte[] result = reactiveConstraintEvaluation.clone();
        for (int i = 0; i < result.length; i++) {
            result[i] = (byte) (3 - result[i]);
        }
        return result;
    }

    /**
     * Return the logarithm in base 2 of a given number
     *
     * @param number
     * @return
     */
    private static double log2(float number) {
//        return (Math.log(number) / Math.log(2) + 1e-10);
        return Math.log(number) / Math.log(2);
    }
}
