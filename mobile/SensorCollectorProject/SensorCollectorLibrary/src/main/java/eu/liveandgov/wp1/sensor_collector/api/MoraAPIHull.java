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
public class MoraAPIHull implements MoraAPI {
    private final MoraAPI actual;

    private final ScheduledExecutorService scheduledExecutorService;

    /**
     * <p>Constructs the threaded MORA API on an actual implementation and an executor</p>
     *
     * @param actual                   The implementation
     * @param scheduledExecutorService The executor service
     */
    public MoraAPIHull(MoraAPI actual, ScheduledExecutorService scheduledExecutorService) {
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
    public void setConfig(MoraConfig c) {
        try {
            actual.setConfig(c);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void setConfig(final MoraConfig c, final Runnable runnable) {
        scheduledExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                setConfig(c);
                runnable.run();
            }
        });
    }

    @Override
    public void resetConfig() {
        try {
            actual.resetConfig();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void resetConfig(final Runnable runnable) {
        scheduledExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                resetConfig();
                runnable.run();
            }
        });
    }

    @Override
    public void registerRecorder(final RecorderConfig c) {
        try {
            actual.registerRecorder(c);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void registerRecorder(final RecorderConfig c, final Runnable runnable) {
        scheduledExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                registerRecorder(c);
                runnable.run();
            }
        });
    }

    @Override
    public void unregisterRecorder(final RecorderConfig c) {

        try {
            actual.unregisterRecorder(c);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void unregisterRecorder(final RecorderConfig c, final Runnable runnable) {
        scheduledExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                unregisterRecorder(c);
                runnable.run();
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
        try {
            actual.annotate(userTag);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void annotate(final String userTag, final Runnable runnable) {
        scheduledExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                annotate(userTag);
                runnable.run();
            }
        });
    }

    @Override
    public void startRecording() {
        try {
            actual.startRecording();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void startRecording(final Runnable runnable) {
        scheduledExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                startRecording();
                runnable.run();
            }
        });
    }

    @Override
    public void stopRecording() {
        try {
            actual.stopRecording();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void stopRecording(final Runnable runnable) {
        scheduledExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                stopRecording();
                runnable.run();
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
        try {
            actual.startStreaming();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void startStreaming(final Runnable runnable) {
        scheduledExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                startStreaming();
                runnable.run();
            }
        });
    }

    @Override
    public void stopStreaming() {
        try {
            actual.stopStreaming();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void stopStreaming(final Runnable runnable) {
        scheduledExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                stopStreaming();
                runnable.run();
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
        try {
            actual.deleteTrip(trip);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteTrip(final Trip trip, final Runnable runnable) {
        scheduledExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                deleteTrip(trip);
                runnable.run();
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
