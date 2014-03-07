package eu.liveandgov.wp1.classifier;

// Generated with Weka 3.6.10
//
// This code is public domain and comes with no warranty.
//
// Timestamp: Fri Mar 07 15:58:54 CET 2014

public class UKOB_NEW_Classifier {
    public static String getActivityName(int p) {
        switch (p) {
            case 0:
                return "on_table";
            case 1:
                return "running";
            case 2:
                return "sitting";
            case 3:
                return "standing";
            case 4:
                return "walking";
            default:
                return "unknown";
        }
    }

    public static double classify(Object[] i)
            throws Exception {

        double p = Double.NaN;
        p = UKOB_NEW_Classifier.N204ce4dd132(i);
        return p;
    }
    static double N204ce4dd132(Object []i) {
        double p = Double.NaN;
        if (i[3] == null) {
            p = 2;
        } else if (((Double) i[3]).doubleValue() <= -3.147356) {
            p = UKOB_NEW_Classifier.N184579bc133(i);
        } else if (((Double) i[3]).doubleValue() > -3.147356) {
            p = UKOB_NEW_Classifier.N1d558088135(i);
        }
        return p;
    }
    static double N184579bc133(Object []i) {
        double p = Double.NaN;
        if (i[8] == null) {
            p = 2;
        } else if (((Double) i[8]).doubleValue() <= 1.07363) {
            p = 2;
        } else if (((Double) i[8]).doubleValue() > 1.07363) {
            p = UKOB_NEW_Classifier.N793b3216134(i);
        }
        return p;
    }
    static double N793b3216134(Object []i) {
        double p = Double.NaN;
        if (i[18] == null) {
            p = 2;
        } else if (((Double) i[18]).doubleValue() <= 9.0) {
            p = 2;
        } else if (((Double) i[18]).doubleValue() > 9.0) {
            p = 4;
        }
        return p;
    }
    static double N1d558088135(Object []i) {
        double p = Double.NaN;
        if (i[5] == null) {
            p = 0;
        } else if (((Double) i[5]).doubleValue() <= 0.037589) {
            p = UKOB_NEW_Classifier.N58696fc3136(i);
        } else if (((Double) i[5]).doubleValue() > 0.037589) {
            p = UKOB_NEW_Classifier.N47378b88138(i);
        }
        return p;
    }
    static double N58696fc3136(Object []i) {
        double p = Double.NaN;
        if (i[9] == null) {
            p = 0;
        } else if (((Double) i[9]).doubleValue() <= 0.479831) {
            p = 0;
        } else if (((Double) i[9]).doubleValue() > 0.479831) {
            p = UKOB_NEW_Classifier.N7228988d137(i);
        }
        return p;
    }
    static double N7228988d137(Object []i) {
        double p = Double.NaN;
        if (i[9] == null) {
            p = 2;
        } else if (((Double) i[9]).doubleValue() <= 0.923177) {
            p = 2;
        } else if (((Double) i[9]).doubleValue() > 0.923177) {
            p = 3;
        }
        return p;
    }
    static double N47378b88138(Object []i) {
        double p = Double.NaN;
        if (i[6] == null) {
            p = 4;
        } else if (((Double) i[6]).doubleValue() <= 49.781174) {
            p = UKOB_NEW_Classifier.Nfa9b23f139(i);
        } else if (((Double) i[6]).doubleValue() > 49.781174) {
            p = UKOB_NEW_Classifier.N307b158d195(i);
        }
        return p;
    }
    static double Nfa9b23f139(Object []i) {
        double p = Double.NaN;
        if (i[5] == null) {
            p = 4;
        } else if (((Double) i[5]).doubleValue() <= 54.237549) {
            p = UKOB_NEW_Classifier.N4df54e21140(i);
        } else if (((Double) i[5]).doubleValue() > 54.237549) {
            p = 1;
        }
        return p;
    }
    static double N4df54e21140(Object []i) {
        double p = Double.NaN;
        if (i[11] == null) {
            p = 4;
        } else if (((Double) i[11]).doubleValue() <= 100.112328) {
            p = UKOB_NEW_Classifier.N23dc8083141(i);
        } else if (((Double) i[11]).doubleValue() > 100.112328) {
            p = UKOB_NEW_Classifier.N3b39530b194(i);
        }
        return p;
    }
    static double N23dc8083141(Object []i) {
        double p = Double.NaN;
        if (i[4] == null) {
            p = 4;
        } else if (((Double) i[4]).doubleValue() <= 61.716549) {
            p = UKOB_NEW_Classifier.N61792ad9142(i);
        } else if (((Double) i[4]).doubleValue() > 61.716549) {
            p = UKOB_NEW_Classifier.N55d98345193(i);
        }
        return p;
    }
    static double N61792ad9142(Object []i) {
        double p = Double.NaN;
        if (i[5] == null) {
            p = 4;
        } else if (((Double) i[5]).doubleValue() <= 13.149132) {
            p = UKOB_NEW_Classifier.N59ec3e8d143(i);
        } else if (((Double) i[5]).doubleValue() > 13.149132) {
            p = UKOB_NEW_Classifier.N2130126b169(i);
        }
        return p;
    }
    static double N59ec3e8d143(Object []i) {
        double p = Double.NaN;
        if (i[11] == null) {
            p = 4;
        } else if (((Double) i[11]).doubleValue() <= 0.452057) {
            p = UKOB_NEW_Classifier.N24fec91a144(i);
        } else if (((Double) i[11]).doubleValue() > 0.452057) {
            p = UKOB_NEW_Classifier.N6b490981160(i);
        }
        return p;
    }
    static double N24fec91a144(Object []i) {
        double p = Double.NaN;
        if (i[6] == null) {
            p = 4;
        } else if (((Double) i[6]).doubleValue() <= 16.656715) {
            p = UKOB_NEW_Classifier.N5344dcef145(i);
        } else if (((Double) i[6]).doubleValue() > 16.656715) {
            p = UKOB_NEW_Classifier.Nf1ae851155(i);
        }
        return p;
    }
    static double N5344dcef145(Object []i) {
        double p = Double.NaN;
        if (i[6] == null) {
            p = 4;
        } else if (((Double) i[6]).doubleValue() <= 1.000236) {
            p = UKOB_NEW_Classifier.N4e04f99e146(i);
        } else if (((Double) i[6]).doubleValue() > 1.000236) {
            p = UKOB_NEW_Classifier.N28a01c16147(i);
        }
        return p;
    }
    static double N4e04f99e146(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 1;
        } else if (((Double) i[2]).doubleValue() <= -6.271492) {
            p = 1;
        } else if (((Double) i[2]).doubleValue() > -6.271492) {
            p = 4;
        }
        return p;
    }
    static double N28a01c16147(Object []i) {
        double p = Double.NaN;
        if (i[5] == null) {
            p = 4;
        } else if (((Double) i[5]).doubleValue() <= 9.851411) {
            p = 4;
        } else if (((Double) i[5]).doubleValue() > 9.851411) {
            p = UKOB_NEW_Classifier.N2793fd30148(i);
        }
        return p;
    }
    static double N2793fd30148(Object []i) {
        double p = Double.NaN;
        if (i[11] == null) {
            p = 4;
        } else if (((Double) i[11]).doubleValue() <= -0.434301) {
            p = 4;
        } else if (((Double) i[11]).doubleValue() > -0.434301) {
            p = UKOB_NEW_Classifier.N64d36e9c149(i);
        }
        return p;
    }
    static double N64d36e9c149(Object []i) {
        double p = Double.NaN;
        if (i[16] == null) {
            p = 4;
        } else if (((Double) i[16]).doubleValue() <= 13.0) {
            p = 4;
        } else if (((Double) i[16]).doubleValue() > 13.0) {
            p = UKOB_NEW_Classifier.N7502f77a150(i);
        }
        return p;
    }
    static double N7502f77a150(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 4;
        } else if (((Double) i[2]).doubleValue() <= -9.785049) {
            p = 4;
        } else if (((Double) i[2]).doubleValue() > -9.785049) {
            p = UKOB_NEW_Classifier.N15c5a69a151(i);
        }
        return p;
    }
    static double N15c5a69a151(Object []i) {
        double p = Double.NaN;
        if (i[3] == null) {
            p = 4;
        } else if (((Double) i[3]).doubleValue() <= -1.422839) {
            p = 4;
        } else if (((Double) i[3]).doubleValue() > -1.422839) {
            p = UKOB_NEW_Classifier.N633cd3a0152(i);
        }
        return p;
    }
    static double N633cd3a0152(Object []i) {
        double p = Double.NaN;
        if (i[3] == null) {
            p = 1;
        } else if (((Double) i[3]).doubleValue() <= -1.028417) {
            p = 1;
        } else if (((Double) i[3]).doubleValue() > -1.028417) {
            p = UKOB_NEW_Classifier.N2e69e046153(i);
        }
        return p;
    }
    static double N2e69e046153(Object []i) {
        double p = Double.NaN;
        if (i[3] == null) {
            p = 4;
        } else if (((Double) i[3]).doubleValue() <= 7.872425) {
            p = UKOB_NEW_Classifier.N29032b78154(i);
        } else if (((Double) i[3]).doubleValue() > 7.872425) {
            p = 1;
        }
        return p;
    }
    static double N29032b78154(Object []i) {
        double p = Double.NaN;
        if (i[15] == null) {
            p = 4;
        } else if (((Double) i[15]).doubleValue() <= 29.0) {
            p = 4;
        } else if (((Double) i[15]).doubleValue() > 29.0) {
            p = 1;
        }
        return p;
    }
    static double Nf1ae851155(Object []i) {
        double p = Double.NaN;
        if (i[3] == null) {
            p = 4;
        } else if (((Double) i[3]).doubleValue() <= 8.235121) {
            p = 4;
        } else if (((Double) i[3]).doubleValue() > 8.235121) {
            p = UKOB_NEW_Classifier.N2f8a2596156(i);
        }
        return p;
    }
    static double N2f8a2596156(Object []i) {
        double p = Double.NaN;
        if (i[4] == null) {
            p = 4;
        } else if (((Double) i[4]).doubleValue() <= 4.86241) {
            p = 4;
        } else if (((Double) i[4]).doubleValue() > 4.86241) {
            p = UKOB_NEW_Classifier.N16a9b33c157(i);
        }
        return p;
    }
    static double N16a9b33c157(Object []i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 1;
        } else if (((Double) i[1]).doubleValue() <= 2.820256) {
            p = UKOB_NEW_Classifier.N5f3633c3158(i);
        } else if (((Double) i[1]).doubleValue() > 2.820256) {
            p = UKOB_NEW_Classifier.N5f80780a159(i);
        }
        return p;
    }
    static double N5f3633c3158(Object []i) {
        double p = Double.NaN;
        if (i[9] == null) {
            p = 4;
        } else if (((Double) i[9]).doubleValue() <= 0.12637) {
            p = 4;
        } else if (((Double) i[9]).doubleValue() > 0.12637) {
            p = 1;
        }
        return p;
    }
    static double N5f80780a159(Object []i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 4;
        } else if (((Double) i[1]).doubleValue() <= 4.085389) {
            p = 4;
        } else if (((Double) i[1]).doubleValue() > 4.085389) {
            p = 1;
        }
        return p;
    }
    static double N6b490981160(Object []i) {
        double p = Double.NaN;
        if (i[9] == null) {
            p = 4;
        } else if (((Double) i[9]).doubleValue() <= 0.983828) {
            p = UKOB_NEW_Classifier.N80f2b2e161(i);
        } else if (((Double) i[9]).doubleValue() > 0.983828) {
            p = UKOB_NEW_Classifier.N7fbb8353165(i);
        }
        return p;
    }
    static double N80f2b2e161(Object []i) {
        double p = Double.NaN;
        if (i[5] == null) {
            p = 4;
        } else if (((Double) i[5]).doubleValue() <= 10.219557) {
            p = 4;
        } else if (((Double) i[5]).doubleValue() > 10.219557) {
            p = UKOB_NEW_Classifier.N1bdbdd24162(i);
        }
        return p;
    }
    static double N1bdbdd24162(Object []i) {
        double p = Double.NaN;
        if (i[9] == null) {
            p = 1;
        } else if (((Double) i[9]).doubleValue() <= 0.216643) {
            p = UKOB_NEW_Classifier.N7f9374c5163(i);
        } else if (((Double) i[9]).doubleValue() > 0.216643) {
            p = 4;
        }
        return p;
    }
    static double N7f9374c5163(Object []i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 4;
        } else if (((Double) i[1]).doubleValue() <= 0.777919) {
            p = 4;
        } else if (((Double) i[1]).doubleValue() > 0.777919) {
            p = UKOB_NEW_Classifier.N29d772f2164(i);
        }
        return p;
    }
    static double N29d772f2164(Object []i) {
        double p = Double.NaN;
        if (i[17] == null) {
            p = 1;
        } else if (((Double) i[17]).doubleValue() <= 19.0) {
            p = 1;
        } else if (((Double) i[17]).doubleValue() > 19.0) {
            p = 4;
        }
        return p;
    }
    static double N7fbb8353165(Object []i) {
        double p = Double.NaN;
        if (i[6] == null) {
            p = 1;
        } else if (((Double) i[6]).doubleValue() <= 4.230997) {
            p = 1;
        } else if (((Double) i[6]).doubleValue() > 4.230997) {
            p = UKOB_NEW_Classifier.N6faaffa8166(i);
        }
        return p;
    }
    static double N6faaffa8166(Object []i) {
        double p = Double.NaN;
        if (i[3] == null) {
            p = 4;
        } else if (((Double) i[3]).doubleValue() <= -0.643157) {
            p = UKOB_NEW_Classifier.N4376a7de167(i);
        } else if (((Double) i[3]).doubleValue() > -0.643157) {
            p = 4;
        }
        return p;
    }
    static double N4376a7de167(Object []i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 1;
        } else if (((Double) i[1]).doubleValue() <= -0.118966) {
            p = 1;
        } else if (((Double) i[1]).doubleValue() > -0.118966) {
            p = UKOB_NEW_Classifier.N2307026c168(i);
        }
        return p;
    }
    static double N2307026c168(Object []i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 4;
        } else if (((Double) i[1]).doubleValue() <= 1.173586) {
            p = 4;
        } else if (((Double) i[1]).doubleValue() > 1.173586) {
            p = 1;
        }
        return p;
    }
    static double N2130126b169(Object []i) {
        double p = Double.NaN;
        if (i[11] == null) {
            p = 1;
        } else if (((Double) i[11]).doubleValue() <= -0.427478) {
            p = UKOB_NEW_Classifier.N5b093fd2170(i);
        } else if (((Double) i[11]).doubleValue() > -0.427478) {
            p = UKOB_NEW_Classifier.N7da99cc6186(i);
        }
        return p;
    }
    static double N5b093fd2170(Object []i) {
        double p = Double.NaN;
        if (i[7] == null) {
            p = 1;
        } else if (((Double) i[7]).doubleValue() <= 12.444286) {
            p = UKOB_NEW_Classifier.N3c3d22af171(i);
        } else if (((Double) i[7]).doubleValue() > 12.444286) {
            p = UKOB_NEW_Classifier.N5a8d77a3185(i);
        }
        return p;
    }
    static double N3c3d22af171(Object []i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 4;
        } else if (((Double) i[1]).doubleValue() <= -2.788938) {
            p = 4;
        } else if (((Double) i[1]).doubleValue() > -2.788938) {
            p = UKOB_NEW_Classifier.N51b02e0e172(i);
        }
        return p;
    }
    static double N51b02e0e172(Object []i) {
        double p = Double.NaN;
        if (i[8] == null) {
            p = 4;
        } else if (((Double) i[8]).doubleValue() <= 18.259687) {
            p = UKOB_NEW_Classifier.N59fc308173(i);
        } else if (((Double) i[8]).doubleValue() > 18.259687) {
            p = UKOB_NEW_Classifier.N5c66ba68180(i);
        }
        return p;
    }
    static double N59fc308173(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 1;
        } else if (((Double) i[2]).doubleValue() <= -9.028058) {
            p = UKOB_NEW_Classifier.N3bbd451a174(i);
        } else if (((Double) i[2]).doubleValue() > -9.028058) {
            p = UKOB_NEW_Classifier.N712d0e5178(i);
        }
        return p;
    }
    static double N3bbd451a174(Object []i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 1;
        } else if (((Double) i[1]).doubleValue() <= 0.054357) {
            p = UKOB_NEW_Classifier.N703cc9a175(i);
        } else if (((Double) i[1]).doubleValue() > 0.054357) {
            p = 4;
        }
        return p;
    }
    static double N703cc9a175(Object []i) {
        double p = Double.NaN;
        if (i[9] == null) {
            p = 4;
        } else if (((Double) i[9]).doubleValue() <= 0.988371) {
            p = 4;
        } else if (((Double) i[9]).doubleValue() > 0.988371) {
            p = UKOB_NEW_Classifier.Na6d960f176(i);
        }
        return p;
    }
    static double Na6d960f176(Object []i) {
        double p = Double.NaN;
        if (i[5] == null) {
            p = 1;
        } else if (((Double) i[5]).doubleValue() <= 13.414605) {
            p = UKOB_NEW_Classifier.N2096b822177(i);
        } else if (((Double) i[5]).doubleValue() > 13.414605) {
            p = 1;
        }
        return p;
    }
    static double N2096b822177(Object []i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 1;
        } else if (((Double) i[1]).doubleValue() <= -0.116156) {
            p = 1;
        } else if (((Double) i[1]).doubleValue() > -0.116156) {
            p = 4;
        }
        return p;
    }
    static double N712d0e5178(Object []i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 4;
        } else if (((Double) i[1]).doubleValue() <= -0.515174) {
            p = UKOB_NEW_Classifier.N64506e03179(i);
        } else if (((Double) i[1]).doubleValue() > -0.515174) {
            p = 4;
        }
        return p;
    }
    static double N64506e03179(Object []i) {
        double p = Double.NaN;
        if (i[3] == null) {
            p = 4;
        } else if (((Double) i[3]).doubleValue() <= 7.711304) {
            p = 4;
        } else if (((Double) i[3]).doubleValue() > 7.711304) {
            p = 1;
        }
        return p;
    }
    static double N5c66ba68180(Object []i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 1;
        } else if (((Double) i[1]).doubleValue() <= 7.946371) {
            p = UKOB_NEW_Classifier.N5c58313c181(i);
        } else if (((Double) i[1]).doubleValue() > 7.946371) {
            p = 4;
        }
        return p;
    }
    static double N5c58313c181(Object []i) {
        double p = Double.NaN;
        if (i[6] == null) {
            p = 4;
        } else if (((Double) i[6]).doubleValue() <= 8.975226) {
            p = UKOB_NEW_Classifier.N22088981182(i);
        } else if (((Double) i[6]).doubleValue() > 8.975226) {
            p = UKOB_NEW_Classifier.N5e7b859b183(i);
        }
        return p;
    }
    static double N22088981182(Object []i) {
        double p = Double.NaN;
        if (i[15] == null) {
            p = 1;
        } else if (((Double) i[15]).doubleValue() <= 16.0) {
            p = 1;
        } else if (((Double) i[15]).doubleValue() > 16.0) {
            p = 4;
        }
        return p;
    }
    static double N5e7b859b183(Object []i) {
        double p = Double.NaN;
        if (i[11] == null) {
            p = 1;
        } else if (((Double) i[11]).doubleValue() <= -0.609702) {
            p = 1;
        } else if (((Double) i[11]).doubleValue() > -0.609702) {
            p = UKOB_NEW_Classifier.N7f80b392184(i);
        }
        return p;
    }
    static double N7f80b392184(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 4;
        } else if (((Double) i[2]).doubleValue() <= -6.059775) {
            p = 4;
        } else if (((Double) i[2]).doubleValue() > -6.059775) {
            p = 1;
        }
        return p;
    }
    static double N5a8d77a3185(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 4;
        } else if (((Double) i[2]).doubleValue() <= -10.409928) {
            p = 4;
        } else if (((Double) i[2]).doubleValue() > -10.409928) {
            p = 1;
        }
        return p;
    }
    static double N7da99cc6186(Object []i) {
        double p = Double.NaN;
        if (i[3] == null) {
            p = 4;
        } else if (((Double) i[3]).doubleValue() <= 5.222265) {
            p = UKOB_NEW_Classifier.Na73cb9e187(i);
        } else if (((Double) i[3]).doubleValue() > 5.222265) {
            p = UKOB_NEW_Classifier.N6c0c10eb191(i);
        }
        return p;
    }
    static double Na73cb9e187(Object []i) {
        double p = Double.NaN;
        if (i[7] == null) {
            p = 4;
        } else if (((Double) i[7]).doubleValue() <= 10.370795) {
            p = UKOB_NEW_Classifier.N383cf76e188(i);
        } else if (((Double) i[7]).doubleValue() > 10.370795) {
            p = UKOB_NEW_Classifier.N2a98739a189(i);
        }
        return p;
    }
    static double N383cf76e188(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 1;
        } else if (((Double) i[2]).doubleValue() <= -8.81798) {
            p = 1;
        } else if (((Double) i[2]).doubleValue() > -8.81798) {
            p = 4;
        }
        return p;
    }
    static double N2a98739a189(Object []i) {
        double p = Double.NaN;
        if (i[6] == null) {
            p = 4;
        } else if (((Double) i[6]).doubleValue() <= 9.88914) {
            p = UKOB_NEW_Classifier.N7ecd994e190(i);
        } else if (((Double) i[6]).doubleValue() > 9.88914) {
            p = 4;
        }
        return p;
    }
    static double N7ecd994e190(Object []i) {
        double p = Double.NaN;
        if (i[9] == null) {
            p = 4;
        } else if (((Double) i[9]).doubleValue() <= 0.975227) {
            p = 4;
        } else if (((Double) i[9]).doubleValue() > 0.975227) {
            p = 1;
        }
        return p;
    }
    static double N6c0c10eb191(Object []i) {
        double p = Double.NaN;
        if (i[8] == null) {
            p = 4;
        } else if (((Double) i[8]).doubleValue() <= 15.139568) {
            p = 4;
        } else if (((Double) i[8]).doubleValue() > 15.139568) {
            p = UKOB_NEW_Classifier.Nc2aebb8192(i);
        }
        return p;
    }
    static double Nc2aebb8192(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 1;
        } else if (((Double) i[2]).doubleValue() <= 4.638933) {
            p = 1;
        } else if (((Double) i[2]).doubleValue() > 4.638933) {
            p = 4;
        }
        return p;
    }
    static double N55d98345193(Object []i) {
        double p = Double.NaN;
        if (i[9] == null) {
            p = 1;
        } else if (((Double) i[9]).doubleValue() <= 0.382263) {
            p = 1;
        } else if (((Double) i[9]).doubleValue() > 0.382263) {
            p = 4;
        }
        return p;
    }
    static double N3b39530b194(Object []i) {
        double p = Double.NaN;
        if (i[13] == null) {
            p = 0;
        } else if (((Double) i[13]).doubleValue() <= 177.0) {
            p = 0;
        } else if (((Double) i[13]).doubleValue() > 177.0) {
            p = 3;
        }
        return p;
    }
    static double N307b158d195(Object []i) {
        double p = Double.NaN;
        if (i[11] == null) {
            p = 1;
        } else if (((Double) i[11]).doubleValue() <= 0.475229) {
            p = UKOB_NEW_Classifier.N60c3f4d8196(i);
        } else if (((Double) i[11]).doubleValue() > 0.475229) {
            p = 4;
        }
        return p;
    }
    static double N60c3f4d8196(Object []i) {
        double p = Double.NaN;
        if (i[3] == null) {
            p = 1;
        } else if (((Double) i[3]).doubleValue() <= -0.719296) {
            p = UKOB_NEW_Classifier.N60ffc289197(i);
        } else if (((Double) i[3]).doubleValue() > -0.719296) {
            p = 1;
        }
        return p;
    }
    static double N60ffc289197(Object []i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 1;
        } else if (((Double) i[1]).doubleValue() <= 0.430599) {
            p = 1;
        } else if (((Double) i[1]).doubleValue() > 0.430599) {
            p = 4;
        }
        return p;
    }
}

