package leveleight.artus.com.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.Toast;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;
import com.couchbase.lite.View;
import com.google.common.base.Joiner;
import com.google.common.primitives.Ints;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.EditorAction;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {
    private static final String QUERY_ALL = "query_all";
    private static final String QUERY_INTEGER = "query_integer";
    private static final String QUERY_SELECT_INTERVAL = "query_more_than";
    private static final String QUERY_START = "query_between";
    private static final String QUERY_TILL = "query_till";

    private CouchDatabase mCouchDb;

    @ViewById(R.id.status_label)
    protected TextView mStatus;

    @ViewById(R.id.edittext_start_label)
    protected TextView mStartLabel;

    @ViewById(R.id.edittext_end_label)
    protected TextView mEndLabel;

    @Click(R.id.showAllBtn)
    protected void handleShowAllBtn() {
        List list = getResult(QUERY_ALL);
        print(list);
    }

    @Click(R.id.showIntegerBtn)
    protected void handleShowIntegerBtn() {
        List list = getResult(QUERY_INTEGER);
        print(list);
    }

    @Click(R.id.showInterval)
    protected void handleSelectInterval() {
        Integer end = Ints.tryParse(mEndLabel.getText().toString());
        Integer start = Ints.tryParse(mStartLabel.getText().toString());

        List list = Collections.EMPTY_LIST;
        if (end != null && start != null) {
            list = querySelectInterval(QUERY_SELECT_INTERVAL, aQuery -> {
                aQuery.setStartKey(start);
                aQuery.setEndKey(end);
            });
        } else if (end != null && start == null) {
            list = querySelectInterval(QUERY_SELECT_INTERVAL, aQuery -> {
                aQuery.setEndKey(end);
            });
        } else if (end == null && start != null) {
            list = querySelectInterval(QUERY_SELECT_INTERVAL, aQuery -> {
                aQuery.setStartKey(start);
            });
        } else {
            Toast.makeText(this, "Wrong", Toast.LENGTH_SHORT).show();
        }
        print(list);
    }

    interface IQueryInitialization {
        void setup(Query aQuery);
    }

    private List<String> querySelectInterval(String aQuery, IQueryInitialization aSetuper) {
        Query query = mCouchDb.mDatabase.getView(aQuery).createQuery();
        List list = new ArrayList<>();
        try {
            aSetuper.setup(query);
            query.setDescending(false);
            QueryEnumerator result = query.run();

            for (Iterator<QueryRow> it = result; it.hasNext(); ) {
                QueryRow row = it.next();
                list.add(row.getKey());
            }
        } catch (CouchbaseLiteException aE) {
            aE.printStackTrace();
        }
        return list;
    }


    private List<String> getResult(String aQuery) {
        Query query = mCouchDb.mDatabase.getView(aQuery).createQuery();
        List list = new ArrayList<>();
        try {
            QueryEnumerator result = query.run();
            for (Iterator<QueryRow> it = result; it.hasNext(); ) {
                QueryRow row = it.next();
                String key = (String) row.getKey();
                list.add(key);
            }
        } catch (CouchbaseLiteException aE) {
            aE.printStackTrace();
        }
        return list;
    }

    @EditorAction(R.id.edittext_save_label)
    void handleEditAction(TextView aTxt, int action, KeyEvent aEvent) {
        String txt = aTxt.getText().toString();
        save(txt);
        aTxt.setText("");
    }

    @EditorAction(R.id.edittext_end_label)
    void handleEndAction(TextView aTxt, int action, KeyEvent aEvent) {
        handleSelectInterval();
    }

    @EditorAction(R.id.edittext_start_label)
    void handleStartAction(TextView aTxt, int action, KeyEvent aEvent) {
        mEndLabel.requestFocus();
    }

    private void print(List aList) {
        String str = Joiner.on(" ").join(aList);
        mStatus.setText(str);

    }

    private void save(String aTxt) {

        Database db = mCouchDb.mDatabase;
        Document doc = db.createDocument();

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(SimpleItem.DATA, aTxt);

        try {
            doc.putProperties(properties);
        } catch (CouchbaseLiteException aE) {
            aE.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCouchDb = CouchDatabase.Instance(this);
        mCouchDb.addExecutor(mExecutor);
        mCouchDb.start();
    }

    @Override
    protected void onDestroy() {
        mCouchDb.close();
        mCouchDb = null;
        super.onDestroy();
    }

    private final static class SimpleItem {
        public static final String TYPE = SimpleItem.class.toString();
        public static final String DATA = "data";
        public final String mData;

        public SimpleItem(String aData) {
            mData = aData;
        }

    }

    private static CouchDatabase.IInitializeExecutor mExecutor = new CouchDatabase.IInitializeExecutor() {
        @Override
        public void execute(Database aDatabase) {
            View query = aDatabase.getView(QUERY_ALL);
            query.setMap((document, emitter) ->
                    emitter.emit(document.get(SimpleItem.DATA), "nothing"), "1");

            query = aDatabase.getView(QUERY_INTEGER);
            query.setMap((document, emitter) -> {
                String str = (String) document.get(SimpleItem.DATA);
                if (Ints.tryParse(str) != null) {
                    emitter.emit(str, "nothing");
                }
            }, "1");

            query = aDatabase.getView(QUERY_SELECT_INTERVAL);
            query.setMap((document, emitter) -> {
                String str = (String) document.get(SimpleItem.DATA);
                if (Ints.tryParse(str) != null) {
                    emitter.emit(Ints.tryParse(str), "nothing");
                }
            }, "6");
        }
    };
}
