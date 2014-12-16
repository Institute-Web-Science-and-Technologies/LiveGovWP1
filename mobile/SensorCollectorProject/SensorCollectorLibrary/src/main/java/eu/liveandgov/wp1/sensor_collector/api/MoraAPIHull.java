package eu.liveandgov.wp1.sensor_collector.api;

import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

import com.google.inject.Inject;

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

    @Inject
    ScheduledExecutorService scheduledExecutorService;

    private MoraAPI implementation;

    public MoraAPI getImplementation() {
        return implementation;
    }

    public void setImplementation(MoraAPI implementation) {
        this.implementation = implementation;
    }

    private void assertHasImplementation() {
        if (implementation == null)
            throw new IllegalStateException("Implementation not set");
    }

    @Override
    public MoraConfig getConfig() {
        assertHasImplementation();

        try {
            return implementation.getConfig();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setConfig(MoraConfig c) {
        assertHasImplementation();

        try {
            implementation.setConfig(c);
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
        assertHasImplementation();

        try {
            implementation.resetConfig();
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
        assertHasImplementation();

        try {
            implementation.registerRecorder(c);
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
        assertHasImplementation();

        try {
            implementation.unregisterRecorder(c);
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
        assertHasImplementation();

        try {
            return implementation.getRecorders();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void getRecorders(final Callback<? super List<RecorderConfig>> callback) {
        scheduledExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                callback.call(getRecorders());
            }
        });
    }

    @Override
    public List<String> getRecorderItems(RecorderConfig c) {
        assertHasImplementation();

        try {
            return implementation.getRecorderItems(c);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void getRecorderItems(final RecorderConfig c, final Callback<? super List<String>> callback) {
        scheduledExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                callback.call(getRecorderItems(c));
            }
        });
    }

    @Override
    public void annotate(final String userTag) {
        assertHasImplementation();

        try {
            implementation.annotate(userTag);
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
        assertHasImplementation();

        try {
            implementation.startRecording();
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
        assertHasImplementation();

        try {
            implementation.stopRecording();
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
        assertHasImplementation();

        try {
            return implementation.isRecording();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void isRecording(final Callback<? super Boolean> callback) {
        scheduledExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                callback.call(isRecording());
            }
        });
    }


    @Override
    public void startStreaming() {
        assertHasImplementation();

        try {
            implementation.startStreaming();
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
        assertHasImplementation();

        try {
            implementation.stopStreaming();
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
        assertHasImplementation();

        try {
            return implementation.isStreaming();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void isStreaming(final Callback<? super Boolean> callback) {
        scheduledExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                callback.call(isStreaming());
            }
        });
    }

    @Override
    public List<Trip> getTrips() {
        assertHasImplementation();

        try {
            return implementation.getTrips();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void getTrips(final Callback<? super List<Trip>> callback) {
        scheduledExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                callback.call(getTrips());
            }
        });
    }

    @Override
    public boolean transferTrip(Trip trip) {
        assertHasImplementation();

        try {
            return implementation.transferTrip(trip);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void transferTrip(final Trip trip, final Callback<? super Boolean> callback) {
        scheduledExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                callback.call(transferTrip(trip));
            }
        });
    }

    @Override
    public void deleteTrip(final Trip trip) {
        assertHasImplementation();

        try {
            implementation.deleteTrip(trip);
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
        assertHasImplementation();

        try {
            return implementation.getReports();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void getReports(final Callback<? super List<Bundle>> callback) {
        scheduledExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                callback.call(getReports());
            }
        });
    }

    @Override
    public IBinder asBinder() {
        return implementation.asBinder();
    }
}
