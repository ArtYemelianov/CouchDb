package leveleight.artus.com.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.Toast;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.EditorAction;

@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {

    private CouchDatabase mCouchDb;

    @Click(R.id.saveBtn)
    protected void handleSaveBtn() {

    }

    @Click(R.id.showBtn)
    protected void handleShowBtn() {

    }

    @EditorAction(R.id.edittext_label)
    void handleEditAction(TextView aTxt, int action, KeyEvent aEvent) {

        Toast.makeText(this, "Editor action", Toast.LENGTH_SHORT).show();
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
