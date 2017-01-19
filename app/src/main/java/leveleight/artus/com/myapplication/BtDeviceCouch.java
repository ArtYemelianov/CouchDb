package leveleight.artus.com.myapplication;

import android.support.annotation.NonNull;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.View;

import java.util.HashMap;
import java.util.Map;

/**
 * The
 *
 * @author Artem Emelyanow
 */
public class BtDeviceCouch implements ICouchPersitance<BtDevice> {
    public static final String KType = "_ble_devices_";
    public static final String KName = "_name";
    public static final String KAddress = "_address";
    private final Document mDocument;
    private BtDevice mInstanceDevice;

    /**
     * Constructor
     */
    private BtDeviceCouch(@NonNull Builder aBuilder) {
        if (aBuilder.mDocument != null) {
            mDocument = aBuilder.mDocument;
        } else if (aBuilder.mDatabase != null && aBuilder.mInstance != null) {
            mDocument = aBuilder.mDatabase.createDocument();
            mInstanceDevice = aBuilder.mInstance;
        } else {
            throw new IllegalArgumentException("The database or fields cannot be null");
        }
    }

    @Override
    public boolean equals(Object aObj) {
        if (aObj == this) {
            return true;
        }
        if (aObj == null || !(aObj instanceof BtDevice)) {
            return false;
        }
        Document doc = (Document) aObj;
        return mDocument.getId().equals(doc.getId());
    }

    @Override
    public void store() {
        Map<String, Object> properties = properties();
        properties.put(KName, mInstanceDevice.Name);
        properties.put(KAddress, mInstanceDevice.Mac);
        properties.put(CouchDatabase.KDocType, KType);

        try {
            mDocument.putProperties(properties);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Map<String, Object> properties() {
        Map<String, Object> properties = new HashMap<>();
        properties.putAll(mDocument.getProperties());

        properties.put(KName, mInstanceDevice.Name);
        properties.put(KAddress, mInstanceDevice.Mac);
        return properties;
    }

    @Override
    public BtDevice restore() {
        String name = (String) mDocument.getProperties().get(KName);
        String address = (String) mDocument.getProperties().get(KAddress);
        return new BtDevice(name, address);
    }

    /**
     * Builder
     */
    public static class Builder {
        private Document mDocument;
        private Database mDatabase;
        private BtDevice mInstance;

        /**
         * Sets document
         */
        public Builder document(@NonNull Document aDocument) {
            mDocument = aDocument;
            return this;
        }

        /**
         * Sets database
         */
        public Builder database(@NonNull Database aDatabase) {
            mDatabase = aDatabase;
            return this;
        }

        /**
         * Creates object from instance
         */
        public Builder intance(@NonNull BtDevice aDevice) {
            mInstance = aDevice;
            return this;
        }

        /**
         * Builds instance
         *
         * @return A new instance
         */
        public BtDeviceCouch build() {
            if (mInstance == null && mDatabase == null) {
                throw new IllegalArgumentException("The some fields are missed");
            } else if (mDocument == null) {
                throw new IllegalArgumentException("The document missed");
            }
            return new BtDeviceCouch(this);
        }
    }

    public static final CouchDatabase.IInitializeExecutor mExecutor = new CouchDatabase.IInitializeExecutor() {
        public final String QUARY_ALL = KType.concat("query_all");

        @Override
        public void execute(Database aDatabase) {
            // select all devices
            View quary_all = aDatabase.getView(QUARY_ALL);
            quary_all.setDocumentType(KType);
            quary_all.setMap((document, emitter) -> {
                emitter.emit(document.get(KAddress), document.get(KName));
            }, "0.1");

            //
        }
    };
}