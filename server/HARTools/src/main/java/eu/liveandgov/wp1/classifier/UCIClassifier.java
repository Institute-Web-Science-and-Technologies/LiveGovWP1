// Generated with Weka 3.6.10
//
// This code is public domain and comes with no warranty.
//
// Timestamp: Tue Feb 25 02:02:25 CET 2014

package eu.liveandgov.wp1.classifier;

//import weka.core.Attribute;
//import weka.core.Capabilities;
//import weka.core.Capabilities.Capability;
//import weka.core.Instance;
//import weka.core.Instances;
//import weka.core.RevisionUtils;
//import weka.classifiers.classifier;
//
//public class WekaWrapper
//        extends classifier {
//
//    /**
//     * Returns only the toString() method.
//     *
//     * @return a string describing the classifier
//     */
//    public String globalInfo() {
//        return toString();
//    }
//
//    /**
//     * Returns the capabilities of this classifier.
//     *
//     * @return the capabilities
//     */
//    public Capabilities getCapabilities() {
//        weka.core.Capabilities result = new weka.core.Capabilities(this);
//
//        result.enable(weka.core.Capabilities.Capability.NOMINAL_ATTRIBUTES);
//        result.enable(weka.core.Capabilities.Capability.NUMERIC_ATTRIBUTES);
//        result.enable(weka.core.Capabilities.Capability.DATE_ATTRIBUTES);
//        result.enable(weka.core.Capabilities.Capability.MISSING_VALUES);
//        result.enable(weka.core.Capabilities.Capability.NOMINAL_CLASS);
//        result.enable(weka.core.Capabilities.Capability.MISSING_CLASS_VALUES);
//
//        result.setMinimumNumberInstances(0);
//
//        return result;
//    }
//
//    /**
//     * only checks the data against its capabilities.
//     *
//     * @param i the training data
//     */
//    public void buildClassifier(Instances i) throws Exception {
//        // can classifier handle the data?
//        getCapabilities().testWithFail(i);
//    }
//
//    /**
//     * Classifies the given instance.
//     *
//     * @param i the instance to classify
//     * @return the classification result
//     */
//    public double classifyInstance(Instance i) throws Exception {
//        Object[] s = new Object[i.numAttributes()];
//
//        for (int j = 0; j < s.length; j++) {
//            if (!i.isMissing(j)) {
//                if (i.attribute(j).isNominal())
//                    s[j] = new String(i.stringValue(j));
//                else if (i.attribute(j).isNumeric())
//                    s[j] = new Double(i.value(j));
//            }
//        }
//
//        // set class value to missing
//        s[i.classIndex()] = null;
//
//        return UCIClassifier.classify(s);
//    }
//
//    /**
//     * Returns the revision string.
//     *
//     * @return        the revision
//     */
//    public String getRevision() {
//        return RevisionUtils.extract("1.0");
//    }
//
//    /**
//     * Returns only the classnames and what classifier it is based on.
//     *
//     * @return a short description
//     */
//    public String toString() {
//        return "Auto-generated classifier wrapper, based on weka.classifiers.trees.J48 (generated with Weka 3.6.10).\n" + this.getClass().getName() + "/UCIClassifier";
//    }
//
//    /**
//     * Runs the classfier from commandline.
//     *
//     * @param args the commandline arguments
//     */
//    public static void main(String args[]) {
//        runClassifier(new WekaWrapper(), args);
//    }
//}

public class UCIClassifier {

    public static String getActivityName(int p) {
        switch (p) {
            case 0:
                return "LAYING";
            case 1:
                return "SITTING";
            case 2:
                return "STANDING";
            case 3:
                return "WALKING";
            case 4:
                return "WALKING_DOWNSTAIRS";
            case 5:
                return "WALKING_UPSTAIRS";
            default:
                return "UNKNOWN";
        }
    }

