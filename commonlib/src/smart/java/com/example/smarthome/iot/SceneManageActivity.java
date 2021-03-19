package com.example.smarthome.iot;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.smarthome.R;
import com.example.smarthome.iot.adapter.SceneManageAdapter;
import com.example.smarthome.iot.entry.CommonResp;
import com.example.smarthome.iot.entry.SceneListVo;
import com.example.smarthome.iot.entry.SceneManageItemVo;
import com.example.smarthome.iot.entry.SmartInfoVo;
import com.example.smarthome.iot.entry.eventbus.UpdateFamilyEvent;
import com.example.smarthome.iot.net.Constant;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.convert.StringConvert;
import com.lzy.okgo.model.Response;
import com.lzy.okrx2.adapter.ObservableResponse;
import com.xhwl.commonlib.base.BaseActivity;
import com.xhwl.commonlib.constant.SpConstant;
import com.xhwl.commonlib.uiutils.LogUtils;
import com.xhwl.commonlib.uiutils.SPUtils;
import com.xhwl.commonlib.uiutils.ToastUtil;
import com.xhwl.commonlib.uiutils.dialog.BaseDialog;
import com.zyao89.view.zloading.ZLoadingDialog;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.zyao89.view.zloading.Z_TYPE.ROTATE_CIRCLE;

/**
 * author: glq
 * date: 2019/4/8 13:39
 * description: 场景管理
 * update: 2019/4/8
 * version: V1.4.1
 */
public class SceneManageActivity extends BaseActivity implements View.OnClickListener, BaseQuickAdapter.OnItemChildClickListener, BaseDialog.OnConfirmListener, BaseDialog.OnCancelListener {

    private RecyclerView mSceneManageRv;
    private RecyclerView mSceneDefaultManageRv;
    /**
     * 场景管理
     */
    private TextView mMySceneTv;
    private boolean isManageMode;
    private Intent mIntent = new Intent();
    private String token;
    //    private SmartInfoVo.FamilysBean mFamilyItem;
    private LinearLayout mTopBackNew;
    private TextView mTopBtnNew;
    private TextView mTopSecondTitleNew;
    private LinearLayout mSceneManageLinear;
    private SceneManageAdapter mSceneManageAdapter;
    private SceneManageAdapter mDefaultSceneAdapter;
    private List<SceneManageItemVo.Item> mDefaultSceneListVos = new ArrayList<>(); //默认场景
    private List<SceneManageItemVo.Item> mSceneManageItemVos = new ArrayList<>(); //新建场景
    private LinearLayout mSceneAddTvLinear;
    private int sceneState; //场景开关状态
    private String familyId, userId;

