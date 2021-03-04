package com.example.smarthome.iot;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.smarthome.iot.adapter.FamilyMoveAdapter;
import com.example.smarthome.iot.adapter.SearchPupItenAdapter;
import com.example.smarthome.iot.entry.HomeSmartDefenseBean;
import com.example.smarthome.iot.entry.SmartInfoList;
import com.xhwl.commonlib.base.BaseActivity;
import com.xhwl.commonlib.base.BaseFrament;
import com.example.smarthome.R;
import com.example.smarthome.iot.entry.CommonResp;
import com.example.smarthome.iot.net.Constant;
import com.example.smarthome.iot.util.DropPopMenuIot;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.convert.StringConvert;
import com.lzy.okgo.model.Response;
import com.lzy.okrx2.adapter.ObservableResponse;
import com.xhwl.commonlib.uiutils.LogUtils;
import com.xhwl.commonlib.uiutils.ToastUtil;
import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
/**
 * author: ydm
 * date: 2020/11/5 9:16
 * description: 移交家庭
 * update:
 * version:
 */
public class TransferFamilyActivity extends BaseActivity implements View.OnClickListener, BaseQuickAdapter.OnItemChildClickListener, TextWatcher {

    private TextView mTopTitle;
    private LinearLayout mTopbackimg;
    private TextView mTopTx;
    private RecyclerView mMoveFamlyRv;
    private ArrayList<SmartInfoList.FamilysBean> mFamilysBean;
    private FamilyMoveAdapter mFamilyMoveAdapter;
    private PopupWindow mPopupWindow;
    private View inflate;
    private EditText mFamilyEt;
    private TextView mFamilyBt;
    private DropPopMenuIot familyDropPopMenu;
    private AutoCompleteTextView AutoComplete;
    private Button mSouSuo;
    private String mFamilyId;
    private int familyId;
    private TextView familyName;
    private TextView mFamilyBack;
    private String familyId1;
    private SearchPupItenAdapter<String> adapter;
    private ArrayList<String> strings;
    private ArrayList<SmartInfoList.FamilysBean> familyBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_move);
        mTopTitle = findViewById(R.id.top_title);
        mTopbackimg = findViewById(R.id.top_back);
        mTopTx = findViewById(R.id.top_btn);
        mMoveFamlyRv = findViewById(R.id.family_move_rv);

        AutoComplete = (AutoCompleteTextView)findViewById(R.id.autoCompleteTextView1);
        //创建一个ArrayAdapter适配器
        familyBean = getIntent().getParcelableArrayListExtra("family");
        inflate = LayoutInflater.from(this).inflate(R.layout.family,null,false);
        mFamilyEt = inflate.findViewById(R.id.family_et);
        familyName = inflate.findViewById(R.id.familyname);
        mFamilyBack = inflate.findViewById(R.id.family_back);
        mFamilyBt = inflate.findViewById(R.id.family_bt);
        mPopupWindow = new PopupWindow(inflate, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setTouchable(true);
        mFamilysBean=new ArrayList<>();
        for (int i = 0; i < familyBean.size(); i++) {
            mFamilysBean.add(i, familyBean.get(familyBean.size()-1-i));
        }
        autoinit(mFamilysBean);
        init();
    }

    private void autoinit(ArrayList<SmartInfoList.FamilysBean> mFamilysBean) {
        strings = new ArrayList<>();
        for (int i = 0; i < mFamilysBean.size(); i++) {
            strings.add(i,mFamilysBean.get(i).getFamilyName());
        }
        adapter = new SearchPupItenAdapter<String>(this,android.R.layout.simple_dropdown_item_1line, strings);
        AutoComplete.setAdapter(adapter);

    }

    private void init() {
        mTopTitle.setText("移交家庭");
        mTopTx.setVisibility(View.GONE);
        mTopbackimg.setOnClickListener(this);
        mFamilyBack.setOnClickListener(this);
        mFamilyBt.setOnClickListener(this);
        mMoveFamlyRv.setLayoutManager(new LinearLayoutManager(this));
        mFamilyMoveAdapter = new FamilyMoveAdapter(mFamilysBean);
        mMoveFamlyRv.setAdapter(mFamilyMoveAdapter);
        mFamilyMoveAdapter.setOnItemChildClickListener(this);
        AutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String s = parent.getItemAtPosition(position).toString();
                ArrayList<SmartInfoList.FamilysBean> familysBean=new ArrayList<>();
                for (int i = 0; i <mFamilysBean.size(); i++) {
                    if (mFamilysBean.get(i).getFamilyName().equals(s)){
                        familysBean.add(mFamilysBean.get(i));
                    }
                }
                mFamilysBean=familysBean;
                if (familysBean.size()==0){
                    ToastUtil.show("此家庭已被移交");
                }
                mFamilyMoveAdapter.setNewData(familysBean);
            }
        });
        AutoComplete.addTextChangedListener(this);

    }




    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.top_back) {
            finish();

        }if (id==R.id.family_back){
            mPopupWindow.dismiss();
        }
        else if (id == R.id.family_bt) {
            String mFamilyPhone = mFamilyEt.getText().toString();
            com.alibaba.fastjson.JSONObject obj = new com.alibaba.fastjson.JSONObject();
            obj.put("familyId",mFamilyId);
            obj.put("targetMasterUserId", mFamilyPhone);
            OkGo.<String>post(Constant.HOST + Constant.Family)
                    .upJson(JSON.toJSONString(obj))
                    .converter(new StringConvert())//
                    .adapt(new ObservableResponse<String>())//
                    .subscribeOn(Schedulers.io())//
                    .doOnSubscribe(new Consumer<Disposable>() {
                        @Override
                        public void accept(@io.reactivex.annotations.NonNull Disposable disposable) throws Exception {
//                        showLoading();
                            LogUtils.e("accept", disposable.isDisposed() + "===");
                        }
                    })//
                    .map(new Function<Response<String>, CommonResp>() {
                        @Override
                        public CommonResp apply(Response<String> stringResponse) throws Exception {
                            LogUtils.e("apply", "====" + stringResponse.body());
//                        JSON
                            CommonResp resp = JSON.parseObject(stringResponse.body(), CommonResp.class);

                            return resp;
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())//
                    .subscribe(new Observer<CommonResp>() {



                        @Override
                        public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
//                        addDisposable(d);
                            LogUtils.e("onSubscribe","   =---====");
                        }

                        @Override
                        public void onNext(CommonResp commonResp) {
                            mPopupWindow.dismiss();
                            String state = commonResp.getState();
                            Log.e("TAG", "onNext: "+commonResp.toString() );
                            if (commonResp.getErrorCode().equals("200")&&mFamilyPhone.length()==11) {
                                if (commonResp.getMessage().contains("家庭不存在")){
                                    ToastUtil.show("家庭不存在");
                                }else {
                                    for (int i = 0; i < mFamilysBean.size(); i++) {
                                        if (mFamilysBean.get(i).getFamilyId().equalsIgnoreCase(mFamilysBean.get(familyId).getFamilyId())){
                                            familyId1 = mFamilysBean.get(i).getFamilyId();
                                            mFamilysBean.remove(i);
                                            ToastUtil.show("移交成功");
                                        }
                                    }

                                    mFamilyMoveAdapter.notifyDataSetChanged();
                                    EventBus.getDefault().post("FamilyMoveActivity");
                                }
                            }else {
                                ToastUtil.show("家庭不存在");
                            }


                        }


                        @Override
                        public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                            e.printStackTrace();
                            ToastUtil.show("家庭不存在");
                        }

                        @Override
                        public void onComplete() {
                            LogUtils.e("onComplete","   =====");
                        }
                    });


        }

    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        int id = view.getId();
        if (id==R.id.move_family_tv){
            familyId=position;
            familyName.setText("当前家庭："+mFamilysBean.get(position).getFamilyName());
            mPopupWindow.showAtLocation(view, Gravity.CENTER,0,0);
            mFamilyId = mFamilysBean.get(position).getFamilyId();
        }

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        mFamilysBean.clear();
        for (int i = 0; i < familyBean.size(); i++) {
            if (familyBean.get(i).getFamilyId().equals(familyId1)){
                familyBean.remove(i);
            }
        }
        for (int i = 0; i < familyBean.size(); i++) {
            mFamilysBean.add(i,familyBean.get(familyBean.size()-1-i));
        }
        mFamilyMoveAdapter.setNewData(mFamilysBean);
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
