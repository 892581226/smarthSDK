package com.xhwl.commonlib.uiutils.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.smarthome.R;


/**
 * 云对讲满意度调查 提交 dialog
 */
public class SatisfactionSubmitDialog extends Dialog {

    ImageView mDialogSubmitClose;
    TextView mDialogMessage1;
    TextView mDialogMessage2;
    TextView mDialogMessage3;
    TextView mDialogMessageSubmit;
    ImageView mDialogSatisfactionSubmitIcon;
    private int messageStr;

    private OnDialogSubmitCloseListener mOnDialogSubmitCloseListener;
    private OnDialogMessage mOnDialogMessage;
    private OnDialogMessage2 mOnDialogMessage2;
    private OnDialogMessage3 mOnDialogMessage3;
    private OnmDialogMessageSubmit mOnmDialogMessageSubmit;

    public SatisfactionSubmitDialog(@NonNull Context context) {
        super(context, R.style.MyDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_satisfaction_submit_layout);
        //按空白处不能取消动画
        setCanceledOnTouchOutside(false);
        initView();
        initData();
        initEvent();
    }

    private void initEvent() {
        mDialogSubmitClose.setOnClickListener(v -> {
            if (mOnDialogSubmitCloseListener != null) {
                mOnDialogSubmitCloseListener.onClick();
            }
        });
        mDialogMessage1.setOnClickListener(v -> {
            if (mOnDialogMessage != null) {
                mOnDialogMessage.onClick();
            }
        });
        mDialogMessage2.setOnClickListener(v -> {
            if (mOnDialogMessage2 != null) {
                mOnDialogMessage2.onClick();
            }
        });
        mDialogMessage3.setOnClickListener(v -> {
            if (mOnDialogMessage3 != null) {
                mOnDialogMessage3.onClick();
            }
        });
        mDialogMessageSubmit.setOnClickListener(v -> {
            if (mOnmDialogMessageSubmit != null) {
                mOnmDialogMessageSubmit.onClick();
            }
        });
    }

    private void initView() {
        mDialogSubmitClose = findViewById(R.id.dialog_submit_close);
        mDialogMessage1 = findViewById(R.id.dialog_message_1);
        mDialogMessage2 = findViewById(R.id.dialog_message_2);
        mDialogMessage3 = findViewById(R.id.dialog_message_3);
        mDialogMessageSubmit = findViewById(R.id.dialog_message_submit);
        mDialogSatisfactionSubmitIcon = findViewById(R.id.dialog_satisfaction_submit_icon);
    }

    /**
     * 初始化界面控件的显示数据
     */
    private void initData() {
        if (messageStr == 1) {
            mDialogSatisfactionSubmitIcon.setImageResource(R.drawable.icon_super_praise_big);
            mDialogMessage1.setText(R.string.dialog_super_praise_message1);
            mDialogMessage2.setText(R.string.dialog_super_praise_message2);
            mDialogMessage3.setText(R.string.dialog_super_praise_message3);

            setMessageIcon("#598DF3", R.drawable.dialog_select_blue);
        } else if (messageStr == 2) {
            mDialogSatisfactionSubmitIcon.setImageResource(R.drawable.icon_praise_big);
            mDialogMessage1.setText(R.string.dialog_praise_message1);
            mDialogMessage2.setText(R.string.dialog_praise_message2);
            mDialogMessage3.setText(R.string.dialog_praise_message3);

            setMessageIcon("#42D084", R.drawable.dialog_select_green);

        } else if (messageStr == 3) {
            mDialogSatisfactionSubmitIcon.setImageResource(R.drawable.icon_general_big);
            mDialogMessage1.setText(R.string.dialog_general_message1);
            mDialogMessage2.setText(R.string.dialog_general_message2);
            mDialogMessage3.setText(R.string.dialog_general_message3);

            setMessageIcon("#FEC235", R.drawable.dialog_select_yellow);
        } else if (messageStr == 4) {
            mDialogSatisfactionSubmitIcon.setImageResource(R.drawable.icon_bad_big);
            mDialogMessage1.setText(R.string.dialog_bad_message1);
            mDialogMessage2.setText(R.string.dialog_bad_message2);
            mDialogMessage3.setText(R.string.dialog_bad_message3);

            setMessageIcon("#FF5513", R.drawable.dialog_select_red);
        } else if (messageStr == 5) {
            mDialogSatisfactionSubmitIcon.setImageResource(R.drawable.icon_very_bad_big);
            mDialogMessage1.setText(R.string.dialog_very_bad_message1);
            mDialogMessage2.setText(R.string.dialog_very_bad_message2);
            mDialogMessage3.setText(R.string.dialog_very_bad_message3);

            setMessageIcon("#F7382A", R.drawable.dialog_select_red);
        }
    }

    private void setMessageIcon(String s, int p) {
        mDialogMessage1.setTextColor(Color.parseColor(s));
        mDialogMessage2.setTextColor(Color.parseColor(s));
        mDialogMessage3.setTextColor(Color.parseColor(s));
        mDialogMessage1.setBackgroundResource(p);
        mDialogMessage2.setBackgroundResource(p);
        mDialogMessage3.setBackgroundResource(p);

    }

