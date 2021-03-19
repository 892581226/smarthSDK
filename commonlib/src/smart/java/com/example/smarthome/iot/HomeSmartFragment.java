package com.example.smarthome.iot;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.eques.icvss.api.ICVSSListener;
import com.eques.icvss.api.ICVSSUserInstance;
import com.eques.icvss.core.module.user.BuddyType;
import com.eques.icvss.utils.Method;
import com.example.smarthome.iot.entry.HomeSmartDefenseBean;
import com.example.smarthome.iot.entry.SmartInfoList;
import com.example.smarthome.iot.impl.device.cateye.activity.InComingCallsActivity;
import com.example.smarthome.iot.impl.device.cateye.utils.Constants;
import com.example.smarthome.iot.impl.device.cateye.utils.ICVSSUserModule;
import com.example.smarthome.iot.impl.device.gateway.activity.AppendGatewayActivity;
import com.example.smarthome.iot.impl.device.gateway.fragment.RoomDevicePagerFragment_gateway;
import com.xhwl.commonlib.base.BaseFrament;
import com.example.smarthome.R;
import com.example.smarthome.iot.adapter.DeviceListAdapter;
import com.example.smarthome.iot.adapter.DynamicFragmentPagerAdapter;
import com.example.smarthome.iot.adapter.SceneControlAdapter;
import com.example.smarthome.iot.entry.CommonResp;
import com.example.smarthome.iot.entry.SmartInfoVo;
import com.example.smarthome.iot.entry.eventbus.UpdateFamilyEvent;
import com.example.smarthome.iot.impl.DeviceAppendActivity;
import com.example.smarthome.iot.net.Constant;
import com.example.smarthome.iot.util.DropPopMenuIot;
import com.example.smarthome.iot.util.dynamicpagerindicator.CustomPagerIndicator;
//import com.example.smarthome.iot.yscamera.YsAddCameraActivity;
//import com.example.smarthome.iot.yscamera.YsCameraListActivity;
//import com.example.smarthome.iot.yscamera.YsCaptureActivity;
import com.kcrason.dynamicpagerindicatorlibrary.DynamicPagerIndicator;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.convert.StringConvert;
import com.lzy.okgo.model.Response;
import com.lzy.okrx2.adapter.ObservableResponse;
//import com.videogo.exception.BaseException;
//import com.videogo.openapi.EZOpenSDK;
//import com.videogo.openapi.bean.EZDeviceInfo;
import com.xhwl.commonlib.constant.SpConstant;
import com.xhwl.commonlib.uiutils.LogUtils;
import com.xhwl.commonlib.uiutils.SPUtils;
import com.xhwl.commonlib.uiutils.StringUtils;
import com.xhwl.commonlib.uiutils.ToastUtil;
import com.xhwl.commonlib.uiutils.viewpager.NoScrollViewPager;
import com.zyao89.view.zloading.ZLoadingDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.xhwl.commonlib.constant.SpConstant.SP_USER_MASTER_TELEPHONE;
import static com.zyao89.view.zloading.Z_TYPE.ROTATE_CIRCLE;

/**
 * author: glq
 * date: 2019/4/10 17:04
 * description: 智能家居
 * update: 2019/4/10
 * version: V1.4.1
 */

public class HomeSmartFragment extends BaseFrament implements View.OnClickListener, BaseQuickAdapter.OnItemChildClickListener, DynamicPagerIndicator.OnItemTabClickListener, ICVSSListener {

    private View view;
    /**
     * 我的小家
     */
    private TextView mHomeSmartFamilyName;
    private ImageView mHomeSmartAddMore;
    private LinearLayout mHomeSmartAddMoreLinear;
    private RecyclerView mHomeSmartSceneRv;
    /**
     * 我的设备
     */
    private TextView mHomeSmartRoomTabMyDevice;
    private View mHomeSmartRoomTabMyDeviceIcon;
    //    private DynamicPagerIndicator mHomeSmartRoomTabLayout;
    private CustomPagerIndicator mHomeSmartRoomTabLayout;
    private ImageView mHomeSmartRoomMore;
    private RecyclerView mHomeSmartDeviceRv;
    private List<String> mStrings = new ArrayList<>();
    private DropPopMenuIot familyDropPopMenu;
    private DropPopMenuIot dropPopMenu;
    private SceneControlAdapter mSceneControlAdapter;
    private DeviceListAdapter mDeviceManageAdapter;
    private Intent mIntent = new Intent();
    private ImageView mIotSmartHomeBanner;
    private ImageView mIotSmartHomeAddDevice;
    private RelativeLayout mIotSmartHomeBannerDefault;
    private NoScrollViewPager mHomeSmartDeviceViewPager;
    private String token;

    private SmartInfoList mSmartInfoVo = new SmartInfoList();
    private SmartInfoVo.FamilysBean mFamilysBeanList = new SmartInfoVo.FamilysBean();// 家庭列表
    private List<SmartInfoList.FamilysBean> mFamilysBean = new ArrayList<>();// 家庭列表
    private List<SmartInfoVo.FamilysBean.RoomsBean> mRoomsBeanList = new ArrayList<>(); // 房间列表
    private List<SmartInfoVo.FamilysBean.DeviceInfoBean> mDeviceInfoBeanList = new ArrayList<>(); // 全部设备列表
    private List<SmartInfoVo.FamilysBean.DeviceInfoBean> mRoomDeviceInfoBeanList = new ArrayList<>(); // 房间设备列表

    private List<SmartInfoVo.FamilysBean.GatewaysBean> mGatewaysBeanList = new ArrayList<>(); // 网关列表

//    private List<SceneManageItemVo.Item> mSceneManageItemVos = new ArrayList<>(); // 场景列表

    private DynamicFragmentPagerAdapter mFragmentPagerAdapter;
    private boolean isUpdate=false, updateRoom;
    private int currentFamilyPosition = -1;
    private int sceneState;
    //    private ViewPager mViewPager;
    private String[] mTitles;

    private List<SmartInfoVo.FamilysBean.ScenesBean> mScenesBeanList = new ArrayList<>();//场景列表
    private String telephone;
    private ConstraintLayout home_smart_family_top;
    private SmartInfoVo.FamilysBean currentFamilyBean;
    private ConstraintLayout main_con_layout;
    private boolean defense;
    private TextView tv_defense;
    private String familyId;
    private int state;//0:未绑定设备 1:已绑定设备
    private boolean mProperty;
    private int INITTYPE=0;
    private EditText mFamilyEt;
    private Button mFamilyBt;
    private EditText mCreateFamilyEt;
    private Button mCreateFamilyBt;
    private View inflate1;
    private PopupWindow mPopupWindow1;
    private String mFamilyId1;
    private ZLoadingDialog loadingDialog;
    private ImageView mSmartBack;
    private int stateHk;
    private int jurisdiction;

    public static HomeSmartFragment newInstance(String userId) {
        Bundle bundle = new Bundle();
        bundle.putString("phone", userId);
        HomeSmartFragment smartFragment = new HomeSmartFragment();
        smartFragment.setArguments(bundle);
        return smartFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        assert getArguments() != null;
        telephone = getArguments().getString("phone");
        loadingDialog = new ZLoadingDialog(Objects.requireNonNull(getActivity()));
        loadingDialog.setLoadingBuilder(ROTATE_CIRCLE)//设置类型
                .setLoadingColor(getResources().getColor(R.color.yellow))//颜色
                .setHintText("Loading...")
                .setHintTextSize(16) // 设置字体大小 dp
                .setHintTextColor(Color.GRAY)  // 设置字体颜色
                .setDialogBackgroundColor(getResources().getColor(R.color.transparent))
                .setDurationTime(0.5) // 设置动画时间百分比 - 0.5倍
                .setDialogBackgroundColor(Color.TRANSPARENT)
                .setCanceledOnTouchOutside(false);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home_smart, container, false);
        inflate1 = LayoutInflater.from(getContext()).inflate(R.layout.createfamily,null,false);
        familyDropPopMenu = new DropPopMenuIot(getActivity(), true);
        mPopupWindow1 = new PopupWindow(inflate1, 800, 500);
        mPopupWindow1.setOutsideTouchable(true);
        mPopupWindow1.setFocusable(true);
        mPopupWindow1.setTouchable(true);