    public static double classify(Object[] i)
            throws Exception {

        double p;
        p = UCIClassifier.N29d602b30(i);
        return p;
    }
    static double N29d602b30(Object []i) {
        double p = Double.NaN;
        if (i[9] == null) {
            p = 0;
        } else if ((Double) i[9] <= 269.954926) {
            p = UCIClassifier.N214b4b3a1(i);
        } else if ((Double) i[9] > 269.954926) {
            p = UCIClassifier.N4cba8fce36(i);
        }
        return p;
    }
    static double N214b4b3a1(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 0;
        } else if ((Double) i[2] <= 2.943442) {
            p = 0;
        } else if ((Double) i[2] > 2.943442) {
            p = UCIClassifier.N5637dde92(i);
        }
        return p;
    }
    static double N5637dde92(Object []i) {
        double p = Double.NaN;
        if (i[3] == null) {
            p = 2;
        } else if ((Double) i[3] <= -0.390226) {
            p = UCIClassifier.N6dca18373(i);
        } else if ((Double) i[3] > -0.390226) {
            p = UCIClassifier.N6173560219(i);
        }
        return p;
    }
    static double N6dca18373(Object []i) {
        double p = Double.NaN;
        if (i[6] == null) {
            p = 2;
        } else if ((Double) i[6] <= 0.700096) {
            p = UCIClassifier.N6efbfb304(i);
        } else if ((Double) i[6] > 0.700096) {
            p = 1;
        }
        return p;
    }
    static double N6efbfb304(Object []i) {
        double p = Double.NaN;
        if (i[4] == null) {
            p = 2;
        } else if ((Double) i[4] <= 1.329958) {
            p = UCIClassifier.N6128453c5(i);
        } else if ((Double) i[4] > 1.329958) {
            p = UCIClassifier.N7371b24615(i);
        }
        return p;
    }
    static double N6128453c5(Object []i) {
        double p = Double.NaN;
        if (i[6] == null) {
            p = 2;
        } else if ((Double) i[6] <= 0.343011) {
            p = UCIClassifier.N1ad997f96(i);
        } else if ((Double) i[6] > 0.343011) {
            p = UCIClassifier.N771931f811(i);
        }
        return p;
    }
    static double N1ad997f96(Object []i) {
        double p = Double.NaN;
        if (i[4] == null) {
            p = 2;
        } else if ((Double) i[4] <= -0.598831) {
            p = UCIClassifier.N43886a347(i);
        } else if ((Double) i[4] > -0.598831) {
            p = 2;
        }
        return p;
    }
    static double N43886a347(Object []i) {
        double p = Double.NaN;
        if (i[5] == null) {
            p = 2;
        } else if ((Double) i[5] <= 0.003981) {
            p = UCIClassifier.N30f49e8f8(i);
        } else if ((Double) i[5] > 0.003981) {
            p = 2;
        }
        return p;
    }
    static double N30f49e8f8(Object []i) {
        double p = Double.NaN;
        if (i[8] == null) {
            p = 1;
        } else if ((Double) i[8] <= 102.136177) {
            p = 1;
        } else if ((Double) i[8] > 102.136177) {
            p = UCIClassifier.Nbd5d7659(i);
        }
        return p;
    }
    static double Nbd5d7659(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 2;
        } else if ((Double) i[2] <= 9.908153) {
            p = 2;
        } else if ((Double) i[2] > 9.908153) {
            p = UCIClassifier.N4302df510(i);
        }
        return p;
    }
    static double N4302df510(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 1;
        } else if ((Double) i[2] <= 9.95014) {
            p = 1;
        } else if ((Double) i[2] > 9.95014) {
            p = 2;
        }
        return p;
    }
    static double N771931f811(Object []i) {
        double p = Double.NaN;
        if (i[5] == null) {
            p = 1;
        } else if ((Double) i[5] <= 0.008142) {
            p = 1;
        } else if ((Double) i[5] > 0.008142) {
            p = UCIClassifier.N171fcdde12(i);
        }
        return p;
    }
    static double N171fcdde12(Object []i) {
        double p = Double.NaN;
        if (i[5] == null) {
            p = 2;
        } else if ((Double) i[5] <= 0.07211) {
            p = UCIClassifier.N2904b5ae13(i);
        } else if ((Double) i[5] > 0.07211) {
            p = 1;
        }
        return p;
    }
    static double N2904b5ae13(Object []i) {
        double p = Double.NaN;
        if (i[8] == null) {
            p = 1;
        } else if ((Double) i[8] <= 101.748245) {
            p = 1;
        } else if ((Double) i[8] > 101.748245) {
            p = UCIClassifier.N7433c78b14(i);
        }
        return p;
    }
    static double N7433c78b14(Object []i) {
        double p = Double.NaN;
        if (i[6] == null) {
            p = 1;
        } else if ((Double) i[6] <= 0.358472) {
            p = 1;
        } else if ((Double) i[6] > 0.358472) {
            p = 2;
        }
        return p;
    }
    static double N7371b24615(Object []i) {
        double p = Double.NaN;
        if (i[9] == null) {
            p = 1;
        } else if ((Double) i[9] <= 0.526522) {
            p = UCIClassifier.N2d6f4ce016(i);
        } else if ((Double) i[9] > 0.526522) {
            p = UCIClassifier.N466c137c18(i);
        }
        return p;
    }
    static double N2d6f4ce016(Object []i) {
        double p = Double.NaN;
        if (i[8] == null) {
            p = 1;
        } else if ((Double) i[8] <= 102.322304) {
            p = UCIClassifier.N66201d6d17(i);
        } else if ((Double) i[8] > 102.322304) {
            p = 2;
        }
        return p;
    }
    static double N66201d6d17(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 2;
        } else if ((Double) i[2] <= 9.533285) {
            p = 2;
        } else if ((Double) i[2] > 9.533285) {
            p = 1;
        }
        return p;
    }
    static double N466c137c18(Object []i) {
        double p = Double.NaN;
        if (i[10] == null) {
            p = 1;
        } else if ((Double) i[10] <= 0.040138) {
            p = 1;
        } else if ((Double) i[10] > 0.040138) {
            p = 2;
        }
        return p;
    }
    static double N6173560219(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 1;
        } else if ((Double) i[2] <= 9.771587) {
            p = 1;
        } else if ((Double) i[2] > 9.771587) {
            p = UCIClassifier.N5b13cf4920(i);
        }
        return p;
    }
    static double N5b13cf4920(Object []i) {
        double p = Double.NaN;
        if (i[3] == null) {
            p = 1;
        } else if ((Double) i[3] <= 0.884546) {
            p = UCIClassifier.N718df05521(i);
        } else if ((Double) i[3] > 0.884546) {
            p = 1;
        }
        return p;
    }
    static double N718df05521(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 1;
        } else if ((Double) i[2] <= 10.063571) {
            p = UCIClassifier.N1d9d96b122(i);
        } else if ((Double) i[2] > 10.063571) {
            p = 2;
        }
        return p;
    }
    static double N1d9d96b122(Object []i) {
        double p = Double.NaN;
        if (i[9] == null) {
            p = 1;
        } else if ((Double) i[9] <= 7.936812) {
            p = UCIClassifier.N55104da723(i);
        } else if ((Double) i[9] > 7.936812) {
            p = UCIClassifier.N69c1f2c33(i);
        }
        return p;
    }
    static double N55104da723(Object []i) {
        double p = Double.NaN;
        if (i[8] == null) {
            p = 2;
        } else if ((Double) i[8] <= 101.028893) {
            p = UCIClassifier.N215a3a9224(i);
        } else if ((Double) i[8] > 101.028893) {
            p = UCIClassifier.N2abb585925(i);
        }
        return p;
    }
    static double N215a3a9224(Object []i) {
        double p = Double.NaN;
        if (i[6] == null) {
            p = 2;
        } else if ((Double) i[6] <= 0.033121) {
            p = 2;
        } else if ((Double) i[6] > 0.033121) {
            p = 1;
        }
        return p;
    }
    static double N2abb585925(Object []i) {
        double p = Double.NaN;
        if (i[8] == null) {
            p = 1;
        } else if ((Double) i[8] <= 101.328201) {
            p = 1;
        } else if ((Double) i[8] > 101.328201) {
            p = UCIClassifier.N71a550f926(i);
        }
        return p;
    }
    static double N71a550f926(Object []i) {
        double p = Double.NaN;
        if (i[12] == null) {
            p = 1;
        } else if ((Double) i[12] <= 5.427856) {
            p = 1;
        } else if ((Double) i[12] > 5.427856) {
            p = UCIClassifier.N1c6745b927(i);
        }
        return p;
    }
    static double N1c6745b927(Object []i) {
        double p = Double.NaN;
        if (i[9] == null) {
            p = 2;
        } else if ((Double) i[9] <= 0.501479) {
            p = 2;
        } else if ((Double) i[9] > 0.501479) {
            p = UCIClassifier.N4012834028(i);
        }
        return p;
    }
    static double N4012834028(Object []i) {
        double p = Double.NaN;
        if (i[4] == null) {
            p = 1;
        } else if ((Double) i[4] <= 0.959302) {
            p = UCIClassifier.N7f66ff9c29(i);
        } else if ((Double) i[4] > 0.959302) {
            p = 2;
        }
        return p;
    }
    static double N7f66ff9c29(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 1;
        } else if ((Double) i[2] <= 10.035295) {
            p = UCIClassifier.N43179c1c30(i);
        } else if ((Double) i[2] > 10.035295) {
            p = UCIClassifier.N4ad38c3d32(i);
        }
        return p;
    }
    static double N43179c1c30(Object []i) {
        double p = Double.NaN;
        if (i[12] == null) {
            p = 1;
        } else if ((Double) i[12] <= 31.792858) {
            p = 1;
        } else if ((Double) i[12] > 31.792858) {
            p = UCIClassifier.N430a14ad31(i);
        }
        return p;
    }
    static double N430a14ad31(Object []i) {
        double p = Double.NaN;
        if (i[9] == null) {
            p = 1;
        } else if ((Double) i[9] <= 2.810832) {
            p = 1;
        } else if ((Double) i[9] > 2.810832) {
            p = 2;
        }
        return p;
    }
    static double N4ad38c3d32(Object []i) {
        double p = Double.NaN;
        if (i[7] == null) {
            p = 2;
        } else if ((Double) i[7] <= 0.081761) {
            p = 2;
        } else if ((Double) i[7] > 0.081761) {
            p = 1;
        }
        return p;
    }
    static double N69c1f2c33(Object []i) {
        double p = Double.NaN;
        if (i[8] == null) {
            p = 1;
        } else if ((Double) i[8] <= 101.308372) {
            p = 1;
        } else if ((Double) i[8] > 101.308372) {
            p = UCIClassifier.N73c2851734(i);
        }
        return p;
    }
    static double N73c2851734(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 2;
        } else if ((Double) i[2] <= 10.015995) {
            p = 2;
        } else if ((Double) i[2] > 10.015995) {
            p = UCIClassifier.N67afe46035(i);
        }
        return p;
    }
    static double N67afe46035(Object []i) {
        double p = Double.NaN;
        if (i[7] == null) {
            p = 2;
        } else if ((Double) i[7] <= 0.179089) {
            p = 2;
        } else if ((Double) i[7] > 0.179089) {
            p = 1;
        }
        return p;
    }
    static double N4cba8fce36(Object []i) {
        double p = Double.NaN;
        if (i[9] == null) {
            p = 3;
        } else if ((Double) i[9] <= 6528.52002) {
            p = UCIClassifier.N6c3744bc37(i);
        } else if ((Double) i[9] > 6528.52002) {
            p = UCIClassifier.N71d9d55b148(i);
        }
        return p;
    }
    static double N6c3744bc37(Object []i) {
        double p = Double.NaN;
        if (i[10] == null) {
            p = 3;
        } else if ((Double) i[10] <= 0.118923) {
            p = UCIClassifier.N2081ca2538(i);
        } else if ((Double) i[10] > 0.118923) {
            p = UCIClassifier.N506dd10860(i);
        }
        return p;
    }
    static double N2081ca2538(Object []i) {
        double p = Double.NaN;
        if (i[10] == null) {
            p = 3;
        } else if ((Double) i[10] <= 0.055807) {
            p = UCIClassifier.N28fe53cf39(i);
        } else if ((Double) i[10] > 0.055807) {
            p = UCIClassifier.N3b48a8e647(i);
        }
        return p;
    }
    static double N28fe53cf39(Object []i) {
        double p = Double.NaN;
        if (i[9] == null) {
            p = 3;
        } else if ((Double) i[9] <= 2090.507568) {
            p = UCIClassifier.N5130500f40(i);
        } else if ((Double) i[9] > 2090.507568) {
            p = UCIClassifier.N3ad8326d41(i);
        }
        return p;
    }
    static double N5130500f40(Object []i) {
        double p = Double.NaN;
        if (i[10] == null) {
            p = 5;
        } else if ((Double) i[10] <= 0.029647) {
            p = 5;
        } else if ((Double) i[10] > 0.029647) {
            p = 3;
        }
        return p;
    }
    static double N3ad8326d41(Object []i) {
        double p = Double.NaN;
        if (i[4] == null) {
            p = 3;
        } else if ((Double) i[4] <= -2.075402) {
            p = 3;
        } else if ((Double) i[4] > -2.075402) {
            p = UCIClassifier.N47d6b04942(i);
        }
        return p;
    }
    static double N47d6b04942(Object []i) {
        double p = Double.NaN;
        if (i[6] == null) {
            p = 5;
        } else if ((Double) i[6] <= 2.959999) {
            p = UCIClassifier.N5fcfad7743(i);
        } else if ((Double) i[6] > 2.959999) {
            p = 3;
        }
        return p;
    }
    static double N5fcfad7743(Object []i) {
        double p = Double.NaN;
        if (i[7] == null) {
            p = 5;
        } else if ((Double) i[7] <= 1.826596) {
            p = UCIClassifier.N3b8590c544(i);
        } else if ((Double) i[7] > 1.826596) {
            p = 5;
        }
        return p;
    }
    static double N3b8590c544(Object []i) {
        double p = Double.NaN;
        if (i[12] == null) {
            p = 5;
        } else if ((Double) i[12] <= 0.870867) {
            p = 5;
        } else if ((Double) i[12] > 0.870867) {
            p = UCIClassifier.N3de3940a45(i);
        }
        return p;
    }
    static double N3de3940a45(Object []i) {
        double p = Double.NaN;
        if (i[8] == null) {
            p = 3;
        } else if ((Double) i[8] <= 112.968475) {
            p = UCIClassifier.N280c3c4446(i);
        } else if ((Double) i[8] > 112.968475) {
            p = 3;
        }
        return p;
    }
    static double N280c3c4446(Object []i) {
        double p = Double.NaN;
        if (i[5] == null) {
            p = 3;
        } else if ((Double) i[5] <= 6.993151) {
            p = 3;
        } else if ((Double) i[5] > 6.993151) {
            p = 5;
        }
        return p;
    }
    static double N3b48a8e647(Object []i) {
        double p = Double.NaN;
        if (i[6] == null) {
            p = 5;
        } else if ((Double) i[6] <= 1.06505) {
            p = UCIClassifier.N1f48b27248(i);
        } else if ((Double) i[6] > 1.06505) {
            p = UCIClassifier.N5dbb6a6949(i);
        }
        return p;
    }
    static double N1f48b27248(Object []i) {
        double p = Double.NaN;
        if (i[5] == null) {
            p = 3;
        } else if ((Double) i[5] <= 2.907737) {
            p = 3;
        } else if ((Double) i[5] > 2.907737) {
            p = 5;
        }
        return p;
    }
    static double N5dbb6a6949(Object []i) {
        double p = Double.NaN;
        if (i[9] == null) {
            p = 3;
        } else if ((Double) i[9] <= 3601.210205) {
            p = 3;
        } else if ((Double) i[9] > 3601.210205) {
            p = UCIClassifier.N3b47439250(i);
        }
        return p;
    }
    static double N3b47439250(Object []i) {
        double p = Double.NaN;
        if (i[7] == null) {
            p = 3;
        } else if ((Double) i[7] <= 1.533426) {
            p = UCIClassifier.N43a544a551(i);
        } else if ((Double) i[7] > 1.533426) {
            p = UCIClassifier.N199de18156(i);
        }
        return p;
    }
    static double N43a544a551(Object []i) {
        double p = Double.NaN;
        if (i[3] == null) {
            p = 5;
        } else if ((Double) i[3] <= -1.002184) {
            p = UCIClassifier.N173dcf5552(i);
        } else if ((Double) i[3] > -1.002184) {
            p = 3;
        }
        return p;
    }
    static double N173dcf5552(Object []i) {
        double p = Double.NaN;
        if (i[6] == null) {
            p = 5;
        } else if ((Double) i[6] <= 3.369114) {
            p = UCIClassifier.N5af6e15e53(i);
        } else if ((Double) i[6] > 3.369114) {
            p = 3;
        }
        return p;
    }
    static double N5af6e15e53(Object []i) {
        double p = Double.NaN;
        if (i[6] == null) {
            p = 3;
        } else if ((Double) i[6] <= 2.106327) {
            p = UCIClassifier.N64610fa54(i);
        } else if ((Double) i[6] > 2.106327) {
            p = UCIClassifier.N6204904d55(i);
        }
        return p;
    }
    static double N64610fa54(Object []i) {
        double p = Double.NaN;
        if (i[4] == null) {
            p = 3;
        } else if ((Double) i[4] <= -0.123062) {
            p = 3;
        } else if ((Double) i[4] > -0.123062) {
            p = 5;
        }
        return p;
    }
    static double N6204904d55(Object []i) {
        double p = Double.NaN;
        if (i[7] == null) {
            p = 3;
        } else if ((Double) i[7] <= 1.161764) {
            p = 3;
        } else if ((Double) i[7] > 1.161764) {
            p = 5;
        }
        return p;
    }
    static double N199de18156(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 3;
        } else if ((Double) i[2] <= 9.277604) {
            p = UCIClassifier.N4c3fe94a57(i);
        } else if ((Double) i[2] > 9.277604) {
            p = UCIClassifier.N77ed206158(i);
        }
        return p;
    }
    static double N4c3fe94a57(Object []i) {
        double p = Double.NaN;
        if (i[4] == null) {
            p = 3;
        } else if ((Double) i[4] <= -3.742124) {
            p = 3;
        } else if ((Double) i[4] > -3.742124) {
            p = 4;
        }
        return p;
    }
    static double N77ed206158(Object []i) {
        double p = Double.NaN;
        if (i[9] == null) {
            p = 3;
        } else if ((Double) i[9] <= 6036.083496) {
            p = 3;
        } else if ((Double) i[9] > 6036.083496) {
            p = UCIClassifier.N70e8fdc959(i);
        }
        return p;
    }
    static double N70e8fdc959(Object []i) {
        double p = Double.NaN;
        if (i[8] == null) {
            p = 5;
        } else if ((Double) i[8] <= 113.944) {
            p = 5;
        } else if ((Double) i[8] > 113.944) {
            p = 3;
        }
        return p;
    }
    static double N506dd10860(Object []i) {
        double p = Double.NaN;
        if (i[10] == null) {
            p = 3;
        } else if ((Double) i[10] <= 0.210035) {
            p = UCIClassifier.N59b68d7861(i);
        } else if ((Double) i[10] > 0.210035) {
            p = UCIClassifier.N37d7f3f4139(i);
        }
        return p;
    }
    static double N59b68d7861(Object []i) {
        double p = Double.NaN;
        if (i[9] == null) {
            p = 3;
        } else if ((Double) i[9] <= 3574.864746) {
            p = UCIClassifier.N6001ef4b62(i);
        } else if ((Double) i[9] > 3574.864746) {
            p = UCIClassifier.N79a8648891(i);
        }
        return p;
    }
    static double N6001ef4b62(Object []i) {
        double p = Double.NaN;
        if (i[7] == null) {
            p = 3;
        } else if ((Double) i[7] <= 1.572092) {
            p = UCIClassifier.N1f05562b63(i);
        } else if ((Double) i[7] > 1.572092) {
            p = UCIClassifier.N377e4cec73(i);
        }
        return p;
    }
    static double N1f05562b63(Object []i) {
        double p = Double.NaN;
        if (i[4] == null) {
            p = 3;
        } else if ((Double) i[4] <= 1.804352) {
            p = UCIClassifier.N175c30f664(i);
        } else if ((Double) i[4] > 1.804352) {
            p = 4;
        }
        return p;
    }
    static double N175c30f664(Object []i) {
        double p = Double.NaN;
        if (i[9] == null) {
            p = 3;
        } else if ((Double) i[9] <= 2927.934326) {
            p = UCIClassifier.N2592727565(i);
        } else if ((Double) i[9] > 2927.934326) {
            p = UCIClassifier.Nf8b729867(i);
        }
        return p;
    }
    static double N2592727565(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 3;
        } else if ((Double) i[2] <= 9.548457) {
            p = UCIClassifier.N3198729866(i);
        } else if ((Double) i[2] > 9.548457) {
            p = 3;
        }
        return p;
    }
    static double N3198729866(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 4;
        } else if ((Double) i[2] <= 9.373204) {
            p = 4;
        } else if ((Double) i[2] > 9.373204) {
            p = 3;
        }
        return p;
    }
    static double Nf8b729867(Object []i) {
        double p = Double.NaN;
        if (i[6] == null) {
            p = 3;
        } else if ((Double) i[6] <= 2.800068) {
            p = UCIClassifier.Nc10612168(i);
        } else if ((Double) i[6] > 2.800068) {
            p = 3;
        }
        return p;
    }
    static double Nc10612168(Object []i) {
        double p = Double.NaN;
        if (i[5] == null) {
            p = 3;
        } else if ((Double) i[5] <= 5.361926) {
            p = 3;
        } else if ((Double) i[5] > 5.361926) {
            p = UCIClassifier.N758c3b769(i);
        }
        return p;
    }
    static double N758c3b769(Object []i) {
        double p = Double.NaN;
        if (i[12] == null) {
            p = 4;
        } else if ((Double) i[12] <= 2.042665) {
            p = UCIClassifier.N5499272570(i);
        } else if ((Double) i[12] > 2.042665) {
            p = 3;
        }
        return p;
    }
    static double N5499272570(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 4;
        } else if ((Double) i[2] <= 9.85298) {
            p = UCIClassifier.N12d9198771(i);
        } else if ((Double) i[2] > 9.85298) {
            p = 4;
        }
        return p;
    }
    static double N12d9198771(Object []i) {
        double p = Double.NaN;
        if (i[12] == null) {
            p = 4;
        } else if ((Double) i[12] <= 1.170892) {
            p = 4;
        } else if ((Double) i[12] > 1.170892) {
            p = UCIClassifier.N6b1af7bb72(i);
        }
        return p;
    }
    static double N6b1af7bb72(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 4;
        } else if ((Double) i[2] <= 9.814983) {
            p = 4;
        } else if ((Double) i[2] > 9.814983) {
            p = 3;
        }
        return p;
    }
    static double N377e4cec73(Object []i) {
        double p = Double.NaN;
        if (i[8] == null) {
            p = 4;
        } else if ((Double) i[8] <= 107.791428) {
            p = UCIClassifier.N44ec366a74(i);
        } else if ((Double) i[8] > 107.791428) {
            p = UCIClassifier.N6fe88c7f75(i);
        }
        return p;
    }
    static double N44ec366a74(Object []i) {
        double p = Double.NaN;
        if (i[10] == null) {
            p = 4;
        } else if ((Double) i[10] <= 0.177631) {
            p = 4;
        } else if ((Double) i[10] > 0.177631) {
            p = 3;
        }
        return p;
    }
    static double N6fe88c7f75(Object []i) {
        double p = Double.NaN;
        if (i[6] == null) {
            p = 3;
        } else if ((Double) i[6] <= 4.86866) {
            p = UCIClassifier.Nc601f3f76(i);
        } else if ((Double) i[6] > 4.86866) {
            p = 4;
        }
        return p;
    }
    static double Nc601f3f76(Object []i) {
        double p = Double.NaN;
        if (i[9] == null) {
            p = 3;
        } else if ((Double) i[9] <= 1671.464111) {
            p = 3;
        } else if ((Double) i[9] > 1671.464111) {
            p = UCIClassifier.N7aa3677177(i);
        }
        return p;
    }
    static double N7aa3677177(Object []i) {
        double p = Double.NaN;
        if (i[12] == null) {
            p = 3;
        } else if ((Double) i[12] <= 2.310261) {
            p = UCIClassifier.N7ddc6a9d78(i);
        } else if ((Double) i[12] > 2.310261) {
            p = 3;
        }
        return p;
    }
    static double N7ddc6a9d78(Object []i) {
        double p = Double.NaN;
        if (i[12] == null) {
            p = 3;
        } else if ((Double) i[12] <= -0.844935) {
            p = 3;
        } else if ((Double) i[12] > -0.844935) {
            p = UCIClassifier.N11dba9f979(i);
        }
        return p;
    }
    static double N11dba9f979(Object []i) {
        double p = Double.NaN;
        if (i[10] == null) {
            p = 3;
        } else if ((Double) i[10] <= 0.204345) {
            p = UCIClassifier.N6c74239780(i);
        } else if ((Double) i[10] > 0.204345) {
            p = UCIClassifier.Nef1df689(i);
        }
        return p;
    }
    static double N6c74239780(Object []i) {
        double p = Double.NaN;
        if (i[3] == null) {
            p = 4;
        } else if ((Double) i[3] <= -2.456705) {
            p = UCIClassifier.N3cccc62181(i);
        } else if ((Double) i[3] > -2.456705) {
            p = UCIClassifier.N27e3bfb682(i);
        }
        return p;
    }
    static double N3cccc62181(Object []i) {
        double p = Double.NaN;
        if (i[12] == null) {
            p = 4;
        } else if ((Double) i[12] <= 0.74279) {
            p = 4;
        } else if ((Double) i[12] > 0.74279) {
            p = 3;
        }
        return p;
    }
    static double N27e3bfb682(Object []i) {
        double p = Double.NaN;
        if (i[7] == null) {
            p = 3;
        } else if ((Double) i[7] <= 3.341181) {
            p = UCIClassifier.N593f5a2f83(i);
        } else if ((Double) i[7] > 3.341181) {
            p = 4;
        }
        return p;
    }
    static double N593f5a2f83(Object []i) {
        double p = Double.NaN;
        if (i[12] == null) {
            p = 3;
        } else if ((Double) i[12] <= 0.802247) {
            p = UCIClassifier.N4e39f16f84(i);
        } else if ((Double) i[12] > 0.802247) {
            p = UCIClassifier.N2ec8f0a487(i);
        }
        return p;
    }
    static double N4e39f16f84(Object []i) {
        double p = Double.NaN;
        if (i[6] == null) {
            p = 4;
        } else if ((Double) i[6] <= 2.461774) {
            p = UCIClassifier.N3e19d68885(i);
        } else if ((Double) i[6] > 2.461774) {
            p = 3;
        }
        return p;
    }
    static double N3e19d68885(Object []i) {
        double p = Double.NaN;
        if (i[5] == null) {
            p = 4;
        } else if ((Double) i[5] <= 6.278641) {
            p = UCIClassifier.N12539a9286(i);
        } else if ((Double) i[5] > 6.278641) {
            p = 3;
        }
        return p;
    }
    static double N12539a9286(Object []i) {
        double p = Double.NaN;
        if (i[10] == null) {
            p = 4;
        } else if ((Double) i[10] <= 0.183699) {
            p = 4;
        } else if ((Double) i[10] > 0.183699) {
            p = 3;
        }
        return p;
    }
    static double N2ec8f0a487(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 3;
        } else if ((Double) i[2] <= 9.858363) {
            p = 3;
        } else if ((Double) i[2] > 9.858363) {
            p = UCIClassifier.Na2eb6fb88(i);
        }
        return p;
    }
    static double Na2eb6fb88(Object []i) {
        double p = Double.NaN;
        if (i[12] == null) {
            p = 3;
        } else if ((Double) i[12] <= 1.19905) {
            p = 3;
        } else if ((Double) i[12] > 1.19905) {
            p = 4;
        }
        return p;
    }
    static double Nef1df689(Object []i) {
        double p = Double.NaN;
        if (i[6] == null) {
            p = 4;
        } else if ((Double) i[6] <= 1.998531) {
            p = UCIClassifier.N529801f490(i);
        } else if ((Double) i[6] > 1.998531) {
            p = 3;
        }
        return p;
    }
    static double N529801f490(Object []i) {
        double p = Double.NaN;
        if (i[5] == null) {
            p = 4;
        } else if ((Double) i[5] <= 5.508829) {
            p = 4;
        } else if ((Double) i[5] > 5.508829) {
            p = 3;
        }
        return p;
    }
    static double N79a8648891(Object []i) {
        double p = Double.NaN;
        if (i[6] == null) {
            p = 5;
        } else if ((Double) i[6] <= 2.200146) {
            p = UCIClassifier.N1e605b1e92(i);
        } else if ((Double) i[6] > 2.200146) {
            p = UCIClassifier.N3a396fce98(i);
        }
        return p;
    }
    static double N1e605b1e92(Object []i) {
        double p = Double.NaN;
        if (i[7] == null) {
            p = 5;
        } else if ((Double) i[7] <= 1.363378) {
            p = UCIClassifier.N47fe1e2693(i);
        } else if ((Double) i[7] > 1.363378) {
            p = UCIClassifier.N4638fb5996(i);
        }
        return p;
    }
    static double N47fe1e2693(Object []i) {
        double p = Double.NaN;
        if (i[4] == null) {
            p = 5;
        } else if ((Double) i[4] <= -0.990013) {
            p = UCIClassifier.N7c6d75b694(i);
        } else if ((Double) i[4] > -0.990013) {
            p = 5;
        }
        return p;
    }
    static double N7c6d75b694(Object []i) {
        double p = Double.NaN;
        if (i[7] == null) {
            p = 5;
        } else if ((Double) i[7] <= 1.161013) {
            p = 5;
        } else if ((Double) i[7] > 1.161013) {
            p = UCIClassifier.N764b3f8b95(i);
        }
        return p;
    }
    static double N764b3f8b95(Object []i) {
        double p = Double.NaN;
        if (i[5] == null) {
            p = 3;
        } else if ((Double) i[5] <= 6.527034) {
            p = 3;
        } else if ((Double) i[5] > 6.527034) {
            p = 5;
        }
        return p;
    }
    static double N4638fb5996(Object []i) {
        double p = Double.NaN;
        if (i[4] == null) {
            p = 3;
        } else if ((Double) i[4] <= 0.616417) {
            p = UCIClassifier.N46fdb41397(i);
        } else if ((Double) i[4] > 0.616417) {
            p = 4;
        }
        return p;
    }
    static double N46fdb41397(Object []i) {
        double p = Double.NaN;
        if (i[12] == null) {
            p = 4;
        } else if ((Double) i[12] <= 0.610828) {
            p = 4;
        } else if ((Double) i[12] > 0.610828) {
            p = 3;
        }
        return p;
    }
    static double N3a396fce98(Object []i) {
        double p = Double.NaN;
        if (i[12] == null) {
            p = 3;
        } else if ((Double) i[12] <= 1.580731) {
            p = UCIClassifier.N10db5b3f99(i);
        } else if ((Double) i[12] > 1.580731) {
            p = UCIClassifier.N30394ffa125(i);
        }
        return p;
    }
    static double N10db5b3f99(Object []i) {
        double p = Double.NaN;
        if (i[12] == null) {
            p = 3;
        } else if ((Double) i[12] <= -0.491765) {
            p = 3;
        } else if ((Double) i[12] > -0.491765) {
            p = UCIClassifier.N31438dbe100(i);
        }
        return p;
    }
    static double N31438dbe100(Object []i) {
        double p = Double.NaN;
        if (i[9] == null) {
            p = 3;
        } else if ((Double) i[9] <= 5669.368652) {
            p = UCIClassifier.N4a0ece36101(i);
        } else if ((Double) i[9] > 5669.368652) {
            p = UCIClassifier.N465098f9122(i);
        }
        return p;
    }
    static double N4a0ece36101(Object []i) {
        double p = Double.NaN;
        if (i[7] == null) {
            p = 4;
        } else if ((Double) i[7] <= 1.108897) {
            p = UCIClassifier.N12046136102(i);
        } else if ((Double) i[7] > 1.108897) {
            p = UCIClassifier.N23dd246105(i);
        }
        return p;
    }
    static double N12046136102(Object []i) {
        double p = Double.NaN;
        if (i[4] == null) {
            p = 5;
        } else if ((Double) i[4] <= -0.515233) {
            p = 5;
        } else if ((Double) i[4] > -0.515233) {
            p = UCIClassifier.N5d8a2977103(i);
        }
        return p;
    }
    static double N5d8a2977103(Object []i) {
        double p = Double.NaN;
        if (i[6] == null) {
            p = 4;
        } else if ((Double) i[6] <= 3.896789) {
            p = UCIClassifier.N19a8739b104(i);
        } else if ((Double) i[6] > 3.896789) {
            p = 3;
        }
        return p;
    }
    static double N19a8739b104(Object []i) {
        double p = Double.NaN;
        if (i[3] == null) {
            p = 4;
        } else if ((Double) i[3] <= -2.246712) {
            p = 4;
        } else if ((Double) i[3] > -2.246712) {
            p = 3;
        }
        return p;
    }
    static double N23dd246105(Object []i) {
        double p = Double.NaN;
        if (i[8] == null) {
            p = 3;
        } else if ((Double) i[8] <= 117.811577) {
            p = UCIClassifier.N28b7f2d0106(i);
        } else if ((Double) i[8] > 117.811577) {
            p = UCIClassifier.N2b115a61119(i);
        }
        return p;
    }
    static double N28b7f2d0106(Object []i) {
        double p = Double.NaN;
        if (i[4] == null) {
            p = 3;
        } else if ((Double) i[4] <= -0.344617) {
            p = UCIClassifier.N44a64a92107(i);
        } else if ((Double) i[4] > -0.344617) {
            p = UCIClassifier.Ndc0adca114(i);
        }
        return p;
    }
    static double N44a64a92107(Object []i) {
        double p = Double.NaN;
        if (i[12] == null) {
            p = 4;
        } else if ((Double) i[12] <= 0.196267) {
            p = UCIClassifier.N169da74108(i);
        } else if ((Double) i[12] > 0.196267) {
            p = UCIClassifier.N2d4c8822110(i);
        }
        return p;
    }
    static double N169da74108(Object []i) {
        double p = Double.NaN;
        if (i[7] == null) {
            p = 4;
        } else if ((Double) i[7] <= 4.411524) {
            p = 4;
        } else if ((Double) i[7] > 4.411524) {
            p = UCIClassifier.N4c84f665109(i);
        }
        return p;
    }
    static double N4c84f665109(Object []i) {
        double p = Double.NaN;
        if (i[3] == null) {
            p = 4;
        } else if ((Double) i[3] <= -2.431347) {
            p = 4;
        } else if ((Double) i[3] > -2.431347) {
            p = 3;
        }
        return p;
    }
    static double N2d4c8822110(Object []i) {
        double p = Double.NaN;
        if (i[7] == null) {
            p = 5;
        } else if ((Double) i[7] <= 1.372293) {
            p = UCIClassifier.N7b7d8769111(i);
        } else if ((Double) i[7] > 1.372293) {
            p = UCIClassifier.N6e453dd5112(i);
        }
        return p;
    }
    static double N7b7d8769111(Object []i) {
        double p = Double.NaN;
        if (i[3] == null) {
            p = 5;
        } else if ((Double) i[3] <= -2.449134) {
            p = 5;
        } else if ((Double) i[3] > -2.449134) {
            p = 3;
        }
        return p;
    }
    static double N6e453dd5112(Object []i) {
        double p = Double.NaN;
        if (i[7] == null) {
            p = 3;
        } else if ((Double) i[7] <= 3.131395) {
            p = 3;
        } else if ((Double) i[7] > 3.131395) {
            p = UCIClassifier.N3dea382113(i);
        }
        return p;
    }
    static double N3dea382113(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 3;
        } else if ((Double) i[2] <= 9.084851) {
            p = 3;
        } else if ((Double) i[2] > 9.084851) {
            p = 5;
        }
        return p;
    }
    static double Ndc0adca114(Object []i) {
        double p = Double.NaN;
        if (i[6] == null) {
            p = 4;
        } else if ((Double) i[6] <= 4.189232) {
            p = UCIClassifier.N64d1afd3115(i);
        } else if ((Double) i[6] > 4.189232) {
            p = UCIClassifier.N73e2bda7118(i);
        }
        return p;
    }
    static double N64d1afd3115(Object []i) {
        double p = Double.NaN;
        if (i[12] == null) {
            p = 3;
        } else if ((Double) i[12] <= 0.265352) {
            p = 3;
        } else if ((Double) i[12] > 0.265352) {
            p = UCIClassifier.N26e795b116(i);
        }
        return p;
    }
    static double N26e795b116(Object []i) {
        double p = Double.NaN;
        if (i[4] == null) {
            p = 4;
        } else if ((Double) i[4] <= 0.999001) {
            p = UCIClassifier.N22e1469c117(i);
        } else if ((Double) i[4] > 0.999001) {
            p = 3;
        }
        return p;
    }
    static double N22e1469c117(Object []i) {
        double p = Double.NaN;
        if (i[10] == null) {
            p = 3;
        } else if ((Double) i[10] <= 0.144513) {
            p = 3;
        } else if ((Double) i[10] > 0.144513) {
            p = 4;
        }
        return p;
    }
    static double N73e2bda7118(Object []i) {
        double p = Double.NaN;
        if (i[3] == null) {
            p = 3;
        } else if ((Double) i[3] <= -2.242316) {
            p = 3;
        } else if ((Double) i[3] > -2.242316) {
            p = 5;
        }
        return p;
    }
    static double N2b115a61119(Object []i) {
        double p = Double.NaN;
        if (i[4] == null) {
            p = 5;
        } else if ((Double) i[4] <= 0.720585) {
            p = UCIClassifier.N44ac5e120(i);
        } else if ((Double) i[4] > 0.720585) {
            p = 3;
        }
        return p;
    }
    static double N44ac5e120(Object []i) {
        double p = Double.NaN;
        if (i[4] == null) {
            p = 4;
        } else if ((Double) i[4] <= -0.08633) {
            p = UCIClassifier.N1c904f75121(i);
        } else if ((Double) i[4] > -0.08633) {
            p = 5;
        }
        return p;
    }
    static double N1c904f75121(Object []i) {
        double p = Double.NaN;
        if (i[6] == null) {
            p = 5;
        } else if ((Double) i[6] <= 2.633541) {
            p = 5;
        } else if ((Double) i[6] > 2.633541) {
            p = 4;
        }
        return p;
    }
    static double N465098f9122(Object []i) {
        double p = Double.NaN;
        if (i[6] == null) {
            p = 3;
        } else if ((Double) i[6] <= 4.757226) {
            p = UCIClassifier.N556b277f123(i);
        } else if ((Double) i[6] > 4.757226) {
            p = UCIClassifier.N6dee2ea8124(i);
        }
        return p;
    }
    static double N556b277f123(Object []i) {
        double p = Double.NaN;
        if (i[7] == null) {
            p = 3;
        } else if ((Double) i[7] <= 3.880378) {
            p = 3;
        } else if ((Double) i[7] > 3.880378) {
            p = 4;
        }
        return p;
    }
    static double N6dee2ea8124(Object []i) {
        double p = Double.NaN;
        if (i[7] == null) {
            p = 5;
        } else if ((Double) i[7] <= 3.492887) {
            p = 5;
        } else if ((Double) i[7] > 3.492887) {
            p = 3;
        }
        return p;
    }
    static double N30394ffa125(Object []i) {
        double p = Double.NaN;
        if (i[7] == null) {
            p = 3;
        } else if ((Double) i[7] <= 1.357591) {
            p = UCIClassifier.N2b1bed2126(i);
        } else if ((Double) i[7] > 1.357591) {
            p = UCIClassifier.N40bf015133(i);
        }
        return p;
    }
    static double N2b1bed2126(Object []i) {
        double p = Double.NaN;
        if (i[10] == null) {
            p = 3;
        } else if ((Double) i[10] <= 0.16523) {
            p = UCIClassifier.N6366ce5f127(i);
        } else if ((Double) i[10] > 0.16523) {
            p = UCIClassifier.N5f0704e1129(i);
        }
        return p;
    }
    static double N6366ce5f127(Object []i) {
        double p = Double.NaN;
        if (i[4] == null) {
            p = 3;
        } else if ((Double) i[4] <= -0.478101) {
            p = UCIClassifier.N7276f1f4128(i);
        } else if ((Double) i[4] > -0.478101) {
            p = 3;
        }
        return p;
    }
    static double N7276f1f4128(Object []i) {
        double p = Double.NaN;
        if (i[5] == null) {
            p = 3;
        } else if ((Double) i[5] <= 7.909524) {
            p = 3;
        } else if ((Double) i[5] > 7.909524) {
            p = 5;
        }
        return p;
    }
    static double N5f0704e1129(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 3;
        } else if ((Double) i[2] <= 9.711042) {
            p = 3;
        } else if ((Double) i[2] > 9.711042) {
            p = UCIClassifier.N45d18084130(i);
        }
        return p;
    }
    static double N45d18084130(Object []i) {
        double p = Double.NaN;
        if (i[12] == null) {
            p = 3;
        } else if ((Double) i[12] <= 1.848191) {
            p = UCIClassifier.N3d4c7deb131(i);
        } else if ((Double) i[12] > 1.848191) {
            p = 4;
        }
        return p;
    }
    static double N3d4c7deb131(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 3;
        } else if ((Double) i[2] <= 9.796209) {
            p = UCIClassifier.N68deeebd132(i);
        } else if ((Double) i[2] > 9.796209) {
            p = 4;
        }
        return p;
    }
    static double N68deeebd132(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 4;
        } else if ((Double) i[2] <= 9.777028) {
            p = 4;
        } else if ((Double) i[2] > 9.777028) {
            p = 3;
        }
        return p;
    }
    static double N40bf015133(Object []i) {
        double p = Double.NaN;
        if (i[4] == null) {
            p = 3;
        } else if ((Double) i[4] <= -0.098079) {
            p = 3;
        } else if ((Double) i[4] > -0.098079) {
            p = UCIClassifier.N2bbef4c6134(i);
        }
        return p;
    }
    static double N2bbef4c6134(Object []i) {
        double p = Double.NaN;
        if (i[9] == null) {
            p = 3;
        } else if ((Double) i[9] <= 5574.191895) {
            p = UCIClassifier.N5b8099a135(i);
        } else if ((Double) i[9] > 5574.191895) {
            p = UCIClassifier.N3bea817f137(i);
        }
        return p;
    }
    static double N5b8099a135(Object []i) {
        double p = Double.NaN;
        if (i[6] == null) {
            p = 3;
        } else if ((Double) i[6] <= 2.351232) {
            p = UCIClassifier.N757e6064136(i);
        } else if ((Double) i[6] > 2.351232) {
            p = 3;
        }
        return p;
    }
    static double N757e6064136(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 3;
        } else if ((Double) i[2] <= 9.73296) {
            p = 3;
        } else if ((Double) i[2] > 9.73296) {
            p = 4;
        }
        return p;
    }
    static double N3bea817f137(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 5;
        } else if ((Double) i[2] <= 9.783417) {
            p = 5;
        } else if ((Double) i[2] > 9.783417) {
            p = UCIClassifier.N20dbd794138(i);
        }
        return p;
    }
    static double N20dbd794138(Object []i) {
        double p = Double.NaN;
        if (i[5] == null) {
            p = 3;
        } else if ((Double) i[5] <= 10.252527) {
            p = 3;
        } else if ((Double) i[5] > 10.252527) {
            p = 5;
        }
        return p;
    }
    static double N37d7f3f4139(Object []i) {
        double p = Double.NaN;
        if (i[12] == null) {
            p = 4;
        } else if ((Double) i[12] <= -0.255376) {
            p = UCIClassifier.N42d134d0140(i);
        } else if ((Double) i[12] > -0.255376) {
            p = UCIClassifier.N34e265f5141(i);
        }
        return p;
    }
    static double N42d134d0140(Object []i) {
        double p = Double.NaN;
        if (i[4] == null) {
            p = 3;
        } else if ((Double) i[4] <= -3.211778) {
            p = 3;
        } else if ((Double) i[4] > -3.211778) {
            p = 4;
        }
        return p;
    }
    static double N34e265f5141(Object []i) {
        double p = Double.NaN;
        if (i[10] == null) {
            p = 4;
        } else if ((Double) i[10] <= 0.315365) {
            p = UCIClassifier.N7b93d2f2142(i);
        } else if ((Double) i[10] > 0.315365) {
            p = 4;
        }
        return p;
    }
    static double N7b93d2f2142(Object []i) {
        double p = Double.NaN;
        if (i[6] == null) {
            p = 4;
        } else if ((Double) i[6] <= 2.394157) {
            p = 4;
        } else if ((Double) i[6] > 2.394157) {
            p = UCIClassifier.N25fe4d40143(i);
        }
        return p;
    }
    static double N25fe4d40143(Object []i) {
        double p = Double.NaN;
        if (i[12] == null) {
            p = 4;
        } else if ((Double) i[12] <= 1.273226) {
            p = UCIClassifier.N5a81b83c144(i);
        } else if ((Double) i[12] > 1.273226) {
            p = 3;
        }
        return p;
    }
    static double N5a81b83c144(Object []i) {
        double p = Double.NaN;
        if (i[9] == null) {
            p = 3;
        } else if ((Double) i[9] <= 4076.038818) {
            p = 3;
        } else if ((Double) i[9] > 4076.038818) {
            p = UCIClassifier.N7a66998f145(i);
        }
        return p;
    }
    static double N7a66998f145(Object []i) {
        double p = Double.NaN;
        if (i[12] == null) {
            p = 4;
        } else if ((Double) i[12] <= -0.025488) {
            p = 4;
        } else if ((Double) i[12] > -0.025488) {
            p = UCIClassifier.N65ebba10146(i);
        }
        return p;
    }
    static double N65ebba10146(Object []i) {
        double p = Double.NaN;
        if (i[7] == null) {
            p = 3;
        } else if ((Double) i[7] <= 2.479808) {
            p = UCIClassifier.N5704a4b6147(i);
        } else if ((Double) i[7] > 2.479808) {
            p = 4;
        }
        return p;
    }
    static double N5704a4b6147(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 3;
        } else if ((Double) i[2] <= 9.576704) {
            p = 3;
        } else if ((Double) i[2] > 9.576704) {
            p = 4;
        }
        return p;
    }
    static double N71d9d55b148(Object []i) {
        double p = Double.NaN;
        if (i[12] == null) {
            p = 5;
        } else if ((Double) i[12] <= -0.159183) {
            p = UCIClassifier.N144683c2149(i);
        } else if ((Double) i[12] > -0.159183) {
            p = UCIClassifier.N21743ff4150(i);
        }
        return p;
    }
    static double N144683c2149(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 4;
        } else if ((Double) i[2] <= 9.340976) {
            p = 4;
        } else if ((Double) i[2] > 9.340976) {
            p = 5;
        }
        return p;
    }
    static double N21743ff4150(Object []i) {
        double p = Double.NaN;
        if (i[10] == null) {
            p = 5;
        } else if ((Double) i[10] <= 0.155706) {
            p = UCIClassifier.N5712bd54151(i);
        } else if ((Double) i[10] > 0.155706) {
            p = UCIClassifier.Ndc737be187(i);
        }
        return p;
    }
    static double N5712bd54151(Object []i) {
        double p = Double.NaN;
        if (i[4] == null) {
            p = 3;
        } else if ((Double) i[4] <= -3.073379) {
            p = UCIClassifier.Nf4c0275152(i);
        } else if ((Double) i[4] > -3.073379) {
            p = UCIClassifier.N4735572b153(i);
        }
        return p;
    }
    static double Nf4c0275152(Object []i) {
        double p = Double.NaN;
        if (i[12] == null) {
            p = 5;
        } else if ((Double) i[12] <= 0.043695) {
            p = 5;
        } else if ((Double) i[12] > 0.043695) {
            p = 3;
        }
        return p;
    }
    static double N4735572b153(Object []i) {
        double p = Double.NaN;
        if (i[8] == null) {
            p = 5;
        } else if ((Double) i[8] <= 114.506012) {
            p = 5;
        } else if ((Double) i[8] > 114.506012) {
            p = UCIClassifier.N7eedec92154(i);
        }
        return p;
    }
    static double N7eedec92154(Object []i) {
        double p = Double.NaN;
        if (i[7] == null) {
            p = 5;
        } else if ((Double) i[7] <= 1.383678) {
            p = UCIClassifier.N3646a658155(i);
        } else if ((Double) i[7] > 1.383678) {
            p = UCIClassifier.N22cb4138161(i);
        }
        return p;
    }
    static double N3646a658155(Object []i) {
        double p = Double.NaN;
        if (i[12] == null) {
            p = 5;
        } else if ((Double) i[12] <= 1.273488) {
            p = UCIClassifier.N5852f73e156(i);
        } else if ((Double) i[12] > 1.273488) {
            p = 3;
        }
        return p;
    }
    static double N5852f73e156(Object []i) {
        double p = Double.NaN;
        if (i[10] == null) {
            p = 5;
        } else if ((Double) i[10] <= 0.117741) {
            p = 5;
        } else if ((Double) i[10] > 0.117741) {
            p = UCIClassifier.N2ee634bf157(i);
        }
        return p;
    }
    static double N2ee634bf157(Object []i) {
        double p = Double.NaN;
        if (i[6] == null) {
            p = 5;
        } else if ((Double) i[6] <= 3.241215) {
            p = 5;
        } else if ((Double) i[6] > 3.241215) {
            p = UCIClassifier.Nb90ffa7158(i);
        }
        return p;
    }
    static double Nb90ffa7158(Object []i) {
        double p = Double.NaN;
        if (i[4] == null) {
            p = 5;
        } else if ((Double) i[4] <= -0.553637) {
            p = 5;
        } else if ((Double) i[4] > -0.553637) {
            p = UCIClassifier.N5c8032df159(i);
        }
        return p;
    }
    static double N5c8032df159(Object []i) {
        double p = Double.NaN;
        if (i[12] == null) {
            p = 5;
        } else if ((Double) i[12] <= 0.478957) {
            p = UCIClassifier.N648bfdea160(i);
        } else if ((Double) i[12] > 0.478957) {
            p = 3;
        }
        return p;
    }
    static double N648bfdea160(Object []i) {
        double p = Double.NaN;
        if (i[7] == null) {
            p = 5;
        } else if ((Double) i[7] <= 0.744339) {
            p = 5;
        } else if ((Double) i[7] > 0.744339) {
            p = 3;
        }
        return p;
    }
    static double N22cb4138161(Object []i) {
        double p = Double.NaN;
        if (i[5] == null) {
            p = 3;
        } else if ((Double) i[5] <= 25.994524) {
            p = UCIClassifier.N4e26d560162(i);
        } else if ((Double) i[5] > 25.994524) {
            p = 5;
        }
        return p;
    }
    static double N4e26d560162(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 5;
        } else if ((Double) i[2] <= 9.740656) {
            p = UCIClassifier.N5782b9b5163(i);
        } else if ((Double) i[2] > 9.740656) {
            p = UCIClassifier.N3bf60430170(i);
        }
        return p;
    }
    static double N5782b9b5163(Object []i) {
        double p = Double.NaN;
        if (i[8] == null) {
            p = 5;
        } else if ((Double) i[8] <= 127.295631) {
            p = UCIClassifier.N476e46f5164(i);
        } else if ((Double) i[8] > 127.295631) {
            p = 3;
        }
        return p;
    }
    static double N476e46f5164(Object []i) {
        double p = Double.NaN;
        if (i[3] == null) {
            p = 5;
        } else if ((Double) i[3] <= 1.044218) {
            p = UCIClassifier.N18f4a376165(i);
        } else if ((Double) i[3] > 1.044218) {
            p = 3;
        }
        return p;
    }
    static double N18f4a376165(Object []i) {
        double p = Double.NaN;
        if (i[7] == null) {
            p = 5;
        } else if ((Double) i[7] <= 1.569137) {
            p = UCIClassifier.N6517a4c6166(i);
        } else if ((Double) i[7] > 1.569137) {
            p = UCIClassifier.N7342f703167(i);
        }
        return p;
    }
    static double N6517a4c6166(Object []i) {
        double p = Double.NaN;
        if (i[6] == null) {
            p = 5;
        } else if ((Double) i[6] <= 4.627631) {
            p = 5;
        } else if ((Double) i[6] > 4.627631) {
            p = 3;
        }
        return p;
    }
    static double N7342f703167(Object []i) {
        double p = Double.NaN;
        if (i[12] == null) {
            p = 5;
        } else if ((Double) i[12] <= 0.094645) {
            p = 5;
        } else if ((Double) i[12] > 0.094645) {
            p = UCIClassifier.N31672113168(i);
        }
        return p;
    }
    static double N31672113168(Object []i) {
        double p = Double.NaN;
        if (i[9] == null) {
            p = 3;
        } else if ((Double) i[9] <= 6863.288574) {
            p = 3;
        } else if ((Double) i[9] > 6863.288574) {
            p = UCIClassifier.N69ac7fbb169(i);
        }
        return p;
    }
    static double N69ac7fbb169(Object []i) {
        double p = Double.NaN;
        if (i[4] == null) {
            p = 5;
        } else if ((Double) i[4] <= 0.262445) {
            p = 5;
        } else if ((Double) i[4] > 0.262445) {
            p = 3;
        }
        return p;
    }
    static double N3bf60430170(Object []i) {
        double p = Double.NaN;
        if (i[9] == null) {
            p = 3;
        } else if ((Double) i[9] <= 7786.189941) {
            p = UCIClassifier.N148d0a11171(i);
        } else if ((Double) i[9] > 7786.189941) {
            p = UCIClassifier.N17db8f8e180(i);
        }
        return p;
    }
    static double N148d0a11171(Object []i) {
        double p = Double.NaN;
        if (i[5] == null) {
            p = 3;
        } else if ((Double) i[5] <= 11.024488) {
            p = UCIClassifier.N378feca1172(i);
        } else if ((Double) i[5] > 11.024488) {
            p = UCIClassifier.N4fff395a174(i);
        }
        return p;
    }
    static double N378feca1172(Object []i) {
        double p = Double.NaN;
        if (i[6] == null) {
            p = 5;
        } else if ((Double) i[6] <= 2.619078) {
            p = 5;
        } else if ((Double) i[6] > 2.619078) {
            p = UCIClassifier.N49f85a86173(i);
        }
        return p;
    }
    static double N49f85a86173(Object []i) {
        double p = Double.NaN;
        if (i[5] == null) {
            p = 3;
        } else if ((Double) i[5] <= 10.939869) {
            p = 3;
        } else if ((Double) i[5] > 10.939869) {
            p = 5;
        }
        return p;
    }
    static double N4fff395a174(Object []i) {
        double p = Double.NaN;
        if (i[10] == null) {
            p = 3;
        } else if ((Double) i[10] <= 0.061946) {
            p = UCIClassifier.N7d0e6cbd175(i);
        } else if ((Double) i[10] > 0.061946) {
            p = 3;
        }
        return p;
    }
    static double N7d0e6cbd175(Object []i) {
        double p = Double.NaN;
        if (i[3] == null) {
            p = 3;
        } else if ((Double) i[3] <= 0.05526) {
            p = UCIClassifier.N3e052c6f176(i);
        } else if ((Double) i[3] > 0.05526) {
            p = 5;
        }
        return p;
    }
    static double N3e052c6f176(Object []i) {
        double p = Double.NaN;
        if (i[6] == null) {
            p = 5;
        } else if ((Double) i[6] <= 1.902067) {
            p = 5;
        } else if ((Double) i[6] > 1.902067) {
            p = UCIClassifier.N45a84b38177(i);
        }
        return p;
    }
    static double N45a84b38177(Object []i) {
        double p = Double.NaN;
        if (i[7] == null) {
            p = 3;
        } else if ((Double) i[7] <= 2.333403) {
            p = UCIClassifier.N2be26d42178(i);
        } else if ((Double) i[7] > 2.333403) {
            p = UCIClassifier.N1e731e90179(i);
        }
        return p;
    }
    static double N2be26d42178(Object []i) {
        double p = Double.NaN;
        if (i[4] == null) {
            p = 3;
        } else if ((Double) i[4] <= 1.416008) {
            p = 3;
        } else if ((Double) i[4] > 1.416008) {
            p = 5;
        }
        return p;
    }
    static double N1e731e90179(Object []i) {
        double p = Double.NaN;
        if (i[3] == null) {
            p = 5;
        } else if ((Double) i[3] <= -0.79639) {
            p = 5;
        } else if ((Double) i[3] > -0.79639) {
            p = 3;
        }
        return p;
    }
    static double N17db8f8e180(Object []i) {
        double p = Double.NaN;
        if (i[6] == null) {
            p = 5;
        } else if ((Double) i[6] <= 3.703726) {
            p = 5;
        } else if ((Double) i[6] > 3.703726) {
            p = UCIClassifier.N4fadbfde181(i);
        }
        return p;
    }
    static double N4fadbfde181(Object []i) {
        double p = Double.NaN;
        if (i[9] == null) {
            p = 3;
        } else if ((Double) i[9] <= 12349.428711) {
            p = UCIClassifier.N180fb0b0182(i);
        } else if ((Double) i[9] > 12349.428711) {
            p = 5;
        }
        return p;
    }
    static double N180fb0b0182(Object []i) {
        double p = Double.NaN;
        if (i[10] == null) {
            p = 3;
        } else if ((Double) i[10] <= 0.068729) {
            p = UCIClassifier.N2e18ff27183(i);
        } else if ((Double) i[10] > 0.068729) {
            p = UCIClassifier.N6546169186(i);
        }
        return p;
    }
    static double N2e18ff27183(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 5;
        } else if ((Double) i[2] <= 9.939621) {
            p = UCIClassifier.N6b177115184(i);
        } else if ((Double) i[2] > 9.939621) {
            p = 3;
        }
        return p;
    }
    static double N6b177115184(Object []i) {
        double p = Double.NaN;
        if (i[12] == null) {
            p = 5;
        } else if ((Double) i[12] <= 0.253681) {
            p = 5;
        } else if ((Double) i[12] > 0.253681) {
            p = UCIClassifier.N500150a0185(i);
        }
        return p;
    }
    static double N500150a0185(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 5;
        } else if ((Double) i[2] <= 9.907705) {
            p = 5;
        } else if ((Double) i[2] > 9.907705) {
            p = 3;
        }
        return p;
    }
    static double N6546169186(Object []i) {
        double p = Double.NaN;
        if (i[4] == null) {
            p = 3;
        } else if ((Double) i[4] <= 1.235643) {
            p = 3;
        } else if ((Double) i[4] > 1.235643) {
            p = 5;
        }
        return p;
    }
    static double Ndc737be187(Object []i) {
        double p = Double.NaN;
        if (i[4] == null) {
            p = 3;
        } else if ((Double) i[4] <= -0.285276) {
            p = UCIClassifier.N121ca203188(i);
        } else if ((Double) i[4] > -0.285276) {
            p = 3;
        }
        return p;
    }
    static double N121ca203188(Object []i) {
        double p = Double.NaN;
        if (i[8] == null) {
            p = 3;
        } else if ((Double) i[8] <= 122.934059) {
            p = UCIClassifier.N15d07c3f189(i);
        } else if ((Double) i[8] > 122.934059) {
            p = UCIClassifier.N3e0339209(i);
        }
        return p;
    }
    static double N15d07c3f189(Object []i) {
        double p = Double.NaN;
        if (i[10] == null) {
            p = 3;
        } else if ((Double) i[10] <= 0.200816) {
            p = UCIClassifier.N2a8d1749190(i);
        } else if ((Double) i[10] > 0.200816) {
            p = UCIClassifier.N3bbe9a50204(i);
        }
        return p;
    }
    static double N2a8d1749190(Object []i) {
        double p = Double.NaN;
        if (i[6] == null) {
            p = 5;
        } else if ((Double) i[6] <= 3.017013) {
            p = 5;
        } else if ((Double) i[6] > 3.017013) {
            p = UCIClassifier.N14f3cf72191(i);
        }
        return p;
    }
    static double N14f3cf72191(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 5;
        } else if ((Double) i[2] <= 9.33498) {
            p = UCIClassifier.N12b8501d192(i);
        } else if ((Double) i[2] > 9.33498) {
            p = UCIClassifier.N24fe9ad1198(i);
        }
        return p;
    }
    static double N12b8501d192(Object []i) {
        double p = Double.NaN;
        if (i[4] == null) {
            p = 3;
        } else if ((Double) i[4] <= -3.396216) {
            p = UCIClassifier.N293a985193(i);
        } else if ((Double) i[4] > -3.396216) {
            p = 5;
        }
        return p;
    }
    static double N293a985193(Object []i) {
        double p = Double.NaN;
        if (i[12] == null) {
            p = 5;
        } else if ((Double) i[12] <= 2.568958) {
            p = UCIClassifier.N2c5e5c15194(i);
        } else if ((Double) i[12] > 2.568958) {
            p = UCIClassifier.N75157f77196(i);
        }
        return p;
    }
    static double N2c5e5c15194(Object []i) {
        double p = Double.NaN;
        if (i[12] == null) {
            p = 3;
        } else if ((Double) i[12] <= 2.499915) {
            p = UCIClassifier.N66ef7d74195(i);
        } else if ((Double) i[12] > 2.499915) {
            p = 5;
        }
        return p;
    }
    static double N66ef7d74195(Object []i) {
        double p = Double.NaN;
        if (i[3] == null) {
            p = 5;
        } else if ((Double) i[3] <= -2.453303) {
            p = 5;
        } else if ((Double) i[3] > -2.453303) {
            p = 3;
        }
        return p;
    }
    static double N75157f77196(Object []i) {
        double p = Double.NaN;
        if (i[4] == null) {
            p = 3;
        } else if ((Double) i[4] <= -3.498443) {
            p = 3;
        } else if ((Double) i[4] > -3.498443) {
            p = UCIClassifier.N566399ae197(i);
        }
        return p;
    }
    static double N566399ae197(Object []i) {
        double p = Double.NaN;
        if (i[7] == null) {
            p = 3;
        } else if ((Double) i[7] <= 5.303316) {
            p = 3;
        } else if ((Double) i[7] > 5.303316) {
            p = 5;
        }
        return p;
    }
    static double N24fe9ad1198(Object []i) {
        double p = Double.NaN;
        if (i[9] == null) {
            p = 5;
        } else if ((Double) i[9] <= 6574.35791) {
            p = 5;
        } else if ((Double) i[9] > 6574.35791) {
            p = UCIClassifier.N47662250199(i);
        }
        return p;
    }
    static double N47662250199(Object []i) {
        double p = Double.NaN;
        if (i[8] == null) {
            p = 3;
        } else if ((Double) i[8] <= 121.792183) {
            p = UCIClassifier.N256d6cf200(i);
        } else if ((Double) i[8] > 121.792183) {
            p = UCIClassifier.N1330b13c201(i);
        }
        return p;
    }
    static double N256d6cf200(Object []i) {
        double p = Double.NaN;
        if (i[5] == null) {
            p = 3;
        } else if ((Double) i[5] <= 17.217524) {
            p = 3;
        } else if ((Double) i[5] > 17.217524) {
            p = 5;
        }
        return p;
    }
    static double N1330b13c201(Object []i) {
        double p = Double.NaN;
        if (i[8] == null) {
            p = 5;
        } else if ((Double) i[8] <= 121.992386) {
            p = 5;
        } else if ((Double) i[8] > 121.992386) {
            p = UCIClassifier.N61c3e3fb202(i);
        }
        return p;
    }
    static double N61c3e3fb202(Object []i) {
        double p = Double.NaN;
        if (i[8] == null) {
            p = 3;
        } else if ((Double) i[8] <= 122.258484) {
            p = 3;
        } else if ((Double) i[8] > 122.258484) {
            p = UCIClassifier.N3aca5e2203(i);
        }
        return p;
    }
    static double N3aca5e2203(Object []i) {
        double p = Double.NaN;
        if (i[3] == null) {
            p = 5;
        } else if ((Double) i[3] <= -2.195144) {
            p = 5;
        } else if ((Double) i[3] > -2.195144) {
            p = 3;
        }
        return p;
    }
    static double N3bbe9a50204(Object []i) {
        double p = Double.NaN;
        if (i[12] == null) {
            p = 4;
        } else if ((Double) i[12] <= 0.162787) {
            p = 4;
        } else if ((Double) i[12] > 0.162787) {
            p = UCIClassifier.N5e8518d4205(i);
        }
        return p;
    }
    static double N5e8518d4205(Object []i) {
        double p = Double.NaN;
        if (i[9] == null) {
            p = 3;
        } else if ((Double) i[9] <= 6685.70166) {
            p = UCIClassifier.N742136c6206(i);
        } else if ((Double) i[9] > 6685.70166) {
            p = UCIClassifier.N30933cba207(i);
        }
        return p;
    }
    static double N742136c6206(Object []i) {
        double p = Double.NaN;
        if (i[4] == null) {
            p = 3;
        } else if ((Double) i[4] <= -1.784408) {
            p = 3;
        } else if ((Double) i[4] > -1.784408) {
            p = 4;
        }
        return p;
    }
    static double N30933cba207(Object []i) {
        double p = Double.NaN;
        if (i[9] == null) {
            p = 3;
        } else if ((Double) i[9] <= 10187.660156) {
            p = 3;
        } else if ((Double) i[9] > 10187.660156) {
            p = UCIClassifier.N1277f040208(i);
        }
        return p;
    }
    static double N1277f040208(Object []i) {
        double p = Double.NaN;
        if (i[8] == null) {
            p = 5;
        } else if ((Double) i[8] <= 122.181946) {
            p = 5;
        } else if ((Double) i[8] > 122.181946) {
            p = 3;
        }
        return p;
    }
    static double N3e0339209(Object []i) {
        double p = Double.NaN;
        if (i[6] == null) {
            p = 5;
        } else if ((Double) i[6] <= 5.693575) {
            p = 5;
        } else if ((Double) i[6] > 5.693575) {
            p = UCIClassifier.N6745934e210(i);
        }
        return p;
    }
    static double N6745934e210(Object []i) {
        double p = Double.NaN;
        if (i[4] == null) {
            p = 3;
        } else if ((Double) i[4] <= -4.222781) {
            p = 3;
        } else if ((Double) i[4] > -4.222781) {
            p = UCIClassifier.N8ca1ada211(i);
        }
        return p;
    }
    static double N8ca1ada211(Object []i) {
        double p = Double.NaN;
        if (i[4] == null) {
            p = 3;
        } else if ((Double) i[4] <= -2.144297) {
            p = UCIClassifier.Nca8e2b8212(i);
        } else if ((Double) i[4] > -2.144297) {
            p = 3;
        }
        return p;
    }
    static double Nca8e2b8212(Object []i) {
        double p = Double.NaN;
        if (i[8] == null) {
            p = 3;
        } else if ((Double) i[8] <= 127.820206) {
            p = UCIClassifier.N23bca486213(i);
        } else if ((Double) i[8] > 127.820206) {
            p = 5;
        }
        return p;
    }
    static double N23bca486213(Object []i) {
        double p = Double.NaN;
        if (i[5] == null) {
            p = 5;
        } else if ((Double) i[5] <= 11.608488) {
            p = UCIClassifier.N35d56bbe214(i);
        } else if ((Double) i[5] > 11.608488) {
            p = 3;
        }
        return p;
    }
    static double N35d56bbe214(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 5;
        } else if ((Double) i[2] <= 8.825693) {
            p = 5;
        } else if ((Double) i[2] > 8.825693) {
            p = 3;
        }
        return p;
    }
}

