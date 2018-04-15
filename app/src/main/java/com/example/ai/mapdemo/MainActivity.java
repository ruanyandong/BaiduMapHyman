package com.example.ai.mapdemo;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private LocationClient mLocationClient;

    private MapView mMapView = null;

    private BaiduMap mBaiduMap=null;

    private boolean isFirstLocation=true;

    private MyListener listener;

    /**
     * 地图移位后回到原位，记录新的经纬度
     */
    private double mLatitude;
    private double mLongitude;
    //自定义定位图标
    private BitmapDescriptor mIconLocation;

    /**
     * 方向传感器监听器
     */
    private MyOrientationListener myOrientationListener;


    /**
     *记录当前的X方向值
     */
    private float mCurrentX;

    /**
     *模式变量
     */
    private MyLocationConfiguration.LocationMode mLocationMode;

    /**
     *覆盖物图标
     */
    private BitmapDescriptor mMarker;

    /**
     *覆盖物布局
     */
    private RelativeLayout mMarkerLayout;

    /**
     * infoWindow
     */
    private InfoWindow infoWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         * 初始化定位客户端
         * 注册监听器
         * 在使用SDK各组件之前初始化context信息，传入ApplicationContext
         * 注意该方法要再setContentView方法之前实现
         */
        initSDKLocationClientRegisterLocationListener();
        /**
         * 加载布局
         */
        setContentView(R.layout.activity_main);

        /**
         * 初始化地图、设置能够获取自己的位置
         */
        initMapView();


        /**
         * 检查权限和申请权限
         */
        checkPermission();

        /**
         * 检查GPS是否打开
         */
        checkGPS();

        /**
         * 初始化覆盖物图标
         */
        initMarker();

        /**
         * 点击Marker显示marker具体信息和infoWindow
         */
        setOnMarkerListener();

        /**
         * 点击地图的时候，隐藏具体信息、隐藏infoWindows
         */
        setOnMapClickListener();


    }

    /**
     * 点击地图的时候，隐藏具体信息、隐藏infoWindows
     */
    private void setOnMapClickListener() {
        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                /**
                 * 点击地图，隐藏具体信息
                 */
                mMarkerLayout.setVisibility(View.GONE);
                /**
                 * 点击地图，隐藏infoWindows
                 */
                mBaiduMap.hideInfoWindow();

            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });
    }

    /**
     * 点击Marker显示marker具体信息和infoWindow
     */
    private void setOnMarkerListener() {
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                Bundle bundle=marker.getExtraInfo();
                InfoBean infoBean=(InfoBean)bundle.getSerializable("info");

                ImageView imageView=(ImageView)mMarkerLayout.findViewById(R.id.id_info_img);
                TextView distance=(TextView)mMarkerLayout.findViewById(R.id.id_info_distance);
                TextView name=(TextView)mMarkerLayout.findViewById(R.id.id_info_name);

                TextView zan=(TextView)mMarkerLayout.findViewById(R.id.id_info_zan);

                imageView.setImageResource(infoBean.getImgId());
                distance.setText(infoBean.getDistance());
                name.setText(infoBean.getName());
                zan.setText(infoBean.getZan()+"");

                /**
                 * 设置infoWindows
                 */
                //InfoWindow infoWindow;

                TextView tv=new TextView(MainActivity.this);
                //tv.setBackgroundResource(R.drawable.location_tips);
                tv.setBackground(getResources().getDrawable(R.drawable.location_tips));

                tv.setPadding(30,20,30,50);
                tv.setText(infoBean.getName());

                LatLng latLng=marker.getPosition();

                //Point point=mBaiduMap.getProjection().toScreenLocation(latLng);
                //point.y-=47;

                //LatLng ll=mBaiduMap.getProjection().fromScreenLocation(point);

                infoWindow=new InfoWindow(tv,latLng,-47);


                mBaiduMap.showInfoWindow(infoWindow);

                mMarkerLayout.setVisibility(View.VISIBLE);
                return true;
            }
        });
    }

    /**
     * 初始化覆盖物图标
     */
    private void initMarker() {
       mMarker=BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding);
       mMarkerLayout=findViewById(R.id.id_marker_layout);

    }

    /**
     * 初始化地图、设置能够获取自己的位置
     */
    private void initMapView() {
        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap=mMapView.getMap();
        //设置能够获取自己的位置
        mBaiduMap.setMyLocationEnabled(true);
    }


    /**
     * 初始化定位客户端
     * 注册监听器
     * 在使用SDK各组件之前初始化context信息，传入ApplicationContext
     * 注意该方法要再setContentView方法之前实现
     */
    private void initSDKLocationClientRegisterLocationListener() {
        //初始化定位客户端
        mLocationClient=new LocationClient(getApplicationContext());
        listener=new MyListener();
        //注册监听器
        mLocationClient.registerLocationListener(listener);
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());
    }

    /**
     * 检查GPS是否打开
     */
    private void checkGPS() {
        LocationManager locManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        if(!locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            // 未打开位置开关，可能导致定位失败或定位不准，提示用户或做相应处理
            Toast.makeText(MainActivity.this,
                    "未打开GPS开关，可能导致定位失败或定位不准",
                    Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 检查权限和申请权限
     */
    private void checkPermission() {
        List<String> permissionList=new ArrayList<String>();
        if (ContextCompat.checkSelfPermission(
                MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)!=
                PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (ContextCompat.checkSelfPermission(
                MainActivity.this,
                Manifest.permission.READ_PHONE_STATE)!=
                PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(
                MainActivity.this,
                Manifest.permission.CAMERA)!=
                PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.CAMERA);

        }
        if (ContextCompat.checkSelfPermission(
                MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)!=
                PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (!permissionList.isEmpty()){
            String[] permissions=permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(MainActivity.this,permissions,1);
        }else{
            acquireLocation();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        /**
         * 开启定位
         */
        //mBaiduMap.setMyLocationEnabled(true);
        //if (mLocationClient.isStarted()){
            //没启动再启动
            //mLocationClient.start();
        //}

    }

    /**
     * 开始获取当前的位置
     */
    private void acquireLocation(){
        initLocation();
        /**
         * 开始定位
         */
        mLocationClient.start();

        /**
         * 开启方向传感器
         */
        myOrientationListener.start();
    }

    /**
     * 初始化一些定位的参数
     */
    private void initLocation(){
        //mLocationClient=new LocationClient(this);
        //listener=new MyListener();
        //mLocationClient.registerLocationListener(listener);

        mLocationMode= MyLocationConfiguration.LocationMode.NORMAL;

        LocationClientOption locationClientOption=new LocationClientOption();
        locationClientOption.setCoorType("bd09ll");
        locationClientOption.setIsNeedAddress(true);
        locationClientOption.setOpenGps(true);
        locationClientOption.setScanSpan(1000);

        mLocationClient.setLocOption(locationClientOption);
        //初始化定位图标
        mIconLocation= BitmapDescriptorFactory.fromResource(R.drawable.arrow);

        myOrientationListener=new MyOrientationListener(MainActivity.this);

        /**
         * 当方向改变时，转动定位图标
         */
        myOrientationListener.setOrientationListener(
                new MyOrientationListener.onOrientationListener() {
            @Override
            public void onOrientationChanged(float x) {
                /**
                 * 更新方向值
                 */
                mCurrentX=x;
            }
        });

    }


    private void navigationTo(BDLocation bdLocation){

        MyLocationData.Builder builder=new MyLocationData.Builder();
        builder.latitude(bdLocation.getLatitude());
        builder.longitude(bdLocation.getLongitude());
        builder.direction(mCurrentX);//定位方向
        builder.accuracy(bdLocation.getRadius());
        MyLocationData data=builder.build();

        mBaiduMap.setMyLocationData(data);

        //设置自定义图标
        MyLocationConfiguration configuration=new
                MyLocationConfiguration(mLocationMode,true,mIconLocation);

        mBaiduMap.setMyLocationConfiguration(configuration);



        /**
         * 记录最新的位置，更新经纬度
         */
        mLatitude=bdLocation.getLatitude();
        mLongitude=bdLocation.getLongitude();


        if (isFirstLocation){
            LatLng latLng=new LatLng(bdLocation.getLatitude(),bdLocation.getLongitude());

            MapStatusUpdate mapStatusUpdate=MapStatusUpdateFactory.newLatLngZoom(latLng,20f) ;
            mBaiduMap.animateMapStatus(mapStatusUpdate);

            //mapStatusUpdate=MapStatusUpdateFactory.zoomTo(20f);
            //mBaiduMap.animateMapStatus(mapStatusUpdate);

            /*判断baiduMap是已经移动到指定位置*/
            if (mBaiduMap.getLocationData()!=null)
                if (mBaiduMap.getLocationData().latitude==bdLocation.getLatitude()
                        &&mBaiduMap.getLocationData().longitude==bdLocation.getLongitude()){
                    isFirstLocation = false;
                }

        }




    }


    private class MyListener extends BDAbstractLocationListener{

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {

            if (bdLocation.getLocType()==BDLocation.TypeGpsLocation ||
                    bdLocation.getLocType()==BDLocation.TypeNetWorkLocation){
                navigationTo(bdLocation);



            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 1:
                if (grantResults.length>0){
                    for (int result:grantResults) {
                        if (result !=PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(MainActivity.this,
                                    "必须同意所有权限才能使用此程序",
                                    Toast.LENGTH_LONG).show();
                            finish();
                            return;
                        }
                    }
                    acquireLocation();
                }else {
                    Toast.makeText(MainActivity.this,
                            "发生未知错误",
                            Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        /**
         * 停止定位
         */
        mBaiduMap.setMyLocationEnabled(false);
        /**
         * 关闭定位
         */
        mLocationClient.stop();
        /**
         * 停止方向传感器
         */
        myOrientationListener.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
        //mBaiduMap.setMyLocationEnabled(false);
        //mLocationClient.stop();

    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.id_map_common:
                mBaiduMap.setMapType(mBaiduMap.MAP_TYPE_NORMAL);
                break;
            case R.id.id_map_sate:
                mBaiduMap.setMapType(mBaiduMap.MAP_TYPE_SATELLITE);
                break;
            case R.id.id_map_traffic:
                if (mBaiduMap.isTrafficEnabled()){
                    mBaiduMap.setTrafficEnabled(false);
                    item.setTitle("实时交通(off)");
                }else{
                    mBaiduMap.setTrafficEnabled(true);
                    item.setTitle("实时交通(on)");
                }
                break;
            /**
             * 定位到我的位置
             */
            case R.id.id_map_location:
                LatLng latLng=new LatLng(mLatitude,mLongitude);

                MapStatusUpdate mapStatusUpdate=MapStatusUpdateFactory.newLatLngZoom(latLng,20f) ;
                mBaiduMap.animateMapStatus(mapStatusUpdate);
                break;
            /**
             *修改定位模式
             */
            case R.id.id_map_mode_common:
                mLocationMode= MyLocationConfiguration.LocationMode.NORMAL;

                break;
            case R.id.id_map_mode_following:
                mLocationMode= MyLocationConfiguration.LocationMode.FOLLOWING;
                break;
            case R.id.id_map_mode_compass:
                mLocationMode= MyLocationConfiguration.LocationMode.COMPASS;
                break;
            //添加到我的位置
            case R.id.id_map_overlay:
                addOverlays(InfoBean.infoBeans);
            default:
                break;

        }

        return true;
    }

    /**
     * 添加覆盖物
     * @param infoBeans
     */
    private void addOverlays(List<InfoBean> infoBeans) {
        /**
         * 请出图层
         */
        mBaiduMap.clear();

        LatLng latLng=null;
        Marker marker=null;
        OverlayOptions options;

        for (InfoBean info:infoBeans) {

            //经纬度
            latLng=new LatLng(info.getLatitude(),info.getLongitude());
            //图标
            options=new MarkerOptions().position(latLng).icon(mMarker).zIndex(5);
            marker=(Marker)mBaiduMap.addOverlay(options);
            Bundle bundle=new Bundle();
            bundle.putSerializable("info",info);

            marker.setExtraInfo(bundle);
        }
        //把地图移动到到最后一个覆盖物的位置
        MapStatusUpdate update=MapStatusUpdateFactory.newLatLng(latLng);
        mBaiduMap.setMapStatus(update);
    }

}