        initView(view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView(view);
        initData();

        //setCatEye(mFamilyId1,"sdk_demo");
    }

    public static ICVSSUserInstance icvss;
    public static final String DEF_KEYID = "5d91e3b2b7fbb31c";

    private void setCatEye(String userName,String appKey){
        icvss = ICVSSUserModule.getInstance(this).getIcvss();
        icvss.equesLogin(getActivity(), Constants.DISTRIBUTE_URL, userName, appKey);
    }

    private void setViewPagerContent(final ViewPager viewPager, final DynamicPagerIndicator dynamicPagerIndicator,
                                     final int index, String[] title, boolean isUpdate) {
//        mViewPager = viewPager;
        LogUtils.e("setViewPagerContent", isUpdate + "---");
        if (!isUpdate && getActivity() != null) {
            LogUtils.e("setViewPagerContent", "---------------");
            viewPager.removeView(viewPager);
            mFragmentPagerAdapter = new DynamicFragmentPagerAdapter(getActivity().getSupportFragmentManager(), createFragments(index), title);
            viewPager.setAdapter(mFragmentPagerAdapter);
            dynamicPagerIndicator.setViewPager(viewPager);
        } else {
            LogUtils.e("setViewPagerContent", "+++++++++++++");
            mFragmentPagerAdapter.update(createFragments(index), title);
            mHomeSmartRoomTabLayout.updateIndicator();
            viewPager.setAdapter(mFragmentPagerAdapter);
            viewPager.setCurrentItem(0);
        }
    }


    private List<Fragment> createFragments(int index) {
        List<Fragment> fragments = new ArrayList<>();
        fragments.clear();
        for (int i = 0; i < index; i++) {

            if (i==1){
                //创建网关fragment界面
                Log.e("createFragments 网关",mRoomsBeanList.get(i).getDeviceInfo().size()+"？？？？");
                RoomDevicePagerFragment_gateway fragment = new RoomDevicePagerFragment_gateway();
                Bundle bundle = new Bundle();
                bundle.putInt("key_of_index", i);
                bundle.putParcelableArrayList("deviceList", (ArrayList<? extends Parcelable>) mRoomsBeanList.get(i).getDeviceInfo());
                bundle.putParcelableArrayList("roomList", (ArrayList<? extends Parcelable>) mRoomsBeanList);
                fragment.setArguments(bundle);
                fragments.add(fragment);
                continue;
            }

            LogUtils.e("createFragments", mRoomsBeanList.get(i).getDeviceInfo().size()+"!!!!!!!!");
//            fragments.add(RoomDevicePagerFragment.create(i, mRoomsBeanList.get(i).getDeviceInfo(), mRoomsBeanList));
            RoomDevicePagerFragment fragment = new RoomDevicePagerFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("key_of_index", i);
            bundle.putParcelableArrayList("deviceList", (ArrayList<? extends Parcelable>) mRoomsBeanList.get(i).getDeviceInfo());
            bundle.putParcelableArrayList("roomList", (ArrayList<? extends Parcelable>) mRoomsBeanList);
            fragment.setArguments(bundle);
            fragments.add(fragment);
        }
        return fragments;
    }


    private void initData() {

        LinearLayoutManager ms = new LinearLayoutManager(getActivity());
        ms.setOrientation(LinearLayoutManager.HORIZONTAL);// 设置 recyclerview 布局方式为横向布局
        mHomeSmartSceneRv.setLayoutManager(ms);
        mHomeSmartDeviceRv.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        mDeviceManageAdapter = new DeviceListAdapter(mDeviceInfoBeanList);
        mHomeSmartDeviceRv.setAdapter(mDeviceManageAdapter);
        mDeviceManageAdapter.setOnItemChildClickListener(this);
        mDeviceManageAdapter.bindToRecyclerView(mHomeSmartDeviceRv);


//        telephone = SPUtils.get(getActivity(), SpConstant.SP_USER_TELEPHONE, "");
//        telephone = "13602590172";
        LogUtils.e("initData", telephone + "------");
        SPUtils.put(getActivity(), SpConstant.SP_USER_TELEPHONE, telephone);
        //暂时注释萤石摄像头
//        ezvizGetToken(telephone);
        smartInitFamily(telephone,0);


//        getYsToken();//ra.23hvjheo5pxdtr4j73nlrmw41kb14oyc-6m1w8fpdb6-0e14h87-o7wfcxzte

    }

