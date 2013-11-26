package eu.liveandgov.wp1.human_activity_recognition.classifier;

// Generated with Weka 3.6.10
//
// This code is public domain and comes with no warranty.
//
// Timestamp: Tue Nov 19 16:28:02 CET 2013

import eu.liveandgov.wp1.human_activity_recognition.Activities;

public class DecisionTree {

    public static double classify(Object[] i)
            throws Exception {

        double p = Double.NaN;
        p = DecisionTree.N1a2b93350(i);
        return p;
    }
    static double N1a2b93350(Object []i) {
        double p = Double.NaN;
        if (i[5] == null) {
            p = 1;
        } else if (((Double) i[5]).doubleValue() <= 2.71506) {
            p = DecisionTree.N22cd89ff1(i);
        } else if (((Double) i[5]).doubleValue() > 2.71506) {
            p = DecisionTree.N748441299(i);
        }
        return p;
    }
    static double N22cd89ff1(Object []i) {
        double p = Double.NaN;
        if (i[4] == null) {
            p = 1;
        } else if (((Double) i[4]).doubleValue() <= 3.005323) {
            p = DecisionTree.N641ed6322(i);
        } else if (((Double) i[4]).doubleValue() > 3.005323) {
            p = 6;
        }
        return p;
    }
    static double N641ed6322(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 1;
        } else if (((Double) i[2]).doubleValue() <= -7.496102) {
            p = 1;
        } else if (((Double) i[2]).doubleValue() > -7.496102) {
            p = DecisionTree.N24809ff83(i);
        }
        return p;
    }
    static double N24809ff83(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 5;
        } else if (((Double) i[2]).doubleValue() <= -1.637774) {
            p = DecisionTree.N788665804(i);
        } else if (((Double) i[2]).doubleValue() > -1.637774) {
            p = DecisionTree.N3f71f4515(i);
        }
        return p;
    }
    static double N788665804(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 5;
        } else if (((Double) i[0]).doubleValue() <= 4.069702) {
            p = 5;
        } else if (((Double) i[0]).doubleValue() > 4.069702) {
            p = 2;
        }
        return p;
    }
    static double N3f71f4515(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 2;
        } else if (((Double) i[2]).doubleValue() <= 2.904724) {
            p = DecisionTree.N525f12616(i);
        } else if (((Double) i[2]).doubleValue() > 2.904724) {
            p = DecisionTree.Nd96067e8(i);
        }
        return p;
    }
    static double N525f12616(Object []i) {
        double p = Double.NaN;
        if (i[6] == null) {
            p = 0;
        } else if (((Double) i[6]).doubleValue() <= 81.43853) {
            p = 0;
        } else if (((Double) i[6]).doubleValue() > 81.43853) {
            p = DecisionTree.N5fafc8867(i);
        }
        return p;
    }
    static double N5fafc8867(Object []i) {
        double p = Double.NaN;
        if (i[6] == null) {
            p = 2;
        } else if (((Double) i[6]).doubleValue() <= 103.878769) {
            p = 2;
        } else if (((Double) i[6]).doubleValue() > 103.878769) {
            p = 0;
        }
        return p;
    }
    static double Nd96067e8(Object []i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 3;
        } else if (((Double) i[1]).doubleValue() <= 0.739937) {
            p = 3;
        } else if (((Double) i[1]).doubleValue() > 0.739937) {
            p = 1;
        }
        return p;
    }
    static double N748441299(Object []i) {
        double p = Double.NaN;
        if (i[4] == null) {
            p = 3;
        } else if (((Double) i[4]).doubleValue() <= 66.937401) {
            p = DecisionTree.N16d2268210(i);
        } else if (((Double) i[4]).doubleValue() > 66.937401) {
            p = 4;
        }
        return p;
    }
    static double N16d2268210(Object []i) {
        double p = Double.NaN;
        if (i[4] == null) {
            p = 0;
        } else if (((Double) i[4]).doubleValue() <= 21.150537) {
            p = DecisionTree.N3ede2c8211(i);
        } else if (((Double) i[4]).doubleValue() > 21.150537) {
            p = DecisionTree.N34192e7c24(i);
        }
        return p;
    }
    static double N3ede2c8211(Object []i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 0;
        } else if (((Double) i[1]).doubleValue() <= 10.052891) {
            p = DecisionTree.N6c3c2b0c12(i);
        } else if (((Double) i[1]).doubleValue() > 10.052891) {
            p = DecisionTree.N25d2d2fa23(i);
        }
        return p;
    }
    static double N6c3c2b0c12(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 6;
        } else if (((Double) i[2]).doubleValue() <= -3.049124) {
            p = 6;
        } else if (((Double) i[2]).doubleValue() > -3.049124) {
            p = DecisionTree.N622e585713(i);
        }
        return p;
    }
    static double N622e585713(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 3;
        } else if (((Double) i[0]).doubleValue() <= -3.127031) {
            p = 3;
        } else if (((Double) i[0]).doubleValue() > -3.127031) {
            p = DecisionTree.N50a9ea1c14(i);
        }
        return p;
    }
    static double N50a9ea1c14(Object []i) {
        double p = Double.NaN;
        if (i[5] == null) {
            p = 0;
        } else if (((Double) i[5]).doubleValue() <= 29.211945) {
            p = DecisionTree.N4348fda315(i);
        } else if (((Double) i[5]).doubleValue() > 29.211945) {
            p = DecisionTree.Nb9fc35c22(i);
        }
        return p;
    }
    static double N4348fda315(Object []i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 3;
        } else if (((Double) i[1]).doubleValue() <= -10.426781) {
            p = 3;
        } else if (((Double) i[1]).doubleValue() > -10.426781) {
            p = DecisionTree.N6d03fad716(i);
        }
        return p;
    }
    static double N6d03fad716(Object []i) {
        double p = Double.NaN;
        if (i[3] == null) {
            p = 0;
        } else if (((Double) i[3]).doubleValue() <= 4.713222) {
            p = 0;
        } else if (((Double) i[3]).doubleValue() > 4.713222) {
            p = DecisionTree.N2049712b17(i);
        }
        return p;
    }
    static double N2049712b17(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 0;
        } else if (((Double) i[2]).doubleValue() <= -0.374151) {
            p = DecisionTree.N35a4ce9c18(i);
        } else if (((Double) i[2]).doubleValue() > -0.374151) {
            p = 3;
        }
        return p;
    }
    static double N35a4ce9c18(Object []i) {
        double p = Double.NaN;
        if (i[3] == null) {
            p = 0;
        } else if (((Double) i[3]).doubleValue() <= 13.363245) {
            p = DecisionTree.N56f87f4719(i);
        } else if (((Double) i[3]).doubleValue() > 13.363245) {
            p = 0;
        }
        return p;
    }
    static double N56f87f4719(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 0;
        } else if (((Double) i[0]).doubleValue() <= 1.649558) {
            p = DecisionTree.N546c3aec20(i);
        } else if (((Double) i[0]).doubleValue() > 1.649558) {
            p = 3;
        }
        return p;
    }
    static double N546c3aec20(Object []i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 0;
        } else if (((Double) i[1]).doubleValue() <= -9.542562) {
            p = DecisionTree.Nd90874121(i);
        } else if (((Double) i[1]).doubleValue() > -9.542562) {
            p = 0;
        }
        return p;
    }
    static double Nd90874121(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 3;
        } else if (((Double) i[0]).doubleValue() <= -1.610446) {
            p = 3;
        } else if (((Double) i[0]).doubleValue() > -1.610446) {
            p = 0;
        }
        return p;
    }
    static double Nb9fc35c22(Object []i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 4;
        } else if (((Double) i[1]).doubleValue() <= -10.116372) {
            p = 4;
        } else if (((Double) i[1]).doubleValue() > -10.116372) {
            p = 0;
        }
        return p;
    }
    static double N25d2d2fa23(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 0;
        } else if (((Double) i[0]).doubleValue() <= -0.684458) {
            p = 0;
        } else if (((Double) i[0]).doubleValue() > -0.684458) {
            p = 4;
        }
        return p;
    }
    static double N34192e7c24(Object []i) {
        double p = Double.NaN;
        if (i[5] == null) {
            p = 3;
        } else if (((Double) i[5]).doubleValue() <= 54.333553) {
            p = DecisionTree.N613ae99c25(i);
        } else if (((Double) i[5]).doubleValue() > 54.333553) {
            p = 4;
        }
        return p;
    }
    static double N613ae99c25(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 0;
        } else if (((Double) i[2]).doubleValue() <= -0.730925) {
            p = DecisionTree.N62bf32a226(i);
        } else if (((Double) i[2]).doubleValue() > -0.730925) {
            p = DecisionTree.N5792354530(i);
        }
        return p;
    }
    static double N62bf32a226(Object []i) {
        double p = Double.NaN;
        if (i[4] == null) {
            p = 0;
        } else if (((Double) i[4]).doubleValue() <= 34.155815) {
            p = DecisionTree.N76955c5327(i);
        } else if (((Double) i[4]).doubleValue() > 34.155815) {
            p = 4;
        }
        return p;
    }
    static double N76955c5327(Object []i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 0;
        } else if (((Double) i[1]).doubleValue() <= 0.739937) {
            p = DecisionTree.N47e089f728(i);
        } else if (((Double) i[1]).doubleValue() > 0.739937) {
            p = 4;
        }
        return p;
    }
    static double N47e089f728(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 0;
        } else if (((Double) i[0]).doubleValue() <= 3.387716) {
            p = DecisionTree.N6681dbfe29(i);
        } else if (((Double) i[0]).doubleValue() > 3.387716) {
            p = 3;
        }
        return p;
    }
    static double N6681dbfe29(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 3;
        } else if (((Double) i[0]).doubleValue() <= -1.03735) {
            p = 3;
        } else if (((Double) i[0]).doubleValue() > -1.03735) {
            p = 0;
        }
        return p;
    }
    static double N5792354530(Object []i) {
        double p = Double.NaN;
        if (i[5] == null) {
            p = 3;
        } else if (((Double) i[5]).doubleValue() <= 35.405323) {
            p = 3;
        } else if (((Double) i[5]).doubleValue() > 35.405323) {
            p = DecisionTree.N3fe771ed31(i);
        }
        return p;
    }
    static double N3fe771ed31(Object []i) {
        double p = Double.NaN;
        if (i[4] == null) {
            p = 0;
        } else if (((Double) i[4]).doubleValue() <= 38.746643) {
            p = DecisionTree.N73e89f6132(i);
        } else if (((Double) i[4]).doubleValue() > 38.746643) {
            p = 3;
        }
        return p;
    }
    static double N73e89f6132(Object []i) {
        double p = Double.NaN;
        if (i[3] == null) {
            p = 3;
        } else if (((Double) i[3]).doubleValue() <= 11.180486) {
            p = 3;
        } else if (((Double) i[3]).doubleValue() > 11.180486) {
            p = 0;
        }
        return p;
    }

    public static String myClassify(Object[] i) throws Exception {
        double p = Double.NaN;
        p = N1a2b93350(i);

        if(p == 0D) {
            return Activities.STAIRS;
        } else if (p == 4D){
            return Activities.RUNNING;
        } else if (p == 2D) {
            return Activities.STANDING;
        } else if (p == 1D) {
            return Activities.SITTING;
        } else if (p == 3D) {
            return Activities.WALKING;
        } else if (p == 5D) {
            return Activities.DRIVING;
        } else if (p == 6D) {
            return Activities.CYCLING;
        }

        return Activities.UNKNOWN;
    }
}

