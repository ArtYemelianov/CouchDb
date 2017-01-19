package leveleight.artus.com.myapplication;

import android.bluetooth.BluetoothDevice;
import android.support.annotation.NonNull;

/**
 * Presents simple class to determine BT device
 */
public class BtDevice {
    /**
     * Name of device
     */
    public final String Name;
    /**
     * The mac of device.<br> Consists from
     */
    public final String Mac;

    /**
     * Constructor
     *
     * @param aDevice {@link BluetoothDevice}
     */
    public BtDevice(@NonNull BluetoothDevice aDevice) {
        Name = aDevice.getName();
        Mac = aDevice.getAddress();
    }

    /**
     * Constructor
     *
     * @param aName The name of device
     * @param aMac  The mac of device
     */
    public BtDevice(@NonNull String aName, @NonNull String aMac) {
        Name = aName;
        Mac = aMac;
    }

    @Override
    public boolean equals(Object aObj) {
        if (aObj == this) {
            return true;
        }
        if (aObj == null || !(aObj instanceof BtDevice)) {
            return false;
        }
        BtDevice other = (BtDevice) aObj;

        boolean result = other.Name.equals(Name);
        result = result && other.Mac.equals(Mac);
        return result;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Name.hashCode();
        result = prime * result + Mac.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return String.format("%s {%s}", Name, Mac);
    }
}