    private void smartInitFamily(String telephone,int changedId) {
        if (changedId!=2)
            loadingDialog.show();
        OkGo.<String>get(Constant.HOST + Constant.INIT_THOME_LIST)
//                .headers("aaa", "111")//
                .params("userId", telephone)//
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
                .map(new Function<Response<String>, SmartInfoList>() {

                    @Override
                    public SmartInfoList apply(Response<String> stringResponse) throws Exception {
                        LogUtils.e("apply", "====" + stringResponse.body());
//                        JSON
                        CommonResp resp = JSON.parseObject(stringResponse.body(), CommonResp.class);
                        SmartInfoList obj = JSON.parseObject(resp.getResult(), SmartInfoList.class);
                        if (obj != null) {
                        }
                        return obj;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<SmartInfoList>() {
                    @Override
                    public void onSubscribe(Disposable d) {


                    }

                    @Override
                    public void onNext(SmartInfoList smartInfoVo) {
                        Log.e("smartInit212","onNext====="+smartInfoVo.toString());
                        mSmartInfoVo=smartInfoVo;
                        mFamilysBean = smartInfoVo.getFamilys();
                        familyDropPopMenu.setMenuList(mFamilysBean);
                        if (smartInfoVo.getUser()==null){
                            mProperty=false;
                        }else {
                            mProperty = smartInfoVo.getUser().isProperty();
                        }
                        if (changedId==1){
                            changedSmart();
                        }else if (changedId==2){ //创建家庭，分享家庭刷新
                            loadingDialog.dismiss();
                        }else if (changedId==3){
                            startSmartInit(2); //删除家庭刷新显示第一个家庭
                        }else {
                            startSmartInit(1);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        loadingDialog.dismiss();
                        Log.e("smartInit2","onNext====="+e.toString());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void startSmartInit(int type) {
        String mfamilyId="";
        String familyId = SPUtils.get(Objects.requireNonNull(getActivity()), "FamilyPhone"+telephone,"");
        if(type==1){
            for (int i = 0; i < mFamilysBean.size(); i++) {
                if (!familyId.isEmpty()&&mFamilysBean.get(i).getFamilyId().equals(familyId)){
                    mfamilyId = familyId;
                    break;
                } else if(mFamilysBean.size()>0){
                    mfamilyId = mFamilysBean.get(INITTYPE).getFamilyId();
                }
            }
        }else if(mFamilysBean.size()>0){
            mfamilyId = mFamilysBean.get(INITTYPE).getFamilyId();
        }
        // http://139.9.74.92:8080/user/dev1.0/families/1240180511792566272?userId=15909682670
        OkGo.<String>get(Constant.HOST + Constant.INIT_HOME_INIT+mfamilyId)
//                .headers("aaa", "111")//
                .params("userId",telephone)
                .converter(new StringConvert())//
                .adapt(new ObservableResponse<String>())//
                .subscribeOn(Schedulers.io())//
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull Disposable disposable) throws Exception {
                        LogUtils.e("accept", disposable.isDisposed() + "===");
                    }
                })//
                .map(new Function<Response<String>, SmartInfoVo>() {
                    @Override
                    public SmartInfoVo apply(Response<String> stringResponse) throws Exception {
                        LogUtils.e("apply", "====" + stringResponse.body());
//                        JSON
                        CommonResp resp = JSON.parseObject(stringResponse.body(), CommonResp.class);
                        SmartInfoVo obj = JSON.parseObject(resp.getResult(), SmartInfoVo.class);
                        LogUtils.e("apply", "====" +resp.toString() +"----"+obj.toString());
                        if (obj != null) {
                        }
                        return obj;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())//
                .subscribe(new Observer<SmartInfoVo>() {



                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
//                        addDisposable(d);
                        LogUtils.e("onSubscribe","   =---====");
                    }

                    @Override
                    public void onNext(SmartInfoVo smartInfoVo) {
                        loadingDialog.dismiss();
                        mFamilysBeanList = smartInfoVo.getFamily();
                        mRoomsBeanList.clear();
                        mGatewaysBeanList.clear();
                        mScenesBeanList.clear();
                        mDeviceInfoBeanList.clear();

                        if (mFamilysBeanList!=null) {
                            SmartInfoVo.FamilysBean mainFamily = null;
                            // if(mFamilysBeanList.getJurisdiction() == 1){
                            mainFamily = mFamilysBeanList;
                            defense = mFamilysBeanList.isDefense();
                            HomeSmartFragment.this.familyId=mFamilysBeanList.getFamilyId();
                            jurisdiction = mainFamily.getJurisdiction();
                            if (mFamilysBeanList.getProject()!=null&&mFamilysBeanList.getProject().getProjectCode()!=null){
                                tv_defense.setVisibility(View.VISIBLE);
                                if (defense){
                                    tv_defense.setText("一键撤防");
                                    tv_defense.setBackgroundResource(R.drawable.ff7363_4_corner);
                                }else {
                                    tv_defense.setText("一键布防");
                                    tv_defense.setBackgroundResource(R.drawable.corner_4_42d084);
                                }
                            }else {
                                tv_defense.setVisibility(View.GONE);
                            }
                            LogUtils.e("init currentFamilyPosition",+currentFamilyPosition+"---------------");
                           /* }else {
                                mainFamily = mFamilysBeanList;
                            }*/
                            if (mainFamily.getOtherDevices()!=null){
                                List<SmartInfoVo.FamilysBean.OtherDevices> otherDevices = mainFamily.getOtherDevices();
                                for (int i = 0; i <otherDevices.size() ; i++) {
                                    if (otherDevices.get(i).getSupplier().contains("海康电子猫眼")){
                                        stateHk =1;
                                        SPUtils.put(Objects.requireNonNull(getActivity()), "DEVICE_SERIAL", otherDevices.get(i).getDev_id());
                                        SPUtils.put(getActivity(), "DEVICE_VERIFY_CODE", otherDevices.get(i).getValidateCode());
                                        break;
                                    }else {
                                        stateHk =0;
                                    }
                                }
                            }else {
                                stateHk =0;
                            }
                            mRoomsBeanList.addAll(mainFamily.getRooms());
                            mGatewaysBeanList.addAll(mainFamily.getGateways());
                            mFamilyId1 = mainFamily.getFamilyId();
                            SPUtils.put(getActivity(), "FamilyId" , mFamilyId1);
                            setCatEye(mFamilyId1,"sdk_demo");
                            SPUtils.put(getActivity(), "GatewaysBeanList" + telephone, JSON.toJSONString(mGatewaysBeanList));
                            SPUtils.put(getActivity(), SP_USER_MASTER_TELEPHONE,mainFamily.getMasterUserid());
                            List<SmartInfoVo.FamilysBean.ScenesBean> tempList = mainFamily.getScenes();
                            SPUtils.put(getActivity(), "scenesNum" + telephone, tempList.size());
                            LogUtils.e("init scenesNum", tempList.size() + "--------");
                            SPUtils.remove(getActivity(), "scenes" + telephone);
                            SPUtils.put(getActivity(), "scenes" + telephone, JSON.toJSONString(tempList));
                            mScenesBeanList.addAll(tempList);
                            SmartInfoVo.FamilysBean.ScenesBean moreScene = new SmartInfoVo.FamilysBean.ScenesBean();
                            moreScene.setSceneName("更多");
                            moreScene.setIseffective(false);
                            mScenesBeanList.add(moreScene);


                            SPUtils.remove(getActivity(), "rooms" + telephone);
                            SPUtils.put(getActivity(), "rooms" + telephone, JSON.toJSONString(mRoomsBeanList));
                            SmartInfoVo.FamilysBean.RoomsBean roomsBean = new SmartInfoVo.FamilysBean.RoomsBean();
                            //萤石注释
                            List<SmartInfoVo.FamilysBean.DeviceInfoBean> myDevList = new ArrayList<>();
                            for (int i = 0; i < mGatewaysBeanList.size(); i++) {
                                myDevList.addAll( mGatewaysBeanList.get(i).getDeviceInfo());
                            }
                            SPUtils.remove(getActivity(), "gateWayDevs" + telephone);
                            SPUtils.put(getActivity(), "gateWayDevs" + telephone, JSON.toJSONString(myDevList));
                            //显示网关   暂不显示
                            List<SmartInfoVo.FamilysBean.DeviceInfoBean> gateWayInfos=new ArrayList<>();
                            for (int i = 0; i < mGatewaysBeanList.size(); i++) {
                                SmartInfoVo.FamilysBean.DeviceInfoBean gateWayDevice = new SmartInfoVo.FamilysBean.DeviceInfoBean();
                                gateWayDevice.setId(mGatewaysBeanList.get(i).getGatewayId());
                                gateWayDevice.setDeviceName(mGatewaysBeanList.get(i).getGatewayName());
//                                gateWayDevice.setDeviceType(DeviceType.HILINK_GATEWAY);
                                gateWayDevice.setDeviceType(mGatewaysBeanList.get(i).getDeviceType());
                                gateWayDevice.setSupplierId("网关");
                                //---------
                                Log.e("首页网关测试",mGatewaysBeanList.get(i).getGatewayName());
                                myDevList.add(gateWayDevice);
                                gateWayInfos.add(gateWayDevice);
                            }

                            for (int j = 0; j < mGatewaysBeanList.size(); j++){
                                SmartInfoVo.FamilysBean.DeviceInfoBean gateWayDevice = new SmartInfoVo.FamilysBean.DeviceInfoBean();
                                for (int k=0;k<mGatewaysBeanList.get(j).getDeviceInfo().size();k++){
                                    gateWayDevice.setId(mGatewaysBeanList.get(j).getDeviceInfo().get(k).getId());
                                    gateWayDevice.setDeviceName(mGatewaysBeanList.get(j).getDeviceInfo().get(k).getDeviceName());
                                    gateWayDevice.setDeviceType(DeviceType.HILINK_GATEWAY);
                                    Log.e("首页网关测试2",mGatewaysBeanList.get(j).getDeviceInfo().get(k).getDeviceName());
//                                    gateWayInfos.add(gateWayDevice);
                                }
                            }

                            SPUtils.put(getActivity(),"getJurisdiction"+telephone,mainFamily.getJurisdiction());

                            //暂时注释萤石摄像头
//                            if(mainFamily.getJurisdiction() == 1 && cameraDevice!=null){
//                                myDevList.add(cameraDevice);
//                            }
                            roomsBean.setRoomName("我的设备");
                            roomsBean.setDeviceInfo(myDevList);
                            mRoomsBeanList.add(0, roomsBean);

                            SmartInfoVo.FamilysBean.RoomsBean roomsBean1 = new SmartInfoVo.FamilysBean.RoomsBean();
                            roomsBean1.setRoomName("网关");
                            roomsBean1.setDeviceInfo(gateWayInfos);
                            mRoomsBeanList.add(1,roomsBean1);
                            LogUtils.e("smartInit", "smartInfoVo: " + mRoomsBeanList.size()+" "+mRoomsBeanList.toString());

                            //设置家庭
                            mHomeSmartFamilyName.setText(mainFamily.getFamilyName());
                            SPUtils.remove(getActivity(), "familyId" + telephone);
                            SPUtils.put(getActivity(), "familyId" + telephone, mainFamily.getFamilyId());
                            if (mainFamily.getGateways() != null && mainFamily.getGateways().size() > 0
                                    && !StringUtils.isEmpty(mainFamily.getGateways().get(0).getGatewayId())) {
                                SPUtils.remove(getActivity(), "gatewayId" + telephone+mainFamily.getFamilyId());
                                SPUtils.put(getActivity(), "gatewayId" + telephone+mainFamily.getFamilyId(), mainFamily.getGateways().get(0).getGatewayId());
                                String gate1=SPUtils.get(getActivity(),"gatewayId" + telephone+mainFamily.getFamilyId(),"");
                                Log.e("首页网关id查看",gate1);
                            }
                            // 设置房间
                            mTitles = new String[mRoomsBeanList.size()];
                            for (int i = 0; i < mRoomsBeanList.size(); i++) {
                                mTitles[i] = mRoomsBeanList.get(i).getRoomName();
                            }
//                          mTitles[mRoomsBeanList.size()]="网关";
                            setViewPagerContent(mHomeSmartDeviceViewPager, mHomeSmartRoomTabLayout, mRoomsBeanList.size(), mTitles, isUpdate);

//                            if(mainFamily.getJurisdiction() ==1){
//                                ezvizGetToken(userId);
//                            }
                            if (mFamilysBeanList!=null){
                                mSceneControlAdapter.notifyDataSetChanged();
                            }

                        }

                        LogUtils.e("onNext11:", "------------------");//smartInfoVo.getUserid()+"---"+smartInfoVo.getFamilys().size()
                    }


                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        e.printStackTrace();
                        loadingDialog.dismiss();
                        Log.e("HomeSmartFragment",e.getMessage());
                        Toast.makeText(getActivity(),"请求失败",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onComplete() {
                        LogUtils.e("onComplete","   =====");
                    }
                });

    }

    private void initView(View view) {
        if (getActivity() != null) {
            token = SPUtils.get(getActivity(), SpConstant.SP_USERTOKEN, "");
//            mViewPager = new ViewPager(getActivity());
        }
        mSmartBack = view.findViewById(R.id.smart_back);
        mSmartBack.setOnClickListener(this);
        mCreateFamilyEt = inflate1.findViewById(R.id.create_family_et);
        mCreateFamilyBt = inflate1.findViewById(R.id.create_family_bt);
        main_con_layout = view.findViewById(R.id.main_con_layout);
        home_smart_family_top = view.findViewById(R.id.home_smart_family_top);
        mHomeSmartFamilyName = (TextView) view.findViewById(R.id.home_smart_family_name);
        mHomeSmartFamilyName.setOnClickListener(this);
        mHomeSmartAddMore = (ImageView) view.findViewById(R.id.home_smart_add_more);
        mHomeSmartAddMore.setOnClickListener(this);
        mHomeSmartSceneRv = (RecyclerView) view.findViewById(R.id.home_smart_scene_rv);
        mHomeSmartRoomTabMyDevice = (TextView) view.findViewById(R.id.home_smart_room_tab_my_device);
        mHomeSmartRoomTabMyDevice.setOnClickListener(this);
        mHomeSmartRoomTabLayout = (CustomPagerIndicator)view.findViewById(R.id.home_smart_room_tab_layout);
        mHomeSmartRoomMore = (ImageView) view.findViewById(R.id.home_smart_room_more);
        mHomeSmartRoomMore.setOnClickListener(this);
        mHomeSmartDeviceRv = (RecyclerView) view.findViewById(R.id.home_smart_device_rv);
        mHomeSmartAddMoreLinear = (LinearLayout) view.findViewById(R.id.home_smart_add_more_linear);
        mIotSmartHomeBanner = (ImageView) view.findViewById(R.id.iot_smart_home_banner);
        mIotSmartHomeBanner.setOnClickListener(this);
        mIotSmartHomeAddDevice = (ImageView) view.findViewById(R.id.iot_smart_home_add_device);
        mIotSmartHomeAddDevice.setOnClickListener(this);
        mIotSmartHomeBannerDefault = (RelativeLayout) view.findViewById(R.id.iot_smart_home_banner_default);
        mHomeSmartDeviceViewPager = (NoScrollViewPager) view.findViewById(R.id.home_smart_device_view_pager);
        mHomeSmartDeviceViewPager.setScanScroll(false);
        mHomeSmartRoomTabMyDeviceIcon = (View) view.findViewById(R.id.home_smart_room_tab_my_device_icon);
        mSceneControlAdapter = new SceneControlAdapter(mScenesBeanList);
        mHomeSmartSceneRv.setAdapter(mSceneControlAdapter);
        mSceneControlAdapter.setOnItemChildClickListener(this);
        mHomeSmartRoomTabLayout.setOnItemTabClickListener(this);

        mCreateFamilyBt.setOnClickListener(this);


        tv_defense = view.findViewById(R.id.tv_defense);
        tv_defense.setOnClickListener(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        LogUtils.e("onDetach","   ====");
        OkGo.getInstance().cancelAll();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.e("onDestroy","------");
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
//        EZOpenSDK.getInstance().logout();
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onClick(View v) {
        if (getActivity() != null) {
            int i = v.getId();
            if (i == R.id.home_smart_family_name) {// 点击切换家庭
                //familyDropPopMenu = new DropPopMenuIot(getActivity(), true);
                smartInitFamily(telephone,2);
                familyDropPopMenu.setTriangleIndicatorViewColor(Color.WHITE);
                familyDropPopMenu.setBackgroundResource(R.drawable.bg_drop_pop_menu_white_shap);
                familyDropPopMenu.setItemTextColor(Color.BLACK);
                familyDropPopMenu.setOnItemClickListener((adapterView, view, position, id, menuItem) -> {
                    familyId = mFamilysBean.get(position).getFamilyId();
                    loadingDialog.show();
                    changedSmart();
                });
                //familyDropPopMenu.setMenuList(mFamilysBean);
                familyDropPopMenu.show(getActivity(), mHomeSmartFamilyName);
            } else if (i == R.id.home_smart_add_more) {// 点击显示更多操作
                mStrings.clear();
                mStrings.add("添加网关");
                mStrings.add("家庭管理");
                mStrings.add("场景管理");
                mStrings.add("分享管理");
            /*    mStrings.add("红外遥控");
                mStrings.add("猫眼/摄像头");*/
                if (mProperty){
                    mStrings.add("创建家庭");
                    mStrings.add("移交家庭");
                }
                dropPopMenu = new DropPopMenuIot(getActivity(), false);
                dropPopMenu.setTriangleIndicatorViewColor(Color.WHITE);
                dropPopMenu.setBackgroundResource(R.drawable.bg_drop_pop_menu_white_shap);
                dropPopMenu.setItemTextColor(Color.BLACK);
                dropPopMenu.setOnItemClickListener((adapterView, view, position, id, menuItem) -> {
                    if (position == 0) {//添加设备
                        mIntent.setClass(getActivity(), AppendGatewayActivity.class);
                        startActivity(mIntent);
                    } else if (position == 1) {//家庭管理
                        mIntent.setClass(getActivity(), FamilyRoomActivity.class);
                        if (mFamilysBeanList != null) {
                            mIntent.putExtra("familyItem", mFamilysBeanList);
                            startActivity(mIntent);
                        } else {
                            return;
                        }
                    } else if (position == 2) {//场景模式
                        mIntent.setClass(getActivity(), SceneManageActivity.class);
//                      mIntent.putExtra("familyItem", mFamilysBeanList.get(currentFamilyPosition));
                        startActivity(mIntent);
                    } else if (position == 3) {//分享使用
                        if (mFamilysBeanList != null && mFamilysBeanList.getJurisdiction()==1) {
                            mIntent.setClass(getActivity(), ShareFamilyActivity.class);
                            startActivity(mIntent);
                        } else {
                            ToastUtil.show(getActivity(),"您不是该家庭的管理员,暂无法分享哦");
                        }
                    }/*else if (position==4){//红外遥控
                        mIntent.setClass(Objects.requireNonNull(getActivity()), DeviceListActivity.class);
                        startActivity(mIntent);
                    }else if (position==5){//添加电子猫眼
                        *//*Intent intent=new Intent();
                        if (state==1){
//                            Toast.makeText(getActivity(),"已绑定电子猫眼",Toast.LENGTH_LONG).show();
                            intent.setClass(getActivity(), AlreadyBindCatEyeActivity.class);
                            startActivity(intent);
                        }else if (state==0){
//                            Toast.makeText(getActivity(),"未绑定电子猫眼",Toast.LENGTH_LONG).show();
                            intent.setClass(getActivity(), NoBindCatEyeActivity.class);
                            startActivity(intent);
                        }*//*
                        Intent mIntent = new Intent();
                        if (stateHk==1){
                            mIntent.setClass(getActivity(), AlreadyBindHYCatEyeActivity.class);
                            startActivity(mIntent);
                        }else if (stateHk==0){
                            if (jurisdiction==1){
                                mIntent.setClass(getActivity(), YsCaptureActivity.class);
                                startActivity(mIntent);
                            }else {
                                ToastUtil.show("请在主家庭添加猫眼");
                            }
                        }
                    }*/else if (position==4){//创建家庭
                        mPopupWindow1.showAtLocation(getView(),Gravity.CENTER,0,0);

                    }else if (position==5){ //移交家庭
                        mIntent.setClass(getActivity(), TransferFamilyActivity.class);
                        mIntent.putParcelableArrayListExtra("family", (ArrayList<? extends Parcelable>) mFamilysBean);
                        startActivity(mIntent);

                    }
                });
                dropPopMenu.setMenu(mStrings);
                dropPopMenu.show(getActivity(), mHomeSmartAddMoreLinear);
            } else if (i == R.id.home_smart_room_tab_my_device) {// 点击切换至我的设备
//                mHomeSmartRoomTabMyDevice.setTextColor(getResources().getColor(R.color.color_3E3E3E));
//                mHomeSmartRoomTabMyDevice.setTextSize(18);
//                mHomeSmartRoomTabMyDeviceIcon.setVisibility(View.VISIBLE);
//                mHomeSmartDeviceRv.setVisibility(View.VISIBLE);
//                mHomeSmartDeviceViewPager.setVisibility(View.GONE);
            } else if (i == R.id.home_smart_room_more) {// 点击跳转至房间管理
                if (mFamilysBeanList != null) {
                    mIntent.setClass(getActivity(), FamilyRoomActivity.class);
                    mIntent.putExtra("familyItem", mFamilysBeanList);
                    mIntent.putParcelableArrayListExtra("roomItem", (ArrayList<? extends Parcelable>) mFamilysBeanList.getRooms());
                    startActivity(mIntent);
                }
            } else if (i == R.id.iot_smart_home_banner) {// 点击banner

            } else if (i == R.id.iot_smart_home_add_device) {// 点击添加设备
                mIntent.setClass(getActivity(), DeviceAppendActivity.class);
                startActivity(mIntent);
            }else if (i==R.id.tv_defense){
                //一键布防撤防
                if (defense){
                    //点击撤防
                    openOrCloseDefense(familyId,false);
                }else {
                    //点击布防
                    openOrCloseDefense(familyId, true);
                }
            }else if (i==R.id.smart_back){
                getActivity().finish();
            }
            else if (i==R.id.create_family_bt){  //创建家庭
                String mCreateFamily = mCreateFamilyEt.getText().toString();
                com.alibaba.fastjson.JSONObject obj = new com.alibaba.fastjson.JSONObject();
                obj.put("targetUserId", mCreateFamily);
                obj.put("userId",  mSmartInfoVo.getUserid());
                OkGo.<String>post(Constant.HOST + Constant.CreateFamily)
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
                                SmartInfoVo obj = JSON.parseObject(resp.getResult(), SmartInfoVo.class);
                                if (obj != null) {
                                    LogUtils.e("apply", "====" + obj.getUserid());
                                }
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
                                String state = commonResp.getState();
                                if (state.equalsIgnoreCase("true")){
                                    if (commonResp.getErrorCode().equals("200")) {
                                        smartInitFamily(telephone,2);
                                        ToastUtil.show("创建成功");
                                        mPopupWindow1.dismiss();
                                    }else {
                                        ToastUtil.show("创建失败");
                                    }
                                }

                            }


                            @Override
                            public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                                e.printStackTrace();
                                Log.e("TAG", "onError: "+e.getMessage().toString() );
                                Toast.makeText(getActivity(),"家庭不存在",Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onComplete() {
                                LogUtils.e("onComplete","   =====");
                            }
                        });
            }
        }
    }

    private void openOrCloseDefense(String familyId,boolean defense1){
        Log.e("openOrCloseDefense","familyId:"+familyId+" defense:"+defense1);
        HomeSmartDefenseBean homeSmartDefenseBean=new HomeSmartDefenseBean();
        homeSmartDefenseBean.setFamilyId(familyId);
        homeSmartDefenseBean.setDefense(defense1);
        OkGo.<String>post(Constant.HOST+Constant.Open_close_Defense)
                .upJson(JSON.toJSONString(homeSmartDefenseBean))
                .converter(new StringConvert())
                .adapt(new ObservableResponse<>())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {

                    }
                })
                .map(new Function<Response<String>, CommonResp>() {
                    @Override
                    public CommonResp apply(Response<String> stringResponse) throws Exception {
                        CommonResp resp=JSON.parseObject(stringResponse.body(),CommonResp.class);
                        return resp;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CommonResp>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(CommonResp commonResp) {
                        Log.e("openOrCloseDefense","onNext"+commonResp.getErrorCode());
                        if (commonResp.getErrorCode().equalsIgnoreCase("200")){
//                            smartInit2(telephone);
                            if (defense1){
                                tv_defense.setText("一键撤防");
                                tv_defense.setBackgroundResource(R.drawable.ff7363_4_corner);
                                defense=true;
                            }else {
                                tv_defense.setText("一键布防");
                                tv_defense.setBackgroundResource(R.drawable.corner_4_42d084);
                                defense=false;
                            }
                        }else {
                            Toast.makeText(getActivity(),commonResp.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("openOrCloseDefense","onError"+e.getMessage()+"====");
                    }

                    @Override
                    public void onComplete() {
                        Log.e("openOrCloseDefense","onComplete====");
                    }
                });

    }

    private void changeFamily(SmartInfoVo.FamilysBean familyBean) {
        if(familyBean!=null){
            mRoomsBeanList.clear();
            mGatewaysBeanList.clear();
            mScenesBeanList.clear();
            mDeviceInfoBeanList.clear();

            if (familyBean!=null) {
                mRoomsBeanList.addAll(familyBean.getRooms());
                mGatewaysBeanList.addAll(familyBean.getGateways());
                SPUtils.put(getActivity(), "GatewaysBeanList" + telephone, JSON.toJSONString(mGatewaysBeanList));
                List<SmartInfoVo.FamilysBean.ScenesBean> tempList = familyBean.getScenes();
                SPUtils.put(getActivity(), "scenesNum" + telephone, tempList.size());
                LogUtils.e("changeFamily scenesNum", tempList.size() + "--------");
                SPUtils.remove(getActivity(), "scenes" + telephone);
                SPUtils.put(getActivity(), "scenes" + telephone, JSON.toJSONString(tempList));
                mScenesBeanList.addAll(tempList);
                SmartInfoVo.FamilysBean.ScenesBean moreScene = new SmartInfoVo.FamilysBean.ScenesBean();
                moreScene.setSceneName("更多");
                moreScene.setIseffective(false);
                mScenesBeanList.add(moreScene);
                jurisdiction = familyBean.getJurisdiction();
                if (familyBean.getOtherDevices()!=null){
                    List<SmartInfoVo.FamilysBean.OtherDevices> otherDevices = familyBean.getOtherDevices();
                    for (int i = 0; i <otherDevices.size() ; i++) {
                        if (otherDevices.get(i).getSupplier().contains("海康电子猫眼")){
                            stateHk =1;
                            SPUtils.put(getActivity(),"DEVICE_SERIAL", otherDevices.get(i).getDev_id());
                            SPUtils.put(getActivity(), "DEVICE_VERIFY_CODE", otherDevices.get(i).getValidateCode());
                            break;
                        }else {
                            stateHk =0;
                        }
                    }
                }else {
                    stateHk =0;
                }
                mFamilyId1 = familyBean.getFamilyId();
                SPUtils.put(getActivity(), "FamilyId" , mFamilyId1);
                SPUtils.put(getActivity(), "FamilyPhone"+telephone , mFamilyId1);
                setCatEye(mFamilyId1,"sdk_demo");
                SPUtils.remove(getActivity(), "rooms" + telephone);
                SPUtils.put(getActivity(), "rooms" + telephone, JSON.toJSONString(mRoomsBeanList));
                SmartInfoVo.FamilysBean.RoomsBean roomsBean = new SmartInfoVo.FamilysBean.RoomsBean();
                List<SmartInfoVo.FamilysBean.DeviceInfoBean> myDevList = new ArrayList<>();

                for (int i = 0; i < mGatewaysBeanList.size(); i++) {
                    myDevList.addAll(mGatewaysBeanList.get(i).getDeviceInfo());
                }
                SPUtils.remove(getActivity(), "gateWayDevs" + telephone);
                SPUtils.put(getActivity(), "gateWayDevs" + telephone, JSON.toJSONString(myDevList));
                SPUtils.put(getActivity(), SP_USER_MASTER_TELEPHONE,familyBean.getMasterUserid());
                //显示网关  暂不显示
                List<SmartInfoVo.FamilysBean.DeviceInfoBean> gateWayInfos=new ArrayList<>();
                for (int i = 0; i < mGatewaysBeanList.size(); i++) {
                    SmartInfoVo.FamilysBean.DeviceInfoBean gateWayDevice = new SmartInfoVo.FamilysBean.DeviceInfoBean();
                    gateWayDevice.setId(mGatewaysBeanList.get(i).getGatewayId());
                    gateWayDevice.setDeviceName(mGatewaysBeanList.get(i).getGatewayName());
                    gateWayDevice.setDeviceType(mGatewaysBeanList.get(i).getDeviceType());
                    gateWayDevice.setSupplierId("网关");
                    //---------
                    myDevList.add(gateWayDevice);
                    gateWayInfos.add(gateWayDevice);
                }
                SPUtils.put(getActivity(),"getJurisdiction"+telephone,familyBean.getJurisdiction());
                //暂时注释萤石摄像头
//                if(familyBean.getJurisdiction() == 1 && cameraDevice!=null){
//                    myDevList.add(cameraDevice);
//                }
                roomsBean.setRoomName("我的设备");
                roomsBean.setDeviceInfo(myDevList);
                mRoomsBeanList.add(0, roomsBean);

                SmartInfoVo.FamilysBean.RoomsBean roomsBean1 = new SmartInfoVo.FamilysBean.RoomsBean();
                roomsBean1.setRoomName("网关");
                roomsBean1.setDeviceInfo(gateWayInfos);
                mRoomsBeanList.add(1,roomsBean1);
//                LogUtils.e("smartInit", "smartInfoVo: " + mRoomsBeanList.size()+mRoomsBeanList.get(1).getDeviceInfo().get(1).getDeviceType());

                LogUtils.e("changeFamily", "changeFamily: " + mRoomsBeanList.size());

                //设置家庭
                mHomeSmartFamilyName.setText(familyBean.getFamilyName());
                SPUtils.remove(getActivity(), "familyId" + telephone);
                SPUtils.put(getActivity(), "familyId" + telephone, familyBean.getFamilyId());
                if (familyBean.getGateways() != null && familyBean.getGateways().size() > 0
                        && !StringUtils.isEmpty(familyBean.getGateways().get(0).getGatewayId())) {
                    SPUtils.remove(getActivity(), "gatewayId" + telephone+familyBean.getFamilyId());
                    SPUtils.put(getActivity(), "gatewayId" + telephone+familyBean.getFamilyId(), familyBean.getGateways().get(0).getGatewayId());
                    String gate1=SPUtils.get(getActivity(),"gatewayId" + telephone+familyBean.getFamilyId(),"");
                    Log.e("首页网关查看",gate1);
                }
                // 设置房间
                mTitles = new String[mRoomsBeanList.size()+1];
                for (int i = 0; i < mRoomsBeanList.size(); i++) {
                    mTitles[i] = mRoomsBeanList.get(i).getRoomName();
                }
                mTitles[mRoomsBeanList.size()]="网关";

//                mHomeSmartDeviceViewPager.removeAllViews();
//                mFragmentPagerAdapter = new DynamicFragmentPagerAdapter(getActivity().getSupportFragmentManager(), createFragments(mRoomsBeanList.size()), mTitles);
//                mHomeSmartDeviceViewPager.setAdapter(mFragmentPagerAdapter);
//                ((ViewGroup)mHomeSmartRoomTabLayout.getParent()).removeAllViews();
//                mHomeSmartRoomTabLayout.setViewPager(mHomeSmartDeviceViewPager);

//                setViewPagerContent(mHomeSmartDeviceViewPager, mHomeSmartRoomTabLayout, mRoomsBeanList.size(), mTitles, isUpdate);

                mFragmentPagerAdapter.update(createFragments(mRoomsBeanList.size()), mTitles);
                mHomeSmartRoomTabLayout.updateIndicator();
                mHomeSmartDeviceViewPager.setAdapter(mFragmentPagerAdapter);
                mHomeSmartDeviceViewPager.setCurrentItem(0);
               /* Log.e("TAG", "changeFamily: "+mTitles.length );
                for (int i = 0; i < mRoomsBeanList.size(); i++) {
                    Log.e("TAG", "changeFamily: "+mRoomsBeanList.get(i).getRoomName());
                }*/

                mSceneControlAdapter.notifyDataSetChanged();
            }
        }

    }

    /**
     * 场景控制
     *
     * @param adapter
     * @param view
     * @param position
     * @return
     */
    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        if (getActivity() != null) {
            int i = view.getId();
            if (i == R.id.item_iot_scene_control) {
                if (mScenesBeanList.size() - 1 == position) {
                    mIntent.setClass(getActivity(), SceneManageActivity.class);

//                    mIntent.setClass(getActivity(), YsCameraListActivity.class);//萤石测试
//                    mIntent.setClass(getActivity(), YsAddCameraActivity.class);
                    startActivity(mIntent);
                } else {
                    executeScene(mScenesBeanList.get(position),position);
                }
//                    if (mSceneManageItemVos.size() - 1 == position) {
//                        mIntent.setClass(getActivity(), SceneManageActivity.class);
//                        mIntent.putExtra("familyItem", mFamilyListItemVo);
//                        startActivity(mIntent);
//                        break;
//                    }
//                    if (mSceneManageItemVos.get(position).getState() == 1) {
//                        sceneState = 0;
//                        mSceneManageItemVos.get(position).setState(sceneState);
//                    } else {
//                        sceneState = 1;
//                        mSceneManageItemVos.get(position).setState(sceneState);
//                    }
            } else if (i == R.id.item_iot_device_linear) {
                showToast("dianji");
                mDeviceManageAdapter.notifyDataSetChanged();
            } else if (i == R.id.item_iot_device_switch) {
                ToastUtil.showCenter(getActivity(),"点击开关");
//                    mDeviceInfoBeanList.get(position).setDeviceSwitchState(!mDeviceManageItemVos.get(position).isDeviceSwitchState());
                mDeviceManageAdapter.notifyDataSetChanged();
            }
        }
//        return false;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(UpdateFamilyEvent messageEvent) {
        if (messageEvent.isUpdate()) {
            isUpdate = true;
            // 刷新家庭列表
            smartInitFamily(telephone,0);
        }
    }


    //点击房间tab监听
    @Override
    public void onItemTabClick(int position) {
        mHomeSmartRoomTabMyDevice.setTextColor(getResources().getColor(R.color.color_4c333333));
        mHomeSmartRoomTabMyDevice.setTextSize(16);
        mHomeSmartDeviceViewPager.setCurrentItem(position);
        String roonName = mTitles[position];
        LogUtils.e("onItemTabClick", roonName);
//        if(mRoomsBeanList!=null && mRoomsBeanList.get(position).getDeviceInfo().size()>0){
//            home_smart_family_top.setBackground(getActivity().getResources().getDrawable(R.drawable.bg_iot_home_smart_have_device));
//        } else {
//            home_smart_family_top.setBackground(getActivity().getResources().getDrawable(R.drawable.bg_iot_home_smart_no_device));
//        }

//        for (int i = 0; i < mDeviceInfoBeanList.size(); i++) {
//            if (mDeviceInfoBeanList.get(i).getRoomName().equals(mRoomsBeanList.get(position).getRoomName())) {
//                mRoomDeviceInfoBeanList.add(mDeviceInfoBeanList.get(i));
//            }
//        }

//        mHomeSmartRoomTabMyDeviceIcon.setVisibility(View.INVISIBLE);
//        mHomeSmartDeviceRv.setVisibility(View.GONE);
//        mHomeSmartDeviceViewPager.setVisibility(View.VISIBLE);
    }


    private void executeScene(SmartInfoVo.FamilysBean.ScenesBean bean, int position) {
        //执行场景
        Log.e("执行场景参数",bean.getFamilyId()+" "+bean.getSceneId()+" "+bean.getTaskTpye());
        OkGo.<String>get(Constant.HOST + Constant.smartExecuteScene)
                .params("familyId", bean.getFamilyId())
                .params("sceneId", bean.getSceneId())
                .params("taskType", bean.getTaskTpye())
                .converter(new StringConvert())//该方法是网络请求的转换器，功能是将网络请求的结果转换成我们需要的数据类型
                .adapt(new ObservableResponse<String>())//该方法是方法返回值的适配器，功能是将网络请求的Call<T>对象，适配成我们需要的Observable<T>对象
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
//                        JSON
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
                        LogUtils.e("onSubscribe","   =---====");
                    }

                    @Override
                    public void onNext(CommonResp resp) {
                        Log.e("执行场景参数",resp.toString());
                        if (resp.getErrorCode().equalsIgnoreCase("200")) {
                            ToastUtil.show(getActivity(), "执行场景成功");
                            for (int i = 0; i < mScenesBeanList.size(); i++) {
                                if (i!=position){
                                    mScenesBeanList.get(i).setIseffective(false);
                                }
                            }
                            bean.setIseffective(true);
                            mSceneControlAdapter.notifyDataSetChanged();
                            //EventBus.getDefault().post(new UpdateFamilyEvent(true));
                        } else {
                            ToastUtil.show(getActivity(), resp.getMessage());
                        }

                    }


                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "请求失败", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onComplete() {
//                        dismissLoading();
                        LogUtils.e("onComplete","   =====");
                    }
                });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(String initCurrentFamily) {
        if (initCurrentFamily.equalsIgnoreCase("CurrentFamilyPosition")) {
            currentFamilyPosition = -1;
            isUpdate = true;
            // 刷新家庭列表
            smartInitFamily(telephone,0);
        }  else if(initCurrentFamily.equalsIgnoreCase("FamilyRoomActivity")){
            //萤石摄像头刷新
//            currentFamilyPosition = -1;
//            isUpdate = true;
//            getYsCameraList();
            smartInitFamily(telephone,0);
        }else if (initCurrentFamily.equalsIgnoreCase("FamilyMoveActivity")){
            currentFamilyPosition = -1;
            isUpdate = true;
            // 刷新家庭列表
            smartInitFamily(telephone,3);
        }else if (initCurrentFamily.equalsIgnoreCase("GateDeviceActivity")){
            isUpdate = true;
            // 刷新家庭列表
            changedSmart();
        }else if (initCurrentFamily.equalsIgnoreCase("delShareFamily")){
            currentFamilyPosition = -1;
            isUpdate = true;
            // 刷新家庭列表
            smartInitFamily(telephone,3);
        }
    }

    @Override
    public void onDisconnect(int i) {

    }

    @Override
    public void onPingPong(int i) {

    }

    /**
     * InComingCallActicity
     */

    public static final String INCOMING_SID = "inComingSid";
    public static final String INCOMING_FROM = "inComingFrom";
    public static final String INCOMING_UID = "inComingUid";
    public static final String INCOMING_BID = "inComingBid";

    @Override
    public void onMeaasgeResponse(org.json.JSONObject jsonObject) {
//        Message msg=new Message();
//        msg.obj=jsonObject;
//        msg.what=LOGIN;
//        myHandler.sendMessage(msg);

        String method=jsonObject.optString(Method.METHOD);
        int code=jsonObject.optInt(Method.ATTR_ERROR_CODE);
        Log.e("智能家居数据",jsonObject.toString());
        if (method.equals("login")&&code==4000){
            Log.e("智能家居数据","method:"+method+"code:"+code);
            icvss.equesGetDeviceList();
        }else if (method.equals(Method.METHOD_BDYLIST)){
            //获取设备列表
            JSONArray bdys = jsonObject.optJSONArray(Method.METHOD_BDYLIST);
            JSONObject bdyObj = bdys.optJSONObject(0);
            Log.e("智能家居数据设备",jsonObject.toString());
            if (bdyObj != null) {
                String bid = bdyObj.optString(Method.ATTR_BUDDY_BID, null);
                if (bid != null) {

                }
                state = 1;
            } else {
                state=0;
            }
            JSONArray onlines = jsonObject.optJSONArray(Method.ATTR_ONLINES);
            JSONObject onlinesObj = onlines.optJSONObject(0);
            if (onlinesObj != null) {
                String uid = onlinesObj.optString(Method.ATTR_BUDDY_UID, null);
                String bid = bdyObj.optString(Method.ATTR_BUDDY_BID, null);
                if (uid != null) {
                    SPUtils.put(getActivity(),"uid",uid);
                }
                if (bid!=null){
                    SPUtils.put(getActivity(),"bid",bid);
                }
            } else {
                // 一般不在线状态下没有此数据
            }
        }else if (method.equals(Method.METHOD_ONADDBDY_REQ)){
            //猫眼扫描二维码返回的数据
            String reqid=jsonObject.optString("reqid");
            SPUtils.put(getActivity(),"reqid",reqid);
        }else if (method.equals(Method.METHOD_ONADDBDY_RESULT)){
            //添加猫眼成功返回的数据
            if (code==4000){
                icvss.equesGetDeviceList();
            }
        }else if (method.equals(Method.METHOD_CALL)){
            String uidStr = jsonObject.optString(Method.ATTR_FROM);
            String sid = jsonObject.optString(Method.ATTR_CALL_SID);
            String state = jsonObject.optString(Method.ATTR_CALL_STATE);
            int callType = jsonObject.optInt(Method.ATTR_CALL_TYPE, 0);
            if (state.equals(Constants.CALL_OPEN)){
                Intent intent = new Intent(getActivity(), InComingCallsActivity.class);
                intent.putExtra(INCOMING_SID, sid);
                intent.putExtra(INCOMING_UID, uidStr);
                intent.putExtra(INCOMING_BID, uidStr);
                intent.putExtra(Method.METHOD_ATTR_CAMERATYPE, callType);
                intent.putExtra(Method.ATTR_ROLE, BuddyType.TYPE_WIFI_DOOR_T21);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getActivity().startActivity(intent);
            }
        }else if (method.equals(Method.METHOD_RMBDY_RESULT)){
            //删除设备返回的数据
            icvss.equesGetDeviceList();
        }else if (method.equals(Method.METHOD_ONBDY_REMOVED)){
            //设备在别处登录
            icvss.equesGetDeviceList();
        }

    }

    private void changedSmart() {
        loadingDialog.show();
        OkGo.<String>get(Constant.HOST + Constant.INIT_HOME_INIT+ familyId)
//                .headers("aaa", "111")//
                //.params("familyId", mfamilyId)
                .params("userId", telephone)//
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
                .map(new Function<Response<String>, SmartInfoVo>() {
                    @Override
                    public SmartInfoVo apply(Response<String> stringResponse) throws Exception {
                        LogUtils.e("apply", "====" + stringResponse.body());
//                        JSON
                        CommonResp resp = JSON.parseObject(stringResponse.body(), CommonResp.class);
                        SmartInfoVo obj = JSON.parseObject(resp.getResult(), SmartInfoVo.class);
                        if (obj != null) {
                        }
                        return obj;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())//
                .subscribe(new Observer<SmartInfoVo>() {



                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
//                        addDisposable(d);
                        LogUtils.e("onSubscribe","   =---====");
                    }

                    @Override
                    public void onNext(SmartInfoVo smartInfoVo) {
                        loadingDialog.dismiss();
                        SmartInfoVo.FamilysBean familys = smartInfoVo.getFamily();
                        mHomeSmartFamilyName.setText(familys.getFamilyName()); // 更新家庭名称显示
                        LogUtils.e("currentFamilyPosition-----",currentFamilyPosition+"============");

                        defense = familys.isDefense();
                        familyId = familys.getFamilyId();
                        LogUtils.e("currentFamilyPosition-----",defense+"============");
                        if (familys.getProject()!=null&&familys.getProject().getProjectCode()!=null){
                            tv_defense.setVisibility(View.VISIBLE);
                            if (defense){
                                tv_defense.setText("一键撤防");
                                tv_defense.setBackgroundResource(R.drawable.ff7363_4_corner);
                            }else {
                                tv_defense.setText("一键布防");
                                tv_defense.setBackgroundResource(R.drawable.corner_4_42d084);
                            }
                        }else {
                            tv_defense.setVisibility(View.GONE);
                        }

                        //切换家庭
                        mFamilysBeanList = familys;
                        changeFamily(mFamilysBeanList);

                        LogUtils.e("onNext:", "------------------");//smartInfoVo.getUserid()+"---"+smartInfoVo.getFamilys().size()
                    }


                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        e.printStackTrace();
                        loadingDialog.dismiss();
                        Log.e("HomeSmartFragment",e.getMessage());
                        Toast.makeText(getActivity(),"请求失败",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onComplete() {
                        LogUtils.e("onComplete","   =====");
                    }
                });

    }
    //    private void ezvizGetToken(String userId) {
//        OkGo.<String>get(Constant.HOST + Constant.Ezviz_GetToken)
//                .params("userId", userId)//
//                .converter(new StringConvert())//
//                .adapt(new ObservableResponse<String>())//
//                .subscribeOn(Schedulers.io())//
//                .doOnSubscribe(new Consumer<Disposable>() {
//                    @Override
//                    public void accept(@io.reactivex.annotations.NonNull Disposable disposable) throws Exception {
////                        showLoading();
//                        LogUtils.e("accept", disposable.isDisposed() + "===");
//                    }
//                })//
//                .map(new Function<Response<String>, CommonResp>() {
//                    @Override
//                    public CommonResp apply(Response<String> stringResponse) throws Exception {
//                        LogUtils.e("apply", "====" + stringResponse.body());
////                        JSON
//                        CommonResp resp = JSON.parseObject(stringResponse.body(), CommonResp.class);
//                        return resp;
//                    }
//                })
//                .observeOn(AndroidSchedulers.mainThread())//
//                .subscribe(new Observer<CommonResp>() {
//                    @Override
//                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
////                        addDisposable(d);
//                        LogUtils.e("onSubscribe", "========---====");
//                    }
//
//                    @Override
//                    public void onNext(CommonResp commonResp) {
//                        if(commonResp.getErrorCode().equalsIgnoreCase("200")){
//                            JSONObject object = JSON.parseObject(commonResp.getResult());
//                            String accessToken = object.getString("accessToken");
//                            LogUtils.e("ezvizGetToken",accessToken+"--------------");
//                            if(!StringUtils.isEmpty(accessToken)){
//                                EZOpenSDK.getInstance().setAccessToken(accessToken);
//                                getYsCameraList();
//
//                            } else {
//                                //未注册萤石
//                                smartInit(telephone);
//                            }
//                        } else {
//                            smartInit(telephone);
//                        }
//                        LogUtils.e("onNext:", "------------------");//smartInfoVo.getUserid()+"---"+smartInfoVo.getFamilys().size()
//                    }
//
//
//                    @Override
//                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
//                        e.printStackTrace();
//                        smartInit(telephone);
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        LogUtils.e("onComplete", "============");
//                    }
//                });
//    }


//    private List<EZDeviceInfo> cameraList = new ArrayList<>();
//    SmartInfoVo.FamilysBean.DeviceInfoBean cameraDevice;
//    private void getYsCameraList() {
//        new GetYsCameraListTask().execute();
//    }
//
//    private class GetYsCameraListTask extends AsyncTask<Void, Void, List<EZDeviceInfo>> {
//        public GetYsCameraListTask(){
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            LogUtils.e("onPreExecute","-----------------------");
//        }
//
//        @Override
//        protected List<EZDeviceInfo> doInBackground(Void... voids) {
//            try {
//                cameraList = EZOpenSDK.getInstance().getDeviceList(0, 20);
//
//                LogUtils.e("onLoadMoreRequested",cameraList.size()+"================");
//
//            } catch (BaseException e) {
//                e.printStackTrace();
//            }
//            return cameraList;
//        }
//
//        @Override
//        protected void onPostExecute(List<EZDeviceInfo> result) {
//            super.onPostExecute(result);
//            LogUtils.e("onPostExecute",result.size()+"===============");
//            if(cameraList.size()>0){
//                cameraDevice = new SmartInfoVo.FamilysBean.DeviceInfoBean();
//                cameraDevice.setId("");
//                cameraDevice.setDeviceName("萤石摄像头");
//                cameraDevice.setDeviceType(DeviceType.YS_CAMERA);
//                cameraDevice.setDeviceIcon("https://statics.ys7.com/device/image/CS-C2C-1A1WFR/101.jpeg");
//                cameraDevice.setOnline(true);
//                cameraDevice.setLocation("设备数量"+result.size());
//            } else {
//                cameraDevice = null;
//            }
//            smartInit(telephone);
//        }
//
//        protected void onError(){
//            LogUtils.e("GetYsCameraListTask","onError------");
//            smartInit(telephone);
//        }
//    }
//
//
//    private void getYsCameraListFromInterface(String token) {//貌似主账号token才能用
//        JSONObject obj = new JSONObject();
//        obj.put("accessToken", token);
//        obj.put("pageStart", 0);
//        obj.put("pageSize",20);
//        OkGo.<String>post(Constant.YS_GetToken_Host+"api/lapp/device/list")
//                .upJson(JSON.toJSONString(obj))
//                .converter(new StringConvert())//该方法是网络请求的转换器，功能是将网络请求的结果转换成我们需要的数据类型
//                .adapt(new ObservableResponse<String>())//该方法是方法返回值的适配器，功能是将网络请求的Call<T>对象，适配成我们需要的Observable<T>对象
//                .subscribeOn(Schedulers.io())
//                .doOnSubscribe(new Consumer<Disposable>() {
//                    @Override
//                    public void accept(@io.reactivex.annotations.NonNull Disposable disposable) throws Exception {
////                        showLoading();
//                        LogUtils.e("accept",disposable.isDisposed()+"===");
//                    }
//                })
//                .map(new Function<Response<String>, String>() {
//                    @Override
//                    public String apply(Response<String> stringResponse) throws Exception {
//                        LogUtils.e("apply","===="+stringResponse.body());
////                        CommonResp resp = JSON.parseObject(stringResponse.body(), CommonResp.class);
////                        LogUtils.e("resp",resp.getState()+"=====");
//                        return stringResponse.body();
//                    }
//                })
//                .observeOn(AndroidSchedulers.mainThread())//
//                .subscribe(new Observer<String>() {
//                    @Override
//                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
////                        addDisposable(d);
//                        LogUtils.e("onSubscribe","========---====");
//                    }
//
//                    @Override
//                    public void onNext(String resp) {
//                        if(resp !=null){
////                            if(resp.getErrorCode().equalsIgnoreCase("200")){
////
////                            } else {
////
////                            }
//                        } else {
//
//                        }
//
//                    }
//
//
//                    @Override
//                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
//                        e.printStackTrace();
//
//                    }
//
//                    @Override
//                    public void onComplete() {
////                        dismissLoading();
//                        LogUtils.e("onComplete","============");
//                    }
//                });
//    }
}
