package eu.liveandgov.wp1.classifier;

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
    p = UKOB_NEW_Classifier.N2faae4d066(i);
    return p;
  }
  static double N2faae4d066(Object []i) {
    double p = Double.NaN;
    if (i[3] == null) {
      p = 2;
    } else if (((Double) i[3]).doubleValue() <= -3.147356) {
    p = UKOB_NEW_Classifier.N7c942c2267(i);
    } else if (((Double) i[3]).doubleValue() > -3.147356) {
    p = UKOB_NEW_Classifier.N6e4c469d69(i);
    } 
    return p;
  }
  static double N7c942c2267(Object []i) {
    double p = Double.NaN;
    if (i[8] == null) {
      p = 2;
    } else if (((Double) i[8]).doubleValue() <= 1.07363) {
      p = 2;
    } else if (((Double) i[8]).doubleValue() > 1.07363) {
    p = UKOB_NEW_Classifier.N63ddac1368(i);
    } 
    return p;
  }
  static double N63ddac1368(Object []i) {
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
  static double N6e4c469d69(Object []i) {
    double p = Double.NaN;
    if (i[5] == null) {
      p = 0;
    } else if (((Double) i[5]).doubleValue() <= 0.037589) {
    p = UKOB_NEW_Classifier.N51b025fd70(i);
    } else if (((Double) i[5]).doubleValue() > 0.037589) {
    p = UKOB_NEW_Classifier.N6babd20972(i);
    } 
    return p;
  }
  static double N51b025fd70(Object []i) {
    double p = Double.NaN;
    if (i[9] == null) {
      p = 0;
    } else if (((Double) i[9]).doubleValue() <= 0.479831) {
      p = 0;
    } else if (((Double) i[9]).doubleValue() > 0.479831) {
    p = UKOB_NEW_Classifier.N38e2ef171(i);
    } 
    return p;
  }
  static double N38e2ef171(Object []i) {
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
  static double N6babd20972(Object []i) {
    double p = Double.NaN;
    if (i[6] == null) {
      p = 4;
    } else if (((Double) i[6]).doubleValue() <= 49.781174) {
    p = UKOB_NEW_Classifier.N5d6a841873(i);
    } else if (((Double) i[6]).doubleValue() > 49.781174) {
    p = UKOB_NEW_Classifier.N28640f99129(i);
    } 
    return p;
  }
  static double N5d6a841873(Object []i) {
    double p = Double.NaN;
    if (i[5] == null) {
      p = 4;
    } else if (((Double) i[5]).doubleValue() <= 54.237549) {
    p = UKOB_NEW_Classifier.N7c06739174(i);
    } else if (((Double) i[5]).doubleValue() > 54.237549) {
      p = 1;
    } 
    return p;
  }
  static double N7c06739174(Object []i) {
    double p = Double.NaN;
    if (i[11] == null) {
      p = 4;
    } else if (((Double) i[11]).doubleValue() <= 100.112328) {
    p = UKOB_NEW_Classifier.Nb8d743475(i);
    } else if (((Double) i[11]).doubleValue() > 100.112328) {
    p = UKOB_NEW_Classifier.N339f6fc9128(i);
    } 
    return p;
  }
  static double Nb8d743475(Object []i) {
    double p = Double.NaN;
    if (i[4] == null) {
      p = 4;
    } else if (((Double) i[4]).doubleValue() <= 61.716549) {
    p = UKOB_NEW_Classifier.N73c807d876(i);
    } else if (((Double) i[4]).doubleValue() > 61.716549) {
    p = UKOB_NEW_Classifier.N2fd4acd7127(i);
    } 
    return p;
  }
  static double N73c807d876(Object []i) {
    double p = Double.NaN;
    if (i[5] == null) {
      p = 4;
    } else if (((Double) i[5]).doubleValue() <= 13.149132) {
    p = UKOB_NEW_Classifier.N517b314a77(i);
    } else if (((Double) i[5]).doubleValue() > 13.149132) {
    p = UKOB_NEW_Classifier.Nc6cf6e1103(i);
    } 
    return p;
  }
  static double N517b314a77(Object []i) {
    double p = Double.NaN;
    if (i[11] == null) {
      p = 4;
    } else if (((Double) i[11]).doubleValue() <= 0.452057) {
    p = UKOB_NEW_Classifier.N6ee11b1078(i);
    } else if (((Double) i[11]).doubleValue() > 0.452057) {
    p = UKOB_NEW_Classifier.N33ca8a0294(i);
    } 
    return p;
  }
  static double N6ee11b1078(Object []i) {
    double p = Double.NaN;
    if (i[6] == null) {
      p = 4;
    } else if (((Double) i[6]).doubleValue() <= 16.656715) {
    p = UKOB_NEW_Classifier.N7cb7f04e79(i);
    } else if (((Double) i[6]).doubleValue() > 16.656715) {
    p = UKOB_NEW_Classifier.Nb9f50bc89(i);
    } 
    return p;
  }
  static double N7cb7f04e79(Object []i) {
    double p = Double.NaN;
    if (i[6] == null) {
      p = 4;
    } else if (((Double) i[6]).doubleValue() <= 1.000236) {
    p = UKOB_NEW_Classifier.N1001d0da80(i);
    } else if (((Double) i[6]).doubleValue() > 1.000236) {
    p = UKOB_NEW_Classifier.N6736a06b81(i);
    } 
    return p;
  }
  static double N1001d0da80(Object []i) {
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
  static double N6736a06b81(Object []i) {
    double p = Double.NaN;
    if (i[5] == null) {
      p = 4;
    } else if (((Double) i[5]).doubleValue() <= 9.851411) {
      p = 4;
    } else if (((Double) i[5]).doubleValue() > 9.851411) {
    p = UKOB_NEW_Classifier.N335e05bd82(i);
    } 
    return p;
  }
  static double N335e05bd82(Object []i) {
    double p = Double.NaN;
    if (i[11] == null) {
      p = 4;
    } else if (((Double) i[11]).doubleValue() <= -0.434301) {
      p = 4;
    } else if (((Double) i[11]).doubleValue() > -0.434301) {
    p = UKOB_NEW_Classifier.N61cad5a383(i);
    } 
    return p;
  }
  static double N61cad5a383(Object []i) {
    double p = Double.NaN;
    if (i[16] == null) {
      p = 4;
    } else if (((Double) i[16]).doubleValue() <= 13.0) {
      p = 4;
    } else if (((Double) i[16]).doubleValue() > 13.0) {
    p = UKOB_NEW_Classifier.N4b8ff27d84(i);
    } 
    return p;
  }
  static double N4b8ff27d84(Object []i) {
    double p = Double.NaN;
    if (i[2] == null) {
      p = 4;
    } else if (((Double) i[2]).doubleValue() <= -9.785049) {
      p = 4;
    } else if (((Double) i[2]).doubleValue() > -9.785049) {
    p = UKOB_NEW_Classifier.N5779134c85(i);
    } 
    return p;
  }
  static double N5779134c85(Object []i) {
    double p = Double.NaN;
    if (i[3] == null) {
      p = 4;
    } else if (((Double) i[3]).doubleValue() <= -1.422839) {
      p = 4;
    } else if (((Double) i[3]).doubleValue() > -1.422839) {
    p = UKOB_NEW_Classifier.N4de20f7186(i);
    } 
    return p;
  }
  static double N4de20f7186(Object []i) {
    double p = Double.NaN;
    if (i[3] == null) {
      p = 1;
    } else if (((Double) i[3]).doubleValue() <= -1.028417) {
      p = 1;
    } else if (((Double) i[3]).doubleValue() > -1.028417) {
    p = UKOB_NEW_Classifier.N3463eba987(i);
    } 
    return p;
  }
  static double N3463eba987(Object []i) {
    double p = Double.NaN;
    if (i[3] == null) {
      p = 4;
    } else if (((Double) i[3]).doubleValue() <= 7.872425) {
    p = UKOB_NEW_Classifier.Nc04bf1e88(i);
    } else if (((Double) i[3]).doubleValue() > 7.872425) {
      p = 1;
    } 
    return p;
  }
  static double Nc04bf1e88(Object []i) {
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
  static double Nb9f50bc89(Object []i) {
    double p = Double.NaN;
    if (i[3] == null) {
      p = 4;
    } else if (((Double) i[3]).doubleValue() <= 8.235121) {
      p = 4;
    } else if (((Double) i[3]).doubleValue() > 8.235121) {
    p = UKOB_NEW_Classifier.N86d6c9a90(i);
    } 
    return p;
  }
  static double N86d6c9a90(Object []i) {
    double p = Double.NaN;
    if (i[4] == null) {
      p = 4;
    } else if (((Double) i[4]).doubleValue() <= 4.86241) {
      p = 4;
    } else if (((Double) i[4]).doubleValue() > 4.86241) {
    p = UKOB_NEW_Classifier.N47f4f6c891(i);
    } 
    return p;
  }
  static double N47f4f6c891(Object []i) {
    double p = Double.NaN;
    if (i[1] == null) {
      p = 1;
    } else if (((Double) i[1]).doubleValue() <= 2.820256) {
    p = UKOB_NEW_Classifier.N2375e96092(i);
    } else if (((Double) i[1]).doubleValue() > 2.820256) {
    p = UKOB_NEW_Classifier.N122cafd093(i);
    } 
    return p;
  }
  static double N2375e96092(Object []i) {
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
  static double N122cafd093(Object []i) {
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
  static double N33ca8a0294(Object []i) {
    double p = Double.NaN;
    if (i[9] == null) {
      p = 4;
    } else if (((Double) i[9]).doubleValue() <= 0.983828) {
    p = UKOB_NEW_Classifier.N362aa3de95(i);
    } else if (((Double) i[9]).doubleValue() > 0.983828) {
    p = UKOB_NEW_Classifier.N237b3a1e99(i);
    } 
    return p;
  }
  static double N362aa3de95(Object []i) {
    double p = Double.NaN;
    if (i[5] == null) {
      p = 4;
    } else if (((Double) i[5]).doubleValue() <= 10.219557) {
      p = 4;
    } else if (((Double) i[5]).doubleValue() > 10.219557) {
    p = UKOB_NEW_Classifier.N296c5f9a96(i);
    } 
    return p;
  }
  static double N296c5f9a96(Object []i) {
    double p = Double.NaN;
    if (i[9] == null) {
      p = 1;
    } else if (((Double) i[9]).doubleValue() <= 0.216643) {
    p = UKOB_NEW_Classifier.N9f88cb597(i);
    } else if (((Double) i[9]).doubleValue() > 0.216643) {
      p = 4;
    } 
    return p;
  }
  static double N9f88cb597(Object []i) {
    double p = Double.NaN;
    if (i[1] == null) {
      p = 4;
    } else if (((Double) i[1]).doubleValue() <= 0.777919) {
      p = 4;
    } else if (((Double) i[1]).doubleValue() > 0.777919) {
    p = UKOB_NEW_Classifier.N1cddc43098(i);
    } 
    return p;
  }
  static double N1cddc43098(Object []i) {
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
  static double N237b3a1e99(Object []i) {
    double p = Double.NaN;
    if (i[6] == null) {
      p = 1;
    } else if (((Double) i[6]).doubleValue() <= 4.230997) {
      p = 1;
    } else if (((Double) i[6]).doubleValue() > 4.230997) {
    p = UKOB_NEW_Classifier.N6f2499c4100(i);
    } 
    return p;
  }
  static double N6f2499c4100(Object []i) {
    double p = Double.NaN;
    if (i[3] == null) {
      p = 4;
    } else if (((Double) i[3]).doubleValue() <= -0.643157) {
    p = UKOB_NEW_Classifier.N4beb4bdd101(i);
    } else if (((Double) i[3]).doubleValue() > -0.643157) {
      p = 4;
    } 
    return p;
  }
  static double N4beb4bdd101(Object []i) {
    double p = Double.NaN;
    if (i[1] == null) {
      p = 1;
    } else if (((Double) i[1]).doubleValue() <= -0.118966) {
      p = 1;
    } else if (((Double) i[1]).doubleValue() > -0.118966) {
    p = UKOB_NEW_Classifier.N44c1c11b102(i);
    } 
    return p;
  }
  static double N44c1c11b102(Object []i) {
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
  static double Nc6cf6e1103(Object []i) {
    double p = Double.NaN;
    if (i[11] == null) {
      p = 1;
    } else if (((Double) i[11]).doubleValue() <= -0.427478) {
    p = UKOB_NEW_Classifier.N45c43426104(i);
    } else if (((Double) i[11]).doubleValue() > -0.427478) {
    p = UKOB_NEW_Classifier.N5a012e32120(i);
    } 
    return p;
  }
  static double N45c43426104(Object []i) {
    double p = Double.NaN;
    if (i[7] == null) {
      p = 1;
    } else if (((Double) i[7]).doubleValue() <= 12.444286) {
    p = UKOB_NEW_Classifier.N543bce92105(i);
    } else if (((Double) i[7]).doubleValue() > 12.444286) {
    p = UKOB_NEW_Classifier.N7704c481119(i);
    } 
    return p;
  }
  static double N543bce92105(Object []i) {
    double p = Double.NaN;
    if (i[1] == null) {
      p = 4;
    } else if (((Double) i[1]).doubleValue() <= -2.788938) {
      p = 4;
    } else if (((Double) i[1]).doubleValue() > -2.788938) {
    p = UKOB_NEW_Classifier.N2276fe72106(i);
    } 
    return p;
  }
  static double N2276fe72106(Object []i) {
    double p = Double.NaN;
    if (i[8] == null) {
      p = 4;
    } else if (((Double) i[8]).doubleValue() <= 18.259687) {
    p = UKOB_NEW_Classifier.N323b000b107(i);
    } else if (((Double) i[8]).doubleValue() > 18.259687) {
    p = UKOB_NEW_Classifier.N606215a4114(i);
    } 
    return p;
  }
  static double N323b000b107(Object []i) {
    double p = Double.NaN;
    if (i[2] == null) {
      p = 1;
    } else if (((Double) i[2]).doubleValue() <= -9.028058) {
    p = UKOB_NEW_Classifier.N3f7febf0108(i);
    } else if (((Double) i[2]).doubleValue() > -9.028058) {
    p = UKOB_NEW_Classifier.N37dbbd35112(i);
    } 
    return p;
  }
  static double N3f7febf0108(Object []i) {
    double p = Double.NaN;
    if (i[1] == null) {
      p = 1;
    } else if (((Double) i[1]).doubleValue() <= 0.054357) {
    p = UKOB_NEW_Classifier.N675afa21109(i);
    } else if (((Double) i[1]).doubleValue() > 0.054357) {
      p = 4;
    } 
    return p;
  }
  static double N675afa21109(Object []i) {
    double p = Double.NaN;
    if (i[9] == null) {
      p = 4;
    } else if (((Double) i[9]).doubleValue() <= 0.988371) {
      p = 4;
    } else if (((Double) i[9]).doubleValue() > 0.988371) {
    p = UKOB_NEW_Classifier.N5dbc18a110(i);
    } 
    return p;
  }
  static double N5dbc18a110(Object []i) {
    double p = Double.NaN;
    if (i[5] == null) {
      p = 1;
    } else if (((Double) i[5]).doubleValue() <= 13.414605) {
    p = UKOB_NEW_Classifier.N1e7f4e07111(i);
    } else if (((Double) i[5]).doubleValue() > 13.414605) {
      p = 1;
    } 
    return p;
  }
  static double N1e7f4e07111(Object []i) {
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
  static double N37dbbd35112(Object []i) {
    double p = Double.NaN;
    if (i[1] == null) {
      p = 4;
    } else if (((Double) i[1]).doubleValue() <= -0.515174) {
    p = UKOB_NEW_Classifier.N3b62ff39113(i);
    } else if (((Double) i[1]).doubleValue() > -0.515174) {
      p = 4;
    } 
    return p;
  }
  static double N3b62ff39113(Object []i) {
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
  static double N606215a4114(Object []i) {
    double p = Double.NaN;
    if (i[1] == null) {
      p = 1;
    } else if (((Double) i[1]).doubleValue() <= 7.946371) {
    p = UKOB_NEW_Classifier.N477af36b115(i);
    } else if (((Double) i[1]).doubleValue() > 7.946371) {
      p = 4;
    } 
    return p;
  }
  static double N477af36b115(Object []i) {
    double p = Double.NaN;
    if (i[6] == null) {
      p = 4;
    } else if (((Double) i[6]).doubleValue() <= 8.975226) {
    p = UKOB_NEW_Classifier.N59031a76116(i);
    } else if (((Double) i[6]).doubleValue() > 8.975226) {
    p = UKOB_NEW_Classifier.N5abe66a1117(i);
    } 
    return p;
  }
  static double N59031a76116(Object []i) {
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
  static double N5abe66a1117(Object []i) {
    double p = Double.NaN;
    if (i[11] == null) {
      p = 1;
    } else if (((Double) i[11]).doubleValue() <= -0.609702) {
      p = 1;
    } else if (((Double) i[11]).doubleValue() > -0.609702) {
    p = UKOB_NEW_Classifier.Na440292118(i);
    } 
    return p;
  }
  static double Na440292118(Object []i) {
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
  static double N7704c481119(Object []i) {
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
  static double N5a012e32120(Object []i) {
    double p = Double.NaN;
    if (i[3] == null) {
      p = 4;
    } else if (((Double) i[3]).doubleValue() <= 5.222265) {
    p = UKOB_NEW_Classifier.N38002c8121(i);
    } else if (((Double) i[3]).doubleValue() > 5.222265) {
    p = UKOB_NEW_Classifier.N5c24c96b125(i);
    } 
    return p;
  }
  static double N38002c8121(Object []i) {
    double p = Double.NaN;
    if (i[7] == null) {
      p = 4;
    } else if (((Double) i[7]).doubleValue() <= 10.370795) {
    p = UKOB_NEW_Classifier.N49369a43122(i);
    } else if (((Double) i[7]).doubleValue() > 10.370795) {
    p = UKOB_NEW_Classifier.N1fc9ca42123(i);
    } 
    return p;
  }
  static double N49369a43122(Object []i) {
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
  static double N1fc9ca42123(Object []i) {
    double p = Double.NaN;
    if (i[6] == null) {
      p = 4;
    } else if (((Double) i[6]).doubleValue() <= 9.88914) {
    p = UKOB_NEW_Classifier.N78fdc35b124(i);
    } else if (((Double) i[6]).doubleValue() > 9.88914) {
      p = 4;
    } 
    return p;
  }
  static double N78fdc35b124(Object []i) {
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
  static double N5c24c96b125(Object []i) {
    double p = Double.NaN;
    if (i[8] == null) {
      p = 4;
    } else if (((Double) i[8]).doubleValue() <= 15.139568) {
      p = 4;
    } else if (((Double) i[8]).doubleValue() > 15.139568) {
    p = UKOB_NEW_Classifier.N7323bf0f126(i);
    } 
    return p;
  }
  static double N7323bf0f126(Object []i) {
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
  static double N2fd4acd7127(Object []i) {
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
  static double N339f6fc9128(Object []i) {
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
  static double N28640f99129(Object []i) {
    double p = Double.NaN;
    if (i[11] == null) {
      p = 1;
    } else if (((Double) i[11]).doubleValue() <= 0.475229) {
    p = UKOB_NEW_Classifier.N413c1a86130(i);
    } else if (((Double) i[11]).doubleValue() > 0.475229) {
      p = 4;
    } 
    return p;
  }
  static double N413c1a86130(Object []i) {
    double p = Double.NaN;
    if (i[3] == null) {
      p = 1;
    } else if (((Double) i[3]).doubleValue() <= -0.719296) {
    p = UKOB_NEW_Classifier.N50f174df131(i);
    } else if (((Double) i[3]).doubleValue() > -0.719296) {
      p = 1;
    } 
    return p;
  }
  static double N50f174df131(Object []i) {
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

