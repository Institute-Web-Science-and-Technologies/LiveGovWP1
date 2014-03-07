package eu.liveandgov.wp1.classifier;

// Generated with Weka 3.6.10
//
// This code is public domain and comes with no warranty.
//
// Timestamp: Fri Mar 07 17:38:46 CET 2014
public class UKOBClassifier2 {
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
        p = UKOBClassifier2.N32a49591264(i);
        return p;
    }
    static double N32a49591264(Object []i) {
        double p = Double.NaN;
        if (i[4] == null) {
            p = 2;
        } else if (((Double) i[4]).doubleValue() <= -3.147295) {
            p = UKOBClassifier2.N53577c90265(i);
        } else if (((Double) i[4]).doubleValue() > -3.147295) {
            p = UKOBClassifier2.N72eb4fc6267(i);
        }
        return p;
    }
    static double N53577c90265(Object []i) {
        double p = Double.NaN;
        if (i[13] == null) {
            p = 2;
        } else if (((Double) i[13]).doubleValue() <= 41.0) {
            p = 2;
        } else if (((Double) i[13]).doubleValue() > 41.0) {
            p = UKOBClassifier2.N14b2fcaf266(i);
        }
        return p;
    }
    static double N14b2fcaf266(Object []i) {
        double p = Double.NaN;
        if (i[17] == null) {
            p = 2;
        } else if (((Double) i[17]).doubleValue() <= 9.0) {
            p = 2;
        } else if (((Double) i[17]).doubleValue() > 9.0) {
            p = 4;
        }
        return p;
    }
    static double N72eb4fc6267(Object []i) {
        double p = Double.NaN;
        if (i[6] == null) {
            p = 0;
        } else if (((Double) i[6]).doubleValue() <= 0.054857) {
            p = UKOBClassifier2.N36c28b1b268(i);
        } else if (((Double) i[6]).doubleValue() > 0.054857) {
            p = UKOBClassifier2.N2ba4b1c3272(i);
        }
        return p;
    }
    static double N36c28b1b268(Object []i) {
        double p = Double.NaN;
        if (i[10] == null) {
            p = 0;
        } else if (((Double) i[10]).doubleValue() <= 0.479831) {
            p = UKOBClassifier2.N1e3ab5b3269(i);
        } else if (((Double) i[10]).doubleValue() > 0.479831) {
            p = UKOBClassifier2.N5a0b8a4c271(i);
        }
        return p;
    }
    static double N1e3ab5b3269(Object []i) {
        double p = Double.NaN;
        if (i[6] == null) {
            p = 0;
        } else if (((Double) i[6]).doubleValue() <= 0.005016) {
            p = 0;
        } else if (((Double) i[6]).doubleValue() > 0.005016) {
            p = UKOBClassifier2.N206f0a46270(i);
        }
        return p;
    }
    static double N206f0a46270(Object []i) {
        double p = Double.NaN;
        if (i[6] == null) {
            p = 0;
        } else if (((Double) i[6]).doubleValue() <= 0.014665) {
            p = 0;
        } else if (((Double) i[6]).doubleValue() > 0.014665) {
            p = 4;
        }
        return p;
    }
    static double N5a0b8a4c271(Object []i) {
        double p = Double.NaN;
        if (i[10] == null) {
            p = 2;
        } else if (((Double) i[10]).doubleValue() <= 0.923013) {
            p = 2;
        } else if (((Double) i[10]).doubleValue() > 0.923013) {
            p = 3;
        }
        return p;
    }
    static double N2ba4b1c3272(Object []i) {
        double p = Double.NaN;
        if (i[7] == null) {
            p = 4;
        } else if (((Double) i[7]).doubleValue() <= 49.781174) {
            p = UKOBClassifier2.N49928f97273(i);
        } else if (((Double) i[7]).doubleValue() > 49.781174) {
            p = UKOBClassifier2.N70c0988e310(i);
        }
        return p;
    }
    static double N49928f97273(Object []i) {
        double p = Double.NaN;
        if (i[6] == null) {
            p = 4;
        } else if (((Double) i[6]).doubleValue() <= 54.282841) {
            p = UKOBClassifier2.N3511283d274(i);
        } else if (((Double) i[6]).doubleValue() > 54.282841) {
            p = 1;
        }
        return p;
    }
    static double N3511283d274(Object []i) {
        double p = Double.NaN;
        if (i[5] == null) {
            p = 4;
        } else if (((Double) i[5]).doubleValue() <= 62.217972) {
            p = UKOBClassifier2.N7968d802275(i);
        } else if (((Double) i[5]).doubleValue() > 62.217972) {
            p = UKOBClassifier2.N7624e71a309(i);
        }
        return p;
    }
    static double N7968d802275(Object []i) {
        double p = Double.NaN;
        if (i[5] == null) {
            p = 3;
        } else if (((Double) i[5]).doubleValue() <= 0.065876) {
            p = UKOBClassifier2.N523da993276(i);
        } else if (((Double) i[5]).doubleValue() > 0.065876) {
            p = UKOBClassifier2.N4e481c13277(i);
        }
        return p;
    }
    static double N523da993276(Object []i) {
        double p = Double.NaN;
        if (i[10] == null) {
            p = 4;
        } else if (((Double) i[10]).doubleValue() <= 0.661986) {
            p = 4;
        } else if (((Double) i[10]).doubleValue() > 0.661986) {
            p = 3;
        }
        return p;
    }
    static double N4e481c13277(Object []i) {
        double p = Double.NaN;
        if (i[10] == null) {
            p = 0;
        } else if (((Double) i[10]).doubleValue() <= 0.001486) {
            p = UKOBClassifier2.N602b4b8b278(i);
        } else if (((Double) i[10]).doubleValue() > 0.001486) {
            p = UKOBClassifier2.N3a6cc400279(i);
        }
        return p;
    }
    static double N602b4b8b278(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 0;
        } else if (((Double) i[2]).doubleValue() <= 1.331967) {
            p = 0;
        } else if (((Double) i[2]).doubleValue() > 1.331967) {
            p = 4;
        }
        return p;
    }
    static double N3a6cc400279(Object []i) {
        double p = Double.NaN;
        if (i[4] == null) {
            p = 4;
        } else if (((Double) i[4]).doubleValue() <= -0.495485) {
            p = UKOBClassifier2.N3ab7f9f7280(i);
        } else if (((Double) i[4]).doubleValue() > -0.495485) {
            p = UKOBClassifier2.Nc62ef8a289(i);
        }
        return p;
    }
    static double N3ab7f9f7280(Object []i) {
        double p = Double.NaN;
        if (i[6] == null) {
            p = 4;
        } else if (((Double) i[6]).doubleValue() <= 17.095425) {
            p = UKOBClassifier2.N47be53f281(i);
        } else if (((Double) i[6]).doubleValue() > 17.095425) {
            p = UKOBClassifier2.N467816d6287(i);
        }
        return p;
    }
    static double N47be53f281(Object []i) {
        double p = Double.NaN;
        if (i[10] == null) {
            p = 4;
        } else if (((Double) i[10]).doubleValue() <= 0.983741) {
            p = 4;
        } else if (((Double) i[10]).doubleValue() > 0.983741) {
            p = UKOBClassifier2.N62078d65282(i);
        }
        return p;
    }
    static double N62078d65282(Object []i) {
        double p = Double.NaN;
        if (i[7] == null) {
            p = 1;
        } else if (((Double) i[7]).doubleValue() <= 4.522755) {
            p = 1;
        } else if (((Double) i[7]).doubleValue() > 4.522755) {
            p = UKOBClassifier2.N5dd4142a283(i);
        }
        return p;
    }
    static double N5dd4142a283(Object []i) {
        double p = Double.NaN;
        if (i[6] == null) {
            p = 4;
        } else if (((Double) i[6]).doubleValue() <= 10.482138) {
            p = UKOBClassifier2.Ne780186284(i);
        } else if (((Double) i[6]).doubleValue() > 10.482138) {
            p = UKOBClassifier2.N773d02a2286(i);
        }
        return p;
    }
    static double Ne780186284(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 4;
        } else if (((Double) i[2]).doubleValue() <= -0.458731) {
            p = 4;
        } else if (((Double) i[2]).doubleValue() > -0.458731) {
            p = UKOBClassifier2.N68ac0bd5285(i);
        }
        return p;
    }
    static double N68ac0bd5285(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 1;
        } else if (((Double) i[2]).doubleValue() <= -0.233184) {
            p = 1;
        } else if (((Double) i[2]).doubleValue() > -0.233184) {
            p = 4;
        }
        return p;
    }
    static double N773d02a2286(Object []i) {
        double p = Double.NaN;
        if (i[7] == null) {
            p = 1;
        } else if (((Double) i[7]).doubleValue() <= 12.188478) {
            p = 1;
        } else if (((Double) i[7]).doubleValue() > 12.188478) {
            p = 4;
        }
        return p;
    }
    static double N467816d6287(Object []i) {
        double p = Double.NaN;
        if (i[5] == null) {
            p = 1;
        } else if (((Double) i[5]).doubleValue() <= 21.605122) {
            p = UKOBClassifier2.N76235fbe288(i);
        } else if (((Double) i[5]).doubleValue() > 21.605122) {
            p = 4;
        }
        return p;
    }
    static double N76235fbe288(Object []i) {
        double p = Double.NaN;
        if (i[10] == null) {
            p = 4;
        } else if (((Double) i[10]).doubleValue() <= 0.954038) {
            p = 4;
        } else if (((Double) i[10]).doubleValue() > 0.954038) {
            p = 1;
        }
        return p;
    }
    static double Nc62ef8a289(Object []i) {
        double p = Double.NaN;
        if (i[7] == null) {
            p = 4;
        } else if (((Double) i[7]).doubleValue() <= 16.490442) {
            p = UKOBClassifier2.N335c5360290(i);
        } else if (((Double) i[7]).doubleValue() > 16.490442) {
            p = UKOBClassifier2.Nb7ca8f7294(i);
        }
        return p;
    }
    static double N335c5360290(Object []i) {
        double p = Double.NaN;
        if (i[6] == null) {
            p = 4;
        } else if (((Double) i[6]).doubleValue() <= 9.582843) {
            p = 4;
        } else if (((Double) i[6]).doubleValue() > 9.582843) {
            p = UKOBClassifier2.N7265ddf7291(i);
        }
        return p;
    }
    static double N7265ddf7291(Object []i) {
        double p = Double.NaN;
        if (i[4] == null) {
            p = 4;
        } else if (((Double) i[4]).doubleValue() <= 8.40419) {
            p = 4;
        } else if (((Double) i[4]).doubleValue() > 8.40419) {
            p = UKOBClassifier2.N7dcfbdcd292(i);
        }
        return p;
    }
    static double N7dcfbdcd292(Object []i) {
        double p = Double.NaN;
        if (i[7] == null) {
            p = 4;
        } else if (((Double) i[7]).doubleValue() <= 12.148404) {
            p = UKOBClassifier2.N51b61e42293(i);
        } else if (((Double) i[7]).doubleValue() > 12.148404) {
            p = 1;
        }
        return p;
    }
    static double N51b61e42293(Object []i) {
        double p = Double.NaN;
        if (i[12] == null) {
            p = 1;
        } else if (((Double) i[12]).doubleValue() <= 153.0) {
            p = 1;
        } else if (((Double) i[12]).doubleValue() > 153.0) {
            p = 4;
        }
        return p;
    }
    static double Nb7ca8f7294(Object []i) {
        double p = Double.NaN;
        if (i[10] == null) {
            p = 1;
        } else if (((Double) i[10]).doubleValue() <= 0.42949) {
            p = UKOBClassifier2.N2538f605295(i);
        } else if (((Double) i[10]).doubleValue() > 0.42949) {
            p = UKOBClassifier2.N26f8ba75305(i);
        }
        return p;
    }
    static double N2538f605295(Object []i) {
        double p = Double.NaN;
        if (i[9] == null) {
            p = 4;
        } else if (((Double) i[9]).doubleValue() <= 15.387526) {
            p = UKOBClassifier2.N3e9fd55a296(i);
        } else if (((Double) i[9]).doubleValue() > 15.387526) {
            p = UKOBClassifier2.N2c470874298(i);
        }
        return p;
    }
    static double N3e9fd55a296(Object []i) {
        double p = Double.NaN;
        if (i[3] == null) {
            p = 4;
        } else if (((Double) i[3]).doubleValue() <= 3.587514) {
            p = 4;
        } else if (((Double) i[3]).doubleValue() > 3.587514) {
            p = UKOBClassifier2.N6f7027d4297(i);
        }
        return p;
    }
    static double N6f7027d4297(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 1;
        } else if (((Double) i[2]).doubleValue() <= -0.349548) {
            p = 1;
        } else if (((Double) i[2]).doubleValue() > -0.349548) {
            p = 4;
        }
        return p;
    }
    static double N2c470874298(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 1;
        } else if (((Double) i[2]).doubleValue() <= 8.170732) {
            p = UKOBClassifier2.N6b7c0e61299(i);
        } else if (((Double) i[2]).doubleValue() > 8.170732) {
            p = 4;
        }
        return p;
    }
    static double N6b7c0e61299(Object []i) {
        double p = Double.NaN;
        if (i[5] == null) {
            p = 4;
        } else if (((Double) i[5]).doubleValue() <= 3.678065) {
            p = 4;
        } else if (((Double) i[5]).doubleValue() > 3.678065) {
            p = UKOBClassifier2.N1d943968300(i);
        }
        return p;
    }
    static double N1d943968300(Object []i) {
        double p = Double.NaN;
        if (i[7] == null) {
            p = 1;
        } else if (((Double) i[7]).doubleValue() <= 18.338417) {
            p = UKOBClassifier2.N6e44ea03301(i);
        } else if (((Double) i[7]).doubleValue() > 18.338417) {
            p = UKOBClassifier2.N48e82611303(i);
        }
        return p;
    }
    static double N6e44ea03301(Object []i) {
        double p = Double.NaN;
        if (i[5] == null) {
            p = 1;
        } else if (((Double) i[5]).doubleValue() <= 9.27165) {
            p = 1;
        } else if (((Double) i[5]).doubleValue() > 9.27165) {
            p = UKOBClassifier2.N6e5fa383302(i);
        }
        return p;
    }
    static double N6e5fa383302(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 4;
        } else if (((Double) i[2]).doubleValue() <= 4.054577) {
            p = 4;
        } else if (((Double) i[2]).doubleValue() > 4.054577) {
            p = 1;
        }
        return p;
    }
    static double N48e82611303(Object []i) {
        double p = Double.NaN;
        if (i[4] == null) {
            p = 4;
        } else if (((Double) i[4]).doubleValue() <= 1.945792) {
            p = UKOBClassifier2.N11b4b7c304(i);
        } else if (((Double) i[4]).doubleValue() > 1.945792) {
            p = 1;
        }
        return p;
    }
    static double N11b4b7c304(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 4;
        } else if (((Double) i[2]).doubleValue() <= 6.919058) {
            p = 4;
        } else if (((Double) i[2]).doubleValue() > 6.919058) {
            p = 1;
        }
        return p;
    }
    static double N26f8ba75305(Object []i) {
        double p = Double.NaN;
        if (i[10] == null) {
            p = 4;
        } else if (((Double) i[10]).doubleValue() <= 0.50341) {
            p = UKOBClassifier2.N13996b50306(i);
        } else if (((Double) i[10]).doubleValue() > 0.50341) {
            p = UKOBClassifier2.N3d545b3d307(i);
        }
        return p;
    }
    static double N13996b50306(Object []i) {
        double p = Double.NaN;
        if (i[9] == null) {
            p = 4;
        } else if (((Double) i[9]).doubleValue() <= 28.327211) {
            p = 4;
        } else if (((Double) i[9]).doubleValue() > 28.327211) {
            p = 1;
        }
        return p;
    }
    static double N3d545b3d307(Object []i) {
        double p = Double.NaN;
        if (i[9] == null) {
            p = 4;
        } else if (((Double) i[9]).doubleValue() <= 51.117184) {
            p = 4;
        } else if (((Double) i[9]).doubleValue() > 51.117184) {
            p = UKOBClassifier2.N6d32213f308(i);
        }
        return p;
    }
    static double N6d32213f308(Object []i) {
        double p = Double.NaN;
        if (i[15] == null) {
            p = 1;
        } else if (((Double) i[15]).doubleValue() <= 23.0) {
            p = 1;
        } else if (((Double) i[15]).doubleValue() > 23.0) {
            p = 4;
        }
        return p;
    }
    static double N7624e71a309(Object []i) {
        double p = Double.NaN;
        if (i[10] == null) {
            p = 1;
        } else if (((Double) i[10]).doubleValue() <= 0.403173) {
            p = 1;
        } else if (((Double) i[10]).doubleValue() > 0.403173) {
            p = 4;
        }
        return p;
    }
    static double N70c0988e310(Object []i) {
        double p = Double.NaN;
        if (i[4] == null) {
            p = 4;
        } else if (((Double) i[4]).doubleValue() <= -0.806392) {
            p = UKOBClassifier2.N745fcc76311(i);
        } else if (((Double) i[4]).doubleValue() > -0.806392) {
            p = UKOBClassifier2.N3d6892aa312(i);
        }
        return p;
    }
    static double N745fcc76311(Object []i) {
        double p = Double.NaN;
        if (i[12] == null) {
            p = 1;
        } else if (((Double) i[12]).doubleValue() <= 27.0) {
            p = 1;
        } else if (((Double) i[12]).doubleValue() > 27.0) {
            p = 4;
        }
        return p;
    }
    static double N3d6892aa312(Object []i) {
        double p = Double.NaN;
        if (i[7] == null) {
            p = 1;
        } else if (((Double) i[7]).doubleValue() <= 52.602509) {
            p = UKOBClassifier2.N1c74f665313(i);
        } else if (((Double) i[7]).doubleValue() > 52.602509) {
            p = 1;
        }
        return p;
    }
    static double N1c74f665313(Object []i) {
        double p = Double.NaN;
        if (i[12] == null) {
            p = 1;
        } else if (((Double) i[12]).doubleValue() <= 70.0) {
            p = UKOBClassifier2.N42dc6f7b314(i);
        } else if (((Double) i[12]).doubleValue() > 70.0) {
            p = 1;
        }
        return p;
    }
    static double N42dc6f7b314(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 1;
        } else if (((Double) i[2]).doubleValue() <= -0.201386) {
            p = 1;
        } else if (((Double) i[2]).doubleValue() > -0.201386) {
            p = 4;
        }
        return p;
    }
}

