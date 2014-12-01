package eu.liveandgov.wp1.sensor_collector.api;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.common.collect.ComparisonChain;

import java.io.Serializable;

/**
 * Configuration object for passing via API
 * Created by lukashaertel on 17.11.2014.
 */
public class MoraConfig implements Parcelable, Comparable<MoraConfig>, Serializable {
    public static final Creator<MoraConfig> CREATOR = new Creator<MoraConfig>() {
        @Override
        public MoraConfig createFromParcel(Parcel source) {
            return new MoraConfig(source);
        }

        @Override
        public MoraConfig[] newArray(int size) {
            return new MoraConfig[size];
        }
    };
    public String user;

    public int secretLength;

    public String upload;

    public String streaming;

    public Integer gps;

    public boolean velocity;

    public Integer acceleration;

    public Integer linearAcceleration;

    public Integer gravity;

    public Integer magnetometer;

    public Integer rotation;

    public Integer wifi;

    public Integer bluetooth;

    public Integer gsm;

    private boolean googleActivity;

    private static boolean readBoolean(Parcel source) {
        return source.readByte() != 0;
    }

    private static Integer readInteger(Parcel source) {
        if (readBoolean(source))
            return source.readInt();
        else
            return null;
    }

    private static void writeBoolean(Parcel target, boolean v) {
        target.writeByte(v ? (byte) 1 : (byte) 0);
    }

    private static void writeInteger(Parcel target, Integer v) {
        if (v == null)
            writeBoolean(target, false);
        else {
            writeBoolean(target, true);
            target.writeInt(v);
        }
    }

    private MoraConfig(Parcel source) {
        user = source.readString();
        secretLength = source.readInt();
        upload = source.readString();
        streaming = source.readString();
        gps = readInteger(source);
        velocity = readBoolean(source);
        acceleration = readInteger(source);
        linearAcceleration = readInteger(source);
        gravity = readInteger(source);
        magnetometer = readInteger(source);
        rotation = readInteger(source);
        wifi = readInteger(source);
        bluetooth = readInteger(source);
        gsm = readInteger(source);
        googleActivity = readBoolean(source);
    }

    public MoraConfig(String user, int secretLength, String upload, String streaming, Integer gps, boolean velocity, Integer acceleration, Integer linearAcceleration, Integer gravity, Integer magnetometer, Integer rotation, Integer wifi, Integer bluetooth, Integer gsm, boolean googleActivity) {
        this.user = user;
        this.secretLength = secretLength;
        this.upload = upload;
        this.streaming = streaming;
        this.gps = gps;
        this.velocity = velocity;
        this.acceleration = acceleration;
        this.linearAcceleration = linearAcceleration;
        this.gravity = gravity;
        this.magnetometer = magnetometer;
        this.rotation = rotation;
        this.wifi = wifi;
        this.bluetooth = bluetooth;
        this.gsm = gsm;
        this.googleActivity = googleActivity;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(user);
        dest.writeInt(secretLength);
        dest.writeString(upload);
        dest.writeString(streaming);
        writeInteger(dest, gps);
        writeBoolean(dest, velocity);
        writeInteger(dest, acceleration);
        writeInteger(dest, linearAcceleration);
        writeInteger(dest, gravity);
        writeInteger(dest, magnetometer);
        writeInteger(dest, rotation);
        writeInteger(dest, wifi);
        writeInteger(dest, bluetooth);
        writeInteger(dest, gsm);
        writeBoolean(dest, googleActivity);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MoraConfig that = (MoraConfig) o;

        if (googleActivity != that.googleActivity) return false;
        if (secretLength != that.secretLength) return false;
        if (velocity != that.velocity) return false;
        if (acceleration != null ? !acceleration.equals(that.acceleration) : that.acceleration != null)
            return false;
        if (bluetooth != null ? !bluetooth.equals(that.bluetooth) : that.bluetooth != null)
            return false;
        if (gps != null ? !gps.equals(that.gps) : that.gps != null) return false;
        if (gravity != null ? !gravity.equals(that.gravity) : that.gravity != null) return false;
        if (gsm != null ? !gsm.equals(that.gsm) : that.gsm != null) return false;
        if (linearAcceleration != null ? !linearAcceleration.equals(that.linearAcceleration) : that.linearAcceleration != null)
            return false;
        if (magnetometer != null ? !magnetometer.equals(that.magnetometer) : that.magnetometer != null)
            return false;
        if (rotation != null ? !rotation.equals(that.rotation) : that.rotation != null)
            return false;
        if (streaming != null ? !streaming.equals(that.streaming) : that.streaming != null)
            return false;
        if (upload != null ? !upload.equals(that.upload) : that.upload != null) return false;
        if (user != null ? !user.equals(that.user) : that.user != null) return false;
        if (wifi != null ? !wifi.equals(that.wifi) : that.wifi != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = user != null ? user.hashCode() : 0;
        result = 31 * result + secretLength;
        result = 31 * result + (upload != null ? upload.hashCode() : 0);
        result = 31 * result + (streaming != null ? streaming.hashCode() : 0);
        result = 31 * result + (gps != null ? gps.hashCode() : 0);
        result = 31 * result + (velocity ? 1 : 0);
        result = 31 * result + (acceleration != null ? acceleration.hashCode() : 0);
        result = 31 * result + (linearAcceleration != null ? linearAcceleration.hashCode() : 0);
        result = 31 * result + (gravity != null ? gravity.hashCode() : 0);
        result = 31 * result + (magnetometer != null ? magnetometer.hashCode() : 0);
        result = 31 * result + (rotation != null ? rotation.hashCode() : 0);
        result = 31 * result + (wifi != null ? wifi.hashCode() : 0);
        result = 31 * result + (bluetooth != null ? bluetooth.hashCode() : 0);
        result = 31 * result + (gsm != null ? gsm.hashCode() : 0);
        result = 31 * result + (googleActivity ? 1 : 0);
        return result;
    }

    @Override
    public int compareTo(@NonNull MoraConfig another) {
        return ComparisonChain.start()
                .compare(user, another.user)
                .compare(secretLength, another.secretLength)
                .compare(upload, another.upload)
                .compare(streaming, another.streaming)
                .compare(gps, another.gps)
                .compareFalseFirst(velocity, another.velocity)
                .compare(acceleration, another.acceleration)
                .compare(linearAcceleration, another.linearAcceleration)
                .compare(gravity, another.gravity)
                .compare(magnetometer, another.magnetometer)
                .compare(rotation, another.rotation)
                .compare(wifi, another.wifi)
                .compare(bluetooth, another.bluetooth)
                .compare(gsm, another.gsm)
                .compareFalseFirst(googleActivity, another.googleActivity)
                .result();
    }

}
