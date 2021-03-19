package debug;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.xhwl.commonlib.base.BaseActivity;
import com.xhwl.commonlib.uiutils.ToastUtil;


public class SmartTestActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView textView = new TextView(this);
        textView.setGravity(Gravity.CENTER);
        textView.setText("DebugActivity\nClick to finish this activity");
        setContentView(textView);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                finish();
//                startActivity(new Intent(SmartTestActivity.this,));
                ToastUtil.show(SmartTestActivity.this,"点击");
            }
        });
    }
}
