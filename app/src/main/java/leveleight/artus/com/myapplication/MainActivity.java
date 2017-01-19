package leveleight.artus.com.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;

@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {

    private CouchDatabase mCouchDb;

    @Click(R.id.saveBtn)
    protected void handleSaveBtn() {

    }

    @Click(R.id.showBtn)
    protected void handleShowBtn() {

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCouchDb = CouchDatabase.Instance(this);
    }

    @Override
    protected void onDestroy() {
        mCouchDb.close();
        mCouchDb = null;
        super.onDestroy();
    }
}