    private List<SmartInfoVo.FamilysBean.ScenesBean> mDefaultSceneList = new ArrayList<>(); //默认场景
    private List<SmartInfoVo.FamilysBean.ScenesBean> mSceneManageList = new ArrayList<>(); //新建场景
    private BaseDialog mBaseDialog;
    private String delSenceId;
    private int delPos;
    private ZLoadingDialog loadingDialog;
    private boolean flay=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iot_scene_manage);
        userId = SPUtils.get(this, SpConstant.SP_USER_TELEPHONE, "");
        familyId = SPUtils.get(this, "familyId" + userId, "");
        initView();
        initData();
    }

    @SuppressLint("ResourceAsColor")
    private void initData() {
        mTopSecondTitleNew.setText("场景管理");
        mTopBtnNew.setText("管理");
        mTopBtnNew.setVisibility(View.VISIBLE);
        mDefaultSceneAdapter = new SceneManageAdapter(mDefaultSceneList);
        mSceneManageAdapter = new SceneManageAdapter(mSceneManageList);
        mSceneManageAdapter.setOnItemChildClickListener(this);
        mDefaultSceneAdapter.setOnItemChildClickListener(this);
//        mSceneManageRv.setAdapter(mSceneManageAdapter);
//        mSceneDefaultManageRv.setAdapter(mDefaultSceneAdapter);
        mDefaultSceneAdapter.bindToRecyclerView(mSceneDefaultManageRv);
        mSceneManageAdapter.bindToRecyclerView(mSceneManageRv);

        loadingDialog = new ZLoadingDialog(this);
        loadingDialog.setLoadingBuilder(ROTATE_CIRCLE)//设置类型
                .setLoadingColor(getResources().getColor(R.color.yellow))//颜色
                .setHintText("Loading...")
                .setHintTextSize(16) // 设置字体大小 dp
                .setHintTextColor(Color.GRAY)  // 设置字体颜色
                .setDialogBackgroundColor(getResources().getColor(R.color.transparent))
                .setDurationTime(0.5) // 设置动画时间百分比 - 0.5倍
                .setCanceledOnTouchOutside(false);

        mBaseDialog = new BaseDialog(this, 1)
                .setTitleText("提示")
                .setInfoText("确定删除该场景模式?")
                .setCancelVisity(true)
                .setConfirmListener(this)
                .setCancelListener(this);
        mBaseDialog.setCancelable(false);
    }

    private void initView() {
        token = SPUtils.get(this, SpConstant.SP_USERTOKEN, "");
//        mFamilyItem = getIntent().getParcelableExtra("familyItem");
        mMySceneTv = (TextView) findViewById(R.id.my_scene_tv);
        mSceneManageRv = (RecyclerView) findViewById(R.id.scene_manage_rv);
        mSceneDefaultManageRv = (RecyclerView) findViewById(R.id.scene_default_manage_rv);
        mSceneManageRv.setLayoutManager(new LinearLayoutManager(this));
        mSceneDefaultManageRv.setLayoutManager(new LinearLayoutManager(this));
        mTopBackNew = (LinearLayout) findViewById(R.id.top_back_new);
        mTopBackNew.setOnClickListener(this);
        mTopBtnNew = (TextView) findViewById(R.id.top_btn_new);
        mTopBtnNew.setOnClickListener(this);
        mTopSecondTitleNew = (TextView) findViewById(R.id.top_second_title_new);
        mSceneManageLinear = (LinearLayout) findViewById(R.id.scene_manage_linear);
        mSceneAddTvLinear = (LinearLayout) findViewById(R.id.scene_add_tv_linear);
        mSceneAddTvLinear.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.scene_add_tv_linear) {// 新建场景
            mIntent.setClass(SceneManageActivity.this, SceneAppendActivity.class);
//                mIntent.putExtra("familyId",mFamilyItem.getFamilyId());
            startActivity(mIntent);
        } else if (i == R.id.top_back_new) {
            finish();
        } else if (i == R.id.top_btn_new) {
            if (mSceneManageList.size() > 0) {
                if (isManageMode) {
                    mTopBtnNew.setText("管理");
                    isManageMode = false;
                    mSceneAddTvLinear.setVisibility(View.VISIBLE);
                } else {
                    isManageMode = true;
                    mTopBtnNew.setText("完成");
                    mSceneAddTvLinear.setVisibility(View.GONE);
                }
                mSceneManageAdapter.setManageMode(isManageMode);
                mSceneManageAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSceneList(familyId);
        if (!isFinishing()){
            //loadingDialog.show();
        }
    }


    private void getSceneList(String familyId) {
//        NetWorkWrapper.smartGetSceneList(familyId,new HttpHandler<SceneListVo>(){
//
//            @Override
//            public void onSuccess(ServerTip serverTip, SceneListVo sceneListVo) {
//                loadingDialog.dismiss();
//                mDefaultSceneList.clear();
//                mSceneManageList.clear();
//                List<SmartInfoVo.FamilysBean.ScenesBean> tempList = sceneListVo.getScenes();
//                for(SmartInfoVo.FamilysBean.ScenesBean scenesBean:tempList){
//                    if(scenesBean.getSceneType().equalsIgnoreCase("create")){
//                        mSceneManageList.add(scenesBean);
//                    } else {
//                        mDefaultSceneList.add(scenesBean);
//                    }
//                }
//                mSceneManageAdapter.setNewData(mSceneManageList);
//                mDefaultSceneAdapter.setNewData(mDefaultSceneList);
//                if (mSceneManageList != null && mSceneManageList.size() > 0) {
//                    mMySceneTv.setVisibility(View.VISIBLE);
//                    mSceneManageLinear.setVisibility(View.VISIBLE);
//                    mTopBtnNew.setVisibility(View.VISIBLE);
//                }
//            }
//
//            @Override
//            public void onFailure(ServerTip serverTip) {
//                super.onFailure(serverTip);
//                loadingDialog.dismiss();
//                ToastUtil.show("请求失败");
//            }
//        });

        OkGo.<String>get(Constant.HOST + Constant.Family_getSceneList)
                .params("familyId", familyId)//
                .converter(new StringConvert())//
                .adapt(new ObservableResponse<String>())//
                .subscribeOn(Schedulers.io())//
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull Disposable disposable) throws Exception {
                        loadingDialog.show();
                        LogUtils.e("accept", disposable.isDisposed() + "===");
                    }
                })//
                .map(new Function<Response<String>, SceneListVo>() {
                    @Override
                    public SceneListVo apply(Response<String> stringResponse) throws Exception {
                        LogUtils.e("apply", "====" + stringResponse.body());
//                        JSON
                        CommonResp resp = JSON.parseObject(stringResponse.body(), CommonResp.class);
                        SceneListVo obj = JSON.parseObject(resp.getResult(), SceneListVo.class);

                        return obj;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())//
                .subscribe(new Observer<SceneListVo>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
//                        addDisposable(d);
                        LogUtils.e("onSubscribe", "========---====");
                    }

                    @Override
                    public void onNext(SceneListVo sceneListVo) {
                        mDefaultSceneList.clear();
                        mSceneManageList.clear();
                        List<SmartInfoVo.FamilysBean.ScenesBean> tempList = sceneListVo.getScenes();
                        SPUtils.put(SceneManageActivity.this, "sceneList" + userId, JSON.toJSONString(tempList));
                        for (SmartInfoVo.FamilysBean.ScenesBean scenesBean : tempList) {
                            if (scenesBean.getSceneType().equalsIgnoreCase("create")) {
                                mSceneManageList.add(scenesBean);
                            } else {
                                mDefaultSceneList.add(scenesBean);
                            }
                        }
                        mSceneManageAdapter.setNewData(mSceneManageList);
                        mDefaultSceneAdapter.setNewData(mDefaultSceneList);
                        if (mSceneManageList != null && mSceneManageList.size() > 0) {
                            mMySceneTv.setVisibility(View.VISIBLE);
                            mSceneManageLinear.setVisibility(View.VISIBLE);
                            mTopBtnNew.setVisibility(View.VISIBLE);
                        }
                        loadingDialog.dismiss();
                        LogUtils.e("onNext:", "------------------");//smartInfoVo.getUserid()+"---"+smartInfoVo.getFamilys().size()
                    }


                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        e.printStackTrace();
                        loadingDialog.dismiss();
                        ToastUtil.show(SceneManageActivity.this, "请求失败");
                    }

                    @Override
                    public void onComplete() {
                        loadingDialog.dismiss();
                        LogUtils.e("onComplete", "============");
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (flay) {
            EventBus.getDefault().post(new UpdateFamilyEvent(true));
        }
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        int i = view.getId();
        if (i == R.id.item_scene_delete_iv) {// 删除
            // 删除
            delSenceId = mSceneManageList.get(position).getSceneId();
            delPos = position;
            mBaseDialog.show();
        } else if (i == R.id.item_scene_switch_tv) {// 开启场景
            if (!isManageMode) {
                if (adapter.equals(mSceneManageAdapter)) {
                    if (mSceneManageList.size() > 0) {
                        if (!isFinishing()){
                            //loadingDialog.show();
                        }
                        executeScene(mSceneManageList.get(position));
                    }
                } else {
                    if (mDefaultSceneList.size() > 0) {
                        if (!isFinishing()){
                            //loadingDialog.show();
                        }
                        executeScene(mDefaultSceneList.get(position));
                    }
                }

            }
        } else if (i == R.id.item_scene_layout) {
            if (!isManageMode) {
                Intent intent = new Intent(SceneManageActivity.this, SceneUpdateActivity.class);
                if (adapter.equals(mSceneManageAdapter)) {
                    intent.putExtra("sceneId", mSceneManageList.get(position).getSceneId());
                    intent.putExtra("defaultScene", false);
                } else {
                    intent.putExtra("sceneId", mDefaultSceneList.get(position).getSceneId());
                    intent.putExtra("defaultScene", true);
                }
                startActivity(intent);
            }
        }
//        return false;
    }


    //删除场景(新接口)
    private void deleteScene(String familyId, String sceneId, int position) {
//        NetWorkWrapper.smartDeleteScene(familyId, sceneId, new HttpHandler<String>() {
//            @Override
//            public void onSuccess(ServerTip serverTip, String s) {
//                loadingDialog.dismiss();
//                ToastUtil.showCenterToast("删除成功");
//                mSceneManageList.remove(position);
//                if (mSceneManageList.size() <= 0) {
//                    mMySceneTv.setVisibility(View.GONE);
//                }
//                mSceneManageAdapter.notifyDataSetChanged();
//                EventBus.getDefault().post(new UpdateFamilyEvent(true));
//            }
//
//            @Override
//            public void onFailure(ServerTip serverTip) {
//                super.onFailure(serverTip);
//                loadingDialog.dismiss();
//                ToastUtil.show("请求失败");
//            }
//        });

        OkGo.<String>get(Constant.HOST + Constant.Scene_delete)
                .params("familyId", familyId)
                .params("sceneId", sceneId)
                .converter(new StringConvert())
                .adapt(new ObservableResponse<String>())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull Disposable disposable) throws Exception {
//                        showLoading();
                        LogUtils.e("accept", disposable.isDisposed() + "===");
                    }
                })
                .map(new Function<Response<String>, CommonResp>() {
                    @Override
                    public CommonResp apply(Response<String> stringResponse) throws Exception {
                        LogUtils.e("apply", "====" + stringResponse.body());
                        CommonResp resp = JSON.parseObject(stringResponse.body(), CommonResp.class);
                        LogUtils.e("resp", resp.getState() + "=====");
                        return resp;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())//
                .subscribe(new Observer<CommonResp>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
//                        addDisposable(d);
                        LogUtils.e("onSubscribe", "========---====");
                    }

                    @Override
                    public void onNext(CommonResp resp) {
                        Log.e("TAG", "onNext: "+resp +"--"+familyId+"--"+sceneId);
                        if (resp.getErrorCode().equalsIgnoreCase("200")) {
                            ToastUtil.show(SceneManageActivity.this, "删除成功");
                            mSceneManageList.remove(position);
                            if (mSceneManageList.size() <= 0) {
                                mMySceneTv.setVisibility(View.GONE);
                            }
                            mSceneManageAdapter.notifyDataSetChanged();
                            EventBus.getDefault().post(new UpdateFamilyEvent(true));
                        } else {
                            ToastUtil.show(SceneManageActivity.this, "删除失败");
                        }

                    }


                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        e.printStackTrace();
                        ToastUtil.show(SceneManageActivity.this, "请求失败");
                    }

                    @Override
                    public void onComplete() {
                        //loadingDialog.dismiss();
                        LogUtils.e("onComplete", "============");
                    }
                });
    }

    private void executeScene(SmartInfoVo.FamilysBean.ScenesBean bean) {
//        NetWorkWrapper.smartExecuteScene(bean.getFamilyId(),
//                bean.getSenceId(),
//                bean.getTaskTpye(),
//                new HttpHandler<String>() {
//                    @Override
//                    public void onSuccess(ServerTip serverTip, String s) {
//                        ToastUtil.show("执行场景成功");
//                        EventBus.getDefault().post(new UpdateFamilyEvent(true));
//                        getSceneList(familyId);
//                    }
//
//                    @Override
//                    public void onFailure(ServerTip serverTip) {
//                        super.onFailure(serverTip);
//                        ToastUtil.show("请求失败");
//                    }
//                });
        OkGo.<String>get(Constant.HOST + Constant.smartExecuteScene)
                .params("familyId", bean.getFamilyId())
                .params("sceneId", bean.getSceneId())
                .params("taskType", bean.getTaskTpye())
                .converter(new StringConvert())
                .adapt(new ObservableResponse<String>())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull Disposable disposable) throws Exception {
//                        showLoading();
                        LogUtils.e("accept", disposable.isDisposed() + "===");
                    }
                })
                .map(new Function<Response<String>, CommonResp>() {
                    @Override
                    public CommonResp apply(Response<String> stringResponse) throws Exception {
                        LogUtils.e("apply", "====" + stringResponse.body());
                        CommonResp resp = JSON.parseObject(stringResponse.body(), CommonResp.class);
                        LogUtils.e("resp", resp.getState() + "=====");
                        return resp;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())//
                .subscribe(new Observer<CommonResp>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
//                        addDisposable(d);
                        LogUtils.e("onSubscribe", "========---====");
                    }

                    @Override
                    public void onNext(CommonResp resp) {
                        if (resp.getErrorCode().equalsIgnoreCase("200")) {
                            ToastUtil.show(SceneManageActivity.this, "执行场景成功");
                            getSceneList(familyId);
                            if (!bean.isIseffective()){
                                flay=true;
                            }
                        } else {
                            ToastUtil.show(SceneManageActivity.this, "加载失败");
                        }

                    }


                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        e.printStackTrace();
                        ToastUtil.show(SceneManageActivity.this, "加载失败");
                    }

                    @Override
                    public void onComplete() {
                        //loadingDialog.dismiss();
                        LogUtils.e("onComplete", "============");
                    }
                });
    }


    @Override
    public void onConfirmClick() {
        mBaseDialog.dismiss();
        deleteScene(familyId, delSenceId, delPos);
        //loadingDialog.show();

    }

    @Override
    public void onCancelListener() {
        if (mBaseDialog.isShowing()) {
            mBaseDialog.dismiss();
        }
    }
}