    public void setMessage(int message) {
        messageStr = message;
    }

    public void setMessageBg(int level, int message) {
        if (message == 1) {
            setMessageIcon("#598DF3", R.drawable.dialog_select_blue);
            if (level == 1) {
                mDialogMessage1.setTextColor(Color.parseColor("#ffffff"));
                mDialogMessage1.setBackgroundResource(R.drawable.dialog_select_blue_1);
            } else if (level == 2) {
                mDialogMessage2.setTextColor(Color.parseColor("#ffffff"));
                mDialogMessage2.setBackgroundResource(R.drawable.dialog_select_blue_1);
            } else if (level == 3) {
                mDialogMessage3.setTextColor(Color.parseColor("#ffffff"));
                mDialogMessage3.setBackgroundResource(R.drawable.dialog_select_blue_1);
            }
        } else if (message == 2) {
            setMessageIcon("#42D084", R.drawable.dialog_select_green);
            if (level == 1) {
                mDialogMessage1.setTextColor(Color.parseColor("#ffffff"));
                mDialogMessage1.setBackgroundResource(R.drawable.dialog_select_green_1);
            } else if (level == 2) {
                mDialogMessage2.setTextColor(Color.parseColor("#ffffff"));
                mDialogMessage2.setBackgroundResource(R.drawable.dialog_select_green_1);
            } else if (level == 3) {
                mDialogMessage3.setTextColor(Color.parseColor("#ffffff"));
                mDialogMessage3.setBackgroundResource(R.drawable.dialog_select_green_1);
            }
        } else if (message == 3) {
            setMessageIcon("#FEC235", R.drawable.dialog_select_yellow);
            if (level == 1) {
                mDialogMessage1.setTextColor(Color.parseColor("#ffffff"));
                mDialogMessage1.setBackgroundResource(R.drawable.dialog_select_yellow_1);
            } else if (level == 2) {
                mDialogMessage2.setTextColor(Color.parseColor("#ffffff"));
                mDialogMessage2.setBackgroundResource(R.drawable.dialog_select_yellow_1);
            } else if (level == 3) {
                mDialogMessage3.setTextColor(Color.parseColor("#ffffff"));
                mDialogMessage3.setBackgroundResource(R.drawable.dialog_select_yellow_1);
            }
        } else if (message == 4) {
            setMessageIcon("#FA6757", R.drawable.dialog_select_red);
            if (level == 1) {
                mDialogMessage1.setTextColor(Color.parseColor("#ffffff"));
                mDialogMessage1.setBackgroundResource(R.drawable.dialog_select_red_1);
            } else if (level == 2) {
                mDialogMessage2.setTextColor(Color.parseColor("#ffffff"));
                mDialogMessage2.setBackgroundResource(R.drawable.dialog_select_red_1);
            } else if (level == 3) {
                mDialogMessage3.setTextColor(Color.parseColor("#ffffff"));
                mDialogMessage3.setBackgroundResource(R.drawable.dialog_select_red_1);
            }
        }else if (message == 5) {
            setMessageIcon("#F7382A", R.drawable.dialog_select_red);
            if (level == 1) {
                mDialogMessage1.setTextColor(Color.parseColor("#ffffff"));
                mDialogMessage1.setBackgroundResource(R.drawable.dialog_select_very_red_1);
            } else if (level == 2) {
                mDialogMessage2.setTextColor(Color.parseColor("#ffffff"));
                mDialogMessage2.setBackgroundResource(R.drawable.dialog_select_very_red_1);
            } else if (level == 3) {
                mDialogMessage3.setTextColor(Color.parseColor("#ffffff"));
                mDialogMessage3.setBackgroundResource(R.drawable.dialog_select_very_red_1);
            }
        }

    }

    /**
     * 设置按钮被点击的接口
     */
    public interface OnDialogSubmitCloseListener {
        public void onClick();
    }

    public interface OnDialogMessage {
        public void onClick();
    }

    public interface OnDialogMessage2 {
        public void onClick();
    }

    public interface OnDialogMessage3 {
        public void onClick();
    }

    public interface OnmDialogMessageSubmit {
        public void onClick();
    }

    /**
     * 点击关闭
     */
    public void setOnDialogSubmitCloseListener(OnDialogSubmitCloseListener onDialogSubmitCloseListener) {
        this.mOnDialogSubmitCloseListener = onDialogSubmitCloseListener;
    }


    /**
     * 点击了第一个
     */
    public void setOnDialogMessage(OnDialogMessage onDialogMessage) {
        this.mOnDialogMessage = onDialogMessage;
    }

    /**
     * 点击了第二个
     */
    public void setOnDialogMessage2(OnDialogMessage2 onDialogMessage2) {
        this.mOnDialogMessage2 = onDialogMessage2;
    }

    /**
     * 点击了第三个
     */
    public void setOnDialogMessage3(OnDialogMessage3 onDialogMessage3) {
        this.mOnDialogMessage3 = onDialogMessage3;
    }

    /**
     * 点击提交
     */
    public void setOnmDialogMessageSubmit(OnmDialogMessageSubmit onmDialogMessageSubmit) {
        this.mOnmDialogMessageSubmit = onmDialogMessageSubmit;
    }
}
