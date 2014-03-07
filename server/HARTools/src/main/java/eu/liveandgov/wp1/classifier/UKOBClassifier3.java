package eu.liveandgov.wp1.classifier;

import eu.liveandgov.wp1.data.FeatureVector;

// Generated with Weka 3.6.10
//
// This code is public domain and comes with no warranty.
//
// Timestamp: Fri Mar 07 17:38:46 CET 2014
public class UKOBClassifier3 {
    public static String getActivityName(int p) {
        switch (p) {
            case 0:
                return "running";
            case 1:
                return "sitting";
            case 2:
                return "on_table";
            case 3:
                return "walking";
            case 4:
                return "standing";
            default:
                return "unknown";
        }
    }

    public static double classify(FeatureVector v) {

        Object [] mapped = new Object[] {
                (double) 0, // tag
                (double) v.yMean,
                (double) v.yVar,
                (double) v.s2Mean,
                (double) v.s2Var,
                (double) v.tilt,
                (double) v.S2FTBins[0]
        };
        double p = Double.NaN;
        p = UKOBClassifier3.N10c573e40(mapped);
        return p;
    }

    public static double classify(Object[] i)
            throws Exception {

        double p = Double.NaN;
        p = UKOBClassifier3.N10c573e40(i);
        return p;
    }
    static double N10c573e40(Object []i) {
        double p = Double.NaN;
        if (i[4] == null) {
            p = 1;
        } else if (((Double) i[4]).doubleValue() <= 1.105713) {
            p = UKOBClassifier3.N133b86561(i);
        } else if (((Double) i[4]).doubleValue() > 1.105713) {
            p = UKOBClassifier3.N4ba9b56023(i);
        }
        return p;
    }
    static double N133b86561(Object []i) {
        double p = Double.NaN;
        if (i[5] == null) {
            p = 2;
        } else if (((Double) i[5]).doubleValue() <= 0.050954) {
            p = UKOBClassifier3.N28f081f72(i);
        } else if (((Double) i[5]).doubleValue() > 0.050954) {
            p = UKOBClassifier3.N6352aca58(i);
        }
        return p;
    }
    static double N28f081f72(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 2;
        } else if (((Double) i[2]).doubleValue() <= 0.005234) {
            p = UKOBClassifier3.N45e494203(i);
        } else if (((Double) i[2]).doubleValue() > 0.005234) {
            p = UKOBClassifier3.N69f6c9656(i);
        }
        return p;
    }
    static double N45e494203(Object []i) {
        double p = Double.NaN;
        if (i[3] == null) {
            p = 2;
        } else if (((Double) i[3]).doubleValue() <= 9.851572) {
            p = UKOBClassifier3.N21b8e4b94(i);
        } else if (((Double) i[3]).doubleValue() > 9.851572) {
            p = 2;
        }
        return p;
    }
    static double N21b8e4b94(Object []i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 2;
        } else if (((Double) i[1]).doubleValue() <= -0.458121) {
            p = UKOBClassifier3.N71b03efa5(i);
        } else if (((Double) i[1]).doubleValue() > -0.458121) {
            p = 1;
        }
        return p;
    }
    static double N71b03efa5(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 1;
        } else if (((Double) i[2]).doubleValue() <= 0.002575) {
            p = 1;
        } else if (((Double) i[2]).doubleValue() > 0.002575) {
            p = 2;
        }
        return p;
    }
    static double N69f6c9656(Object []i) {
        double p = Double.NaN;
        if (i[3] == null) {
            p = 1;
        } else if (((Double) i[3]).doubleValue() <= 9.928569) {
            p = 1;
        } else if (((Double) i[3]).doubleValue() > 9.928569) {
            p = UKOBClassifier3.N49203c3c7(i);
        }
        return p;
    }
    static double N49203c3c7(Object []i) {
        double p = Double.NaN;
        if (i[3] == null) {
            p = 2;
        } else if (((Double) i[3]).doubleValue() <= 10.051793) {
            p = 2;
        } else if (((Double) i[3]).doubleValue() > 10.051793) {
            p = 3;
        }
        return p;
    }
    static double N6352aca58(Object []i) {
        double p = Double.NaN;
        if (i[5] == null) {
            p = 1;
        } else if (((Double) i[5]).doubleValue() <= 0.923013) {
            p = UKOBClassifier3.N48c4b7949(i);
        } else if (((Double) i[5]).doubleValue() > 0.923013) {
            p = UKOBClassifier3.N7dd3b3aa21(i);
        }
        return p;
    }
    static double N48c4b7949(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 1;
        } else if (((Double) i[2]).doubleValue() <= 0.194465) {
            p = UKOBClassifier3.N6af07ade10(i);
        } else if (((Double) i[2]).doubleValue() > 0.194465) {
            p = UKOBClassifier3.N2d38ca5014(i);
        }
        return p;
    }
    static double N6af07ade10(Object []i) {
        double p = Double.NaN;
        if (i[5] == null) {
            p = 1;
        } else if (((Double) i[5]).doubleValue() <= 0.362933) {
            p = UKOBClassifier3.N5212bbab11(i);
        } else if (((Double) i[5]).doubleValue() > 0.362933) {
            p = UKOBClassifier3.N1de51e8d13(i);
        }
        return p;
    }
    static double N5212bbab11(Object []i) {
        double p = Double.NaN;
        if (i[5] == null) {
            p = 1;
        } else if (((Double) i[5]).doubleValue() <= 0.060368) {
            p = UKOBClassifier3.N4bdf01a512(i);
        } else if (((Double) i[5]).doubleValue() > 0.060368) {
            p = 1;
        }
        return p;
    }
    static double N4bdf01a512(Object []i) {
        double p = Double.NaN;
        if (i[3] == null) {
            p = 1;
        } else if (((Double) i[3]).doubleValue() <= 9.943254) {
            p = 1;
        } else if (((Double) i[3]).doubleValue() > 9.943254) {
            p = 3;
        }
        return p;
    }
    static double N1de51e8d13(Object []i) {
        double p = Double.NaN;
        if (i[4] == null) {
            p = 1;
        } else if (((Double) i[4]).doubleValue() <= 0.011024) {
            p = 1;
        } else if (((Double) i[4]).doubleValue() > 0.011024) {
            p = 3;
        }
        return p;
    }
    static double N2d38ca5014(Object []i) {
        double p = Double.NaN;
        if (i[3] == null) {
            p = 1;
        } else if (((Double) i[3]).doubleValue() <= 9.916767) {
            p = UKOBClassifier3.N6b6a616115(i);
        } else if (((Double) i[3]).doubleValue() > 9.916767) {
            p = UKOBClassifier3.N151f5e5f16(i);
        }
        return p;
    }
    static double N6b6a616115(Object []i) {
        double p = Double.NaN;
        if (i[5] == null) {
            p = 1;
        } else if (((Double) i[5]).doubleValue() <= 0.273936) {
            p = 1;
        } else if (((Double) i[5]).doubleValue() > 0.273936) {
            p = 3;
        }
        return p;
    }
    static double N151f5e5f16(Object []i) {
        double p = Double.NaN;
        if (i[5] == null) {
            p = 1;
        } else if (((Double) i[5]).doubleValue() <= 0.245069) {
            p = UKOBClassifier3.N3e6cb9ce17(i);
        } else if (((Double) i[5]).doubleValue() > 0.245069) {
            p = UKOBClassifier3.N20dda8ad19(i);
        }
        return p;
    }
    static double N3e6cb9ce17(Object []i) {
        double p = Double.NaN;
        if (i[3] == null) {
            p = 1;
        } else if (((Double) i[3]).doubleValue() <= 9.972461) {
            p = UKOBClassifier3.N541aa36618(i);
        } else if (((Double) i[3]).doubleValue() > 9.972461) {
            p = 3;
        }
        return p;
    }
    static double N541aa36618(Object []i) {
        double p = Double.NaN;
        if (i[4] == null) {
            p = 1;
        } else if (((Double) i[4]).doubleValue() <= 0.510102) {
            p = 1;
        } else if (((Double) i[4]).doubleValue() > 0.510102) {
            p = 3;
        }
        return p;
    }
    static double N20dda8ad19(Object []i) {
        double p = Double.NaN;
        if (i[5] == null) {
            p = 3;
        } else if (((Double) i[5]).doubleValue() <= 0.288242) {
            p = UKOBClassifier3.N386d06b620(i);
        } else if (((Double) i[5]).doubleValue() > 0.288242) {
            p = 3;
        }
        return p;
    }
    static double N386d06b620(Object []i) {
        double p = Double.NaN;
        if (i[4] == null) {
            p = 1;
        } else if (((Double) i[4]).doubleValue() <= 0.062213) {
            p = 1;
        } else if (((Double) i[4]).doubleValue() > 0.062213) {
            p = 3;
        }
        return p;
    }
    static double N7dd3b3aa21(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 4;
        } else if (((Double) i[2]).doubleValue() <= 0.113404) {
            p = 4;
        } else if (((Double) i[2]).doubleValue() > 0.113404) {
            p = UKOBClassifier3.N55b89e6f22(i);
        }
        return p;
    }
    static double N55b89e6f22(Object []i) {
        double p = Double.NaN;
        if (i[3] == null) {
            p = 4;
        } else if (((Double) i[3]).doubleValue() <= 9.928355) {
            p = 4;
        } else if (((Double) i[3]).doubleValue() > 9.928355) {
            p = 0;
        }
        return p;
    }
    static double N4ba9b56023(Object []i) {
        double p = Double.NaN;
        if (i[4] == null) {
            p = 3;
        } else if (((Double) i[4]).doubleValue() <= 29.66967) {
            p = UKOBClassifier3.N72c2d86e24(i);
        } else if (((Double) i[4]).doubleValue() > 29.66967) {
            p = UKOBClassifier3.N2ce85fc858(i);
        }
        return p;
    }
    static double N72c2d86e24(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 3;
        } else if (((Double) i[2]).doubleValue() <= 17.484667) {
            p = UKOBClassifier3.N560f589e25(i);
        } else if (((Double) i[2]).doubleValue() > 17.484667) {
            p = UKOBClassifier3.N60691a8948(i);
        }
        return p;
    }
    static double N560f589e25(Object []i) {
        double p = Double.NaN;
        if (i[4] == null) {
            p = 3;
        } else if (((Double) i[4]).doubleValue() <= 13.280125) {
            p = UKOBClassifier3.N983193626(i);
        } else if (((Double) i[4]).doubleValue() > 13.280125) {
            p = UKOBClassifier3.N71da9ff429(i);
        }
        return p;
    }
    static double N983193626(Object []i) {
        double p = Double.NaN;
        if (i[4] == null) {
            p = 3;
        } else if (((Double) i[4]).doubleValue() <= 3.783196) {
            p = UKOBClassifier3.N7dec2d1a27(i);
        } else if (((Double) i[4]).doubleValue() > 3.783196) {
            p = 3;
        }
        return p;
    }
    static double N7dec2d1a27(Object []i) {
        double p = Double.NaN;
        if (i[5] == null) {
            p = 3;
        } else if (((Double) i[5]).doubleValue() <= 0.984085) {
            p = 3;
        } else if (((Double) i[5]).doubleValue() > 0.984085) {
            p = UKOBClassifier3.N1c85468c28(i);
        }
        return p;
    }
    static double N1c85468c28(Object []i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 3;
        } else if (((Double) i[1]).doubleValue() <= -9.910137) {
            p = 3;
        } else if (((Double) i[1]).doubleValue() > -9.910137) {
            p = 0;
        }
        return p;
    }
    static double N71da9ff429(Object []i) {
        double p = Double.NaN;
        if (i[5] == null) {
            p = 3;
        } else if (((Double) i[5]).doubleValue() <= 0.992684) {
            p = UKOBClassifier3.N483b869130(i);
        } else if (((Double) i[5]).doubleValue() > 0.992684) {
            p = UKOBClassifier3.N3c56218544(i);
        }
        return p;
    }
    static double N483b869130(Object []i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 3;
        } else if (((Double) i[1]).doubleValue() <= 1.309428) {
            p = 3;
        } else if (((Double) i[1]).doubleValue() > 1.309428) {
            p = UKOBClassifier3.N3bffbea331(i);
        }
        return p;
    }
    static double N3bffbea331(Object []i) {
        double p = Double.NaN;
        if (i[4] == null) {
            p = 3;
        } else if (((Double) i[4]).doubleValue() <= 18.553131) {
            p = UKOBClassifier3.N133cde1b32(i);
        } else if (((Double) i[4]).doubleValue() > 18.553131) {
            p = UKOBClassifier3.N58b69a7343(i);
        }
        return p;
    }
    static double N133cde1b32(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 3;
        } else if (((Double) i[2]).doubleValue() <= 11.064838) {
            p = UKOBClassifier3.N119c87b33(i);
        } else if (((Double) i[2]).doubleValue() > 11.064838) {
            p = UKOBClassifier3.N250e773e42(i);
        }
        return p;
    }
    static double N119c87b33(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 3;
        } else if (((Double) i[2]).doubleValue() <= 4.110563) {
            p = UKOBClassifier3.N43b903cd34(i);
        } else if (((Double) i[2]).doubleValue() > 4.110563) {
            p = 3;
        }
        return p;
    }
    static double N43b903cd34(Object []i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 3;
        } else if (((Double) i[1]).doubleValue() <= 3.260465) {
            p = 3;
        } else if (((Double) i[1]).doubleValue() > 3.260465) {
            p = UKOBClassifier3.N27a8aa7735(i);
        }
        return p;
    }
    static double N27a8aa7735(Object []i) {
        double p = Double.NaN;
        if (i[4] == null) {
            p = 3;
        } else if (((Double) i[4]).doubleValue() <= 15.42098) {
            p = UKOBClassifier3.N324f7ef836(i);
        } else if (((Double) i[4]).doubleValue() > 15.42098) {
            p = UKOBClassifier3.N988db6a38(i);
        }
        return p;
    }
    static double N324f7ef836(Object []i) {
        double p = Double.NaN;
        if (i[5] == null) {
            p = 3;
        } else if (((Double) i[5]).doubleValue() <= 0.387486) {
            p = UKOBClassifier3.N118e59637(i);
        } else if (((Double) i[5]).doubleValue() > 0.387486) {
            p = 3;
        }
        return p;
    }
    static double N118e59637(Object []i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 3;
        } else if (((Double) i[1]).doubleValue() <= 3.690602) {
            p = 3;
        } else if (((Double) i[1]).doubleValue() > 3.690602) {
            p = 0;
        }
        return p;
    }
    static double N988db6a38(Object []i) {
        double p = Double.NaN;
        if (i[3] == null) {
            p = 3;
        } else if (((Double) i[3]).doubleValue() <= 10.201086) {
            p = 3;
        } else if (((Double) i[3]).doubleValue() > 10.201086) {
            p = UKOBClassifier3.N77fd110939(i);
        }
        return p;
    }
    static double N77fd110939(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 0;
        } else if (((Double) i[2]).doubleValue() <= 3.894996) {
            p = 0;
        } else if (((Double) i[2]).doubleValue() > 3.894996) {
            p = UKOBClassifier3.N769a36a40(i);
        }
        return p;
    }
    static double N769a36a40(Object []i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 0;
        } else if (((Double) i[1]).doubleValue() <= 3.346335) {
            p = 0;
        } else if (((Double) i[1]).doubleValue() > 3.346335) {
            p = UKOBClassifier3.N286787f341(i);
        }
        return p;
    }
    static double N286787f341(Object []i) {
        double p = Double.NaN;
        if (i[4] == null) {
            p = 0;
        } else if (((Double) i[4]).doubleValue() <= 15.557586) {
            p = 0;
        } else if (((Double) i[4]).doubleValue() > 15.557586) {
            p = 3;
        }
        return p;
    }
    static double N250e773e42(Object []i) {
        double p = Double.NaN;
        if (i[4] == null) {
            p = 3;
        } else if (((Double) i[4]).doubleValue() <= 13.731128) {
            p = 3;
        } else if (((Double) i[4]).doubleValue() > 13.731128) {
            p = 0;
        }
        return p;
    }
    static double N58b69a7343(Object []i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 0;
        } else if (((Double) i[1]).doubleValue() <= 4.409575) {
            p = 0;
        } else if (((Double) i[1]).doubleValue() > 4.409575) {
            p = 3;
        }
        return p;
    }
    static double N3c56218544(Object []i) {
        double p = Double.NaN;
        if (i[3] == null) {
            p = 0;
        } else if (((Double) i[3]).doubleValue() <= 11.12501) {
            p = UKOBClassifier3.N3ab2c1b545(i);
        } else if (((Double) i[3]).doubleValue() > 11.12501) {
            p = 3;
        }
        return p;
    }
    static double N3ab2c1b545(Object []i) {
        double p = Double.NaN;
        if (i[5] == null) {
            p = 3;
        } else if (((Double) i[5]).doubleValue() <= 0.994824) {
            p = UKOBClassifier3.N2dcb702e46(i);
        } else if (((Double) i[5]).doubleValue() > 0.994824) {
            p = 0;
        }
        return p;
    }
    static double N2dcb702e46(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 3;
        } else if (((Double) i[2]).doubleValue() <= 13.799113) {
            p = 3;
        } else if (((Double) i[2]).doubleValue() > 13.799113) {
            p = UKOBClassifier3.N731f37f47(i);
        }
        return p;
    }
    static double N731f37f47(Object []i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 0;
        } else if (((Double) i[1]).doubleValue() <= -9.081824) {
            p = 0;
        } else if (((Double) i[1]).doubleValue() > -9.081824) {
            p = 3;
        }
        return p;
    }
    static double N60691a8948(Object []i) {
        double p = Double.NaN;
        if (i[3] == null) {
            p = 0;
        } else if (((Double) i[3]).doubleValue() <= 10.971927) {
            p = UKOBClassifier3.N144d49d249(i);
        } else if (((Double) i[3]).doubleValue() > 10.971927) {
            p = UKOBClassifier3.N3078c8d54(i);
        }
        return p;
    }
    static double N144d49d249(Object []i) {
        double p = Double.NaN;
        if (i[5] == null) {
            p = 3;
        } else if (((Double) i[5]).doubleValue() <= 0.978959) {
            p = UKOBClassifier3.N5e29846750(i);
        } else if (((Double) i[5]).doubleValue() > 0.978959) {
            p = UKOBClassifier3.N3791c44952(i);
        }
        return p;
    }
    static double N5e29846750(Object []i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 3;
        } else if (((Double) i[1]).doubleValue() <= 2.710814) {
            p = 3;
        } else if (((Double) i[1]).doubleValue() > 2.710814) {
            p = UKOBClassifier3.N77b3b67c51(i);
        }
        return p;
    }
    static double N77b3b67c51(Object []i) {
        double p = Double.NaN;
        if (i[4] == null) {
            p = 3;
        } else if (((Double) i[4]).doubleValue() <= 13.573646) {
            p = 3;
        } else if (((Double) i[4]).doubleValue() > 13.573646) {
            p = 0;
        }
        return p;
    }
    static double N3791c44952(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 0;
        } else if (((Double) i[2]).doubleValue() <= 18.848948) {
            p = UKOBClassifier3.N42edb11f53(i);
        } else if (((Double) i[2]).doubleValue() > 18.848948) {
            p = 0;
        }
        return p;
    }
    static double N42edb11f53(Object []i) {
        double p = Double.NaN;
        if (i[4] == null) {
            p = 0;
        } else if (((Double) i[4]).doubleValue() <= 19.895086) {
            p = 0;
        } else if (((Double) i[4]).doubleValue() > 19.895086) {
            p = 3;
        }
        return p;
    }
    static double N3078c8d54(Object []i) {
        double p = Double.NaN;
        if (i[5] == null) {
            p = 0;
        } else if (((Double) i[5]).doubleValue() <= 0.339393) {
            p = 0;
        } else if (((Double) i[5]).doubleValue() > 0.339393) {
            p = UKOBClassifier3.N649c7e8855(i);
        }
        return p;
    }
    static double N649c7e8855(Object []i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 3;
        } else if (((Double) i[1]).doubleValue() <= -10.487063) {
            p = UKOBClassifier3.N5e37465256(i);
        } else if (((Double) i[1]).doubleValue() > -10.487063) {
            p = 3;
        }
        return p;
    }
    static double N5e37465256(Object []i) {
        double p = Double.NaN;
        if (i[3] == null) {
            p = 0;
        } else if (((Double) i[3]).doubleValue() <= 12.075174) {
            p = UKOBClassifier3.N7ee9e1d057(i);
        } else if (((Double) i[3]).doubleValue() > 12.075174) {
            p = 3;
        }
        return p;
    }
    static double N7ee9e1d057(Object []i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 3;
        } else if (((Double) i[1]).doubleValue() <= -10.916328) {
            p = 3;
        } else if (((Double) i[1]).doubleValue() > -10.916328) {
            p = 0;
        }
        return p;
    }
    static double N2ce85fc858(Object []i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 3;
        } else if (((Double) i[1]).doubleValue() <= -9.828011) {
            p = UKOBClassifier3.N4be85a8059(i);
        } else if (((Double) i[1]).doubleValue() > -9.828011) {
            p = UKOBClassifier3.N5f9de71262(i);
        }
        return p;
    }
    static double N4be85a8059(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 3;
        } else if (((Double) i[2]).doubleValue() <= 54.509483) {
            p = 3;
        } else if (((Double) i[2]).doubleValue() > 54.509483) {
            p = UKOBClassifier3.N38db06f60(i);
        }
        return p;
    }
    static double N38db06f60(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 0;
        } else if (((Double) i[2]).doubleValue() <= 56.475075) {
            p = UKOBClassifier3.N4b3a493b61(i);
        } else if (((Double) i[2]).doubleValue() > 56.475075) {
            p = 0;
        }
        return p;
    }
    static double N4b3a493b61(Object []i) {
        double p = Double.NaN;
        if (i[6] == null) {
            p = 0;
        } else if (((Double) i[6]).doubleValue() <= 4.0) {
            p = 0;
        } else if (((Double) i[6]).doubleValue() > 4.0) {
            p = 3;
        }
        return p;
    }
    static double N5f9de71262(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 3;
        } else if (((Double) i[2]).doubleValue() <= 12.159168) {
            p = UKOBClassifier3.N77ad7fc863(i);
        } else if (((Double) i[2]).doubleValue() > 12.159168) {
            p = UKOBClassifier3.N1fa060da64(i);
        }
        return p;
    }
    static double N77ad7fc863(Object []i) {
        double p = Double.NaN;
        if (i[3] == null) {
            p = 3;
        } else if (((Double) i[3]).doubleValue() <= 10.241337) {
            p = 3;
        } else if (((Double) i[3]).doubleValue() > 10.241337) {
            p = 0;
        }
        return p;
    }
    static double N1fa060da64(Object []i) {
        double p = Double.NaN;
        if (i[4] == null) {
            p = 0;
        } else if (((Double) i[4]).doubleValue() <= 37.296032) {
            p = UKOBClassifier3.N5a36986e65(i);
        } else if (((Double) i[4]).doubleValue() > 37.296032) {
            p = 0;
        }
        return p;
    }
    static double N5a36986e65(Object []i) {
        double p = Double.NaN;
        if (i[5] == null) {
            p = 0;
        } else if (((Double) i[5]).doubleValue() <= 0.428868) {
            p = UKOBClassifier3.N36518c0766(i);
        } else if (((Double) i[5]).doubleValue() > 0.428868) {
            p = UKOBClassifier3.N23c03b6d67(i);
        }
        return p;
    }
    static double N36518c0766(Object []i) {
        double p = Double.NaN;
        if (i[3] == null) {
            p = 3;
        } else if (((Double) i[3]).doubleValue() <= 10.789918) {
            p = 3;
        } else if (((Double) i[3]).doubleValue() > 10.789918) {
            p = 0;
        }
        return p;
    }
    static double N23c03b6d67(Object []i) {
        double p = Double.NaN;
        if (i[3] == null) {
            p = 0;
        } else if (((Double) i[3]).doubleValue() <= 10.897606) {
            p = 0;
        } else if (((Double) i[3]).doubleValue() > 10.897606) {
            p = 3;
        }
        return p;
    }
}

