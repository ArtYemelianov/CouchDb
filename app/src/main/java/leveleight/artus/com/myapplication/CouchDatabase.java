package leveleight.artus.com.myapplication;

import android.content.Context;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Manager;
import com.couchbase.lite.android.AndroidContext;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * The helper class, that allow creates instance of general modules of couch database author Artem
 * Emelyanow
 */
public class CouchDatabase {
    private static String KDatabaseName = "danke_ring";
    public static String KDocType = "_doc_type";

    public final Database mDatabase;
    private final Manager mManager;

    private static CouchDatabase mInstance;

    private final Set<IInitializeExecutor> mInitializeViews;

    public static CouchDatabase Instance(Context aContext) {
        if (mInstance == null) {
            try {
                mInstance = new CouchDatabase(aContext, KDatabaseName);
            } catch (CouchbaseLiteException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return mInstance;
    }

    /**
     * Add executorsto database
     * @param aIInitializeExecutor Executor
     */
    public void addExecutor(IInitializeExecutor aIInitializeExecutor) {
        mInitializeViews.add(aIInitializeExecutor);
    }

    /**
     * Constructor
     * @param aContext      Context
     * @param aDatabaseName Database
     * @throws CouchbaseLiteException
     */
    private CouchDatabase(Context aContext, String aDatabaseName) throws CouchbaseLiteException, IOException {
        mManager = new Manager(new AndroidContext(aContext), Manager.DEFAULT_OPTIONS);

        if (!Manager.isValidDatabaseName(aDatabaseName)) {
            throw new IllegalArgumentException("The database name " + aDatabaseName + " is wrong");
        }
        mDatabase = mManager.getDatabase(aDatabaseName);
        mInitializeViews = new HashSet<>();

    }

    /**
     * Initiates database to normal work
     */
    public void start() {
        for (IInitializeExecutor executor : mInitializeViews) {
            executor.execute(mDatabase);
        }
    }

    public void close() {
        if (mManager != null) {
            // also operation closes all databases
            mManager.close();
        }
    }

    /**
     * Describes commands that are should be done on start of initialization of database. As views,
     * etc.
     */
    public interface IInitializeExecutor {
        /**
         * Initialize view of database
         * @param aDatabase Database
         */
        void execute(Database aDatabase);
    }
}
