// Generated with Weka 3.6.10
//
// This code is public domain and comes with no warranty.
//
// Timestamp: Sat Mar 01 01:55:36 CET 2014

package eu.liveandgov.wp1.classifier;

public class UKOBClassifier {

    public static String getActivityName(int p) {
        switch (p) {
            case 0:
                return "CYCLING";
            case 1:
                return "RUNNING";
            case 2:
                return "SITTING";
            case 3:
                return "STAIRS";
            case 4:
                return "WALKING";
            default:
                return "UNKNOWN";
        }
    }

    public static double classify(Object[] i)
            throws Exception {

        double p;
        p = UKOBClassifier.N53c36f460(i);
        return p;
    }
    static double N53c36f460(Object []i) {
        double p = Double.NaN;
        if (i[17] == null) {
            p = 0;
        } else if ((Double) i[17] <= 927.0) {
            p = UKOBClassifier.N43be87a01(i);
        } else if ((Double) i[17] > 927.0) {
            p = UKOBClassifier.N2ea0eb2a49(i);
        }
        return p;
    }
    static double N43be87a01(Object []i) {
        double p = Double.NaN;
        if (i[23] == null) {
            p = 0;
        } else if ((Double) i[23] <= 152.0) {
            p = UKOBClassifier.N11ba3c1f2(i);
        } else if ((Double) i[23] > 152.0) {
            p = UKOBClassifier.N3571805748(i);
        }
        return p;
    }
    static double N11ba3c1f2(Object []i) {
        double p = Double.NaN;
        if (i[19] == null) {
            p = 4;
        } else if ((Double) i[19] <= 211.0) {
            p = UKOBClassifier.N59c120503(i);
        } else if ((Double) i[19] > 211.0) {
            p = UKOBClassifier.N7556cd9345(i);
        }
        return p;
    }
    static double N59c120503(Object []i) {
        double p = Double.NaN;
        if (i[3] == null) {
            p = 0;
        } else if ((Double) i[3] <= -2.936108) {
            p = UKOBClassifier.N163092394(i);
        } else if ((Double) i[3] > -2.936108) {
            p = UKOBClassifier.N68ed83637(i);
        }
        return p;
    }
    static double N163092394(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 4;
        } else if ((Double) i[2] <= 1.132095) {
            p = UKOBClassifier.N4ecfe7905(i);
        } else if ((Double) i[2] > 1.132095) {
            p = 0;
        }
        return p;
    }
    static double N4ecfe7905(Object []i) {
        double p = Double.NaN;
        if (i[17] == null) {
            p = 4;
        } else if ((Double) i[17] <= 458.0) {
            p = UKOBClassifier.N336bc75c6(i);
        } else if ((Double) i[17] > 458.0) {
            p = 2;
        }
        return p;
    }
    static double N336bc75c6(Object []i) {
        double p = Double.NaN;
        if (i[34] == null) {
            p = 4;
        } else if ((Double) i[34] <= 2.0) {
            p = 4;
        } else if ((Double) i[34] > 2.0) {
            p = 0;
        }
        return p;
    }
    static double N68ed83637(Object []i) {
        double p = Double.NaN;
        if (i[3] == null) {
            p = 4;
        } else if ((Double) i[3] <= 7.830095) {
            p = UKOBClassifier.N414d0e668(i);
        } else if ((Double) i[3] > 7.830095) {
            p = UKOBClassifier.N1d84bc1044(i);
        }
        return p;
    }
    static double N414d0e668(Object []i) {
        double p = Double.NaN;
        if (i[13] == null) {
            p = 4;
        } else if ((Double) i[13] <= 13.0) {
            p = UKOBClassifier.N29ec6c089(i);
        } else if ((Double) i[13] > 13.0) {
            p = UKOBClassifier.N5c939bdb42(i);
        }
        return p;
    }
    static double N29ec6c089(Object []i) {
        double p = Double.NaN;
        if (i[17] == null) {
            p = 0;
        } else if ((Double) i[17] <= 87.0) {
            p = UKOBClassifier.N60a896b810(i);
        } else if ((Double) i[17] > 87.0) {
            p = UKOBClassifier.N2f26761014(i);
        }
        return p;
    }
    static double N60a896b810(Object []i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 4;
        } else if ((Double) i[1] <= 3.434441) {
            p = UKOBClassifier.N5c3f3b9b11(i);
        } else if ((Double) i[1] > 3.434441) {
            p = UKOBClassifier.N3abc869013(i);
        }
        return p;
    }
    static double N5c3f3b9b11(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 4;
        } else if ((Double) i[2] <= -2.172633) {
            p = UKOBClassifier.N3b626c6d12(i);
        } else if ((Double) i[2] > -2.172633) {
            p = 0;
        }
        return p;
    }
    static double N3b626c6d12(Object []i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 1;
        } else if ((Double) i[1] <= -3.51528) {
            p = 1;
        } else if ((Double) i[1] > -3.51528) {
            p = 4;
        }
        return p;
    }
    static double N3abc869013(Object []i) {
        double p = Double.NaN;
        if (i[18] == null) {
            p = 0;
        } else if ((Double) i[18] <= 469.0) {
            p = 0;
        } else if ((Double) i[18] > 469.0) {
            p = 4;
        }
        return p;
    }
    static double N2f26761014(Object []i) {
        double p = Double.NaN;
        if (i[17] == null) {
            p = 4;
        } else if ((Double) i[17] <= 415.0) {
            p = UKOBClassifier.N6112c9f15(i);
        } else if ((Double) i[17] > 415.0) {
            p = UKOBClassifier.N46e5590e37(i);
        }
        return p;
    }
    static double N6112c9f15(Object []i) {
        double p = Double.NaN;
        if (i[14] == null) {
            p = 4;
        } else if ((Double) i[14] <= 17.0) {
            p = UKOBClassifier.N51887dd516(i);
        } else if ((Double) i[14] > 17.0) {
            p = UKOBClassifier.N13ae8bac26(i);
        }
        return p;
    }
    static double N51887dd516(Object []i) {
        double p = Double.NaN;
        if (i[33] == null) {
            p = 4;
        } else if ((Double) i[33] <= 60.0) {
            p = UKOBClassifier.N57fd54c417(i);
        } else if ((Double) i[33] > 60.0) {
            p = UKOBClassifier.N1c64a58425(i);
        }
        return p;
    }
    static double N57fd54c417(Object []i) {
        double p = Double.NaN;
        if (i[21] == null) {
            p = 4;
        } else if ((Double) i[21] <= 138.0) {
            p = UKOBClassifier.N38c83cfd18(i);
        } else if ((Double) i[21] > 138.0) {
            p = UKOBClassifier.N6bbef7024(i);
        }
        return p;
    }
    static double N38c83cfd18(Object []i) {
        double p = Double.NaN;
        if (i[19] == null) {
            p = 4;
        } else if ((Double) i[19] <= 98.0) {
            p = UKOBClassifier.N621c232a19(i);
        } else if ((Double) i[19] > 98.0) {
            p = UKOBClassifier.N2548ccb820(i);
        }
        return p;
    }
    static double N621c232a19(Object []i) {
        double p = Double.NaN;
        if (i[20] == null) {
            p = 0;
        } else if ((Double) i[20] <= 58.0) {
            p = 0;
        } else if ((Double) i[20] > 58.0) {
            p = 4;
        }
        return p;
    }
    static double N2548ccb820(Object []i) {
        double p = Double.NaN;
        if (i[16] == null) {
            p = 4;
        } else if ((Double) i[16] <= 108.0) {
            p = UKOBClassifier.N4e78572721(i);
        } else if ((Double) i[16] > 108.0) {
            p = UKOBClassifier.N3ea1e9b022(i);
        }
        return p;
    }
    static double N4e78572721(Object []i) {
        double p = Double.NaN;
        if (i[3] == null) {
            p = 4;
        } else if ((Double) i[3] <= 0.398407) {
            p = 4;
        } else if ((Double) i[3] > 0.398407) {
            p = 0;
        }
        return p;
    }
    static double N3ea1e9b022(Object []i) {
        double p = Double.NaN;
        if (i[35] == null) {
            p = 4;
        } else if ((Double) i[35] <= 271.0) {
            p = 4;
        } else if ((Double) i[35] > 271.0) {
            p = UKOBClassifier.N77f541ef23(i);
        }
        return p;
    }
    static double N77f541ef23(Object []i) {
        double p = Double.NaN;
        if (i[22] == null) {
            p = 0;
        } else if ((Double) i[22] <= 34.0) {
            p = 0;
        } else if ((Double) i[22] > 34.0) {
            p = 4;
        }
        return p;
    }
    static double N6bbef7024(Object []i) {
        double p = Double.NaN;
        if (i[13] == null) {
            p = 4;
        } else if ((Double) i[13] <= 1.0) {
            p = 4;
        } else if ((Double) i[13] > 1.0) {
            p = 0;
        }
        return p;
    }
    static double N1c64a58425(Object []i) {
        double p = Double.NaN;
        if (i[21] == null) {
            p = 0;
        } else if ((Double) i[21] <= 74.0) {
            p = 0;
        } else if ((Double) i[21] > 74.0) {
            p = 4;
        }
        return p;
    }
    static double N13ae8bac26(Object []i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 4;
        } else if ((Double) i[1] <= 2.904369) {
            p = UKOBClassifier.N2853d34c27(i);
        } else if ((Double) i[1] > 2.904369) {
            p = UKOBClassifier.N70501e4e34(i);
        }
        return p;
    }
    static double N2853d34c27(Object []i) {
        double p = Double.NaN;
        if (i[11] == null) {
            p = 1;
        } else if ((Double) i[11] <= -0.900072) {
            p = 1;
        } else if ((Double) i[11] > -0.900072) {
            p = UKOBClassifier.N1755374328(i);
        }
        return p;
    }
    static double N1755374328(Object []i) {
        double p = Double.NaN;
        if (i[3] == null) {
            p = 4;
        } else if ((Double) i[3] <= -1.14394) {
            p = UKOBClassifier.N5b9f1bac29(i);
        } else if ((Double) i[3] > -1.14394) {
            p = UKOBClassifier.N2ad1e83230(i);
        }
        return p;
    }
    static double N5b9f1bac29(Object []i) {
        double p = Double.NaN;
        if (i[23] == null) {
            p = 4;
        } else if ((Double) i[23] <= 64.0) {
            p = 4;
        } else if ((Double) i[23] > 64.0) {
            p = 3;
        }
        return p;
    }
    static double N2ad1e83230(Object []i) {
        double p = Double.NaN;
        if (i[18] == null) {
            p = 4;
        } else if ((Double) i[18] <= 221.0) {
            p = UKOBClassifier.N3ae3409431(i);
        } else if ((Double) i[18] > 221.0) {
            p = UKOBClassifier.N30084a7433(i);
        }
        return p;
    }
    static double N3ae3409431(Object []i) {
        double p = Double.NaN;
        if (i[34] == null) {
            p = 4;
        } else if ((Double) i[34] <= 6.0) {
            p = UKOBClassifier.N1da4d2c032(i);
        } else if ((Double) i[34] > 6.0) {
            p = 4;
        }
        return p;
    }
    static double N1da4d2c032(Object []i) {
        double p = Double.NaN;
        if (i[28] == null) {
            p = 0;
        } else if ((Double) i[28] <= 24.0) {
            p = 0;
        } else if ((Double) i[28] > 24.0) {
            p = 4;
        }
        return p;
    }
    static double N30084a7433(Object []i) {
        double p = Double.NaN;
        if (i[29] == null) {
            p = 4;
        } else if ((Double) i[29] <= 94.0) {
            p = 4;
        } else if ((Double) i[29] > 94.0) {
            p = 0;
        }
        return p;
    }
    static double N70501e4e34(Object []i) {
        double p = Double.NaN;
        if (i[9] == null) {
            p = 4;
        } else if ((Double) i[9] <= 0.58851) {
            p = 4;
        } else if ((Double) i[9] > 0.58851) {
            p = UKOBClassifier.N1bf5cc7d35(i);
        }
        return p;
    }
    static double N1bf5cc7d35(Object []i) {
        double p = Double.NaN;
        if (i[6] == null) {
            p = 0;
        } else if ((Double) i[6] <= 14.624495) {
            p = 0;
        } else if ((Double) i[6] > 14.624495) {
            p = UKOBClassifier.N264430e236(i);
        }
        return p;
    }
    static double N264430e236(Object []i) {
        double p = Double.NaN;
        if (i[23] == null) {
            p = 4;
        } else if ((Double) i[23] <= 58.0) {
            p = 4;
        } else if ((Double) i[23] > 58.0) {
            p = 0;
        }
        return p;
    }
    static double N46e5590e37(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 4;
        } else if ((Double) i[2] <= 7.03627) {
            p = UKOBClassifier.N7b39ca7e38(i);
        } else if ((Double) i[2] > 7.03627) {
            p = 0;
        }
        return p;
    }
    static double N7b39ca7e38(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 1;
        } else if ((Double) i[2] <= -9.683737) {
            p = 1;
        } else if ((Double) i[2] > -9.683737) {
            p = UKOBClassifier.Nf1d556639(i);
        }
        return p;
    }
    static double Nf1d556639(Object []i) {
        double p = Double.NaN;
        if (i[22] == null) {
            p = 4;
        } else if ((Double) i[22] <= 7.0) {
            p = UKOBClassifier.N4ed1a34a40(i);
        } else if ((Double) i[22] > 7.0) {
            p = 0;
        }
        return p;
    }
    static double N4ed1a34a40(Object []i) {
        double p = Double.NaN;
        if (i[29] == null) {
            p = 4;
        } else if ((Double) i[29] <= 63.0) {
            p = 4;
        } else if ((Double) i[29] > 63.0) {
            p = UKOBClassifier.N253777b341(i);
        }
        return p;
    }
    static double N253777b341(Object []i) {
        double p = Double.NaN;
        if (i[5] == null) {
            p = 0;
        } else if ((Double) i[5] <= 4.077469) {
            p = 0;
        } else if ((Double) i[5] > 4.077469) {
            p = 4;
        }
        return p;
    }
    static double N5c939bdb42(Object []i) {
        double p = Double.NaN;
        if (i[19] == null) {
            p = 1;
        } else if ((Double) i[19] <= 108.0) {
            p = 1;
        } else if ((Double) i[19] > 108.0) {
            p = UKOBClassifier.N5edd765843(i);
        }
        return p;
    }
    static double N5edd765843(Object []i) {
        double p = Double.NaN;
        if (i[15] == null) {
            p = 4;
        } else if ((Double) i[15] <= 62.0) {
            p = 4;
        } else if ((Double) i[15] > 62.0) {
            p = 3;
        }
        return p;
    }
    static double N1d84bc1044(Object []i) {
        double p = Double.NaN;
        if (i[13] == null) {
            p = 3;
        } else if ((Double) i[13] <= 1.0) {
            p = 3;
        } else if ((Double) i[13] > 1.0) {
            p = 1;
        }
        return p;
    }
    static double N7556cd9345(Object []i) {
        double p = Double.NaN;
        if (i[3] == null) {
            p = 4;
        } else if ((Double) i[3] <= 0.547275) {
            p = UKOBClassifier.N15d2aa1446(i);
        } else if ((Double) i[3] > 0.547275) {
            p = 0;
        }
        return p;
    }
    static double N15d2aa1446(Object []i) {
        double p = Double.NaN;
        if (i[18] == null) {
            p = 4;
        } else if ((Double) i[18] <= 242.0) {
            p = 4;
        } else if ((Double) i[18] > 242.0) {
            p = UKOBClassifier.N399c123d47(i);
        }
        return p;
    }
    static double N399c123d47(Object []i) {
        double p = Double.NaN;
        if (i[9] == null) {
            p = 0;
        } else if ((Double) i[9] <= 0.984152) {
            p = 0;
        } else if ((Double) i[9] > 0.984152) {
            p = 4;
        }
        return p;
    }
    static double N3571805748(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 4;
        } else if ((Double) i[2] <= -9.822539) {
            p = 4;
        } else if ((Double) i[2] > -9.822539) {
            p = 1;
        }
        return p;
    }
    static double N2ea0eb2a49(Object []i) {
        double p = Double.NaN;
        if (i[9] == null) {
            p = 2;
        } else if ((Double) i[9] <= 0.58851) {
            p = 2;
        } else if ((Double) i[9] > 0.58851) {
            p = UKOBClassifier.N46af2a5050(i);
        }
        return p;
    }
    static double N46af2a5050(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 4;
        } else if ((Double) i[2] <= -8.323328) {
            p = 4;
        } else if ((Double) i[2] > -8.323328) {
            p = 0;
        }
        return p;
    }
}

