package eu.liveandgov.wp1.sensor_collector.api;

import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

import eu.liveandgov.wp1.data.Callback;

/**
 * <p>
 * Wraps the MORA API with a scheduled executor service and wrapping the remote exceptions as
 * runtime exceptions.
 * </p>
 * <p>
 * The threaded MORA API also provides wrappers for the methods using callback patterns.
 * </p>
 * <p>
 * Created on 05.12.2014.
 * </p>
 *
 * @author lukashaertel
 */
public class ThreadedMoraAPI implements MoraAPI {
    private final MoraAPI actual;

    private final ScheduledExecutorService scheduledExecutorService;

    /**
     * <p>Constructs the threaded MORA API on an actual implementation and an executor</p>
     *
     * @param actual                   The implementation
     * @param scheduledExecutorService The executor service
     */
    public ThreadedMoraAPI(MoraAPI actual, ScheduledExecutorService scheduledExecutorService) {
        this.actual = actual;
        this.scheduledExecutorService = scheduledExecutorService;
    }

    @Override
    public MoraConfig getConfig() {
        try {
            return actual.getConfig();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setConfig(final MoraConfig c) {
        scheduledExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    actual.setConfig(c);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Override
    public void resetConfig() {
        scheduledExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    actual.resetConfig();
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Override
    public void registerRecorder(final RecorderConfig c) {
        scheduledExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    actual.registerRecorder(c);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Override
    public void unregisterRecorder(final RecorderConfig c) {
        scheduledExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    actual.unregisterRecorder(c);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Override
    public List<RecorderConfig> getRecorders() {
        try {
            return actual.getRecorders();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void getRecorders(final Callback<List<RecorderConfig>> callback) {
        scheduledExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                callback.call(getRecorders());
            }
        });
    }

    @Override
    public List<String> getRecorderItems(RecorderConfig c) {
        try {
            return actual.getRecorderItems(c);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void getRecorderItems(final RecorderConfig c, final Callback<List<String>> callback) {
        scheduledExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                callback.call(getRecorderItems(c));
            }
        });
    }

    @Override
    public void annotate(final String userTag) {
        scheduledExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    actual.annotate(userTag);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Override
    public void startRecording() {
        scheduledExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    actual.startRecording();
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Override
    public void stopRecording() {
        scheduledExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    actual.stopRecording();
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Override
    public boolean isRecording() {
        try {
            return actual.isRecording();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void isRecording(final Callback<Boolean> callback) {
        scheduledExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                callback.call(isRecording());
            }
        });
    }


    @Override
    public void startStreaming() {
        scheduledExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    actual.startStreaming();
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Override
    public void stopStreaming() {
        scheduledExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    actual.stopStreaming();
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Override
    public boolean isStreaming() {
        try {
            return actual.isStreaming();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void isStreaming(final Callback<Boolean> callback) {
        scheduledExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                callback.call(isStreaming());
            }
        });
    }

    @Override
    public List<Trip> getTrips() {
        try {
            return actual.getTrips();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void getTrips(final Callback<List<Trip>> callback) {
        scheduledExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                callback.call(getTrips());
            }
        });
    }

    @Override
    public boolean transferTrip(Trip trip) {
        try {
            return actual.transferTrip(trip);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void transferTrip(final Trip trip, final Callback<Boolean> callback) {
        scheduledExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                callback.call(transferTrip(trip));
            }
        });
    }

    @Override
    public void deleteTrip(final Trip trip) {
        scheduledExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    actual.deleteTrip(trip);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Override
    public List<Bundle> getReports() {
        try {
            return actual.getReports();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void getReports(final Callback<List<Bundle>> callback) {
        scheduledExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                callback.call(getReports());
            }
        });
    }

    @Override
    public IBinder asBinder() {
        return actual.asBinder();
    }
}
