package eu.liveandgov.wp1.human_activity_recognition.classifier;

import eu.liveandgov.wp1.human_activity_recognition.Activities;

/**
 * Created with IntelliJ IDEA.
 * User: hartmann
 * Date: 11/27/13
 * Time: 2:12 AM
 * To change this template use File | Settings | File Templates.
 */
public class GoodTree {

        public static double classify(Object[] i)
                throws Exception {

            double p = Double.NaN;
            p = GoodTree.N384936220(i);
            return p;
        }

        public static String myClassify(Object[] i) throws Exception {
            double p = Double.NaN;
            p = GoodTree.N384936220(i);

            if(p == 0D) {
                return Activities.RUNNING;
            } else if (p == 1D){
                return Activities.WALKING;
            } else if (p == 2D) {
                return Activities.STANDING;
            } else if (p == 3D) {
                return Activities.SITTING;
            } else if (p == 4D) {
                return Activities.ONTABLE;
            }

            return Activities.UNKNOWN;
        }


        static double N384936220(Object []i) {
            double p = Double.NaN;
            if (i[2] == null) {
                p = 3;
            } else if (((Double) i[2]).doubleValue() <= -7.493181) {
                p = 3;
            } else if (((Double) i[2]).doubleValue() > -7.493181) {
                p = GoodTree.N4e810f0c1(i);
            }
            return p;
        }
        static double N4e810f0c1(Object []i) {
            double p = Double.NaN;
            if (i[4] == null) {
                p = 2;
            } else if (((Double) i[4]).doubleValue() <= 2.332705) {
                p = GoodTree.N7b0305172(i);
            } else if (((Double) i[4]).doubleValue() > 2.332705) {
                p = GoodTree.N34368193(i);
            }
            return p;
        }
        static double N7b0305172(Object []i) {
            double p = Double.NaN;
            if (i[3] == null) {
                p = 4;
            } else if (((Double) i[3]).doubleValue() <= 7.5E-4) {
                p = 4;
            } else if (((Double) i[3]).doubleValue() > 7.5E-4) {
                p = 2;
            }
            return p;
        }
        static double N34368193(Object []i) {
            double p = Double.NaN;
            if (i[4] == null) {
                p = 1;
            } else if (((Double) i[4]).doubleValue() <= 68.281776) {
                p = GoodTree.N3e6742fb4(i);
            } else if (((Double) i[4]).doubleValue() > 68.281776) {
                p = 0;
            }
            return p;
        }
        static double N3e6742fb4(Object []i) {
            double p = Double.NaN;
            if (i[5] == null) {
                p = 1;
            } else if (((Double) i[5]).doubleValue() <= 50.164753) {
                p = GoodTree.N6d5e8cbe5(i);
            } else if (((Double) i[5]).doubleValue() > 50.164753) {
                p = GoodTree.N1ea2ad3043(i);
            }
            return p;
        }
        static double N6d5e8cbe5(Object []i) {
            double p = Double.NaN;
            if (i[10] == null) {
                p = 1;
            } else if (((Double) i[10]).doubleValue() <= 31.066542) {
                p = GoodTree.N5a6a460a6(i);
            } else if (((Double) i[10]).doubleValue() > 31.066542) {
                p = GoodTree.N1fa917dd39(i);
            }
            return p;
        }
        static double N5a6a460a6(Object []i) {
            double p = Double.NaN;
            if (i[10] == null) {
                p = 0;
            } else if (((Double) i[10]).doubleValue() <= -1.051506) {
                p = 0;
            } else if (((Double) i[10]).doubleValue() > -1.051506) {
                p = GoodTree.N771c68e57(i);
            }
            return p;
        }
        static double N771c68e57(Object []i) {
            double p = Double.NaN;
            if (i[7] == null) {
                p = 0;
            } else if (((Double) i[7]).doubleValue() <= 4315.435059) {
                p = GoodTree.N6a2acf7a8(i);
            } else if (((Double) i[7]).doubleValue() > 4315.435059) {
                p = GoodTree.N209b890a9(i);
            }
            return p;
        }
        static double N6a2acf7a8(Object []i) {
            double p = Double.NaN;
            if (i[8] == null) {
                p = 1;
            } else if (((Double) i[8]).doubleValue() <= 0.729503) {
                p = 1;
            } else if (((Double) i[8]).doubleValue() > 0.729503) {
                p = 0;
            }
            return p;
        }
        static double N209b890a9(Object []i) {
            double p = Double.NaN;
            if (i[0] == null) {
                p = 1;
            } else if (((Double) i[0]).doubleValue() <= -1.256627) {
                p = 1;
            } else if (((Double) i[0]).doubleValue() > -1.256627) {
                p = GoodTree.N4342003f10(i);
            }
            return p;
        }
        static double N4342003f10(Object []i) {
            double p = Double.NaN;
            if (i[0] == null) {
                p = 1;
            } else if (((Double) i[0]).doubleValue() <= -0.825952) {
                p = GoodTree.N221e4a9811(i);
            } else if (((Double) i[0]).doubleValue() > -0.825952) {
                p = GoodTree.N4bcec1c919(i);
            }
            return p;
        }
        static double N221e4a9811(Object []i) {
            double p = Double.NaN;
            if (i[1] == null) {
                p = 1;
            } else if (((Double) i[1]).doubleValue() <= -9.767137) {
                p = GoodTree.N72b352a712(i);
            } else if (((Double) i[1]).doubleValue() > -9.767137) {
                p = GoodTree.N323b082b14(i);
            }
            return p;
        }
        static double N72b352a712(Object []i) {
            double p = Double.NaN;
            if (i[1] == null) {
                p = 1;
            } else if (((Double) i[1]).doubleValue() <= -9.824697) {
                p = 1;
            } else if (((Double) i[1]).doubleValue() > -9.824697) {
                p = GoodTree.N5af78cc513(i);
            }
            return p;
        }
        static double N5af78cc513(Object []i) {
            double p = Double.NaN;
            if (i[8] == null) {
                p = 1;
            } else if (((Double) i[8]).doubleValue() <= 0.985943) {
                p = 1;
            } else if (((Double) i[8]).doubleValue() > 0.985943) {
                p = 0;
            }
            return p;
        }
        static double N323b082b14(Object []i) {
            double p = Double.NaN;
            if (i[3] == null) {
                p = 1;
            } else if (((Double) i[3]).doubleValue() <= 5.746142) {
                p = GoodTree.N419558d015(i);
            } else if (((Double) i[3]).doubleValue() > 5.746142) {
                p = GoodTree.N33f9e15316(i);
            }
            return p;
        }
        static double N419558d015(Object []i) {
            double p = Double.NaN;
            if (i[2] == null) {
                p = 1;
            } else if (((Double) i[2]).doubleValue() <= -0.860185) {
                p = 1;
            } else if (((Double) i[2]).doubleValue() > -0.860185) {
                p = 0;
            }
            return p;
        }
        static double N33f9e15316(Object []i) {
            double p = Double.NaN;
            if (i[10] == null) {
                p = 0;
            } else if (((Double) i[10]).doubleValue() <= 7.821955) {
                p = GoodTree.N5a382acd17(i);
            } else if (((Double) i[10]).doubleValue() > 7.821955) {
                p = 1;
            }
            return p;
        }
        static double N5a382acd17(Object []i) {
            double p = Double.NaN;
            if (i[0] == null) {
                p = 0;
            } else if (((Double) i[0]).doubleValue() <= -1.221954) {
                p = GoodTree.N1d82270118(i);
            } else if (((Double) i[0]).doubleValue() > -1.221954) {
                p = 0;
            }
            return p;
        }
        static double N1d82270118(Object []i) {
            double p = Double.NaN;
            if (i[0] == null) {
                p = 1;
            } else if (((Double) i[0]).doubleValue() <= -1.23601) {
                p = 1;
            } else if (((Double) i[0]).doubleValue() > -1.23601) {
                p = 0;
            }
            return p;
        }
        static double N4bcec1c919(Object []i) {
            double p = Double.NaN;
            if (i[5] == null) {
                p = 1;
            } else if (((Double) i[5]).doubleValue() <= 45.786636) {
                p = GoodTree.N73149a0020(i);
            } else if (((Double) i[5]).doubleValue() > 45.786636) {
                p = GoodTree.N6316b10d35(i);
            }
            return p;
        }
        static double N73149a0020(Object []i) {
            double p = Double.NaN;
            if (i[1] == null) {
                p = 1;
            } else if (((Double) i[1]).doubleValue() <= 9.924546) {
                p = GoodTree.N4d8ab10621(i);
            } else if (((Double) i[1]).doubleValue() > 9.924546) {
                p = GoodTree.N33e877e234(i);
            }
            return p;
        }
        static double N4d8ab10621(Object []i) {
            double p = Double.NaN;
            if (i[10] == null) {
                p = 1;
            } else if (((Double) i[10]).doubleValue() <= 16.977512) {
                p = GoodTree.N4c6c28af22(i);
            } else if (((Double) i[10]).doubleValue() > 16.977512) {
                p = GoodTree.N77fa8f4f28(i);
            }
            return p;
        }
        static double N4c6c28af22(Object []i) {
            double p = Double.NaN;
            if (i[0] == null) {
                p = 1;
            } else if (((Double) i[0]).doubleValue() <= -0.714485) {
                p = GoodTree.N50e3205b23(i);
            } else if (((Double) i[0]).doubleValue() > -0.714485) {
                p = GoodTree.N6f615fd924(i);
            }
            return p;
        }
        static double N50e3205b23(Object []i) {
            double p = Double.NaN;
            if (i[1] == null) {
                p = 1;
            } else if (((Double) i[1]).doubleValue() <= -10.015231) {
                p = 1;
            } else if (((Double) i[1]).doubleValue() > -10.015231) {
                p = 0;
            }
            return p;
        }
        static double N6f615fd924(Object []i) {
            double p = Double.NaN;
            if (i[1] == null) {
                p = 1;
            } else if (((Double) i[1]).doubleValue() <= 5.855923) {
                p = 1;
            } else if (((Double) i[1]).doubleValue() > 5.855923) {
                p = GoodTree.N61dbd8af25(i);
            }
            return p;
        }
        static double N61dbd8af25(Object []i) {
            double p = Double.NaN;
            if (i[8] == null) {
                p = 0;
            } else if (((Double) i[8]).doubleValue() <= 0.896613) {
                p = 0;
            } else if (((Double) i[8]).doubleValue() > 0.896613) {
                p = GoodTree.N286efb5a26(i);
            }
            return p;
        }
        static double N286efb5a26(Object []i) {
            double p = Double.NaN;
            if (i[0] == null) {
                p = 1;
            } else if (((Double) i[0]).doubleValue() <= -0.278481) {
                p = GoodTree.Ne37e67327(i);
            } else if (((Double) i[0]).doubleValue() > -0.278481) {
                p = 1;
            }
            return p;
        }
        static double Ne37e67327(Object []i) {
            double p = Double.NaN;
            if (i[8] == null) {
                p = 0;
            } else if (((Double) i[8]).doubleValue() <= 0.998189) {
                p = 0;
            } else if (((Double) i[8]).doubleValue() > 0.998189) {
                p = 1;
            }
            return p;
        }
        static double N77fa8f4f28(Object []i) {
            double p = Double.NaN;
            if (i[4] == null) {
                p = 1;
            } else if (((Double) i[4]).doubleValue() <= 23.498308) {
                p = GoodTree.N62d6c91229(i);
            } else if (((Double) i[4]).doubleValue() > 23.498308) {
                p = GoodTree.N5db5739b32(i);
            }
            return p;
        }
        static double N62d6c91229(Object []i) {
            double p = Double.NaN;
            if (i[8] == null) {
                p = 1;
            } else if (((Double) i[8]).doubleValue() <= 0.99616) {
                p = 1;
            } else if (((Double) i[8]).doubleValue() > 0.99616) {
                p = GoodTree.N32aef7030(i);
            }
            return p;
        }
        static double N32aef7030(Object []i) {
            double p = Double.NaN;
            if (i[7] == null) {
                p = 1;
            } else if (((Double) i[7]).doubleValue() <= 30132.333984) {
                p = GoodTree.N77cda3af31(i);
            } else if (((Double) i[7]).doubleValue() > 30132.333984) {
                p = 0;
            }
            return p;
        }
        static double N77cda3af31(Object []i) {
            double p = Double.NaN;
            if (i[0] == null) {
                p = 0;
            } else if (((Double) i[0]).doubleValue() <= 0.207259) {
                p = 0;
            } else if (((Double) i[0]).doubleValue() > 0.207259) {
                p = 1;
            }
            return p;
        }
        static double N5db5739b32(Object []i) {
            double p = Double.NaN;
            if (i[2] == null) {
                p = 1;
            } else if (((Double) i[2]).doubleValue() <= 0.192715) {
                p = 1;
            } else if (((Double) i[2]).doubleValue() > 0.192715) {
                p = GoodTree.N33b8f52d33(i);
            }
            return p;
        }
        static double N33b8f52d33(Object []i) {
            double p = Double.NaN;
            if (i[4] == null) {
                p = 0;
            } else if (((Double) i[4]).doubleValue() <= 33.66777) {
                p = 0;
            } else if (((Double) i[4]).doubleValue() > 33.66777) {
                p = 1;
            }
            return p;
        }
        static double N33e877e234(Object []i) {
            double p = Double.NaN;
            if (i[2] == null) {
                p = 0;
            } else if (((Double) i[2]).doubleValue() <= -0.227126) {
                p = 0;
            } else if (((Double) i[2]).doubleValue() > -0.227126) {
                p = 1;
            }
            return p;
        }
        static double N6316b10d35(Object []i) {
            double p = Double.NaN;
            if (i[0] == null) {
                p = 1;
            } else if (((Double) i[0]).doubleValue() <= 0.398243) {
                p = 1;
            } else if (((Double) i[0]).doubleValue() > 0.398243) {
                p = GoodTree.N66c1ff4d36(i);
            }
            return p;
        }
        static double N66c1ff4d36(Object []i) {
            double p = Double.NaN;
            if (i[8] == null) {
                p = 1;
            } else if (((Double) i[8]).doubleValue() <= 0.945267) {
                p = 1;
            } else if (((Double) i[8]).doubleValue() > 0.945267) {
                p = GoodTree.N4a604cef37(i);
            }
            return p;
        }
        static double N4a604cef37(Object []i) {
            double p = Double.NaN;
            if (i[8] == null) {
                p = 0;
            } else if (((Double) i[8]).doubleValue() <= 0.99602) {
                p = 0;
            } else if (((Double) i[8]).doubleValue() > 0.99602) {
                p = GoodTree.N785b050e38(i);
            }
            return p;
        }
        static double N785b050e38(Object []i) {
            double p = Double.NaN;
            if (i[4] == null) {
                p = 0;
            } else if (((Double) i[4]).doubleValue() <= 29.207949) {
                p = 0;
            } else if (((Double) i[4]).doubleValue() > 29.207949) {
                p = 1;
            }
            return p;
        }
        static double N1fa917dd39(Object []i) {
            double p = Double.NaN;
            if (i[4] == null) {
                p = 0;
            } else if (((Double) i[4]).doubleValue() <= 27.327623) {
                p = GoodTree.N165dbe6840(i);
            } else if (((Double) i[4]).doubleValue() > 27.327623) {
                p = 1;
            }
            return p;
        }
        static double N165dbe6840(Object []i) {
            double p = Double.NaN;
            if (i[2] == null) {
                p = 1;
            } else if (((Double) i[2]).doubleValue() <= -0.964948) {
                p = 1;
            } else if (((Double) i[2]).doubleValue() > -0.964948) {
                p = GoodTree.N647fa95041(i);
            }
            return p;
        }
        static double N647fa95041(Object []i) {
            double p = Double.NaN;
            if (i[4] == null) {
                p = 0;
            } else if (((Double) i[4]).doubleValue() <= 26.13715) {
                p = 0;
            } else if (((Double) i[4]).doubleValue() > 26.13715) {
                p = GoodTree.N7944f6bb42(i);
            }
            return p;
        }
        static double N7944f6bb42(Object []i) {
            double p = Double.NaN;
            if (i[3] == null) {
                p = 1;
            } else if (((Double) i[3]).doubleValue() <= 10.16326) {
                p = 1;
            } else if (((Double) i[3]).doubleValue() > 10.16326) {
                p = 0;
            }
            return p;
        }
        static double N1ea2ad3043(Object []i) {
            double p = Double.NaN;
            if (i[0] == null) {
                p = 1;
            } else if (((Double) i[0]).doubleValue() <= 0.072444) {
                p = 1;
            } else if (((Double) i[0]).doubleValue() > 0.072444) {
                p = GoodTree.N4a183a0644(i);
            }
            return p;
        }
        static double N4a183a0644(Object []i) {
            double p = Double.NaN;
            if (i[8] == null) {
                p = 1;
            } else if (((Double) i[8]).doubleValue() <= 0.951076) {
                p = GoodTree.N7c8985ea45(i);
            } else if (((Double) i[8]).doubleValue() > 0.951076) {
                p = GoodTree.N28b7058646(i);
            }
            return p;
        }
        static double N7c8985ea45(Object []i) {
            double p = Double.NaN;
            if (i[8] == null) {
                p = 0;
            } else if (((Double) i[8]).doubleValue() <= 0.482945) {
                p = 0;
            } else if (((Double) i[8]).doubleValue() > 0.482945) {
                p = 1;
            }
            return p;
        }
        static double N28b7058646(Object []i) {
            double p = Double.NaN;
            if (i[3] == null) {
                p = 0;
            } else if (((Double) i[3]).doubleValue() <= 11.898458) {
                p = GoodTree.N7cbb54c47(i);
            } else if (((Double) i[3]).doubleValue() > 11.898458) {
                p = 0;
            }
            return p;
        }
        static double N7cbb54c47(Object []i) {
            double p = Double.NaN;
            if (i[0] == null) {
                p = 1;
            } else if (((Double) i[0]).doubleValue() <= 0.475098) {
                p = GoodTree.N4eeb949348(i);
            } else if (((Double) i[0]).doubleValue() > 0.475098) {
                p = GoodTree.N4c67675f49(i);
            }
            return p;
        }
        static double N4eeb949348(Object []i) {
            double p = Double.NaN;
            if (i[4] == null) {
                p = 0;
            } else if (((Double) i[4]).doubleValue() <= 34.284286) {
                p = 0;
            } else if (((Double) i[4]).doubleValue() > 34.284286) {
                p = 1;
            }
            return p;
        }
        static double N4c67675f49(Object []i) {
            double p = Double.NaN;
            if (i[4] == null) {
                p = 0;
            } else if (((Double) i[4]).doubleValue() <= 41.073502) {
                p = 0;
            } else if (((Double) i[4]).doubleValue() > 41.073502) {
                p = GoodTree.N18b3b52950(i);
            }
            return p;
        }
        static double N18b3b52950(Object []i) {
            double p = Double.NaN;
            if (i[1] == null) {
                p = 0;
            } else if (((Double) i[1]).doubleValue() <= -10.412929) {
                p = 0;
            } else if (((Double) i[1]).doubleValue() > -10.412929) {
                p = 1;
            }
            return p;
        }
    }
